/**
 * @fileoverview {@link RequiredValidator}クラスを記述したファイルです。
 */
import { FieldValidator } from "./FieldValidator.js";
/**
 * 必須バリデータクラス。
 *
 * @extends FieldValidator
 */
export class RequiredValidator extends FieldValidator {
    /**
     * コンストラクタ。
     */
    constructor() {
        super();
        this.messageKey = "error.required";
    }
    /**
     * バリデーションを行ないます。
     * @param v 値。
     * @returns バリデーション結果。
     */
    validate(v) {
        return !this.isBlank(v);
    }
}
