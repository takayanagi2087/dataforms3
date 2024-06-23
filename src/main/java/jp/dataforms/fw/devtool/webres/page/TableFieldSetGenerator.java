package jp.dataforms.fw.devtool.webres.page;

import jp.dataforms.fw.devtool.webres.gen.FieldSetGenerator;
import jp.dataforms.fw.field.base.Field;

/**
 * テーブルタグのフィールドセットジェネレータ。
 */
public class TableFieldSetGenerator extends FieldSetGenerator {
	
	/**
	 * コンストラクタ。
	 */
	public TableFieldSetGenerator() {
		
	}
	
	/**
	 * フィールドDIVのタグを生成します。
	 * @param field フィールド。
	 * @param tabs インデントじ
	 * @param fieldTag フィールドタグ。
	 * @return フィールドdivタグ。
	 */
	protected String getFieldDiv(final Field<?> field, final String tabs, final String fieldTag) {
		StringBuilder sb = new StringBuilder();
		sb.append(tabs + "\t\t\t<tr>\n");
		sb.append(tabs + "\t\t\t\t<th>" + this.getFieldLabel(field) + "</th>\n");
		sb.append(tabs + "\t\t\t\t<td>" + fieldTag + "</td>\n");
		sb.append(tabs + "\t\t\t</tr>\n");
		return sb.toString();
	}

	@Override
	public String getStartTag(final String tabs) {
		StringBuilder sb = new StringBuilder();
		sb.append(tabs + "\t<table class=\"responsive\">\n");
		sb.append(tabs + "\t\t<tbody>\n");
		return sb.toString();
	}

	@Override
	public String getEndTag(final String tabs) {
		StringBuilder sb = new StringBuilder();
		sb.append(tabs + "\t\t</tbody>\n");
		sb.append(tabs + "\t</table>\n");
		return sb.toString();
	}

	@Override
	public String getRangeFieldPair(String tabs, RangeFieldPair pair) {
		StringBuilder sb = new StringBuilder();
		sb.append(tabs + "\t\t\t<tr>\n");
		sb.append(tabs + "\t\t\t\t<th>" + pair.getComment() + "</th>\n");
		sb.append(tabs + "\t\t\t\t<td><div class=\"multiField\">\n");
		sb.append(tabs + "\t\t\t\t\t<input type=\"text\" id=\"" + pair.getFrom().getId() + "\" class=\"" + pair.getRangeFieldClass() + "\" />\n");
		sb.append(tabs + "\t\t\t\t\t～\n");
		sb.append(tabs + "\t\t\t\t\t<input type=\"text\" id=\"" + pair.getTo().getId() + "\" class=\"" + pair.getRangeFieldClass() + "\"/>\n");
		sb.append(tabs + "\t\t\t\t</div></td>\n");
		sb.append(tabs + "\t\t\t</tr>\n");
		return sb.toString();
	}
}
