package jp.dataforms.fw.app.enumtype.dao;

import jp.dataforms.fw.dao.Query;

/**
 * 列挙型名称の問合せ。
 *
 */
public class EnumNameTableQuery extends Query {
	/**
	 * コンストラクタ。
	 */
	public EnumNameTableQuery() {
		EnumNameTable table = new EnumNameTable();
		this.setFieldList(table.getFieldList());
		this.setMainTable(table);
	}

	/**
	 * コンストラクタ。
	 * @param enumId 列挙型ID。
	 */
	public EnumNameTableQuery(final Long enumId) {
		this();
		this.setCondition("m.enum_id=:enum_id");
		EnumNameTable.Entity p = new EnumNameTable.Entity();
		p.setEnumId(enumId);
		this.setConditionData(p.getMap());
	}
}
