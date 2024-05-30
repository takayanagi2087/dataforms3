/**
 * @fileOverview {@link WebAuthnForm}クラスを記述したファイルです。
 */

'use strict';

import { WebAuthnUtil } from '../../../util/WebAuthnUtil.js';
import { Form } from '../../../controller/Form.js';

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
	
	string2Buffer(src) {
		return (new Uint8Array([].map.call(src, function(c) {
			return c.charCodeAt(0)
		}))).buffer;
	}

	
	buffer2Base64(ab) {
		const str = String.fromCharCode.apply(null, new Uint8Array(ab))
		return window.btoa(str);
	}
	
	
	/**
	 * 生体情報登録。
	 */
	async regist() {
		let opt = await this.submit("getOption");
		logger.log("opt=", opt);
		let util = new WebAuthnUtil();
		let resp = await util.create(opt);
		let m = this.getWebMethod("regist");
		let r = await m.execute(resp);
		logger.log("r=", r);
		
	}
}

