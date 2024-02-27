package jp.dataforms.fw.devtool.pageform.page;

import jp.dataforms.fw.controller.QueryForm;
import jp.dataforms.fw.devtool.field.ClassNameField;
import jp.dataforms.fw.devtool.field.FunctionSelectField;
import jp.dataforms.fw.devtool.field.PackageNameField;
import jp.dataforms.fw.validator.RequiredValidator;

/**
 * ページクラス検索フォーム。
 *
 */
public class PageGeneratorQueryForm extends QueryForm {
	/**
	 * コンストラクタ。
	 */
	public PageGeneratorQueryForm() {
		this.addField(new FunctionSelectField());
		this.addField(new PackageNameField()).addValidator(new RequiredValidator());
		this.addField(new ClassNameField());
	}
}
