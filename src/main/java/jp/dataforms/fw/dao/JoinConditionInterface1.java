package jp.dataforms.fw.dao;

/**
 * Joinの条件生成インターフェース。
 *
 */
@FunctionalInterface
public interface JoinConditionInterface1 {
	/**
	 * Joinの結合条件を生成します。
	 * @param joinTable 木都合するテーブル。
	 * @return Joinの結合条件。
	 */
	String getJoinCondition(final Table joinTable);
}
