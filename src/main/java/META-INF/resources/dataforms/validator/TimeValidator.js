/**
 * @fileOverview {@link TimeValidator}クラスを記述したファイルです。
 */

'use strict';

import { DateTimeValidator } from './DateTimeValidator.js';


/**
 * @class TimeValidator
 * 時刻バリデータ。
 * <pre>
 * </pre>
 * @extends DateTimeValidator
 */
export class TimeValidator extends DateTimeValidator {
	/**
	 * コンストラクタ。
	 */
	constructor() {
		super();
		this.dateFormatKey = "format.timefield";
	}
}