package jp.dataforms.fw.app.user.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jp.dataforms.fw.app.enumtype.dao.EnumDao;
import jp.dataforms.fw.dao.Dao;
import jp.dataforms.fw.dao.JDBCConnectableObject;
import jp.dataforms.fw.dao.Query;
import jp.dataforms.fw.dao.SingleTableQuery;
import jp.dataforms.fw.dao.sqlgen.SqlGenerator;
import jp.dataforms.fw.exception.ApplicationException;
import jp.dataforms.fw.field.base.FieldList;
import jp.dataforms.fw.field.common.SelectField;
import jp.dataforms.fw.util.CryptUtil;
import jp.dataforms.fw.util.JsonUtil;
import jp.dataforms.fw.util.UserInfoTableUtil;

/**
 *
 * ユーザ関連テーブルアクセスクラス。
 *
 */
public class UserDao extends Dao {

    /**
     * Logger.
     */
    private static Logger logger = LogManager.getLogger(UserDao.class.getName());


	/**
	 * コンストラクタ。
	 * @param obj JDBC接続可能オブジェクト。
	 * @throws Exception 例外。
	 */
	public UserDao(final JDBCConnectableObject obj) throws Exception {
		super(obj);
	}

	/**
	 * ユーザ属性のリストを取得します。
	 * @return ユーザ属性のリスト。
	 * @throws Exception 例外。
	 */
	public List<String> queryUserAttributeList() throws Exception {
		List<String> ret = new ArrayList<String>();
		EnumDao dao = new EnumDao(this);
		List<Map<String, Object>> list = dao.getTypeList("userAttribute", "default");
		for (Map<String, Object> m: list) {
			SelectField.OptionEntity e = new SelectField.OptionEntity(m);
			ret.add(e.getValue());
		}

		return ret;
	}


	/**
	 * ユーザの問い合わせを行います。
	 * @param flist 問い合わせフォームのフィールドリスト。
	 * @param data 問い合わせフォームのフィール入力データ。
	 * @return 検索結果。
	 * @throws Exception 例外。
	 *
	 */
	public Map<String, Object> queryUser(final FieldList flist, final Map<String, Object> data) throws Exception {
		data.put("userAttributeList", this.queryUserAttributeList());
		UserQuery query = new UserQuery(flist, data);
		String sortOrder = (String) data.get("sortOrder");
		FieldList sflist = query.getFieldList().getOrderByFieldList(sortOrder);
		if (sflist.size() == 0) {
			query.setOrderByFieldList(query.getMainTable().getPkFieldList());
		} else {
			query.setOrderByFieldList(sflist);
		}
		Map<String, Object> ret = executePageQuery(query);
		return ret;
	}


	/**
	 * ユーザの問い合わせを行ないます。
	 * @param flist フィールドリスト。
	 * @param data パラメータ。
	 * @return 検索結果。
	 * @throws Exception 例外。
	 */
	public List<Map<String, Object>> queryUserList(final FieldList flist, final Map<String, Object> data) throws Exception {
		data.put("userAttributeList", this.queryUserAttributeList());
		UserQuery query = new UserQuery(flist, data);
		return this.executeQuery(query);
	}


	/**
	 * テーブルに対する主キーの設定を行います。
	 *
	 * @param data 更新データ。
	 */
	private void setTablePrimaryKey(final Map<String, Object> data) {
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> list = (List<Map<String, Object>>) data.get("attTable");
		UserInfoTable.Entity e = new UserInfoTable.Entity(data);
		if (list != null) {
			for (Map<String, Object> m: list) {
				UserAttributeTable.Entity a = new UserAttributeTable.Entity(m);
//				m.put("userId", data.get("userId"));
				a.setUserId(e.getUserId());
			}
		}
	}


	/**
	 * ユーザ情報を新規登録します。
	 * @param data ユーザ情報。
	 * @throws Exception 例外。
	 */
	public void insertUser(final Map<String, Object> data) throws Exception {
//		data.put("password", CryptUtil.encrypt((String) data.get("password")));
		UserInfoTable.Entity e = new UserInfoTable.Entity(data);
		e.setPassword(CryptUtil.encryptUserPassword(e.getPassword()));

		UserInfoTable table = UserInfoTableUtil.newUserInfoTable(); //new UserInfoTable();
//		Long pk = this.getNewRecordId(table);
//		data.put("userId", pk);
		this.executeInsert(table, data);
		this.setTablePrimaryKey(data);
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> list = (List<Map<String, Object>>) data.get("attTable");
		if (list != null) {
			UserAttributeTable aftable = new UserAttributeTable();
			this.executeInsert(aftable, list);
		}
	}

