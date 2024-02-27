package jp.dataforms.fw.devtool.pageform.page;

import jp.dataforms.fw.devtool.base.page.DeveloperPage;

/**
 * DaoとページJavaクラス作成ページ。
 */
public class DaoAndPageGeneratorPage extends DeveloperPage {
	/**
	 * コンストラクタ。
	 */
	public DaoAndPageGeneratorPage() {
		this.addForm(new PageGeneratorQueryForm());
		this.addForm(new PageGeneratorQueryResultForm());
		this.addForm(new DaoAndPageGeneratorEditForm());
		this.addDialog(new FieldListDialog());
	}
}
