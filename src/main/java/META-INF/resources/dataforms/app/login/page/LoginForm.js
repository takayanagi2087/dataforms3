/**
 * @fileOverview {@link LoginForm}クラスを記述したファイルです。
 */

'use strict';


import { WebAuthnUtil } from '../../../util/WebAuthnUtil.js';
import { Form } from '../../../controller/Form.js';
import { Dialog } from '../../../controller/Dialog.js';
import { JsonResponse } from '../../../response/JsonResponse.js';

/**
 * @class LoginForm
 * ログインフォームクラス。
 * <pre>
 * ユーザIDとパスワードを入力しログイン処理を行います。
 * </pre>
 * @extends Form
 */
export class LoginForm extends Form {
	/**
	 * パスワード認証を行います。
	 */
	async passwordAuth() {
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

	/**
	 * パスキー認証を行います。
	 */
	async passKeyAuth() {
		let r = await this.submit("getOption");
		logger.log("r=", r);
		if (r.status == JsonResponse.SUCCESS) {
			let opt = r.result;
			let util = new WebAuthnUtil();
			let resp = await util.get(opt);
			resp.loginId = this.get("loginId").val();			
			let m = this.getWebMethod("passKeyAuth");
			let res = await m.execute(resp);
			logger.log("r=", res);
			if (res.status == JsonResponse.SUCCESS) {
				currentPage.toTopPage();
			}
		}
	}

	/**
	 * ログインを行います。
	 */
	async login() {
		try {
			if (this.validate()) {
				let passkey = this.get("authenticatorName").val();
				logger.log("passkey:", passkey);
				if (passkey != null && passkey.length > 0) {
					this.passKeyAuth();
				} else {
					this.passwordAuth();
				}
			}
		} catch (e) {
			currentPage.reportError(e);
		}

	}


	/**
	 * パスキーの一覧を取得します。
	 */
	async getPasskeyList() {
		try {
			if (this.validate()) {
				let loginId = this.get("loginId").val();
				logger.log("loginId=" + loginId);
				let sel = this.getComponent("authenticatorName");
				await sel.getPasskeyList(loginId);
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

		this.get("passkeyListButton").click(() => {
			this.getPasskeyList();
			return false;
		});
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

