package jp.dataforms.test.checkitem.page.responsive;

import org.openqa.selenium.Dimension;

import jp.dataforms.fw.controller.Page;
import jp.dataforms.fw.controller.WebComponent;
import jp.dataforms.test.annotation.TestItemInfo;
import jp.dataforms.test.component.PageTestElement;
import jp.dataforms.test.selenium.Browser;

/**
 * ページの全面表示テスト。
 */
@TestItemInfo(group = ResponsiveTestItem.GROUP, seq = "005")
public class PageSpTestItem extends ResponsiveTestItem {
	/**
	 * テスト条件。
	 */
	private static final String CONDITION = """
		スマートフォンの画面幅で表示。
		""";
	
	/**
	 * テストの期待値。
	 */
	private static final String EXPECTED = """
		スマートフォンレイアウトとなること。
		""";
	/**
	 * コンストラクタ。
	 * @param pageClass ページクラス。
	 * @param compClass ページクラス。
	 */
	public PageSpTestItem(final Class<? extends Page> pageClass, final Class<? extends WebComponent> compClass) {
		super(pageClass, compClass, CONDITION, EXPECTED);
	}
	
	@Override
	protected ResultType  test(final Page page, final PageTestElement pageTestElement) throws Exception {
		
		Browser b = pageTestElement.getBrowser();
		b.setClientSize(new Dimension(SP_WIDTH, ResponsiveTestItem.getHeight()));
		
		return ResultType.USER_CHECK;
	}
}
