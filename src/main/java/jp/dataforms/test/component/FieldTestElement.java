package jp.dataforms.test.component;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import jp.dataforms.test.selenium.Browser;

/**
 * フィールドのテスト要素。
 */
public class FieldTestElement extends TestElement {
	/**
	 * コンストラクタ。
	 * @param browser ブラウザ。
	 * @param parent 親コンポーネント。
	 * @param element WebElement。
	 */
	public FieldTestElement(final Browser browser, final TestElement parent, final WebElement element) {
		super(browser, parent, element);
	}


	/**
	 * 値を指定したフィールドに設定します。
	 * @param value 値。
	 */
	public void setValue(final String value) {
		WebElement element = this.getWebElement();
		String tagName = element.getTagName().toLowerCase();
		if ("select".equals(tagName)) {
			Select select = new Select(element);
			select.selectByValue(value);
		} else {
			element.click();
			element.clear();
			element.sendKeys(value);
		}
	}

	/**
	 * フィールドの値を取得します。
	 * @return フィールドの値。
	 */
	public String getValue() {
		WebElement element = this.getWebElement();
		return element.getAttribute("value");
	}

}
