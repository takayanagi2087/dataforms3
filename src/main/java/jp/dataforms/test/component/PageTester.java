package jp.dataforms.test.component;


import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import jp.dataforms.fw.controller.WebComponent;
import jp.dataforms.test.selenium.Browser;

/**
 * ページのテスター。
 */
public class PageTester extends DataFormsTester {
	/**
	 * Logger.
	 */
	private static Logger logger = LogManager.getLogger(PageTester.class);

	/**
	 * コンストラクタ。
	 * @param browser ブラウザ。
	 * @param parent 親コンポーネント。
	 * @param element 要素。
	 */
	public PageTester(final Browser browser, final Tester parent, final WebElement element) {
		super(browser, parent, element);
	}

	@Override
	protected String getXPathRange() {
		return "";
	}

	/**
	 * ダイアログのテスターを取得します。
	 * @param id ID。
	 * @param cls ダイアログクラス。
	 * @return ダイアログ。
	 */
	public DialogTester getDialog(final String id, final Class<? extends DialogTester> cls) {
		try {
			WebElement element = this.getWebElement().findElement(By.xpath("//div[@data-id='" + id + "']"));
			DialogTester form = cls.getConstructor(Browser.class, WebComponent.class, WebElement.class).newInstance(this.getBrowser(), this, element);
			return form;
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
		}
		return null;
	}

	/**
	 * ダイアログのテスターを取得します。
	 * @param id ID。
	 * @return ダイアログ。
	 */
	public DialogTester  getDialog(final String id) {
		return this.getDialog(id, DialogTester.class);
	}

	/**
	 * AlertDialogを所得します。
	 * @return AlertDialog。
	 */
	public AlertDialogTester getAlertDialog() {
		return (AlertDialogTester) this.getDialog(AlertDialogTester.ID, AlertDialogTester.class);
	}
	
	/**
	 * ConfirmDialogを所得します。
	 * @return ConfirmDialog。
	 */
	public ConfirmDialogTester getConfirmDialog() {
		return (ConfirmDialogTester) this.getDialog(ConfirmDialogTester.ID, ConfirmDialogTester.class);
	}
	
	/**
	 * 全てのサイドメニューグループをクリックします。
	 */
	public void clickAllMenuGroup() {
		WebDriver driver = this.getBrowser().getWebDriver();
		List<WebElement> list = driver.findElements(By.xpath("//p[@class='sideMenuGroup']"));
		for (WebElement e: list) {
			e.click();
		}
	}

	/**
	 * 全てのサイドメニューグループをクリックします。
	 * @param name メニューグループ名称。
	 */
	public void clickMenuGroup(final String name) {
		WebDriver driver = this.getBrowser().getWebDriver();
		WebElement element = driver.findElement(By.xpath("//p[contains(text(), '" + name + "')]"));
		element.click();
	}

}
