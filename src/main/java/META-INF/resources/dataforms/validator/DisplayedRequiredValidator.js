/**
 * @fileOverview {@link DisplayedRequiredValidator}クラスを記述したファイルです。
 */

'use strict';

import { RequiredValidator } from './RequiredValidator.js';


/**
 * @class DisplayedRequiredValidator
 *
 * @extends RequiredValidator
 */
export class DisplayedRequiredValidator extends RequiredValidator {
	/**
	 * HTMLエレメントとの対応付けを行います。
	 */
	async attach() {
		await super.attach(this);
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



