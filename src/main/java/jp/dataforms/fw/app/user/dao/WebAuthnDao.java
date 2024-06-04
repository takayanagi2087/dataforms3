package jp.dataforms.fw.app.user.dao;

import java.util.List;
import java.util.Map;

import jp.dataforms.fw.dao.Dao;
import jp.dataforms.fw.dao.JDBCConnectableObject;
import jp.dataforms.fw.dao.SingleTableQuery;
import jp.dataforms.fw.util.WebAuthnUtil;

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
		query.setCondition("m.user_id = :user_id");
		WebAuthnTable.Entity e = new WebAuthnTable.Entity();
		e.setUserId(userId);
		query.setConditionData(e.getMap());
		List<Map<String, Object>> list = this.executeQuery(query);
		int no = 1;
		for (Map<String, Object> m: list) {
			m.put("rowNo", no++);
			byte flags = WebAuthnUtil.getFlags(m);
			if ((flags & WebAuthnUtil.BE_FLAG) != 0) {
				m.put("beFlag", "1");
			} else {
				m.put("beFlag", "0");
			}
			if ((flags & WebAuthnUtil.BS_FLAG) != 0) {
				m.put("bsFlag", "1");
			} else {
				m.put("bsFlag", "0");
			}
		}
		return list;
	}
	

	/**
	 * WebAuthnTableを検索します。
	 * @param userId ユーザID。
	 * @param an 認証器の名称。
	 * @return WebAuthnTableの検索結果。
	 * @throws Exception 例外。
	 */
	public List<Map<String, Object>> query(final Long userId, final String an) throws Exception {
		WebAuthnTable table = new WebAuthnTable();
		SingleTableQuery query = new SingleTableQuery(table);
		query.setCondition("m.user_id = :user_id and m.authenticator_name = :authenticator_name");
		WebAuthnTable.Entity e = new WebAuthnTable.Entity();
		e.setUserId(userId);
		e.setAuthenticatorName(an);
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
	 * LoginIdに対応したPassKeyのリストを取得する。
	 * @param loginId ログインID。
	 * @return PassKeyのリスト。
	 * @throws Exception 例外。
	 */
	public List<Map<String, Object>> query(final String loginId) throws Exception {
		WebAuthnTable table = new WebAuthnTable();
		SingleTableQuery query = new SingleTableQuery(table);
		query.setCondition("m.user_id = (select user_id from user_info where login_id=:login_id)");
		UserInfoTable.Entity p = new UserInfoTable.Entity();
		p.setLoginId(loginId);
		query.setConditionData(p.getMap());
		List<Map<String, Object>> list = this.executeQuery(query);
		return list;
	}
	
	
	/**
	 * loginIdからWebAuthnTableを取得します。
	 * @param loginId loginId。
	 * @param authenticatorName 認証器の名称。
	 * @return WebAuthn情報。
	 * @throws Exception 例外。
	 */
	public Map<String, Object> queryWebAuthnInfo(final String loginId, final String authenticatorName) throws Exception {
		WebAuthnTable table = new WebAuthnTable();
		SingleTableQuery query = new SingleTableQuery(table);
		query.setCondition("m.user_id = (select user_id from user_info where login_id=:login_id) and m.authenticator_name = :authenticator_name");
		UserInfoTable.Entity p = new UserInfoTable.Entity();
		p.setLoginId(loginId);
		p.getMap().put(WebAuthnTable.Entity.ID_AUTHENTICATOR_NAME, authenticatorName);
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
		if (e.getWebAuthnId() != null) {
			this.update(data);
		} else {
			this.insert(data);
		}
	}
	
	/**
	 * 認証器の削除を行います。
	 * @param webAuthenId 認証器のID。
	 * @throws Exception 例外。
	 */
	public void delete(final Long webAuthenId) throws Exception {
		WebAuthnTable table = new WebAuthnTable();
		WebAuthnTable.Entity p = new WebAuthnTable.Entity();
		p.setWebAuthnId(webAuthenId);
		this.executeDelete(table, p.getMap());
	}
}
