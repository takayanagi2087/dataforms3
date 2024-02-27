/**
 * @fileOverview {@link TableGeneratorEditForm}クラスを記述したファイルです。
 */

'use strict';

/**
 * @class TableGeneratorEditForm
 *
 * @extends EditForm
 */
class TableGeneratorEditForm extends EditForm {
	/**
	 * HTMLエレメントとの対応付けを行います。
	 */
	attach() {
		super.attach();
		let tbl = this.getComponent("fieldList");
		this.get("allErrorButton").click(() => {
			this.find("[id$='\\.overwriteMode']").each((_, el) => {
				$(el).val("error");
			});
		});
		this.get("allSkipButton").click(() => {
			this.find("[id$='\\.overwriteMode']").each((_, el) => {
				$(el).val("skip");
			});
		});
		this.get("allForceOverwriteButton").click(() => {
			this.find("[id$='\\.overwriteMode']").each((_, el) => {
				$(el).val("force");
			});
		});
		this.get("errorSkipButton").click(() => {
			this.find("[id$='\\.fieldClassName'].errorField").each((_, el) => {
				tbl.getSameRowField($(el), "overwriteMode").val("skip");
			});
		});
		this.get("errorForceButton").click(() => {
			this.find("[id$='\\.fieldClassName'].errorField").each((_, el) => {
				tbl.getSameRowField($(el), "overwriteMode").val("force");
			});
		});
		this.get("printButton").click(() => {
			this.print();
		});

		this.get("showImportButton").click(() => {
			this.showImportField();
		});

		this.get("importButton").click(() => {
			this.importTable();
		});

		this.get("functionSelect").change(() => {
			this.onChangeFunction();
		});
	}

	/**
	 * 機能変更時のパッケージ設定。
	 */
	onChangeFunction() {
		let func = this.getFieldValue("functionSelect");
		if (func != null && func.length > 0) {
			let fieldList = this.getComponent("fieldList");
			for (let i = 0; i < fieldList.getRowCount(); i++) {
				let pkgf = fieldList.getRowField(i, "packageName");
				let oldv = pkgf.getValue();
				if (oldv.length == 0) {
					pkgf.setValue(func.substr(1) + ".field");
				}
			}
		}
	}

	/**
	 * インポート関連フィールドの表示。
	 */
	showImportField() {
		this.find(".importFields").toggle();
	}

	/**
	 * インポート情報をフォームに設定します。
	 * @param {Object} data インポートデータ。
	 */
	setTableInfo(data) {
		this.setFieldValue("tableClassName", data.tableClassName);
		this.setFieldValue("tableComment", data.tableComment);
		if (data.updateInfoFlag == "0") {
			this.get("updateInfoFlag").prop("checked", false);
		} else {
			this.get("updateInfoFlag").prop("checked", true);
		}
		let fieldList = this.getComponent("fieldList");
		fieldList.setTableData(data.fieldList);
	}

	/**
	 * インポート関連フィールドの表示。
	 */
	async importTable() {
		try {
			let m = this.getWebMethod("importTable");
			let importTable = this.getFieldValue("importTable");
			let func = this.getFieldValue("functionSelect");
			let r = await m.execute("importTable=" + importTable + "&functionSelect=" + func);
			if (r.status == JsonResponse.SUCCESS) {
				this.setTableInfo(r.result);
				this.find(".importFields").toggle();
			}
		} catch (e) {
			currentPage.reportError(e);
		}
	}


	/**
	 * 編集モードにします。
	 * <pre>
	 * 各フィールドを編集可能状態にします。
	 * </pre>
	 */
	toEditMode() {
		super.toEditMode();
		// 更新時にはクラス名を編集できなくする。
		let funcsel = this.getComponent("functionSelect");
		let pkgname = this.getComponent("packageName");
		let cnfield = this.getComponent("tableClassName");
		if (this.saveMode == "new") {
			funcsel.lock(false);
			pkgname.lock(false);
			cnfield.lock(false);
		} else {
			funcsel.lock(true);
			pkgname.lock(true);
			cnfield.lock(true);
		}
		let tbl = this.getComponent("fieldList");
		let n = tbl.find("tbody>tr").length;
		for (let i = 0; i < n; i++) {
			let spkg = tbl.getComponent("fieldList[" + i + "].superPackageName");
			let scls = tbl.getComponent("fieldList[" + i + "].superSimpleClassName");
			let ovm = tbl.getComponent("fieldList[" + i + "].overwriteMode");
			logger.log("spkg.id=" + spkg.id);
			logger.log("scls.id=" + scls.id);
			let flg = this.get("fieldList[" + i + "].isDataformsField");
			logger.log("flg=" + flg.val());
			if (flg.val() == "0") {
				logger.log("unlock");
				spkg.lock(false);
				scls.lock(false);
				ovm.get().show();
			} else {
				logger.log("lock");
				spkg.lock(true);
				scls.lock(true);
				ovm.get().hide();
				let len = tbl.getComponent("fieldList[" + i + "].fieldLength");
				if (len.get().val().length > 0) {
					len.lock(false);
				} else {
					len.lock(true);
				}
			}
		}
		this.get("allErrorButton").prop("disabled", false);
		this.get("allSkipButton").prop("disabled", false);
		this.get("allForceOverwriteButton").prop("disabled", false);
		this.get("errorSkipButton").prop("disabled", false);
		this.get("errorForceButton").prop("disabled", false);
	}

