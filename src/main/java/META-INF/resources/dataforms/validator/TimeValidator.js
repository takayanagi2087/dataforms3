/**
 * @fileOverview {@link TimeValidator}クラスを記述したファイルです。
 */

'use strict';

/**
 * @class TimeValidator
 * 時刻バリデータ。
 * <pre>
 * </pre>
 * @extends DateTimeValidator
 */
class TimeValidator extends DateTimeValidator {
	/**
	 * コンストラクタ。
	 */
	constructor() {
		super();
		this.dateFormatKey = "format.timefield";
	}
}