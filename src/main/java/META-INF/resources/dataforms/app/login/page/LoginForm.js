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

	setPassKeyList(plist) {
		let html = "";
		if (plist.length == 1) {
			html += "<input type='hidden' id='authenticatorName' name='authenticatorName' value='" + plist[0] + "' />";
			this.get("passKeyList").hide();
		} else {
			this.get("passKeyList").show();
			html += "以下のPassKeyが登録されています。使用するPassKeyを選択してください。<br/>";
			html += "<input type='hidden' name='authenticatorName' value='' />";
			for (let i = 0; i < plist.length; i++) {
				html += "<input type='button' class='authenticatorName' value='" + plist[i] +"' /> &nbsp;&nbsp;"
			}
		}
		logger.log("html=" + html);
		this.get("passKeyList").html(html);
	}
	
	/**
	 * WebAuthn対応のログイン。
	 */
	async passKey() {
		try {
			let passKeyList = await this.submit("getPassKeyList");
			if (passKeyList.status == JsonResponse.SUCCESS) {
				let plist = passKeyList.result;
				this.setPassKeyList(plist);
				if (plist.length == 0) {
					currentPage.alert(null, "パスキーが登録されていません。");
				} else if (plist.length == 1) {
					await this.passKeyAuth();
				} else {
					this.find("input.authenticatorName").click(async (ev) => {
						let bname = $(ev.currentTarget).val();
						logger.log("bname=" + bname);
						this.find("[name='authenticatorName']").val(bname);
						await this.passKeyAuth();
					});
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
		this.get("passKeyButton").click(() => {
			this.passKey();
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

