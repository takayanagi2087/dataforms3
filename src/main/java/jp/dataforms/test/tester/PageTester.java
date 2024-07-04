package jp.dataforms.test.tester;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jp.dataforms.fw.controller.Page;
import jp.dataforms.fw.controller.WebComponent;
import jp.dataforms.fw.devtool.javasrc.JavaSrcGenerator.Template;
import jp.dataforms.fw.menu.FunctionMap;
import jp.dataforms.fw.util.ClassFinder;
import jp.dataforms.fw.util.FileUtil;
import jp.dataforms.fw.util.JsonUtil;
import jp.dataforms.test.annotation.TestItemInfo;
import jp.dataforms.test.component.PageTestElement;
import jp.dataforms.test.selenium.Browser;
import jp.dataforms.test.selenium.BrowserInfo;
import jp.dataforms.test.testitem.TestItem;
import jp.dataforms.test.testitem.TestItem.TestItemResult;
import jp.dataforms.test.testitem.page.responsive.ResponsiveTestItem;
import lombok.Data;
import lombok.Getter;


/**
 * テスト実行ツール。
 */
public abstract class PageTester {
	/**
	 * Logger.
	 */
	private static Logger logger = LogManager.getLogger(PageTester.class);
	
	/**
	 * 設定ファイルのパス。
	 */
	private String confFile = null;
	
	
	/**
	 * Selenium設定情報。
	 */
	@Data
	public static class Selenium {
		/**
		 * ドライバーのファイル名。
		 */
		private String driver = null;
		/**
		 * ドライバのフォルダ。
		 */
		private String driverFolder = null;
		/**
		 * headlessフラグ。
		 */
		private Boolean headless = null;
		/**
		 * ブラウザリスト。
		 */
		private List<BrowserInfo> driverList = null;
		
		/**
		 * コンストラクタ。
		 */
		public Selenium() {
			
		}
		
		private String getDriverPath() throws Exception {
			String ret = null;
			File dir = new File(this.getDriverFolder());
			File[] list = dir.listFiles();
			for (File f: list) {
				String name = f.getName();
				if (name.indexOf(this.driver) == 0) {
					ret = f.getAbsolutePath();
				}
			}
			return ret;
		}
		
		/**
		 * ブラウザ情報を取得します。
		 * @return ブラウザ情報。
		 */
		public BrowserInfo getBrowserInfo() throws Exception {
			String driverPath = this.getDriverPath();
			logger.debug("driverPath=" + driverPath);
			BrowserInfo ret = null;
			for (BrowserInfo bi: this.driverList) {
				if (bi.getDriver().equals(this.driver)) {
					ret = bi;
					ret.setDriver(driverPath);
					break;
				}
			}
			return ret;
		}
	}
	
	/**
	 * Webアプリケーション。
	 */
	@Data
	public static class WebApplication {
		/**
		 *  WebアプリケーションのURL。
		 */
		private String applicationURL = null;
		/** 
		 * テスト結果の保存パス。
		 */
		private String testResult = null;
		
		/**
		 * コンストラクタ。
		 */
		public WebApplication() {
			
		}
	}
	
	/**
	 * テストに使用するUser。
	 */
	@Data
	public static class TestUser {
		/**
		 * LoginID。
		 */
		private  String loginId = null;
		/**
		 * パスワード。
		 */
		private String password = null;
		
		/**
		 * コンストラクタ。
		 */
		public TestUser() {
			
		}
	}
	
	/**
	 * テスト設定情報。
	 */
	@Data
	public static class Conf {
		/**
		 * Selenium設定情報。
		 */
		private Selenium selenium = null;
		/**
		 * テスト対象アプリケーション情報。
		 */
		private WebApplication testApp = null;
		/**
		 * ユーザリスト。
		 */
		private List<TestUser> userList = null;
		/**
		 * コンストラクタ。
		 */
		public Conf() {
			
		}
		
