/**
 * @fileOverview {@link OnetimePasswordForm}クラスを記述したファイルです。
 */

'use strict';

/**
 * @class OnetimePasswordForm
 *
 * @extends Form
 */
class OnetimePasswordForm extends Form {
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
		this.get("loginButton").click(() => {
			this.login();
			return false;
		});
		this.get("sendButton").click(() => {
			this.send();
			return false;
		});
	}

	/**
	 * ログインを行います。
	 */
	async login() {
		try {
			if (this.validate()) {
				let r = await this.submit("login");
				this.parent.resetErrorStatus();
				if (r.status == JsonResponse.SUCCESS) {
					currentPage.toTopPage();
				} else {
					this.parent.setErrorInfo(this.getValidationResult(r), this);
				}
			}
		} catch (e) {
			currentPage.reportError(e);
		}
	}

	/**
	 * パスワード再送信。
	 */
	async send() {
		try {
			let r =await this.submit("sendOnetimePassword");
			if (r.status == JsonResponse.SUCCESS) {
				currentPage.alert(null, r.result);
			}
		} catch (e) {
			currentPage.reportError(e);
		}
	}
}

