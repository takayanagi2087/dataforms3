/**
 * @fileOverview {@link FieldValidator}クラスを記述したファイルです。
 */

'use strict';

import { MessagesUtil } from '../util/MessagesUtil.js';
import { WebComponent } from '../controller/WebComponent.js';

/**
 * @class FieldValidator
 * フィールドバリデータ基本クラス。
 * @extends WebComponent
 */
export class FieldValidator extends WebComponent {
	/**
	 * エラーメッセージのキー。
	 */
	declare messageKey: string; // フレームワークが実行時に設定
	
	/**
	 * コンストラクタ。
	 */
	constructor() {
		super();
	}

	/**
	 * 初期化を行います。
	 */
	init(): void {
		super.init();
	}

	/**
	 * バリデーションを行ないます。
	 * @param v 値
	 * @returns バリデーション結果
	 */
	validate(v: string | null | undefined): boolean {
		return true;
	}

	/**
	 * エラーメッセージを取得します。
	 * @param dspname 表示名
	 * @returns エラーメッセージ
	 */
	getMessage(dspname: string): string | undefined {
		return MessagesUtil.getMessage(this.messageKey, dspname);
	}

	/**
	 * 未入力かどうかをチェックします。
	 * @param v 値
	 * @returns 未入力の場合true
	 */
	isBlank(v: string | null | undefined): boolean {
		if (v != null) {
			if (v.length !== 0) {
				return false;
			}
		}
		return true;
	}
}
