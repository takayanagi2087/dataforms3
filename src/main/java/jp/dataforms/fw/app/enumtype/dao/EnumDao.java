package jp.dataforms.fw.app.enumtype.dao;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jp.dataforms.fw.app.enumtype.field.EnumCodeField;
import jp.dataforms.fw.app.enumtype.page.EnumEditForm;
import jp.dataforms.fw.dao.Dao;
import jp.dataforms.fw.dao.JDBCConnectableObject;
import jp.dataforms.fw.dao.Query;
import jp.dataforms.fw.exception.ApplicationException;
import jp.dataforms.fw.field.base.Field.MatchType;
import jp.dataforms.fw.field.base.FieldList;
import jp.dataforms.fw.field.common.LangCodeField;
import jp.dataforms.fw.field.common.RowNoField;
import jp.dataforms.fw.field.common.SelectField;
import jp.dataforms.fw.field.sqlfunc.AliasField;
import jp.dataforms.fw.servlet.DataFormsServlet;
import jp.dataforms.fw.util.StringUtil;

/**
 * Daoクラス。
 *
 */
public class EnumDao extends Dao {
	/**
	 * Logger.
	 */
//	private static Logger logger = Logger.getLogger(EnumDao.class);

	/**
	 * コンストラクタ。
	 * @param obj JDBC接続可能オブジェクト。
	 * @throws Exception 例外。
	 */
	public EnumDao(final JDBCConnectableObject obj) throws Exception {
		super(obj);
	}

	/**
	 * 問い合わせ結果フォームのフィールドリストを取得します。
	 * @return 問い合わせ結果フォームのフィールドリスト。
	 */
	public static FieldList getQueryResultFieldList() {
		EnumTableQuery query = new EnumTableQuery();
		FieldList list = new FieldList();
		list.addField(new RowNoField());
		list.addAll(query.getFieldList());
		return list;
	}

	/**
	 * QueryFormから入力された条件から、テーブルを検索し、指定されたページの情報を返します。
	 * @param data 条件データ。
	 * @param flist 条件フィールドリスト。
	 * @return 検索結果。
	 * @throws Exception 例外。
	 */
	public Map<String, Object> queryPage(final Map<String, Object> data, final FieldList flist) throws Exception {
		EnumTableQuery query = new EnumTableQuery((Long) null);
		query.setConditionFieldList(flist);
		query.setConditionData(data);
		String sortOrder = (String) data.get("sortOrder");
		FieldList sflist = query.getFieldList().getOrderByFieldList(sortOrder);
		if (sflist.size() == 0) {
			query.setOrderByFieldList(query.getMainTable().getPkFieldList());
		} else {
			query.setOrderByFieldList(sflist);
		}
		return this.executePageQuery(query);
	}

	/**
	 * QueryFormから入力された条件から、テーブルを検索し、マッチするすべてのデータを返します。
	 * @param data 条件データ。
	 * @param flist 条件フィールドリスト。
	 * @return 検索結果。
	 * @throws Exception 例外。
	 */
	public List<Map<String, Object>> query(final Map<String, Object> data, final FieldList flist) throws Exception {
		EnumTableQuery query = new EnumTableQuery();
		query.setConditionFieldList(flist);
		query.setConditionData(data);
		return this.executeQuery(query);
	}

	/**
	 * 名前を取得します。
	 * @param data データ。
	 * @throws Exception 例外。
	 */
	private void queryName(final Map<String, Object> data) throws Exception {
		EnumTable.Entity e = new EnumTable.Entity(data);
		Long enumId = e.getEnumId();
		List<Map<String, Object>> list = this.executeQuery(new EnumNameTableQuery(enumId));
		for (Map<String, Object> m: list) {
			EnumNameTable.Entity ne = new EnumNameTable.Entity(m);
			String lang = ne.getLangCode();
			if (LangCodeField.DEFAULT.equals(lang)) {
				data.put("enumName", ne.getEnumName());
			} else {
				data.put(lang + "EnumName", ne.getEnumName());
			}
		}
	}

