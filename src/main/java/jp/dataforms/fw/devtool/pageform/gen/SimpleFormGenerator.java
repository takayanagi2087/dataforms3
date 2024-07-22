package jp.dataforms.fw.devtool.pageform.gen;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jp.dataforms.fw.devtool.javasrc.JavaSrcGenerator;
import jp.dataforms.fw.devtool.pageform.page.DaoAndPageGeneratorEditForm;
import jp.dataforms.fw.servlet.DataFormsServlet;
import jp.dataforms.fw.util.FileUtil;

/**
 * 単純なフォームジェネレータ。
 */
public class SimpleFormGenerator extends JavaSrcGenerator {

	/**
	 * Logger.
	 */
	private static Logger logger = LogManager.getLogger(SimpleFormGenerator.class);

	/**
	 * コンストラクタ。
	 */
	public SimpleFormGenerator() {
		
	}
	
	@Override
	protected Template getTemplate() throws Exception {
		Template tmp = new Template(this.getClass(), "simpletemplate/Form.java.template");
		return tmp;
	}

	/**
	 * フォームのソースを作成します。
	 * @param data POSTされたデータ。
	 * @throws Exception 例外。
	 */
	private void generateFormClass(final Map<String, Object> data) throws Exception {
		Template tmp = this.getTemplate(); //new Template(this.getClass(), "simpletemplate/Form.java.template");
		logger.debug("page src=" + tmp.getSource());
		String packageName = (String) data.get(DaoAndPageGeneratorEditForm.ID_PACKAGE_NAME);
		String pageName = (String) data.get(DaoAndPageGeneratorEditForm.ID_PAGE_NAME);
		String formClassName = (String) data.get(DaoAndPageGeneratorEditForm.ID_FORM_CLASS_NAME);
		tmp.replace(DaoAndPageGeneratorEditForm.ID_PACKAGE_NAME, packageName);
		tmp.replace(DaoAndPageGeneratorEditForm.ID_PAGE_NAME, pageName);
		tmp.replace(DaoAndPageGeneratorEditForm.ID_FORM_CLASS_NAME, formClassName);
		logger.debug("page=" + tmp.getSource());

		String path = (String) data.get(DaoAndPageGeneratorEditForm.ID_JAVA_SOURCE_PATH);
		String formclass = packageName + "." + formClassName;
		String srcPath = path + "/" + formclass.replaceAll("\\.", "/") + ".java";
		logger.debug("srcPath=" + srcPath);
		FileUtil.writeTextFileWithBackup(srcPath, tmp.getSource(), DataFormsServlet.getEncoding());
	}


	/**
	 * ソースの生成処理。
	 */
	@Override
	public void generage(final Map<String, Object> data) throws Exception {
		logger.info("generate simple form.");
		this.generateFormClass(data);
	}
}
