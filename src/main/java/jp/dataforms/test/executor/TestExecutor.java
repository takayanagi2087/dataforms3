package jp.dataforms.test.executor;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jp.dataforms.fw.util.FileUtil;
import jp.dataforms.fw.util.JsonUtil;
import jp.dataforms.test.selenium.Browser;
import jp.dataforms.test.selenium.BrowserInfo;
import lombok.Data;

/**
 * テスト実行ツール。
 */
public class TestExecutor {
	/**
	 * Logger.
	 */
	private static Logger logger = LogManager.getLogger(TestExecutor.class);
	
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
		 * ブラウザ名。
		 */
		private String browser = null;
		/**
		 * headlessフラグ。
		 */
		private Boolean headless = null;
		/**
		 * ブラウザリスト。
		 */
		private List<BrowserInfo> browserList = null;
		
		/**
		 * コンストラクタ。
		 */
		public Selenium() {
			
		}
		
		/**
		 * ブラウザ情報を取得します。
		 * @return ブラウザ情報。
		 */
		public BrowserInfo getBrowserInfo() {
			BrowserInfo ret = null;
			for (BrowserInfo bi: this.browserList) {
				if (bi.getName().equals(this.browser)) {
					ret = bi;
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
	 * プロジェクト。
	 */
	@Data
	public static class Project {
		/**
		 * Eclipseプロジェクトのパス。
		 */
		private String projectPath = null;
		/**
		 * Eclipseプロジェクトの設定パス。
		 */
		private String tomcatConfigPath = null;
		/**
		 * テストソース。
		 */
		private String testSrc = null;
		/**
		 * ソースのsnapshot。
		 */
		private String snapshot = null;
		
		/**
		 * コンストラクタ。
		 */
		public Project() {
			
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
		 * プロジェクト情報。
		 */
		private Project project = null;
		
		/**
		 * コンストラクタ。
		 */
		public Conf() {
			
		}
	}
	
	/**
	 * 設定情報。
	 */
	private Conf conf = null;
	/**
	 * URI。
	 */
	private String uri = null;
	
	/**
	 * コンストラクタ。
	 * @param confFile 設定ファイルのパス。
	 * @param uri テストのURI。
	 * 
	 */
	public TestExecutor(final String confFile, final String uri) {
		this.confFile = confFile;
		this.uri = uri;
	}
	
	/**
	 * テスト実行。
	 * @throws Exception 例外。
	 */
	public void exec() throws Exception {
		logger.debug("path=" + this.confFile);
		String json = FileUtil.readTextFile(this.confFile, "utf-8");
		this.conf = (Conf) JsonUtil.decode(json, Conf.class);
		logger.debug("conf=" + JsonUtil.encode(this.conf, true));
		
		BrowserInfo bi = this.conf.getSelenium().getBrowserInfo();
		Browser browser = new Browser(bi);
		browser.open(this.conf.getTestApp().getApplicationURL() + uri);
	}
	
	
	/**
	 * メイン処理。
	 * @param args コマンドライン。
	 * <pre>
	 * args[0]	...	テスト設定ファイル。
	 * args[1]	... テストURL。
	 * </pre>
	 */
	public static void main(String[] args) {
		try {
			TestExecutor exec = new TestExecutor(args[0], args[1]);
			exec.exec();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
}
