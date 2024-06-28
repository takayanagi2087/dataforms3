package jp.dataforms.test.executor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jp.dataforms.fw.app.login.page.LoginPage;
import jp.dataforms.fw.controller.Page;
import jp.dataforms.fw.controller.WebComponent;
import jp.dataforms.fw.devtool.javasrc.JavaSrcGenerator.Template;
import jp.dataforms.fw.menu.FunctionMap;
import jp.dataforms.fw.util.ClassFinder;
import jp.dataforms.fw.util.FileUtil;
import jp.dataforms.fw.util.JsonUtil;
import jp.dataforms.test.annotation.CheckItemInfo;
import jp.dataforms.test.checkitem.CheckItem;
import jp.dataforms.test.checkitem.CheckItem.ResultType;
import jp.dataforms.test.checkitem.component.page.responsive.ResponsiveCheckItem;
import jp.dataforms.test.component.PageTestElement;
import jp.dataforms.test.selenium.Browser;
import jp.dataforms.test.selenium.BrowserInfo;
import lombok.Data;
import lombok.Getter;

/**
 * テスト実行ツール。
 */
public class PageTester {
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
	 * 指定された条件のチェック項目を取得します。
	 * @param basePackage クラスを検索するパッケージ。
	 * @param baseCheckItem チェック項目の基本クラス。
	 * @param target ターゲットクラス。
	 * @return チェック項目リスト。
	 * @throws Exception 例外。
	 */
	public List<CheckItem> queryCheckItem(
			final String basePackage,
			final Class<? extends CheckItem> baseCheckItem, 
			final Class<? extends WebComponent> target) throws Exception  {
		
		List<CheckItem> ret = new ArrayList<CheckItem>();
		ClassFinder cf = new ClassFinder();
		List<Class<?>> list = cf.findClasses(basePackage, baseCheckItem);
		for (Class<?> cls: list) {
			CheckItemInfo a = cls.getAnnotation(CheckItemInfo.class);
			if (a != null) {
				CheckItem ci = (CheckItem) cls.getConstructor().newInstance();
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
	protected void sortCheckItem(List<CheckItem> list) {
		// テスト項目をソート
		list.sort((a, b) -> {
			String ta = a.getTargetClass().getName();
			String tb = b.getTargetClass().getName();
			int cmp = ta.compareTo(tb);
			if (cmp == 0) {
				String ga = a.getGroup();
				String gb = b.getGroup();
				cmp = ga.compareTo(gb);
				if (cmp == 0) {
					String sa = a.getSeq();
					String sb = b.getSeq();
					cmp = sa.compareTo(sb);
				}
			}
			return cmp;
		});
	}

	/**
	 * レスポンシブデザインテストを実行します。
	 * @return レスポンシブデザインテストの結果リスト。
	 * @throws Exception 例外。
	 */
	protected List<CheckItem> checkResponsive() throws Exception {
		FunctionMap map = FunctionMap.getAppFunctionMap();
		String uri = map.getWebPath(this.pageClass.getName());
		logger.info("uri = " + uri);
		ResponsiveCheckItem.setHeight(540);
		BrowserInfo bi = this.conf.getSelenium().getBrowserInfo();
		Browser browser = new Browser(bi);
		PageTestElement pt = browser.open(this.conf.getTestApp().getApplicationURL() + uri.substring(1) + ".df");
		Page page = this.pageClass.getConstructor().newInstance();
		List<CheckItem> list = this.queryCheckItem("jp.dataforms.test.checkitem.component", ResponsiveCheckItem.class, null);
		for (CheckItem ci: list) {
			logger.debug("checkTarget=" + ci.getTargetClass().getName());
			logger.info("GROUP:" + ci.getGroup() + ", SEQ:" + ci.getSeq());
			logger.info("CONDITION:" + ci.getCondition());
			ResultType result = ci.test(page, pt);
			ci.saveResult(page, pt, result);
			Browser.sleep(1);
		}
		browser.close();
		return list;
	}
	
	/**
	 * ページのテンプレートを取得します。
	 * @return ページテンプレート。
	 * @throws Exception 例外。
	 */
	protected Template getTemplate() throws Exception {
		Template tmp = new Template(PageTester.class, "template/index.html");
		return tmp;
	}

	
	/**
	 * テスト結果リストindex.htmlの作成。
	 * @param list テスト結果リスト。
	 * @throws Exception 例外。
	 */
	protected void saveIndexHtml(final List<CheckItem> list) throws Exception {
		Template indexTemplate = this.getTemplate();
		Page page = this.pageClass.getConstructor().newInstance();
		indexTemplate.replace("pageName", page.getPageName());
		String pageClassName = this.pageClass.getName();
		indexTemplate.replace("pageClass", pageClassName);
		StringBuilder sb = new StringBuilder();
		int no = 1;
		for (CheckItem ci: list) {
			sb.append(ci.getListRow(no++));
		}
		indexTemplate.replace("resultList", sb.toString());
		String fn = CheckItem.getTestResult() + "/index.html";
		FileUtil.writeTextFile(fn, indexTemplate.getSource(), "utf-8");
	}
	
	/**
	 * テスト実行。
	 * @throws Exception 例外。
	 */
	public void exec() throws Exception {
		logger.debug("path=" + this.confFile);
		this.conf = Conf.read(confFile);
		logger.debug("conf=" + JsonUtil.encode(this.conf, true));
		CheckItem.setTestResult(this.conf.getTestApp().getTestResult() + "/" + this.pageClass.getName());
		List<CheckItem> list = this.checkResponsive();
		this.saveIndexHtml(list);
		
	}
	
	
	/**
	 * メイン処理。
	 * @param args コマンドライン。
	 * <pre>
	 * args[0]	...	テスト設定ファイル。
	 * args[1]	... テストURI。
	 * </pre>
	 */
	public static void main(String[] args) {
		try {
			PageTester exec = new PageTester(args[0], LoginPage.class);
			exec.exec();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
}
