/**
 * @fileoverview {@link DisplayedRequiredValidator}クラスを記述したファイルです。
 */
import { RequiredValidator } from "./RequiredValidator.js";
/**
 * 表示中の項目だけを必須とするバリデータクラス。
 *
 * @extends RequiredValidator
 */
export class DisplayedRequiredValidator extends RequiredValidator {
    /**
     * バリデーションを行ないます。
     * @param v 値。
     * @returns バリデーション結果。
     */
    validate(v) {
        const form = this.getParentForm();
        if (form != null) {
            const parent = form.get(this.parent.id);
            if (parent != null) {
                const visible = parent.is(":visible");
                if (visible) {
                    return !this.isBlank(v);
                }
            }
        }
        return true;
    }
}
