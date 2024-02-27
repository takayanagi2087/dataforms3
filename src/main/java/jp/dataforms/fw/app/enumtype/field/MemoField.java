package jp.dataforms.fw.app.enumtype.field;

import jp.dataforms.fw.field.sqltype.VarcharField;
import jp.dataforms.fw.validator.MaxLengthValidator;


/**
 * MemoFieldフィールドクラス。
 *
 */
public class MemoField extends VarcharField {
	/**
	 * フィールド長。
	 */
	private static final int LENGTH = 1024;

	/**
	 * フィールドコメント。
	 */
	private static final String COMMENT = "メモ";
	/**
	 * コンストラクタ。
	 */
	public MemoField() {
		super(null, LENGTH);
		this.setComment(COMMENT);
	}
	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public MemoField(final String id) {
		super(id, LENGTH);
		this.setComment(COMMENT);
	}

	@Override
	protected void onBind() {
		super.onBind();
		this.addValidator(new MaxLengthValidator(this.getLength()));

	}
}
