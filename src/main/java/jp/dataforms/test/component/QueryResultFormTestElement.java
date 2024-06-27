package jp.dataforms.test.component;

import org.openqa.selenium.WebElement;

import jp.dataforms.test.selenium.Browser;

/**
 * 問合せ結果フォームのテスト要素。
 */
public class QueryResultFormTestElement extends FormTestElement {
	
	/**
	 * QueryResultFormのID。
	 */
	public static final String ID = "queryResultForm";

	/**
	 * 問合せ結果テーブルのID。
	 */
	public static final String ID_QUERY_RESULT = "queryResult";
	
	/**
	 * コンストラクタ。
	 * @param browser ブラウザ。
	 * @param parent 親コンポーネント。
	 * @param element WebElement。
	 */
	public QueryResultFormTestElement(final Browser browser, final TestElement parent, final WebElement element) {
		super(browser, parent, element);
	}
	
	/**
	 * 指定したインデックスのレコードを更新する。
	 * @param idx レコードのインデックス。
	 */
	public void update(final int idx) {
		TableTestElement table = this.getTable(ID_QUERY_RESULT);
		FieldTestElement link = table.getField(idx, "updateButton");
		link.click();
		this.getParent().waitVisibility(EditFormTestElement.ID);
	}
	
	/**
	 * 検索結果テーブルを取得します。
	 * @return 検索結果テーブル。
	 */
	public TableTestElement getQueryResultTable() {
		return this.getTable(ID_QUERY_RESULT);
	}

}
