package jp.dataforms.fw.app.enumtype.field;

import jp.dataforms.fw.field.common.RecordIdField;


/**
 * ParentIdFieldフィールドクラス。
 *
 */
public class ParentIdField extends RecordIdField {

	/**
	 * フィールドコメント。
	 */
	private static final String COMMENT = "親IDフィールド";
	/**
	 * コンストラクタ。
	 */
	public ParentIdField() {
		super(null);
		this.setComment(COMMENT);
	}
	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public ParentIdField(final String id) {
		super(id);
		this.setComment(COMMENT);
	}

	@Override
	protected void onBind() {
		super.onBind();

	}
}
