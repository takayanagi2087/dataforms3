package jp.dataforms.fw.app.user.dao;

import java.util.List;
import java.util.Map;

import jp.dataforms.fw.dao.Dao;
import jp.dataforms.fw.dao.JDBCConnectableObject;
import jp.dataforms.fw.dao.SingleTableQuery;

/**
 * WebAuthn Dao。
 */
public class WebAuthnDao extends Dao {
	/**
	 * コンストラクタ。
	 * @param cobj JDBC接続可能オブジェクト。
	 * @throws Exception 例外。
	 */
	public WebAuthnDao(final JDBCConnectableObject cobj) throws Exception {
		super(cobj);
	}
	
	/**
	 * WebAuthnTableを検索します。
	 * @param userId ユーザID。
	 * @return WebAuthnTableの検索結果。
	 * @throws Exception 例外。
	 */
	public List<Map<String, Object>> query(final Long userId) throws Exception {
		WebAuthnTable table = new WebAuthnTable();
		SingleTableQuery query = new SingleTableQuery(table);
		query.setComment("m.user_id = :user_id");
		WebAuthnTable.Entity e = new WebAuthnTable.Entity();
		e.setUserId(userId);
		query.setConditionData(e.getMap());
		return this.executeQuery(query);
	}
	
	/**
	 * データを更新します。
	 * @param data データ。
	 * @throws Exception 例外。
	 */
	public void update(final Map<String, Object> data) throws Exception {
		WebAuthnTable table = new WebAuthnTable();
		this.executeUpdate(table, data);
	}
	
	/**
	 * データを追加します。
	 * @param data データ。
	 * @throws Exception 例外。
	 */
	public void insert(final Map<String, Object> data) throws Exception {
		WebAuthnTable table = new WebAuthnTable();
		this.executeInsert(table, data);
	}
	
	/**
	 * loginIdからWebAuthnTableを取得します。
	 * @param loginId loginId。
	 * @return WebAuthn情報。
	 * @throws Exception 例外。
	 */
	public Map<String, Object> queryWebAuthnInfo(final String loginId) throws Exception {
		WebAuthnTable table = new WebAuthnTable();
		SingleTableQuery query = new SingleTableQuery(table);
		query.setCondition("m.user_id = (select user_id from user_info where login_id=:login_id)");
		UserInfoTable.Entity p = new UserInfoTable.Entity();
		p.setLoginId(loginId);
		query.setConditionData(p.getMap());
		Map<String, Object> ret = null;
		List<Map<String, Object>> list = this.executeQuery(query);
		if (list.size() > 0) {
			ret = list.get(0);
		}
		return ret;
	}
	
	/**
	 * WebAuthの情報を登録します。
	 * @param data WebAuth情報。
	 * @throws Exception 例外。
	 */
	public void regist(final Map<String, Object> data) throws Exception {
		WebAuthnTable.Entity e = new WebAuthnTable.Entity(data);
		Long userId = e.getUserId();
		List<Map<String, Object>> list = this.query(userId);
		if (list.size() > 0) {
			this.update(data);
		} else {
			this.insert(data);
		}
	}
}
