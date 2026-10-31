package jp.dataforms.fw.devtool.query.page;

import jp.dataforms.fw.devtool.field.FieldClassSelectField;
import jp.dataforms.fw.devtool.field.FieldIdField;
import jp.dataforms.fw.devtool.field.FieldSingleSelectField;
import jp.dataforms.fw.field.base.FieldList;
import jp.dataforms.fw.field.base.TextField;
import jp.dataforms.fw.htmltable.EditableHtmlTable;

/**
 * 問い合わせクラスフィールドリスト。
 */
public class FieldListHtmlTable extends EditableHtmlTable {

	/**
	 * 参照フィールドID。
	 */
	public static final String ID_REF_FIELD_ID = "refFieldId";
	/**
	 * フィールドクラス。
	 */
	private static final String ID_FIELD_CLASS = "fieldClass";
	/**
	 * オプション(Json形式、非表示)。
	 */
	private static final String ID_OPTION_JSON = "optionJson";
	/**
	 * オプション(表示形式)。
	 */
	private static final String ID_OPTION_TEXT = "optionText";
	/**
	 * コメント。
	 */
	private static final String ID_COMMENT = "comment";

	/**
	 * コンストラクタ。
	 * @param id　テーブルID。
	 */
	public FieldListHtmlTable(String id) {
		super(id);
		FieldList flist = new FieldList();
		flist.addField(new FieldClassSelectField(ID_FIELD_CLASS)); // 参照フィールドクラス。
		flist.addField(new FieldIdField());	// フィールドID(fieldのAlias)。
		flist.addField(new FieldSingleSelectField(ID_REF_FIELD_ID)).setReadonly(true); // 参照フィールドID。
		flist.addField(new TextField(ID_OPTION_JSON)).setHidden(true); // option
		flist.addField(new TextField(ID_OPTION_TEXT)); // option表示
		flist.addField(new TextField(ID_COMMENT)); // option表示
		this.setFieldList(flist);
		this.setFixedColumns(4);
	}
}
