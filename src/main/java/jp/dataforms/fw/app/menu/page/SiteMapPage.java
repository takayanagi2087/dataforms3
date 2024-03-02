package jp.dataforms.fw.app.menu.page;

import jp.dataforms.fw.app.base.page.BasePage;

/**
 * サイトマップページクラス。
 *
 */
public class SiteMapPage extends BasePage {
	/**
	 * コンストラクタ。
	 */
	public SiteMapPage() {
		this.addForm(new SiteMapForm());
	}
}
