/**
 * @fileOverview {@link DaoAndPageGeneratorEditForm}クラスを記述したファイルです。
 */

'use strict';

/**
 * @class DaoAndPageGeneratorEditForm
 *
 * @extends EditForm
 */
class DaoAndPageGeneratorEditForm extends EditForm {
	/**
	 * コンストラクタ。
	 */
	constructor() {
		super();
	}

	/**
	 * HTMLエレメントとの対応付けを行います。
	 */
	attach() {
		super.attach();
		this.get("allErrorButton").click(() => {
			this.find("[id$='OverwriteMode']").each((_, el) => {
				$(el).val("error");
			});
		});
		this.get("allSkipButton").click(() => {
			this.find("[id$='OverwriteMode']").each((_, el) => {
				$(el).val("skip");
			});
		});
		this.get("allForceOverwriteButton").click(() => {
			this.find("[id$='OverwriteMode']").each((_, el) => {
				$(el).val("force");
			});
		});
		this.get("errorSkipButton").click(() => {
			this.find(".errorField").each((_, el) => {
				let id = $(el).attr(this.getIdAttribute());
				if (id.indexOf("ClassName") > 0) {
					let owmId = id.replace("ClassName", "ClassOverwriteMode");
					this.setFieldValue(owmId, "skip");
				}
			});
		});
		this.get("errorForceButton").click(() => {
			this.find(".errorField").each((_, el) => {
				let id = $(el).attr(this.getIdAttribute());
				if (id.indexOf("ClassName") > 0) {
					let owmId = id.replace("ClassName", "ClassOverwriteMode");
					this.setFieldValue(owmId, "force");
				}
			});
		});

		this.get("pagePattern").change((ev) => {
			this.onChangePageType(ev);
		});
		this.get("pageClassName").change((ev) => {
			this.onUpdateClassName($(ev.currentTarget));
		});
		this.get("listQueryButton").click(() => {
			this.onListQueryButton();
		});
		this.get("editQueryButton").click(() => {
			this.onEditQueryButton();
		});
		this.onChangePageType(null);
	}

	/**
	 * ページ動作の変更時の処理。
	 */
	onChangePagePattern() {
	}

	/**
	 * Form名称を更新します。
	 * @param {jQuery} pc ページクラス名フィールド。
	 */
	onUpdateClassName(pc) {
		let pcname = pc.val();
		let n = pcname.replace(/Page$/, "");
		this.setFieldValue("daoClassName", n + "Dao");
		this.setFieldValue("formClassName", n + "Form");
		this.setFieldValue("queryFormClassName", n + "QueryForm");
		this.setFieldValue("queryResultFormClassName", n + "QueryResultForm");
		this.setFieldValue("editFormClassName", n + "EditForm");

	}

