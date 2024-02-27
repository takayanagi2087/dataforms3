/**
 * @fileOverview {@link Field}クラスを記述したファイルです。
 */

'use strict';

/**
 * @class Field
 * フィールドクラス。
 * <pre>
 * 各種フィールドの基底クラスです。
 * </pre>
 * @extends WebComponent
 */
class Field extends WebComponent {
	/**
	 * コンストラクタ。
	 */
	constructor() {
		super();
	}

	/**
	 * HTMLエレメントとの対応付けを行います。
	 * <pre>
	 * 関連データ取得がtrueの場合、blurイベントに関連データ取得処理を登録します。
	 * Autocompleteが有効な場合、Autocompleteの設定を行います。
	 * 計算イベント発生フィールドの場合、changeイベントで計算処理を呼び出すように設定します。
	 * </pre>
	 */
	attach() {
		let thisField = this;
		super.attach();
		if (this.label == null) {
			this.label = this.getLabel();
		}
		// nameが指定されていない場合nameにidを設定.
		let comp = this.parent.get(this.id);
		if (comp.attr("name") == null) {
			comp.attr("name", comp.attr(this.getIdAttribute()));
		}
		this.backupStyle();
		if (this.relationDataAcquisition) {
			if (this.relationDataEvent == "BLUR") {
				comp.blur((ev) => {
					this.adjustIdIndex($(ev.currentTarget));
					this.getRelationData();
				});
			} else {
				comp.change((ev) => {
					this.adjustIdIndex($(ev.currentTarget));
					this.getRelationData();
				});
			}
		}
		thisField.get().prop("fieldObject", this);
		if (this.autocomplete) {
			this.setAutocomplete();
		}
		if (this.calcEventField) {
			comp.change((ev) => {
				this.callOnCalc($(ev.currentTarget));
			});
		}
		if (this.readonly) {
			this.lock(true);
		}
	}

	/**
	 * テーブルに配置されたフィールドidのインデックスを調整します。
	 * @param {jQuery} jq 調整するフィールドのjQueryオブジェクト。
	 */
	adjustIdIndex(jq) {
		this.id = jq.attr(this.getIdAttribute())
		if (currentPage.useUniqueId) {
			this.realId = jq.attr("id");
		}
	}

	/**
	 * 親フォームのonCalcメソットを呼び出します。
	 * @param {jQuery} f フィールド。
	 */
	callOnCalc(f) {
		let form = this.getParentForm();
		form.onCalc(f);
	}

	/**
	 * バリデータの初期化を行います。
	 * @param {Array} vlist バリデータリスト.
	 */
	initValidator(vlist) {
		this.validators = [];
		for (let i = 0; i < vlist.length; i++) {
			let v = vlist[i];
			let validator = this.newInstance(v);
			validator.init();
			this.validators[i] = validator;
		}
	}

	/**
	 * Autocomplete等の際にサーバに送信するパラメータを取得します。
	 * @returns {String} サーバに送信するQueryString形式のパラメータ。
	 */
	getAjaxParameter() {
		if (this.ajaxParameter == "FORM") {
			return this.getParentForm().get().serialize() + "&currentFieldId=" + this.id;
		} else {
			return this.id + "=" + this.get().val() + "&currentFieldId=" + this.id;
		}
	}

	/**
	 * フィールドのラベルエレメントを取得します。
	 * @returns {jQuery} フィールドに対応したラベルを取得します。
	 */
	getLabelElement() {
		let label = null;
		let tag = this.parent.find("label[for='" + this.id +"']");
		if (tag.length > 0) {
			// ラベルのIDが指定されていた場合の処理.
			label = tag;
		} else {
			// フィールドに対応したラベルを探す処理.
			let l = this.parent.get(this.id).prev('label:last');
			if (l.length > 0) {
				label = l;
			} else {
				l = this.parent.get(this.id).parent(':last').prev().find('label:first');
				if (l.length > 0) {
					label = l;
				} else {
					l = this.parent.get(this.id).parent(':last').prev();
					label = l;
					if (label.text().length == 0) {
						l = this.parent.get(this.id).parents('td:last').prev()
						label = l;
					}
				}
			}
			if (label == null || label.length == 0) {
				let l = this.parent.get(this.id + "[0]").parent(':last').prev('label:last');
				if (l.length > 0) {
					label = l;
				} else {
					l = this.parent.get(this.id + "[0]").parent(':last').parent(':last').prev().find('label:first');
					if (l.length > 0) {
						label = l;
					} else {
						l = this.parent.get(this.id + "[0]").parent(':last').parent(':last').prev();
						label = l;
					}
				}
			}
		}
		return label;
	}

