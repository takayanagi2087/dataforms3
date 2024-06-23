package jp.dataforms.fw.devtool.menu.gen;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jp.dataforms.fw.controller.Form;
import jp.dataforms.fw.controller.Page;
import jp.dataforms.fw.controller.WebComponent;
import jp.dataforms.fw.devtool.base.page.DeveloperPage;
import jp.dataforms.fw.devtool.javasrc.JavaSrcGenerator;
import jp.dataforms.fw.devtool.menu.page.MenuEditForm;
import jp.dataforms.fw.devtool.menu.page.MenuTable;
import jp.dataforms.fw.menu.FunctionMap;
import jp.dataforms.fw.menu.FunctionMap.Menu;
import jp.dataforms.fw.menu.FunctionMap.PageInfo;
import jp.dataforms.fw.servlet.DataFormsServlet;
import jp.dataforms.fw.util.ClassFinder;
import jp.dataforms.fw.util.FileUtil;

/**
 * AppFunctionMapクラスジェネレータ。
 * 
 */
public class AppFunctionMapGenerator extends JavaSrcGenerator {
	/**
	 * Logger.
	 */
	private static Logger logger = LogManager.getLogger(AppFunctionMapGenerator.class);

	
	/**
	 * コンストラクタ。
	 */
	public AppFunctionMapGenerator() {
		
	}
	
	/**
	 * ページのテンプレートを取得します。
	 * @return ページテンプレート。
	 * @throws Exception 例外。
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
	 * FunctionMapのインスタンスを取得します。
	 * @param mapclass FunctionMapのクラス名。
	 * @return FunctionMapのインスタンス。
	 * @throws Exception 例外。
	 */
	private FunctionMap getFunctionMapInstance(final String mapclass) throws Exception {
		FunctionMap fmap = null;
		try {
			Class<?> mcls = Class.forName(mapclass);
			if (mcls != null) {
				fmap = (FunctionMap) mcls.getConstructor().newInstance();
			}
		} catch (ClassNotFoundException e) {
			;
		}
		return fmap;
	}
	
	
	/**
	 * FunctionMap中のページクラス名のリストを取得します。
	 * @param fmap FunctionMapクラスのインスタンス。
	 * @return FunctionMap中のページクラス名のリスト。
	 * @throws Exception 例外。
	 */
	private List<String> getMapPageList(final FunctionMap fmap) throws Exception {
		List<String> ret = new ArrayList<String>();
		if (fmap != null) {
			List<PageInfo> list = fmap.getPageList();
			for (PageInfo pi: list) {
				if (pi.getMenuPath().indexOf("/dataforms") == 0) {
					continue;
				}
				ret.add(pi.getPageClass());
			}
		}
		return ret;
	}
	
	private String getAddPageCode(final String mapclass, final List<Map<String, Object>> menuList) throws Exception {
		FunctionMap fmap = this.getFunctionMapInstance(mapclass);
		List<String> oldlist = this.getMapPageList(fmap);
		ClassFinder finder = new ClassFinder();
		StringBuilder sb = new StringBuilder();
		if (fmap != null) {
			for (Menu m: fmap.getMenuList()) {
				String path = m.getPath();
				if (path.indexOf("/dataforms") == 0) {
					continue;
				}
				String pkg = fmap.getPackage(m);
				logger.debug("pkg=" + pkg);
				List<Class<?>> list =  finder.findClasses(pkg, Page.class);
				for (Class<?> cls: list) {
					@SuppressWarnings("unchecked")
					Class<? extends WebComponent> pcls = (Class<? extends WebComponent>) cls;
					String cn = pcls.getName();
					if (oldlist.indexOf(cn) < 0) {
						oldlist.add(cn);
					}
				}
			}
			for (String cn: oldlist) {
				String code = "\t\tthis.addPage(new PageInfo(" + cn + ".class));\n";
				sb.append(code);
			}
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
		String genAddPageCode = (String) data.get(MenuEditForm.ID_GEN_ADD_PAGE_CODE);
		logger.debug("basePackage=" + basePackage);
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> list = (List<Map<String, Object>>) data.get(MenuTable.ID_MENU_LIST);
		String pathPackage = this.getPathPackage(list);
		logger.debug("pathPackage=" + pathPackage);
		String menu = this.getAppMenu(list);
		logger.debug("menu=" + menu);
		String mapclass = basePackage + ".menu.AppFunctionMap";
		Template templ = this.getTemplate();
		templ.replace("basePackage", basePackage);
		templ.replace("appPathPackage", pathPackage);
		templ.replace("appMenu", menu);
		if ("1".equals(genAddPageCode)) {
			templ.replace("genAddPageCode", "true");
		} else {
			templ.replace("genAddPageCode", "false");
		}
		if ("1".equals(genAddPageCode)) {
			String code = this.getAddPageCode(mapclass, list);
			templ.replace("appPage", code);
		} else {
			templ.replace("appPage", "");
		}
		String src = templ.getSource();
		logger.debug("src=" + src);
		
		String path = DeveloperPage.getJavaSourcePath();
		String srcPath = path + "/" + mapclass.replaceAll("\\.", "/") + ".java";
		FileUtil.writeTextFileWithBackup(srcPath, templ.getSource(), DataFormsServlet.getEncoding());
	}
}
