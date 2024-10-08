/**
 * @fileOverview {@link FieldValidator}クラスを記述したファイルです。
 */

'use strict';

import { MessagesUtil } from '../util/MessagesUtil.js';
import { WebComponent } from '../controller/WebComponent.js';

/**
 * @class FieldValidator
 * フィールドバリデータ基本クラス。
 * <pre>
 * </pre>
 * @extends WebComponent
 */
export class FieldValidator extends WebComponent {
	/**
	 * コンストラクタ。
	 */
	constructor() {
		super();
	}
	
	/**
	 * 初期化を行います。
	 */
	init() {
		super.init();
	}

	/**
	 * バリデーションを行ないます。
	 * @param {String} v 値。
	 * @returns {Boolean} バリデーション結果。
	 */
	validate(v) {
		return true;
	}

	/**
	 * エラーメッセージを取得します。
	 * @returns {String} エラーメッセージ。
	 */
	getMessage(dspname) {
		return MessagesUtil.getMessage(this.messageKey, dspname);
	}

	/**
	 * 未入力かどうかをチェックします。
	 * @param {String} v 値。
	 * @returns {Boolean} 未入力の場合true。
	 */
	isBlank(v) {
		if (v != null) {
			if (v.length != 0) {
				return false;
			}
		}
		return true;
	}

}





