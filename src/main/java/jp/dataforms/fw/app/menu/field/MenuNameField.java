package jp.dataforms.fw.app.menu.field;

import jp.dataforms.fw.field.sqltype.VarcharField;
import jp.dataforms.fw.validator.RequiredValidator;

/**
 * メニュー名称フィールドクラス。
 *
 */
public class MenuNameField extends VarcharField {

	/**
	 * フィールド長。
	 */
	private static final int LENGTH = 64;

	/**
	 * コメント。
	 */
	private static final String COMMENT = "メニューの名称.";

	/**
	 * コンストラクタ。
	 */
	public MenuNameField() {
		super(null, LENGTH);
		this.setComment(COMMENT);
		this.addValidator(new RequiredValidator());
	}

	/**
	 * コンストラクタ。
	 * @param id フィールドのID。
	 */
	public MenuNameField(final String id) {
		super(id, LENGTH);
		this.setComment(COMMENT);
		this.addValidator(new RequiredValidator());
	}
}
