package jp.dataforms.fw.devtool.field;

import jp.dataforms.fw.field.common.VarcharSingleSelectField;

/**
 * フィールド選択フィールド。
 *
 */
public class FieldSingleSelectField extends VarcharSingleSelectField {
	/**
	 * コンストラクタ。
	 */
	public FieldSingleSelectField() {
		this(null);
	}

	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public FieldSingleSelectField(final String id) {
		super(id, 128);
		this.setComment("フィールドID");
	}
}
