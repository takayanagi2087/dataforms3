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
	 * 最終ログイン情報のローカルストレージキー。
	 */
	static get LAST_LOGIN_INFO() {
		return "lastLoginInfo";
	}
	
	/**
	 * パスワード認証を行います。
	 */
	async passwordAuth() {
		try {
			let result = await this.submit("login");
			this.parent.resetErrorStatus();
			if (result.status == JsonResponse.SUCCESS) {
				if (this.parent instanceof Dialog) {
					this.parent.close();
				}
				if (result.result == "onetime") {
					window.location.href = "OnetimePasswordPage.html";
				} else {
					currentPage.toTopPage();
				}
			} else {
				this.parent.setErrorInfo(this.getValidationResult(result), this);
			}
		} catch (e) {
			currentPage.reportError(e);
		}
	}

	/**
	 * パスキー認証を行います。
	 */
	async passKeyAuth() {
		try {
			let r = await this.submit("getOption");
			this.parent.resetErrorStatus();
			if (r.status == JsonResponse.SUCCESS) {
				logger.log("r=", r);
				let opt = r.result;
				let util = new WebAuthnUtil();
				let resp = await util.get(opt);
				resp.loginId = this.get("loginId").val();
				resp.password = this.get("password").val();	
				resp.authenticatorName = this.get("authenticatorName").val();
				resp.keepLogin = this.get("keepLogin").prop("checked") ? "1" : "0";
				logger.log("resp=", resp);
				let m = this.getWebMethod("passKeyAuth");
				let res = await m.execute(resp);
				logger.log("r=", res);
				if (res.status == JsonResponse.SUCCESS) {
					currentPage.toTopPage();
				}
				this.saveLastLoginInfo();
			} else {
				logger.log("result", r);
				this.parent.setErrorInfo(this.getValidationResult(r), this);
			}
		} catch (e) {
			currentPage.reportError(e);
		}
	}

	/**
	 * ログインを行います。
	 */
	async login() {
		if (this.validate()) {
			let passkey = this.get("authenticatorName").val();
			logger.log("passkey:", passkey);
			if (passkey != null && passkey.length > 0) {
				this.passKeyAuth();
			} else {
				this.passwordAuth();
			}
		}
	}


	/**
	 * 最後のログイン情報を保存します。
	 */
	saveLastLoginInfo() {
		if (this.get("saveLastLogin").prop("checked")) {
			let loginId = this.get("loginId").val();
			let passkey = this.get("authenticatorName").val();
			let passkeyName = this.get("authenticatorName").find("option:selected").text();
			let lastLoginInfo = {};
			lastLoginInfo.loginId = loginId;
			lastLoginInfo.passkey = passkey;
			lastLoginInfo.passkeyName = passkeyName;
			let json = JSON.stringify(lastLoginInfo);
			localStorage.setItem(LoginForm.LAST_LOGIN_INFO, json);
		} else {
			localStorage.removeItem(LoginForm.LAST_LOGIN_INFO);
		}
	}

	/**
	 * 最後のログイン情報を取得します。
	 */
	loadLastLoginInfo() {
		try {
			let lastLoginInfo = localStorage.getItem(LoginForm.LAST_LOGIN_INFO);
			if (lastLoginInfo != null) {
				let json = localStorage.getItem(LoginForm.LAST_LOGIN_INFO);
				let lastLoginInfo = JSON.parse(json);
				this.get("loginId").val(lastLoginInfo.loginId);
				let optlist = [
					{"value": lastLoginInfo.passkey, "name": lastLoginInfo.passkeyName}
				];
				let sel = this.getComponent("authenticatorName");
				sel.setOptionList(optlist);
				sel.setValue(lastLoginInfo.passkey);
				this.get("saveLastLogin").prop("checked", true);
			} else {
				this.get("saveLastLogin").prop("checked", false);
			}
		} catch (e) {
			logger.error("error:", e);
			let sel = this.getComponent("authenticatorName");
			sel.setOptionList([]);
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
	 * 認証オプションを取得する。
	 */
	async getAuthOption() {
		logger.log("getAuthOption");
		let opt = await this.submit("getAuthOption");
		if (r.status == JsonResponse.SUCCESS) {
			logger.log("opt=", opt);
			this.getPasskeyList();
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
		this.get("loginId").change(() => {
			this.getAuthOption();
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
		this.loadLastLoginInfo();
	}

}

