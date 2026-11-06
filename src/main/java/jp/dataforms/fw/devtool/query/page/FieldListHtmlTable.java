package jp.dataforms.fw.devtool.query.page;

import jp.dataforms.fw.devtool.field.FieldClassSelectField;
import jp.dataforms.fw.devtool.field.FieldIdField;
import jp.dataforms.fw.devtool.field.FieldSingleSelectField;
import jp.dataforms.fw.field.base.FieldList;
import jp.dataforms.fw.field.base.TextField;
import jp.dataforms.fw.field.sqltype.IntegerField;
import jp.dataforms.fw.htmltable.EditableHtmlTable;
import jp.dataforms.fw.validator.RequiredValidator;

/**
 * 問い合わせクラスフィールドリスト。
 */
public class FieldListHtmlTable extends EditableHtmlTable {
	/**
	 * フィールド変換クラス。
	 */
	public static final String ID_FUNC_FIELD_CLASS_NAME = "funcFieldClassName";
	/**
	 * フィールドID。
	 */
	public static final String ID_FIELD_ID = "fieldId";
	/**
	 * 参照フィールドID。
	 */
	public static final String ID_REF_FIELD_ID = "refFieldId";
	/**
	 * 参照フィールドID。
	 */
	public static final String ID_FIELD_CLASS_NAME = "fieldClassName";
	/**
	 * 参照フィールドID。
	 */
	public static final String ID_TABLE_CLASS_NAME = "tableClassName";
	/**
	 * オプション(Json形式、非表示)。
	 */
	public static final String ID_OPTION_JSON = "optionJson";
	/**
	 * オプション(表示形式)。
	 */
	public static final String ID_OPTION_TEXT = "optionText";
	/**
	 * コメント。
	 */
	public static final String ID_COMMENT = "comment";

	/**
	 * コンストラクタ。
	 * @param id　テーブルID。
	 */
	public FieldListHtmlTable(String id) {
		super(id);
		FieldList flist = new FieldList();
		flist.addField(new IntegerField("no")); // No.
		flist.addField(new FieldClassSelectField(ID_FUNC_FIELD_CLASS_NAME)); // 機能フィールドクラス。
		flist.addField(new FieldIdField()).addValidator(new RequiredValidator());	// フィールドID(fieldのAlias)。
		flist.addField(new FieldSingleSelectField(ID_REF_FIELD_ID)); // 参照フィールドID。
		flist.addField(new TextField(ID_FIELD_CLASS_NAME)).setHidden(true); // 参照フィールドクラス。
		flist.addField(new TextField(ID_TABLE_CLASS_NAME)).setHidden(true); // 参照フィールドクラス。
		flist.addField(new TextField(ID_OPTION_JSON)).setHidden(true); // option
		flist.addField(new TextField(ID_OPTION_TEXT)).setReadonly(true); // option表示
		flist.addField(new TextField(ID_COMMENT)); // フィールドコメント
		this.setFieldList(flist);
		this.setFixedColumns(4);
	}
}
