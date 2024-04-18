package jp.dataforms.fw.devtool.menu.gen;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jp.dataforms.fw.controller.Form;
import jp.dataforms.fw.devtool.base.page.DeveloperPage;
import jp.dataforms.fw.devtool.javasrc.JavaSrcGenerator;
import jp.dataforms.fw.devtool.menu.page.MenuEditForm;
import jp.dataforms.fw.devtool.menu.page.MenuTable;
import jp.dataforms.fw.servlet.DataFormsServlet;
import jp.dataforms.fw.util.FileUtil;

/**
 * AppFunctionMapクラスジェネレータ。
 */
public class AppFunctionMapGenerator extends JavaSrcGenerator {
	/**
	 * Logger.
	 */
	private static Logger logger = LogManager.getLogger(AppFunctionMapGenerator.class);

	/**
	 * ページのテンプレートを取得します。
	 * @return ページテンプレート。
	 */
	protected Template getTemplate() throws Exception {
		Template tmp = new Template(this.getClass(), "template/AppFunctionMap.java.templete");
		return tmp;
	}

	/**
	 * PathとPackage対応表のコードを作成する。
	 * @param list メニューのリスト。
	 * @return PathとPackage対応表のコード。
	 */
	private String getPathPackage(final List<Map<String, Object>> list) {
		StringBuilder sb = new StringBuilder();
		for (Map<String, Object> m: list) {
			String path = (String) m.get(MenuTable.ID_PATH);
			if (path.indexOf("/dataforms") == 0) {
				continue;
			}
			String packageName = (String) m.get(MenuTable.ID_PACKAGE_NAME);
			sb.append("\t\tthis.addPathPackage(new PathPackage(\"" + path + "\", \"" + packageName + "\"));\n");
		}
		return sb.toString();
	}
	
	/**
	 * 言語毎の名称リストを取得します。
	 * @param m メニュー名称マップ。
	 * @return 言語毎の名称リスト。
	 */
	private String getLangNameList(final Map<String, Object> m) {
		Pattern p = Pattern.compile("(.+)Name$");
		StringBuilder sb = new StringBuilder();
		Set<String> keys = m.keySet();
		for (String key: keys) {
			if (MenuTable.ID_DEFAULT_NAME.equals(key) || MenuTable.ID_PACKAGE_NAME.equals(key)) {
				continue;
			}
			Matcher matcher = p.matcher(key);
			if (matcher.find()) {
				String g = matcher.group(1);
				if (sb.length() > 0) {
					sb.append(",");
				}
				String n = (String) m.get(key);
				sb.append("\"" + g + "\\\\t" + n + "\"");
			}
		}
		return sb.toString();
	}
	
	/**
	 * メニューの追加コード取得します。
	 * @param list メニュー一覧。
	 * @return メニューの追加コード。
	 */
	private String getAppMenu(final List<Map<String, Object>> list) {
		StringBuilder sb = new StringBuilder();
		for (Map<String, Object> m: list) {
			String path = (String) m.get(MenuTable.ID_PATH);
			if (path.indexOf("/dataforms") == 0) {
				continue;
			}
			String defaultName = (String) m.get(MenuTable.ID_DEFAULT_NAME);
			String langName = (String) this.getLangNameList(m);
			sb.append("\t\tthis.addMenu(new Menu(\"" + path + "\", \"" + defaultName + "\", " + langName + "));\n");
		}
		return sb.toString();
	}
	
	/**
	 * AppFunctionMapクラスを生成します。
	 * @param form 呼び出したフォーム。
	 * @param data POSTされたデータ。
	 */
	@Override
	public void generage(Form form, Map<String, Object> data) throws Exception {
		String basePackage = (String) data.get(MenuEditForm.ID_APP_BASE_PACKAGE);
		logger.debug("basePackage=" + basePackage);
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> list = (List<Map<String, Object>>) data.get(MenuTable.ID_MENU_LIST);
		String pathPackage = this.getPathPackage(list);
		logger.debug("pathPackage=" + pathPackage);
		String menu = this.getAppMenu(list);
		logger.debug("menu=" + menu);
		Template templ = this.getTemplate();
		templ.replace("basePackage", basePackage);
		templ.replace("appPathPackage", pathPackage);
		templ.replace("appMenu", menu);
		templ.replace("appPage", "");
		String src = templ.getSource();
		logger.debug("src=" + src);
		
		String path = DeveloperPage.getJavaSourcePath();
		String formclass = basePackage + ".menu.AppFunctionMap";
		String srcPath = path + "/" + formclass.replaceAll("\\.", "/") + ".java";
		FileUtil.writeTextFileWithBackup(srcPath, templ.getSource(), DataFormsServlet.getEncoding());

		
	}
}
