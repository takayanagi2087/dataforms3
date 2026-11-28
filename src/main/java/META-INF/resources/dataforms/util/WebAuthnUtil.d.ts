/**
 * WebAuthn生体認証ユーティリティクラス。
 */
interface CreateOptionsInput {
    challenge: string;
    serverName: string;
    rpName: string;
    name: string;
    displayName: string;
    requireResidentKey?: boolean;
}
interface CreateResult {
    id: string;
    rawId: string;
    authenticatorAttachment: string | null;
    type: string;
    attestationObject: string;
    clientDataJSON: string;
}
interface GetOptionsInput {
    challenge: string;
    id: string;
}
interface GetResult {
    id: string;
    rawId: string;
    authenticatorAttachment: string | null;
    type: string;
    authenticatorData: string;
    clientDataJSON: string;
    signature: string;
    userHandle: string | null;
}
/**
 * WebAuthn API呼び出しユーティリティクラス。
 */
export declare class WebAuthnUtil {
    /**
     * 文字列をArrayBufferに変換します。
     * @param src 変換元文字列。
     * @return 変換結果。
     */
    stringToArrayBuffer(src: string): ArrayBuffer;
    /**
     * 資格情報を取得します。
     * @param opt 鯖からのOption情報。
     * @return 資格情報。
     */
    create(opt: CreateOptionsInput): Promise<CreateResult | null>;
    /**
     * 生体認証を行いその結果を取得します。
     * @param opt サーバから送信されたオプション。
     * @return 公開キー認証情報。
     */
    get(opt: GetOptionsInput): Promise<GetResult | null>;
}
export {};
