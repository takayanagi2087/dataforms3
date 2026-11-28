/**
 * @fileoverview {@link FileSizeValidator}クラスを記述したファイルです。
 */
import { MessagesUtil } from "../util/MessagesUtil.js";
import { FieldValidator } from "./FieldValidator.js";
/**
 * 最大ファイルサイズバリデータクラス。
 *
 * @extends FieldValidator
 */
export class FileSizeValidator extends FieldValidator {
    /**
     * バリデーションを行ないます。
     * @param v 値。
     * @returns バリデーション結果。
     */
    validate(v) {
        if (this.isBlank(v)) {
            return true;
        }
        const p = this.parent.get();
        if (p != null) {
            const fileInput = p[0];
            if (fileInput != null) {
                if (fileInput.files && fileInput.files.length > 0) {
                    const size = fileInput.files[0].size;
                    return size <= this.maxFileSize;
                }
            }
        }
        return true;
    }
    /**
     * エラーメッセージを取得します。
     * @param dspname 項目名。
     * @returns エラーメッセージ。
     */
    getMessage(dspname) {
        const maxFileSizeMB = (this.maxFileSize / 1024 / 1024) + "MB";
        const ret = MessagesUtil.getMessage(this.messageKey, dspname, maxFileSizeMB);
        if (ret != null) {
            return ret;
        }
        else {
            return "message not found.";
        }
    }
}
