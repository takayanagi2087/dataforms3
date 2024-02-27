package jp.dataforms.fw.devtool.field;

import jp.dataforms.fw.field.base.TextField;

/**
 * テーブルクラス名フィールドクラス。
 *
 */
public class TableFullClassNameField extends TextField {
	/**
	 * フィールドコメント。
	 */
	private static final String COMMENT = "テーブルクラス名";
	/**
	 * コンストラクタ。
	 */
	public TableFullClassNameField() {
		this(null);
	}
	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public TableFullClassNameField(final String id) {
		super(id);
		this.setComment(COMMENT);
	}

	@Override
	protected void onBind() {
		super.onBind();
	}
}
