package jp.dataforms.fw.app.user.page;

import jp.dataforms.fw.app.base.page.BasePage;

/**
 * パスワードリセットメール送信フォームクラス。
 *
 */
public class PasswordResetMailPage extends BasePage {
	/**
	 * コンストラクタ。
	 */
	public PasswordResetMailPage() {
		this.addForm(new PasswordResetMailForm());
	}
}
