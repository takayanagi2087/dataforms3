package jp.dataforms.fw.menu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jp.dataforms.fw.annotation.ApplicationFunctionMap;
import jp.dataforms.fw.controller.Page;
import jp.dataforms.fw.controller.WebComponent;
import jp.dataforms.fw.devtool.menu.page.MenuEditForm;
import jp.dataforms.fw.devtool.menu.page.MenuTable;
import jp.dataforms.fw.servlet.DataFormsServlet;
import jp.dataforms.fw.util.ClassFinder;
import jp.dataforms.fw.util.HtmlUtil;
import lombok.Getter;
import lombok.Setter;

/**
 *  Pathとパッケージの変換を行うクラス。
 */
public class FunctionMap {
	
	/**
	 * Logger.
	 */
	private static Logger logger = LogManager.getLogger(FunctionMap.class);
	
	/**
	 * Path-Package対応表。
	 */
	public class PathPackage {
		/**
		 * Path。
		 */
		@Setter
		@Getter
		private String path = null;
		/**
		 * 対応するパッケージ。
		 */
		@Setter
		@Getter
		private String basePackage = null;
		/**
		 * コンストラクタ。
		 * @param path パス。
		 * @param basePackage 基本パッケージ。
		 */
		public PathPackage(final String path, final String basePackage) {
			this.path = path;
			this.basePackage = basePackage;
		}
	}
	
	/**
	 * Path-Packageの対応表。
	 */
	@Setter
	@Getter
	private List<PathPackage> pathPackageList = null;
	
	
	/**
	 * Path-Package対応表を追加します。
	 * @param pathPackage Pathに対応するJavaのパッケージ情報。
	 */
	public void addPathPackage(final PathPackage pathPackage) {
		if (this.pathPackageList == null) {
			this.pathPackageList = new ArrayList<PathPackage>();
		}
		this.pathPackageList.add(pathPackage);
	}
	
	
	/**
	 * フレームワークのPath-Package対応表の登録を行う。
	 */
	protected void addFwPathPackage() {
		this.addPathPackage(new PathPackage("/dataforms/devtool", WebComponent.BASE_PACKAGE + ".devtool"));
		this.addPathPackage(new PathPackage("/dataforms/app", WebComponent.BASE_PACKAGE + ".app"));
		this.addPathPackage(new PathPackage("/dataforms", WebComponent.BASE_PACKAGE));
	}

	/**
	 * アプリケーションのPath-Package対応表の登録を行う。
	 */
	protected void addAppPathPackage() {
	}

	/**
	 * メニュークラス。
	 */
	public class Menu {
		/**
		 * 言語名称マップ。
		 */
		@Setter
		@Getter
		private LangNameMap langNameMap = null;
		/**
		 * メニューID。
		 */
		@Setter
		@Getter
		private String path = null;
		
		/**
		 * コンストラクタ。
		 * @param path メニューID。
		 * @param names 言語毎の名称リスト。
		 * <pre>
		 *  国コードを指定する場合次のように\tで区切って指定	"ja\tname"
		 *  デフォルト言語の場合名称のみを指定					"name"
		 * </pre>
		 */
		public Menu(final String path, final String ...names) {
			this.path = path;
			this.langNameMap = new LangNameMap(names);
		}
		
		/**
		 * メニューの名称を取得する。
		 * @param lang 言語コード。
		 * @return 名称。
		 */
		public String getName(final String lang) {
			String name = this.langNameMap.get(lang);
			if (name == null) {
				name = this.getName(LangNameMap.LANG_DEFAULT);
			}
			return name;
		}
	}
	
	/**
	 * メニューリスト。
	 */
	@Setter
	@Getter
	private List<Menu> menuList = null;
	
	/**
	 * メニューを追加する。
	 * @param menu Pathに対応するJavaのパッケージ情報。
	 */
	public void addMenu(final Menu menu) {
		if (this.menuList == null) {
			this.menuList = new ArrayList<Menu>();
		}
		this.menuList.add(menu);
	}
	
	/**
	 * フレームワークのメニューを追加します。
	 */
	private void addFwMenu() {
		this.addMenu(new Menu("/dataforms/app", "Basic Function", "ja\t基本機能"));
		this.addMenu(new Menu("/dataforms/devtool", "Developer tool", "ja\t開発ツール"));
	}
	
	/**
	 * アプリケーションのメニューを追加します。
	 */
	protected void addAppMenu() {
	}
	
