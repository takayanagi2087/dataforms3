package jp.dataforms.fw.devtool.pageform.page;

import jp.dataforms.fw.devtool.base.page.DeveloperPage;

/**
 * テストツール生成ページ。
 */
public class TestSrcGeneratorPage extends DeveloperPage {
	/**
	 * コンストラクタ。
	 */
	public TestSrcGeneratorPage() {
		this.addForm(new PageGeneratorQueryForm());
		this.addForm(new PageGeneratorQueryResultForm());
		this.addForm(new TestSrcGeneratorEditForm());
	}
}
