package jp.dataforms.fw.app.login.page;

import java.util.Map;

import jp.dataforms.fw.app.base.page.BasePage;

/**
 * ログインページクラスです。
 *
 */
public class LoginPage extends BasePage {

    /**
     * Logger.
     */
//    private static Logger log = Logger.getLogger(LoginPage.class.getName());
	/**
	 * コンストラクタ。
	 */
	public LoginPage() {
		this.addForm(new LoginForm());
	}

	/**
	 * Pageのタイトルを返します。
	 *
	 * @return Pageのタイトル。
	 */
	@Override
	public String getPageName() {
		return "ログインページ";
	}


	/**
	 * {@inheritDoc}
	 * ログインしている場合メニューに表示しないように制御します。
	 */
	@Override
	public boolean isAuthenticated(final Map<String, Object> params) throws Exception {
		// ログインしている場合メニューに表示しない。
		return this.getUserInfo() != null ? false : true;
	}
}
