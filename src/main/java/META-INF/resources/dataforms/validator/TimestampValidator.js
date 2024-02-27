/**
 * @fileOverview {@link TimestampValidator}クラスを記述したファイルです。
 */

'use strict';

/**
 * @class TimestampValidator
 * タイムスタンプバリデータ。
 * @extends DateTimeValidator
 */
class TimestampValidator extends DateTimeValidator {
	/**
	 * コンストラクタ。
	 */
	constructor() {
		super();
		this.dateFormatKey = "format.timestampfield";
	}
}

