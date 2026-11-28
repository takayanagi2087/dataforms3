/**
 * Base64変換ユーティリティクラス。
 */
export declare class Base64Util {
    /**
     * Base64の文字列をArrayBufferに展開します。
     * @param base64 Base64の文字列。
     * @return 変換結果。
     */
    private static base64ToArrayBuffer;
    /**
     * ArrayBufferの内容をBase64文字列に変換します。
     * @param ab ArrayBufferの内容。
     * @return Bas64文字列。
     */
    static arrayBufferToBase64(ab: ArrayBuffer): string;
    /**
     * Base64URLをArrayBuffer変換します。
     * @param base64url Base64URLの文字列。
     * @return 変換結果。
     */
    static base64urlToArrayBuffer(base64url: string): ArrayBuffer;
    /**
     * ArrayBufferの内容をBase64URL文字列に変換します。
     * @param ab ArrayBufferの内容。
     * @return Bas64URLの文字列。
     */
    static arrayBufferToBase64url(ab: ArrayBuffer): string;
    /**
     * Base64の内容をBase64URL文字列に変換します。
     * @param base64 Base64の文字列。
     * @return Base64URLの文字列
     */
    private static base64ToBase64url;
    /**
     * Base64URLの文字列をBase64に変換する。
     * @param base64url Base64urlの文字列。
     * @return Base64の文字列
     */
    private static base64urlToBase64;
}
//# sourceMappingURL=Base64Util.d.ts.map