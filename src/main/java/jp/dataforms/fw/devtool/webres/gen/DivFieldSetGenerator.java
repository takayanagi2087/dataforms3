package jp.dataforms.fw.devtool.webres.gen;

import jp.dataforms.fw.devtool.webres.page.RangeFieldPair;
import jp.dataforms.fw.field.base.Field;

/**
 * DIVタグのフィールドセット生成クラス。
 */
public abstract class DivFieldSetGenerator extends FieldSetGenerator {
	
	/**
	 * コンストラクタ。
	 */
	public DivFieldSetGenerator() {
		
	}
	
	/**
	 * DIVのクラスを取得します。
	 * @return DIVのクラス。
	 */
	protected abstract String getDivClass();

	@Override
	public String getStartTag(String tabs) {
		return tabs + "\t<div class=\"" + this.getDivClass() + "\">\n";
	}

	@Override
	public String getEndTag(String tabs) {
		return tabs + "\t</div>\n";
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
		sb.append(tabs + "\t\t<div class=\"fieldDiv\">\n");
		sb.append(tabs + "\t\t\t<div class=\"label\"><label for=\"" + field.getId() + "\">" + this.getFieldLabel(field) + "</label></div>\n");
		sb.append(tabs + "\t\t\t<div class=\"field\">" + fieldTag + "</div>\n");
		sb.append(tabs + "\t\t</div>\n");
		return sb.toString();
	}

	/**
	 * 範囲フィールドを展開します。
	 * @param tabs タブの数。
	 * @param pair 範囲フィールドペア。
	 * @return 展開したHTML。
	 */
	@Override
	public String getRangeFieldPair(final String tabs, final RangeFieldPair pair) {
		StringBuilder sb = new StringBuilder();
		sb.append(tabs + "\t\t<div class=\"fieldDiv\">\n");
		sb.append(tabs + "\t\t\t<div class=\"label\">" + pair.getComment() + "</div>\n");
		sb.append(tabs + "\t\t\t<div class=\"field\"><div class=\"multiField\">\n");
		sb.append(tabs + "\t\t\t\t<input type=\"text\" id=\"" + pair.getFrom().getId() + "\" class=\"" + pair.getRangeFieldClass() + "\" />\n");
		sb.append(tabs + "\t\t\t\t～\n");
		sb.append(tabs + "\t\t\t\t<input type=\"text\" id=\"" + pair.getTo().getId() + "\" class=\"" + pair.getRangeFieldClass() + "\"/>\n");
		sb.append(tabs + "\t\t\t</div></div>\n");
		sb.append(tabs + "\t\t</div>\n");
		return sb.toString();
	}


}
