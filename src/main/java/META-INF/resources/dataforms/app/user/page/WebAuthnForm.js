/**
 * @fileOverview {@link WebAuthnForm}クラスを記述したファイルです。
 */

'use strict';

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
		const optionsFromServer = {
			"challenge": this.string2Buffer(opt.challenge), // ArrayBufferに変換
			"rp": {
				"id": opt.serverName,
				"name": opt.rpName
			},
			"user": {                      // ユーザー情報
				"id": this.string2Buffer(opt.name),
				"name": opt.name,
				"displayName": opt.displayName
			},
			"pubKeyCredParams": [
				{
					"type": "public-key",
					"alg": -7
				},
				{
					"type": "public-key",
					"alg": -257
				}
			],
			"authenticatorSelection": {
				authenticatorAttachment: "platform",
			},
			"timeout": 60000              // ms単位
			, attestation: 'direct' // 認証に関するオプション
		}
		logger.log("optionsFromServer=", optionsFromServer);
		const credential = await navigator.credentials.create({
			publicKey: optionsFromServer
		});
		logger.log("resp=\n", credential);
		logger.log("id=" + credential.id);
		
		// サーバ送信用のデータを作成。
		// バイナリはBase64に変換する。
		let resp = {};
		resp.id = credential.id;
		resp.rawId = this.buffer2Base64(credential.rawId);
		resp.authenticatorAttachment = credential.authenticatorAttachment;
		resp.type = credential.type;
		resp.attestationObject = this.buffer2Base64(credential.response.attestationObject);
		resp.clientDataJSON = this.buffer2Base64(credential.response.clientDataJSON);
		logger.log("resp=\n" + JSON.stringify(resp, null, "\t"));
		let m = this.getWebMethod("regist");
		let r = await m.execute(resp);
		logger.log("r=", r);
		
	}
}

