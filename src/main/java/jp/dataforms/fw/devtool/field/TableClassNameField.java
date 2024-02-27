package jp.dataforms.fw.devtool.field;

import jp.dataforms.fw.dao.Table;

/**
 * テーブルクラス名フィールドクラス。
 *
 */
public class TableClassNameField extends SimpleClassNameField {
	/**
	 * フィールドコメント。
	 */
	private static final String COMMENT = "テーブルクラス名";
	/**
	 * コンストラクタ。
	 */
	public TableClassNameField() {
		this(null);
	}

	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public TableClassNameField(final String id) {
		super(id);
		this.addBaseClass(Table.class);
		this.setComment(COMMENT);
		this.setAutocomplete(false);
	}

	@Override
	protected String getClassNameSuffix() {
		return "Table";
	}

	@Override
	protected void onBind() {
		super.onBind();
	}
}
