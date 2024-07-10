package jp.dataforms.fw.devtool.pageform.gentest;

import java.io.File;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jp.dataforms.fw.controller.Form;
import jp.dataforms.fw.devtool.javasrc.JavaSrcGenerator;
import jp.dataforms.fw.devtool.pageform.page.TestSrcGeneratorEditForm;
import jp.dataforms.fw.servlet.DataFormsServlet;
import jp.dataforms.fw.util.FileUtil;

/**
 * ページテスタージェネレータ。
 */
public class FormTestItemGenerator extends JavaSrcGenerator {
	/**
	 * Logger.
	 */
	private Logger logger = LogManager.getLogger(FormTestItemGenerator.class);
	
	/**
	 * フォーム。
	 */
	private Form form = null;
	
	/**
	 * コンストラクタ。
	 * @param form フォーム。
	 */
	public FormTestItemGenerator(final Form form) {
		this.form = form;
	}

	@Override
	protected Template getTemplate() throws Exception {
		Template tmp = new Template(this.getClass(), "template/FormTestItem.java.template");
		return tmp;
	}

	@Override
	public void generage(final Form form, final Map<String, Object> data) throws Exception {
		String packageName = (String) data.get(TestSrcGeneratorEditForm.ID_PACKAGE_NAME);
		String pageClassName = (String) data.get(TestSrcGeneratorEditForm.ID_PAGE_CLASS_NAME);

		
		String basePath = (String) data.get(TestSrcGeneratorEditForm.ID_TEST_TOOL_SRC_PATH);
		String testItemPackageName = (String) data.get(TestSrcGeneratorEditForm.ID_TEST_ITEM_PACKAGE_NAME);
		String testItemClassName = this.form.getClass().getSimpleName() + "TestItem";
		
		String srcFile = basePath + testItemPackageName.replaceAll("\\.", "/") + "/" + testItemClassName + ".java";

		String importList = "import " + packageName + "." + pageClassName + ";\n";
		importList += "import " + this.form.getClass().getName() + ";\n";
		
		Template tmp = this.getTemplate();
		tmp.replace("package", testItemPackageName);
		tmp.replace("importList", importList);
		tmp.replace("pageClass", pageClassName);
		tmp.replace("formClass", this.form.getClass().getSimpleName());
		logger.debug("srcFile=" + srcFile);
		logger.debug("src=" + tmp.getSource());
		File sf = new File(srcFile);
		File pf = sf.getParentFile();
		if (!pf.exists()) {
			pf.mkdirs();
		}
		FileUtil.writeTextFileWithBackup(sf.getAbsolutePath(), tmp.getSource(), DataFormsServlet.getEncoding());
	}
}
