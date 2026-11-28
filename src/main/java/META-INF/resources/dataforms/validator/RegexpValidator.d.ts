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
     * バリデーションを行ないます。
     * @param v 値。
     * @returns バリデーション結果。
     */
    validate(v: string): boolean;
}
//# sourceMappingURL=RegexpValidator.d.ts.map