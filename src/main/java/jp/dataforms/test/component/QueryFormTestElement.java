package jp.dataforms.test.component;

import org.openqa.selenium.WebElement;

import jp.dataforms.test.selenium.Browser;

/**
 * 問合せフォームのテスト要素。
 */
public class QueryFormTestElement extends FormTestElement{
	/**
	 * 問い合わせフォーム。
	 */
	public static final String ID = "queryForm";

	/**
	 * 検索ボタンのID。
	 */
	public static final String ID_QUERY_BUTTON = "queryButton";

	/**
	 * エクスボードボタンのID。
	 */
	public static final String ID_EXPORT_BUTTON = "exportButton";

	/**
	 * 新規登録ボタンのID。
	 */
	public static final String ID_NEW_BUTTON = "newButton";

	/**
	 * コンストラクタ。
	 * @param browser ブラウザ。
	 * @param parent 親コンポーネント。
	 * @param element WebElement。
	 */
	public QueryFormTestElement(final Browser browser, final TestElement parent, final WebElement element) {
		super(browser, parent, element);
	}


	/**
	 * 検索を実行します。
	 */
	public void query() {
		ButtonTestElement queryButton = this.getButton(ID_QUERY_BUTTON);
		queryButton.click();
		DataFormsTestElement parent = (DataFormsTestElement) this.getParent();
		parent.waitVisibility(QueryResultFormTestElement.ID);
	}

	/**
	 * 新規登録を実行します。
	 */
	public void newData() {
		ButtonTestElement newButton = this.getButton(ID_NEW_BUTTON);
		newButton.click();
		DataFormsTestElement parent = (DataFormsTestElement) this.getParent();
		parent.waitVisibility(EditFormTestElement.ID);
	}

}
