package jp.dataforms.fw.app.user.dao;

import jp.dataforms.fw.app.user.dao.UserQuery.UserAttributeSubQuery;
import jp.dataforms.fw.dao.Table;
import jp.dataforms.fw.dao.TableRelation;

/**
 * ユーザテーブルの関係を定義するクラスです。
 *
 */
public class UserInfoTableRelation extends TableRelation {
	/**
	 * コンストラクタ。
	 * @param table 対象テーブル。
	 */
	public UserInfoTableRelation(final Table table) {
		super(table);
	}

	/**
	 * {@inheritDoc}
	 * <pre>
	 * 結合対象テーブルは以下の通りです。
	 * 	UserAttributeTable
	 *  UserAttributeQueryのサブクエリ　alias="ua"のサブクエリ
	 *  alias="ul"のサブクエリ
	 * </pre>
	 */
	@Override
	public String getJoinCondition(final Table joinTable, final String alias) {
		if (joinTable instanceof UserAttributeTable
		 || joinTable instanceof UserAttributeSubQuery
		 || "ua".equals(alias)
		 || "ai".equals(alias)) {
			return (
					this.getTable().getLinkFieldCondition(UserInfoTable.Entity.ID_USER_ID, joinTable, alias, UserAttributeTable.Entity.ID_USER_ID)
			);
		}
		return null;
	}

}
