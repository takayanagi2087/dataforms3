/**
 * @fileOverview {@link EnumQueryForm}クラスを記述したファイルです。
 */

'use strict';

/**
 * @class EnumQueryForm
 *
 * @extends QueryForm
 */
export class EnumQueryForm extends QueryForm {
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
		if (currentPage.userInfo.userLevel == "developer") {
			this.get("exportInitDataButton").click(() => {
				this.exportInitData()
			});
			this.get("importDataButton").click(() => {
				this.importInitData();
			});
		} else {
			this.get("exportInitDataButton").remove();
			this.get("importDataButton").remove();
		}
	}

	/**
	 * データのエクスポートを行います。
	 */
	async exportInitData() {
		try {
			let ret = await currentPage.confirm(null, MessagesUtil.getMessage("message.exportAsInitialDataConfirm"));
			if (ret) {
				let data = await this.submit("export");
				currentPage.alert(null, data.result);
			}
		} catch (e) {
			currentPage.reportError(e);
		}
	}

	/**
	 * データのインポートを行います。
	 */
	async importInitData() {
		try {
			let ret = await currentPage.confirm(null, MessagesUtil.getMessage("message.importInitialDataConfirm"));
			if (ret) {
				let data = await this.submit("importData");
				await currentPage.alert(null, data.result);
			}
		} catch (e) {
			currentPage.reportError(e);
		}
	}
}

