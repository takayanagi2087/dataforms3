package jp.dataforms.fw.app.func.dao;

import jp.dataforms.fw.dao.Query;
import jp.dataforms.fw.field.base.FieldList;

/**
 * 機能テーブルの問い合わせクラス。
 *
 */
public class FuncInfoQuery extends Query {
	/**
	 * コンストラクタ.
	 */
	public FuncInfoQuery() {
		FuncInfoTable tbl = new FuncInfoTable();
		this.setFieldList(tbl.getFieldList());
		this.setMainTable(tbl);
		this.setOrderByFieldList(new FieldList(tbl.getSortOrderField()));
	}
}