		/**
		 * 設定ファイルを読み込みます。
		 * @param confFile 設定ファイル。
		 * @return テスト設定情報。
		 * @throws Exception 例外。
		 */
		public static Conf read(final String confFile) throws Exception {
			String json = FileUtil.readTextFile(confFile, "utf-8");
			return  (Conf) JsonUtil.decode(json, Conf.class);
			
		}
		
		/**
		 * テストするむユーザ情報を取得します。
		 * @param loginId ログインID。
		 * @return ユーザ情報。
		 */
		public TestUser getTestUser(final String loginId) {
			TestUser ret = null;
			for (TestUser u: this.userList) {
				if (u.getLoginId().equals(loginId)) {
					ret = u;
					break;
				}
			}
			return ret;
		}
	}
	
	/**
	 * 設定情報。
	 */
	@Getter
	private Conf conf = null;
	
	/**
	 * テスト対象のページクラス。
	 */
	private Class<? extends Page> pageClass = null;
	
	/**
	 * コンストラクタ。
	 * @param confFile 設定ファイルのパス。
	 * @param pageClass ページクラス。
	 * 
	 */
	public PageTester(final String confFile, final Class<? extends Page> pageClass) {
		this.confFile = confFile;
		this.pageClass = pageClass;
	}
	
	
	/**
	 * テスト項目のインスタンスを取得します。
	 * @param cls テスト項目のクラス。
	 * @param pageClass ページクラス。
	 * @param compClass テスト対象コンポーネントクラス。
	 * @return テスト項目のインスタンス。
	 * @throws Exception 例外。
	 */
	private TestItem getTestItemInstance(Class<? extends TestItem> cls, final Class<? extends Page> pageClass, 
			final Class<? extends WebComponent> compClass) throws Exception {
		if (pageClass != null && compClass != null) {
			TestItem ci = (TestItem) cls.getConstructor(Class.class, Class.class).
					newInstance(pageClass, compClass);
			return ci;
		} else if (pageClass == null && compClass != null) {
			TestItem ci = (TestItem) cls.getConstructor(Class.class).
					newInstance(compClass);
			return ci;
		} else if (pageClass != null && compClass == null) {
			TestItem ci = (TestItem) cls.getConstructor(Class.class).
					newInstance(pageClass);
			return ci;
		} else if (pageClass == null && compClass == null) {
			TestItem ci = (TestItem) cls.getConstructor().
					newInstance();
			return ci;
		}
		return null;
	}
	
	
	
	/**
	 * 指定された条件のチェック項目を取得します。
	 * @param basePackage クラスを検索するパッケージ。
	 * @param baseCheckItem チェック項目の基本クラス。
	 * @param pageClass ページクラス。
	 * @param compClass ターゲットクラス。
	 * @return チェック項目リスト。
	 * @throws Exception 例外。
	 */
	public List<TestItem> queryCheckItem(
			final String basePackage,
			final Class<? extends TestItem> baseCheckItem, 
			final Class<? extends Page> pageClass, 
			final Class<? extends WebComponent> compClass) throws Exception  {
		
		List<TestItem> ret = new ArrayList<TestItem>();
		ClassFinder cf = new ClassFinder();
		List<Class<?>> list = cf.findClasses(basePackage, baseCheckItem);
		for (Class<?> cls: list) {
			TestItemInfo a = cls.getAnnotation(TestItemInfo.class);
			if (a != null) {
				logger.debug("TestItemClass=" + cls.getName());
				@SuppressWarnings("unchecked")
				TestItem ci = this.getTestItemInstance((Class<? extends TestItem>) cls, pageClass, compClass);
				ret.add(ci);
			}
		}
		this.sortCheckItem(ret);
		return ret;
	}

