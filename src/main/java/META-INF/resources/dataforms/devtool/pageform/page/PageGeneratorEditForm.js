/**
 * @fileOverview {@link PageGeneratorEditForm}クラスを記述したファイルです。
 */

'use strict';

/**
 * @class PageGeneratorEditForm
 * テーブル情報フォームクラス。
 * <pre>
 * テーブル情報を表示するためのフォームです。
 * </pre>
 * @extends EditForm
 */
class PageGeneratorEditForm extends EditForm {
	/**
	 * HTMLエレメントフォームとの対応付けを行います。
	 *
	 */
	attach() {
		super.attach();

		this.get("pageClassName").change((ev) => {
			this.updateFormName($(ev.currentTarget));
		});
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
		this.get("queryFormClassFlag").click(() => {
			this.setFormFlag();
		});
		this.get("queryResultFormClassFlag").click(() => {
			this.setFormFlag();
		});
		this.get("editFormClassFlag").click(() => {
			this.setFormFlag();
		});
	}

	/**
	 * Form名称を更新します。
	 * @param {jQuery} pc ページクラス名フィールド。
	 */
	updateFormName(pc) {
		let pcname = pc.val();
		let n = pcname.replace(/Page$/, "");
		this.setFieldValue("queryFormClassName", n + "QueryForm");
		this.setFieldValue("queryResultFormClassName", n + "QueryResultForm");
		this.setFieldValue("editFormClassName", n + "EditForm");
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
		let cnfield = this.getComponent("pageClassName");
		if (this.saveMode == "new") {
			funcsel.lock(false);
			pkgname.lock(false);
			cnfield.lock(false);
		} else {
			funcsel.lock(true);
			pkgname.lock(true);
			cnfield.lock(true);
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
	 * 計算処理。
	 * @param element {jQuery} 計算イベントが発生した要素。
	 */
	onCalc(element) {
		this.setFormClassName();
		if (element != null) {
			if (element.attr(this.getIdAttribute()) == "functionSelect") {
				let funcname = element.val();
				let packageName = funcname.replace(/\//g, ".").substr(1) + ".dao";
				this.setFieldValue("tablePackageName", packageName);
			}
		}
		this.setFormFlag();
	}

	/**
	 * フォームの生成チェックボックスに応じて、各項目を制御します。
	 *
	 */
	setFormFlag() {
		let thisForm = this;
		if (!this.get("queryFormClassFlag").prop("checked")) {
			thisForm.get("queryFormClassName").val("");
			thisForm.get("queryFormClassName").prop("disabled", true);
			thisForm.get("queryFormClassOverwriteMode").prop("disabled", true);
		} else {
			thisForm.get("queryFormClassName").prop("disabled", false);
			thisForm.setFormClassNameField("queryFormClassName", "QueryForm");
			thisForm.get("queryFormClassOverwriteMode").prop("disabled", false);
		}
		if (!this.get("queryResultFormClassFlag").prop("checked")) {
			thisForm.get("queryResultFormClassName").val("");
			thisForm.get("queryResultFormClassName").prop("disabled", true);
			thisForm.get("queryResultFormClassOverwriteMode").prop("disabled", true);
		} else {
			thisForm.get("queryResultFormClassName").prop("disabled", false);
			thisForm.setFormClassNameField("queryResultFormClassName", "QueryResultForm");
			thisForm.get("queryResultFormClassOverwriteMode").prop("disabled", false);
		}
		if (!this.get("editFormClassFlag").prop("checked")) {
			thisForm.get("editFormClassName").val("");
			thisForm.get("editFormClassName").prop("disabled", true);
			thisForm.get("editFormClassOverwriteMode").prop("disabled", true);
		} else {
			thisForm.get("editFormClassName").prop("disabled", false);
			thisForm.setFormClassNameField("editFormClassName", "EditForm");
			thisForm.get("editFormClassOverwriteMode").prop("disabled", false);
		}
	}

	/**
	 * ページクラス名からフォームクラス名を設定します。
	 * @param name フォームクラス名ID。
	 * @param type フォームの種類。
	 */
	setFormClassNameField(name, type) {
		let pageclass = this.getFieldValue("pageClassName");
		let n = pageclass.replace(/Page$/, "");
		let f = this.get(name);
		if (f.val() == "") {
			if (!f.prop("disabled")) {
				f.val(n + type);
			}
		}
	}

	/**
	 * Pageクラス名の変更時に呼び出され、各種フォームクラスの名称を設定します。
	 *
	 */
	setFormClassName() {
		let pageclass = this.getFieldValue("pageClassName");
		if (pageclass.length > 0) {
			this.setFormClassNameField("queryFormClassName", "QueryForm");
			this.setFormClassNameField("queryResultFormClassName", "QueryResultForm");
			this.setFormClassNameField("editFormClassName", "EditForm");
		} else {
			this.setFieldValue("queryFormClassName", "");
			this.setFieldValue("queryResultFormClassName", "");
			this.setFieldValue("editFormClassName", "");
		}

	}

	/**
	 * 各フィールドのバリデーションを行います。
	 */
	validateFields() {
		let ret = super.validateFields();
		if (this.find("[name='updateTable']:checked").val() == "1") {
			let v = new RequiredValidator();
			v.messageKey = "error.required";
			{
				let r = this.getComponent("tablePackageName").performValidator(v);
				if (r != null) {
					ret.push(r);
				}
			}
			{
				let r = this.getComponent("tableClassName").performValidator(v);
				if (r != null) {
					ret.push(r);
				}
			}
			{
				let r = this.getComponent("daoClassName").performValidator(v);
				if (r != null) {
					ret.push(r);
				}
			}
		}
		return ret;
	}

	/**
	 * フォームデータを設定します。
	 * @param {Object} data フォームデータ。
	 */
	setFormData(data) {
		super.setFormData(data);
		let daoFunctionSelect = this.getComponent("daoFunctionSelect");
		daoFunctionSelect.selectPackage(this.getComponent("daoPackageName").getValue());
	}
}

