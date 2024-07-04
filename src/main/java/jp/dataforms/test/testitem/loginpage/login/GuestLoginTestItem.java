package jp.dataforms.test.testitem.loginpage.login;

import jp.dataforms.test.annotation.TestItemInfo;
import jp.dataforms.test.selenium.Browser;

/**
 * ゲストログインテスト項目。
 */
@TestItemInfo(group = "login", seq = "004")
public class GuestLoginTestItem extends LoginTestItem {
	/**
	 * Logger.
	 */
	// private static Logger logger = LogManager.getLogger(PadPasswordTestItem.class);

	/**
	 * テスト条件。
	 */
	private static final String CONDITION = """
		ゲストユーザでログインする。
		""";

	/**
	 * 期待値。
	 */
	private static final String EXPECTED = """
		ゲストユーザのサイトマップが表示されること。
		""";

	/**
	 * コンストラクタ。
	 */
	public GuestLoginTestItem() {
		super(CONDITION, EXPECTED);
	}
	
	@Override
	protected String getLoginId() {
		return "guest";
	}
	
	@Override
	protected ResultType checkSiteMap(final Browser browser) {
		ResultType ret = ResultType.SYSTEM_NG;
		if (!this.findLink(browser, "/app/user/page/ChangePasswordPage.df")) {
			// ユーザページが表示されていないことを確認。
			ret = ResultType.SYSTEM_OK;
		}
		return ret;
	}
	
}
