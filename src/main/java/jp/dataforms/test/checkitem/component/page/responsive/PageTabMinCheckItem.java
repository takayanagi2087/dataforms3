package jp.dataforms.test.checkitem.component.page.responsive;

import org.openqa.selenium.Dimension;

import jp.dataforms.fw.controller.Page;
import jp.dataforms.test.annotation.CheckItemInfo;
import jp.dataforms.test.component.Tester;
import jp.dataforms.test.selenium.Browser;

/**
 * ページの全面表示テスト。
 */
@CheckItemInfo(target = Page.class, group = ResponsiveCheckItem.GROUP, seq = "004")
public class PageTabMinCheckItem extends ResponsiveCheckItem {
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
	public PageTabMinCheckItem() {
		super(CONDITION, EXPECTED);
	}
	
	@Override
	public ResultType  test(final Tester tester) throws Exception {
		
		Browser b = tester.getBrowser();
		b.setClientSize(new Dimension(TAB_MIN_WIDTH, 800));
		
		return ResultType.USER_CHECK;
	}
}
