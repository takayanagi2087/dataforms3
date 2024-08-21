package jp.dataforms.fw.devtool.pageform.page;

import jp.dataforms.fw.devtool.field.FunctionSelectField;
import jp.dataforms.fw.devtool.field.PackageNameField;
import jp.dataforms.fw.devtool.field.QueryOrTableClassNameField;
import jp.dataforms.fw.field.base.FieldList;
import jp.dataforms.fw.field.base.TextField;
import jp.dataforms.fw.htmltable.EditableHtmlTable;

/**
 * 複数リスト問合せテーブル。
 */
public class MultiRecordQueryHtmlTable extends EditableHtmlTable {
	/**
	 * コンストラクタ。
	 * @param id テーブルID。む
	 */
	public MultiRecordQueryHtmlTable(final String id) {
		super(id);
		FieldList flist = new FieldList();
		flist.addField(new FunctionSelectField());
		flist.addField(new PackageNameField());
		flist.addField(new QueryOrTableClassNameField(DaoAndPageGeneratorEditForm.ID_QUERY_CLASS_NAME))
			.setAutocomplete(true)
			.setRelationDataAcquisition(true);
		flist.addField(new TextField(DaoAndPageGeneratorEditForm.ID_QUERY_CONFIG));
		this.setFieldList(flist);
	}
}
