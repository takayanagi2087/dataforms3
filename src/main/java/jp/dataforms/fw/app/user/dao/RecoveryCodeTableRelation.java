package jp.dataforms.fw.app.user.dao;

import java.util.List;
import java.util.ArrayList;
import jp.dataforms.fw.dao.ForeignKey;
import jp.dataforms.fw.dao.Table;
import jp.dataforms.fw.dao.TableRelation;

/**
 * RecoveryCodeTableの関係を定義するクラスです。
 *
 */
public class RecoveryCodeTableRelation extends TableRelation {

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
		/*
		 * 以下の定義を有効にすると外部キーHogeTableに対する外部キーを設定します。
		 * この設定だけでHogeTableとの結合条件も生成されるようになります。
		 */
		// foreignKeyList.add(new ForeignKey("fkRecoveryCodeTable01", RecoveryCodeTable.Entity.ID_HOGE_ID, HogeTable.class));
	}

	@Override
	public List<ForeignKey> getForeignKeyList() {
		return foreignKeyList;
	}

	/**
	 * コンストラクタ。
	 * @param table 対象テーブル。
	 */
	public RecoveryCodeTableRelation(final Table table) {
		super(table);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getJoinCondition(final Table joinTable, final String alias) {
		/*
		 * 外部キーを設定せずに結合条件を生成する場合は以下を有効にします。
		 */
/*
		if (joinTable instanceof HogeTable) {
			return this.getTable().getLinkFieldCondition(HogeTable.Entity.ID_HOGE_ID, joinTable, alias);
		}
*/
		return super.getJoinCondition(joinTable, alias);
	}
}
