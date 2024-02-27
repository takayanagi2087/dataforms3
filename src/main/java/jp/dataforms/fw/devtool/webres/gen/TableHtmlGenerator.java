package jp.dataforms.fw.devtool.webres.gen;

import java.util.ArrayList;
import java.util.List;

import jp.dataforms.fw.field.base.Field;
import jp.dataforms.fw.field.common.DeleteFlagField;
import jp.dataforms.fw.field.common.FileField;
import jp.dataforms.fw.field.common.FlagField;
import jp.dataforms.fw.field.common.MultiSelectField;
import jp.dataforms.fw.field.common.SingleSelectField;
import jp.dataforms.fw.field.sqltype.ClobField;
import jp.dataforms.fw.htmltable.EditableHtmlTable;
import jp.dataforms.fw.htmltable.HtmlTable;
import jp.dataforms.fw.htmltable.PageScrollHtmlTable;

/**
 * テーブルHTMLジェネレータ。
 *
 */
public class TableHtmlGenerator extends HtmlGenerator {
	/**
	 * 対象テーブル。
	 */
	private HtmlTable table = null;

	/**
	 * 生成されたカラム数。
	 */
	private int columnCount = 0;

	/**
	 * コンストラクタ。
	 * @param table テーブル。
	 * @param indent 段付けのtab数。
	 */
	public TableHtmlGenerator(final HtmlTable table, final int indent) {
		super(indent);
		this.table = table;
	}

	/**
	 * 対象テーブルを取得します。
	 * @return 対象テーブル。
	 */
	public HtmlTable getTable() {
		return table;
	}

	/**
	 * 生成されたカラム数を取得します。
	 * @return 生成されたカラム数。
	 */
	protected int getColumnCount() {
		return columnCount;
	}

	/**
	 * カラム数を設定します。
	 * @param columnCount カラム数。
	 */
	public void setColumnCount(final int columnCount) {
		this.columnCount = columnCount;
	}

	/**
	 * HtmlTableに対応したTableHtmlGeneratorを作成します。
	 * @param table HTMLテーブル。
	 * @param indent 段付けのtab数。
	 * @return HtmlTableに対応したTableHtmlGenerator。
	 */
	public static TableHtmlGenerator newTableHtmlGenerator(final HtmlTable table, final int indent) {
		TableHtmlGenerator ret = null;
		if (table instanceof EditableHtmlTable) {
			ret = new EditableTableHtmlGenerator(table, indent);
		} else if (table instanceof PageScrollHtmlTable) {
			ret = new PageScrollTableHtmlGenerator(table, indent);
		} else {
			ret = new TableHtmlGenerator(table, indent);
		}
		return ret;
	}

	/**
	 * テーブルの開始タグを作成します。
	 * @param t HtmlTableオブジェクト。
	 * @return テーブルの開始タグ。
	 */
	protected String generateStartTableTag(final HtmlTable t) {
		return "<table id=\"" + t.getId() + "\">\n";
	}

	/**
	 * テキストフィールドのHTMLを作成します。
	 * @param tblid テーブルID。
	 * @param field フィールド。
	 * @return フィールドのタグ。
	 */
	private String getTextFieldHtml(final String tblid, final Field<?> field) {
		return "<input type=\"text\" id=\"" + tblid + "[0]." + field.getId() + "\" />";
	}

	/**
	 * SelectフィールドのHTMLを作成します。
	 * @param tblid テーブルID。
	 * @param field フィールド。
	 * @return フィールドのタグ。
	 */
	private String getSelectFieldHtml(final String tblid, final Field<?> field) {
		return "<select id=\"" + tblid + "[0]." + field.getId() + "\"></select>";
	}

	/**
	 * ラジオボタンフィールドのHTMLを作成します。
	 * @param tblid テーブルID。
	 * @param field フィールド。
	 * @return フィールドのタグ。
	 */
	private String getRadioFieldHtml(final String tblid, final Field<?> field) {
		return "<span><input type=\"radio\" id=\"" + tblid + "[0]." + field.getId() + "[0]\" name=\"" +  tblid + "[0]." + field.getId() + "\"/></span>";
	}

