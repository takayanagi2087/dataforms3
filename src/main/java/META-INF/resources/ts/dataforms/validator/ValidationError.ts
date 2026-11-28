/**
 * @fileOverview {@link ValidationError}クラスを記述したファイルです。
 */

'use strict';

/**
 * @class ValidationError
 * バリデーションエラー情報クラス。
 */
export class ValidationError {
	readonly fieldId: string;
	readonly message: string;

	/**
	 * コンストラクタ。
	 * @param fid フィールドID
	 * @param msg メッセージ
	 */
	constructor(fid: string, msg: string) {
		this.fieldId = fid;
		this.message = msg;
	}
}
