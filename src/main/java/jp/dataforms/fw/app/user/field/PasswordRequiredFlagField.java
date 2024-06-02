package jp.dataforms.fw.app.user.field;

import jp.dataforms.fw.field.common.FlagField;

/**
 * パスワード必須フラグフィールド。
 */
public class PasswordRequiredFlagField extends FlagField {
	/**
	 * コンストラクタ。
	 */
	public PasswordRequiredFlagField() {
		super(null);
		this.setDefaultValue("1");
	}
}
