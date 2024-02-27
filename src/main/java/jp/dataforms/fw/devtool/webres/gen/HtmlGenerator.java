package jp.dataforms.fw.devtool.webres.gen;

import jp.dataforms.fw.field.base.Field;

/**
 * HtmlGeneratorクラス。
 */
public class HtmlGenerator {
	/**
	 * 段付のtab数。
	 */
	private int indent = 0;

	/**
	 * コンストラクタ。
	 * @param indent 段付けのtab数。
	 */
	public HtmlGenerator(final int indent) {
		this.indent = indent;
	}

	/**
	 * 段付けのtab数を取得します。
	 * @return 段付けのtab数。
	 */
	public int getIndent() {
		return indent;
	}

	/**
	 * 段付け用のtabを取得します。
	 * @return 段付け用のtab。
	 */
	protected String getTabs() {
		String tabs = "";
		for (int i = 0; i < this.getIndent(); i++) {
			tabs += "\t";
		}
		return tabs;
	}

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
}

