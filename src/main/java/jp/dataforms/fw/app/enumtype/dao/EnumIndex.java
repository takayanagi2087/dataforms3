package jp.dataforms.fw.app.enumtype.dao;

import jp.dataforms.fw.dao.Index;
import jp.dataforms.fw.field.base.FieldList;

/**
 * EnumTableのインデックス。
 *
 */
public class EnumIndex extends Index {
	/**
	 * コンストラクタ。
	 */
	public EnumIndex() {
		EnumTable table = new EnumTable();
		this.setUnique(true);
		this.setTable(table);
		this.setFieldList(new FieldList(table.getParentIdField(), table.getEnumCodeField()));
		this.setViolationMessageKey("error.duplicateenumcode");
	}
}
