/**
 * @fileoverview {@link RequiredValidator}クラスを記述したファイルです。
 */
import { FieldValidator } from "./FieldValidator.js";
/**
 * 必須バリデータクラス。
 *
 * @extends FieldValidator
 */
export declare class RequiredValidator extends FieldValidator {
    /**
     * コンストラクタ。
     */
    constructor();
    /**
     * バリデーションを行ないます。
     * @param v 値。
     * @returns バリデーション結果。
     */
    validate(v: string): boolean;
}
//# sourceMappingURL=RequiredValidator.d.ts.map