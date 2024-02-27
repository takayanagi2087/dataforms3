package jp.dataforms.fw.devtool.pageform.page;

import jp.dataforms.fw.devtool.base.page.DeveloperPage;

/**
 * ページJavaクラス作成ページ。
 */
public class PageGeneratorPage extends DeveloperPage {
	/**
	 * コンストラクタ。
	 */
	public PageGeneratorPage() {
		this.addForm(new PageGeneratorQueryForm());
		this.addForm(new PageGeneratorQueryResultForm());
		this.addForm(new PageGeneratorEditForm());
		this.setMenuItem(false);
	}


}
