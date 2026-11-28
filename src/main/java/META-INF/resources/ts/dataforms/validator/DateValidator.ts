/**
 * @fileoverview {@link DateValidator}クラスを記述したファイルです。
 */

import { DateTimeValidator } from "./DateTimeValidator.js";

/**
 * 日付バリデータクラス。
 *
 * @extends DateTimeValidator
 */
export class DateValidator extends DateTimeValidator {
	/**
	 * コンストラクタ。
	 */
	public constructor() {
		super();
		this.dateFormatKey = "format.datefield";
	}
}
