package jp.dataforms.fw.app.user.page;

import jp.dataforms.fw.app.base.page.UserPage;

/**
 * パスワード変更ページ。
 *
 */
public class ChangePasswordPage extends UserPage {
	/**
	 * コンストラクタ。
	 */
	public ChangePasswordPage() {
		this.addForm(new ChangePasswordForm(false));
	}
}
