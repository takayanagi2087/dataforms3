/**
 * @fileOverview {@link PasswordRecenryptForm}クラスを記述したファイルです。
 */

'use strict';

/**
 * @class PasswordRecenryptForm
 *
 * @extends Form
 */
class PasswordRecenryptForm extends Form {
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
		this.get("reencryptButton").click(() => {
			this.reencrypt();
		});
	}

	/**
	 * フォームのバリデーション。
	 */
	validateForm() {
		let list = super.validateForm();
		if (list.length == 0) {
			let p = this.get("cryptPassword").val();
			let pc = this.get("cryptPasswordCheck").val();
			if (p != pc) {
				list.push(new ValidationError("cryptPasswordCheck", MessagesUtil.getMessage("error.passwordnotmatch")));
			}
		}
		return list;
	}

	/**
	 * 再暗号化処理。
	 */
	async reencrypt() {
		try {
			if (this.validate()) {
				this.parent.resetErrorStatus();
				if (await currentPage.confirm(null, MessagesUtil.getMessage("message.confirmreencrypted"))) {
					let r = await this.submit("reencrypt");
					this.parent.resetErrorStatus();
					if (r.status == JsonResponse.INVALID) {
						currentPage.setErrorInfo(this.getValidationResult(r), this);
					} else {
						currentPage.alert(null, r.result);
					}
				}
			}
		} catch (e) {
			currentPage.reportError(e);
		}
	}
}

