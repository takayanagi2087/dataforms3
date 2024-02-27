package jp.dataforms.fw.devtool.webres.gen;

import jp.dataforms.fw.controller.Form;
import jp.dataforms.fw.controller.WebComponent;
import jp.dataforms.fw.field.base.Field;
import jp.dataforms.fw.htmltable.EditableHtmlTable;

/**
 * 問い合わせフォームHTMLジェネレータ。
 *
 */
public class EditFormHtmlGenerator extends FormHtmlGenerator {
	/**
	 * コンストラクタ。
	 *
	 * @param form フォーム。
	 * @param indent 段付けのtab数。
	 */
	public EditFormHtmlGenerator(final Form form, final int indent) {
		super(form, indent);

	}

	/**
	 * フォーム直下にフィールドを検索します。
	 * @param form フォーム。
	 * @param field 検索するフィールド。
	 * @return 見つかったフィールド。
	 */
	private Field<?> findField(final Form form, final Field<?> field) {
		for (WebComponent f: form.getComponentList()) {
			if (f instanceof Field) {
				if (f.getClass().getName().equals(field.getClass().getName())) {
					if (f.getId().equals(field.getId())) {
						return (Field<?>) f;
					}
				}
			}
		}
		return null;
	}


	/**
	 * テーブル中のフィールドと同じフィールドがフォーム直下に存在した場合、そのフィールドをhiddenに設定します。
	 * @param form フォーム。
	 * @param table テーブル。
	 */
	private void hideDuplicateField(final Form form, final EditableHtmlTable table) {
		for (Field<?> f: table.getFieldList()) {
			Field<?> ff = this.findField(form, f);
			if (ff != null) {
				f.setHidden(true);
			}
		}
	}

	/**
	 * テーブル中のフィールドの可視性を設定します。
	 * @param form フォーム。
	 */
	private void setTableFieldVisibility(final Form form) {
		for (WebComponent c: form.getComponentList()) {
			if (c instanceof EditableHtmlTable) {
				EditableHtmlTable table = (EditableHtmlTable) c;
				this.hideDuplicateField(form, table);
			}
		}
	}

	@Override
	protected void generateTable(final Form f, final StringBuilder sb) {
		this.setTableFieldVisibility(f);
		super.generateTable(f, sb);
	}

	@Override
	protected String getFormTitle() {
		return "<span id=\"editFormTitle\"></span>";
	}

	@Override
	protected String getFormButtionHtml() {
		String tabs = this.getTabs();
		String ret = tabs + "\t<input type=\"button\" id=\"confirmButton\" class=\"largeButton\" value=\"確認\"/>\n" +
				tabs + "\t<input type=\"button\" id=\"saveButton\" class=\"largeButton\" value=\"登録\"/>\n" +
				tabs + "\t<input type=\"button\" id=\"resetButton\" class=\"largeButton\" value=\"リセット\"/>\n" +
				tabs + "\t<input type=\"button\" id=\"deleteButton\" class=\"largeButton\" value=\"削除\"/>\n" +
				tabs + "\t<input type=\"button\" id=\"backButton\" class=\"largeButton\" value=\"戻る\"/>\n";
		return ret;
	}

}