	/**
	 * チェック項目リストをソートします。
	 * @param list チェック項目リスト。
	 */
	protected void sortCheckItem(List<TestItem> list) {
		// テスト項目をソート
		list.sort((a, b) -> {
			String ga = a.getGroup();
			String gb = b.getGroup();
			int cmp = ga.compareTo(gb);
			if (cmp == 0) {
				String sa = a.getSeq();
				String sb = b.getSeq();
				cmp = sa.compareTo(sb);
			}
			return cmp;
		});
	}

	
	/**
	 * ブラウザを取得します。
	 * @return ブラウザ。
	 * @throws Exception 例外。
	 */
	protected Browser getBrowser() throws Exception {
		BrowserInfo bi = this.conf.getSelenium().getBrowserInfo();
		Browser browser = new Browser(bi);
		return browser;
	}
	
	
	/**
	 * ページのインスタンスを取得します。
	 * @return ページのインスタンス。
	 * @throws Exception 例外。
	 */
	protected Page getPageInstance() throws Exception {
		Page page = this.pageClass.getConstructor().newInstance();
		return page;
	}
	
	/**
	 * レスポンシブデザインテストを実行します。
	 * @param browser ブラウザ。
	 * @param pageClass ページクラス。
	 * @param compClass コンポーネントクラス。
	 * 
	 * @return レスポンシブデザインテストの結果リスト。
	 * @throws Exception 例外。
	 */
	protected List<TestItem> testResponsive(final Browser browser, final Class<? extends Page> pageClass, final Class<? extends WebComponent> compClass) throws Exception {
//		Page page = this.getPageInstance();
		ResponsiveTestItem.setHeight(540);
		List<TestItem> list = this.queryCheckItem("jp.dataforms.test.testitem.page", ResponsiveTestItem.class, pageClass, compClass);
		for (TestItem ci: list) {
			logger.info("GROUP:" + ci.getGroup() + ", SEQ:" + ci.getSeq());
			logger.info("CONDITION:" + ci.getCondition());
			ci.exec(browser);
			Browser.sleep(1);
		}
		return list;
	}
	
	/**
	 * ページのテンプレートを取得します。
	 * @return ページテンプレート。
	 * @throws Exception 例外。
	 */
	protected Template getTemplate() throws Exception {
		Template tmp = new Template(PageTester.class, "template/index.html.template");
		return tmp;
	}

	/**
	 * ページのテスト結果情報のクラス。
	 */
	@Data
	public static class PageTestResult {
		/**
		 * ページ名称。
		 */
		public String pageName = null;
		/**
		 * ページクラス名。
		 */
		public String pageClassName = null;
		
		/**
		 * テスト項目リスト。
		 */
		private List<TestItemResult> testItemList = null;
		
		/**
		 * コンストラクタ。
		 */
		public PageTestResult() {
			this.testItemList = new ArrayList<TestItemResult>();
		}
		
		/**
		 * テスト結果を検索します。
		 * @param item テスト項目結果。
		 * @return ヒットしたインデックス。
		 */
		public int find(final TestItemResult item) {
			int ret = -1;
			for (int i = 0; i < this.testItemList.size(); i++) {
				TestItemResult r = this.testItemList.get(i);
				if (r.getLink().equals(item.getLink())) {
					ret = i;
					break;
				}
			}
			return ret;
		}
		
		/**
		 * テスト結果を更新します。
		 * @param list 新規テスト結果リスト。
		 */
		public void update(final List<TestItemResult> list) {
			for (int i = 0; i < list.size(); i++) {
				int idx = this.find(list.get(i));
				if (idx >= 0) {
					this.testItemList.set(idx, list.get(i));
				} else {
					this.testItemList.add(list.get(i));
				}
			}
			this.testItemList.sort((a, b) -> {
				String ga = a.getCompClass();
				String gb = b.getCompClass();
				int cmp = ga.compareTo(gb);
				if (cmp == 0) {
					String sa = a.getTestId();
					String sb = b.getTestId();
					cmp = sa.compareTo(sb);
				}
				return cmp;
			});

		}
	}
	
	
	/**
	 * index.htmlから既存のテスト結果を取得します。
	 * @return テスト結果のjson部分。
	 * @throws Exception 例外。
	 */
	protected PageTestResult readOldTestResult() throws Exception {
		Page page = this.pageClass.getConstructor().newInstance();
		String fn = TestItem.getTestResult() + "/" + page.getClass().getName() + "/index.html";
		File f = new File(fn);
		PageTestResult ret = null;
		if (f.exists()) {
			String indexHtml = FileUtil.readTextFile(fn, "utf-8");
			logger.debug("indexHtml=" + indexHtml);
			Pattern p = Pattern.compile("// ### testResult begin([\\s\\S]*)// ### testResult end", Pattern.MULTILINE);
			Matcher m = p.matcher(indexHtml);
			if (m.find()) {
				String json = m.group(1);
				logger.debug("*** json=" + json);
				ret = (PageTestResult) JsonUtil.decode(json, PageTestResult.class);
			}
		}
		return ret;
	}
	
	
	
