/**
 * @fileOverview {@link WebAuthnForm}クラスを記述したファイルです。
 */

'use strict';

import { WebAuthnUtil } from '../../../util/WebAuthnUtil.js';
import { Form } from '../../../controller/Form.js';
import { JsonResponse } from '../../../response/JsonResponse.js';

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
	 * 生体情報登録。
	 */
	async regist() {
		try {
			let opt = await this.submit("getOption");
			if (opt.status == JsonResponse.SUCCESS) {
				logger.log("opt=", opt);
				let util = new WebAuthnUtil();
				let resp = await util.create(opt.result);
				let m = this.getWebMethod("regist");
				let r = await m.execute(resp);
				logger.log("r=", r);
			}
		} catch (e) {
			currentPage.reportError(e);
		}
		
	}
}

