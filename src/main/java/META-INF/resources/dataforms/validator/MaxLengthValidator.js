/**
 * @fileOverview {@link MaxLengthValidator}クラスを記述したファイルです。
 */

'use strict';

/**
 * @class MaxLengthValidator
 * 最大長バリデータクラス。
 * @extends FieldValidator
 */
class MaxLengthValidator extends FieldValidator {

	/**
	 * バリデーションを行ないます。
	 * @param {String} v 値。
	 * @returns {Boolean} バリデーション結果。
	 */
	validate(v) {
		if (this.isBlank(v)) {
			return true;
		} else {
			if (v.length <= this.length) {
				return true;
			} else {
				return false;
			}
		}
	}

	/**
	 * エラーメッセージを取得します。
	 * @param dspname 項目名。
	 * @returns {String} エラーメッセージ。
	 */
	getMessage(dspname) {
		return MessagesUtil.getMessage(this.messageKey, dspname, this.length);
	}
}

