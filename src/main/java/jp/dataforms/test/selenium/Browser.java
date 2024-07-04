package jp.dataforms.test.selenium;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.google.common.io.Files;

import jp.dataforms.test.component.PageTestElement;
import lombok.Getter;
import lombok.Setter;

/**
 * ブラウザクラス。
 *
 */
public class Browser {
	/**
	 * Logger.
	 */
	private static Logger logger = LogManager.getLogger(Browser.class);

	/**
	 * ヘッドレスフラグ。
	 */
	@Setter
	@Getter
	private static boolean headless = false;

	/**
	 * ブラウザ情報。
	 */
	@Getter
	private BrowserInfo browserInfo = null;

	/**
	 * Webドライバ。
	 */
	@Getter
	private WebDriver webDriver = null;


	/**
	 * タイムアウト(秒)。
	 */
	public static final int DEFAULT_TIMEOUT_SEC = 30;
	
	/**
	 * TIMEOUT。
	 */
	public static final Duration TIMEOUT = Duration.ofSeconds(DEFAULT_TIMEOUT_SEC);


	/**
	 * コンストラクタ。
	 * @param binfo ブラウザ情報。
	 * @throws Exception 例外。
	 */
	public Browser(final BrowserInfo binfo) throws Exception {
		this.browserInfo = binfo;
		this.webDriver = this.newWebDriver();
	}

	/**
	 * WebDriverのインスタンス作成します。
	 * @return WebDriverのインスタンス。
	 * @throws Exception 例外。
	 */
	private WebDriver newWebDriver() throws Exception {
		System.setProperty(this.browserInfo.getPropName(), this.browserInfo.getDriver());
		String cls = this.browserInfo.getClassName();
		logger.info("className=" + cls);
		@SuppressWarnings("unchecked")
		Class<? extends WebDriver> driverClass = (Class<? extends WebDriver>) Class.forName(cls);
		logger.info("driverClass=" + driverClass);
		if (!Browser.headless) {
			WebDriver webDriver = driverClass.getConstructor().newInstance();
			return webDriver;
		} else {
			String optcls = this.browserInfo.getOptionClassName();
			logger.debug("optionClass=" + optcls);
			Class<?> optionClass = (Class<?>) Class.forName(optcls);
			Method m = optionClass.getMethod("addArguments", List.class);
			Object opt = optionClass.getConstructor().newInstance();
			List<String> argList = new ArrayList<String>();
			argList.add("--headless");
			m.invoke(opt, argList); // opt.addArguments("--headless");
			WebDriver webDriver = driverClass.getConstructor(optionClass).newInstance(opt);
			webDriver.manage().window().setSize(new Dimension(1280, 800));
			return webDriver;
		}
	}

	/**
	 * ブラウザを最大化します。
	 */
	public void maximize() {
		if (Browser.headless) {
			// headlessモードの場合FullHDに設定する。
			this.webDriver.manage().window().setSize(new Dimension(1720, 1080));
		} else {
			this.webDriver.manage().window().maximize();
		}
	}

	/**
	 * ブラウザを最小化します。
	 */
	public void minimize() {
		this.webDriver.manage().window().minimize();
	}

	/**
	 * ウインドウサイズを設定します。
	 * @param dim サイズ情報。
	 */
	public void setSize(final Dimension dim) {
		this.webDriver.manage().window().setSize(dim);
	}

	/**
	 * クライアントサイズを設定します。
	 * @param dim サイズ情報。
	 */
	public void setClientSize(Dimension dim) {
		this.setSize(dim);
		int w = dim.width;
		int h = dim.height;

		WebElement el = this.getWebDriver().findElement(By.cssSelector("html"));
		int width = Integer.parseInt(el.getAttribute("clientWidth"));
		int height = Integer.parseInt(el.getAttribute("clientHeight"));
		int dw = w - width;
		int dh = h - height;
		this.setSize(new Dimension(w + dw, h + dh));
	}
	
	/**
	 * ページを開きます。
	 * @param url URL。
	 * @return ページのインスタンス。
	 */
	public PageTestElement open(final String url) {
		By locator = By.xpath("//body");
		return this.open(url, locator);
	}

	/**
	 * ページを開きます。
	 * @param url URL。
	 * @param id ページが表示されたと判定する要素ID
	 * @return ページのインスタンス。
	 */
	public PageTestElement open(final String url, final String id) {
		String xpath = "//*[@data-id='" + id + "']";
		logger.debug("xpath=" + xpath);
		return this.open(url, By.xpath(xpath));
	}

	
	/**
	 * ページを開きます。
	 * @param url URL。
	 * @param locator ページが表示されたと判定する要素の指定。
	 * @return ページのインスタンス。
	 */
	public PageTestElement open(final String url, final By locator) {
		this.webDriver.get(url);
		WebDriverWait wait = new WebDriverWait(webDriver, TIMEOUT);
		wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
		Browser.sleep(5);
		WebElement element = this.webDriver.findElement(By.xpath("//body"));
		PageTestElement page = new PageTestElement(this, null, element);
		return page;
	}

