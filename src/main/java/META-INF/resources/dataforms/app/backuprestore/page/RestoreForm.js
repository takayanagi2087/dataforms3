/**
 * @fileOverview {@link RestoreForm}クラスを記述したファイルです。
 */

'use strict';

/**
 * @class RestoreForm
 *
 * @extends Form
 */
class RestoreForm extends Form {
	/**
	 * HTMLエレメントとの対応付けを行います。
	 */
	attach() {
		super.attach();
		this.get("restoreButton").click(() => { this.restore(); return false;	});
	}

	/**
	 * リストア処理を行います。
	 */
	async restore() {
		try {
			if (this.validate()) {
				let systemname = MessagesUtil.getMessage("message.systemname");
				let msg = MessagesUtil.getMessage("message.restoreconfirm");
				if (await currentPage.confirm(systemname, msg)) {
					let r = await this.submit("restore");
					this.parent.resetErrorStatus();
					if (r.status == JsonResponse.INVALID) {
						currentPage.setErrorInfo(this.getValidationResult(r), this);
					} else {
						currentPage.alert(systemname, r.result);
					}
				}
			}
		} catch (e) {
			currentPage.reportError(e);
		}
	}
}



