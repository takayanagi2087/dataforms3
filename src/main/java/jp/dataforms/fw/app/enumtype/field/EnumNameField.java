package jp.dataforms.fw.app.enumtype.field;

import jp.dataforms.fw.field.sqltype.VarcharField;
import jp.dataforms.fw.validator.MaxLengthValidator;


/**
 * EnumNameFieldフィールドクラス。
 *
 */
public class EnumNameField extends VarcharField {
	/**
	 * フィールド長。
	 */
	private static final int LENGTH = 128;

	/**
	 * フィールドコメント。
	 */
	private static final String COMMENT = "列挙型名称";
	/**
	 * コンストラクタ。
	 */
	public EnumNameField() {
		super(null, LENGTH);
		this.setComment(COMMENT);
	}
	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public EnumNameField(final String id) {
		super(id, LENGTH);
		this.setComment(COMMENT);
	}

	@Override
	protected void onBind() {
		super.onBind();
		this.addValidator(new MaxLengthValidator(this.getLength()));

	}
}
