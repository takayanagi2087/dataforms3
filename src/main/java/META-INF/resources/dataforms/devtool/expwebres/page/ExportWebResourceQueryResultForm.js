/**
 * @fileOverview {@link ExportWebResourceQueryResultForm}クラスを記述したファイルです。
 */

'use strict';

/**
 * @class ExportWebResourceQueryResultForm
 *
 * @extends QueryResultForm
 */
class ExportWebResourceQueryResultForm extends QueryResultForm {
	/**
	 * HTMLエレメントとの対応付けを行います。
	 */
	attach() {
		super.attach();
		this.get("selAll").click((ev) => {
			logger.log("selAll=" + $(ev.currentTarget).prop("checked"));
			this.selAll($(ev.currentTarget));
		});
		this.get("exportButton").click((ev) => {
			this.exportWebRes();
		});
		this.get("selectNotExportedButton").click((ev) => {
			this.selectedNotExportedFile();
		});
	}

	/**
	 * 各フィールドのバリデーションを行います。
	 * @returns バリデーション結果。
	 */
	validateFields() {
		let ret = super.validateFields();
		if (ret.length == 0) {
			if (this.get("forceOverwrite").prop("checked") == false) {
				let result = this.formData.queryResult;
				for (let i = 0; i < result.length; i++) {
					let selid = "queryResult[" + i + "].sel";
					if (this.get(selid).prop("checked")) {
						let efid = "queryResult[" + i + "].existFlag";
						if (this.getFieldValue(efid) == "1") {
							ret.push(new ValidationError("queryResult[" + i + "].fileName",
								MessagesUtil.getMessage("error.alreadyexported", result[i].fileName)));
						}
					}
				}
			}
		}
		return ret;
	}

	/**
	 * エクスポートされていないファイルを選択します。
	 */
	selectedNotExportedFile() {
		let result = this.formData.queryResult;
		for (let i = 0; i < result.length; i++) {
			let selid = "queryResult[" + i + "].sel";
			let efid = "queryResult[" + i + "].existFlag";
			if (this.getFieldValue(efid) != "1") {
				this.get(selid).prop("checked", true);
			}
		}
	}

	/**
	 * Webリソースをエクスポートします。
	 */
	async exportWebRes() {
		try {
			this.parent.resetErrorStatus();
			if (this.validate()) {
				let p = this.get().serialize();
				logger.log("p=" + p);
				let r = await this.submit("exportWebResource");
				if (r != null) {
					if (r.status == JsonResponse.SUCCESS) {
						let systemName = MessagesUtil.getMessage("message.systemname");
						currentPage.alert(systemName, r.result);
					}
				}
			}
		} catch (e) {
			currentPage.reportError(e);
		}
	}

	/**
	 * 全選択チェックボックスのイベント処理を行います。
	 * @param ck {jQuery} チェックボックスのjQueryオブジェクト。
	 *
	 */
	selAll(ck) {
		if (ck.prop("checked")) {
			this.find("[id$='.sel']").prop("checked", true);
		} else {
			this.find("[id$='.sel']").prop("checked", false);
		}
	}
}


