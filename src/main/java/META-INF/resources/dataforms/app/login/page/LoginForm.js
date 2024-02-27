/**
 * @fileOverview {@link LoginForm}クラスを記述したファイルです。
 */

'use strict';

/**
 * @class LoginForm
 * ログインフォームクラス。
 * <pre>
 * ユーザIDとパスワードを入力しログイン処理を行います。
 * </pre>
 * @extends Form
 */
class LoginForm extends Form {
	/**
	 * ログイン処理を行います。
	 *
	 */
	async login() {
		try {
			if (this.validate()) {
				let result = await this.submit("login");
				this.parent.resetErrorStatus();
				if (result.status == JsonResponse.SUCCESS) {
					if (this.parent instanceof Dialog) {
						this.parent.close();
					}
					if (result.result == "onetime") {
						window.location.href = "OnetimePasswordPage.df";
					} else {
						currentPage.toTopPage();
					}
				} else {
					this.parent.setErrorInfo(this.getValidationResult(result), this);
				}
			}
		} catch (e) {
			currentPage.reportError(e);
		}
	}

	/**
	 * HTMLエレメントとの対応付けを行います。
	 * <pre>
	 * 以下のイベント処理を登録します。
	 * #loginButton ... ログイン処理。
	 * </pre>
	 */
	attach() {
		super.attach();
		this.get("loginButton").click(() => {
			this.login();
			return false;
		});
		if (this.passwordResetMailPage != null) {
			$(this.convertSelector("#passwordResetLink")).attr("href", currentPage.contextPath + this.passwordResetMailPage);
		} else {
			$(this.convertSelector("#passwordResetLink")).hide();
		}
		if (this.autoLogin) {
			this.get("keepLogin").show();
			this.find("label[for='" + this.get("keepLogin").attr("id") + "']").show();
		} else {
			this.get("keepLogin").hide();
			this.find("label[for='" + this.get("keepLogin").attr("id") + "']").hide();
		}
	}

}

