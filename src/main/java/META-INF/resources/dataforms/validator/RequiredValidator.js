/**
 * @fileOverview {@link RequiredValidator}クラスを記述したファイルです。
 */

'use strict';

import { FieldValidator } from './FieldValidator.js';


/**
 * @class RequiredValidator
 * 必須バリデータクラス。
 * <pre>
 * </pre>
 * @extends FieldValidator
 */
export class RequiredValidator extends FieldValidator {
	/**
	 * コンストラクタ。
	 */
	constructor() {
		super();
		this.messageKey = "error.required";
	}

	/**
	 * バリデーションを行ないます。
	 * @param {String} v 値。
	 * @returns {Boolean} バリデーション結果。
	 */
	validate(v) {
		return (this.isBlank(v) == false);
	}
}