	/**
	 * PKでレコードを限定し、データを取得します。
	 * @param data 条件データ PKの情報をすべて含むマップ。
	 * @return ヒットしたレコード。
	 * @throws Exception 例外。
	 */
	public Map<String, Object> query(final Map<String, Object> data) throws Exception {
		EnumTableQuery query = new EnumTableQuery();
		query.setConditionFieldList(query.getMainTable().getPkFieldList());
		query.setConditionData(data);
		Map<String, Object> ret = this.executeRecordQuery(query);
		this.queryName(ret);
		EnumTable.Entity p = new EnumTable.Entity(ret);
		List<Map<String, Object>> optionList = this.executeQuery(new EnumTableQuery(p.getEnumId()));
		for (Map<String, Object> m: optionList) {
			this.queryName(m);
		}
		ret.put(EnumEditForm.ID_OPTION_TABLE, optionList);
		return ret;
	}

	/**
	 * 列挙型名称テーブルを削除します。
	 * @param enumId 列挙型のID。
	 * @throws Exception 例外。
	 */
	private void deleteEnumTypeName(final Long enumId) throws Exception {
		String sql = "delete from enum_name where enum_id=:enum_id";
		EnumTable.Entity p = new EnumTable.Entity();
		p.setEnumId(enumId);
		this.executeUpdate(sql, p.getMap());
	}

	/**
	 * 列挙型名称テーブルを削除します。
	 * @param parentId 列挙型のID。
	 * @throws Exception 例外。
	 */
	private void deleteEnumOptionName(final Long parentId) throws Exception {
		String sql = "delete from enum_name where enum_id in (select enum_id from enum where parent_id=:parent_id)";
		EnumTable.Entity p = new EnumTable.Entity();
		p.setParentId(parentId);
		this.executeUpdate(sql, p.getMap());
	}

	/**
	 * 列挙型名称テーブルを更新します。
	 * @param data オプションデータ。
	 * @throws Exception 例外。
	 */
	private void saveEnumName(final Map<String, Object> data) throws Exception {
		EnumTable.Entity e = new EnumTable.Entity(data);
		Long enumId = e.getEnumId();
		List<String> langList = DataFormsServlet.getSupportLanguage();
		EnumNameTable table = new EnumNameTable();
		{
			EnumNameTable.Entity ne = new EnumNameTable.Entity();
			ne.setEnumId(enumId);
			ne.setLangCode(LangCodeField.DEFAULT);
			ne.setEnumName((String) data.get(EnumNameTable.Entity.ID_ENUM_NAME));
			ne.setCreateUserId(e.getCreateUserId());
			ne.setUpdateUserId(e.getUpdateUserId());
			this.executeInsert(table, ne.getMap());
		}
		for (String lang: langList) {
			EnumNameTable.Entity ne = new EnumNameTable.Entity();
			ne.setEnumId(enumId);
			ne.setLangCode(lang);
			String name = (String) (String) data.get(lang + "EnumName");
			if (!StringUtil.isBlank(name)) {
				ne.setEnumName(name);
				ne.setCreateUserId(e.getCreateUserId());
				ne.setUpdateUserId(e.getUpdateUserId());
				this.executeInsert(table, ne.getMap());
			}
		}
	}

	/**
	 * 列挙型名称テーブルを更新します。
	 * @param list オプションデータリスト。
	 * @throws Exception 例外。
	 */
	private void saveEnumName(final List<Map<String, Object>> list) throws Exception {
		for (Map<String, Object> m: list) {
			this.saveEnumName(m);
		}
	}

