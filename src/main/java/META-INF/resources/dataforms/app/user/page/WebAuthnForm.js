/**
 * @fileOverview {@link WebAuthnForm}クラスを記述したファイルです。
 */

'use strict';

import { WebAuthnUtil } from '../../../util/WebAuthnUtil.js';
import { Form } from '../../../controller/Form.js';
import { JsonResponse } from '../../../response/JsonResponse.js';
import { MessagesUtil } from '../../../util/MessagesUtil.js';

/**
 * @class WebAuthnForm
 *
 * @extends Form
 */
export class WebAuthnForm extends Form {
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
						let util = new WebAuthnUtil();
						let resp = await util.create(opt.result);
						resp.authenticatorName = this.get("authenticatorName").val();
						let m = this.getWebMethod("registAuthenticator");
						let r = await m.execute(resp);
						logger.log("r=", r);
						if (r.status == JsonResponse.SUCCESS) {
							logger.log("r=", r);
							let table = this.getComponent("authenticatorList");
							table.setTableData(r.result);
						}
					}
				}
			}
		} catch (e) {
			currentPage.reportError(e);
		}
		
	}
}

