/**
 * @fileOverview  * {@link Form}クラスを記述したファイルです。
 */

'use strict';

/**
 * @class Form
 *
 * フォームクラス。
 * <pre>
 * HTMLのformタグに対応するクラスです。
 * </pre>
 * @extends WebComponent
 * @prop {Object} formData setFormDataメソッドで設定されたフォームデータ。
 * @prop {Boolian} clientValidation クライアントでバリデーションを行うかどうかを示すフラグ。
 * @prop {Array} fields サーバから送信された情報を元に作成されたフィールドjsクラスのインスタンスを記録した配列。
 * @prop {Array} htmlTables サーバから送信された情報を元に作成されたHTMLテーブルjsクラスのインスタンスを記録した配列。
 */
class Form extends WebComponent {
	/**
	 * コンストラクタ。
	 */
	constructor() {
		super();
		this.clientValidation = true;
		this.fields = [];
		this.htmlTables = []
	}

	/**
	 * 各フィールドの初期化を行います。
	 * @param {Array} fieldList フィールドリスト。
	 */
	initField(fieldList) {
		for (let i = 0; i < fieldList.length; i++) {
			let f = fieldList[i];
			let field = this.newInstance(f);
			field.init();
			this.fields[i] = field;
		}
	}

	/**
	 * HTMLテーブルの初期化を行います。
	 * @param {Array} htmlTableList HTMLテーブルリスト.
	 */
	initHtmlTable(htmlTableList) {
		for (let i = 0; i < htmlTableList.length; i++) {
			let t = htmlTableList[i];
			let tbl = this.newInstance(t);
			tbl.init(this.formData);
			this.htmlTables[i] = tbl;
		}
	}


	/**
	 * 必須マークを設定します。
	 * <pre>
	 * 入力フィールドにRequiredValidatorが設定されていた場合、対応するラベルに対して、必須マークを付加します。
	 * </pre>
	 */
	setRequiredMark() {
		for (let i = 0; i <this.fields.length; i++) {
			let f = this.fields[i];
//			let o = this.find('#' + this.selectorEscape(f.id));
			let o = f.get();
			if (o.length > 0) {
				if (f.isRequired()) {
					let e = f.getLabelElement();
					e.addClass("requiredFieldLabel");
				}
			}
		}
		for (let i = 0; i < this.htmlTables.length; i++) {
			this.htmlTables[i].setRequiredMark();
		}
	}

	/**
	 * フォームデータを設定します。
	 * @param {Object} formData フォームデータ。
	 */
	setFormData(formData) {
		this.formData = formData;
		for (let i = 0; i < this.htmlTables.length; i++) {
			let tbl = this.htmlTables[i];
			tbl.clear();
			tbl.setFormData(formData);
		}
		for (let i = 0; i <this.fields.length; i++) {
			let field = this.fields[i];
			field.setValue(formData[field.id]);
		}
		this.onCalc(null);
	}

	/**
	 * フォームのフィールドに対して、値を設定します。
	 * @param {String} fid フィールドID。
	 * @param {String} value 値。
	 */
	setFieldValue(fid, value) {
		if (this.isHtmlTableElementId(fid)) {
			let tblid = this.getHtmlTableId(fid);
			let colid = this.getHtmlTableColumnId(fid);
			let table = this.getComponent(tblid);
			let field = table.getComponent(colid);
			if (field != null) {
				let f = new field.constructor();
				Object.assign(f, field);
				f.id = fid;
				f.setValue(value);
			}
		} else {
			let field = this.getComponent(fid);
			if (field != null) {
				field.setValue(value);
			}
		}
	}