	/**
	 * ユーザIDの存在チェックを行います。
	 * @param data データ。
	 * @param forUpdate 更新用の存在チェックの場合true。
	 * @return 存在する場合true。
	 * @throws Exception 例外。
	 */
	public boolean existLoginId(final Map<String, Object> data, final boolean forUpdate) throws Exception {
		UserInfoTable table = UserInfoTableUtil.newUserInfoTable(); //new UserInfoTable();
		return this.existRecord(table, new FieldList(table.getLoginIdField()), data, forUpdate);
	}

	/**
	 * 指定されたユーザIDのユーザ情報を取得する問い合わせクラスです。
	 */
	private static class GetUserIdQuery extends Query {
		/**
		 * コンストラクタ。
		 * @param data フォームから入力されたデータ。
		 */
		public GetUserIdQuery(final Map<String, Object> data) {
			UserInfoTable tbl = UserInfoTableUtil.newUserInfoTable(); //new UserInfoTable();
			this.setFieldList(tbl.getFieldList());
			this.setMainTable(tbl);
			this.setConditionFieldList(new FieldList(tbl.getUserIdField()));
			this.setConditionData(data);
		}
	}

	/**
	 * 指定されたユーザIDのユーザ情報を取得する問い合わせクラスです。
	 */
	private static class GetLoginIdQuery extends Query {
		/**
		 * コンストラクタ。
		 * @param data フォームから入力されたデータ。
		 */
		public GetLoginIdQuery(final Map<String, Object> data) {
			UserInfoTable tbl = UserInfoTableUtil.newUserInfoTable(); //new UserInfoTable();
			this.setFieldList(tbl.getFieldList());
			this.setMainTable(tbl);
			this.setConditionFieldList(new FieldList(tbl.getLoginIdField(), tbl.getPasswordField()));
			this.setConditionData(data);
		}
	}


	/**
	 *
	 * 指定されたユーザIDのユーザ属性情報を取得する問い合わせクラスです。
	 */
	private static class GetUserAttributeQuery extends Query {
		/**
		 * コンストラクタ。
		 * @param data フォームから入力されたデータ。
		 */
		public GetUserAttributeQuery(final Map<String, Object> data) {
			UserAttributeTable tbl = new UserAttributeTable();
			this.setFieldList(tbl.getFieldList());
			this.setMainTable(tbl);
			this.setConditionFieldList(new FieldList(tbl.getUserIdField()));
			this.setConditionData(data);
		}
	}

	/**
	 * 選択されたユーザ情報を取得します。
	 * @param data フォームから入力されたデータ。
	 * @return ユーザ情報。
	 * @throws Exception 例外。
	 */
	public Map<String, Object> getSelectedData(final Map<String, Object> data) throws Exception {
		Map<String, Object> ret = this.executeRecordQuery(new GetUserIdQuery(data));
		UserInfoTable.Entity e = new UserInfoTable.Entity(ret);
		List<Map<String, Object>> attTable = this.executeQuery(new GetUserAttributeQuery(data));
		ret.put("attTable", attTable);
//		ret.put("password", CryptUtil.decrypt((String) ret.get("password")));
		e.setPassword(CryptUtil.decryptUserPassword(e.getPassword()));
		ret.put("passwordCheck", e.getPassword());
		return ret;
	}

	/**
	 * 更新可能かどうかを判定します。
	 * @param data パラメータ。
	 * @return 更新可能な場合true。
	 * @throws Exception 例外。
	 */
	private boolean isUpdatableUser(final Map<String, Object> data) throws Exception {
		UserInfoTable tbl = UserInfoTableUtil.newUserInfoTable(); //new UserInfoTable();
		boolean ret = this.isUpdatable(tbl, data);
		if (ret) {
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> list = (List<Map<String, Object>>) data.get("attTable");
			if (list != null) {
				UserAttributeTable ftbl = new UserAttributeTable();
				for (Map<String, Object> m: list) {
					UserAttributeTable.Entity r = new UserAttributeTable.Entity(m);
					if (r.getUserId() != null) {
						if (!this.isUpdatable(ftbl, m)) {
							ret = false;
							break;
						}
					}
				}
			}
		}
		logger.debug("isUpdatableUser={}", ret);
		return ret;
	}