	/**
	 * ページ情報。
	 */
	public class PageInfo {
		/**
		 * メニューID。
		 */
		@Getter
		private String menuPath = null;
		/**
		 * ページクラス。
		 */
		@Getter
		private String pageClass = null;
		
		/**
		 * 言語ごとの名称マップ。
		 */
		@Getter
		private LangNameMap langNameMap = null;
		
		/**
		 * 言語ごとの説明マップ。
		 */
		@Getter
		private LangNameMap langDescMap = null;
		
		/**
		 * コンストラクタ。
		 * @param menuPath メニューのパス。
		 * @param cls ページクラス。
		 */
		public PageInfo(final String menuPath, final Class<? extends WebComponent> cls) {
			if (menuPath != null) {
				this.menuPath = menuPath;
			} else {
				PathPackage pp = FunctionMap.this.findPath(cls.getName());
				this.menuPath = pp.getPath();
			}
			this.pageClass = cls.getName();
		}

		/**
		 * コンストラクタ。
		 * @param cls ページクラス。
		 */
		public PageInfo(final Class<? extends WebComponent> cls) {
			this(null, cls);
			
		}
		

		/**
		 * ページのHTMLを取得します。
		 * @param htmlPath HTMLのパス。
		 * @return HTML文字列。
		 */
		private String getPageHtml(final String htmlPath) {
			String html = null;
			try {
				WebComponent wc = new WebComponent();
				html = wc.getWebResource(htmlPath);
			} catch (Exception e) {
				logger.warn(e.getMessage(), e);
			}
			return html;
		}
		
		
		/**
		 * 言語ごとのページ名のマップを取得します。
		 * @throws Exception 例外。
		 */
		public void readPageTitleMap() throws Exception {
			if (this.langNameMap == null) {
				this.langNameMap = new LangNameMap();
			}
			if (this.langDescMap == null) {
				this.langDescMap = new LangNameMap();
			}
			String classname = this.pageClass;
			String path = FunctionMap.this.getWebPath(classname);
			logger.debug(() -> "path=" + path);
			{
				String htmlpath = path + ".html";
				String html = this.getPageHtml(htmlpath);
				if (html != null) {
					String title = HtmlUtil.getTitle(html);
					logger.debug("title=" + title);
					if (title != null) {
						this.langNameMap.put(LangNameMap.LANG_DEFAULT, title);
					} else {
						Class<?> cls = Class.forName(classname);
						this.langNameMap.put(LangNameMap.LANG_DEFAULT, cls.getSimpleName());
					}
					String desc = HtmlUtil.getDescription(html);
					if (desc != null) {
						logger.debug("desc=" + desc);
						this.langDescMap.put(LangNameMap.LANG_DEFAULT, desc);
					} else {
						this.langDescMap.put(LangNameMap.LANG_DEFAULT, "");
					}
				} else {
					Class<?> cls = Class.forName(classname);
					this.langNameMap.put(LangNameMap.LANG_DEFAULT, cls.getSimpleName());
					this.langDescMap.put(LangNameMap.LANG_DEFAULT, "");
					
				}
			}
			List<String> langList = DataFormsServlet.getSupportLanguage();
			for (String lang: langList) {
				String htmlpath = path + "_" + lang + ".html";
				String html = this.getPageHtml(htmlpath);
				if (html != null) {
					String title = HtmlUtil.getTitle(html);
					logger.debug("title_" + lang + "=" + title);
					this.langNameMap.put(lang, title);
					String desc = HtmlUtil.getDescription(html);
					logger.debug("desc_" + lang + "=" + desc);
					this.langDescMap.put(lang, desc);
				}
			}
			
		}

		/**
		 * 言語毎の名称を取得します。
		 * @param lang 言語コード。
		 * @return ページの名称。
		 */
		public String getName(final String lang) {
			if (this.langNameMap == null) {
				return "";
			}
			String ret = this.langNameMap.get(lang);
			if (ret == null) {
				ret = this.langNameMap.get(LangNameMap.LANG_DEFAULT);
			}
			return ret;
		}

		/**
		 * 言語毎の説明を取得します。
		 * @param lang 言語コード。
		 * @return ページの説明。
		 */
		public String getDesc(final String lang) {
			String ret = this.langDescMap.get(lang);
			if (ret == null) {
				ret = this.langDescMap.get(LangNameMap.LANG_DEFAULT);
			}
			return ret;
		}

	}
	
