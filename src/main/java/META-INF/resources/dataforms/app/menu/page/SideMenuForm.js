/**
 * メニューフォーム.
 */

'use strict';

import { MenuForm } from './MenuForm.js';

export class SideMenuForm extends MenuForm {
	/**
	 * ページの各エレメントとの対応付け.
	 */
	async attach() {
		super.attach();
		this.menu = await this.newInstance(this.sideMenu);
		await this.menu.init();
		await this.menu.attach();
	}
}


