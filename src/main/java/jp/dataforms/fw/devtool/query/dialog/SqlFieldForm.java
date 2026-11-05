package jp.dataforms.fw.devtool.query.dialog;

import jp.dataforms.fw.devtool.field.FieldIdField;
import jp.dataforms.fw.devtool.field.SqlFieldTypeField;
import jp.dataforms.fw.devtool.validator.ClassNameValidator;
import jp.dataforms.fw.dialog.okcancel.OkCancelForm;
import jp.dataforms.fw.field.base.TextField;
import jp.dataforms.fw.validator.RequiredValidator;

/**
 * SqlFieldダイアログ。
 */
public class SqlFieldForm extends OkCancelForm {
	/**
	 * コンストラクタ。
	 */
	public SqlFieldForm() {
		super(null);
		this.addField(new FieldIdField()).setComment("出力フィールドID").addValidator(new RequiredValidator());
		this.addField(new SqlFieldTypeField()).addValidator(new RequiredValidator());
		this.addField(new TextField("fieldClassName"))
			.addValidator(new RequiredValidator())
			.addValidator(new ClassNameValidator("Field"));
	}
}