	/**
	 * ページリスト。
	 */
	@Setter
	@Getter
	private List<PageInfo> pageList = null;
	
	/**
	 * ページを追加する。
	 * @param page ページ情報。
	 */
	public void addPage(final PageInfo page) {
		if (this.pageList == null) {
			this.pageList = new ArrayList<PageInfo>();
		}
		this.pageList.add(page);
	}
	
	/**
	 * アプリケーションのページを追加します。
	 */
	public void addAppPage() {
		
	}
	
	/**
	 * フレームワークの基本機能のページクラスを登録します。
	 */
	private void addBasicFunctionPage() {
		// 基本機能のページ
		this.addPage(new PageInfo(jp.dataforms.fw.app.top.page.TopPage.class));
		this.addPage(new PageInfo(jp.dataforms.fw.app.login.page.LoginPage.class));
		this.addPage(new PageInfo(jp.dataforms.fw.app.login.page.OnetimePasswordPage.class));
		this.addPage(new PageInfo(jp.dataforms.fw.app.menu.page.SiteMapPage.class));
		this.addPage(new PageInfo(jp.dataforms.fw.app.user.page.UserManagementPage.class));
		this.addPage(new PageInfo(jp.dataforms.fw.app.user.page.UserSelfEditPage.class));
		this.addPage(new PageInfo(jp.dataforms.fw.app.user.page.ChangePasswordPage.class));
		this.addPage(new PageInfo(jp.dataforms.fw.app.user.page.WebAuthnPage.class));
		this.addPage(new PageInfo(jp.dataforms.fw.app.user.page.UserRegistPage.class));
		this.addPage(new PageInfo(jp.dataforms.fw.app.user.page.UserEnablePage.class));
		this.addPage(new PageInfo(jp.dataforms.fw.app.user.page.PasswordResetMailPage.class));
		this.addPage(new PageInfo(jp.dataforms.fw.app.user.page.PasswordResetPage.class));
		this.addPage(new PageInfo(jp.dataforms.fw.app.enumtype.page.EnumPage.class));
		this.addPage(new PageInfo(jp.dataforms.fw.app.user.page.PasswordReencryptPage.class));
		this.addPage(new PageInfo(jp.dataforms.fw.app.backuprestore.page.BackupRestorePage.class));
	}
	
	/**
	 * 開発ツールのページを追加します。
	 */
	private void addDeveloperPage() {
		this.addPage(new PageInfo(jp.dataforms.fw.devtool.init.page.InitDevelopmentToolPage.class));
		this.addPage(new PageInfo(jp.dataforms.fw.devtool.db.page.InitializeDatabasePage.class));
		this.addPage(new PageInfo(jp.dataforms.fw.devtool.menu.page.MenuEditPage.class));
		this.addPage(new PageInfo(jp.dataforms.fw.devtool.table.page.TableGeneratorPage.class));
		this.addPage(new PageInfo(jp.dataforms.fw.devtool.query.page.QueryGeneratorPage.class));
		this.addPage(new PageInfo(jp.dataforms.fw.devtool.pageform.page.DaoAndPageGeneratorPage.class));
		this.addPage(new PageInfo(jp.dataforms.fw.devtool.webres.page.WebResourcePage.class));
		this.addPage(new PageInfo(jp.dataforms.fw.devtool.expwebres.page.ExportWebResourcePage.class));
		this.addPage(new PageInfo(jp.dataforms.fw.devtool.db.page.TableManagementPage.class));
		this.addPage(new PageInfo(jp.dataforms.fw.devtool.query.page.QueryExecutorPage.class));
		this.addPage(new PageInfo(jp.dataforms.fw.devtool.update.page.UpdateSqlPage.class));
		this.addPage(new PageInfo(jp.dataforms.fw.devtool.doc.page.DocFramePage.class));
		this.addPage(new PageInfo(jp.dataforms.fw.devtool.version.page.VersionInfoPage.class));
	}
	
	/**
	 * フレームワークのページを追加します。
	 */
	public void addFwPage() {
		this.addBasicFunctionPage();
		this.addDeveloperPage();
	}

	/**
	 * 初期化済フラグ。
	 */
	@Getter
	private boolean initialized = false;
	
	/**
	 * コンストラクタ。
	 */
	public FunctionMap() {
		this.addAppPathPackage();
		this.addFwPathPackage();
		this.addAppMenu();
		this.addFwMenu();
		this.addAppPage();
		this.addFwPage();
		logger.debug("menu size=" + this.getMenuList().size());
	}

