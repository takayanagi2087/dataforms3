package jp.dataforms.fw.devtool.db.table;

import jp.dataforms.fw.devtool.field.ClassNameField;
import jp.dataforms.fw.field.common.PresenceField;
import jp.dataforms.fw.field.common.RowNoField;
import jp.dataforms.fw.field.sqltype.IntegerField;
import jp.dataforms.fw.field.sqltype.VarcharField;
import jp.dataforms.fw.htmltable.HtmlTable;

/**
 * DBテーブルの一覧を表示するHtmlTable。
 *
 */
public class DbTableListHtmlTable extends HtmlTable {
	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public DbTableListHtmlTable(final String id) {
		super(id
			, new ClassNameField("checkedClass")
			, new RowNoField()
			, (new ClassNameField()).setSortable(true)
			, (new VarcharField("tableName", 64)).setSortable(true)
			, (new VarcharField("tableComment", 1024)).setSortable(true)
			, (new VarcharField("indexNames", 1024)).setSortable(true)
			, new PresenceField("status").setSortable(true)
			, new PresenceField("statusVal")
			, new PresenceField("sequenceGeneration").setSortable(true)
			, new PresenceField("difference").setSortable(true)
			, new PresenceField("differenceVal")
			, new PresenceField("backupTable").setSortable(true)
			, new IntegerField("recordCount").setSortable(true)
		);
	}
}
