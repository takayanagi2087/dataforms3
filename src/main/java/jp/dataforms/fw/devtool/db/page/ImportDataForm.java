package jp.dataforms.fw.devtool.db.page;

import jp.dataforms.fw.controller.Form;
import jp.dataforms.fw.devtool.field.PathNameField;
import jp.dataforms.fw.servlet.DataFormsServlet;

/**
 * インポートフォームクラス。
 * <pre>
 * インポートデータが存在するフォルダを指定するフォームです。
 * </pre>
 */
public class ImportDataForm extends Form {
	/**
	 * コンストラクタ。
	 */
	public ImportDataForm() {
		super("importDataForm");
		this.addField(new PathNameField());
	}


	@Override
	public void init() throws Exception {
		super.init();
		this.setFormData("pathName", DataFormsServlet.getExportImportDir());
	}
}
