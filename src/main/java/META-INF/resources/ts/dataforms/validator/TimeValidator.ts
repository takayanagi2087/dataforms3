/**
 * @fileoverview {@link TimeValidator}クラスを記述したファイルです。
 */

import { DateTimeValidator } from "./DateTimeValidator.js";

/**
 * 時刻バリデータ。
 *
 * @extends DateTimeValidator
 */
export class TimeValidator extends DateTimeValidator {
	/**
	 * コンストラクタ。
	 */
	public constructor() {
		super();
		this.dateFormatKey = "format.timefield";
	}
}
