package jp.dataforms.fw.devtool.query.page;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.dataforms.fw.dao.Query;
import jp.dataforms.fw.dao.Table;
import jp.dataforms.fw.devtool.field.FieldDisplaySelectField;
import jp.dataforms.fw.devtool.field.FieldFullClassNameField;
import jp.dataforms.fw.devtool.field.MatchTypeSelectField;
import jp.dataforms.fw.devtool.field.QueryFieldIdField;
import jp.dataforms.fw.devtool.field.SummerySelectField;
import jp.dataforms.fw.devtool.field.TableFullClassNameField;
import jp.dataforms.fw.devtool.field.TableOrSubQueryClassNameField;
import jp.dataforms.fw.field.base.Field;
import jp.dataforms.fw.field.base.FieldList;
import jp.dataforms.fw.field.base.TextField;
import jp.dataforms.fw.field.common.FlagField;
import jp.dataforms.fw.field.common.RecordIdField;
import jp.dataforms.fw.field.common.SortOrderField;
import jp.dataforms.fw.htmltable.EditableHtmlTable;
import jp.dataforms.fw.util.StringUtil;
import jp.dataforms.fw.validator.RequiredValidator;

/**
 * 選択フィールドHtmlテーブルクラス。
 *
 */
public class SelectFieldHtmlTable extends EditableHtmlTable {

	/**
	 * 選択フラグ。
	 */
	public static final String ID_SEL = "sel";

	/**
	 * テーブルのフルクラス名。
	 */
	public static final String ID_TABLE_FULL_CLASS_NAME = "tableFullClassName";

	/**
	 * フィールドID。
	 */
	public static final String ID_FIELD_ID = "fieldId";

	/**
	 * フィールドクラス名。
	 */
	public static final String ID_FIELD_CLASS_NAME = "fieldClassName";

	/**
	 * 一覧フォームのフィールド表示設定。
	 */
	public static final String ID_LIST_FIELD_DISPLAY = "listFieldDisplay";

	/**
	 * 編集キー。
	 */
	public static final String ID_EDIT_KEY = "editKey";

	/**
	 * 編集フォームのフィールド表示設定。
	 */
	public static final String ID_EDIT_FIELD_DISPLAY = "editFieldDisplay";

	/**
	 * テーブルクラス名。
	 */
	public static final String ID_TABLE_CLASS_NAME = "tableClassName";

	/**
	 * マッチタイプ。
	 */
	public static final String ID_MATCH_TYPE = "matchType";

	/**
	 * フィールド別名。
	 */
	public static final String ID_ALIAS = "alias";

	/**
	 * テーブル別名。
	 */
	public static final String ID_TABLE_ALIAS = "tableAlias";

	/**
	 * コメント。
	 */
	public static final String ID_COMMENT = "comment";


	/**
	 * コンストラクタ。
	 * @param id デーブルID。
	 */
	public SelectFieldHtmlTable(final String id) {
		this(id, false);
	}

	/**
	 * コンストラクタ。
	 * @param id デーブルID。
	 * @param daoflg Daoフラグ。
	 */
	public SelectFieldHtmlTable(final String id, final boolean daoflg) {
		super(id);
		FieldList flist = new FieldList(
			(daoflg ? new FlagField(ID_SEL): new SummerySelectField(ID_SEL))
			, new SortOrderField()
			, new QueryFieldIdField(ID_FIELD_ID).addValidator(new RequiredValidator()).setReadonly(daoflg)
			, new TextField(ID_ALIAS)
			, new FieldFullClassNameField(ID_FIELD_CLASS_NAME).setReadonly(true)
			, new TableFullClassNameField(ID_TABLE_FULL_CLASS_NAME).setReadonly(true)
			, new TableOrSubQueryClassNameField(ID_TABLE_CLASS_NAME).setReadonly(true)
			, new MatchTypeSelectField(ID_MATCH_TYPE)
			, new FieldDisplaySelectField(ID_LIST_FIELD_DISPLAY)
			, new FlagField(ID_EDIT_KEY)
			, new FieldDisplaySelectField(ID_EDIT_FIELD_DISPLAY)
			, new TextField(ID_TABLE_ALIAS ).setReadonly(true)
			, new TextField(ID_COMMENT)
		);
		this.setFieldList(flist);
		if (!daoflg) {
			this.setFixedColumns(5);
		}
	}

