package jp.dataforms.fw.devtool.webres.gen;

import jp.dataforms.fw.htmltable.HtmlTable;

/**
 * 編集可能テーブルHTMLジェネレータ。
 *
 */
public class EditableTableHtmlGenerator extends TableHtmlGenerator {
	/**
	 * コンストラクタ。
	 * @param table テーブル。
	 * @param indent 段付けのtab数。
	 */
	public EditableTableHtmlGenerator(final HtmlTable table, final int indent) {
		super(table, indent);
	}

	@Override
	protected String generateStartTableTag(final HtmlTable t) {
		return "<table id=\"" + t.getId() + "\" class=\"editableTable\">\n";
	}

	@Override
	protected void addFieldHeader(final HtmlTable t, final StringBuilder sb) {
		String tabs = this.getTabs();
		sb.append(tabs + "\t\t\t\t\t<th colspan=\"2\" nowrap>\n");
		sb.append(tabs + "\t\t\t\t\t\t\n");
		sb.append(tabs + "\t\t\t\t\t</th>\n");
		sb.append(tabs + "\t\t\t\t\t<th nowrap>\n");
		sb.append(tabs + "\t\t\t\t\t\tNo.\n");
		sb.append(tabs + "\t\t\t\t\t</th>\n");
		super.addFieldHeader(t, sb);
		this.setColumnCount(this.getColumnCount() + 3);
	}

	@Override
	protected void addFields(final HtmlTable t, final StringBuilder sb) {
		String tabs = this.getTabs();
		sb.append(tabs + "\t\t\t\t\t<td class=\"buttonColumn\">\n");
		sb.append(tabs + "\t\t\t\t\t\t<input type=\"button\" id=\"" + t.getId() + "[0].addButton\" value=\"+\"/>\n");
		sb.append(tabs + "\t\t\t\t\t</td>\n");
		sb.append(tabs + "\t\t\t\t\t<td class=\"buttonColumn\">\n");
		sb.append(tabs + "\t\t\t\t\t\t<input type=\"button\" id=\"" + t.getId() + "[0].deleteButton\" value=\"-\"/>\n");
		sb.append(tabs + "\t\t\t\t\t</td>\n");
		sb.append(tabs + "\t\t\t\t\t<td style=\"text-align: right;\" class=\"rowno\">\n");
		sb.append(tabs + "\t\t\t\t\t\t<span id=\"" + t.getId() + "[0].no\"></span>\n");
		sb.append(tabs + "\t\t\t\t\t</td>\n");
		super.addFields(t, sb);

	}

	@Override
	protected String generateTableFooter(final HtmlTable t) {
		StringBuilder sb = new StringBuilder();
		String tabs = this.getTabs();
		sb.append(tabs + "\t\t\t<tfoot>\n");
		sb.append(tabs + "\t\t\t\t<tr>\n");
		sb.append(tabs + "\t\t\t\t\t<th class=\"buttonColumn\">\n");
		sb.append(tabs + "\t\t\t\t\t\t<input type=\"button\" id=\"" + t.getId() + ".addButton\" value=\"+\"/>\n");
		sb.append(tabs + "\t\t\t\t\t</th>\n");
		sb.append(tabs + "\t\t\t\t\t<th colspan=\"" + (this.getColumnCount() - 1) + "\">\n");
		sb.append(tabs + "\t\t\t\t\t</th>\n");
		sb.append(tabs + "\t\t\t\t</tr>\n");
		sb.append(tabs + "\t\t\t</tfoot>\n");
		return sb.toString();
	}

	@Override
	public String generateTableHtml() {
		StringBuilder sb = new StringBuilder();
//		String tabs = this.getTabs();
//		sb.append(tabs + "<div style=\"overflow-x:scroll;width:100%\">\n");
		sb.append(super.generateTableHtml());
//		sb.append(tabs + "</div>\n");
		return sb.toString();
	}
}
