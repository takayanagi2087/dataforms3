package jp.dataforms.fw.doc.page;

import jp.dataforms.fw.app.base.page.BasePage;

/**
 * ドキュメントのフレーム用のページです。
 *
 */
public class DocFramePage extends BasePage {
	/**
	 * コンストラクタ。
	 */
	public DocFramePage() {

	}

	@Override
	protected void buildCssList() {
		super.buildCssList();
		this.addPreloadCss("/dataforms/doc/css/docFrame.css");
	}
}
