package jp.dataforms.fw.devtool.dao.page;

import jp.dataforms.fw.controller.QueryForm;
import jp.dataforms.fw.devtool.field.ClassNameField;
import jp.dataforms.fw.devtool.field.FunctionSelectField;
import jp.dataforms.fw.devtool.field.PackageNameField;
import jp.dataforms.fw.validator.RequiredValidator;

/**
 * Daoクラス問合せフォーム。
 *
 */
public class DaoGeneratorQueryForm extends QueryForm {
	/**
	 * コンストラクタ。
	 */
	public DaoGeneratorQueryForm() {
		this.addField(new FunctionSelectField());
		this.addField(new PackageNameField()).addValidator(new RequiredValidator());
		this.addField(new ClassNameField());
	}
}
