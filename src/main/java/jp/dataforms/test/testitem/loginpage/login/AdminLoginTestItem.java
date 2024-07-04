package jp.dataforms.test.testitem.loginpage.login;

import jp.dataforms.test.annotation.TestItemInfo;
import jp.dataforms.test.selenium.Browser;

/**
 * 管理者ログインテスト項目。
 */
@TestItemInfo(group = "login", seq = "002")
public class AdminLoginTestItem extends LoginTestItem {
	/**
	 * Logger.
	 */
	// private static Logger logger = LogManager.getLogger(PadPasswordTestItem.class);

	/**
	 * テスト条件。
	 */
	private static final String CONDITION = """
		管理者でログインする。
		""";

	/**
	 * 期待値。
	 */
	private static final String EXPECTED = """
		管理者のサイトマップが表示されること。
		""";

	/**
	 * コンストラクタ。
	 */
	public AdminLoginTestItem() {
		super(CONDITION, EXPECTED);
	}
	
	@Override
	protected String getLoginId() {
		return "admin";
	}
	
	@Override
	protected ResultType checkSiteMap(final Browser browser) {
		ResultType ret = ResultType.SYSTEM_NG;
		if (!this.findLink(browser, "/devtool/menu/page/MenuEditPage.df")) {
			// 開発者ページが表示されていないことを確認。
			ret = ResultType.SYSTEM_OK;
		}
		return ret;
	}

}
