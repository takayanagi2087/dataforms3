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
public class SampleFormTestItemGenerator extends JavaSrcGenerator {
	/**
	 * Logger.
	 */
	private Logger logger = LogManager.getLogger(SampleFormTestItemGenerator.class);
	
	/**
	 * フォーム。
	 */
	private Form form = null;
	
	/**
	 * コンストラクタ。
	 * @param form フォーム。
	 */
	public SampleFormTestItemGenerator(final Form form) {
		this.form = form;
	}

	@Override
	protected Template getTemplate() throws Exception {
		Template tmp = new Template(this.getClass(), "template/SampleFormTestItem.java.template");
		return tmp;
	}

	@Override
	public void generage(final Form form, final Map<String, Object> data) throws Exception {
		// String packageName = (String) data.get(TestSrcGeneratorEditForm.ID_PACKAGE_NAME);
		String pageClassName = (String) data.get(TestSrcGeneratorEditForm.ID_PAGE_CLASS_NAME);

		
		
		String basePath = (String) data.get(TestSrcGeneratorEditForm.ID_TEST_TOOL_SRC_PATH);
		String testItemPackageName = (String) data.get(TestSrcGeneratorEditForm.ID_TEST_ITEM_PACKAGE_NAME);
		String testItemClassName = this.form.getClass().getSimpleName() + "SampleTestItem";
		
		String srcFile = basePath + testItemPackageName.replaceAll("\\.", "/") + "/" + testItemClassName + ".java";

		String testElementPackageName = (String) data.get(TestSrcGeneratorEditForm.ID_TEST_ELEMENT_PACKAGE_NAME);
		String testElementClassName = this.form.getClass().getSimpleName() + "TestElement";
		String pageTestElementClassName = (String) data.get(TestSrcGeneratorEditForm.ID_PAGE_TEST_ELEMENT_CLASS_NAME);
		
//		String importList = "import " + packageName + "." + pageClassName + ";\n";
//		importList += "import " + this.form.getClass().getName() + ";\n";
		String importList = "import " + testElementPackageName + "." + pageTestElementClassName + ";\n";
		importList += "import " + testElementPackageName + "." + testElementClassName + ";\n";
		
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
