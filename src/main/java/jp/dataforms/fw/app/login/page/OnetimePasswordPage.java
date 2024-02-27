package jp.dataforms.fw.app.login.page;

import java.util.Map;

import jp.dataforms.fw.app.base.page.BasePage;

/**
 * ワンタイムパスワード確認ページクラス。
 *
 */
public class OnetimePasswordPage extends BasePage {

	/**
	 * Logger。
	 */
//	private static Logger logger = LogManager.getLogger(OnetimePasswordPage.class);

	/**
	 * コンストラクタ。
	 */
	public OnetimePasswordPage() {
		this.addForm(new OnetimePasswordForm());
		this.setMenuItem(false);
	}

	@Override
	public boolean isAuthenticated(Map<String, Object> params) throws Exception {
		// TODO 自動生成されたメソッド・スタブ
		return super.isAuthenticated(params);
	}
}
