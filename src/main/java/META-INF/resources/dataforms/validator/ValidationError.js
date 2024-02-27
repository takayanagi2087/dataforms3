/**
 * @fileOverview {@link ValidationError}クラスを記述したファイルです。
 */

'use strict';

/**
 * @class ValidationError.
 * バリデーションエラー情報クラス.
 */
class ValidationError {
	/**
	 * コンストラクタ。
	 * @param {String} fid フィールドID.
	 * @param {String} msg メッセージ.
	 */
	constructor(fid, msg) {
		this.fieldId = fid;
		this.message = msg;
	}
}

