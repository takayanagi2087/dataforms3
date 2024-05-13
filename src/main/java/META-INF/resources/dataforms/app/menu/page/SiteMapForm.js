/**
 * メニューフォーム.
 */

'use strict';

import { MenuForm } from './MenuForm.js';


/**
 * メニューフォーム.
 */
export class SiteMapForm extends MenuForm {
	/**
	 * ページの各エレメントとの対応付け.
	 */
	async attach() {
		super.attach();
		this.menu = await this.newInstance(this.menu);
		await this.menu.init();
		await this.menu.attach();
	}
}

