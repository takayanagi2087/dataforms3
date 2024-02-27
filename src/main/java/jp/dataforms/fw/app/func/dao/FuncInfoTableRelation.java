package jp.dataforms.fw.app.func.dao;

import jp.dataforms.fw.dao.Table;
import jp.dataforms.fw.dao.TableRelation;


/**
 * 機能情報テーブルの関係を定義するクラスです。
 *
 */
public class FuncInfoTableRelation extends TableRelation {
	/**
	 * コンストラクタ。
	 * @param table テーブル。
	 */
	public FuncInfoTableRelation(final Table table) {
		super(table);
	}
	
	@Override
	public String getJoinCondition(final Table joinTable, final String alias) {
		return null;
	}
}
