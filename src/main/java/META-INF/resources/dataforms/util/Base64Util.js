/**
 * Base64変換ユーティリティクラス。
 */
export class Base64Util {
	/**
	 * Base64の文字列をArrayBufferに展開します。
	 * @param {String}  base64 Base64の文字列。
	 * @return {ArrayBuffer} 変換結果。
	 */
	static base64ToArrayBuffer(base64) {
		const str = window.atob(base64);
		const len = str.length;
		const bytes = new Uint8Array(len);
		for (let i = 0; i < len; i++) {
			bytes[i] = str.charCodeAt(i);
		}
		return bytes.buffer;
	}

	/**
	 * ArrayBufferの内容をBase64文字列に変換します。
	 * @param {ArrayBuffer} ab ArrayBufferの内容。
	 * @return {String} Bas64文字列。
	 */
	static arrayBufferToBase64(ab) {
		const str = String.fromCharCode.apply(null, new Uint8Array(ab))
		return window.btoa(str);
	}

	/**
	 * Base64URLをArrayBuffer変換します。
	 * @param {String} base64url Base64URLの文字列。
	 * @return {ArrayBuffer} 変換結果。 
	 */
	static base64urlToArrayBuffer(base64url) {
		return Base64Util.base64ToArrayBuffer(Base64Util.base64urlToBase64(base64url));
	}

	/**
	 * ArrayBufferの内容をBase64URL文字列に変換します。
	 * @param {ArrayBuffer} ab ArrayBufferの内容。
	 * @return {String} Bas64URLの文字列。
	 */
	static arrayBufferToBase64url(ab) {
		const str = String.fromCharCode.apply(null, new Uint8Array(ab))
		return Base64Util.base64ToBase64url(window.btoa(str));
	}

	/**
	 * Base64の内容をBase64URL文字列に変換します。
	 * @param {String} base64 Base64の文字列。
	 * @return {String} Base64URLの文字列
	 */
	static base64ToBase64url(base64) {
		return base64.replace(/\+/g, '-').replace(/\//g, '_').replace(/=*$/g, '')
	}

	/**
	 * Base64URLの文字列をBase64に変換する。
	 * @param {String} base64url Base64urlの文字列。
	 * @return {String} Base64の文字列
	 */
	static base64urlToBase64(base64url) {
		let base64 = base64url.replace(/-/g, '+').replace(/_/g, '/');
		const padding = base64.length % 4;
		if (padding > 0) {
			return base64 + '===='.slice(padding);
		}
		return base64;
	}	
}