	/**
	 * Userのパスワードを取得します。
	 * @param userId ユーザID。
	 * @return 暗号化されたパスワード。
	 * @throws Exception 例外。
	 */
	public String getUserPassword(final Long userId) throws Exception {
		UserInfoTable table = new UserInfoTable();
		SingleTableQuery query = new SingleTableQuery(table);
		query.setConditionFieldList(new FieldList(table.getUserIdField()));
		UserInfoTable.Entity p = new UserInfoTable.Entity();
		p.setUserId(userId);
		query.setConditionData(p.getMap());
		Map<String, Object> user = this.executeRecordQuery(query);
		logger.info("userInfo=" + JsonUtil.encode(user, true));

		UserInfoTable.Entity e = new UserInfoTable.Entity(user);
		return e.getPassword();
	}

	/**
	 * ユーザ情報の更新を行ないます。
	 * @param data フォームデータ。
	 * @throws Exception 例外。
	 */
	public void updateUser(final Map<String, Object> data) throws Exception {
		if (!this.isUpdatableUser(data)) {
			throw new ApplicationException(this.getPage(), "error.notupdatable");
		}
//		data.put("password", CryptUtil.encrypt((String) data.get("password")));
		UserInfoTable.Entity e = new UserInfoTable.Entity(data);
		if (CryptUtil.getUserPasswordType() == CryptUtil.UserPasswordType.REVERSIBLE_PASSWORD) {
			// 可逆パスワードの場合はパスワードも更新可能。
			e.setPassword(CryptUtil.encryptUserPassword(e.getPassword()));
		} else {
			// 不可逆パスワードの場合はパスワードも更新できない。
			Long userId = e.getUserId();
			e.setPassword(this.getUserPassword(userId));
		}
		SqlGenerator gen = this.getSqlGenerator();
		UserInfoTable tbl = UserInfoTableUtil.newUserInfoTable(); //new UserInfoTable();
		String sql = gen.generateUpdateSql(tbl);
		logger.info("sql=" + sql);
		this.executeUpdate(sql, data);

		@SuppressWarnings("unchecked")
		List<Map<String, Object>> list = (List<Map<String, Object>>) data.get("attTable");
		if (list != null) {
			UserAttributeTable atbl = new UserAttributeTable();
			String delsql = gen.generateDeleteSql(atbl, new FieldList(atbl.getUserIdField()));
			this.executeUpdate(delsql, data);
			for (Map<String, Object> rec: list) {
				UserAttributeTable.Entity r = new UserAttributeTable.Entity(rec);
//				rec.put("userId", data.get("userId"));
				r.setUserId(e.getUserId());
			}
			this.executeInsert(atbl, list);
		}
	}

	/**
	 * パスワードを更新します。
	 * @param data 更新データ。
	 * @throws Exception 例外。
	 */
	public void updatePassword(final Map<String, Object> data) throws Exception {
//		data.put("password", CryptUtil.encrypt((String) data.get("password")));
		UserInfoTable.Entity e = new UserInfoTable.Entity(data);
		e.setPassword(CryptUtil.encryptUserPassword(e.getPassword()));
		UserInfoTable tbl = UserInfoTableUtil.newUserInfoTable(); //new UserInfoTable();
		this.executeUpdate(tbl,
			new FieldList(
				/*
				tbl.getField(UserInfoTable.Entity.ID_PASSWORD)
				, tbl.getField(UserInfoTable.Entity.ID_UPDATE_USER_ID)
				, tbl.getField(UserInfoTable.Entity.ID_UPDATE_TIMESTAMP)*/
				tbl.getPasswordField()
				, tbl.getUpdateUserIdField()
				, tbl.getUpdateTimestampField()
			),
			new FieldList(tbl.getField(UserInfoTable.Entity.ID_USER_ID)), data, true);
	}


