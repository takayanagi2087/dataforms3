package jp.dataforms.fw.dao;

import jp.dataforms.fw.field.base.Field;
import jp.dataforms.fw.field.base.FieldList;

/**
 * 単一テーブルの問合せ。
 *
 */
public class SingleTableQuery extends Query {
	/**
	 * コンストラクタ。
	 * @param table テーブル。
	 */
	public SingleTableQuery(final Table table) {
		this.setFieldList(table.getFieldList());
		this.setMainTable(table);
		Field<?> sortOrderField = table.getFieldList().get("sortOrder");
		if (sortOrderField != null) {
			this.setOrderByFieldList(new FieldList(sortOrderField));
		}
	}
}
