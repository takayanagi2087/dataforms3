package jp.dataforms.fw.app.user.dialog;

import jp.dataforms.fw.app.user.page.UserQueryForm;
import jp.dataforms.fw.app.user.page.UserQueryResultForm;
import jp.dataforms.fw.controller.Dialog;

/**
 * ユーザ問い合わせダイアログクラス。
 * <pre>
 * ユーザを検索し、入力するためのダイアログです。
 * </pre>
 *
 */
public class UserQueryDialog extends Dialog {

	/**
	 * コンストラクタ。
	 */
	public UserQueryDialog() {
		super("userQueryDialog");
		this.addForm(new UserQueryForm());
		this.addForm(new UserQueryResultForm());
	}
}
