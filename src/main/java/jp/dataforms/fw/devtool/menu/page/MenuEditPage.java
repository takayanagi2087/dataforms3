package jp.dataforms.fw.devtool.menu.page;

import jp.dataforms.fw.devtool.base.page.DeveloperPage;

/**
 * メニュー編集ページ。
 */
public class MenuEditPage extends DeveloperPage {
	/**
	 * コンストラクタ。
	 */
	public MenuEditPage() {
		this.addForm(new MenuEditForm());
	}
}
