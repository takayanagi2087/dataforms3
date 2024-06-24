package jp.dataforms.test.component;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import jp.dataforms.test.selenium.Browser;

/**
 * フォームのテスター。
 */
public class FormTester extends Tester {
	/**
	 * Logger.
	 */
	 private static Logger logger = LogManager.getLogger(FormTester.class);

	/**
	 * リセットボタンのID。
	 */
	public static final String ID_RESET_BUTTON = "resetButton";

	/**
	 * 戻るボタンのID。
	 */
	public static final String ID_BACK_BUTTON = "backButton";
	
	/**
	 * コンストラクタ。
	 * @param browser ブラウザ。
	 * @param parent 親コンポーネント。
	 * @param webElement FormのWebElement。
	 */
	public FormTester(final Browser browser, final Tester parent, final WebElement webElement) {
		super(browser, parent, webElement);
	}

	/**
	 * 値を指定したフィールドに設定します。
	 * @param id フィールドID。
	 * @param value 値。
	 */
	public void setValue(final String id, final String value) {
		FieldTester field = this.getField(id);
		field.setValue(value);
	}
	
	/**
	 * テーブルを取得します。
	 * @param id ID。
	 * @return テーブルのインスタンス。
	 */
	public TableTester getTable(final String id) {
		String xpath = this.getXPathRange() + "//table[@data-id='" + id + "']";
		List<WebElement> elements = this.findWebElements(By.xpath(xpath));
		logger.debug("*** input elements=" + elements.size());
		if (elements.size() == 1) {
			return new TableTester(this.getBrowser(), this, elements.get(0));
		}
		return null;
	}

}