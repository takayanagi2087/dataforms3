package jp.dataforms.test.component;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import jp.dataforms.test.selenium.Browser;

/**
 * EditFormのテスト要素。
 */
public class EditFormTestElement extends FormTestElement {
	/**
	 * Logger.
	 */
	 private static Logger logger = LogManager.getLogger(EditFormTestElement.class);

	 /**
	  * フィールドID。
	  */
	 public static final String ID = "editForm";
	 
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
	public EditFormTestElement(final Browser browser, final TestElement parent, final WebElement webElement) {
		super(browser, parent, webElement);
	}

	/**
	 * 値を指定したフィールドに設定します。
	 * @param id フィールドID。
	 * @param value 値。
	 */
	public void setValue(final String id, final String value) {
		FieldTestElement field = this.getField(id);
		field.setValue(value);
	}
	
	/**
	 * テーブルを取得します。
	 * @param id ID。
	 * @return テーブルのインスタンス。
	 */
	public TableTestElement getTable(final String id) {
		String xpath = this.getXPathRange() + "//table[@data-id='" + id + "']";
		List<WebElement> elements = this.findWebElements(By.xpath(xpath));
		logger.debug("*** input elements=" + elements.size());
		if (elements.size() == 1) {
			return new TableTestElement(this.getBrowser(), this, elements.get(0));
		}
		return null;
	}

}