	/**
	 * フィールドのラベル文字列を取得する。
	 * @return {String} フィールドに対応したラベル文字列。
	 */
	getLabel() {
		let labelstr = this.id;
		let label = this.getLabelElement();
		if (label != null) {
			labelstr = label.html();
		}
		return labelstr;
	}

	/**
	 * 初期化処理を行います。
	 */
	init() {
		super.init();
		this.initValidator(this.validatorList);
	}

	/**
	 * 属性のバックアップをとります。
	 */
	backupStyle() {
		let comp = this.get();
		comp.prop("readonly-bak", comp.prop("readonly"));
	}

	/**
	 * 関連データの更新後に呼び出されるメソッドです。
	 */
	onUpdateRelationField() {
		logger.log("onUpdateRelationField");
	}

	/**
	 * 関連データの取得を行います。
	 * <pre>
	 * 関連データ取得がONの場合フィールド値の更新時にこの処理が呼ばれます。
	 * 各フィールドのgetRelationDataメソッドが呼び出され、その結果を親フォームに設定します。
	 * </pre>
	 */
	async getRelationData() {
		try {
			let m = this.getWebMethod("getRelationData");
			let form = this.getParentForm();
			let param = this.getAjaxParameter();
			let ret = await m.execute(param);
			if (ret.status == JsonResponse.SUCCESS) {
				for (let k in ret.result) {
					if (Array.isArray(ret.result[k])) {
						let t = form.getComponent(k);
						if (t != null && typeof t.setTableData == "function") {
							t.setTableData(ret.result[k]);
						}
					} else {
						form.setFieldValue(k, ret.result[k]);
					}
				}
			}
			this.onUpdateRelationField();
		} catch (e) {
			currentPage.reportError(e);
		}
	}

	/**
	 * INPUTまたはSELECTタグへ値を設定します。
	 * @param {jQuery} comp 値を設定するコンポーネント。
	 * @param {String} value 値.
	 */
	setInputValue(comp, value) {
		comp.val(value);
	}

	/**
	 * SPAN等の表示用タグへ値を設定します。
	 * @param {jQuery} comp 値を設定するコンポーネント。
	 * @param {String} value 値。
	 */
	setTextValue(comp, value) {
		if (value == null) {
			comp.text("");
		} else {
			comp.text(value);
		}
	}

	/**
	 * 値を設定します。
	 * @param {String} value 設定値。
	 */
	setValue(value) {
		let comp = this.get();
		let tag = comp.prop("tagName");
		if ("INPUT" == tag || "TEXTAREA" == tag || "SELECT" == tag) {
			this.setInputValue(comp, value);
			if ("SELECT" == tag) {
				if (this.readonly) {
					let v = this.find("option:selected").text()
					let span = this.addSpan(comp);
					span.text(v);
				}
			}
		} else {
			this.setTextValue(comp, value);
		}
	}

	/**
	 * 値を取得します。
	 * @return {String} 値。
	 */
	getValue() {
		let comp = this.get();
		let tag = comp.prop("tagName");
		if ("INPUT" == tag || "TEXTAREA" == tag || "SELECT" == tag) {
			return comp.val();
		} else {
			return comp.text();
		}
	}

	/**
	 * フィールドの検証を行ないます。
	 * <pre>
	 * 各フィールドのバリデータを呼び出します。
	 * 追加のチェックが必要な場合、このメソッドをオーバーライドします。
	 * </pre>
	 * @returns {ValidationError} 検証結果。問題が発生しなければnullを返します。
	 */
	validate() {
		let val = this.get().val();
		this.value = val;
		if (this.validators != null) {
			for (let i = 0; i < this.validators.length; i++) {
				let v = this.validators[i];
				if (v.validate(val) == false) {
					let msg = v.getMessage(this.label);
					return new ValidationError(this.id, msg);
				}
			}
		}
		return null;
	}

	/**
	 * 指定されたバリデータを呼び出します。
	 * <pre>
	 * Fieldに登録されていないバリデータを呼び出すときに使用します。
	 * </pre>
	 * @param {FieldValidator} v 呼び出すバリデータ。
	 * @returns {ValidationError} 検証結果。正常な場合null。
	 */
	performValidator(v) {
		let val = this.getParentForm().getFieldValue(this.id);
		this.value = val;
		if (v.validate(val) == false) {
			let msg = v.getMessage(this.label);
			return new ValidationError(this.id, msg);
		}
		return null;
	}