	/**
	 * ページを取得します。
	 * @return ページテスト要素。
	 */
	public PageTestElement getPageTestElement() {
		WebElement element = this.webDriver.findElement(By.xpath("//body"));
		PageTestElement page = new PageTestElement(this, null, element);
		return page;
	}
	/**
	 * HTML要素を検索する。
	 * @param by 検索条件。
	 * @return 見つけた要素。
	 */
	public WebElement findElement(final By by) {
		return this.webDriver.findElement(by);
	}

	/**
	 * HTML要素を検索する。
	 * @param id 検索条件。
	 * @return 見つけた要素。
	 */
	public WebElement findElement(final String id) {
		return this.webDriver.findElement(By.id(id));
	}


	/**
	 * 指定されたIDの要素が現れるまで待機する。
	 * @param by 検索方法。
	 * @return 待機したWebElement。
	 */
	public WebElement waitVisibilityOfElementLocated(final By by) {
		WebDriverWait wait = new WebDriverWait(this.webDriver, TIMEOUT);
		wait.until(ExpectedConditions.visibilityOfElementLocated(by));
		WebElement element = this.webDriver.findElement(by);
		return element;
	}

	/**
	 * 指定されたIDの要素が現れるまで待機する。
	 * @param id 検索方法。
	 * @return 待機したWebElement。
	 */
	public WebElement waitVisibilityOfElementLocated(final String id) {
		WebDriverWait wait = new WebDriverWait(this.webDriver, TIMEOUT);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(id)));
		WebElement element = this.webDriver.findElement(By.id(id));
		return element;
	}


	/**
	 * 指定された要素が表示されるのを待機する。
	 * @param by 検索方法。
	 * @return 待機したWebElement。
	 *
	 */
	public WebElement waitVisibility(final By by) {
		WebDriverWait wait = new WebDriverWait(this.webDriver, TIMEOUT);
		WebElement element = this.webDriver.findElement(by);
		wait.until(ExpectedConditions.visibilityOf(element));
		return element;
	}

	/**
	 * 指定された要素のIDが表示されるのを待機する。
	 * @param id 要素のID。
	 * @return 待機したWebElement。
	 */
	public WebElement waitVisibility(final String id) {
		return this.waitVisibility(By.id(id));
	}


	/**
	 * 時間待ち。
	 * @param sec 秒数。
	 */
	public static void sleep(int sec) {
		try {
			Thread.sleep(sec * 1000);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}


	/**
	 * 画面サイズを調整しスクリーンショットを保存します。
	 * @param filename ファイル名。
	 * @param width 画像の幅指定。
	 * @return 保存したファイルのパス。
	 * @throws Exception 例外。
	 */
	public String saveResizedScreenShot(final String filename, final int width) throws Exception {
		Dimension orgDim = this.webDriver.manage().window().getSize();
		WebElement body = this.webDriver.findElement(By.xpath("//body"));
		int w = orgDim.getWidth();
		if (width > 0) {
			w = width;
		}
		int h = body.getSize().getHeight() + 160;
		Dimension dim = new Dimension(w, h);
		this.webDriver.manage().window().setSize(dim);
		String ret = this.saveScreenShot(filename);
		this.webDriver.manage().window().setSize(orgDim);
		return ret;
	}

	/**
	 * スクリーンショットを保存します。
	 * @param filename ファイル名。
	 * @return 保存したファイルのパス。
	 * @throws Exception 例外。
	 *
	 */
	public String saveResizedScreenShot( final String filename) throws Exception {
		return this.saveResizedScreenShot(filename, -1);
	}

	/**
	 * スクリーンショットの取得のみを行います。。
	 * @param filename ファイル名。
	 * @return 保存したファイルのパス。
	 * @throws IOException 例外。
	 *
	 */
	public String saveScreenShot(final String filename) throws IOException {
		Browser.sleep(2);
	    File sfile = ((TakesScreenshot) this.webDriver).getScreenshotAs(OutputType.FILE);
	    
		logger.debug("screenShot=" + filename);

		File file = new File(filename);
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		Files.move(sfile, file);
		Browser.sleep(2);
		return file.getAbsolutePath();
	}

	/**
	 * ブラウザを閉じます。
	 */
	public void close() {
		this.webDriver.close();
	}


	/**
	 * ブラウザのリロードを行います。
	 * @return ページのテスト要素。
	 */
	public PageTestElement reload() {
		this.webDriver.navigate().refresh();
		Browser.sleep(5);
		By locator = By.xpath("//body");
		this.waitVisibility(locator);
		WebElement element = this.webDriver.findElement(By.xpath("//body"));
		return new PageTestElement(this, null, element);
	}

	/**
	 * ページを取得します。
	 * @return フォーム。
	 */
/*	public Page getPage() {
		WebElement element = this.webDriver.findElement(By.xpath("//body"));
		Page page = new Page(this, null, element);
		return page;
	}*/
}
