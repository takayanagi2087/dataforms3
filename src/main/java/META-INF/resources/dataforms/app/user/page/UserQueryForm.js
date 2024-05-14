/**
 * @fileOverview {@link UserQueryForm}クラスを記述したファイルです。
 */

import { MessagesUtil } from '../../../util/MessagesUtil.js';
import { QueryForm } from '../../../controller/QueryForm.js';

/**
 * @class UserQueryForm
 *
 * @extends QueryForm
 */
export class UserQueryForm extends QueryForm {
	/**
	 * HTMLエレメントとの対応付けを行います。
	 */
	attach() {
		super.attach();
		if (currentPage.userInfo.userLevel == "developer") {
			this.get("exportInitDataButton").click(() => {
				this.exportInitData();
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
				await currentPage.alert(null, data.result);
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



