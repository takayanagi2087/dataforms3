/**
 * @fileOverview {@link DateTimeValidator}クラスを記述したファイルです。
 */

'use strict';

/**
 * @class DateTimeValidator
 * 日付時刻バリデータの基本クラス。
 * <pre>
 * </pre>
 * @extends FieldValidator
 */
class DateTimeValidator extends FieldValidator {
	/**
	 * バリデーションを行ないます。
	 * @param {String} v 値。
	 * @returns {Boolean} バリデーション結果。
	 */
	validate(v) {
		var dateFormat = MessagesUtil.getMessage(this.dateFormatKey);
		if (this.isBlank(v)) {
			return true;
		}
		var fmt = new SimpleDateFormat(dateFormat);
	    if (fmt.parse(v) != null) {
	    	return true;
	    }
	    return false;
	}

	/**
	 * エラーメッセージを取得します。
	 * @returns {String} エラーメッセージ。
	 */
	getMessage(dspname) {
		var dateFormat = MessagesUtil.getMessage(this.dateFormatKey);
		return MessagesUtil.getMessage(this.messageKey, dspname, dateFormat);
	}
}



