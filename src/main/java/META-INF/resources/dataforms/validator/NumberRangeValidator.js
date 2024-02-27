/**
 * @fileOverview {@link NumberRangeValidator}クラスを記述したファイルです。
 */

'use strict';

/**
 * @class NumberRangeValidator
 * 数値範囲バリデータクラス。
 * <pre>
 * </pre>
 * @extends FieldValidator
 */
class NumberRangeValidator extends FieldValidator {
	/**
	 * バリデーションを行ないます。
	 * @param {String} v 値。
	 * @returns {Boolean} バリデーション結果。
	 */
	validate(v) {
		if (this.isBlank(v)) {
			return true;
		}
	    var value = "" + v;
	    // カンマを削除
	    var ntext = value.replace(/,/g, "");
		var num = Number(ntext);
		if (this.min <= num && num <= this.max) {
			return true;
		} else {
			return false;
		}
	};

	/**
	 * エラーメッセージを取得します。
	 * @returns {String} エラーメッセージ。
	 */
	getMessage(dspname) {
		return MessagesUtil.getMessage(this.messageKey, dspname, this.min, this.max);
	};
}

