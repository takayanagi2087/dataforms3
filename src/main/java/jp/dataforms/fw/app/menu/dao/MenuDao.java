package jp.dataforms.fw.app.menu.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jp.dataforms.fw.app.func.dao.FuncInfoQuery;
import jp.dataforms.fw.app.func.dao.FuncInfoTable;
import jp.dataforms.fw.dao.Dao;
import jp.dataforms.fw.dao.JDBCConnectableObject;


/**
 * メニュー表示用の機能テーブルアクセスクラス。
 */
public class MenuDao extends Dao {

    /**
     * Logger.
     */
	//private static Logger log = Logger.getLogger(MenuDao.class.getName());


	/**
	 * コンストラクタ。
	 * @param obj JDBC接続可能オブジェクト。
	 * @throws Exception 例外。
	 */
	public MenuDao(final JDBCConnectableObject obj) throws Exception {
		super(obj);
	}

	/**
	 * 機能の一覧を取得します。
	 * @return 機能の一覧。
	 * @throws Exception 例外。
	 */
	public List<Map<String, Object>> getFuncList() throws Exception {
		if (!this.tableExists((new FuncInfoTable()).getTableName())) {
			return new ArrayList<Map<String, Object>>();
		}
		List<Map<String, Object>> funcList = this.executeQuery(new FuncInfoQuery());
		return funcList;
	}
}
