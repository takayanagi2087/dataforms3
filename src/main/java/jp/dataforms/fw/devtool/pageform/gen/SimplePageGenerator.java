package jp.dataforms.fw.devtool.pageform.gen;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jp.dataforms.fw.controller.Form;
import jp.dataforms.fw.devtool.javasrc.JavaSrcGenerator;
import jp.dataforms.fw.devtool.pageform.page.DaoAndPageGeneratorEditForm;
import jp.dataforms.fw.servlet.DataFormsServlet;
import jp.dataforms.fw.util.FileUtil;

/**
 * 単純なページジェネレータ。
 */
public class SimplePageGenerator extends JavaSrcGenerator {

	/**
	 * Logger.
	 */
	private static Logger logger = LogManager.getLogger(SimplePageGenerator.class);

	@Override
	protected Template getTemplate() throws Exception {
		Template tmp = new Template(this.getClass(), "simpletemplate/Page.java.template");
		return tmp;
	}

	/**
	 * ページのソースを作成します。
	 * @param data POSTされたデータ。
	 * @throws Exception 例外。
	 */
	private void generatePageClass(final Map<String, Object> data) throws Exception {
		Template tmp = this.getTemplate(); // new Template(this.getClass(), "simpletemplate/Page.java.template");
		logger.debug("page src=" + tmp.getSource());
		String packageName = (String) data.get(DaoAndPageGeneratorEditForm.ID_PACKAGE_NAME);
		String pageName = (String) data.get(DaoAndPageGeneratorEditForm.ID_PAGE_NAME);
		String pageClassName = (String) data.get(DaoAndPageGeneratorEditForm.ID_PAGE_CLASS_NAME);
		String functionPath = (String) data.get(DaoAndPageGeneratorEditForm.ID_FUNCTION_SELECT);
		String formClassName = (String) data.get(DaoAndPageGeneratorEditForm.ID_FORM_CLASS_NAME);
		String description = (String) data.get(DaoAndPageGeneratorEditForm.ID_DESCRIPTION);
		tmp.replace(DaoAndPageGeneratorEditForm.ID_PACKAGE_NAME, packageName);
		tmp.replace(DaoAndPageGeneratorEditForm.ID_PAGE_NAME, pageName);
		tmp.replace(DaoAndPageGeneratorEditForm.ID_PAGE_CLASS_NAME, pageClassName);
		tmp.replace(DaoAndPageGeneratorEditForm.ID_DESCRIPTION, description);
		tmp.replace("functionPath", functionPath);
		tmp.replace("formList", "\t\tthis.addForm(new " + formClassName + "());");
		logger.debug("page=" + tmp.getSource());

		String path = (String) data.get(DaoAndPageGeneratorEditForm.ID_JAVA_SOURCE_PATH);
		String pageclass = packageName + "." + pageClassName;
		String srcPath = path + "/" + pageclass.replaceAll("\\.", "/") + ".java";
		logger.debug("srcPath=" + srcPath);
		FileUtil.writeTextFileWithBackup(srcPath, tmp.getSource(), DataFormsServlet.getEncoding());

	}

	/**
	 * ソースの生成処理。
	 */
	@Override
	public void generage(final Form form, final Map<String, Object> data) throws Exception {
		logger.info("generate simple page.");
		this.generatePageClass(data);
	}
}