	/**
	 * フィールドの値を取得します。
	 * @param {String} fid フィールドID。
	 */
	getFieldValue(fid) {
		let ret = null;
		if (this.isHtmlTableElementId(fid)) {
			let tblid = this.getHtmlTableId(fid);
			let colid = this.getHtmlTableColumnId(fid);
			let table = this.getComponent(tblid);
			let field = table.getComponent(colid);
			if (field != null) {
				let f = new field.constructor();
				Object.assign(f, field);
				f.id = fid;
				ret = f.getValue();
			}
		} else {
			let field = this.getComponent(fid);
			if (field != null) {
				ret = field.getValue();
			}
		}
		return ret;
	}

	/**
	 * フォームを初期化します。
	 */
	init() {
		super.init();
		this.initField(this.fieldList);
		this.initHtmlTable(this.htmlTableList);
	}

	/**
	 * フォーム中のHTMLを動的に編集します。
	 * <pre>
	 * このメソットはsuper.attach()の直前に呼ばれるため、
	 * このメソッドをオーバーライドして、HTMLを動的に変更する
	 * 処理を記述することができます。
	 * Formクラスのこのメソッドは何も行いません。
	 * </pre>
	 */
	remodelHtml() {

	}

	/**
	 * HTMLエレメントへの対応付けを行います。
	 * <pre>
	 * 以下のボタンが存在した場合、イベント処理を登録します。
	 * #newButton ... 「新規登録」ボタンの処理.
	 * </pre>
	 */
	attach() {
		if (this.htmlPath != null) {
			let fhtml = $("<div>" + this.additionalHtmlText + "</div>").find("form").html();
			let obj = this.get();
			if (obj.length != 0) {
				obj.html(fhtml);
			} else {
				obj = $(this.convertSelector("#" + this.selectorEscape(this.id)));
				obj.html(fhtml);
				this.parentDivId = obj.parents("div[" + this.getIdAttribute() +"]:first").attr(this.getIdAttribute());
			}
		} else {
			let obj = $(this.convertSelector("#" + this.selectorEscape(this.id)));
			this.parentDivId = obj.parents("div[" + this.getIdAttribute() +"]:first").attr(this.getIdAttribute());
		}
		this.remodelHtml();
		super.attach();
		this.get().addClass(this.id);
		this.get("newButton").prop("disabled" , false);
		this.get("newButton").click(() => {
			this.newData();
			return false;
		});
		this.setRequiredMark();
		this.setFormData(this.formData);
		this.onCalc(null);
	}


	/**
	 * フォームのリセットを行います。
	 */
	reset() {
		this.setFormData(this.formData);
		this.parent.resetErrorStatus();
	}


	/**
	 * フォームのサブミットを行います。
	 *
	 * @param {String} method 送信先のメソッド.
	 * @param {Function} func 応答処理 function(data)。
	 * @return {Promise} funcが指定されなかった場合、Promiseを返す。
	 */
	async submit(method, func) {
		let form = this;
		let f = this.get();
		if (func != null) {
			let m = new ServerMethod(this.getUniqId() + "." + method);
			m.submit(f, function(data) {
				if (data instanceof Blob) {
					// blobが来た場合。
					form.downloadBlob(m, data);
				} else {
					func(data);
				}
			});
			return null;
		} else {
			let m = this.getWebMethod(method);
			let data = await m.submit(f);
			if (data instanceof Blob) {
				// blobが来た場合。
				form.downloadBlob(m, data);
				return null;
			} else {
				return data;
			}
		}
	}

	/**
	 * ファイルフィールド以外をsubmitします。
	 * <pre>
	 * 時間のかかるファイルPOSTを回避する目的で使用します。
	 * サーバメソッドではファイルの処理はできません。
	 * フォームのserialize()で得られたパラメータをfetchに渡して、
	 * 指定されたサーバメソッドを呼び出しその結果を取得します。
	 * </pre>
	 * @param {String} method メソッド名。
	 * @param {Function} func 応答処理 function(data)。
	 * @return {Promise} funcが指定されなかった場合、Promiseを返す。
	 */
	async submitWithoutFile(method, func) {
		let form = this;
		if (func != null) {
			let m = new ServerMethod(this.getUniqId() + "." + method);
			m.submitWithoutFile(form.get(), function(data) {
				if (data instanceof Blob) {
					// blobが来た場合。
					form.downloadBlob(m, data);
				} else {
					func(data);
				}
			});
			return null;
		} else {
			let m = this.getWebMethod(method);
			let data = await m.submitWithoutFile(form.get());
			if (data instanceof Blob) {
				// blobが来た場合。
				form.downloadBlob(m, data);
				return null;
			} else {
				return data;
			}
		}
	}

