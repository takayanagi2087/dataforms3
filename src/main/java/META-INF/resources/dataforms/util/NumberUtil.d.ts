/**
 * @fileOverview {@link NumberUtil}クラスを記述したファイルです。
 */
type NumericString = string | number;
/**
 * @class NumberUtil
 *
 * 数値整形ユーティリティ。
 */
export declare class NumberUtil {
    /**
     * 3桁ごとに','を付けます。
     * @param v 数値文字列
     * @returns 整形後の文字列
     */
    static addComma(v: NumericString): string;
    /**
     * ','を削除します。
     * @param v 数値文字列
     * @returns 整形後の文字列
     */
    static delComma(v: NumericString): string;
    /**
     * 数値文字列を数値に変換します。
     * 数値に','が含まれていても変換します。
     * @param v 数値文字列
     * @returns 数値
     */
    static parse(v: NumericString): number;
}
export {};
//# sourceMappingURL=NumberUtil.d.ts.map