	/**
	 * 初期化を行います。
	 * @throws Exception 例外。
	 */
	public void init() throws Exception {
		if (!this.initialized) {
			for (PageInfo p: this.pageList) {
				p.readPageTitleMap();
			}
			this.initialized = true;
		}
	}
	
	/**
	 * パッケージを取得する。
	 * @param menu メニュー。
	 * @return パッケージ名。
	 */
	public String getPackage(final Menu menu) {
		String ret = null;
		List<PathPackage> pplist = this.getPathPackageList();
		for (PathPackage pp: pplist) {
			if (pp.getPath().equals(menu.getPath())) {
				ret = pp.getBasePackage();
			}
		}
		return ret;
	}
	
	
	/**
	 * ページの追加コード生成フラグを取得します。
	 * @return ページの追加コード生成フラグ。
	 */
	public Boolean genAddPageCode() {
		return false;	
	}
	
	/**
	 * Menuの編集情報を取得します。
	 * @return Menuの編集情報。
	 */
	public Map<String, Object> getMenuMap() {
		Map<String, Object> ret = new HashMap<String, Object>();
		ret.put(MenuEditForm.ID_APP_BASE_PACKAGE, this.getAppBasePackage());
		ret.put(MenuEditForm.ID_GEN_ADD_PAGE_CODE,  this.genAddPageCode() ? "1": "0");
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		List<Menu> mlist = this.getMenuList();
		List<String> langList = DataFormsServlet.getSupportLanguage();
		for (Menu m: mlist) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put(MenuTable.ID_PATH, m.getPath());
			map.put(MenuTable.ID_PACKAGE_NAME, this.getPackage(m));
			map.put(MenuTable.ID_DEFAULT_NAME, m.getName("default"));
			for (String lang: langList) {
				lang = lang.trim();
				map.put(lang + "Name", m.getName(lang));
			}
			list.add(map);
		}
		ret.put(MenuTable.ID_MENU_LIST, list);
		return ret;
	}
	
	/**
	 * pathに対応するPathパッケージ対応表を取得します。
	 * @param path パス。
	 * @return Pathパッケージ対応表。
	 */
	public PathPackage findBasePackage(final String path) {
		PathPackage ret = null;
		int len = 0;
		for (PathPackage e: this.pathPackageList) {
			if (path.indexOf(e.getPath()) == 0) {
				if (len < e.getPath().length()) {
					ret = e;
					len = e.getPath().length();
				}
			}
		}
		return ret;
	}
	
	/**
	 * URIに対応するクラス名を取得します。
	 * @param context コンテキスト。
	 * @param uri リクエストされたURI。
	 * @return URIに対応するクラス名。
	 */
	public String getWebComponentClass(final String context, final String uri) {
		logger.debug("context, url = " + context + "," + uri);
		String path = uri.substring(context.length());
		int idx = path.lastIndexOf(".");
		if (idx >= 0) {
			path = path.substring(0, idx);
			PathPackage p = this.findBasePackage(path);
			if (p != null) {
				path = path.substring(p.getPath().length());
				path = p.getBasePackage() + path;
			}
		}
		path = path.replaceAll("/", ".");
		return path;
	}
	
	/**
	 * クラス名からWebのパスを取得する。
	 * @param clazz クラス名。
	 * @return Webのパス。
	 */
	private PathPackage findPath(final String clazz) {
		PathPackage ret = null;
		int len = 0;
		for (PathPackage e: this.pathPackageList) {
			if (clazz.indexOf(e.getBasePackage()) == 0) {
				if (len < e.getBasePackage().length()) {
					ret = e;
					len = e.getBasePackage().length();
				}
			}
		}
		return ret;
	}
	
	/**
	 * クラス名に対応するWebのパスを取得する。
	 * @param clazz クラス名。
	 * @return Webのパス。
	 */
	public String getWebPath(final String clazz) {
		PathPackage p = this.findPath(clazz);
		String path = clazz;
		if (p != null) {
			path = path.substring(p.getBasePackage().length());
			path = p.getPath() + path; 
		}
		path =  path.replaceAll("\\.", "/");
		return path;
	}
	
	/**
	 * メニューグループの名称を取得します。
	 * @param lang 言語コード。
	 * @param menuPath メニューのパス。
	 * @return メニューの名称。
	 */
	private String getMenuGroupName(final String lang, final String menuPath) {
		String ret = null;
		for (Menu m: this.getMenuList()) {
			if (menuPath.equals(m.getPath())) {
				ret = m.getLangNameMap().get(lang);
				if (ret == null) {
					ret = m.getLangNameMap().get(LangNameMap.LANG_DEFAULT);
				}
				break;
			}
		}
		return ret;
	}
	
	/**
	 * システムのページリストを取得します。
	 * @param page ページ。
	 * @return システムのページリスト。
	 */
	public List<Map<String, Object>> getPageList(final Page page) {
		String lang = getLang(page);
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for (PageInfo p: this.pageList) {
			Map<String, Object> m = new HashMap<String, Object>();
			m.put("menuGroup", p.getMenuPath());
			m.put("menuGroupName", this.getMenuGroupName(lang, p.getMenuPath()));
			m.put("pageClass", p.getPageClass());
			m.put("menuName", p.getName(lang));
			m.put("description", p.getDesc(lang));
			list.add(m);
		}
		return list;
	}


	/**
	 * 言語を取得します。
	 * @param page ページ。
	 * @return 言語コード。
	 */
	public String getLang(final Page page) {
		String lang = DataFormsServlet.getFixedLanguage();
		if (lang == null) {
			lang = page.getRequest().getLocale().getLanguage();
		}
		return lang;
	}
	
	/**
	 * ページの名称を取得します。
	 * @param page ページ。
	 * @return ページの名称。
	 */
	public String getPageName(final Page page) {
		String ret = null;
		for (PageInfo p: this.pageList) {
			if (page.getClass().getName().equals(p.getPageClass())) {
				ret = p.getName(this.getLang(page));
			}
		}
		return ret;
	}

	/**
	 * ページの名称を取得します。
	 * @param classname ページクラス名。
	 * @return ページの名称。
	 */
	public PageInfo findPageInfo(final String classname) {
		PageInfo ret = null;
		if (this.pageList != null) {
			for (PageInfo p: this.pageList) {
				if (p.getPageClass().equals(classname)) {
					ret = p;
					break;
				}
			}
		}
		return ret;
	}

	/**
	 * アプリケーションのベースパッケージを返します。
	 * @return アプリケーションのベースパッケージ。
	 */
	public String getAppBasePackage() {
		return null;
	}

	/**
	 * アプリケーションのパッケージ中のページリストを取得します。
	 */
	public void readAppPageList() {
		try {
			ClassFinder finder = new ClassFinder();
			for (Menu m: this.getMenuList()) {
				String path = m.getPath();
				if (path.indexOf("/dataforms") == 0) {
					continue;
				}
				String pkg = this.getPackage(m);
				logger.debug("pkg=" + pkg);
				List<Class<?>> list =  finder.findClasses(pkg, Page.class);
				for (Class<?> cls: list) {
					@SuppressWarnings("unchecked")
					Class<? extends WebComponent> pcls = (Class<? extends WebComponent>) cls;
					String classname = pcls.getName();
					logger.debug("pageClassName=" + classname);
					PageInfo p = this.findPageInfo(classname);
					if (p == null) {
						this.addPage(new PageInfo(pcls));
					} else {
//						this.addPage(new PageInfo(pcls));
					}
				}

			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	/**
	 * アプリケーションの機能マップを取得します。
	 * @return アプリケーションの機能マップ。
	 * @throws Exception 例外。
	 */
	public static FunctionMap getAppFunctionMap() throws Exception {
		logger.debug("getAppMenu");
		FunctionMap ret = null;
		ClassFinder cf = new ClassFinder();
		List<Class<?>> list = cf.findClasses(null, FunctionMap.class);
		long cnt = 0;
		for (Class<?> c: list) {
			ApplicationFunctionMap a = c.getAnnotation(ApplicationFunctionMap.class);
			if (a != null) {
				ret = (FunctionMap) c.getConstructor().newInstance();
				cnt++;
			}
		}
		if (ret == null) {
			ret = new FunctionMap();
			logger.warn("AppFunctionMap class not found. Use the " + ret.getClass().getName() + " class.");
		}
		if (cnt != 1) {
			logger.warn("There are multiple AppFunctionMap classes. Use the " + ret.getClass().getName() + " class.");
		} else {
			logger.info("Use the " + ret.getClass().getName() + " class.");
		}
		return ret;
	}
}