	/**
	 * チェックボックスフィールドのHTMLを作成します。
	 * @param tblid テーブルID。
	 * @param field フィールド。
	 * @return フィールドのタグ。
	 */
	private String getCheckboxArrayFieldHtml(final String tblid, final Field<?> field) {
		return "<span><input type=\"checkbox\" id=\"" + tblid + "[0]." + field.getId() + "[0]\" name=\"" + tblid + "[0]." +field.getId() + "\"/></span>";
	}


	/**
	 * 複数選択リストフィールドのHTMLを作成します。
	 * @param tblid テーブルID。
	 * @param field フィールド。
	 * @return フィールドのタグ。
	 */
	private String getMultiSelectFieldHtml(final String tblid, final Field<?> field) {
		return "<select id=\"" + tblid + "[0]." +field.getId() + "\" size=\"5\" multiple=\"multiple\"></select>";
	}


	/**
	 * チェックボックスフィールドのHTMLを作成します。
	 * @param tblid テーブルID。
	 * @param field フィールド。
	 * @return フィールドのタグ。
	 */
	private String getCheckboxFlagFieldHtml(final String tblid, final Field<?> field) {
		return "<input type=\"checkbox\" id=\"" + tblid + "[0]." + field.getId() + "\" value=\"1\"/>";
	}

	/**
	 * テキストエリアフィールドのHTMLを作成します。
	 * @param tblid テーブルID。
	 * @param field フィールド。
	 * @return フィールドのタグ。
	 */
	private String getTextareaFieldHtml(final String tblid, final Field<?> field) {
		return "<textarea id=\"" + tblid + "[0]." + field.getId() + "\" rows=\"20\" cols=\"80\"></textarea>";
	}


	/**
	 * ファイルフィールドのHTMLを作成します。
	 * @param tblid テーブルID。
	 * @param field フィールド。
	 * @return フィールドのタグ。
	 */
	private String getFileFieldHtml(final String tblid, final Field<?> field) {
		return "<input type=\"file\" id=\"" + tblid + "[0]." + field.getId() + "\" />";
	}


	/**
	 * 表示フィールドを生成する。
	 * @param tblid テーブルID。
	 * @param field フィールド。
	 * @return フィールドタグ。
	 */
	protected String generateVisibleField(final String tblid, final Field<?> field) {
		Field<?> c = field;
		StringBuilder sb = new StringBuilder();
		if (c.isSpanField()) {
			sb.append("<span id=\"" + tblid + "[0]." + c.getId() + "\"></span>");
		} else if (c instanceof SingleSelectField) {
			SingleSelectField<?> msf = (SingleSelectField<?>) c;
			if (msf.getHtmlFieldType() == SingleSelectField.HtmlFieldType.SELECT) {
				// selectを展開
				sb.append(this.getSelectFieldHtml(tblid, field));
			} else {
				sb.append(this.getRadioFieldHtml(tblid, field));
			}
		} else if (c instanceof MultiSelectField) {
			MultiSelectField<?> msf = (MultiSelectField<?>) c;
			if (msf.getHtmlFieldType() == MultiSelectField.HtmlFieldType.CHECKBOX) {
				// checkboxを展開
				sb.append(this.getCheckboxArrayFieldHtml(tblid, field));
			} else {
				// マルチ選択リストを展開
				sb.append(this.getMultiSelectFieldHtml(tblid, field));
			}
		} else if (c instanceof FlagField) {
			// checkboxを展開
			sb.append(this.getCheckboxFlagFieldHtml(tblid, field));
		} else if (c instanceof ClobField) {
			// textareaを展開
			sb.append(this.getTextareaFieldHtml(tblid, field));
		} else if (c instanceof FileField) {
			// fileを展開
			sb.append(this.getFileFieldHtml(tblid, field));
		} else {
			// 通常はテキストボックス。
			sb.append(this.getTextFieldHtml(tblid, field));
		}
		return sb.toString();
	}

	/**
	 * Hiddenフィールドを展開します。
	 * @param t HTMLテーブル。
	 * @param sb 出力先文字列バッファ。
	 */
	protected void addHiddenFields(final HtmlTable t, final StringBuilder sb) {
		String tabs = getTabs();
		for (Field<?> f: t.getFieldList()) {
			if (f.isHidden()) {
				sb.append(tabs + "\t\t\t\t\t\t<input type=\"hidden\" id=\"" + t.getId() + "[0]." + f.getId() + "\" />\n");
			}
		}
	}

	/**
	 * カラム幅リスト。
	 */
	private List<Integer> columnWidthList = null;


