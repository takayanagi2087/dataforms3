package jp.dataforms.fw.devtool.table.page;

import jp.dataforms.fw.devtool.base.page.SrcGenPage;

/**
 * ページクラス。
 */
public class TableGeneratorPage extends SrcGenPage {
	/**
	 * コンストラクタ。
	 */
	public TableGeneratorPage() {
		this.addForm(new TableGeneratorQueryForm());
		this.addForm(new TableGeneratorQueryResultForm());
		this.addForm(new TableGeneratorEditForm());

	}
}
