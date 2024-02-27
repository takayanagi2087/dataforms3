package jp.dataforms.fw.field.common;

/**
 * 有無フィールドクラス。
 */
public class PresenceMultiSelectField extends PropertiesMultiSelectField {
	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public PresenceMultiSelectField(final String id) {
		super(id, "presence");
	}
}
