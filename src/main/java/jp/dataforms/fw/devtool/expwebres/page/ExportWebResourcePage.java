package jp.dataforms.fw.devtool.expwebres.page;

import jp.dataforms.fw.devtool.base.page.SrcGenPage;

/**
 * webリソースエクスポートページクラス。
 *
 */
public class ExportWebResourcePage extends SrcGenPage {
	/**
	 * コンストラクタ。
	 */
	public ExportWebResourcePage() {
		this.addForm(new ExportWebResourceQueryForm());
		this.addForm(new ExportWebResourceQueryResultForm());
	}
}