	/**
	 * 選択肢リストを更新します。
	 * @param table テーブル。
	 * @param data データ。
	 * @throws Exception 例外。
	 */
	private void updateOptionList(EnumTable table, final Map<String, Object> data) throws Exception {
		EnumTable.Entity e = new EnumTable.Entity(data);
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> list = (List<Map<String, Object>>) data.get(EnumEditForm.ID_OPTION_TABLE);
		for (Map<String, Object> m: list) {
			m.put(EnumTable.Entity.ID_PARENT_ID, e.getEnumId());
			m.put(EnumTable.Entity.ID_ENUM_GROUP_CODE, e.getEnumGroupCode());
		}
		this.deleteEnumOptionName(e.getEnumId());
		EnumTable.Entity p = new EnumTable.Entity();
		p.setParentId(e.getEnumId());
		FieldList flist = new FieldList();
		flist.addField(table.getParentIdField());
		this.saveTable(new EnumTable(), list, p.getMap(), flist);
		this.saveEnumName(list);

	}

	/**
	 * 列挙型コードリストの問合せ。
	 *
	 */
	private static class EnumTypeCodeQuery extends Query {
		/**
		 * コンストラクタ。
		 */
		public EnumTypeCodeQuery() {
			EnumTable table = new EnumTable();
			this.setFieldList(table.getFieldList());
			this.setMainTable(table);
			this.setCondition("m.parent_id is null");
		}
	}

	/**
	 * 列挙型コードの多重登録チェック。
	 * @throws Exception 例外。
	 */
	private void validateEnumTypeCode() throws Exception {
		Set<String> set = new HashSet<String>();
		List<Map<String, Object>> list = this.executeQuery(new EnumTypeCodeQuery());
		for (Map<String, Object> m: list) {
			EnumTable.Entity e = new EnumTable.Entity(m);
//			logger.debug("EnumTypeCode=" + e.getEnumCode());
			if (set.contains(e.getEnumCode())) {
				throw new ApplicationException(this.getPage(), "error.duplicateenumcode");
			}
			set.add(e.getEnumCode());
		}

	}


	/**
	 * データを追加します。
	 * @param data データ。
	 * @return 追加件数。
	 * @throws Exception 例外。
	 */
	public int insert(final Map<String, Object> data) throws Exception {
		EnumTable table = new EnumTable();
		int ret = this.executeInsert(table, data);
		if (ret == 1) {
			this.saveEnumName(data);
			this.updateOptionList(table, data);
		}
		this.validateEnumTypeCode();
		return ret;
	}

	/**
	 * データを更新します。
	 * @param data データ。
	 * @return 更新件数。
	 * @throws Exception 例外。
	 */
	public int update(final Map<String, Object> data) throws Exception {
		// 楽観ロックチェック
		EnumTable table = new EnumTable();
		boolean ret = this.isUpdatable(table, data);
		if (!ret) {
			throw new ApplicationException(this.getPage(), "error.notupdatable");
		}
		// データ更新
		int cnt = this.executeUpdate(table, data);
		if (cnt > 0) {
			EnumTable.Entity e = new EnumTable.Entity(data);
			this.deleteEnumTypeName(e.getEnumId());
			this.saveEnumName(data);
			this.updateOptionList(table, data);
		}
		this.validateEnumTypeCode();
		return cnt;
	}

	/**
	 * データを削除します。
	 * @param data データ。
	 * @return 削除件数。
	 * @throws Exception 例外。
	 */
	public int delete(final Map<String, Object> data) throws Exception {
		EnumTable table = new EnumTable();
		EnumTable.Entity e = new EnumTable.Entity(data);
		this.deleteEnumTypeName(e.getEnumId());
		this.deleteEnumOptionName(e.getEnumId());
		EnumTable.Entity p = new EnumTable.Entity();
		p.setParentId(e.getEnumId());
		FieldList flist = new FieldList();
		flist.addField(table.getParentIdField());
		this.executeDelete(table, flist, p.getMap(), true); // オプションの削除
		int ret = this.executeDelete(table, data); // レコードの物理削除
		return ret;
	}

