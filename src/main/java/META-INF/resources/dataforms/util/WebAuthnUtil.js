/**
 * WebAuthn生体認証ユーティリティクラス。
 * 
 */

import { Base64Util } from './Base64Util.js';

/**
 * WebAuthn API呼び出しユーティリティクラス。
 */
export class WebAuthnUtil {
	/**
	 * 文字列をArrayBufferに変換します。
	 * @param {String} src 変換元文字列。
	 * @return {ArrayBuffer} 変換結果。
	 */	
	stringToArrayBuffer(src) {
		return (new Uint8Array([].map.call(src, function(c) {
			return c.charCodeAt(0)
		}))).buffer;
	}

	/**
	 * 資格情報を取得します。
	 * @param {Object} 鯖からのOption情報。
	 * @return {Object} 資格情報。
	 */
	async create(opt) {
		logger.log("create opt=", opt);
		const optionsFromServer = {
			"challenge": this.stringToArrayBuffer(opt.challenge), // ArrayBufferに変換
			"rp": {
				"id": opt.serverName,
				"name": opt.rpName
			},
			"user": {                      // ユーザー情報
				"id": this.stringToArrayBuffer(opt.name),
				"name": opt.name,
				"displayName": opt.displayName
			},
			"pubKeyCredParams": [
				{
					"type": "public-key",
					"alg": -8
				},
				{
					"type": "public-key",
					"alg": -7
				},
				{
					"type": "public-key",
					"alg": -257
				},
			],
			"authenticatorSelection": {
			    authenticatorAttachment: "platform", // platform:端末に組み込まれている認証器（FaceID、生体認証など）のみを指定。cross-platform:USBやNFCなどを含めた外部端末の認証器（Yubikeyなど）のみを指定。 
			    requireResidentKey: opt.requireResidentKey, // 認証器内にユーザー情報を登録するオプション。Discoverable Credentialにするかどうか。
//			    userVerification: "required" // 認証器によるローカル認証（生体認証、PINなど）の必要性を指定。 required:ローカル認証を必須。preferred:可能な限りローカル認証。discouraged:ローカル認証を許可しない（所有物認証）
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
		resp.rawId =  Base64Util.arrayBufferToBase64url(credential.rawId);
		resp.authenticatorAttachment = credential.authenticatorAttachment;
		resp.type = credential.type;
		resp.attestationObject =  Base64Util.arrayBufferToBase64url(credential.response.attestationObject);
		resp.clientDataJSON =  Base64Util.arrayBufferToBase64url(credential.response.clientDataJSON);
		return resp;
	}
	
	/**
	 * 生体認証を行いその結果を取得します。
	 * @param {Object} opt サーバから送信されたオプション。
	 * @return {PublicKeyCredential} 公開キー認証情報。
	 */
	async get(opt) {
		let id = Base64Util.base64urlToArrayBuffer(opt.id);
		logger.log("id=", id);
		const credentialRequestOptions = {
			'challenge': this.stringToArrayBuffer(opt.challenge),
			'allowCredentials': [{
				'type': "public-key",
				'id': id,
/*				"transports":[
					"usb",
					"nfc",
					"ble"
				]*/
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
		return resp;		
	}
}