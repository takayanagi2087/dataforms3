/**
 * @fileOverview {@link StringUtil}クラスを記述したファイルです。
 */
/**
 * @class StringUtil
 * 文字列ユーティリティ。
 */
export declare class StringUtil {
    /**
     * 空白文字列判定を行います。
     * @param s 判定する文字列
     * @returns 空白文字の場合true
     */
    static isBlank(s: string | null | undefined): boolean;
    /**
     * 半角カナリスト。
     * @returns 半角カナ文字リスト
     */
    private static get HALF_KANA_LIST();
    /**
     * 全角カナリスト。
     * @returns 全角カナ文字リスト
     */
    private static get FULL_KANA_LIST();
    /**
     * 半角カナ→全角カナ変換を行います。
     * @param orgtext 変換対象文字列
     * @returns 変換結果
     */
    static halfToFullKana(orgtext: string): string;
    /**
     * 半角→全角変換を行います。
     * @param str 変換対象文字列
     * @returns 変換結果
     */
    static halfToFull(str: string): string;
    /**
     * 全角カナ→半角カナ変換を行います。
     * @param orgtext 変換対象文字列
     * @returns 変換結果
     */
    static fullToHalfKana(orgtext: string): string;
    /**
     * 全角→半角変換。
     * @param str 変換対象文字列
     * @returns 変換結果
     */
    static fullToHalf(str: string): string;
}
//# sourceMappingURL=StringUtil.d.ts.map