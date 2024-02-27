package jp.dataforms.fw.devtool.db.page;

import jp.dataforms.fw.devtool.base.page.DeveloperPage;

/**
 * DB管理ページクラス。
 *
 */
public class TableManagementPage extends DeveloperPage {
	/**
	 * コンストラクタ。
	 */
	public TableManagementPage() {
		this.addForm(new DatabaseInfoForm());
		this.addForm(new TableManagementQueryForm());
		this.addForm(new TableManagementQueryResultForm());
		this.addDialog(new TableInfoDialog());
		this.addDialog(new ImportDataDialog());
	}
}
