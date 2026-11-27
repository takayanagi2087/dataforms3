package jp.dataforms.fw.devtool.webres.gen;

import jp.dataforms.fw.controller.Form;

/**
 * OkCancelFormのジェルレーター。
 */
public class OkCancelFormHtmlGenerator extends FormHtmlGenerator {

	/**
	 * コンストラクタ。
	 * @param form フォーム。
	 * @param indent　インデント。
	 */
	public OkCancelFormHtmlGenerator(Form form, int indent) {
		super(form, indent);
	}

	
	/**
	 * フォームのボタンHTMLを生成します。
	 */
	@Override
	protected String getFormButtonHtml() {
		String tabs = this.getTabs();
		String ret = tabs + "\t<button type=\"button\" id=\"okButton\">ＯＫ</button>\n" +
				tabs + "\t<button type=\"button\" id=\"cancelButton\">キャンセル</button>\n";
		return ret;
	}
}
