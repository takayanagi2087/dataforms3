/**
 * @fileOverview {@link DateValidator}クラスを記述したファイルです。
 */

'use strict';

import { DateTimeValidator } from './DateTimeValidator.js';

/**
 * @class DateValidator
 * 日付バリデータクラス。
 * <pre>
 * </pre>
 * @extends DateTimeValidator
 */
export class DateValidator extends DateTimeValidator {
	/**
	 * コンストラクタ。
	 */
	constructor() {
		super();
		this.dateFormatKey = "format.datefield";
	}
}