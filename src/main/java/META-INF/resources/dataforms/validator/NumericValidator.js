/**
 * @fileOverview {@link NumericValidator}クラスを記述したファイルです。
 */

'use strict';

/**
 * @class NumericValidator
 * Numeric型数値バリデータクラス。
 * <pre>
 * DBのNumeric型用のチェックで、精度と小数点以下桁数もチェックします。
 * @extends FieldValidator
 */
class NumericValidator extends FieldValidator {
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
	    var sc = 0;
	    var abs = value.replace(/[\-\+,]/g, "");
		var sidx = abs.indexOf('.');
		var ln = abs.length;
		if (sidx >= 0) {
			sc = abs.length - sidx - 1;
			ln = sidx;
		}
		if (ln > this.precision - this.scale) {
			return false;
		}
		if (sc > this.scale) {
			return false;
		}
		return true;
	};

	/**
	 * エラーメッセージを取得します。
	 * @returns {String} エラーメッセージ。
	 */
	getMessage(dspname) {
		return MessagesUtil.getMessage(this.messageKey, dspname, this.precision, this.scale);
	};
}


