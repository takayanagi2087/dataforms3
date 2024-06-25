package jp.dataforms.test.executor;

import java.io.File;
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
				if (bi.getName().equals(this.driver)) {
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
//		String json = FileUtil.readTextFile(this.confFile, "utf-8");
		this.conf = Conf.read(confFile);
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
	 * args[1]	... テストURI。
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
