package jp.dataforms.fw.app.user.field;

import jp.dataforms.fw.field.common.FlagField;

/**
 * パスキー必須フラグフィールド。
 */
public class PasskeyRequiredFlagField extends FlagField {
	/**
	 * コンストラクタ。
	 */
	public PasskeyRequiredFlagField() {
		super(null);
		this.setDefaultValue("0");
	}
}
