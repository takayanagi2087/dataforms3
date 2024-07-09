package jp.dataforms.fw.devtool.pageform.gen;

import java.io.File;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jp.dataforms.fw.controller.Form;
import jp.dataforms.fw.controller.Page;
import jp.dataforms.fw.devtool.javasrc.JavaSrcGenerator;
import jp.dataforms.fw.devtool.pageform.page.TestSrcGeneratorEditForm;
import jp.dataforms.fw.servlet.DataFormsServlet;
import jp.dataforms.fw.util.FileUtil;

/**
 * ページテスタージェネレータ。
 */
public class PageTesterGenerator extends JavaSrcGenerator {
	/**
	 * Logger.
	 */
	private Logger logger = LogManager.getLogger(PageTesterGenerator.class);
	
	/**
	 * ページ。
	 * 
	 */
	private Page page = null;
	
	/**
	 * コンストラクタ。
	 * @param page ページ。
	 */
	public PageTesterGenerator(final Page page) {
		this.page = page;
	}

	@Override
	protected Template getTemplate() throws Exception {
		Template tmp = new Template(this.getClass(), "template/PageTester.java.templete");
		return tmp;
	}

	@Override
	public void generage(final Form form, final Map<String, Object> data) throws Exception {
		String basePath = (String) data.get(TestSrcGeneratorEditForm.ID_TEST_TOOL_SRC_PATH);
		String packageName = (String) data.get(TestSrcGeneratorEditForm.ID_PACKAGE_NAME);
		String testerPackageName = (String) data.get(TestSrcGeneratorEditForm.ID_TESTER_PACKAGE_NAME);
		String pageClass = (String) data.get(TestSrcGeneratorEditForm.ID_PAGE_CLASS_NAME);
		String pageTesterClass = (String) data.get(TestSrcGeneratorEditForm.ID_PAGE_TESTER_CLASS_NAME);

		String pageName = this.page.getPageName();
		
		String srcFile = basePath + testerPackageName.replaceAll("\\.", "/") + "/" + pageTesterClass + ".java";
		String importList = "import " + packageName  + "." + pageClass + ";";
		Template tmp = this.getTemplate();
		tmp.replace("importList", importList);
		tmp.replace("pageName", pageName);
		tmp.replace("pageTesterClass", pageTesterClass);
		tmp.replace("pageClass", pageClass);
		tmp.replace("package", testerPackageName);
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
