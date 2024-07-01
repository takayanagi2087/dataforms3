package jp.dataforms.test.component;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import jp.dataforms.test.selenium.Browser;

/**
 * AlertDialogのテスト要素。
 */
public class AlertDialogTestElement extends DialogTestElement {
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
	public AlertDialogTestElement(final Browser browser, final TestElement parent, final WebElement element) {
		super(browser, parent, element);
	}

	/**
	 * メッセージを取得します。
	 * @return メッセージ。
	 */
	public String getMessage() {
		String text = this.findWebElement(By.id("mainDiv.alertDialog.alertMessage")).getText().trim();
		return text;
	}
	
	/**
	 * ボタンをクリックします。
	 */
	public void clickOkButton() {
		WebElement okButton = this.getBrowser().waitVisibilityOfElementLocated(By.xpath("//*[@data-id='" + ID_OK_BUTTON + "']"));
		okButton.click();
	}
}
