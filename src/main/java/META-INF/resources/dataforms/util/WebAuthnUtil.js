/**
 * WebAuthn生体認証ユーティリティクラス。
 */
import { Base64Util } from './Base64Util.js';
/**
 * WebAuthn API呼び出しユーティリティクラス。
 */
export class WebAuthnUtil {
    /**
     * 文字列をArrayBufferに変換します。
     * @param src 変換元文字列。
     * @return 変換結果。
     */
    stringToArrayBuffer(src) {
        const codes = Array.prototype.map.call(src, (c) => c.charCodeAt(0));
        return new Uint8Array(codes).buffer;
    }
    /**
     * 資格情報を取得します。
     * @param opt サーバーからのOption情報。
     * @return 資格情報。
     */
    async create(opt) {
        logger.log('create opt=', opt);
        const optionsFromServer = {
            challenge: this.stringToArrayBuffer(opt.challenge),
            rp: {
                id: opt.serverName,
                name: opt.rpName
            },
            user: {
                id: this.stringToArrayBuffer(opt.name),
                name: opt.name,
                displayName: opt.displayName
            },
            pubKeyCredParams: [
                { type: 'public-key', alg: -8 },
                { type: 'public-key', alg: -7 },
                { type: 'public-key', alg: -257 }
            ],
            authenticatorSelection: {
                authenticatorAttachment: 'platform',
                requireResidentKey: opt.requireResidentKey
            },
            timeout: 60000,
            attestation: 'direct'
        };
        logger.log('optionsFromServer=', optionsFromServer);
        const credential = await navigator.credentials.create({
            publicKey: optionsFromServer
        });
        if (credential == null) {
            return null;
        }
        if (!(credential instanceof PublicKeyCredential)) {
            throw new TypeError('Expected PublicKeyCredential');
        }
        const response = credential.response;
        logger.log('resp=\n', credential);
        logger.log('id=' + credential.id);
        return {
            id: credential.id,
            rawId: Base64Util.arrayBufferToBase64url(credential.rawId),
            authenticatorAttachment: credential.authenticatorAttachment,
            type: credential.type,
            attestationObject: Base64Util.arrayBufferToBase64url(response.attestationObject),
            clientDataJSON: Base64Util.arrayBufferToBase64url(response.clientDataJSON)
        };
    }
    /**
     * 生体認証を行いその結果を取得します。
     * @param opt サーバから送信されたオプション。
     * @return 公開キー認証情報。
     */
    async get(opt) {
        const id = Base64Util.base64urlToArrayBuffer(opt.id);
        logger.log('id=', id);
        const credentialRequestOptions = {
            challenge: this.stringToArrayBuffer(opt.challenge),
            allowCredentials: [
                {
                    type: 'public-key',
                    id
                }
            ]
        };
        const credential = await navigator.credentials.get({
            publicKey: credentialRequestOptions
        });
        if (credential == null) {
            return null;
        }
        if (!(credential instanceof PublicKeyCredential)) {
            throw new TypeError('Expected PublicKeyCredential');
        }
        const response = credential.response;
        logger.log('credential=', credential);
        const resp = {
            id: credential.id,
            rawId: Base64Util.arrayBufferToBase64url(credential.rawId),
            authenticatorAttachment: credential.authenticatorAttachment,
            type: credential.type,
            authenticatorData: Base64Util.arrayBufferToBase64url(response.authenticatorData),
            clientDataJSON: Base64Util.arrayBufferToBase64url(response.clientDataJSON),
            signature: Base64Util.arrayBufferToBase64url(response.signature),
            userHandle: response.userHandle
                ? Base64Util.arrayBufferToBase64url(response.userHandle)
                : null
        };
        logger.log('resp=', resp);
        return resp;
    }
}
