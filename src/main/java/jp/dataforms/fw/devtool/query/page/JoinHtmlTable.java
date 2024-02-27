package jp.dataforms.fw.devtool.query.page;

import jp.dataforms.fw.devtool.field.AliasNameField;
import jp.dataforms.fw.devtool.field.FunctionSelectField;
import jp.dataforms.fw.devtool.field.JoinTypeField;
import jp.dataforms.fw.devtool.field.PackageNameField;
import jp.dataforms.fw.devtool.field.TableOrSubQueryClassNameField;
import jp.dataforms.fw.field.base.FieldList;
import jp.dataforms.fw.field.base.TextField;
import jp.dataforms.fw.htmltable.EditableHtmlTable;
import jp.dataforms.fw.validator.RequiredValidator;

/**
 * Joinテーブルを編集するためのHTMLテーブルクラスです。
 *
 */
public class JoinHtmlTable extends EditableHtmlTable {
	/**
	 * 結合区分。
	 */
	public static final String ID_JOIN_TYPE = "joinType";

	/**
	 * パッケージ名。
	 */
	public static final String ID_PACKAGE_NAME = "packageName";

	/**
	 * テーブルクラス名フィールドID。
	 */
	public static final String ID_TABLE_CLASS_NAME = "tableClassName";

	/**
	 * 別名フィールドID。
	 */
	public static final String ID_ALIAS_NAME = "aliasName";

	/**
	 * 結合条件フィールドID。
	 */
	public static final String ID_JOIN_CONDITION = "joinCondition";

	/**
	 * コンストラクタ。
	 * @param id デーブルID。
	 */
	public JoinHtmlTable(final String id) {
		super(id);
		FieldList flist = new FieldList(
			(new JoinTypeField(ID_JOIN_TYPE)).addValidator(new RequiredValidator())
			, new FunctionSelectField()
			, (new PackageNameField(ID_PACKAGE_NAME)).addValidator(new RequiredValidator())
			, (new TableOrSubQueryClassNameField(ID_TABLE_CLASS_NAME)).addValidator(new RequiredValidator())
			, (new AliasNameField(ID_ALIAS_NAME)).setCalcEventField(true).addValidator(new RequiredValidator())
			, (new TextField(ID_JOIN_CONDITION)).setReadonly(true)
		);
		flist.get(ID_TABLE_CLASS_NAME).setAutocomplete(true).setRelationDataAcquisition(true);
		this.setFieldList(flist);
		this.setFixedColumns(4);
	}
}
