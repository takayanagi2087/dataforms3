package jp.dataforms.fw.devtool.field;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jp.dataforms.fw.controller.Form;
import jp.dataforms.fw.dao.Table;
import jp.dataforms.fw.field.base.Field;
import jp.dataforms.fw.field.base.FieldList;
import jp.dataforms.fw.field.sqltype.VarcharField;
import jp.dataforms.fw.validator.ValidationError;

/**
 * 問合せフィールドIDフィールドのクラス。
 *
 */
public class QueryFieldIdField extends VarcharField {

	/**
	 * Logger.
	 */
	private static Logger logger = LogManager.getLogger(QueryFieldIdField.class);

	/**
	 * 項目長。
	 */
	private static final int LENGTH = 256;

	/**
	 * フィールドコメント。
	 */
	private static final String COMMENT = "問合せフィールドID";

	/**
	 * コンストラクタ。
	 */
	public QueryFieldIdField() {
		this(null);
	}

	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public QueryFieldIdField(final String id) {
		super(id, LENGTH);
		this.setComment(COMMENT);
		this.setAjaxParameter(AjaxParameter.FORM);
		this.setAutocomplete(true);
	}

	/**
	 * 指定されたテーブルクラス中のフィールドリストを取得します。
	 * @param cls クラス名。
	 * @return フィールドリスト。
	 * @throws Exception 例外。
	 */
	private FieldList getFieldList(final String cls) throws Exception {
		@SuppressWarnings("unchecked")
		Class<? extends Table> clazz = (Class<? extends Table>) Class.forName(cls);
		Table table = clazz.getConstructor().newInstance();
		return table.getFieldList();
	}

	/**
	 * Formで指定された全テーブルのフィールドリストを取得します。
	 * @param data フォームデータ。
	 * @return フィールドリスト。
	 * @throws Exception 例外。
	 */
	private FieldList getTableFieldList(final Map<String, Object> data) throws Exception {
		FieldList flist = new FieldList();
		String mainTablePackageName = (String) data.get("mainTablePackageName");
		String mainTableClassName = (String) data.get("mainTableClassName");
		flist.addAll(this.getFieldList(mainTablePackageName + "." + mainTableClassName));
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> joinTableList = (List<Map<String, Object>>) data.get("joinTableList");
		for (Map<String, Object> m: joinTableList) {
			String packageName = (String) m.get("packageName");
			String tableClassName = (String) m.get("tableClassName");
			flist.addAll(this.getFieldList(packageName + "." + tableClassName));
		}
		return flist;
	}


	@Override
	protected List<Map<String, Object>> queryAutocompleteSourceList(Map<String, Object> p) throws Exception {
		String id = (String) p.get("currentFieldId");
		String text = (String) p.get(id);
		String rowid = this.getHtmlTableRowId(id);
		String colid = this.getHtmlTableColumnId(id);
		Form f = this.getParentForm();
		p.put("overwriteMode", "skip");
		List<ValidationError> elist = f.validate(p);
		if (elist.size() == 0) {
			Map<String, Object> data = f.convertToServerData(p);
			FieldList flist = this.getTableFieldList(data);
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			for (Field<?> field: flist) {
				logger.debug("fieldId=" + field.getId());
				if (field.getId().toLowerCase().indexOf(text.toLowerCase()) >= 0) {
					Map<String, Object> m = new HashMap<String, Object>();
					m.put("fieldId", field.getId());
					m.put("label", field.getId() + ":" + field.getTable().getClass().getSimpleName());
					m.put("fieldClassName", field.getClass().getName());
					m.put("tableClassName", field.getTable().getClass().getSimpleName());
					list.add(m);
				}
			}
			return this.convertToAutocompleteList(rowid, list, colid, "label", "fieldClassName", "tableClassName");
		} else {
			return new ArrayList<Map<String, Object>>();
		}
	}
}
