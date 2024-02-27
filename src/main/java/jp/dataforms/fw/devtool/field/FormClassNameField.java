package jp.dataforms.fw.devtool.field;

import jp.dataforms.fw.controller.Form;

/**
 * フォームクラス名フィールド。
 *
 */
public class FormClassNameField extends SimpleClassNameField {

	/**
	 * フィールドコメント。
	 */
	private static final String COMMENT = "フォームクラス名";

	/**
	 * コンストラクタ。
	 */
	public FormClassNameField() {
		this(null);
	}

	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public FormClassNameField(final String id) {
		super(id);
		this.addBaseClass(Form.class);
		this.setComment(COMMENT);
		this.setAutocomplete(false);
	}

	@Override
	protected String getClassNameSuffix() {
		return "Form";
	}

	@Override
	protected void onBind() {
		super.onBind();
		this.setAutocomplete(false);
	}
}
