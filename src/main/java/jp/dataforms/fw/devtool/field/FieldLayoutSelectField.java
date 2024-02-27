package jp.dataforms.fw.devtool.field;

import jp.dataforms.fw.field.common.PropertiesSingleSelectField;

/**
 * フィールドレイアウト選択フィールド。
 */
public class FieldLayoutSelectField extends PropertiesSingleSelectField {
	/**
	 * プロパティキー。
	 */
	private static final String KEY = "fieldlayout";

	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public FieldLayoutSelectField(final String id) {
		super(id, 16, KEY);
	}
}
