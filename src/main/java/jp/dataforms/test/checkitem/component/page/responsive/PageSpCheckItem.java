package jp.dataforms.test.checkitem.component.page.responsive;

import org.openqa.selenium.Dimension;

import jp.dataforms.fw.controller.Page;
import jp.dataforms.test.annotation.CheckItemInfo;
import jp.dataforms.test.component.Tester;
import jp.dataforms.test.selenium.Browser;

/**
 * ページの全面表示テスト。
 */
@CheckItemInfo(target = Page.class, group = ResponsiveCheckItem.GROUP, seq = "005")
public class PageSpCheckItem extends ResponsiveCheckItem {
	/**
	 * テスト条件。
	 */
	private static final String CONDITION = """
		スマートフォンレイアウトの最大幅で表示。
		""";
	
	/**
	 * テストの期待値。
	 */
	private static final String EXPECTED = """
		スマートフォンレイアウトとなること。
		""";
	/**
	 * コンストラクタ。
	 */
	public PageSpCheckItem() {
		super(CONDITION, EXPECTED);
	}
	
	@Override
	public ResultType  test(final Tester tester) throws Exception {
		
		Browser b = tester.getBrowser();
		b.setClientSize(new Dimension(SP_WIDTH, 800));
		
		return ResultType.USER_CHECK;
	}
}
