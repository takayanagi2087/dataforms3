/**
 * @fileoverview {@link FileSizeValidator}クラスを記述したファイルです。
 */
import { WebComponent } from "../controller/WebComponent.js";
import { FieldValidator } from "./FieldValidator.js";
/**
 * 最大ファイルサイズバリデータクラス。
 *
 * @extends FieldValidator
 */
export declare class FileSizeValidator extends FieldValidator {
    /**
     * 親コンポーネント。
     */
    parent: WebComponent;
    /**
     * 最大ファイルサイズ。
     */
    maxFileSize: number;
    /**
     * バリデーションを行ないます。
     * @param v 値。
     * @returns バリデーション結果。
     */
    validate(v: string): boolean;
    /**
     * エラーメッセージを取得します。
     * @param dspname 項目名。
     * @returns エラーメッセージ。
     */
    getMessage(dspname: string): string;
}
//# sourceMappingURL=FileSizeValidator.d.ts.map