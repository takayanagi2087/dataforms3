package jp.dataforms.test.checkitem.component.page.responsive;

import jp.dataforms.fw.controller.Page;
import jp.dataforms.test.annotation.CheckItemInfo;
import jp.dataforms.test.component.TestElement;
import jp.dataforms.test.selenium.Browser;

/**
 * ページの全面表示テスト。
 */
@CheckItemInfo(target = Page.class, group = ResponsiveCheckItem.GROUP, seq = "001")
public class PagePcMaxCheckItem extends ResponsiveCheckItem {
	/**
	 * テスト条件。
	 */
	private static final String CONDITION = """
		ページを全面表示する。
		""";
	
	/**
	 * テストの期待値。
	 */
	private static final String EXPECTED = """
		PCレイアウトとなること。
		""";
	/**
	 * コンストラクタ。
	 */
	public PagePcMaxCheckItem() {
		super(CONDITION, EXPECTED);
	}
	
	@Override
	public ResultType  test(final TestElement tester) throws Exception {
		Browser b = tester.getBrowser();
		b.maximize();
		return ResultType.USER_CHECK;
	}
}