	/**
	 * ユーザを有効にします。
	 * @param data POSTされたデータ。
	 * @throws Exception 例外。
	 */
	public void enableUser(final Map<String, Object> data) throws Exception {
		if (!this.isUpdatableUser(data)) {
			throw new ApplicationException(this.getPage(), "error.notupdatable");
		}
		UserInfoTable.Entity p = new UserInfoTable.Entity(data);
		UserInfoTable.Entity e = new UserInfoTable.Entity();
		e.setUserId(p.getUserId());
		e.setUpdateUserId(p.getUserId());
		e.setEnabledFlag("1");
		UserInfoTable tbl = UserInfoTableUtil.newUserInfoTable(); // new UserInfoTable();
		this.executeUpdate(tbl,
			new FieldList(
				tbl.getEnabledFlagField()
				, tbl.getUpdateUserIdField()
				, tbl.getUpdateTimestampField()
			),
			new FieldList(tbl.getUserIdField()), e.getMap(), true);
	}

	/**
	 * ユーザ情報変更ページで更新できるフィールドリストを取得します。
	 * @param table ユーザ情報テーブル。
	 * @return ユーザ情報変更ページで更新できるフィールドリスト。
	 */
	public static FieldList getSelfUpdateFieldList(final UserInfoTable table) {
		FieldList flist = new FieldList();
		flist.addAll(table.getFieldList());
		flist.remove(UserInfoTable.Entity.ID_USER_ID);
		flist.remove(UserInfoTable.Entity.ID_PASSWORD);
		flist.remove(UserInfoTable.Entity.ID_EXTERNAL_USER_FLAG);
		flist.remove(UserInfoTable.Entity.ID_ENABLED_FLAG);
		flist.remove(UserInfoTable.Entity.ID_DELETE_FLAG);
		flist.remove(UserInfoTable.Entity.ID_CREATE_USER_ID);
		flist.remove(UserInfoTable.Entity.ID_CREATE_TIMESTAMP);
		return flist;
	}

	/**
	 * ユーザ自身が更新できる項目を更新します。
	 * @param data 更新データ。
	 * @throws Exception 例外。
	 */
	public void updateSelfUser(final Map<String, Object> data) throws Exception {
//		UserInfoTable.Entity e = new UserInfoTable.Entity(data);
//		data.put("password", CryptUtil.encrypt((String) data.get("password")));
//		e.setPassword(CryptUtil.encrypt(e.getPassword()));
		UserInfoTable tbl = UserInfoTableUtil.newUserInfoTable(); //new UserInfoTable();
		FieldList flist = UserDao.getSelfUpdateFieldList(tbl);
		this.executeUpdate(tbl,
			flist,
			new FieldList(tbl.getField(UserInfoTable.Entity.ID_USER_ID)), data, true);
	}

	/**
	 * ユーザの削除処理を行います。
	 * @param data データ。
	 * @throws Exception 例外。
	 */
	public void deleteUser(final Map<String, Object> data) throws Exception {
		SqlGenerator gen = this.getSqlGenerator();

		UserAttributeTable atbl = new UserAttributeTable();
		String asql = gen.generateDeleteSql(atbl, new FieldList(atbl.getField(UserInfoTable.Entity.ID_USER_ID)));
		this.executeUpdate(asql, data);
		
		WebAuthnTable watbl = new WebAuthnTable();
		String wasql = gen.generateDeleteSql(watbl, new FieldList(watbl.getField(UserInfoTable.Entity.ID_USER_ID)));
		this.executeUpdate(wasql, data);

		
		UserInfoTable tbl = UserInfoTableUtil.newUserInfoTable(); // new UserInfoTable();
		String sql = gen.generateDeleteSql(tbl, new FieldList(tbl.getField(UserInfoTable.Entity.ID_USER_ID)));
		this.executeUpdate(sql, data);
	}

	/**
	 * ログインチェックを行ないます。
	 * @param data データ。
	 * @param passwordEncrypt パスワード暗号化を行う場合はtrue。
	 * @return ユーザ情報。
	 * @throws Exception 例外。
	 */
	public Map<String, Object> login(final Map<String, Object> data, final boolean passwordEncrypt) throws Exception {
		UserInfoTable.Entity e = new UserInfoTable.Entity(data);
		if (passwordEncrypt) {
			e.setPassword(CryptUtil.encryptUserPassword(e.getPassword()));
		}
		GetLoginIdQuery query = new GetLoginIdQuery(data);
		query.setCondition("m.enabled_flag='1'");
		Map<String, Object> rec = this.executeRecordQuery(query);
		if (rec != null) {
			data.put(UserInfoTable.Entity.ID_USER_ID, rec.get(UserInfoTable.Entity.ID_USER_ID));
			List<Map<String, Object>> attTable = this.executeQuery(new GetUserAttributeQuery(data));
			rec.put("attTable", attTable);
		} else {
			throw new ApplicationException(this.getWebEntryPoint(), "error.invaliduserid");
		}
		return rec;
	}

/*	public Map<String, Object> queryLoginInfo(final String loginId) throws Exception {
		
	}
*/	
	
