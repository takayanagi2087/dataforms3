package jp.dataforms.test.component;


import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import jp.dataforms.test.selenium.Browser;

/**
 * ページのテスト要素。
 */
public class PageTestElement extends DataFormsTestElement {
	/**
	 * ログインボタンのID。
	 */
	private static final String ID_LOGOUT_BUTTON = "logoutButton";
	
	/**
	 * Logger.
	 */
	private static Logger logger = LogManager.getLogger(PageTestElement.class);

	/**
	 * コンストラクタ。
	 * @param browser ブラウザ。
	 * @param parent 親コンポーネント。
	 * @param element 要素。
	 */
	public PageTestElement(final Browser browser, final TestElement parent, final WebElement element) {
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
	public DialogTestElement getDialog(final String id, final Class<? extends DialogTestElement> cls) {
		try {
			WebElement element = this.getWebElement().findElement(By.xpath("//div[@data-id='" + id + "']"));
			DialogTestElement dialog = cls
					.getConstructor(Browser.class, TestElement.class, WebElement.class)
					.newInstance(this.getBrowser(), this, element);
			return dialog;
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
	public DialogTestElement  getDialog(final String id) {
		return this.getDialog(id, DialogTestElement.class);
	}

	/**
	 * AlertDialogを所得します。
	 * @return AlertDialog。
	 */
	public AlertDialogTestElement getAlertDialog() {
		return (AlertDialogTestElement) this.getDialog(AlertDialogTestElement.ID, AlertDialogTestElement.class);
	}
	
	/**
	 * ConfirmDialogを所得します。
	 * @return ConfirmDialog。
	 */
	public ConfirmDialogTestElement getConfirmDialog() {
		return (ConfirmDialogTestElement) this.getDialog(ConfirmDialogTestElement.ID, ConfirmDialogTestElement.class);
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

	/**
	 * エラーメッセージを取得します。
	 * @return エラーメッセージ。
	 */
	public String getErrorMessage() {
		String message = this.findWebElement(By.id("errorMessages")).getText().trim();
		return message;
	}
	
	/**
	 * エラーメッセージリストを取得します。
	 * @return エラーメッセージリスト。
	 */
	public List<String> getErrorMessageList() {
		List<String> list = new ArrayList<String>();
		String message = this.findWebElement(By.id("errorMessages")).getText().trim();
		String[] sp = message.split("\n");
		for (int i = 0; i < sp.length; i++) {
			list.add(sp[i].trim());
		}
		return list;
	}
	
	
	/**
	 * ログアウトボタンを取得します。
	 * @return ログアウトボタン。
	 */
	public ButtonTestElement getLogoutButton() {
		return this.getButton(ID_LOGOUT_BUTTON);
	}
}
