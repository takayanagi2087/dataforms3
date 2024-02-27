package jp.dataforms.fw.dao;

import java.util.ArrayList;
import java.util.List;

/**
 * テーブル関係を定義するクラスです。
 * <pre>
 * ver 1.0xではHogeTable#getJoinConditionメソッドにテーブルの関係を記述していましたが、
 * 開発ツールでTableクラスを修正した場合、この定義を上書きしてしまうということがありました。
 * ver1.1xではその対策として、HogeTableRelationクラスを用意し、このクラスでテーブルの
 * 関係を定義するように修正しました。
 * </pre>
 */
public class TableRelation {


	/**
	 * 関係を定義するテーブルのインスタンス。
	 */
	private Table table = null;

	/**
	 * コンストラクタ。
	 * @param table 関係を定義するテーブルのインスタンス。
	 */
	public TableRelation(final Table table) {
		this.table = table;
		List<ForeignKey> fklist = this.getForeignKeyList();
		for (ForeignKey fk: fklist) {
			fk.setTable(table);
		}
	}

	/**
	 * 関係を定義するテーブルのインスタンスを取得します。
	 * @return 関係を定義するテーブルのインスタンス。
	 */
	public Table getTable() {
		return table;
	}

	/**
	 * 外部キーのリストを取得します。
	 * @return 外部キーのリスト。
	 */
	public List<ForeignKey> getForeignKeyList() {
		return new ArrayList<ForeignKey>();
	}


	/**
	 * テーブルの結合条件を取得します。
	 * <pre>
	 * このテーブルとjoinTableとの結合条件を作成します。
	 * 通常は指定されたjoinTableのクラスをinstansof演算子で判定し結合条件を作成します。
	 * 一回の問い合わせで、同じテーブルを複数回結合する場合は、それぞれのテーブルを
	 * 別インスタンスで作成し別のaliasを設定し、そのaliasで判定します。
	 * </pre>
	 * @param joinTable 結合対象テーブル。
	 * @param alias 結合対象テーブルの別名。
	 * @return 結合条件。
	 */
	public String getJoinCondition(final Table joinTable, final String alias) {
		List<ForeignKey> fklist = this.getForeignKeyList();
		for (ForeignKey fk: fklist) {
			Class<? extends Table> tc = fk.getReferenceTableClass();
			if (tc.isInstance(joinTable)) {
				StringBuilder sb = new StringBuilder();
				String[] flist = fk.getFieldIdList();
				String[] rflist = fk.getReferenceFieldIdList();
				for (int i = 0; i < flist.length; i++) {
					if (sb.length() > 0) {
						sb.append(" and ");
					}
					String cnd = this.getTable().getLinkFieldCondition(flist[i], joinTable, alias, rflist[i]);
					sb.append(cnd);
				}
				return sb.toString();
			}
		}
		return null;
	}
}
