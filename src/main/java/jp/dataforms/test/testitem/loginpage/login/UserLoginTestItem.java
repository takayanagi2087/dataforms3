package jp.dataforms.test.testitem.loginpage.login;

import jp.dataforms.test.annotation.TestItemInfo;

/**
 * 管理者ログインテスト項目。
 */
@TestItemInfo(group = "login", seq = "003")
public class UserLoginTestItem extends LoginTestItem {
	/**
	 * Logger.
	 */
	// private static Logger logger = LogManager.getLogger(PadPasswordTestItem.class);

	/**
	 * テスト条件。
	 */
	private static final String CONDITION = """
		一般ユーザでログインする。
		""";

	/**
	 * 期待値。
	 */
	private static final String EXPECTED = """
		一般ユーザのサイトマップが表示されること。
		""";

	/**
	 * コンストラクタ。
	 */
	public UserLoginTestItem() {
		super(CONDITION, EXPECTED);
	}
	
	@Override
	protected String getLoginId() {
		return "user";
	}
}