	/**
	 * 指定されたクラスのフィールドリストを取得します。
	 * @param className QueryまたはTableのクラス名。
	 * @return 主テーブルのインスタンス。
	 * @throws Exception 例外。
	 */
	public static FieldList getFieldList(final String className) throws Exception {
		FieldList fieldList = null;
		if (!StringUtil.isBlank(className)) {
			Class<?> qclass = Class.forName(className);
			Object obj = qclass.getConstructor().newInstance();
			if (obj instanceof Query) {
				Query q = (Query) obj;
				fieldList = q.getFieldList();
			} else if (obj instanceof Table){
				Table t = (Table) obj;
				fieldList = t.getFieldList();
			}
		}
		return fieldList;
	}

	/**
	 * 一覧フォームのフィールド表示設定。
	 * @param f フィールド。
	 * @param idx フィールドインデックス。
	 * @return フィールド表示情報。
	 */
	private static Field.Display getListFieldDisplay(final Field<?> f, final int idx) {
		Field.Display ret = Field.Display.NONE;
		if (idx == 0) {
			if (f.isHidden()) {
				ret = Field.Display.INPUT_HIDDEN;
			} else {
				ret = Field.Display.INPUT_READONLY;
			}
		} else 	if (f.isHidden()) {
			ret = Field.Display.INPUT_HIDDEN;
		} else {
			ret = Field.Display.SPAN;
		}
		return ret;
	}

	/**
	 * 編集フォームのフィールド表示設定。
	 * @param f フィールド。
	 * @return フィールド表示情報。
	 */
	private static Field.Display getEditFieldDisplay(final Field<?> f) {
		Field.Display ret = Field.Display.NONE;
		if (f.isHidden()) {
			ret = Field.Display.INPUT_HIDDEN;
		} else {
			ret = Field.Display.INPUT;
		}
		return ret;
	}


	/**
	 * fieldListに対応した表示データを作成します。
	 * @param flist フィールドリスト。
	 * @param alias 別名。
	 * @return テーブルデータ。
	 */
	public static List<Map<String, Object>> getTableData(final FieldList flist, final String alias) {
		boolean pk = false;
		List<Map<String, Object>> ret = new ArrayList<Map<String, Object>>();
		int idx = 0;
		for (Field<?> f: flist) {
			Map<String, Object> ent = new HashMap<String, Object>();
			Table table = f.getTable();
			ent.put(ID_TABLE_FULL_CLASS_NAME, table.getClass().getName());
			ent.put(JoinHtmlTable.ID_TABLE_CLASS_NAME, table.getClass().getSimpleName());
			ent.put(ID_SEL, "0");
			ent.put(ID_FIELD_ID, f.getId());
			ent.put(ID_FIELD_CLASS_NAME, f.getClass().getName());
			ent.put(ID_TABLE_ALIAS, alias);
			ent.put(ID_COMMENT, f.getComment());
			ent.put(ID_MATCH_TYPE, f.getDefaultMatchType());
			// 先頭のrecord idに編集キーを設定する。
			if (pk == false && f instanceof RecordIdField) {
				ent.put(ID_EDIT_KEY, "1");
				pk = true;
			} else {
				ent.put(ID_EDIT_KEY, "0");
			}
			ent.put(ID_LIST_FIELD_DISPLAY, SelectFieldHtmlTable.getListFieldDisplay(f, idx++));
			ent.put(ID_EDIT_FIELD_DISPLAY, SelectFieldHtmlTable.getEditFieldDisplay(f));

			// ent.put(JoinHtmlTable.ID_TABLE_CLASS_NAME, table.getClass().getSimpleName());
			ret.add(ent);
		}
		if (pk == false) {
			// PKが無かった場合、先頭の項目を条件項目に設定する。
			Map<String, Object> m = ret.get(0);
			m.put(ID_LIST_FIELD_DISPLAY, Field.Display.INPUT_HIDDEN);
			m.put(ID_EDIT_KEY, "1");
		}
		return ret;
	}

	/**
	 *
	 * キーフィールドを設定します。
	 * @param list テーブルデータ。
	 * @param keyFieldList キーフィールドリスト。
	 * @return テーブルデータ。
	 *
	 */
	public static List<Map<String, Object>> selectKey(final List<Map<String, Object>> list, final FieldList keyFieldList) {
		if (keyFieldList != null) {
			for (Field<?> f: keyFieldList) {
				String fid = f.getId();
				for (Map<String, Object> m: list) {
					String fieldId = (String) m.get(ID_FIELD_ID);
					if (fid.equals(fieldId)) {
						m.put(ID_SEL, "1");
					}
				}
			}
		}
		return list;
	}
}
