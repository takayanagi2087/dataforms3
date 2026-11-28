/**
 * @fileOverview {@link ValidationError}クラスを記述したファイルです。
 */
'use strict';
/**
 * @class ValidationError
 * バリデーションエラー情報クラス。
 */
export class ValidationError {
    fieldId;
    message;
    /**
     * コンストラクタ。
     * @param fid フィールドID
     * @param msg メッセージ
     */
    constructor(fid, msg) {
        this.fieldId = fid;
        this.message = msg;
    }
}
