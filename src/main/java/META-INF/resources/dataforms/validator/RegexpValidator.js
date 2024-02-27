/**
 * @fileOverview {@link RegexpValidator}クラスを記述したファイルです。
 */

'use strict';

/**
 * @class RegexpValidator
 * 正規表現パターンバリデータ。
 * <pre>
 * </pre>
 * @extends FieldValidator
 */
class RegexpValidator extends FieldValidator {
	/**
	 * バリデーションを行ないます。
	 * @param {String} v 値。
	 * @returns {Boolean} バリデーション結果。
	 */
	validate(v) {
		if (this.isBlank(v)) {
			return true;
		}
		if (this.multiline || this.caseInsensitive || this.dotAll ) {
			let flags = "";
			if (this.multiline) {
				flags += "m"
			}
			if (this.caseInsensitive) {
				flags += "i"
			}
			if (this.dotAll) {
				flags += "s"
			}
			logger.log("flags=" + flags);
			logger.log("pattern=" + this.pattern);
			let regex = new RegExp(this.pattern, flags);
			if (regex.test(v)) {
				return true;
			}
		} else {
			logger.log("pattern=" + this.pattern);
			let regex = new RegExp(this.pattern);
			if (regex.test(v)) {
				return true;
			}
		}
	    return false;
	}
}

