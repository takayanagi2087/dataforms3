/**
 * @fileOverview {@link DisplayedRequiredValidator}クラスを記述したファイルです。
 */

'use strict';


/**
 * @class DisplayedRequiredValidator
 *
 * @extends RequiredValidator
 */
class DisplayedRequiredValidator extends RequiredValidator {
	/**
	 * HTMLエレメントとの対応付けを行います。
	 */
	attach() {
		super.attach(this);
	}

	/**
	 * バリデーションを行ないます。
	 * @param {String} v 値。
	 * @returns {Boolean} バリデーション結果。
	 */
	validate(v) {
		var f = this.getParentForm();
		var vflg = f.get(this.parent.id).is(":visible");
		if (vflg) {
			return (this.isBlank(v) == false);
		} else {
			return true;
		}
	}
}



