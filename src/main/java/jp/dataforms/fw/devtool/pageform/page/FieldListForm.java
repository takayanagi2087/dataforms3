package jp.dataforms.fw.devtool.pageform.page;

import jp.dataforms.fw.controller.Form;
import jp.dataforms.fw.devtool.query.page.SelectFieldHtmlTable;

/**
 * フィールドリスト設定フォーム。
 */
public class FieldListForm extends Form {
	/**
	 * Logger.
	 */
//	private static Logger logger = LogManager.getLogger(FieldListForm.class);

	/**
	 * キーフィールドリスト。
	 */
	private static final String ID_FIELD_LIST = "fieldList";


	/**
	 * コンストラクタ。
	 */
	public FieldListForm() {
		this(null);
	}

	/**
	 * コンストラクタ。
	 * @param id フォームID。
	 */
	public FieldListForm(final String id) {
		super(id);
		SelectFieldHtmlTable table = new SelectFieldHtmlTable(ID_FIELD_LIST, true);
		table.getFieldList().get(SelectFieldHtmlTable.ID_FIELD_ID).clearValidator();
		table.getFieldList().get(SelectFieldHtmlTable.ID_COMMENT).setReadonly(true);
		table.getFieldList().get(SelectFieldHtmlTable.ID_LIST_FIELD_DISPLAY).setCalcEventField(true);
		table.setFixedColumns(3);
		this.addHtmlTable(table);
	}


	/**
	 * 指定されたクラスのフィールドリストを所得する。
	 * @param p パラメータ。
	 * @return フィールドリスト。
	 * @throws Exception 例外。
	 */
/*	@WebMethod
	public Response getFieldList(final Map<String, Object> p) throws Exception {
		logger.debug("p=" + JSON.encode(p));
		FieldList flist = SelectFieldHtmlTable.getFieldList((String) p.get("c"));
		List<Map<String, Object>> list = SelectFieldHtmlTable.getTableData(flist, "");
		Response resp = new JsonResponse(JsonResponse.SUCCESS, list);
		return resp;
	}
*/
}