	/**
	 *ページタイプの変更処理。
	 * @param {Event} ev イベント情報。
	 */
	onChangePageType(ev) {
		logger.log("ev=", ev);
		let pagePattern = this.get("pagePattern").val();
		if (pagePattern != null && pagePattern.length > 0) {
			// ページの説明
			let v = this.get("pagePattern").val();
			logger.log("pagePatten=" + v);
			if (v != null && v.length > 0) {
				this.find("div.pageDesc").hide();
				let did ="desc" + v.substring(2);
				logger.log("did=" + did);
				this.get(did).show();
			}

			let queryFormSelect = pagePattern.charAt(2);
			let listFormSelect = pagePattern.charAt(3);
			let editFormSelect = pagePattern.charAt(4);

			if (queryFormSelect == "0" && listFormSelect == "0" && editFormSelect == "0") {
				this.get("formClassName").prop("disabled", false);
				this.get("formClassOverwriteMode").prop("disabled", false);
				this.find(".queryForm").prop("disabled", true);
				this.find(".listForm").prop("disabled", true);
				this.find(".editForm").prop("disabled", true);
				this.get("daoPackageName").prop("disabled", true);
				this.get("daoClassName").prop("disabled", true);
				this.get("daoClassOverwriteMode").prop("disabled", true);
				this.find(".multiRecoedQueryList").hide();
				this.get("queryFormClassName").val("");
				this.get("queryResultFormClassName").val("");
				this.get("editFormClassName").val("");
			} else {
				let pcname = this.get("pageClassName").val();
				let n = pcname.replace(/Page$/, "");
				this.get("formClassName").prop("disabled", true);
				this.get("formClassOverwriteMode").prop("disabled", true);
				this.find(".listQueryCondition").hide();
				this.find(".editQueryCondition").hide();
				this.find(".noNextForm").hide();
				this.get("daoPackageName").prop("disabled", false);
				this.get("daoClassName").prop("disabled", false);
				this.get("daoClassOverwriteMode").prop("disabled", false);
				if ("1" == queryFormSelect) {
					this.find(".queryForm").prop("disabled", false);
					if ("1" == listFormSelect) {
						this.find(".listQueryCondition").show();
					} else if ("1" == editFormSelect || "2" == editFormSelect) {
						this.find(".editQueryCondition").show();
					} else {
						this.find(".noNextForm").show();
					}
					this.get("queryFormClassName").val(n + "QueryForm");
					this.find(".queryForm").prop("disabled", false);
				} else {
					this.find(".queryForm").prop("disabled", true);
					this.get("queryFormClassName").val("");
				}
				if (listFormSelect == "0") {
					this.find(".queryResultFormDesc").hide();
					this.find(".listForm").prop("disabled", true);
					this.get("queryResultFormClassName").val("");
				} else {
					this.find(".queryResultFormDesc").show();
					this.find(".listForm").prop("disabled", false);
					this.get("queryResultFormClassName").val(n + "QeuryResultForm");
				}
				if (editFormSelect == "0") {
					this.find(".editFormDesc").hide();
					this.find(".editQueryCondition1").hide();
					this.find(".editForm").prop("disabled", true);
					this.get("editFormClassName").val("");
				} else {
					this.find(".editFormDesc").show();
					if (listFormSelect == 1) {
						this.find(".editQueryCondition1").show();
					}
					this.find(".editForm").prop("disabled", false);
					this.get("editFormClassName").val(n + "EditForm");
				}
				if (editFormSelect == "1") {
					this.find(".multiRecoedQueryList").show();
				} else {
					this.find(".multiRecoedQueryList").hide();
				}
			}
		}
	}

	/**
	 * フォームのパッケージを設定する。
	 * @param {jQuery} イベントが発生した要素。
	 */
	setPackageName(jq) {
		logger.log("setPackageName=", jq);
		let packageName = this.get("packageName").val();
		logger.log("packageName=" + packageName);
		this.find(".packageName").each((_, el) => {
			$(el).text(packageName);
		});
		let func = this.get("functionSelect").val();
		this.find(".funcName").each((_, el) => {
			$(el).text(func);
		});
	}

	/**
	 * 一覧問合せフィールド設定。
	 */
	onListQueryButton() {
		let json = this.get("listQueryConfig").val();
		let conf = JSON.parse(json);
		logger.log("conf=" + json);
		let fieldListDialog = this.parent.getComponent("fieldListDialog");
		logger.log("fieldListDialog=", fieldListDialog);
		let pkg = this.get("listQueryPackageName").val();
		let cls = this.get("listQueryClassName").val();
		if (pkg.length > 0 && cls.length > 0) {
			fieldListDialog.showModal({
				title: MessagesUtil.getMessage("daoandpagegenerator.listfielddialog"),
				width: 1024,
				targetClass: pkg + "." + cls,
				listQuery: true,
				conf: conf,
				setFunc: (json) => {
					this.getComponent("listQueryConfig").setValue(json);
				}
			});
		}
	}

	/**
	 * 編集問合せフィールド設定。
	 */
	onEditQueryButton() {
		let json = this.get("editQueryConfig").val();
		let conf = JSON.parse(json);
		let fieldListDialog = this.parent.getComponent("fieldListDialog");
		logger.log("fieldListDialog=", fieldListDialog);
		let pkg = this.get("editQueryPackageName").val();
		let cls = this.get("editQueryClassName").val();
		logger.log("pkg=" + pkg + ", cls=" + cls);
		if (pkg.length > 0 && cls.length > 0) {
			fieldListDialog.showModal({
				title: MessagesUtil.getMessage("daoandpagegenerator.editfielddialog"),
				width: 1024,
				targetClass: pkg + "." + cls,
				listQuery: false,
				conf: conf,
				setFunc: (json) => {
					this.getComponent("editQueryConfig").setValue(json);
				}
			});
		}
	}

	/**
	 * フィールドのデフォルト設定を取得する。
	 * @param {String} cls クラス名。
	 * @param {String} cfgid 設定情報フィールドID。
	 */
	async getFieldInfo(cls, cfgid) {
		let m = this.getWebMethod("getFieldConfig");
		let r = await m.execute("c=" + cls);
		if (r.status == JsonResponse.SUCCESS) {
			this.get(cfgid).val(JSON.stringify(r.result));
		}
	}

