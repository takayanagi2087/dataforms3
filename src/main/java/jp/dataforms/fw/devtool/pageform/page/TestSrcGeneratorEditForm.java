package jp.dataforms.fw.devtool.pageform.page;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jp.dataforms.fw.controller.EditForm;
import jp.dataforms.fw.devtool.field.OverwriteModeField;
import jp.dataforms.fw.field.base.FieldList;
import jp.dataforms.fw.field.base.TextField;
import jp.dataforms.fw.htmltable.HtmlTable;
import jp.dataforms.fw.servlet.DataFormsServlet;
import jp.dataforms.fw.util.JsonUtil;

/**
 * テストソース生成フォーム。
 */
public class TestSrcGeneratorEditForm extends EditForm {
	/**
	 * Logger.
	 */
	private Logger logger = LogManager.getLogger(TestSrcGeneratorEditForm.class);

	/**
	 * ページパッケージ名フィールドID。
	 */
	public static final String ID_PACKAGE_NAME = "packageName";

	/**
	 * テストツールソースパス。
	 */
	public static final String ID_TEST_TOOL_SRC_PATH = "testToolSrcPath";

	/**
	 * ページクラス名のフィールドID。
	 */
	public static final String ID_PAGE_CLASS_NAME = "pageClassName";

	/**
	 * ページテスタークラス名のフィールドID。
	 */
	public static final String ID_PAGE_TESTER_CLASS_NAME = "pageTesterClassName";

	/**
	 * ページテスター上書きモードのフィールドID。
	 */
	public static final String ID_PAGE_TESTER_OVERWRITE_MODE = "pageTesterOverwriteMode";
	
	/**
	 * ページテスト要素クラス名のフィールドID。
	 */
	public static final String ID_PAGE_TEST_ELEMENT_CLASS_NAME = "pageTestElementClassName";

	/**
	 * ページテスト要素上書きモードフィールドID。
	 */
	public static final String ID_PAGE_TEST_ELEMENT_OVERWRITE_MODE = "pageTestElementOverwriteMode";

	/**
	 * フォームクラス名。
	 */
	public static final String ID_FORM_CLASS_NAME = "formClassName";

	/**
	 * フォームテスト要素クラス名。
	 */
	public static final String ID_FORM_TEST_ELEMENT_CLASS_NAME = "formTestElementClassName";

	/**
	 * フォームテスト要素クラス名。
	 */
	public  static final String ID_FORM_TEST_ELEMENT_OVERWRITE_MODE = "formTestElementOverwriteMode";

	/**
	 * コンストラクタ。
	 */
	public TestSrcGeneratorEditForm() {
		this.addField(new TextField(ID_TEST_TOOL_SRC_PATH)).setComment("テストツールソースパス").setReadonly(true);
		this.addField(new TextField(ID_PACKAGE_NAME)).setReadonly(true);
		this.addField(new TextField(ID_PAGE_CLASS_NAME)).setComment("ページクラス名").setReadonly(true);

		this.addField(new TextField(ID_PAGE_TESTER_CLASS_NAME)).setComment("ページテスタークラス名");
		this.addField(new OverwriteModeField(ID_PAGE_TESTER_OVERWRITE_MODE)).setComment("ページテスター上書きモード");
		
		this.addField(new TextField(ID_PAGE_TEST_ELEMENT_CLASS_NAME)).setComment("ページテスト要素クラス名");
		this.addField(new OverwriteModeField(ID_PAGE_TEST_ELEMENT_OVERWRITE_MODE)).setComment("ページテスト要素上書きモード");
		FieldList flist = new FieldList();
		flist.addField(new TextField(ID_FORM_CLASS_NAME)).setComment("フォームクラス名");
		flist.addField(new TextField(ID_FORM_TEST_ELEMENT_CLASS_NAME)).setComment("フォームテスト要素クラス名");
		flist.addField(new OverwriteModeField(ID_FORM_TEST_ELEMENT_OVERWRITE_MODE)).setComment("フォームテスト要素上書きモード");;
		HtmlTable formTable = new HtmlTable("formTable", flist);
		this.addHtmlTable(formTable);
	}
	
	@Override
	protected Map<String, Object> queryData(Map<String, Object> data) throws Exception {
		String pkg = (String) data.get(ID_PACKAGE_NAME);
		String cls = (String) data.get(ID_PAGE_CLASS_NAME);
		String classname = pkg + "." + cls;

		logger.debug("classname=" + classname);
		logger.debug("data=" + JsonUtil.encode(data, true));
		Map<String, Object> ret = new HashMap<String, Object>();
		ret.put(ID_TEST_TOOL_SRC_PATH, DataFormsServlet.getConf().getDevelopmentTool().getTestSourcePath());
		ret.put(ID_PACKAGE_NAME, pkg);
		ret.put(ID_PAGE_CLASS_NAME, cls);
		return ret;
	}

	@Override
	protected boolean isUpdate(Map<String, Object> data) throws Exception {
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}

	@Override
	protected void insertData(Map<String, Object> data) throws Exception {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	protected void updateData(Map<String, Object> data) throws Exception {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void deleteData(Map<String, Object> data) throws Exception {
		// TODO 自動生成されたメソッド・スタブ

	}

}
