package jp.dataforms.fw.app.enumtype.field;

import jp.dataforms.fw.field.sqltype.VarcharField;
import jp.dataforms.fw.validator.MaxLengthValidator;


/**
 * EnumTypeCodeFieldフィールドクラス。
 *
 */
public class EnumCodeField extends VarcharField {
	/**
	 * フィールド長。
	 */
	public static final int LENGTH = 64;

	/**
	 * フィールドコメント。
	 */
	private static final String COMMENT = "列挙型コード";
	/**
	 * コンストラクタ。
	 */
	public EnumCodeField() {
		super(null, LENGTH);
		this.setComment(COMMENT);
	}
	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public EnumCodeField(final String id) {
		super(id, LENGTH);
		this.setComment(COMMENT);
	}

	@Override
	protected void onBind() {
		super.onBind();
		this.addValidator(new MaxLengthValidator(this.getLength()));

	}
}
