package jp.dataforms.fw.devtool.field;

import jp.dataforms.fw.dao.Table;

/**
 * テーブルクラス名フィールドクラス。
 *
 */
public class TableOrSubQueryClassNameField extends SimpleClassNameField {
	/**
	 * フィールドコメント。
	 */
	private static final String COMMENT = "テーブルクラス名";
	/**
	 * コンストラクタ。
	 */
	public TableOrSubQueryClassNameField() {
		this(null);
	}

	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public TableOrSubQueryClassNameField(final String id) {
		super(id);
		this.addBaseClass(Table.class);
		this.setComment(COMMENT);
		this.setAutocomplete(false);
	}

	@Override
	protected String getClassNameSuffix() {
		return "((Table)|(SubQuery))";
	}

	@Override
	protected void onBind() {
		super.onBind();
	}
}
