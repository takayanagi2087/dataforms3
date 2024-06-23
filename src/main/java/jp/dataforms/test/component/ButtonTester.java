package jp.dataforms.test.component;

import org.openqa.selenium.WebElement;

import jp.dataforms.test.selenium.Browser;

/**
 * ボタンのテスター。
 */
public class ButtonTester extends Tester {
	/**
	 * コンストラクタ。
	 * @param browser ブラウザ。
	 * @param parent 親コンポーネント。
	 * @param element WebElement。
	 */
	public ButtonTester(final Browser browser, final Tester parent, final WebElement element) {
		super(browser, parent, element);
	}

	/**
	 * ボタンをクリックします。
	 */
	public void click() {
		this.getWebElement().click();
	}

}
