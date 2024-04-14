package jp.dataforms.fw.devtool.func.page;

import jp.dataforms.fw.devtool.base.page.DeveloperPage;

/**
 * 機能管理ページクラス。
 *
 */
public class FuncManagementPage extends DeveloperPage {
	
	/**
	 * Logger.
	 */
//	private static Logger logger = LogManager.getLogger(FuncManagementPage.class);
	
	/**
	 * コンストラクタ。
	 */
	public FuncManagementPage() {
		this.addForm(new FuncEditForm());
	}
}
