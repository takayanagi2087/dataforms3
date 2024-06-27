package jp.dataforms.test.component;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import jp.dataforms.test.selenium.Browser;

/**
 * ConfirmDialogのテスト要素。
 */
public class ConfirmDialogTestElement extends DialogTestElement {
	/**
	 * ConfirmDialogのID。
	 */
	public static final String ID = "confirmDialog";

	/**
	 * OKボタンのID。
	 */
	public static final String ID_OK_BUTTON = "confirmOkButton";

	/**
	 * キャンセルボタンのID。
	 */
	public static final String ID_CANCEL_BUTTON = "confirmCancelButton";


	/**
	 * コンストラクタ。
	 * @param browser ブラウザ。
	 * @param parent 親コンポーネント。
	 * @param element WebElement。
	 */
	public ConfirmDialogTestElement(final Browser browser, final TestElement parent, final WebElement element) {
		super(browser, parent, element);
	}

	/**
	 * OKボタンをクリックします。
	 */
	public void clickOkButton() {
		WebElement okButton = this.getBrowser().waitVisibilityOfElementLocated(By.xpath("//*[@data-id='" + ID_OK_BUTTON + "']"));
		okButton.click();
	}

	/**
	 * キャンセルボタンをクリックします。
	 */
	public void clickCancelButton() {
		WebElement cancelButton = this.getBrowser().waitVisibilityOfElementLocated(By.xpath("//*[@data-id='" + ID_CANCEL_BUTTON + "']"));
		cancelButton.click();
	}

}
