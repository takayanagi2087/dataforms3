package jp.dataforms.fw.app.login.field;

import jp.dataforms.fw.field.common.PropertiesSingleSelectField;

/**
 * 認証方法選択フィールド。
 */
public class AuthMethodSelectField extends PropertiesSingleSelectField {
	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public AuthMethodSelectField(final String id) {
		super(id, 128, "authmethod");
	}
}
