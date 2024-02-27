/**
 * @fileOverview {@link FileSizeValidator}クラスを記述したファイルです。
 */

'use strict';

/**
 * @class FileSizeValidator
 * 最大長バリデータクラス。
 * @extends FieldValidator
 */
class FileSizeValidator extends FieldValidator {
	/**
	 * バリデーションを行ないます。
	 * @param {String} v 値。
	 * @returns {Boolean} バリデーション結果。
	 */
	validate(v) {
		if (this.isBlank(v)) {
			return true;
		} else {
			if (this.parent.get()[0].files.length > 0) {
				var size = this.parent.get()[0].files[0].size;
				if (size > this.maxFileSize) {
					return false;
				} else {
					return true;
				}
			} else {
				return true;
			}
		}
	}

	/**
	 * エラーメッセージを取得します。
	 * @param dspname 項目名。
	 * @returns {String} エラーメッセージ。
	 */
	getMessage(dspname) {
		return MessagesUtil.getMessage(this.messageKey, dspname, (this.maxFileSize / 1024 / 1024) + "MB");
	}
}
