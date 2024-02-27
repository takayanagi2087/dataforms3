package jp.dataforms.fw.app.menu.field;

import jp.dataforms.fw.field.sqltype.VarcharField;
import jp.dataforms.fw.validator.RequiredValidator;

/**
 * メニュークループ名称。
 *
 */
public class PageClassField extends VarcharField {
	/**
	 * フィールド長。
	 */
	private static final int LENGTH = 256;

	/**
	 * コメント。
	 */
	private static final String COMMENT = "メニューグループの名称.";

	/**
	 * コンストラクタ。
	 */
	public PageClassField() {
		super(null, LENGTH);
		this.setComment(COMMENT);
		this.addValidator(new RequiredValidator());
	}

	/**
	 * コンストラクタ。
	 * @param id フィールドのID。
	 */
	public PageClassField(final String id) {
		super(id, LENGTH);
		this.setComment(COMMENT);
		this.addValidator(new RequiredValidator());
	}
}
