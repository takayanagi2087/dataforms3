package jp.dataforms.test.checkitem.component.page.responsive;

import org.openqa.selenium.Dimension;

import jp.dataforms.fw.controller.Page;
import jp.dataforms.fw.controller.WebComponent;
import jp.dataforms.test.annotation.TestItemInfo;
import jp.dataforms.test.component.TestElement;
import jp.dataforms.test.selenium.Browser;

/**
 * ページの全面表示テスト。
 */
@TestItemInfo(group = ResponsiveTestItem.GROUP, seq = "002")
public class PagePcMinTestItem extends ResponsiveTestItem {
	/**
	 * テスト条件。
	 */
	private static final String CONDITION = """
		PCレイアウトの最小幅で表示。
		""";
	
	/**
	 * テストの期待値。
	 */
	private static final String EXPECTED = """
		PCレイアウトとなること。
		""";
	/**
	 * コンストラクタ。
	 * @param pageClass ページクラス。
	 * @param compClass ページクラス。
	 */
	public PagePcMinTestItem(final Class<? extends Page> pageClass, final Class<? extends WebComponent> compClass) {
		super(pageClass, compClass, CONDITION, EXPECTED);
	}
	
	@Override
	public ResultType  test(final Page page, final TestElement tester) throws Exception {
		
		Browser b = tester.getBrowser();
		b.setClientSize(new Dimension(PC_MIN_WIDTH, ResponsiveTestItem.getHeight()));
		
		return ResultType.USER_CHECK;
	}
}