	/**
	 * フィールドのデフォルト設定を取得する。
	 * @param {String} id フィールドID。
	 */
	getFieldConfig(id) {
		logger.log("field id=" + id);
		if ("listQueryClassName" == id) {
			let pkg = this.get("listQueryPackageName").val();
			let cls = this.get("listQueryClassName").val();
			this.getFieldInfo(pkg + "." + cls, "listQueryConfig");
		} else if ("editQueryClassName" == id) {
			let pkg = this.get("editQueryPackageName").val();
			let cls = this.get("editQueryClassName").val();
			this.getFieldInfo(pkg + "." + cls, "editQueryConfig");
		}
	}

	/**
	 * 各フィールドにデータを設定します。
	 * <pre>
	 * 新規モードの場合、削除ボタンを隠します。
	 * </pre>
	 * @param {Object} data フォームデータ.
	 *
	 */
	setFormData(data) {
		super.setFormData(data);
		let functionSelect = this.get("functionSelect").val();
		let packageName = this.get("packageName").val();
		this.find(".funcName").each((_, el) => {
			$(el).text(functionSelect);
		});
		this.find(".packageName").each((_, el) => {
			$(el).text(packageName);
		});
		let listQueryPackageName = this.get("listQueryPackageName").val();
		if (listQueryPackageName.length > 0) {
			let sel = this.getComponent("listQueryFunctionSelect");
			logger.log("listPackageName=" + listQueryPackageName, sel);
			sel.selectPackage(listQueryPackageName);
		}
		let editQueryPackageName = this.get("editQueryPackageName").val();
		if (editQueryPackageName.length > 0) {
			let sel = this.getComponent("editQueryFunctionSelect");
			logger.log("editQueryPackageName=" + editQueryPackageName, sel);
			sel.selectPackage(editQueryPackageName);
		}
		this.onChangePageType(null);
		let multiRecordQueryList = this.getComponent("multiRecordQueryList");
		for (let i = 0; i < multiRecordQueryList.getRowCount(); i++) {
			let sel = multiRecordQueryList.getRowField(i, "functionSelect");
			let pkg = multiRecordQueryList.getRowField(i, "packageName").getValue();
			sel.selectPackage(pkg);
		}
		if (multiRecordQueryList.getRowCount() > 0) {
			this.find("[id$='\.fieldButton']").click((ev) => {
				this.onFieldButton(ev);
			});
		}
		this.onChangePagePattern();
	}

	/**
	 * フィールド設定ボタンの処理。
	 */
	onFieldButton(ev) {
		logger.log("ev=", ev);
		let multiRecordQueryList = this.getComponent("multiRecordQueryList");
		let pkg = multiRecordQueryList.getSameRowField($(ev.currentTarget), "packageName").val();
		let cls = multiRecordQueryList.getSameRowField($(ev.currentTarget), "queryClassName").val();
		let confField = multiRecordQueryList.getSameRowField($(ev.currentTarget), "queryConfig");
		let json = confField.val();
		let conf = JSON.parse(json);
		logger.log("conf=", conf);
		let fieldListDialog = this.parent.getComponent("fieldListDialog");
		logger.log("fieldListDialog=", fieldListDialog);
		logger.log("pkg=" + pkg + ", cls=" + cls);
		if (pkg.length > 0 && cls.length > 0) {
			fieldListDialog.showModal({
				title: MessagesUtil.getMessage("daoandpagegenerator.editfielddialog"),
				width: 1024,
				targetClass: pkg + "." + cls,
				listQuery: false,
				conf: conf,
				setFunc: (json) => {
					logger.log("*** json=" + json);
					logger.log("confField=", confField);
					confField.val(json);
				}
			});
		}
	}

	/**
	 * フォームのバリデーション。
	 * <pre>
	 * フォーム内のフィールド関連チェックを実装します。
	 * </pre>
	 */
	validateForm() {
		let list = super.validateForm();
		if (list.length == 0) {
			let json = this.get("editQueryConfig").val();
			if (json.length > 0) {
				let flist = JSON.parse(json);
				logger.log("editFieldList=", flist);
			}
			let qfc = this.get("queryFormClassName").val();
			let qrfc = this.get("queryResultFormClassName").val();
			if (qfc.length > 0 || qrfc.length > 0) {
				let lqc = this.get("listQueryClassName").val();
				if (lqc.length == 0) {
					let fn = this.find("label[for='listQueryClassName']").text();
					list.push(new ValidationError("listQueryClassName", MessagesUtil.getMessage("error.required", fn)));
				}
			}
			let efc = this.get("editFormClassName").val();
			if (efc.length > 0) {
				let eqc = this.get("editQueryClassName").val();
				if (eqc.length == 0) {
					let fn = this.find("label[for='editQueryClassName']").text();
					list.push(new ValidationError("editQueryClassName", MessagesUtil.getMessage("error.required", fn)));
				}
			}
		}
		return list;
	}

