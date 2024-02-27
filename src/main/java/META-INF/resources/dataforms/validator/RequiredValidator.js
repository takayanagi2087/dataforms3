/**
 * @fileOverview {@link RequiredValidator}クラスを記述したファイルです。
 */

'use strict';

/**
 * @class RequiredValidator
 * 必須バリデータクラス。
 * <pre>
 * </pre>
 * @extends FieldValidator
 */
class RequiredValidator extends FieldValidator {
	/**
	 * バリデーションを行ないます。
	 * @param {String} v 値。
	 * @returns {Boolean} バリデーション結果。
	 */
	validate(v) {
		return (this.isBlank(v) == false);
	}
}