	/**
	 * LoginIdに対応するpasswordを取得します。
	 * @param loginId loginId。
	 * @return パスワード。
	 * @throws Exception 例外。
	 */
	public String queryPassword(final String loginId) throws Exception {
		UserInfoTable table = new UserInfoTable();
		SingleTableQuery q = new SingleTableQuery(table);
		q.setCondition("m.login_id = :login_id");
		UserInfoTable.Entity p = new UserInfoTable.Entity();
		p.setLoginId(loginId);
		q.setConditionData(p.getMap());
		Map<String, Object> rec = this.executeRecordQuery(q);
		String password = (String) rec.get(UserInfoTable.Entity.ID_PASSWORD);
		return password;
	}
	
	/**
	 * ログインチェックを行ないます。
	 * @param data データ。
	 * @return ユーザ情報。
	 * @throws Exception 例外。
	 */
	public Map<String, Object> login(final Map<String, Object> data) throws Exception {
		return this.login(data, true);
	}
	/**
	 * userIdを指定して、そのユーザ情報を取得します。
	 * @param userId ユーザID。
	 * @return ユーザ情報マップ。
	 * @throws Exception 例外。
	 */
	public Map<String, Object> queryUserInfo(final Long userId) throws Exception {
		//Map<String, Object> p = new HashMap<String, Object>();
		UserInfoTable.Entity p = new UserInfoTable.Entity();
		p.setUserId(userId);
		return this.getSelectedData(p.getMap());
	}

	/**
	 * loginIdを指定して、そのユーザ情報を取得します。
	 * @param loginId ユーザID。
	 * @return ユーザ情報マップ。
	 * @throws Exception 例外。
	 */
	public Map<String, Object> queryUserInfo(final String loginId) throws Exception {
		UserInfoTable table = UserInfoTableUtil.newUserInfoTable(); // new UserInfoTable();
		FieldList flist = new FieldList(table.getLoginIdField());
		UserInfoTable.Entity e = new UserInfoTable.Entity();
		e.setLoginId(loginId);
		List<Map<String, Object>> list =  this.queryUserList(flist, e.getMap());
		if (list.size() > 0) {
			return this.getSelectedData(list.get(0));
		} else {
			return null;
		}
	}


	/**
	 * メールアドレスを指定してユーザを検索します。
	 * @param mail メールアドレス。
	 * @return ユーザリスト。
	 * @throws Exception 例外。
	 */
	public List<Map<String, Object>> queryUserListByMail(final String mail) throws Exception {
		UserInfoTable table = UserInfoTableUtil.newUserInfoTable(); //new UserInfoTable();
		FieldList flist = new FieldList(table.getMailAddressField());
		UserInfoTable.Entity e = new UserInfoTable.Entity();
		e.setMailAddress(mail);
		return this.queryUserList(flist, e.getMap());
	}

	/**
	 * パスワードの再暗号化を行います。
	 * @param algolithm 暗号化アルゴリズム。
	 * @param password パスワード/暗号化キー。
	 * @throws Exception 例外。
	 */
	public void reencryptPassword(final String algolithm, final String password) throws Exception {
		UserInfoTable table = UserInfoTableUtil.newUserInfoTable(); //new UserInfoTable();
		SingleTableQuery q = new SingleTableQuery(table);
		List<Map<String, Object>> list = this.executeQuery(q);
		for (Map<String, Object> m: list) {
			UserInfoTable.Entity e = new UserInfoTable.Entity(m);
			String enc = e.getPassword();
			String upass = CryptUtil.decrypt(enc);

			String nenc = null;
			if (CryptUtil.DES_ALGORITHM.equals(algolithm)) {
				nenc = CryptUtil.desEncrypt(upass, password);
			} else {
				nenc = CryptUtil.aesEncrypt(upass, password, CryptUtil.getAesInitialVector());
			}
			e.setPassword(nenc);
			FieldList uflist = new FieldList();
			uflist.addField(table.getPasswordField());
			this.executeUpdate(table, uflist, table.getPkFieldList(), e.getMap(), true);
		}
	}
}
