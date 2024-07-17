package jp.dataforms.fw.doc.page;

import jp.dataforms.fw.app.base.page.BasePage;

/**
 * バージョン情報ページクラスです。
 *
 */
public class VersionInfoPage extends BasePage {
	/**
	 * コンストラクタ。
	 */
	public VersionInfoPage() {
		this.addForm(new VersionInfoForm());
	}
}
