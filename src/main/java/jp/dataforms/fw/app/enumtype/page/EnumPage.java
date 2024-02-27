package jp.dataforms.fw.app.enumtype.page;

import jp.dataforms.fw.app.base.page.AdminPage;
import jp.dataforms.fw.app.enumtype.dao.EnumDao;
import jp.dataforms.fw.app.enumtype.dao.EnumTable;
import jp.dataforms.fw.dao.Dao;
import jp.dataforms.fw.dao.Table;


/**
 * ページクラス。
 */
public class EnumPage extends AdminPage {
	/**
	 * コンストラクタ。
	 */
	public EnumPage() {
		this.addForm(new EnumQueryForm());
		this.addForm(new EnumQueryResultForm());
		this.addForm(new EnumEditForm());

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
	 * ページjavaクラス作成用のメソッドです。
	 * </pre>
	 * @return 操作対象テーブル。
	 */
	public Class<? extends  Table> getTableClass() {
		return EnumTable.class;
	}

	/**
	 * テーブルを操作するためのDaoクラスを取得します。
	 * <pre>
	 * ページjavaクラス作成用のメソッドです。
	 * </pre>
	 * @return Daoクラス。
	 */
	public Class<? extends Dao> getDaoClass() {
		return EnumDao.class;
	}

}
