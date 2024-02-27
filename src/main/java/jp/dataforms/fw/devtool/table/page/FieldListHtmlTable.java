package jp.dataforms.fw.devtool.table.page;

import jp.dataforms.fw.devtool.field.FieldClassNameField;
import jp.dataforms.fw.devtool.field.FieldIdField;
import jp.dataforms.fw.devtool.field.FieldLengthField;
import jp.dataforms.fw.devtool.field.OverwriteModeField;
import jp.dataforms.fw.devtool.field.PackageNameField;
import jp.dataforms.fw.field.base.FieldList;
import jp.dataforms.fw.field.common.FlagField;
import jp.dataforms.fw.field.sqltype.VarcharField;
import jp.dataforms.fw.htmltable.EditableHtmlTable;
import jp.dataforms.fw.validator.RequiredValidator;

/**
 * フィールドリストテーブルクラス。
 *
 */
public class FieldListHtmlTable extends EditableHtmlTable {
	/**
	 * コンストラクタ。
	 */
	public FieldListHtmlTable() {
		super("fieldList");
		FieldList flist = new FieldList(
			 (new PackageNameField("superPackageName")).addValidator(new RequiredValidator()).setComment("基本パッケージ名").setCalcEventField(true)
			, (new FieldClassNameField("superSimpleClassName")).setPackageNameFieldId("superPackageName").addValidator(new RequiredValidator()).setComment("基本クラス名").setCalcEventField(true)
			, (new PackageNameField()).setComment("パッケージ名").addValidator(new RequiredValidator()).setCalcEventField(true)
			, (new FieldClassNameField()).setComment("クラス名").addValidator(new RequiredValidator()).setCalcEventField(true)
			, new FieldIdField()
			, new FieldLengthField()
			, (new FlagField("pkFlag")).setComment("主キー")
			, (new FlagField("notNullFlag")).setComment("Not Null")
			, (new VarcharField("comment", 256)).setComment("コメント")
			, new FlagField("isDataformsField")
			, new OverwriteModeField()
		);
		this.setFieldList(flist);
	}
}
