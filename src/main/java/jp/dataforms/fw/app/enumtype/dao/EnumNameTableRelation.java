package jp.dataforms.fw.app.enumtype.dao;

import java.util.ArrayList;
import java.util.List;

import jp.dataforms.fw.dao.ForeignKey;
import jp.dataforms.fw.dao.Table;
import jp.dataforms.fw.dao.TableRelation;

/**
 * EnumNameTableの関係を定義するクラスです。
 *
 */
public class EnumNameTableRelation extends TableRelation {

	/**
	 * 外部キーリスト。
	 */
	private static List<ForeignKey> foreignKeyList = null;

	/**
	 * 外部キーリストの定義。
	 * <pre>
	 * この初期化処理で外部キーを定義することにより、自動的に外部キーが設定されます。
	 * </pre>
	 */
	static {
		foreignKeyList = new ArrayList<ForeignKey>();
		// foreignKeyList.add(new ForeignKey("fkEnumNameTable01", EnumNameTable.Entity.ID_ENUM_ID, EnumNameTable.class));
	}

	@Override
	public List<ForeignKey> getForeignKeyList() {
		return foreignKeyList;
	}

	/**
	 * コンストラクタ。
	 * @param table 対象テーブル。
	 */
	public EnumNameTableRelation(final Table table) {
		super(table);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getJoinCondition(final Table joinTable, final String alias) {
		if (joinTable instanceof EnumTable) {
			return this.getTable().getLinkFieldCondition(EnumNameTable.Entity.ID_ENUM_ID, joinTable, alias);
		}
		return super.getJoinCondition(joinTable, alias);
	}
}
