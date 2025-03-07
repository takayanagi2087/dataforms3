package jp.dataforms.fw.field.common;

/**
 * ラジオリストボックスフィールド。
 * @param <TYPE> データ型。
 */
public class RadioListBoxField<TYPE> extends SingleSelectField<TYPE> {
	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public RadioListBoxField(final String id) {
		super(id);
		this.setHtmlFieldType(HtmlFieldType.RADIO);
	}

	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 * @param len フィールド長。
	 */
	public RadioListBoxField(final String id, final int len) {
		super(id, len);
		this.setHtmlFieldType(HtmlFieldType.RADIO);
	}

}