	/**
	 * 確認モードにします。
	 * <pre>
	 * 各フィールドを編集可能状態にします。
	 * </pre>
	 */
	toConfirmMode() {
		super.toConfirmMode();
		this.get("allErrorButton").prop("disabled", true);
		this.get("allSkipButton").prop("disabled", true);
		this.get("allForceOverwriteButton").prop("disabled", true);
		this.get("errorSkipButton").prop("disabled", true);
		this.get("errorForceButton").prop("disabled", true);
	}


	/**
	 * 計算イベント処理を行います。
	 * <pre>
	 * 計算イベントフィールドが更新された場合、このメソッドが呼び出されます。
	 * データ入力時の自動計算が必要な場合このメソッドをオーバーライドしてください。
	 * </pre>
	 * @param {jQuery} element イベントが発生した要素。
	 */
	onCalc(element) {
		if (element != null) {
			logger.log("element.id=" + element.attr(this.getIdAttribute()));
			let id = element.attr(this.getIdAttribute());
			if (id.indexOf("packageName") >= 0 || id.indexOf("fieldClassName") >= 0) {
				this.onCalcClass(element);
			} else if (id.indexOf("superPackageName") > 0 || id.indexOf("superSimpleClassName") >= 0) {
				this.onCalcSuperClass(element);
			}
		}
	}

	/**
	 * フィールドクラスの計算イベント処理を行います。
	 * @param {jQuery} element イベントが発生した要素。
	 */
	async onCalcClass(element) {
		try {
			let thisForm = this;
			let tbl = this.getComponent("fieldList");
			let p = tbl.getSameRowField(element, "packageName").val();
			let c = tbl.getSameRowField(element, "fieldClassName").val();
			if (p.length > 0 && c.length > 0) {
				let classname = p + "." + c;
				logger.log("classname=" + classname);
				let method = this.getWebMethod("getFieldClassInfo");
				let ret = await method.execute("classname=" + classname);
				if (ret.status == JsonResponse.SUCCESS) {
					let dfflg = tbl.getSameRowField(element, "isDataformsField");
					let len = tbl.getSameRowField(element, "fieldLength");
					let cmnt = tbl.getSameRowField(element, "comment");
					let bpkg = tbl.getSameRowField(element, "superPackageName");
					let bcls = tbl.getSameRowField(element, "superSimpleClassName");
					let owm = tbl.getSameRowField(element, "overwriteMode");
					dfflg.val(ret.result.isDataformsField);
					if (ret.result.isDataformsField == "1") {
						if (ret.result.fieldLength != null && ret.result.fieldLength.length > 0) {
							len.val(ret.result.fieldLength);
							tbl.getComponent(len.attr(thisForm.getIdAttribute())).lock(false);
						} else {
							len.val("");
							tbl.getComponent(len.attr(thisForm.getIdAttribute())).lock(true);
						}
						bpkg.val(ret.result.superClassPackage);
						bcls.val(ret.result.superClassSimpleName);
						cmnt.val(ret.result.fieldComment);
						tbl.getComponent(bpkg.attr(thisForm.getIdAttribute())).lock(true);
						tbl.getComponent(bcls.attr(thisForm.getIdAttribute())).lock(true);
						owm.hide();
					} else {
						if (ret.result.fieldLength != null && ret.result.fieldLength.length > 0) {
							if (len.val().length == 0) {
								len.val(ret.result.fieldLength);
							}
						}
						if (ret.result.superClassPackage != null) {
							bpkg.val(ret.result.superClassPackage);
						}
						if (ret.result.superClassSimpleName != null) {
							bcls.val(ret.result.superClassSimpleName);
						}
						if (ret.result.fieldComment != null && ret.result.fieldComment.length > 0) {
							if (cmnt.val().length == 0) {
								cmnt.val(ret.result.fieldComment);
							}
						}
						tbl.getComponent(bpkg.attr(thisForm.getIdAttribute())).lock(false);
						tbl.getComponent(bcls.attr(thisForm.getIdAttribute())).lock(false);
						owm.show();
					}
				}
			}
		} catch (e) {
			currentPage.reportError(e);
		}
	}

	/**
	 * 親クラスの計算イベント処理を行います。
	 * @param {jQuery} element イベントが発生した要素。
	 */
	async onCalcSuperClass(element) {
		try {
			let tbl = this.getComponent("fieldList");
			let p = tbl.getSameRowField(element, "superPackageName").val();
			let c = tbl.getSameRowField(element, "superSimpleClassName").val();
			if (p.length > 0 && c.length > 0) {
				let classname = p + "." + c;
				logger.log("super classname=" + classname);
				let method = this.getWebMethod("getSuperFieldClassInfo");
				let ret = await method.execute("superclassname=" + classname);
				if (ret.status == JsonResponse.SUCCESS) {
					let len = tbl.getSameRowField(element, "fieldLength");
					if (len.val().length == 0) {
						len.val(ret.result.fieldLength);
					}
				}
			}
		} catch (e) {
			currentPage.reportError(e);
		}
	}

	/**
	 * テーブル定義書を作成します。
	 */
	async print() {
		try {
			this.parent.resetErrorStatus();
			await this.submit("print");
		} catch (e) {
			currentPage.reportError(e);
		}
	}
}
