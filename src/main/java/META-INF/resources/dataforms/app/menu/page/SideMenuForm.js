/**
 * メニューフォーム.
 */

'use strict';

class SideMenuForm extends MenuForm {
	/**
	 * ページの各エレメントとの対応付け.
	 */
	attach() {
		super.attach();
		this.menu = this.newInstance(this.sideMenu);
		this.menu.init();
		this.menu.attach();
	}
}


