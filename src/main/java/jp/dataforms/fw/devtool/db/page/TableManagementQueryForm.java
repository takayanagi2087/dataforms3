package jp.dataforms.fw.devtool.db.page;

import jp.dataforms.fw.controller.QueryForm;
import jp.dataforms.fw.devtool.field.ClassNameField;
import jp.dataforms.fw.devtool.field.FunctionSelectField;
import jp.dataforms.fw.devtool.field.PackageNameField;
import jp.dataforms.fw.validator.RequiredValidator;

/**
 * DB管理ページの検索条件フォームクラス。
 *
 */
public class TableManagementQueryForm extends QueryForm {
    /**
     * Logger.
     */
//    private static Logger log = Logger.getLogger(TableManagementQueryForm.class.getName());

    /**
	 * コンストラクタ.
	 */
	public TableManagementQueryForm() {
		this.addField(new FunctionSelectField());
		this.addField(new PackageNameField()).addValidator(new RequiredValidator());
		this.addField(new ClassNameField());
	}
}
