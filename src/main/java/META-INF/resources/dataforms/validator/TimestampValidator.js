/**
 * @fileOverview {@link TimestampValidator}クラスを記述したファイルです。
 */

'use strict';

import { DateTimeValidator } from './DateTimeValidator.js';


/**
 * @class TimestampValidator
 * タイムスタンプバリデータ。
 * @extends DateTimeValidator
 */
export class TimestampValidator extends DateTimeValidator {
	/**
	 * コンストラクタ。
	 */
	constructor() {
		super();
		this.dateFormatKey = "format.timestampfield";
	}
}

