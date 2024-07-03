package jp.dataforms.test.testitem.loginpage.login;

import org.openqa.selenium.Dimension;

import jp.dataforms.fw.controller.Page;
import jp.dataforms.test.annotation.TestItemInfo;
import jp.dataforms.test.selenium.Browser;

/**
 * 開発者ログインテスト項目。
 */
@TestItemInfo(group = "login", seq = "001")
public class DeveloperLoginTestItem extends LoginTestItem {
	/**
	 * Logger.
	 */
	// private static Logger logger = LogManager.getLogger(PadPasswordTestItem.class);

	/**
	 * テスト条件。
	 */
	private static final String CONDITION = """
		開発者でログインする。
		""";

	/**
	 * 期待値。
	 */
	private static final String EXPECTED = """
		開発者のサイトマップが表示されること。
		""";

	/**
	 * コンストラクタ。
	 */
	public DeveloperLoginTestItem() {
		super(CONDITION, EXPECTED);
	}
	
	@Override
	protected String getLoginId() {
		return "devuser";
	}
	
	@Override
	protected void saveResult(Page page, Browser browser, ResultType result) throws Exception {
		browser.setClientSize(new Dimension(1024, 800));
		super.saveResult(page, browser, result);
	}
}
