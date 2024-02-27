package jp.dataforms.fw.app.user.dao;

import java.util.ArrayList;
import java.util.List;

import jp.dataforms.fw.app.enumtype.dao.EnumTable;
import jp.dataforms.fw.dao.ForeignKey;
import jp.dataforms.fw.dao.Table;
import jp.dataforms.fw.dao.TableRelation;

/**
 * ユーザ属性テーブルの関係を定義するクラスです。
 *
 */
public class UserAttributeTableRelation extends TableRelation {

	/**
	 * コンストラクタ。
	 * @param table 対象テーブル。
	 */
	public UserAttributeTableRelation(final Table table) {
		super(table);
	}

	/**
	 * 外部キーリスト。
	 */
	private static List<ForeignKey> foreignKeyList = null;

	/**
	 * 外部キーリストの作成。
	 */
	static {
		UserAttributeTableRelation.foreignKeyList = new ArrayList<ForeignKey>();
	}

	@Override
	public List<ForeignKey> getForeignKeyList() {
		return UserAttributeTableRelation.foreignKeyList;
	}

	/**
	 * {@inheritDoc}
	 * <pre>
	 * 結合対象テーブルは以下の通りです。
	 * EnumOptionNameTable	(aliasが"nm"のもの)
	 * </pre>
	 */
	@Override
	public String getJoinCondition(final Table joinTable, final String alias) {
		if ("nm".equals(alias)) {
			return this.getTable().getLinkFieldCondition(UserAttributeTable.Entity.ID_USER_ATTRIBUTE_VALUE, joinTable, alias, EnumTable.Entity.ID_ENUM_CODE);
		}
		return null;
	}

}
