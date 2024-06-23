package jp.dataforms.test.component;

import org.openqa.selenium.WebElement;

import jp.dataforms.test.selenium.Browser;

/**
 * 問合せフォームのテスター。
 */
public class QueryFormTester extends FormTester{
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
	public QueryFormTester(final Browser browser, final Tester parent, final WebElement element) {
		super(browser, parent, element);
	}


	/**
	 * 検索を実行します。
	 */
	public void query() {
		ButtonTester queryButton = this.getButton(ID_QUERY_BUTTON);
		queryButton.click();
		DataFormsTester parent = (DataFormsTester) this.getParent();
		parent.waitVisibility(QueryResultFormTester.ID);
	}

	/**
	 * 新規登録を実行します。
	 */
	public void newData() {
		ButtonTester newButton = this.getButton(ID_NEW_BUTTON);
		newButton.click();
		DataFormsTester parent = (DataFormsTester) this.getParent();
		parent.waitVisibility(EditFormTester.ID);
	}

}
