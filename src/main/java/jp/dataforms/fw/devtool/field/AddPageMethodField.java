package jp.dataforms.fw.devtool.field;

import jp.dataforms.fw.field.common.PropertiesSingleSelectField;

/**
 * ページの追加方法選択フィールド。
 */
public class AddPageMethodField extends PropertiesSingleSelectField {
	/**
	 * プロパティのキー。
	 */
	private static final String KEY = "addpagemethod";

	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public AddPageMethodField(final String id) {
		super(id, 16, KEY);
	}
	
	/**
	 * コンストラクタ。
	 */
	public AddPageMethodField() {
		this(null);
	}
}
