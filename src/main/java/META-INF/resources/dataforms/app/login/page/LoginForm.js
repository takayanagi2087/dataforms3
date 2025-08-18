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
	 * 認証オプション。
	 */
	#authOption = null;
	
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
					this.saveLastLoginInfo();
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
			let method = this.find("[name='authMethod']:checked").val();
			if (method == "2") {
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
			let authMethod = this.find("[name='authMethod']:checked").val();
			if (authMethod != "3") { // Recovery codeの場合は保存しない。
				let passkey = this.get("authenticatorName").val();
				let lastLoginInfo = {};
				lastLoginInfo.loginId = loginId;
				lastLoginInfo.authMethod = authMethod;
				lastLoginInfo.passkey = passkey;
				let json = JSON.stringify(lastLoginInfo);
				localStorage.setItem(LoginForm.LAST_LOGIN_INFO, json);
			}
		} else {
			localStorage.removeItem(LoginForm.LAST_LOGIN_INFO);
		}
	}

	/**
	 * 最後のログイン情報を取得します。
	 */
	async loadLastLoginInfo() {
		try {
			let json = localStorage.getItem(LoginForm.LAST_LOGIN_INFO);
			if (json != null) {
				let lastLoginInfo = JSON.parse(json);
				this.get("loginId").val(lastLoginInfo.loginId);
				await this.getAuthOption();
				let passkeySel = this.getComponent("authenticatorName");
				passkeySel.setValue(lastLoginInfo.passkey);
				let rm = this.getComponent("authMethod");
				rm.setValue(lastLoginInfo.authMethod);
				this.changeAuthMethod();
				this.get("saveLastLogin").prop("checked", true);
			} else {
				this.get("saveLastLogin").prop("checked", false);
				this.get("loginId").focus();
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
			let loginId = this.get("loginId").val();
			if (loginId.length > 0) {
				logger.log("loginId=" + loginId);
				let sel = this.getComponent("authenticatorName");
				await sel.getPasskeyList(loginId);
			}
		} catch (e) {
			currentPage.reportError(e);
		}
	}

	/**
	 * ラジオボタンの表示設定を行います。
	 * @param {Number} idx インデックス。
	 * @param {Boolean} visible 表示フラグ。
	 */
	setRadioVisible(idx, visible) {
		let id = this.selectorEscape("authMethod[" + idx + "]");
		if (!visible) {
			this.find("input[id='" + id  +"']").hide();
			this.find("label[for$='" + id +"']").hide();
		} else {
			this.find("input[id='" + id  +"']").show();
			this.find("label[for$='" + id +"']").show();
			this.find("input[id='" + id  +"']").prop("checked", true);
		}
	}
	
	/**
	 * 認証方法のラジオボタンの設定を行います。
	 * @param {Object} 認証オプション。
	 */
	setAuthMethodRadio(opt) {
		logger.log("opt=", opt);
		// MTA必須によって、パスワードのみの表示設定。
		if (opt.result.mfaRequired) {
			this.setRadioVisible(0, false);
		} else {
			this.setRadioVisible(0, true);
		}
		if (opt.result.useTotp) {
			this.setRadioVisible(1, true);
		} else {
			this.setRadioVisible(1, false);
		}
		if (this.find("#authenticatorName").find("option").length > 1) {
			this.setRadioVisible(2, true);
		} else {
			this.setRadioVisible(2, false);
		}
		this.setRadioVisible(3, false);
		if (opt.result.recoveryCode) {
			this.find(".recoveryCodeCheck").show();
		} else {
			this.find(".recoveryCodeCheck").hide();
		}
	}
		
	/** 
	 * 認証方法指定モードに移行。
	 * @param {Object} opt 選択できる認証オプション。
	 */
	toAuthMethodMode(opt) {
		this.get("methodDiv").show();
		this.get("passwordDiv").show();
		logger.log("passkeyCount=" + this.find("#authenticatorName").find("option").length);
		if (this.find("#authenticatorName").find("option").length > 1) {
			this.get("passkeyDiv").show();
		}
		if (opt.result.useTotp) {
			this.get("totpDiv").show();
		}
		this.get("flagDiv").show();
		this.get("loginButton").show();
		this.get("backButton").show();
		this.get("nextButton").hide();
		this.getComponent("loginId").lock(true);
		this.setAuthMethodRadio(opt);
		this.changeAuthMethod();
		
	}

	/**
	 * LoginIdの変更モードに移行する。
	 */
	toLoginIdMode() {
		this.get("methodDiv").hide();
		this.get("passwordDiv").hide();
		this.get("totpDiv").hide();
		this.get("passkeyDiv").hide();
		this.get("flagDiv").hide();
		this.get("loginButton").hide();
		this.get("backButton").hide();
		this.get("nextButton").show();
		this.getComponent("loginId").lock(false);
	}
	
	/**
	 * 認証オプションを取得する。
	 */
	async getAuthOption() {
		try {
			logger.log("getAuthOption loginId=" + this.get("loginId").val());
			let opt = await this.submit("getAuthOption");
			if (opt.status == JsonResponse.SUCCESS) {
				this.#authOption = opt;
				logger.log("opt=", opt);
				await this.getPasskeyList();
				this.toAuthMethodMode(opt);
				this.get("password").focus();
			}
		} catch (e) {
			currentPage.reportError(e);
		}
	}
	
	/**
	 * 認証メソッドの変更処理。
	 */
	changeAuthMethod() {
		let method = this.find("[name='authMethod']:checked").val();
		logger.log("authMethod=" + method);
		if (method == "0") {
			this.get("passwordDiv").show();
			this.get("totpDiv").hide();
			this.get("passkeyDiv").hide();
			this.get("recoveryCodeDiv").hide();
		} else if (method == "1") {
			this.get("passwordDiv").show();
			this.get("totpDiv").show();
			this.get("passkeyDiv").hide();
			this.get("recoveryCodeDiv").hide();
		} else if (method == "2") {
			this.get("passwordDiv").hide();
			this.get("totpDiv").hide();
			this.get("passkeyDiv").show();
			this.get("recoveryCodeDiv").hide();
		} else if (method == "3") {
			this.get("passwordDiv").show();
			this.get("totpDiv").hide();
			this.get("passkeyDiv").hide();
			this.get("recoveryCodeDiv").show();
		}
		let pf = this.getComponent("password");
		if (method == "0" || method == "1" || method == "3") {
			pf.setRequired(true);
		} else {
			pf.setRequired(false);
			if (this.#authOption.result.passwordRequired) {
				pf.setRequired(true);
			} else {
				pf.setRequired(false);
			}
		}
	}

	/**
	 * エンターキー押下時の動作。
	 */	
	onEnter() {
		if (this.get("nextButton").is(":visible")) {
			if (this.validateLoginId()) {
				this.getAuthOption();
			}
		} else {
			this.login();
		}
	}

	/**
	 * リカバリーコードモードを有効にする。
	 */
	enableRecoveryCode() {
		if (this.get("recoveryCodeCheck").prop("checked")) {
			this.setRadioVisible(3, true);
		} else {
			this.setAuthMethodRadio(this.#authOption);
		}
		this.changeAuthMethod();
	}

	/**
	 * LoginIdを確認します。
	 * @return 正常の場合true。
	 */
	validateLoginId() {
		currentPage.resetErrorStatus();
		let loginIdField = this.getComponent("loginId");
		let err = loginIdField.validate();
		logger.log("err=", err);
		if (err != null) {
			let errlist = [];
			errlist.push(err);
			currentPage.setErrorInfo(errlist, this);
			return false;
		}
		return true;
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
		this.find("[name='authMethod']").click(() => {
			this.changeAuthMethod();
		});
		this.get("nextButton").click(() => {
			if (this.validateLoginId()) {
				this.getAuthOption();
			}
			return false;
		});
		this.get().on("keydown", (ev) => {
			if(ev.key === "Enter") {
				this.onEnter();
			}
		});
		this.get("backButton").click(() => {
			this.toLoginIdMode();
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
		this.find(".recoveryCodeCheck").click(() => {
			this.enableRecoveryCode();
		});
		this.loadLastLoginInfo();
	}

}



