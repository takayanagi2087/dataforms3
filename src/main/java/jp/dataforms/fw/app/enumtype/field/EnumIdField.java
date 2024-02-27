package jp.dataforms.fw.app.enumtype.field;

import jp.dataforms.fw.field.common.RecordIdField;


/**
 * EnumIdFieldフィールドクラス。
 *
 */
public class EnumIdField extends RecordIdField {

	/**
	 * フィールドコメント。
	 */
	private static final String COMMENT = "列挙型ID";
	/**
	 * コンストラクタ。
	 */
	public EnumIdField() {
		super(null);
		this.setComment(COMMENT);
	}
	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public EnumIdField(final String id) {
		super(id);
		this.setComment(COMMENT);
	}

	@Override
	protected void onBind() {
		super.onBind();

	}
}
