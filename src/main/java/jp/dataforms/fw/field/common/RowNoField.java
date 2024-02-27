package jp.dataforms.fw.field.common;

import jp.dataforms.fw.field.sqltype.IntegerField;

/**
 * 検索結果の行番号フィールドクラス。
 *
 */
public class RowNoField extends IntegerField {
	/**
	 * コンストラクタ。
	 */
	public RowNoField() {
		super(null);
		this.setComment("No.");
	}

	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public RowNoField(final String id) {
		super(id);
		this.setComment("No.");
	}
}
