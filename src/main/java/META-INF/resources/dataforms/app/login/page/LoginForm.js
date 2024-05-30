/**
 * @fileOverview {@link LoginForm}クラスを記述したファイルです。
 */

'use strict';


import { Base64Util } from '../../../util/Base64Util.js';
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

	string2Buffer(src) {
		return (new Uint8Array([].map.call(src, function(c) {
			return c.charCodeAt(0)
		}))).buffer;
	}


	/**
	 * WebAuthn対応のログイン。
	 */
	async webAuthn() {
		let r = await this.submit("getOption");
		logger.log("r=", r);
		if (r.status == JsonResponse.SUCCESS) {
			let opt = r.result;
			let id = Base64Util.base64urlToArrayBuffer(opt.id);
			logger.log("id=", id);
			const credentialRequestOptions = {
				'challenge': this.string2Buffer(opt.challenge),
				'allowCredentials': [{ // これ以外にtransports(usb, bluetooth経由などの指定)も指定できる
					'type': "public-key",
					'id': id
				}]
			}
			let credential = await navigator.credentials.get({
			    publicKey: credentialRequestOptions
  			});
			logger.log("credential=", credential);
			let resp = {};
			resp.id = credential.id;
			resp.rawId = Base64Util.arrayBufferToBase64url(credential.rawId);
			resp.authenticatorAttachment = credential.authenticatorAttachment;
			resp.type = credential.type;

			resp.authenticatorData = Base64Util.arrayBufferToBase64url(credential.response.authenticatorData);
			resp.clientDataJSON = Base64Util.arrayBufferToBase64url(credential.response.clientDataJSON);
			resp.signature = Base64Util.arrayBufferToBase64url(credential.response.signature);
			resp.userHandle = Base64Util.arrayBufferToBase64url(credential.response.userHandle);
			logger.log("resp=", resp);
			let m = this.getWebMethod("webAuthn");
			let res = await m.execute(resp);
			logger.log("r=", res);

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
		this.get("webAuthnButton").click(() => {
			this.webAuthn();
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

