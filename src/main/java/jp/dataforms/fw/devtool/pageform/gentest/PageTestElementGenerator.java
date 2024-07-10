package jp.dataforms.fw.devtool.pageform.gentest;

import java.io.File;
import java.util.List;
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
public class PageTestElementGenerator extends JavaSrcGenerator {
	/**
	 * Logger.
	 */
	private Logger logger = LogManager.getLogger(PageTestElementGenerator.class);
	
	/**
	 * ページ。
	 */
	private Page page = null;
	
	/**
	 * フォームリスト。
	 */
	private List<Form> formList = null;
	
	/**
	 * コンストラクタ。
	 * @param page ページ。
	 * @param formList フォームリスト。
	 */
	public PageTestElementGenerator(final Page page, final List<Form> formList) {
		this.page = page;
		this.formList = formList;
	}

	@Override
	protected Template getTemplate() throws Exception {
		Template tmp = new Template(this.getClass(), "template/PageTestElement.java.template");
		return tmp;
	}

	/**
	 * 定数定義リストを作成します。
	 * @param formList フォームクラス。
	 * @return 定数定義リスト。
	 */
	private String getMethodList(final List<Form> formList) {
		StringBuilder sb = new StringBuilder();
		for (Form f: formList) {
			String testElementClass = f.getClass().getSimpleName() + "TestElement";
			sb.append("\t/**\n");
			sb.append("\t * " + f.getClass().getSimpleName() + "のテスト要素を取得します。\n");
			sb.append("\t * @return " + f.getClass().getSimpleName() + "のテスト要素。\n");
			sb.append("\t */\n");
			sb.append("\tpublic " + testElementClass + " get" + f.getClass().getSimpleName() + "() {\n");
			sb.append("\t\treturn this.getForm(" + testElementClass + ".ID, " + testElementClass + ".class);\n");
			sb.append("\t}\n\n");
		}
		return sb.toString();
	}

	
	@Override
	public void generage(final Form form, final Map<String, Object> data) throws Exception {
		String basePath = (String) data.get(TestSrcGeneratorEditForm.ID_TEST_TOOL_SRC_PATH);
		String testElementPackageName = (String) data.get(TestSrcGeneratorEditForm.ID_TEST_ELEMENT_PACKAGE_NAME);
		String testElementClassName = this.page.getClass().getSimpleName() + "TestElement";

		String pageName = this.page.getPageName();
		String srcFile = basePath + testElementPackageName.replaceAll("\\.", "/") + "/" + testElementClassName + ".java";
		Template tmp = this.getTemplate();
		tmp.replace("package", testElementPackageName);
		tmp.replace("pageName", pageName);
		tmp.replace("pageClass", this.page.getClass().getSimpleName());
		tmp.replace("methodList", this.getMethodList(this.formList));
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
