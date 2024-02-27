package jp.dataforms.fw.devtool.field;

import jp.dataforms.fw.field.base.Field;

/**
 * フィールドクラス名フィールドクラス。
 *
 */
public class FieldClassNameField extends SimpleClassNameField {
	/**
	 * autocompleteの例外文字列。
	 */
	private static final String EXCEPTION_PATTERN = "dataforms\\.field\\.sqlfunc";
	/**
	 * フィールドコメント。
	 */
	private static final String COMMENT = "フィールドクラス名";
	/**
	 * コンストラクタ。
	 */
	public FieldClassNameField() {
		this(null);
	}
	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public FieldClassNameField(final String id) {
		super(id);
		this.addBaseClass(Field.class);
		this.setComment(COMMENT);
		this.addExceptionPattern(EXCEPTION_PATTERN);
	}

	@Override
	protected String getClassNameSuffix() {
		return "Field";
	}

	@Override
	protected void onBind() {
		super.onBind();
		this.setAutocomplete(true);
		this.setRelationDataAcquisition(true);
	}
}
