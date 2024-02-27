package jp.dataforms.fw.field.common;

import jp.dataforms.fw.dao.sqldatatype.SqlVarchar;


/**
 * 有無フィールドクラス。
 */
public class PresenceField extends PropertiesSingleSelectField implements SqlVarchar {
	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public PresenceField(final String id) {
		super(id, 1, "presence");
	}
}