	/**
	 * 列挙型オプションの問い合わせクラスです。
	 *
	 */
	private static class EnumGroupQuery extends Query {
		/**
		 * コンストラクタ。
		 * @param data パラメータ。
		 */
		public EnumGroupQuery(final Map<String, Object> data) {
			EnumNameTable mntbl = new EnumNameTable();
			EnumTable ttbl = new EnumTable();
			ttbl.getEnumGroupCodeField().setMatchType(MatchType.PART);
			ttbl.setAlias("t");
			// 取得フィールドの設定.
			this.setFieldList(new FieldList(
				new AliasField("value", ttbl.getField(EnumTable.Entity.ID_ENUM_CODE))
				, new AliasField("name", mntbl.getEnumNameField())
			));
			this.setMainTable(ttbl);
//			this.setJoinTableList(new TableList(mntbl));
			this.addInnerJoin(mntbl);
			this.setCondition("t.parent_id is null");
			this.setConditionFieldList(new FieldList(ttbl.getEnumGroupCodeField(), mntbl.getLangCodeField()));
			this.setConditionData(data);
			this.setOrderByFieldList(new FieldList(ttbl.getEnumGroupCodeField()));
		}

		/**
		 * パラメータマップを作成します。
		 * @param enumGroupCode 列挙型グループコード。
		 * @param langCode 言語コード。
		 * @return パラメータマップ。
		 */
		private static Map<String, Object> getParamaterMap(final String enumGroupCode, final String langCode) {
			EnumTable.Entity e = new EnumTable.Entity();
			EnumNameTable.Entity ne = new EnumNameTable.Entity(e.getMap());
			e.setEnumGroupCode(enumGroupCode);
			ne.setLangCode(langCode);
			return e.getMap();
		}

		/**
		 * コンストラクタ。
		 * @param enumGroupCode 列挙型グループコード。
		 * @param langCode 言語コード。
		 */
		public EnumGroupQuery(final String enumGroupCode, final String langCode) {
			this(EnumGroupQuery.getParamaterMap(enumGroupCode, langCode));
		}
	}

	/**
	 * デフォルト言語リストをブラウザ言語リストで更新する。
	 * @param deflist デフォルト言語リスト。
	 * @param list ブラウザ言語リスト。
	 * @return 更新されたリスト。
	 */
	private List<Map<String, Object>> updateList(final List<Map<String, Object>> deflist, final List<Map<String, Object>> list) {
		for (Map<String, Object> m: list) {
			SelectField.OptionEntity e = new SelectField.OptionEntity(m);
			for (Map<String, Object> dm: deflist) {
				SelectField.OptionEntity de = new SelectField.OptionEntity(dm);
				if (de.getValue().equals(e.getValue())) {
					de.setName(e.getName());
					break;
				}
			}
		}
		return deflist;
	}

	/**
	 * 指定された列挙型グループの列挙型一覧を取得します。
	 * @param enumGroupCode 列挙型グルーブコード。
	 * @param langCode 言語コード。
	 * @return 列挙型の一覧。
	 * @throws Exception 例外。
	 */
	public List<Map<String, Object>> getTypeList(final String enumGroupCode, final String langCode) throws Exception {
		List<Map<String, Object>> deflist = this.executeQuery(new EnumGroupQuery(enumGroupCode, "default"));
		List<Map<String, Object>> langlist = this.executeQuery(new EnumGroupQuery(enumGroupCode, langCode));
		deflist = this.updateList(deflist, langlist);
		return deflist;
	}

