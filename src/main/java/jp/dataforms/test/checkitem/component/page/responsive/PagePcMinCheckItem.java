package jp.dataforms.test.checkitem.component.page.responsive;

import org.openqa.selenium.Dimension;

import jp.dataforms.fw.controller.Page;
import jp.dataforms.test.annotation.CheckItemInfo;
import jp.dataforms.test.component.TestElement;
import jp.dataforms.test.selenium.Browser;

/**
 * ページの全面表示テスト。
 */
@CheckItemInfo(target = Page.class, group = ResponsiveCheckItem.GROUP, seq = "002")
public class PagePcMinCheckItem extends ResponsiveCheckItem {
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
	 */
	public PagePcMinCheckItem() {
		super(CONDITION, EXPECTED);
	}
	
	@Override
	public ResultType  test(final Page page, final TestElement tester) throws Exception {
		
		Browser b = tester.getBrowser();
		b.setClientSize(new Dimension(PC_MIN_WIDTH, ResponsiveCheckItem.getHeight()));
		
		return ResultType.USER_CHECK;
	}
}
