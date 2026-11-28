/**
 * @fileoverview {@link DisplayedRequiredValidator}クラスを記述したファイルです。
 */
import { WebComponent } from "../controller/WebComponent.js";
import { RequiredValidator } from "./RequiredValidator.js";
/**
 * 表示中の項目だけを必須とするバリデータクラス。
 *
 * @extends RequiredValidator
 */
export declare class DisplayedRequiredValidator extends RequiredValidator {
    /**
     * 親コンポーネント。
     */
    parent: WebComponent;
    /**
     * バリデーションを行ないます。
     * @param v 値。
     * @returns バリデーション結果。
     */
    validate(v: string): boolean;
}
//# sourceMappingURL=DisplayedRequiredValidator.d.ts.map