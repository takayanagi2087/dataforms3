package jp.dataforms.test.testitem.loginpage;

import jp.dataforms.fw.app.login.page.LoginForm;
import jp.dataforms.fw.app.login.page.LoginPage;
import jp.dataforms.test.testitem.TestItem;

/**
 * LoginPage, LoginFormのテスト項目。
 */
public abstract class LoginFormTestItem extends TestItem {
	/**
	 * コンストラクタ。
	 * @param condition テスト条件。
	 * @param expected 期待値。
	 */
	public LoginFormTestItem(final String condition, final String expected) {
		super(LoginPage.class, LoginForm.class, condition, expected);
	}
}
