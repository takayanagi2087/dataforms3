
/**
 * @fileOverview {@link NumberValidator}クラスを記述したファイルです。
 */

'use strict';

/**
 * @class NumbereValidator
 * 数値バリデータクラス。
 * <pre>
 * </pre>
 * @extends FieldValidator
 */
class NumberValidator extends FieldValidator {
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
		var n = Number(ntext);
		if (isNaN(n)) {
			return false;
		} else {
			return true;
		}
	}
}