	/**
	 * SelectFieldの選択肢に変換します。
	 * @param optlist 選択肢リスト。
	 * @return 変換されたリスト。
	 */
	public List<Map<String, Object>>  convertSelectOption(final List<Map<String, Object>> optlist) {
		List<Map<String, Object>> ret = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> m: optlist) {
			SelectField.OptionEntity opt = new SelectField.OptionEntity();
			opt.setValue((String) m.get(EnumTable.Entity.ID_ENUM_CODE));
			opt.setName((String) m.get(EnumNameTable.Entity.ID_ENUM_NAME));
			ret.add(opt.getMap());
		}
		return ret;
	}

	/**
	 * オプションリストを取得します。
	 * @param enumTypeCode 列挙型コード。
	 * @param langCode 言語コード。
	 * @return オプションリスト。
	 * @throws Exception 例外。
	 */
	public List<Map<String, Object>> getOptionList(final String enumTypeCode, final String langCode) throws Exception {
		List<Map<String, Object>> deflist = this.convertSelectOption(this.executeQuery(new EnumOptionQuery(enumTypeCode, "default")));
		List<Map<String, Object>> langlist = this.convertSelectOption(this.executeQuery(new EnumOptionQuery(enumTypeCode, langCode)));
		deflist = this.updateList(deflist, langlist);
		return deflist;
	}

	/**
	 * オプション名称を取得します。
	 * @param enumTypeCode 列挙型コード。
	 * @param enumOptionCode 列挙型オプションコード。
	 * @param langCode 言語コード。
	 * @return オプション名称。
	 * @throws Exception 例外。
	 */
	public String getOptionName(final String enumTypeCode, final String enumOptionCode, final String langCode) throws Exception {
		if (StringUtil.isBlank(enumOptionCode)) {
			return null;
		}
		EnumOptionQuery query = new EnumOptionQuery(enumTypeCode, enumOptionCode, langCode);
		Map<String, Object> rec = this.executeRecordQuery(query);
		if (rec != null) {
			EnumNameTable.Entity e = new EnumNameTable.Entity(rec);
			String optname = e.getEnumName();
			return optname;
		} else {
			return null;
		}
	}

	/**
	 * EnumTypeCodeのオートコンプリート用の問合せを実行します。
	 * @param text 入力テキスト。
	 * @param langCode 言語コード。
	 * @return オートコンプリート用のリスト。
	 * @throws Exception 例外。
	 */
	public List<Map<String, Object>> queryEnumTypeAutocomplateList(final String text, final String langCode) throws Exception {
		EnumQuery query = new EnumQuery();
		FieldList flist = new FieldList();
		flist.addField(new EnumCodeField()).setMatchType(MatchType.PART);
		query.setCondition("e.parent_id is null");
		query.setConditionFieldList(flist);
		EnumTable.Entity e = new EnumTable.Entity();
		e.setEnumCode(text);
		EnumNameTable.Entity ne = new EnumNameTable.Entity(e.getMap());
		ne.setLangCode(langCode);
		query.setConditionData(e.getMap());
		return this.executeQuery(query);
	}

	/**
	 * 列挙型コードに対応した列挙型IDを取得します。
	 * @param enumCode 列挙型コード。
	 * @return  列挙型ID。
	 * @throws Exception 例外。
	 */
	public Long queryEnumId(final String enumCode) throws Exception {
		Map<String, Object> m = this.executeRecordQuery(new EnumTableQuery(enumCode));
		EnumTable.Entity e = new EnumTable.Entity(m);
		return e.getEnumId();
	}

	/**
	 * 列挙型の情報を取得します。
	 *
	 * @param enumTypeCode 列挙型コード。
	 * @param langCode 言語コード。
	 * @return ヒットしたデータマップ。
	 * @throws Exception 例外。
	 */
	public Map<String, Object> queryEnumType(final String enumTypeCode, final String langCode) throws Exception {
		EnumTypeQuery query = new EnumTypeQuery(enumTypeCode, langCode);
		query.setConditionFieldList(new FieldList(query.getFieldList().get(EnumTable.Entity.ID_ENUM_CODE)));
		Map<String, Object> ret = this.executeRecordQuery(query);
		if (ret == null) {
			EnumTable.Entity e = new EnumTable.Entity();
			e.setEnumCode(enumTypeCode);
			EnumNameTable.Entity ne = new EnumNameTable.Entity();
			ne.setEnumName("");
			ret = e.getMap();
		}
		return ret;
	}
}
