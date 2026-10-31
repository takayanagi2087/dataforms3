package jp.dataforms.fw.devtool.query.dialog;

import jp.dataforms.fw.devtool.field.DataFieldTypeSelectField;
import jp.dataforms.fw.devtool.field.FieldIdField;
import jp.dataforms.fw.devtool.field.FieldSingleSelectField;
import jp.dataforms.fw.devtool.query.page.FieldListHtmlTable;
import jp.dataforms.fw.dialog.okcancel.OkCancelForm;

/**
 * ExtractFieldフォーム。
 */
public class ExtractForm extends OkCancelForm {
	/**
	 * 日付フィールドタイプID。
	 */
	private static final String ID_DATE_FIELD_TYPE = "dateFieldType";

	/**
	 * コンストラクタ。
	 */
	public ExtractForm() {
		super(null);
		this.addField(new FieldIdField()).setComment("出力フィールドID");
		this.addField(new DataFieldTypeSelectField(ID_DATE_FIELD_TYPE)).setComment("参照フィールドID");
		this.addField(new FieldSingleSelectField(FieldListHtmlTable.ID_REF_FIELD_ID, false)).setComment("参照フィールドID");
	}

}
