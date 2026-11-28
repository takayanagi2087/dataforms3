/**
 * @fileoverview {@link DateTimeValidator}クラスを記述したファイルです。
 */
import { SimpleDateFormat } from "../util/SimpleDateFormat.js";
import { MessagesUtil } from "../util/MessagesUtil.js";
import { FieldValidator } from "./FieldValidator.js";
/**
 * 日付時刻バリデータの基本クラス。
 *
 * @extends FieldValidator
 */
export class DateTimeValidator extends FieldValidator {
    /**
     * バリデーションを行ないます。
     * @param v 値。
     * @returns バリデーション結果。
     */
    validate(v) {
        let dateFormat = MessagesUtil.getMessage(this.dateFormatKey);
        if (this.isBlank(v)) {
            return true;
        }
        if (dateFormat == null) {
            return false;
        }
        const fmt = new SimpleDateFormat(dateFormat);
        return fmt.parse(v) !== null;
    }
    /**
     * エラーメッセージを取得します。
     * @returns エラーメッセージ。
     */
    getMessage(dspname) {
        const dateFormat = MessagesUtil.getMessage(this.dateFormatKey);
        if (dateFormat == null) {
            return "The date format has not been set.";
        }
        const ret = MessagesUtil.getMessage(this.messageKey, dspname, dateFormat);
        if (ret == null) {
            return "The message is not defined.";
        }
        else {
            return ret;
        }
    }
}
