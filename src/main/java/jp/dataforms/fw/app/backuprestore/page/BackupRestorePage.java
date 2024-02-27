package jp.dataforms.fw.app.backuprestore.page;

import jp.dataforms.fw.app.base.page.AdminPage;
import jp.dataforms.fw.dao.Dao;
import jp.dataforms.fw.dao.Table;


/**
 * ページクラス。
 */
public class BackupRestorePage extends AdminPage {
	/**
	 * コンストラクタ。
	 */
	public BackupRestorePage() {
		this.addForm(new BackupForm());
		this.addForm(new RestoreForm());
	}

	/**
	 * Pageが配置されるパスを返します。
	 *
	 * @return Pageが配置されるパス。
	 */
	public String getFunctionPath() {
		return "/dataforms/app";
	}

	/**
	 * 操作対象テーブルクラスを取得します。
	 * <pre>
	 * ページjavaクラス作成で用のメソッドです。
	 * </pre>
	 * @return 操作対象テーブル。
	 */
	public Class<? extends  Table> getTableClass() {
		return Table.class;
	}

	/**
	 * テーブルを操作するためのDaoクラスを取得します。
	 * <pre>
	 * ページjavaクラス作成で用のメソッドです。
	 * </pre>
	 * @return Daoクラス。
	 */
	public Class<? extends Dao> getDaoClass() {
		return Dao.class;
	}

}
