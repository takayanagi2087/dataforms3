package jp.dataforms.fw.devtool.dao.page;

import jp.dataforms.fw.devtool.base.page.DeveloperPage;

/**
 * Dao Javaソース作成ページ。
 *
 */
public class DaoGeneratorPage extends DeveloperPage {
	/**
	 * コンストラクタ。
	 */
	public DaoGeneratorPage() {
		this.addForm(new DaoGeneratorQueryForm());
		this.addForm(new DaoGeneratorQueryResultForm());
		this.addForm(new DaoGeneratorEditForm());
		this.setMenuItem(false);
	}
}
