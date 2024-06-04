package jp.dataforms.fw.app.user.field;

import jp.dataforms.fw.field.common.PropertiesSingleSelectField;

/**
 * パスキー共有状況。
 */
public class SharedPasskeyField extends PropertiesSingleSelectField {
	/**
	 * コンストラクタ。
	 */
	public SharedPasskeyField() {
		super(null, 16, "sharedpasskey");
	}
}
