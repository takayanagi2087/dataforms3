package jp.dataforms.fw.devtool.query.page;

import jp.dataforms.fw.controller.QueryForm;
import jp.dataforms.fw.devtool.field.FunctionSelectField;
import jp.dataforms.fw.devtool.field.PackageNameField;
import jp.dataforms.fw.devtool.field.QueryClassNameField;
import jp.dataforms.fw.validator.RequiredValidator;

/**
 * 問い合わせフォームクラス。
 */
public class QueryGeneratorQueryForm extends QueryForm {

	/**
	 * コンストラクタ。
	 */
	public QueryGeneratorQueryForm() {
		this.addField(new FunctionSelectField());
		this.addField(new PackageNameField()).addValidator(new RequiredValidator());
		this.addField(new QueryClassNameField("queryClassName")).setAutocomplete(true).setRelationDataAcquisition(true);
	}
}
