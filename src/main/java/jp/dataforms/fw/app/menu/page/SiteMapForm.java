package jp.dataforms.fw.app.menu.page;

import jp.dataforms.fw.menu.Menu;

/**
 * サイドメニューフォームクラス。
 *
 */
public class SiteMapForm extends MenuForm {
	/**
	 * コンストラクタ。
	 */
	public SiteMapForm() {
		super(null);
	}

	@Override
	protected Menu newMenuComponent() {
		Menu menu = new Menu();
		return menu;
	}
}
