package jp.dataforms.test.checkitem.component.page.responsive;

import org.openqa.selenium.Dimension;

import jp.dataforms.fw.controller.Page;
import jp.dataforms.test.annotation.TestItemInfo;
import jp.dataforms.test.component.TestElement;
import jp.dataforms.test.selenium.Browser;

/**
 * ページの全面表示テスト。
 */
@TestItemInfo(group = ResponsiveTestItem.GROUP, seq = "004")
public class PageTabMinTestItem extends ResponsiveTestItem {
	/**
	 * テスト条件。
	 */
	private static final String CONDITION = """
		タブレットレイアウトの最大幅で表示。
		""";
	
	/**
	 * テストの期待値。
	 */
	private static final String EXPECTED = """
		タブレットレイアウトとなること。
		""";
	/**
	 * コンストラクタ。
	 */
	public PageTabMinTestItem() {
		super(CONDITION, EXPECTED);
	}
	
	@Override
	public ResultType  test(final Page page, final TestElement tester) throws Exception {
		
		Browser b = tester.getBrowser();
		b.setClientSize(new Dimension(TAB_MIN_WIDTH, ResponsiveTestItem.getHeight()));
		
		return ResultType.USER_CHECK;
	}
}
