package jp.dataforms.fw.devtool.webres.gen;

import jp.dataforms.fw.controller.Form;

/**
 * 問い合わせフォームHTMLジェネレータ。
 *
 */
public class QueryFormHtmlGenerator extends FormHtmlGenerator {
	/**
	 * コンストラクタ。
	 *
	 * @param form フォーム。
	 * @param indent 段付けのtab数。
	 */
	public QueryFormHtmlGenerator(final Form form, final int indent) {
		super(form, indent);
	}

	@Override
	protected String getFormTitle() {
		return "検索条件";
	}

	@Override
	protected String getFormButtionHtml() {
		String tabs = this.getTabs();
		String ret = tabs + "\t<input type=\"submit\" id=\"queryButton\" class=\"largeButton\" value=\"検索\">\n" +
				tabs + "\t<input type=\"button\" id=\"exportButton\" class=\"largeButton\" value=\"エクスポート\">\n" +
				tabs + "\t<input type=\"button\" id=\"resetButton\" class=\"largeButton\" value=\"リセット\">&nbsp;\n" +
				tabs + "\t<input type=\"button\" id=\"newButton\" class=\"largeButton\" value=\"新規登録\">\n";
		return ret;
	}
}

