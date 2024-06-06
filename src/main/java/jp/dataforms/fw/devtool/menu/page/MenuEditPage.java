package jp.dataforms.fw.devtool.menu.page;

import jp.dataforms.fw.devtool.base.page.SrcGenPage;

/**
 * メニュー編集ページ。
 */
public class MenuEditPage extends SrcGenPage {
	/**
	 * コンストラクタ。
	 */
	public MenuEditPage() {
		this.addForm(new MenuEditForm());
	}
}