	// 独自のWebメソッドを呼び出す場合は、以下のコードを参考にしてください。
	/**
	 * Webメソッドの呼び出しサンプル。
	 *
	 */
/*
	async callWebMethod() {
		try {
			if (this.validate()) {
				let r = await this.submit("webMethod");
				this.parent.resetErrorStatus();
				if (r.status == JsonResponse.SUCCESS) {
					// TODO:成功時の処理を記述します。
					// 応答情報をログ表示
					logger.dir(r);
				} else {
					this.parent.setErrorInfo(this.getValidationResult(r), this);
				}
			}
		} catch (e) {
			currentPage.reportError(e);
		}
	}
*/


	// フォームの計算処理を行う場合、以下の処理を参考にしてください。
	/**
	 * 計算イベント処理を行います。
	 * <pre>
	 * 計算イベントフィールドが更新された場合、このメソッドが呼び出されます。
	 * データ入力時の自動計算が必要な場合このメソッドをオーバーライドしてください。
	 * </pre>
	 * @param {jQuery} element イベントが発生した要素。初期表示の時等特定フィールドが要因でない場合はnullが設定されます。
	 *
	 */
/*
	onCalc(element) {
	}
*/


	// フォームの各種動作をカスタマイズするには以下のメソッドをオーバーライドしてください。


	/**
	 * 編集モードにします。
	 * <pre>
	 * 各フィールドを編集可能状態にします。
	 * </pre>
	 */
/*
	toEditMode() {
		super.toEditMode();
	}
*/

	/**
	 * 確認モードにします。
	 * <pre>
	 * 各フィールドを編集不可状態にします。
	 * </pre>
	 */
/*
	toConfirmMode() {
		super.toConfirmMode();
	}
*/

	/**
	 * 確認ボタンのイベント処理を行います。
	 * <pre>
	 * 対応するFormのconfirmメソッドを呼び出し、問題なければ確認モードに遷移します。
	 * ファイルアップロードフィールドはサーバーに送信されません。
	 * </pre>
	 */
/*
	confirm() {
		super.confirm();
	}
*/
		/**
	 * 新規登録モードにします。
	 * <pre>
	 * 対応するEditFormのgetNewDataを呼び出し、初期データを取得します。
	 * 各フィールドに取得データを設定し、編集モードにします。
	 * </pre>
	 */
/*
	newData() {
		super.newData();
	}
*/

		/**
	 * 更新登録モードにします。
	 * <pre>
	 * 対応するEditFormのgetDataを呼び出し、編集対象データを取得します。
	 * 各フィールドに取得データを設定し、編集モードにします。
	 * </pre>
	 */
/*
	updateData() {
		super.updateData();
	}
*/

	/**
	 * データを参照登録します。
	 * <pre>
	 * 対応するEditFormのgetReferDataを呼び出し、参照対象データを取得します。
	 * 各フィールドに取得データを設定し、編集モードにします。
	 * </pre>
	 *
	 */
/*
	referData() {
		super.referData();
	}
*/
	/**
	 * データを参照します。
	 * <pre>
	 * 対応するEditFormのgetDataを呼び出し、参照対象データを取得します。
	 * 各フィールドに取得データを設定し、参照モードにします。
	 * </pre>
	 */
/*
	viewData() {
		super.viewData();
	}
*/

	/**
	 * 保存や削除後の画面状態遷移を行います。
	 */
/*
	changeStateForAfterUpdate() {
		super.changeStateForAfterUpdate();
	}
*/

	/**
	 * 保存ボタンのイベント処理を行います。
	 * <pre>
	 * 対応するFormのsaveメソッドを呼び出し、保存処理を行います。
	 * ファイルアップロードフィールドもサーバーに送信されます。
	 * </pre>
	 */
/*
	save() {
		super.save();
	}
*/

	/**
	 * 保存ボタンのイベント処理を行います。
	 * <pre>
	 * 対応するEditFormのdeleteメソッドを呼び出し、保存処理を行います。
	 * </pre>
	 */
/*
	del() {
		super.del();
	}
*/
}

