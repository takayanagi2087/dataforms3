package jp.dataforms.fw.devtool.webres.page;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jp.dataforms.fw.app.enumtype.dao.EnumDao;
import jp.dataforms.fw.controller.QueryForm;
import jp.dataforms.fw.devtool.base.page.DeveloperPage;
import jp.dataforms.fw.devtool.field.ClassNameField;
import jp.dataforms.fw.devtool.field.FunctionSelectField;
import jp.dataforms.fw.devtool.field.PackageNameField;
import jp.dataforms.fw.devtool.field.PageClassNameField;
import jp.dataforms.fw.devtool.field.WebComponentTypeListField;
import jp.dataforms.fw.devtool.field.WebSourcePathField;
import jp.dataforms.fw.field.common.FlagField;
import jp.dataforms.fw.validator.RequiredValidator;

/**
 * 問い合わせフォームクラス。
 */
public class WebResourceQueryForm extends QueryForm {
	/**
	 * コンストラクタ。
	 */
	public WebResourceQueryForm() {
		this.addField(new WebSourcePathField()).setReadonly(true);
		FunctionSelectField funcsel = new FunctionSelectField();
//		funcsel.setPackageOption("page");
		this.addField(funcsel);
		this.addField(new PackageNameField()).addValidator(new RequiredValidator());
		this.addField(new PageClassNameField()).setAutocomplete(true).setRelationDataAcquisition(true);
		this.addField(new WebComponentTypeListField());
		this.addField(new FlagField("generatableOnly"));
		this.addField(new ClassNameField());
	}

	@Override
	public void init() throws Exception {
		super.init();
		EnumDao dao = new EnumDao(this);
		List<Map<String, Object>> list = dao.getOptionList("webCompType", this.getPage().getCurrentLanguage());
		List<String> tlist = new ArrayList<String>();
		for (Map<String, Object> m: list) {
			tlist.add((String) m.get("value"));
		}
		this.setFormData("webSourcePath", DeveloperPage.getWebSourcePath());
		this.setFormData("webComponentTypeList", tlist);
		this.setFormData("generatableOnly", "0");
	}
}