	/**
	 * カラム幅リストを取得します。
	 * @return カラム幅リスト。
	 */
	protected List<Integer> getColumnWidthList() {
		return this.columnWidthList;
	}

	/**
	 * フィールドに対応たヘッダを出力します。
	 * @param t テーブル。
	 * @param sb 出力先文字列バッファ。
	 */
	protected void addFieldHeader(final HtmlTable t, final StringBuilder sb) {
		this.columnWidthList = new ArrayList<Integer>();
		String tabs = getTabs();
		this.columnCount = 0;
		for (Field<?> f: t.getFieldList()) {
			if (f.isHidden() || f instanceof DeleteFlagField) {
				continue;
			}
			this.columnCount++;
			sb.append(tabs + "\t\t\t\t\t<th>\n");
			sb.append(tabs + "\t\t\t\t\t\t" + this.getFieldLabel(f) + "\n");
			sb.append(tabs + "\t\t\t\t\t</th>\n");
			this.columnWidthList.add(Integer.valueOf(f.calcDefaultColumnWidth()));
		}
	}

	/**
	 * テーブルヘッダを生成します。
	 * @param t テーブル。
	 * @return テーブルヘッダ。
	 */
	protected String generateTableHeader(final HtmlTable t) {
		String tabs = getTabs();
		StringBuilder sb = new StringBuilder();
		sb.append(tabs + "\t\t\t<thead>\n");
		sb.append(tabs + "\t\t\t\t<tr>\n");
		this.addFieldHeader(t, sb);
		sb.append(tabs + "\t\t\t\t</tr>\n");
		sb.append(tabs + "\t\t\t</thead>\n");
		return sb.toString();
	}


	/**
	 * テーブルボディを生成します。
	 * @param t テーブル。
	 * @return テーブルヘッダ。
	 */
	protected String generateTableBody(final HtmlTable t) {
		String tabs = getTabs();
		StringBuilder sb = new StringBuilder();
		sb.append(tabs + "\t\t\t<tbody>\n");
		sb.append(tabs + "\t\t\t\t<tr>\n");
		this.addFields(t,  sb);
		sb.append(tabs + "\t\t\t\t</tr>\n");
		sb.append(tabs + "\t\t\t</tbody>\n");
		return sb.toString();
	}

	/**
	 * テーブルフッターを作成します。
	 * @param t テーブル。
	 * @return テーブルフッターの文字列。
	 */
	protected String generateTableFooter(final HtmlTable t) {
		return "";
	}


	/**
	 * テーブルに対応したフィールドを出力します。
	 * @param t テーブル。
	 * @param sb 出力先文字列バッファ。
	 */
	protected void addFields(final HtmlTable t, final StringBuilder sb) {
		String tabs = getTabs();
		boolean first = true;
		int idx = 0;
		for (Field<?> f: t.getFieldList()) {
			if (f.isHidden() || f instanceof DeleteFlagField) {
				continue;
			}
			Integer w = this.columnWidthList.get(idx++);
			if (this.getTable().getFixedColumns() >= 0) {
				sb.append(tabs + "\t\t\t\t\t<td style=\"width: " + w + "px;\">\n");
			} else {
				sb.append(tabs + "\t\t\t\t\t<td>\n");
			}
			sb.append(tabs + "\t\t\t\t\t\t" + this.generateVisibleField(t.getId(), f)+ "\n");
			if (first) {
				this.addHiddenFields(t, sb);
				first = false;
			}
			sb.append(tabs + "\t\t\t\t\t</td>\n");
		}
	}



	/**
	 * HtmlTableよりHTMLを作成します。
	 * @return テーブルのHTML。
	 */
	public String generateTableHtml() {
		String tabs = getTabs();
		HtmlTable t = this.getTable();
		StringBuilder sb = new StringBuilder();
		sb.append(tabs + "\t<div>\n");
		sb.append("\t\t" + tabs + this.generateStartTableTag(t));
		if (t.getCaption() != null) {
			sb.append(tabs + "\t\t\t<caption>" + t.getCaption() + "</caption>\n");
		}
		sb.append(this.generateTableHeader(t));
		sb.append(this.generateTableBody(t));
		sb.append(this.generateTableFooter(t));
		sb.append("\t\t" + tabs + "</table>\n");
		sb.append(tabs + "\t</div>\n");
		return sb.toString();
	}
}