	/**
	 * データ確認用のspanタグを作成します。
	 * <pre>
	 * checkbox,raido,select等のフィールドをロックした場合、元々のコントロールを隠し
	 * &lt;span&gt;タグで値を表示します。
	 * </pre>
	 * @param {jQuery} comp 対応するコンポーネント。
	 * @returns {jQuery} 作成したspan要素。
	 */
	addSpan(comp) {
		let spanid = this.id + "_span";
		let span = this.parent.get(spanid);
		if (span.length == 0) {
			if (currentPage.useUniqueId) {
				comp.after("<span data-id='" + spanid + "' id='" + this.realId + "_span' class='selectSpan'></span>");
			} else {
				comp.after("<span id='" + spanid + "' class='selectSpan'></span>");
			}
			span = this.parent.get(spanid);
		}
		return span;
	}

	/**
	 * テキストボックスをロックします。
	 * @param {Boolean} lk ロックする場合true。
	 */
	lockTextbox(lk) {
		let comp = this.get();
		if (lk) {
			comp.prop("readonly", true);
			comp.css("border-style", "none");
			comp.css("outline", "none");
			comp.addClass("lockedTextbox");
		} else {
			comp.prop("readonly", comp.prop("readonly-bak"));
			comp.css("border-style", "");
			comp.css("outline", "");
			comp.removeClass("lockedTextbox");
		}
	}


	/**
	 * ラジオボタンをロックします。
	 * @param {Boolean} lk ロックする場合true。
	 */
	lockRadio(lk) {
		let comp = this.get();
		let block = comp.parent("span");
		let v = "";
		comp.each((_, el) => {
			if ($(el).prop("checked")) {
				v = $(el).val();
			}
		});
		let span = this.addSpan(block);
		if (lk) {
			block.hide();
			span.show();
			this.setTextValue(span, v);
		} else {
			block.show();
			span.hide();
		}
	}


	/**
	 * チェックボックスをロックします。
	 * @param {Boolean} lk ロックする場合true。
	 */
	lockCheckbox(lk) {
		let comp = this.get();
		let block = comp.parent('span');
		let v = [];
		comp.each((_, el) => {
			if ($(el).prop("checked")) {
				v.push($(el).val());
			}
		});
		let span = this.addSpan(block);
		if (lk) {
			block.hide();
			span.show();
			this.setTextValue(span, v);
		} else {
			block.show();
			span.hide();
		}
	}


	/**
	 * ファイルフィールドをロックします。
	 * @param {Boolean} lk ロックする場合true。
	 */
	lockFile(lk) {
		let comp = this.get();
		let selbtn = this.parent.get(this.id + "_sel");
		let delbtn = this.parent.get(this.id + "_del");
		let fnlink = this.parent.get(this.id + "_link");
		if (lk) {
			selbtn.hide();
			delbtn.hide();
			if (comp.val().length > 0) {
				comp.next("a:first").hide();
			}
		} else {
			selbtn.show();
			let v = fnlink.attr("href");
			if (v != null && v.length > 0) {
				delbtn.show();
			} else {
				delbtn.hide();
			}
			comp.next("a:first").show();
		}
	}

	/**
	 * SELECTをロックします。
	 * @param {Boolean} lk ロックする場合true。
	 */
	lockSelect(lk) {
		let comp = this.get();
		let v = this.find("option:selected").text();
		let span = this.addSpan(comp);
		if (lk) {
			comp.hide();
			span.show();
			span.text(v);
		} else {
			comp.show();
			span.hide();
		}
	}

	/**
	 * フィールドのロック/ロック解除を行ないます。
	 * @param {Boolean} lk ロックする場合true。
	 */
	lock(lk) {
		if (lk == false && this.readonly == true) {
			return;
		}
		let comp = this.get();
		let tag = comp.prop("tagName");
		let type = comp.prop("type");
		if (("INPUT" == tag && (type.toLowerCase() == "text"
			|| type.toLowerCase() == "password"
			|| type.toLowerCase() == "email"
			|| type.toLowerCase() == "url"
			|| type.toLowerCase() == "tel"
			|| type.toLowerCase() == "number"
			)) || "TEXTAREA" == tag) {
			this.lockTextbox(lk);
		} else if ("INPUT" == tag && type.toLowerCase() == "radio") {
			this.lockRadio(lk);
		} else if ("INPUT" == tag && type.toLowerCase() == "checkbox") {
			this.lockCheckbox(lk);
		} else if ("INPUT" == tag && type.toLowerCase() == "file") {
			this.lockFile(lk);
		} else if ("SELECT" == tag) {
			this.lockSelect(lk);
		}
	}

