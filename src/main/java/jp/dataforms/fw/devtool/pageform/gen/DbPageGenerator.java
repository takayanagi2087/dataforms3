package jp.dataforms.fw.devtool.pageform.gen;

import java.util.Map;

import org.apache.poi.util.StringUtil;

import jp.dataforms.fw.devtool.javasrc.JavaSrcGenerator;
import jp.dataforms.fw.devtool.pageform.page.DaoAndPageGeneratorEditForm;
import jp.dataforms.fw.servlet.DataFormsServlet;
import jp.dataforms.fw.util.FileUtil;

/**
 * ページジェネレータ。
 */
public class DbPageGenerator extends JavaSrcGenerator {
	/**
	 * Logger.
	 */
//	private static Logger logger = LogManager.getLogger(DbPageGenerator.class);

	/**
	 * コンストラクタ。
	 */
	public DbPageGenerator() {
	}

	/**
	 * ページのテンプレートを取得します。
	 * @return ページテンプレート。
	 */
	protected Template getTemplate() throws Exception {
		Template tmp = new Template(this.getClass(), "template/Page.java.template");
		return tmp;
	}

	@Override
	public void generage(final Map<String, Object> data) throws Exception {
		Template tmp = this.getTemplate();
		String functionPath = (String) data.get(DaoAndPageGeneratorEditForm.ID_FUNCTION_SELECT);
		tmp.replace("functionPath", functionPath);
		String daoPackageName = (String) data.get(DaoAndPageGeneratorEditForm.ID_DAO_PACKAGE_NAME);
		String daoClassName = (String) data.get(DaoAndPageGeneratorEditForm.ID_DAO_CLASS_NAME);
		String daoFullClassName = daoPackageName + "." +  daoClassName;
		tmp.replace("daoClassFullName", daoFullClassName);
		tmp.replace("daoClass", daoClassName);
		String pageClassName = (String) data.get(DaoAndPageGeneratorEditForm.ID_PAGE_CLASS_NAME);
		tmp.replace("pageClassName", pageClassName);

		StringBuilder sb = new StringBuilder();
		String queryFormClassName = (String) data.get(DaoAndPageGeneratorEditForm.ID_QUERY_FORM_CLASS_NAME);
		if (!StringUtil.isBlank(queryFormClassName)) {
			sb.append("\t\tthis.addForm(new " + queryFormClassName + "());\n");
		}
		String queryResultFormClassName = (String) data.get(DaoAndPageGeneratorEditForm.ID_QUERY_RESULT_FORM_CLASS_NAME);
		if (!StringUtil.isBlank(queryResultFormClassName)) {
			sb.append("\t\tthis.addForm(new " + queryResultFormClassName + "());\n");
		}
		String editFormClassName = (String) data.get(DaoAndPageGeneratorEditForm.ID_EDIT_FORM_CLASS_NAME);
		if (!StringUtil.isBlank(editFormClassName)) {
			sb.append("\t\tthis.addForm(new " + editFormClassName + "());\n");
		}
		tmp.replace("formList", sb.toString());
		String path = (String) data.get(DaoAndPageGeneratorEditForm.ID_JAVA_SOURCE_PATH);
		String packageName = (String) data.get(DaoAndPageGeneratorEditForm.ID_PACKAGE_NAME);
		String pageName = (String) data.get(DaoAndPageGeneratorEditForm.ID_PAGE_NAME);
		String description = (String) data.get(DaoAndPageGeneratorEditForm.ID_DESCRIPTION);
		tmp.replace("packageName", packageName);
		tmp.replace("pageName", pageName);
		tmp.replace(DaoAndPageGeneratorEditForm.ID_DESCRIPTION, description);
		String formclass = packageName + "." + pageClassName;
		String srcPath = path + "/" + formclass.replaceAll("\\.", "/") + ".java";
		FileUtil.writeTextFileWithBackup(srcPath, tmp.getSource(), DataFormsServlet.getEncoding());

	}
}
