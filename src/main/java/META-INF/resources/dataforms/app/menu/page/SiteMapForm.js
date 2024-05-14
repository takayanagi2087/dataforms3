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
	 attach() {
		super.attach();
		this.menu = this.newInstance(this.menu);
		this.menu.init();
		this.menu.attach();
	}
}

