package jp.dataforms.fw.menu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jp.dataforms.fw.controller.WebComponent;
import lombok.Getter;
import lombok.Setter;
import net.arnx.jsonic.JSON;

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
	 * 各言語毎の名称マップ。
	 */
	public class LangNameMap extends HashMap<String, String> {
		/**
		 * デフォルト言語コード。
		 */
		public static final String LANG_DEFAULT = "default";

		/**
		 * コンストラクタ。
		 * @param names 言語毎の名称リスト。
		 * <pre>
		 *  国コードを指定する場合次のように\tで区切って指定	"ja\tname"
		 *  デフォルト言語の場合名称のみを指定					"name"
		 * </pre>
		 */
		public LangNameMap(final String... names) {
			for (int i = 0; i < names.length; i++) {
				String[] sp = names[i].split("\t");
				if (sp.length == 2) {
					this.put(sp[0], sp[1]);
				} else {
					this.put(LANG_DEFAULT, sp[0]);
				}
			}
		}
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
		private String menuId = null;
		
		/**
		 * コンストラクタ。
		 * @param menuId メニューID。
		 * @param names 言語毎の名称リスト。
		 * <pre>
		 *  国コードを指定する場合次のように\tで区切って指定	"ja\tname"
		 *  デフォルト言語の場合名称のみを指定					"name"
		 * </pre>
		 */
		public Menu(final String menuId, final String ...names) {
			this.menuId = menuId;
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
		@Setter
		@Getter
		private String menuId = null;
		/**
		 * ページクラス。
		 */
		@Setter
		@Getter
		private String pageClass = null;
		/**
		 * 言語名称マップ。
		 */
		@Setter
		@Getter
		private LangNameMap langNameMap = null;
		
		/**
		 * コンストラクタ。
		 * @param menuId メニューID。
		 * @param classname ページクラス。
		 * @param names 言語毎の名称リスト。
		 */
		public PageInfo(final String menuId, final Class<? extends WebComponent> cls, final String... names) {
			if (menuId != null) {
				this.menuId = menuId;
			} else {
				PathPackage pp = FunctionMap.this.findPath(cls.getName());
				this.menuId = pp.getPath();
			}
			this.pageClass = cls.getName();
			if (this.langNameMap == null) {
				this.langNameMap = new LangNameMap(names);
			}
		}

		/**
		 * コンストラクタ。
		 * @param classname ページクラス名。
		 * @param names 言語毎の名称リスト。
		 */
		public PageInfo(final Class<? extends WebComponent> cls, final String... names) {
			this(null, cls, names);
		}

	}
	
	/**
	 * ページリスト。
	 */
	@Setter
	@Getter
	private List<PageInfo> pageList = null;
	
	/**
	 * メニューを追加する。
	 * @param menu Pathに対応するJavaのパッケージ情報。
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
		this.addPage(new PageInfo(jp.dataforms.fw.app.top.page.TopPage.class
			, "Top page | Depending on a state, I diverge in a login page, a site map."
			, "ja\tトップページ | 状態に応じて、ログインページ、サイトマップに分岐します。"
		));
		this.addPage(new PageInfo(jp.dataforms.fw.app.login.page.LoginPage.class
			, "Login | I perform the user certification."
			, "ja\tログイン | ユーザ認証を行います。"
		));
		this.addPage(new PageInfo(jp.dataforms.fw.app.login.page.OnetimePasswordPage.class
			, "Onetime password| One-time password confirmation."
			, "ja\tワンタイムパスワード確認 | ワンタイムパスワード確認を行います。"
		));
		this.addPage(new PageInfo(jp.dataforms.fw.app.menu.page.SiteMapPage.class
			, "Site map | I display a list of functions of the site."
			, "ja\tサイトマップ | サイトの機能一覧を表示します。"
		));
		this.addPage(new PageInfo(jp.dataforms.fw.app.user.page.UserManagementPage.class
			, "User management | I manage the user."
			, "ja\tユーザ管理 | ユーザの管理を行います。"
		));
		this.addPage(new PageInfo(jp.dataforms.fw.app.user.page.UserSelfEditPage.class
			, "User information update | I change the registration information of the login user."
			, "ja\tユーザ情報変更 | ログインユーザの登録情報を変更します。"
		));
		this.addPage(new PageInfo(jp.dataforms.fw.app.user.page.ChangePasswordPage.class
			, "Change password | It is a page changing the password."
			, "ja\tパスワード変更 | パスワードを変更します。"
		));
		this.addPage(new PageInfo(jp.dataforms.fw.app.user.page.UserRegistPage.class
			, "User registration | Register a new user."
			, "ja\tユーザ登録 | 新たにユーザを登録します。"
		));
		this.addPage(new PageInfo(jp.dataforms.fw.app.user.page.UserEnablePage.class
			, "User registration confirmation | Activate the registered user."
			, "ja\tユーザ登録確認 | 登録したユーザーを有効にします。"
		));
		this.addPage(new PageInfo(jp.dataforms.fw.app.user.page.PasswordResetMailPage.class
			, "Send password reset mail | Send password reset method."
			, "ja\tパスワードリセットメール送信 | パスワードリセット方法をメールします。"
		));
		this.addPage(new PageInfo(jp.dataforms.fw.app.user.page.PasswordResetPage.class
			, "Password reset | Password reset."
			, "ja\tパスワードリセット | パスワードをリセットします。"
		));
		this.addPage(new PageInfo(jp.dataforms.fw.app.enumtype.page.EnumPage.class
			, "Enumeration management | Edit the enumeration related table."
			, "ja\t列挙型管理 | 列挙型関連テーブルを編集します。"
		));
		this.addPage(new PageInfo(jp.dataforms.fw.app.enumtype.page.EnumPage.class
			, "Backup/Restore | Make a backup or restore of the database."
			, "ja\tバックアップ、リストア | データベースのバックアップ、リストアを行います。"
		));
		this.addPage(new PageInfo(jp.dataforms.fw.app.user.page.PasswordReencryptPage.class
			, "Password re-encrypt | Change the password encryption method."
			, "ja\tパスワード再暗号化 | パスワードの暗号化方法を変更します。"
		));
	}
	
	/**
	 * 開発ツールのページを追加します。
	 */
	private void addDeveloperPage() {
		this.addPage(new PageInfo(jp.dataforms.fw.devtool.db.page.InitializeDatabasePage.class
			, "Initialize database"
			, "ja\tテータベース初期化"
		));
		this.addPage(new PageInfo(jp.dataforms.fw.devtool.func.page.FuncManagementPage.class
			, "Function management | I perform the additional deletion of the function."
			, "ja\t機能管理 | 機能の追加削除を行います。"
		));
		this.addPage(new PageInfo(jp.dataforms.fw.devtool.table.page.TableGeneratorPage.class
			, "Table Java class generation | I make a table Java class and a field Java class to belong to it."
			, "ja\tテーブルJavaクラス作成 | テーブルJavaクラスとそれに属するフィールドJavaクラスを作成します。"
		));
		this.addPage(new PageInfo(jp.dataforms.fw.devtool.query.page.QueryGeneratorPage.class
			, "Query Java class generation  | I make a query Java class."
			, "ja\t問合せJavaクラス作成 | 問合せJavaクラスを作成します。"
		));
		this.addPage(new PageInfo(jp.dataforms.fw.devtool.pageform.page.DaoAndPageGeneratorPage.class
			, "DAO & Page Java class generation | I make a page Java class and a form Java class to belong to it."
			, "ja\tDAO & ページJavaクラス作成 | ページJavaクラスとそれに属するフォームJavaクラスを作成します。"
		));
		this.addPage(new PageInfo(jp.dataforms.fw.devtool.dao.page.DaoGeneratorPage.class
			, "DAO Java class generation | I make a DAO Java class."
			, "ja\tDAO Javaクラス作成 | DAO Javaクラスを作成します。"
		));
		this.addPage(new PageInfo(jp.dataforms.fw.devtool.dao.page.DaoGeneratorPage.class
			, "DAO Java class generation | I make a DAO Java class."
			, "ja\tDAO Javaクラス作成 | DAO Javaクラスを作成します。"
		));
		this.addPage(new PageInfo(jp.dataforms.fw.devtool.pageform.page.PageGeneratorPage.class
			, "Page Java class generation | I make a page Java class and a form Java class to belong to it."
			, "ja\tページJavaクラス作成 | ページJavaクラスとそれに属するフォームJavaクラスを作成します。"
		));
		this.addPage(new PageInfo(jp.dataforms.fw.devtool.webres.page.WebResourcePage.class
			, "Web resource generation | I search Web components in an appointed page and make HTML corresponding to it or Javascript."
			, "ja\tWebリソース作成 | 指定されたページ中のWebコンポーネントを検索し、それに対応したHTMLまたはJavascriptを作成します。"
		));
		this.addPage(new PageInfo(jp.dataforms.fw.devtool.expwebres.page.ExportWebResourcePage.class
			, "Export web resource | Expand the *.html,*.js file in the dataforms2.jar to the project."
			, "ja\tWebリソースエクスポート | dataforms2.jarの中の*.html,*.jsファイルをプロジェクトに展開します。"
		));
		this.addPage(new PageInfo(jp.dataforms.fw.devtool.db.page.TableManagementPage.class
			, "Table management | I make the table corresponding to the table Java class on DB."
			, "ja\tテーブル管理 | テーブルJavaクラスに対応したテーブルをDB上に作成します。"
		));
		this.addPage(new PageInfo(jp.dataforms.fw.devtool.query.page.QueryExecutorPage.class
			, "Query execution | Run the query."
			, "ja\t問合せ実行 | 問合せを実行します。"
		));
		this.addPage(new PageInfo(jp.dataforms.fw.devtool.update.page.UpdateSqlPage.class
			, "Update data | Execute SQL to update data."
			, "ja\tデータ更新 | データを更新するSQLを実行します。"
		));
		this.addPage(new PageInfo(jp.dataforms.fw.devtool.doc.page.DocFramePage.class
			, "Documents | Documents for developer."
			, "ja\tドキュメント | 開発者向けドキュメントページ。"
		));
		this.addPage(new PageInfo(jp.dataforms.fw.devtool.version.page.VersionInfoPage.class
			, "Version information | Show dataforms2.jar version information."
			, "ja\tバージョン情報 | dataforms2.jarのバージョン情報を表示します。"
		));
	}
	
	/**
	 * フレームワークのページを追加します。
	 */
	public void addFwPage() {
		this.addBasicFunctionPage();
		this.addDeveloperPage();
	}

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
	 * pathに対応するPathパッケージ対応表を取得します。
	 * @param path パス。
	 * @return Pathパッケージ対応表。
	 */
	private PathPackage findBasePackage(final String path) {
		logger.debug("aaa:" + path + ", " + JSON.encode(this.pathPackageList));
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
}