	/**
	 * ファイルフィールドも含めてsubmitします。
	 *
	 * @param {String} method メソッド。
	 * @param {Function} func 応答処理 function(data)。
	 * @return {Promise} funcが指定されなかった場合、Promiseを返す。
	 */
	async submitWithFile(method, func) {
		let form = this;
		if (func != null) {
			let m = new ServerMethod(this.getUniqId() + "." + method);
			m.submitWithFile(form.get(), function(data) {
				if (data instanceof Blob) {
					// blobが来た場合。
					form.downloadBlob(m, data);
				} else {
					func(data);
				}
			});
		} else {
			let m = this.getWebMethod(method);
			let data = await m.submitWithFile(form.get());
			if (data instanceof Blob) {
				// blobが来た場合。
				form.downloadBlob(m, data);
				return null;
			} else {
				return data;
			}
		}
	}

	/**
	 * blobをダウンロードします。
	 * @param {Object} m WebMethodまたはServerMethodのオブジェクト。
	 * @param {Object} data ダウンロードデータ。
	 */
	downloadBlob(m, data) {
		// blobが来た場合。
		let contentDisposition = m.headers.get("Content-Disposition");
		let filename = "download";
		if (contentDisposition != null && contentDisposition.length > 0) {
			filename = decodeURIComponent(contentDisposition.replace("attachment; filename=", ""));
		}
		logger.log("download blob=" + contentDisposition);
		if (window.navigator.msSaveBlob) {
			// IE,EDGE対応
			window.navigator.msSaveBlob(data, filename);
		} else {
			const url = (window.URL || window.webkitURL).createObjectURL(data);
			// ダウンロード.
			const a = document.createElement('a');
			a.href = url;
			a.download = filename;
			document.body.appendChild(a);
			a.click();
			document.body.removeChild(a);
		}

	}


	/**
	 * ダウンロード用のsubmitを行います。
	 * <pre>
	 * BinaryResponseを返すサーバメソッドに対して、submitを行い、結果をダウンロードします。
	 * サーバメソッドがエラーの発生などの要因でJSONを返した場合、funcにその内容を渡します。
	 * funcを省略した場合、エラーメッセージが返されたという前提でalertを表示する応答処理を実行します。
	 * </pre>
	 * @param {String} method メソッド名。
	 * @param {Function} func 応答処理 function(data)。
	 * @deprecated submitのみでバイナリデータのダウンロードも可能になりました。(submitForDownloadは互換性維持のためにしばらく残します)
	 */
	submitForDownload(method, func) {
		let form = this;
		let m = new ServerMethod(this.getUniqId() + "." + method);
		let rfunc = function(data) {
			if (data instanceof Blob) {
				// blobが来た場合。
				form.downloadBlob(m, data);
			} else {
				// ダウンロードを期待したがJSONが来た場合。
				if (func == null) {
					func = function(ret) {
						let systemName = MessagesUtil.getMessage("message.systemname");
						currentPage.alert(systemName, ret.result);
					}
				}
				func.call(form, data);
			}
		}
		m.submitWithFile(form.get(), rfunc);
	}

