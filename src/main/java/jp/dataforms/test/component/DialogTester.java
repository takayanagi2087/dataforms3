package jp.dataforms.test.component;

import org.openqa.selenium.WebElement;

import jp.dataforms.test.selenium.Browser;

/**
 * Dialogのテスター。
 */
public class DialogTester extends DataFormsTester {
	/**
	 * コンストラクタ。
	 * @param browser ブラウザ。
	 * @param parent 親コンポーネント。
	 * @param element WebElement。
	 */
	public DialogTester(final Browser browser, final Tester parent, final WebElement element) {
		super(browser, parent, element);
	}

}
