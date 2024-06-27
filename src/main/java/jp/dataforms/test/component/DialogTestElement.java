package jp.dataforms.test.component;

import org.openqa.selenium.WebElement;

import jp.dataforms.test.selenium.Browser;

/**
 * Dialogのテスト要素。
 */
public class DialogTestElement extends DataFormsTestElement {
	/**
	 * コンストラクタ。
	 * @param browser ブラウザ。
	 * @param parent 親コンポーネント。
	 * @param element WebElement。
	 */
	public DialogTestElement(final Browser browser, final TestElement parent, final WebElement element) {
		super(browser, parent, element);
	}

}
