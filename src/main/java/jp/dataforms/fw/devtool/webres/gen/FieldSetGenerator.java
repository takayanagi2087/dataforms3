package jp.dataforms.fw.devtool.webres.gen;

import jp.dataforms.fw.devtool.webres.page.RangeFieldPair;
import jp.dataforms.fw.field.base.Field;

/**
 * フィールドの集合ジェネレータの基本クラス。
 */
public abstract class FieldSetGenerator {
	/**
	 * コンストラクタ。
	 */
	public FieldSetGenerator() {
	}

	/**
	 * 開始タグを取得します。
	 * @param tabs 段付けようのtab。
	 * @return 開始タグ。
	 */
	public abstract String getStartTag(final String tabs);

	/**
	 * 終了タグを取得します。
	 * @param tabs 段付けようのtab。
	 * @return 終了タグ。
	 */
	public abstract String getEndTag(final String tabs);

	/**
	 * フィールドに対応したラベルを取得します。
	 * <pre>
	 * フィールドのコメントをラベルとします。
	 * フィールドのコメントがnullの場合idをラベルとします。
	 * </pre>
	 * @param field フィールド。
	 * @return ラベル。
	 */
	protected String getFieldLabel(final Field<?> field) {
		String label = field.getComment();
		if (label == null) {
			label = field.getId();
		}
		return label;
	}

	protected abstract String getFieldDiv(final Field<?> field, final String tabs, final String fieldTag);


	/**
	 * 隠しフィールドのHTMLを作成します。
	 * @param field フィールド。
	 * @param tabs 段付けようのtab。
	 * @return テキストフィールドのHTML。
	 */
	public String getHiddenFieldHtml(final Field<?> field, final String tabs) {
		StringBuilder sb = new StringBuilder();
		sb.append(tabs + "\t<input type=\"hidden\" id=\"" + field.getId() + "\" />\n");
		return sb.toString();
	}

	/**
	 * テキストフィールドのHTMLを作成します。
	 * @param field フィールド。
	 * @param tabs 段付けようのtab。
	 * @return テキストフィールドのHTML。
	 */
	public String getTextFieldHtml(final Field<?> field, final String tabs) {
		String fieldTag = "<input type=\"text\" id=\"" + field.getId() + "\" />";
		return this.getFieldDiv(field, tabs, fieldTag);
	}

	/**
	 * 日付フィールドのHTMLを作成します。
	 * @param field フィールド。
	 * @param tabs 段付けようのtab。
	 * @return 日付フィールドのHTML。
	 */
	public String getDateFieldHtml(final Field<?> field, final String tabs) {
		String fieldTag = "<div class=\"multiField\"><input type=\"text\" id=\"" + field.getId() + "\" /></div>";
		return this.getFieldDiv(field, tabs, fieldTag);
	}

	/**
	 * SelectフィールドのHTMLを作成します。
	 * @param field フィールド。
	 * @param tabs 段付けようのtab。
	 * @return テキストフィールドのHTML。
	 */
	public String getSelectFieldHtml(final Field<?> field, final String tabs) {
		String fieldTag = "<select id=\"" + field.getId() + "\"></select>";
		return this.getFieldDiv(field, tabs, fieldTag);
	}

	/**
	 * SelectフィールドのHTMLを作成します。
	 * @param field フィールド。
	 * @param tabs 段付けようのtab。
	 * @return テキストフィールドのHTML。
	 */
	public String getRadioFieldHtml(final Field<?> field, final String tabs) {
		String fieldTag = "<span><input type=\"radio\" id=\"" + field.getId() + "[0]\" name=\"" + field.getId() + "\"/></span>";
		return this.getFieldDiv(field, tabs, fieldTag);
	}

	/**
	 * チェックボックスリストフィールドのHTMLを作成します。
	 * @param field フィールド。
	 * @param tabs 段付けようのtab。
	 * @return テキストフィールドのHTML。
	 */
	public String getCheckboxArrayFieldHtml(final Field<?> field, final String tabs) {
		String fieldTag = "<span><input type=\"checkbox\" id=\"" + field.getId() + "[0]\" name=\"" + field.getId() + "\"/></span>";
		return this.getFieldDiv(field, tabs, fieldTag);
	}

	/**
	 * マルチ選択リストフィールドのHTMLを作成します。
	 * @param field フィールド。
	 * @param tabs 段付けようのtab。
	 * @return テキストフィールドのHTML。
	 */
	public String getMultiSelectFieldHtml(final Field<?> field, final String tabs) {
		String fieldTag = "<select id=\"" + field.getId() + "\" size=\"5\" multiple=\"multiple\"></select>";
		return this.getFieldDiv(field, tabs, fieldTag);
	}

	/**
	 * チェックボックスフラグフィールドのHTMLを作成します。
	 * @param field フィールド。
	 * @param tabs 段付けようのtab。
	 * @return テキストフィールドのHTML。
	 */
	public String getCheckboxFlagFieldHtml(final Field<?> field, final String tabs) {
		String fieldTag = "<input type=\"checkbox\" id=\"" + field.getId() + "\" value=\"1\"/>";
		return this.getFieldDiv(field, tabs, fieldTag);
	}

	/**
	 * テキストエリアフィールドのHTMLを作成します。
	 * @param field フィールド。
	 * @param tabs 段付けようのtab。
	 * @return テキストフィールドのHTML。
	 */
	public String getTextareaFieldHtml(final Field<?> field, final String tabs) {
		String fieldTag = "<textarea id=\"" + field.getId() + "\" rows=\"20\" cols=\"80\"></textarea>";
		return this.getFieldDiv(field, tabs, fieldTag);
	}

	/**
	 * ファイルフィールドのHTMLを作成します。
	 * @param field フィールド。
	 * @param tabs 段付けようのtab。
	 * @return テキストフィールドのHTML。
	 */
	public String getFileFieldHtml(final Field<?> field, final String tabs) {
		String fieldTag = "<input type=\"file\" id=\"" + field.getId() + "\"/>";
		return this.getFieldDiv(field, tabs, fieldTag);
	}

	/**
	 * 範囲フィールドを展開します。
	 * @param tabs タブの数。
	 * @param pair 範囲フィールドペア。
	 * @return 展開したHTML。
	 */
	public abstract String getRangeFieldPair(final String tabs, final RangeFieldPair pair);

}
