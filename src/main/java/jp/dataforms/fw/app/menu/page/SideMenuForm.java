package jp.dataforms.fw.app.menu.page;

import jp.dataforms.fw.menu.Menu;
import jp.dataforms.fw.menu.SideMenu;

/**
 * サイドメニューフォームクラス。
 *
 */
public class SideMenuForm extends MenuForm {
	/**
	 * コンストラクタ。
	 */
	public SideMenuForm() {
		super(null);
	}

	@Override
	protected Menu newMenuComponent() {
		SideMenu menu = new SideMenu();
		return menu;
	}


}
