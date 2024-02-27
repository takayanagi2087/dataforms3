/**
 * @fileOverview {@link BackupForm}クラスを記述したファイルです。
 */

'use strict';

/**
 * @class BackupForm
 *
 * @extends Form
 */
class BackupForm extends Form {
	/**
	 * HTMLエレメントとの対応付けを行います。
	 */
	attach() {
		super.attach();
		this.get("backupButton").click(() => { this.backup(); return false;});
	}

	/**
	 * バックアップ処理。
	 */
	async backup() {
		try {
			this.parent.resetErrorStatus();
			let r = await this.submit("backup");
			this.parent.resetErrorStatus();
			if (r != null) {
				if (r.status == JsonResponse.INVALID) {
					currentPage.setErrorInfo(this.getValidationResult(r), this);
				} else {
					let systemname = MessagesUtil.getMessage("message.systemname");
					currentPage.alert(systemname, r.result);
				}
			}
		} catch (e) {
			currentPage.reportError(e);
		}
	}
}