	/**
	 * 保存するページのテスト結果を作成します。
	 * @param list テスト結果リスト。
	 * @return ページのテスト結果。
	 * @throws Exception 例外。
	 */
	protected PageTestResult getPageTestResult(final List<TestItem> list) throws Exception {
		Page page = this.pageClass.getConstructor().newInstance();
		List<TestItemResult> testItemList = new ArrayList<TestItemResult>();
		for (TestItem ti: list) {
			testItemList.add(ti.getTestItemResult());
		}
		PageTestResult result = new PageTestResult();
		result.setPageName(page.getPageName());
		result.setPageClassName(page.getClass().getName());
		result.setTestItemList(testItemList);
		return result;
	}
	
	
	/**
	 * テスト結果リストindex.htmlの作成。
	 * @param list テスト結果リスト。
	 * @throws Exception 例外。
	 */
	protected void saveIndexHtml(final List<TestItem> list) throws Exception {
		PageTestResult newResult = this.getPageTestResult(list);
		PageTestResult result = this.readOldTestResult();
		if (result == null) {
			result = newResult;
		} else {
			result.update(newResult.getTestItemList());
		}
		String json = JsonUtil.encode(result, true);
		
		Template indexTemplate = this.getTemplate();
		Page page = this.pageClass.getConstructor().newInstance();
		String source = indexTemplate.getSource();
		Pattern p = Pattern.compile("\\$\\{resultList\\}");
		Matcher m = p.matcher(indexTemplate.getSource());
		if (m.find()) {
			int s = m.start();
			int e = m.end();
			source = source.substring(0, s) + json + source.substring(e);
		}
		String fn = TestItem.getTestResult() + "/" + page.getClass().getName() + "/index.html";
		FileUtil.writeTextFile(fn, source, "utf-8");
	}
	
	/**
	 * 設定ファイルをﾖ見込みます。
	 * @throws Exception 例外。　
	 */
	public void readConf() throws Exception {
		logger.debug("path=" + this.confFile);
		this.conf = Conf.read(confFile);
		logger.debug("conf=" + JsonUtil.encode(this.conf, true));
		TestItem.setConf(this.conf);
	}
	

	/**
	 * ページをオープンします。
	 * @param browser ブラウザ。
	 * @return オープンしたページ要素。
	 * @throws Exception 例外。
	 */
	protected PageTestElement openPage(Browser browser) throws Exception {
		FunctionMap map = FunctionMap.getAppFunctionMap();
		String uri = map.getWebPath(this.pageClass.getName());
		logger.info("uri = " + uri);
		PageTestElement pt = (PageTestElement) browser.open(this.conf.getTestApp().getApplicationURL() + uri.substring(1) + ".df");
		return pt;
	}

	
	/**
	 * テスト実行。
	 * @throws Exception 例外。
	 */
	public abstract void exec() throws Exception;

	/**
	 * メイン処理。
	 * @param args コマンドライン。
	 * <pre>
	 * args[0]	...	テスト設定ファイル。
	 * args[1]	...	テスタークラス名。
	 * </pre>
	 */
	public static void main(String[] args) {
		try {
			if (args.length == 2) {
				@SuppressWarnings("unchecked")
				Class<? extends PageTester> testerClass = (Class<? extends PageTester>) Class.forName(args[1]);
				PageTester exec = testerClass.getConstructor(String.class).newInstance(args[0]);
				exec.readConf();
				exec.exec();
			} else {
				System.err.println("使い方:jp.dataforms.test.tester.PageTester <ConfFile> <PageTesterClass>");
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
}
