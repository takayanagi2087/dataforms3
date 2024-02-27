package jp.dataforms.fw.devtool.func.page;

import jp.dataforms.fw.devtool.base.page.DeveloperPage;

/**
 * 機能管理ページクラス。
 *
 */
public class FuncManagementPage extends DeveloperPage {
	/**
	 * コンストラクタ。
	 */
	public FuncManagementPage() {
		this.addForm(new FuncEditForm());
	}
}
