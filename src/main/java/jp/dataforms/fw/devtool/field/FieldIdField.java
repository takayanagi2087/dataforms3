package jp.dataforms.fw.devtool.field;

import jp.dataforms.fw.field.sqltype.VarcharField;

/**
 * フィールドIDフィールドのクラス。
 *
 */
public class FieldIdField extends VarcharField {
	/**
	 * 項目長。
	 */
	private static final int LENGTH = 256;
	/**
	 * フィールドコメント。
	 */
	private static final String COMMENT = "フィールドID";

	/**
	 * コンストラクタ。
	 */
	public FieldIdField() {
		super(null, LENGTH);
		this.setComment(COMMENT);
	}

	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public FieldIdField(final String id) {
		super(id, LENGTH);
		this.setComment(COMMENT);
	}
}
