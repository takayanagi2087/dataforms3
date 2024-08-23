package jp.dataforms.fw.app.user.field;

import jp.dataforms.fw.field.common.FlagField;

/**
 * 多要素認証必須フラグフィールド。
 */
public class MfaRequiredFlagField extends FlagField {
	/**
	 * コンストラクタ。
	 */
	public MfaRequiredFlagField() {
		super(null);
		this.setDefaultValue("0");
		this.setComment("多要素認証必須フラグ");
	}
}
