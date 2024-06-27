package jp.dataforms.test.component;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import jp.dataforms.test.selenium.Browser;


/**
 * テーブルのテスト要素。
 */
public class TableTestElement extends TestElement {
	/**
	 * コンストラクタ。
	 * @param browser ブラウザ。
	 * @param parent 親コンポーネント。
	 * @param element WebElement。
	 */
	public TableTestElement(final Browser browser, final TestElement parent, final WebElement element) {
		super(browser, parent, element);
	}

	/**
	 * 追加ボタンを取得します。
	 * @return 追加ボタン。
	 */
	public ButtonTestElement getAddButton() {
		String id = this.getId() + ".addButton";
		return this.getButton(id);
	}

	/**
	 * 追加ボタンを取得します。
	 * @param ridx 行インデックス。
	 * @return 追加ボタン。
	 */
	public ButtonTestElement getAddButton(final int ridx) {
		String id = this.getId() + "[" + ridx + "].addButton";
		return this.getButton(id);
	}

	/**
	 * 削除ボタンを取得します。
	 * @param ridx 行インデックス。
	 * @return 追加ボタン。
	 */
	public ButtonTestElement getDeleteButton(final int ridx) {
		String id = this.getId() + "[" + ridx + "].deleteButton";
		return this.getButton(id);
	}

	/**
	 * 行追加します。
	 */
	public void addRow() {
		ButtonTestElement addButton = this.getAddButton();
		addButton.click();
	}

	/**
	 * 行を追加します。
	 * @param ridx 行インデックス。
	 */
	public void addRow(int ridx) {
		ButtonTestElement addButton = this.getAddButton(ridx);
		addButton.click();
	}

	/**
	 * テーブルに値を設定します。
	 * @param ridx 行インデックス。
	 * @param id フィールドID。
	 * @param value 値。
	 */
	public void setValue(final int ridx, final String id, final String value) {
		FieldTestElement field = this.getField(ridx, id);
		field.setValue(value);
	}
	
	/**
	 * テーブルの行数を取得します。
	 * @return テーブルの行数。
	 */
	public int getRowCount() {
		String xpath = this.getXPathRange() + "//tbody/tr";
		List<WebElement> elements = this.findWebElements(By.xpath(xpath));
		return elements.size();
	}

}
