/**
 * @fileOverview {@link MfaForm}クラスを記述したファイルです。
 */

'use strict';

import { WebAuthnUtil } from '../../../util/WebAuthnUtil.js';
import { Form } from '../../../controller/Form.js';
import { JsonResponse } from '../../../response/JsonResponse.js';
import { MessagesUtil } from '../../../util/MessagesUtil.js';

/**
 * @class MfaForm
 *
 * @extends Form
 */
export class MfaForm extends Form {
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
		
		this.get("registButton").click(() => {
			this.regist();
			return false;
		});
		if (currentPage.getPlatform() == "android") {
			this.find("div.croudShare").show();
			this.find("div.passkey").css("grid-template-columns", "1fr 1fr");
		} else {
			this.find("div.croudShare").hide();
			this.find("div.passkey").css("grid-template-columns", "");
		}
		
		this.get("passkeyDescButton").click(() => {
			this.get("passkeyDesc").toggle();
		});
		// TOTP関連のボタン
		this.get("totpButton").click(() => {
			this.generateTotpQr();
		});
		this.get("removeTotpButton").click(() => {
			this.removeTotpQr();
		});
		// リカバリーコード関連のボタン
		this.get("createRecoveryCodesButton").click(() => {
			this.createRecoveryCode();
		});
		this.get("removeRecoveryCodesButton").click(() => {
			this.removeRecoveryCode();
		});
/*		this.get("downloadRecoveryCodesButton").click(() => {
			this.downloadRecoveryCode();
		});*/
	}

	/**
	 * リカバリーコード操作ボタンの状態設定。
	 */
	setRecoveryCodeButton() {
		let list = this.getComponent("recoveryCodeList");
		if (list.getRowCount() > 0) {
			this.get("removeRecoveryCodesButton").prop("disabled", false);
			this.get("downloadRecoveryCodesButton").prop("disabled", false);
		} else {
			this.get("removeRecoveryCodesButton").prop("disabled", true);
			this.get("downloadRecoveryCodesButton").prop("disabled", true);
		}
	}
	
	/**
	 * リカバリコードのダウンロードを行います。
	 */
	async downloadRecoveryCode() {
		try {
			await this.submit("downloadRecoveryCode");
		} catch (e) {
			currentPage.reportError(e);
		}
	}
	
	/**
	 * リカバリーコードを生成します。
	 */
	async createRecoveryCode() {
		try {
			let list = this.getComponent("recoveryCodeList");
			let flg = true;
			if (list.getRowCount() > 0) {
				flg = await currentPage.confirm(null, MessagesUtil.getMessage("message.regeneraterecoverycode"));
			}
			if (flg) {
				let r = await this.submit("createRecoveryCode");
				logger.log("r=", r);
				if (r.status == JsonResponse.SUCCESS) {
					let table = this.getComponent("recoveryCodeList");
					table.setTableData(r.result);
					await this.downloadRecoveryCode();
					let fname = location.hostname + "_recovery_codes*.txt";
					let msg = MessagesUtil.getMessage("message.downlodedrcoverycode", fname);
					currentPage.alert(null, msg);
				}
			}
			this.setRecoveryCodeButton();
		} catch (e) {
			currentPage.reportError(e);
		}
	}
	
	/**
	 * リカバリーコードを削除します。
	 */
	async removeRecoveryCode() {
		try {
			let flg = await currentPage.confirm(null, MessagesUtil.getMessage("message.removerecoverycode"));
			if (flg) {
				let r = await this.submit("removeRecoveryCode");
				logger.log("r=", r);
				if (r.status == JsonResponse.SUCCESS) {
					let table = this.getComponent("recoveryCodeList");
					table.setTableData(r.result);
				}
				this.setRecoveryCodeButton();
			}
		} catch (e) {
			currentPage.reportError(e);
		}
	}
	
	
	/**
	 * 多要素認証設定情報を取得します。
	 */
	async getMfaInfo() {
		try {
			let r = await this.submit("getMfaInfo");
			logger.log("r=", r);
			if (r.status == JsonResponse.SUCCESS) {
				let alist = this.getComponent("authenticatorList");
				alist.setTableData(r.result.authenticatorList);
				this.find("[id$='\.deleteButton']").click((ev) => {
					this.deleteAuthenticator(ev);
				});
				if (r.result.totpQrImage.length > 0) {
					this.get("totpQr").attr("src", r.result.totpQrImage);
				} else {
					this.get("totpQr").attr("src", null);
				}
				let rclist = this.getComponent("recoveryCodeList");
				rclist.setTableData(r.result.recoveryCodeList);				
			}
			this.setRecoveryCodeButton();
		} catch (e) {
			currentPage.reportError(e);
		}
	}
	
	/**
	 * TOTP QRコードの確認処理。
	 * @return {Boolean} 確認を行う場合true。
	 */
	async conformTotpQrUpdate() {
		let totpQr = this.get("totpQr").attr("src");
		if (totpQr == null) {
			return true;
		} else {
			return await currentPage.confirm(null, MessagesUtil.getMessage("message.updatetotp"));
		}
	}
	
	/**
	 * TOTP QRコードの削除。
	 */
	async removeTotpQr() {
		try {
			let c = await currentPage.confirm(null, MessagesUtil.getMessage("message.removeTotp"));
			if (c) {
				let r = await this.submit("removeTotpQr");
				logger.log("r=", r);
				if (r.status == JsonResponse.SUCCESS) {
					this.get("totpQr").remove();
					this.get("totpQrDiv").append("<img id='totpQ' width='128' height='128'/>")
				}
			}
		} catch (e) {
			currentPage.reportError(e);
		}
	}

	
	/**
	 * TOTP QRコードの生成。
	 */
	async generateTotpQr() {
		try {
			let c = await this.conformTotpQrUpdate();
			if (c) {
				let r = await this.submit("generateTotpQr");
				logger.log("r=", r);
				if (r.status == JsonResponse.SUCCESS) {
					this.get("totpQr").attr("src", r.result);
				}
			}
		} catch (e) {
			currentPage.reportError(e);
		}
	}
	
	
	/**
	 * 認証機の名前をチェックします。
	 */
	async #checkAuthenticatorName() {
		let ret = true;
		let dup = false;
		let an = this.get("authenticatorName").val();
		let table = this.getComponent("authenticatorList");
		let list = table.getTableData();
		for (let i = 0; i < list.length; i++) {
			if (list[i].authenticatorName == an) {
				dup = true;
				break;
			} 
		}
		if (dup) {
			ret = await currentPage.confirm(null, MessagesUtil.getMessage("message.duplicateauthenticator"));
		}
		return ret;
	}	

	/**
	 * 認証機器削除を行います。
	 * @param {Event} ev 削除ボタンイベント。
	 */	
	async deleteAuthenticator(ev) {
		try {
			let btn = $(ev.currentTarget);
			let table = this.getComponent("authenticatorList");
			let an = table.getSameRowField(btn, "authenticatorName").text();
			logger.log("an=" + an);
			if (await currentPage.confirm(null, MessagesUtil.getMessage("message.deleteauthenticator", an))) {
				let m = this.getWebMethod("deleteAuthenticator");
				let r = await m.execute("authenticatorName=" + an);
				if (r.status == JsonResponse.SUCCESS) {
					logger.log("r=", r);
					table.setTableData(r.result);
					this.find("[id$='\.deleteButton']").click((ev) => {
						this.deleteAuthenticator(ev);
					});
				}
			}
		} catch (e) {
			currentPage.reportError(e);
		}
	}
	
	/**
	 * 各フィールドにデータを設定します。
	 * <pre>
	 * 新規モードの場合、削除ボタンを隠します。
	 * </pre>
	 * @param {Object} data フォームデータ.
	 *
	 */
	setFormData(data) {
		logger.log("formData=", data);
		super.setFormData(data);
		this.find("[id$='\.deleteButton']").click((ev) => {
			this.deleteAuthenticator(ev);
		});
	}
	
	/**
	 * 認証機器登録を行います。
	 */
	async regist() {
		try {
			if (this.validate()) {
				if (await this.#checkAuthenticatorName()) {
					let opt = await this.submit("getOption");
					if (opt.status == JsonResponse.SUCCESS) {
						logger.log("opt=", opt);
						opt.result.requireResidentKey = this.get("requireResidentKey").prop("checked");
						let util = new WebAuthnUtil();
						let resp = await util.create(opt.result);
						resp.authenticatorName = this.get("authenticatorName").val();
						resp.platform = currentPage.getPlatform();
						let m = this.getWebMethod("registAuthenticator");
						let r = await m.execute(resp);
						logger.log("r=", r);
						if (r.status == JsonResponse.SUCCESS) {
							logger.log("r=", r);
							let table = this.getComponent("authenticatorList");
							table.setTableData(r.result);
							this.find("[id$='\.deleteButton']").click((ev) => {
								this.deleteAuthenticator(ev);
							});
						}
					}
				}
			}
		} catch (e) {
			currentPage.reportError(e);
		}
		
	}
}

