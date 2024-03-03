package jp.dataforms.fw.menu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jp.dataforms.fw.controller.Page;
import jp.dataforms.fw.controller.WebComponent;
import jp.dataforms.fw.servlet.DataFormsServlet;
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
		 * @param path
		 * @param basePackage
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
		this.addPathPackage(new PathPackage("/dataforms/devtool", "jp.dataforms.fw.devtool"));
		this.addPathPackage(new PathPackage("/dataforms/app", "jp.dataforms.fw.app"));
		this.addPathPackage(new PathPackage("/dataforms", "jp.dataforms.fw"));
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
		 * @param menuPath メニューID。
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
		 * コンストラクタ。
		 * @param cls ページクラス。
		 */
		public PageInfo(final Class<? extends WebComponent> cls) {
			this(null, cls);
			
		}
		
		/**
		 * 言語ごとのページ名のマップを取得します。
		 */
		public void readPageTitleMap() {
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
					this.langNameMap.put(LangNameMap.LANG_DEFAULT, title);
					String desc = HtmlUtil.getDescription(html);
					logger.debug("desc=" + desc);
					this.langDescMap.put(LangNameMap.LANG_DEFAULT, desc);
				}
			}
			List<String> langList = DataFormsServlet.getSupportLanguageList();
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
		this.addPage(new PageInfo(jp.dataforms.fw.app.user.page.UserRegistPage.class));
		this.addPage(new PageInfo(jp.dataforms.fw.app.user.page.UserEnablePage.class));
		this.addPage(new PageInfo(jp.dataforms.fw.app.user.page.PasswordResetMailPage.class));
		this.addPage(new PageInfo(jp.dataforms.fw.app.user.page.PasswordResetPage.class));
		this.addPage(new PageInfo(jp.dataforms.fw.app.enumtype.page.EnumPage.class));
		this.addPage(new PageInfo(jp.dataforms.fw.app.enumtype.page.EnumPage.class));
		this.addPage(new PageInfo(jp.dataforms.fw.app.user.page.PasswordReencryptPage.class));
	}
	
	/**
	 * 開発ツールのページを追加します。
	 */
	private void addDeveloperPage() {
		this.addPage(new PageInfo(jp.dataforms.fw.devtool.db.page.InitializeDatabasePage.class));
		this.addPage(new PageInfo(jp.dataforms.fw.devtool.func.page.FuncManagementPage.class));
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
		
	}

	/**
	 * 初期化を行います。
	 */
	public void init() {
		if (!this.initialized) {
			for (PageInfo p: this.pageList) {
				p.readPageTitleMap();
			}
			this.initialized = true;
		}
	}
	
	/**
	 * pathに対応するPathパッケージ対応表を取得します。
	 * @param path パス。
	 * @return Pathパッケージ対応表。
	 */
	private PathPackage findBasePackage(final String path) {
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
	public List<Map<String, Object>> getMenuList(final Page page) {
		String lang = DataFormsServlet.getFixedLanguage();
		if (lang == null) {
			lang = page.getRequest().getLocale().getLanguage();
		}
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
	
}
