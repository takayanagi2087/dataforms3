package jp.dataforms.test.component;

import java.time.Duration;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import jp.dataforms.test.selenium.Browser;
import lombok.Getter;

/**
 * テスト要素の基本クラス。
 */
public class TestElement {
	/**
	 * Logger.
	 */
	private static Logger logger = LogManager.getLogger(TestElement.class);

	/**
	 * TIMEOUT。
	 */
	public static final Duration TIMEOUT = Duration.ofSeconds(30);

	/**
	 * 親コンポーネント。
	 */
	@Getter
	private TestElement parent = null;

	/**
	 * ブラウザ。
	 */
	@Getter
	private Browser browser = null;

	/**
	 * Web 要素。
	 */
	@Getter
	private WebElement webElement = null;

	/**
	 * コンストラクタ。
	 * @param browser ブラウザ。
	 * @param parent 親コンポーネント。
	 * @param element WebElement。
	 */
	public TestElement(final Browser browser, final TestElement parent, final WebElement element) {
		this.browser = browser;
		this.parent = parent;
		this.webElement = element;
	}

	/**
	 * ページのテスターを取得します。
	 * @return ページのテスター。
	 */
	public PageTestElement getPage() {
		PageTestElement page = null;
		for (TestElement c = this; c != null; c = c.parent) {
			if (c instanceof PageTestElement) {
				page = (PageTestElement) c;
				break;
			}
		}
		return page;
	}
	
	/**
	 * IDを取得します。
	 * @return ID。
	 */
	public String getId() {
		String id = this.webElement.getAttribute("data-id");
		return id;
	}

	/**
	 * ID限定条件。
	 * @return XPathの限定条件を取得する。
	 */
	protected String getXPathRange() {
		String ret = "";
		if (this.parent != null) {
			ret = this.parent.getXPathRange();
			String id = this.webElement.getDomAttribute("data-id");
			ret += "//*[@data-id='" + id + "']";
		}
		return ret;
	}

	/**
	 * SelenumのWebElementを検索します。
	 * @param by 検索条件。
	 * @return SelenumのWebElement。
	 */
	public WebElement findWebElement(By by) {
		return this.webElement.findElement(by);
	}

	/**
	 * SelenumのWebElementを検索します。
	 * @param by 検索条件。
	 * @return SelenumのWebElementのリスト。
	 */
	public List<WebElement> findWebElements(By by) {
		return this.webElement.findElements(by);
	}

	/**
	 * クリックします。
	 */
	public void click() {
		this.getWebElement().click();
	}


	/**
	 * 指定されたIDの要素が現れるまで待機する。
	 * @param id 検索方法。
	 * @return 待機したSelenumのWebElement。
	 */
	public WebElement waitVisibility(final String id) {
		WebDriverWait wait = new WebDriverWait(this.getBrowser().getWebDriver(), TIMEOUT);
		String xpath = this.getXPathRange();
		if (id != null) {
			xpath += "//*[@data-id='" + id + "']";
		}
		logger.debug("xpath=" + xpath);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
		WebElement element = this.getBrowser().getWebDriver().findElement(By.xpath(xpath));
		return element;
	}

	/**
	 * フィールドを取得します。
	 * @param id フィールドID。
	 * @return フィールド。
	 */
	public FieldTestElement getField(final String id) {
		logger.debug("*** getField=" + this.getWebElement().getAttribute("id"));
		String xpath = this.getXPathRange() + "//*[@data-id='" + id + "']";
		logger.debug("*** xpath=" + xpath);
		List<WebElement> elements = this.getBrowser().getWebDriver().findElements(By.xpath(xpath));
		logger.debug("*** count=" + elements.size());
		for (WebElement el: elements) {
			logger.debug("el.id=" + el.getAttribute("id"));
		}
		if (elements.size() == 1) {
			WebElement element = elements.get(0);
			return new FieldTestElement(this.getBrowser(), this, element);
		}
		return null;
	}

	
	/**
	 * ボタンを取得します。
	 * @param id ID。
	 * @return Button。
	 */
	public ButtonTestElement getButton(final String id) {
		String xpath = this.getXPathRange() + "//*[@data-id='" + id + "']";
		logger.debug("getButton xpath=" + xpath);
		List<WebElement> elements = this.findWebElements(By.xpath(xpath));
		logger.debug("*** input elements=" + elements.size());
		if (elements.size() == 1) {
			return new ButtonTestElement(this.getBrowser(), this, elements.get(0));
		}
		return null;
	}

	/**
	 * 削除ボタンを取得します。
	 * @param ridx 行インデックス。
	 * @param id フィールドID。
	 * @return フィールド。
	 */
	public FieldTestElement getField(final int ridx, final String id) {
		String fid = this.getId() + "[" + ridx + "]." + id;
		return this.getField(fid);
	}
}
