package jp.dataforms.fw.devtool.webres.gen;

import jp.dataforms.fw.field.base.Field;
import jp.dataforms.fw.field.common.DeleteFlagField;
import jp.dataforms.fw.field.common.RowNoField;
import jp.dataforms.fw.htmltable.HtmlTable;

/**
 * ページスクロールテーブルHTMLジェネレータ。
 *
 */
public class PageScrollTableHtmlGenerator extends TableHtmlGenerator {
	/**
	 * コンストラクタ。
	 * @param table テーブル。
	 * @param indent 段付けのtab数。
	 */
	public PageScrollTableHtmlGenerator(final HtmlTable table, final int indent) {
		super(table, indent);
	}

	@Override
	protected void addFieldHeader(final HtmlTable t, final StringBuilder sb) {
		super.addFieldHeader(t, sb);
		String tabs = this.getTabs();
		sb.append(tabs + "\t\t\t\t\t<th>\n");
		sb.append(tabs + "\t\t\t\t\t\t操作\n");
		sb.append(tabs + "\t\t\t\t\t</th>\n");
		this.setColumnCount(this.getColumnCount() + 1);
	}

	@Override
	protected void addFields(final HtmlTable t, final StringBuilder sb) {
		String tabs = this.getTabs();
		boolean first = true;
		boolean genlink = true;
		int idx = 0;
		for (Field<?> f: t.getFieldList()) {
			if (f.isHidden() || f instanceof DeleteFlagField) {
				continue;
			}
			Integer w = this.getColumnWidthList().get(idx++);
			if (this.getTable().getFixedColumns() >= 0) {
				if (f instanceof RowNoField) {
					sb.append(tabs + "\t\t\t\t\t<td class=\"rowno\">\n");
				} else {
					sb.append(tabs + "\t\t\t\t\t<td style=\"width: " + w + "px;\">\n");
				}
			} else {
				sb.append(tabs + "\t\t\t\t\t<td>\n");
			}
			if (first) {
				sb.append(tabs + "\t\t\t\t\t\t" + this.generateVisibleField(t.getId(), f) + "\n");
				this.addHiddenFields(t, sb);
				first = false;
			} else if (genlink && !first) {
				sb.append(tabs + "\t\t\t\t\t\t" +
						"<a id=\"" + t.getId() + "[0].updateButton\" href=\"javascript:void(0);\">" +
						this.generateVisibleField(t.getId(), f) +
						"</a>" +
						 "\n");
				genlink = false;
			} else {
				sb.append(tabs + "\t\t\t\t\t\t" + this.generateVisibleField(t.getId(), f)+ "\n");
			}
			sb.append(tabs + "\t\t\t\t\t</td>\n");
		}
		if (this.getTable().getFixedColumns() >= 0) {
			sb.append(tabs + "\t\t\t\t\t<td style=\"width: 164px;\">\n");
		} else {
			sb.append(tabs + "\t\t\t\t\t<td>\n");
		}
		sb.append(tabs + "\t\t\t\t\t\t<input type=\"button\" id=\"" + t.getId() + "[0].viewButton\" value=\"表示\">\n");
		sb.append(tabs + "\t\t\t\t\t\t<input type=\"button\" id=\"" + t.getId() + "[0].referButton\" value=\"参照登録\">\n");
		sb.append(tabs + "\t\t\t\t\t\t<input type=\"button\" id=\"" + t.getId() + "[0].deleteButton\" value=\"削除\">\n");
		sb.append(tabs + "\t\t\t\t\t</td>\n");
	}
}

