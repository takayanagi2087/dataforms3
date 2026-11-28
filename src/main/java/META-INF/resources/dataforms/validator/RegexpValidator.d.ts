/**
 * @fileoverview {@link RegexpValidator}クラスを記述したファイルです。
 */
import { FieldValidator } from "./FieldValidator.js";
/**
 * 正規表現パターンバリデータ。
 *
 * @extends FieldValidator
 */
export declare class RegexpValidator extends FieldValidator {
    /**
     * 正規表現パターン。
     */
    pattern: string;
    /**
     * 複数行。
     */
    multiline: boolean;
    /**
     * "."が改行にも待ったするモード。
     */
    dotAll: boolean;
    /**
     * 大文字小文字無視。
     */
    caseInsensitive: boolean;
    /**
     * バリデーションを行ないます。
     * @param v 値。
     * @returns バリデーション結果。
     */
    validate(v: string): boolean;
}
//# sourceMappingURL=RegexpValidator.d.ts.map