/**
 * @fileoverview {@link RegexpValidator}クラスを記述したファイルです。
 */

import { FieldValidator } from "./FieldValidator.js";

/**
 * 正規表現パターンバリデータ。
 *
 * @extends FieldValidator
 */
export class RegexpValidator extends FieldValidator {
	/**
	 * 正規表現パターン。
	 */
	declare pattern: string; 
	/**
	 * 複数行。
	 */
	declare multiline: boolean;  

	/**
	 * "."が改行にも待ったするモード。
	 */
	declare dotAll: boolean;  

	/**
	 * 大文字小文字無視。
	 */
	declare caseInsensitive: boolean;
	/**
	 * バリデーションを行ないます。
	 * @param v 値。
	 * @returns バリデーション結果。
	 */
	public validate(v: string): boolean {
		if (this.isBlank(v)) {
			return true;
		}

		let regex: RegExp;

		if (this.multiline || this.caseInsensitive || this.dotAll) {
			let flags = "";

			if (this.multiline) {
				flags += "m";
			}
			if (this.caseInsensitive) {
				flags += "i";
			}
			if (this.dotAll) {
				flags += "s";
			}

			logger.log("flags=" + flags);
			logger.log("pattern=" + this.pattern);
			regex = new RegExp(this.pattern, flags);
		} else {
			logger.log("pattern=" + this.pattern);
			regex = new RegExp(this.pattern);
		}

		return regex.test(v);
	}
}
