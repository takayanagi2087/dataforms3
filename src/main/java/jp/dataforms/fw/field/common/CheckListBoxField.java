package jp.dataforms.fw.field.common;

/**
 * チェックリストボックスフィールド。
 * @param <TYPE> データ型。
 */
public class CheckListBoxField<TYPE> extends MultiSelectField<TYPE> {
	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public CheckListBoxField(final String id) {
		super(id);
		this.setHtmlFieldType(HtmlFieldType.CHECKBOX);
		this.setMatchType(MatchType.IN);
	}

	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 * @param cls 要素のクラス。
	 */
	public CheckListBoxField(final String id, final Class<?> cls) {
		super(id, cls);
		this.setHtmlFieldType(HtmlFieldType.CHECKBOX);
		this.setMatchType(MatchType.IN);
	}

}