	/**
	 * 必須入力フィールドがどうかを判定します。
	 * <pre>
	 * 指定されたフィールドがテキスト入力フィールとまたはSELECTでかつ
	 * 必須バリデータが設定されている場合trueを返します。
	 * </pre>
	 * @param {jQuery} [el] フィールドに対応するjQueryオブジェクト。HTMLテーブル中のフィールドの場合指定します。
	 */
	isRequired(el) {
		if (el == null) {
			el = this.get();
		}
		let tag = el.prop("tagName");
		let type = el.prop("type");
		if ("INPUT" == tag || "TEXTAREA" == tag || "SELECT" == tag) {
			for (let i = 0; i < this.validatorList.length; i++) {
				let v = this.newInstance(this.validatorList[i]);
				if (v instanceof RequiredValidator) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 入力補完データを取得します。
	 * <pre>
	 * 対応するフィールドのgetAutocompleteSourceを呼び出し、入力候補を取得します。
	 * </pre>
	 * @param {res} リスト設定メソッド.
	 */
	async getSource(res) {
		try {
			let method = this.getWebMethod("getAutocompleteSource");
			let param = this.getAjaxParameter();
			let ret = await method.execute(param,);
			if (ret.status == JsonResponse.SUCCESS) {
				res(ret.result);
			}
		} catch (e) {
			currentPage.reportError(e);
		}
	}

	/**
	 * autocompleteの選択時の処理を記述します。
	 */
	onAutocompleteSelected() {
		if (!this.relationDataAcquisition) {
			this.onUpdateRelationField();
		}
	}

	/**
	 * Autocompleteの設定を行います。
	 */
	setAutocomplete() {
		this.get().autocomplete({
			search: (ev) => {
				this.adjustIdIndex($(ev.currentTarget));
			},
			source: (_, res) => {
				this.getSource(res);
			},
			select: (ev, ui) => {
				for (let k in ui.item) {
					if (k == "label") {
					} else if (k == "value") {
					} else {
						if (this.parent instanceof Form) {
							this.parent.setFieldValue(k, ui.item[k]);
						} else if (this.parent instanceof HtmlTable) {
							this.parent.parent.setFieldValue(k, ui.item[k]);
						}
					}
				}
				if (this.calcEventField) {
					logger.log("thisField.calcEventField=" + this.calcEventField);
					let form = this.getParentForm();
					form.onCalc($(ev.currentTarget));
				}
				this.onAutocompleteSelected();
			}
		});
	}

	/**
	 * フィールドのIDが変更された場合呼び出されます。
	 * <pre>
	 * EditableHtmlTableの場合、行追加/削除やソートによって、各フィールドのIDが変化します。
	 * DateField等はIDが変わると、datepickerの動作がおかしくなるため、このメソッドで、
	 * datepickerを再作成を行います。
	 * </pre>
	 */
	onIdChange() {
	}

	/**
	 * フィールドが削除された場合呼び出されます。
	 * <pre>
	 * HtmlTableのデータ更新時やEdiatableHtmlTableの行削除時等フィールドが、削除されるタイミングで
	 * 呼び出されます。通常イベントハンドラ等の削除を行います。
	 * </pre>
	 */
	onDestroy() {
		this.get().unbind();
	}

	/**
	 * マップ中の対応フィールドを比較します。
	 * @param a {Object} 比較対象のマップ1。
	 * @param b {Object} 比較対象のマップ2。
	 * @returns {Number} 比較結果。
	 */
	comp(a, b) {
		let ret = 0;
		if (a[this.id].toString() < b[this.id].toString()) {
			ret = -1;
		} else if (a[this.id].toString() > b[this.id].toString()) {
			ret = 1;
		}
		return ret;
	}

	/**
	 * 同一フォーム内の別フィールドまたは同一テーブルの同一行内のフィールドを取得します。
	 * @param {String} id 取得するフィールドID。
	 * @returns {Field} フィールド。
	 */
	getNearField(id) {
		let tableId = this.getHtmlTableId(this.id);
		if (tableId != null) {
			return this.parent.getSameRowField(this, id);
		} else {
			return this.parent.getComponent(id);
		}
	}

}


