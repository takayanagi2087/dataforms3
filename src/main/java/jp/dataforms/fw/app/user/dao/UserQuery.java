package jp.dataforms.fw.app.user.dao;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jp.dataforms.fw.app.enumtype.dao.EnumNameTable;
import jp.dataforms.fw.app.enumtype.dao.EnumOptionQuery;
import jp.dataforms.fw.dao.Query;
import jp.dataforms.fw.dao.SubQuery;
import jp.dataforms.fw.dao.Table;
import jp.dataforms.fw.dao.TableList;
import jp.dataforms.fw.field.base.FieldList;
import jp.dataforms.fw.field.sqlfunc.AliasField;
import jp.dataforms.fw.util.JsonUtil;
import jp.dataforms.fw.util.UserInfoTableUtil;

/**
 * ユーザ問い合わせクラス。
 *
 */
public class UserQuery extends Query {


	/**
	 * Logger.
	 */
	private static Logger logger = LogManager.getLogger(UserQuery.class);

	/**
	 * 指定されたユーザタイプを取得する問い合わせクラス。
	 *
	 */
	private static class UserAttributeTypeQuery extends Query {
		/**
		 * コンストラクタ.
		 * @param type ユーザタイプ.
		 * @param langCode 言語コード.
		 */
		public UserAttributeTypeQuery(final String type, final String langCode) {
			UserAttributeTable tbl = new UserAttributeTable();
			SubQuery ntbl = new SubQuery(new EnumOptionQuery(type, langCode));
			ntbl.setAlias("nm");
			FieldList fl = tbl.getFieldList();
			fl.add(new AliasField("attributeName", ntbl.getField(EnumNameTable.Entity.ID_ENUM_NAME)));
			this.setFieldList(tbl.getFieldList());
			this.setMainTable(tbl);
//			this.setJoinTableList(new TableList(ntbl));
			this.addInnerJoin(ntbl);
			this.setCondition("m.user_attribute_type='" + type + "' and nm.lang_code='" + langCode + "' ");
		}
	}


	/**
	 * ユーザ属性副問合せ。
	 */
	public static class UserAttributeSubQuery extends SubQuery {
		/**
		 * コンストラクタ。
		 * @param type ユーザ属性コード。
		 * @param langCode 言語コード。
		 */
		public UserAttributeSubQuery(final String type, final String langCode) {
			super(new UserAttributeTypeQuery(type, langCode));
		}
	}

	/**
	 * コンストラクタ。
	 * @param flist 問い合わせフォームのフィールドリスト。
	 * @param data 問い合わせフォームの入力データ。
	 */
	public UserQuery(final FieldList flist, final Map<String, Object> data) {
		@SuppressWarnings("unchecked")
		List<String> atlist = (List<String>) data.get("userAttributeList");
		logger.debug(() -> "atlist=" + JsonUtil.encode(atlist));
		this.setDistinct(true);
		UserInfoTable mtbl = UserInfoTableUtil.newUserInfoTable(); //new UserInfoTable();
		FieldList fl = new FieldList();
		fl.addAll(mtbl.getFieldList());
		// 該当するユーザ属性のサブクエリ
		SubQuery ua = new SubQuery(new UserAttributeQuery(data));
		ua.setAlias("ua");
		TableList tl = new TableList();
		// ユーザレベルの名称を取得するサブクエリ
		int idx = 0;
		for (String at: atlist) {
			UserAttributeSubQuery ul = new UserAttributeSubQuery(at, (String) data.get("currentLangCode"));
			ul.setAlias(at);
			tl.add(ul);
			fl.add(new AliasField("attribute" + (idx++), ul.getField("attributeName")));
		}

		this.setFieldList(fl);
		this.setMainTable(mtbl);

//		this.setJoinTableList(new TableList(ua));
//		this.setLeftJoinTableList(tl);
		this.addInnerJoin(ua);
		for (Table t: tl) {
			this.addLeftJoin(t);
		}
		this.setConditionFieldList(flist);
		this.setConditionData(data);
	}

}

