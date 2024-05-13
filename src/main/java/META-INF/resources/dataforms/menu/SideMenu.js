/**
 * @fileOverview {@link SideMenu}クラスを記述したファイルです。
 */

'use strict';


import { Menu } from '/dataforms3app/dataforms/menu/Menu.js';

/**
 * @class SideMenu
 *
 * サイドメニュークラス。
 * <pre>
 * </pre>
 * @extends Menu
 */
export class SideMenu extends Menu {
	/**
	 * HTMLエレメントとの対応付けを行います。
	 */
	async attach() {
		await super.attach();
		let menu = this.get();
		menu.find(".sideMenuGroup").click((ev) => {
			if (!this.multiOpenMenu) {
				this.hideAllMenu();
			}
			let menuGroupId = $(ev.currentTarget).attr("data-menu-group-id");
			$(ev.currentTarget).next().slideToggle("fast", () => {
				this.setCookie("menuGroup_" + menuGroupId, $(ev.currentTarget).next().is(":visible"));
			});
			return false;
		});
		menu.find(".sideMenuGroup").each((_, el) => {
			let menuGroupId = $(el).attr("data-menu-group-id");
			let status = this.getCookie("menuGroup_" + menuGroupId);
			if (status == "true") {
				$(el).next().show();
			} else {
				$(el).next().hide();
			}
		});
	}

	/**
	 * 全メニューを隠します。
	 */
	hideAllMenu() {
		let menu = this.get();
		menu.find("[id$='.\pageList']").hide();
		menu.find(".sideMenuGroup").each((_, el) => {
			let menuGroupId = $(el).attr("data-menu-group-id");
			this.setCookie("menuGroup_" + menuGroupId, false);
		});
	}

}



