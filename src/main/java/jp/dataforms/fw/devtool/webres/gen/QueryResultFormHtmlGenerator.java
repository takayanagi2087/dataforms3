package jp.dataforms.fw.devtool.webres.gen;

import jp.dataforms.fw.controller.Form;
import jp.dataforms.fw.controller.WebComponent;

/**
 * 問い合わせ結果フォームHTMLジェネレータ。
 *
 */
public class QueryResultFormHtmlGenerator extends FormHtmlGenerator {
	/**
	 * 指定されたクラスに対応したフォームHTMLジェネレータを作成します。
	 * @param form フォーム。
	 * @param indent 段付けのtab数。
	 */
	public QueryResultFormHtmlGenerator(final Form form, final int indent) {
		super(form, indent);

	}

	@Override
	protected boolean isTargetedForGeneration(final WebComponent c) {
		if ("hitCount".equals(c.getId()) || "pageNo".equals(c.getId()) || "linesPerPage".equals(c.getId()) || "sortOrder".equals(c.getId())) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	protected String getFormTitle() {
		return "検索結果";
	}

}
