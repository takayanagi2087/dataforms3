package jp.dataforms.fw.devtool.field;

import jp.dataforms.fw.field.common.PropertiesSingleSelectField;

/**
 * 編集フォームタイプ。
 *
 */
public class EditFormTypeField extends PropertiesSingleSelectField {
	/**
	 * コンストラクタ。
	 */
	public EditFormTypeField() {
		this(null);
	}

	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public EditFormTypeField(final String id) {
		super(id, 1, "editformtype");
		this.setComment("編集フォームタイプ");
		this.setHtmlFieldType(HtmlFieldType.SELECT);
	}

}
