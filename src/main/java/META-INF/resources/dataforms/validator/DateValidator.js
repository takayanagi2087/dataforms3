/**
 * @fileOverview {@link DateValidator}クラスを記述したファイルです。
 */

'use strict';

/**
 * @class DateValidator
 * 日付バリデータクラス。
 * <pre>
 * </pre>
 * @extends DateTimeValidator
 */
class DateValidator extends DateTimeValidator {
	/**
	 * コンストラクタ。
	 */
	constructor() {
		super();
		this.dateFormatKey = "format.datefield";
	}
}