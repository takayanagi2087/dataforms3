package jp.dataforms.fw.devtool.webres.page;

import jp.dataforms.fw.devtool.base.page.SrcGenPage;

/**
 * Webリソースページクラス。
 * <pre>
 * 指定されたPage javaクラスに含まれるWebComponentのに対応した、
 * html,jsを作成するためのページです。
 * </pre>
 */
public class WebResourcePage extends SrcGenPage {
	/**
	 * コンストラクタ。
	 */
	public WebResourcePage() {
		this.addForm(new WebResourceQueryForm());
		this.addForm(new WebResourceQueryResultForm());
		this.addDialog(new WebResourceDialog());
	}
}
