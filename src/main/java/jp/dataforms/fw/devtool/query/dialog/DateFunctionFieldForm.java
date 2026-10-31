package jp.dataforms.fw.devtool.query.dialog;

import jp.dataforms.fw.devtool.field.DataFieldTypeSelectField;
import jp.dataforms.fw.devtool.field.FieldIdField;
import jp.dataforms.fw.devtool.field.FieldSingleSelectField;
import jp.dataforms.fw.devtool.query.page.FieldListHtmlTable;
import jp.dataforms.fw.dialog.okcancel.OkCancelForm;
import jp.dataforms.fw.field.sqltype.IntegerField;
import jp.dataforms.fw.validator.DisplayedRequiredValidator;
import jp.dataforms.fw.validator.RequiredValidator;

/**
 * DateFunctionFieldFormフォーム。
 */
public class DateFunctionFieldForm extends OkCancelForm {
	/**
	 * 日付フィールドタイプID。
	 */
	private static final String ID_DATE_FIELD_TYPE = "dateFieldType";

	/**
	 * コンストラクタ。
	 */
	public DateFunctionFieldForm() {
		super(null);
		this.addField(new FieldIdField()).setComment("出力フィールドID").addValidator(new RequiredValidator());
		this.addField(new DataFieldTypeSelectField(ID_DATE_FIELD_TYPE)).setComment("参照フィールドID").addValidator(new RequiredValidator());
		this.addField(new FieldSingleSelectField(FieldListHtmlTable.ID_REF_FIELD_ID, false)).setComment("参照フィールドID").addValidator(new RequiredValidator());
		this.addField(new IntegerField("addValue")).setComment("加算値").addValidator(new DisplayedRequiredValidator());
	}

}
