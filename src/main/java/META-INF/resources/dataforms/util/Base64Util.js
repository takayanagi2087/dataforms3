/**
 * 
 */
export class Base64Util {
	static base64ToArrayBuffer(base64) {
		const str = window.atob(base64);
		const len = str.length;
		const bytes = new Uint8Array(len);
		for (let i = 0; i < len; i++) {
			bytes[i] = str.charCodeAt(i);
		}
		return bytes.buffer;
	}

	static arrayBufferToBase64(ab) {
		const str = String.fromCharCode.apply(null, new Uint8Array(ab))
		return window.btoa(str);
	}

	static base64urlToArrayBuffer(base64url) {
		return Base64Util.base64ToArrayBuffer(Base64Util.base64urlToBase64(base64url));
	}

	static arrayBufferToBase64url(ab) {
		const str = String.fromCharCode.apply(null, new Uint8Array(ab))
		return Base64Util.base64ToBase64url(window.btoa(str));
	}

	static base64ToBase64url(base64) {
		return base64.replace(/\+/g, '-').replace(/\//g, '_').replace(/=*$/g, '')
	}

	static base64urlToBase64(base64url) {
		let base64 = base64url.replace(/-/g, '+').replace(/_/g, '/');
		const padding = base64.length % 4;
		if (padding > 0) {
			return base64 + '===='.slice(padding);
		}
		return base64;
	}	
}