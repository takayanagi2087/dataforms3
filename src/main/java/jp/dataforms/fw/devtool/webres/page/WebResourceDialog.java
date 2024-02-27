package jp.dataforms.fw.devtool.webres.page;

import jp.dataforms.fw.controller.Dialog;

/**
 * Webリソース作成ダイアログクラス。
 */
public class WebResourceDialog extends Dialog {

	/**
	 * コンストラクタ。
	 */
	public WebResourceDialog() {
		super(null);
		this.addForm(new WebResourceForm());
	}

}
