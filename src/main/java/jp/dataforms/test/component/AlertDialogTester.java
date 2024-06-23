package jp.dataforms.test.component;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import jp.dataforms.test.selenium.Browser;

/**
 * AlertDialogのテスター。
 */
public class AlertDialogTester extends DialogTester {
	/**
	 * AlertDialogのID。
	 */
	public static final String ID = "alertDialog";

	/**
	 * OKボタンのID。
	 */
	public static final String ID_OK_BUTTON = "alertOkButton";

	/**
	 * コンストラクタ。
	 * @param browser ブラウザ。
	 * @param parent 親コンポーネント。
	 * @param element WebElement。
	 */
	public AlertDialogTester(final Browser browser, final Tester parent, final WebElement element) {
		super(browser, parent, element);
	}

	/**
	 * ボタンをクリックします。
	 */
	public void clickOkButton() {
		WebElement okButton = this.getBrowser().waitVisibilityOfElementLocated(By.xpath("//*[@data-id='" + ID_OK_BUTTON + "']"));
		okButton.click();
	}
}
