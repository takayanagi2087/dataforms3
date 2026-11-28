/**
 * @fileoverview {@link TimestampValidator}クラスを記述したファイルです。
 */

import { DateTimeValidator } from "./DateTimeValidator.js";

/**
 * タイムスタンプバリデータ。
 *
 * @extends DateTimeValidator
 */
export class TimestampValidator extends DateTimeValidator {
	/**
	 * コンストラクタ。
	 */
	public constructor() {
		super();
		this.dateFormatKey = "format.timestampfield";
	}
}
