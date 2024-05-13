/**
 * @fileOverview {@link TextField}クラスを記述したファイルです。
 */

'use strict';

import { Field } from './Field.js';

/**
 * @class TextField
 * テキストフィールドクラス。
 * <pre>
 * テキストフィールドの基底クラスです。
 * </pre>
 * @extends Field
 */
export class TextField extends Field {
	/**
	 * 値を設定します。
	 * @param {String} value 設定値。
	 */
	setValue(value) {
		let ut = $('<div>').html(value).text(); //unescape
		super.setValue(ut);
	}
}

