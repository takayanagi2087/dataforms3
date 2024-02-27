/**
 * @fileOverview {@link FuncEditForm}クラスを記述したファイルです。
 */

'use strict';

/**
 * @class FuncEditForm
 *
 * @extends EditForm
 */
class FuncEditForm extends EditForm {
	/**
	 * HTMLエレメントとの対応付けを行います。
	 */
	attach() {
		super.attach();
		this.get("exportInitDataButton").click(() => {
			this.exportInitData();
		});
		this.get("importDataButton").click(() => {
			this.importInitData();
		});
		this.get("importV1DataButton").click(() => {
			this.importV1InitData();
		});
	}

	/**
	 * データのエクスポートを行います。
	 */
	async exportInitData() {
		if (await currentPage.confirm(null, MessagesUtil.getMessage("message.exportAsInitialDataConfirm"))) {
			try {
				let data = await this.submit("export");
				await currentPage.alert(null, data.result);
			} catch (e) {
				currentPage.reportError(e);
			}
		}
	}

	/**
	 * データのインポートを行います。
	 */
	async importInitData() {
		let ret = await currentPage.confirm(null, MessagesUtil.getMessage("message.importInitialDataConfirm"));
		if (ret) {
			try {
				let data = await this.submit("importData");
				await currentPage.alert(null, data.result);
			} catch (e) {
				currentPage.reportError(e);
			}
		}
	}

	/**
	 * ver1.x形式のデータのインポートを行います。
	 */
	async importV1InitData() {
		let ret = await currentPage.confirm(null, MessagesUtil.getMessage("message.importV1InitialDataConfirm"));
		if (ret) {
			try {
				let data = await this.submit("importV1Data");
				await currentPage.alert(null, data.result);
			} catch (e) {
				currentPage.reportError(e);
			}
		}
	}

	/**
	 * 編集モードにします。
	 */
	toEditMode() {
		super.toEditMode();
		let table = this.getComponent("funcTable");
		this.find("[id$='\\.funcPath']").each((_, tx) => {
			let f = this.getComponent($(tx).attr(this.getIdAttribute()));
			logger.log("id=" + f.id + ":" + $(tx).val());
			if ($(tx).val().indexOf("/dataforms") == 0) {
				let field = table.getSameRowField($(tx), "funcName");
				let namef = this.getComponent(field.attr(this.getIdAttribute()));
				f.lock(true);
				namef.lock(true);
			}
		});
	}
}