	/**
	 * 各フィールドの検証を行います。
	 * @returns {Array} 検証結果リスト。
	 */
	validateFields() {
		let result = [];
		for (let i = 0; i < this.fields.length; i++) {
			let field = this.fields[i];
			let e = field.validate();
			if (e != null) {
				result.push(e);
			}
		}
		for (let i = 0; i < this.htmlTables.length; i++) {
			let r = this.htmlTables[i].validate();
			for (let n = 0; n < r.length; n++) {
				result.push(r[n]);
			}
		}
		return result;
	}

	/**
	 * フォーム単位のバリデーションを行います。
	 * <pre>
	 * フォーム単位のバリデーションが必要な場合、実装してください。
	 * </pre>
	 * @returns {Array} バリデーションの結果。
	 */
	validateForm() {
		let ret = [];
		return ret;
	}

	/**
	 * フォームの検証を行います。
	 * <pre>
	 * クライアントバリデーションが有効に設定されている場合、このメソッドで各フィールドのバリデーションを行います。
	 * 項目関連チェックなどの独自のバリデーションを実装する場合、このメソッドをオーバーライドします。
	 * </pre>
	 */
	validate() {
		if (!this.clientValidation) {
			return true;
		}
		let result = this.validateFields();
		if (result.length == 0) {
			result = this.validateForm();
		}
		if (result.length) {
			this.parent.setErrorInfo(result, this);
			return false;
		} else {
			return true;
		}
	}

	/**
	 * サーバのバリデーション情報を取得します。
	 * <pre>
	 * サーバから受信したバリデーション結果をメッセージの配列に変換します。
	 * サーバから返されるバリデーション情報はフィールドIDとフィールド名が{0}となったメッセージです。
	 * フィールドIDから対応するフィールドのラベルを検索し、{0}に置き換えたメッセージを作成します。
	 *
	 * </pre>
	 * @param {Array} result サーバのバリデーション結果。
	 * @returns {Array} バリデーション結果。
	 */
	getValidationResult(result) {
		let errors = [];
		for (let i = 0; i < result.result.length; i++) {
			let comp = this.getComponent(result.result[i].fieldId);
			let msg = result.result[i].message.replace("{0}", comp.label);
			errors.push(new ValidationError(result.result[i].fieldId, msg));
		}
		return errors;
	}

	/**
	 * 各フィールドのロック/ロック解除を行います。
	 * @param {Boolean} lk ロックする場合true.
	 */
	lockFields(lk) {
		for (let i = 0; i < this.fields.length; i++) {
			let field = this.fields[i];
			field.lock(lk);
		}
		for (let i = 0; i < this.htmlTables.length; i++) {
			this.htmlTables[i].lockFields(lk);
		}
	}

	/**
	 * 「新規登録」ボタンの応答処理です。
	 */
	newData() {
		this.parent.toEditMode();
		let editForm = this.parent.getComponent("editForm");
		editForm.newData();
	}

	/**
	 * 計算イベント処理を行います。
	 * <pre>
	 * 計算イベントフィールドが更新された場合、このメソッドが呼び出されます。
	 * データ入力時の自動計算が必要な場合このメソッドをオーバーライドしてください。
	 * </pre>
	 * @param {jQuery} element イベントが発生した要素。初期表示の時等特定フィールドが要因でない場合はnullが設定されます。
	 *
	 */
	onCalc(element) {
	}

	/**
	 * フォーム中のデータをクリアします。
	 */
	clearData() {
		let data = {};
		for (let i = 0; i < this.htmlTables.length; i++) {
			let tbl = this.htmlTables[i];
			data[tbl.id] = [];
		}
		this.setFormData(data);
	}

	/**
	 * 指定したフォームにHIDDEN項目を追加します。
	 * @param {String} field フィールドID。
	 * @param {String} val 値。
	 */
	setHiddenField(field, val) {
		let hid = this.find('#' + field);
		if (hid.length == 0) {
			this.get().append("<input type='hidden' " + this.getIdAttribute() + "='" + field + "' name='" + field + "' value='" + val + "'>");
			hid = this.find('#' + field);
		}
		hid.val(val);
	}
}

