/**
 * @fileoverview {@link DateTimeValidator}クラスを記述したファイルです。
 */
import { FieldValidator } from "./FieldValidator.js";
/**
 * 日付時刻バリデータの基本クラス。
 *
 * @extends FieldValidator
 */
export declare class DateTimeValidator extends FieldValidator {
    /**
     * エラーメッセージのキー。
     */
    dateFormatKey: string;
    /**
     * バリデーションを行ないます。
     * @param v 値。
     * @returns バリデーション結果。
     */
    validate(v: string): boolean;
    /**
     * エラーメッセージを取得します。
     * @returns エラーメッセージ。
     */
    getMessage(dspname: string): string;
}
//# sourceMappingURL=DateTimeValidator.d.ts.map