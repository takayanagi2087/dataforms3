package jp.dataforms.fw.dao;

/**
 * Joinの条件生成インターフェース。
 *
 * TODO:tableの引数を将来的に削除する。
 *
 */
@FunctionalInterface
@Deprecated
public interface JoinConditionInterface {
	/**
	 * Joinの結合条件を生成します。
	 * @param table 結合元のテーブル。
	 * @param joinTable 木都合するテーブル。
	 * @return Joinの結合条件。
	 */
	String getJoinCondition(final Table table, final Table joinTable);
}
