/**
 * @fileOverview {@link BasePage}クラスを記述したファイルです。
 */

'use strict';

import { Page } from '../../../controller/Page.js';

/**
 * @class BasePage
 * アプリケーションの基本ページクラス。
 * <pre>
 * ログインしなくても表示できるページです。
 * </pre>
 * @extends Page
 */
export class BasePage extends Page {
	/**
	 * HTMLエレメントとの対応付けを行います。
	 */
	async attach() {
		await super.attach();
	}
}