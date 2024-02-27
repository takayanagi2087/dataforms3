package jp.dataforms.fw.devtool.expwebres.page;

import jp.dataforms.fw.devtool.base.page.DeveloperPage;

/**
 * webリソースエクスポートページクラス。
 *
 */
public class ExportWebResourcePage extends DeveloperPage {
	/**
	 * コンストラクタ。
	 */
	public ExportWebResourcePage() {
		this.addForm(new ExportWebResourceQueryForm());
		this.addForm(new ExportWebResourceQueryResultForm());
	}
}

