package jp.dataforms.fw.app.user.page;

import jp.dataforms.fw.app.base.page.AdminPage;

/**
 * ユーザ管理ページクラス。
 *
 */
public class UserManagementPage extends AdminPage {
	/**
	 * コンストラクタ。
	 */
	public UserManagementPage() {
		this.addForm(new UserQueryForm());
		this.addForm(new UserQueryResultForm());
		this.addForm(new UserEditForm(true));
	}
}
