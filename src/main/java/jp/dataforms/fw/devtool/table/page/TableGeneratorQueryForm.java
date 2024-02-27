package jp.dataforms.fw.devtool.table.page;

import jp.dataforms.fw.controller.QueryForm;
import jp.dataforms.fw.devtool.field.ClassNameField;
import jp.dataforms.fw.devtool.field.FunctionSelectField;
import jp.dataforms.fw.devtool.field.PackageNameField;
import jp.dataforms.fw.validator.RequiredValidator;

/**
 * 問い合わせフォームクラス。
 */
public class TableGeneratorQueryForm extends QueryForm {
	/**
	 * コンストラクタ。
	 */
	public TableGeneratorQueryForm() {
		this.addField(new FunctionSelectField());
		this.addField(new PackageNameField()).addValidator(new RequiredValidator());
		this.addField(new ClassNameField());
	}
}
