package jp.dataforms.fw.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.dataforms.fw.annotation.WebMethod;
import jp.dataforms.fw.exception.ApplicationException;
import jp.dataforms.fw.field.base.Field;
import jp.dataforms.fw.field.base.FieldList;
import jp.dataforms.fw.field.sqltype.BigintField;
import jp.dataforms.fw.field.sqltype.IntegerField;
import jp.dataforms.fw.field.sqltype.VarcharField;
import jp.dataforms.fw.response.JsonResponse;

/**
 * 問い合わせ結果フォームクラス。
 *
 */
public abstract class QueryResultForm extends Form {
	/**
	 * Logger.
	 */
//	private static Logger log = Logger.getLogger(QueryResultForm.class.getName());

	/**
	 * 主キーフィールドリスト。
	 */
	private List<String> pkFieldList = new ArrayList<String>();

	/**
	 * コンストラクタ。
	 */
	public QueryResultForm() {
		this(DataForms.ID_QUERY_RESULT_FORM);
	}

	/**
	 * コンストラクタ。
	 * @param id フォームID。
	 */
	public QueryResultForm(final String id) {
		super(id);
		this.addField(new BigintField("hitCount"));
		this.addField(new IntegerField("pageNo"));
		this.addField(new IntegerField("linesPerPage"));
		this.addField(new VarcharField("sortOrder", 1024)).setHidden(true);
	}

	/**
	 * 主キーフィールドを追加します。
	 * <pre>
	 * 主キーフィールドに指定されたフィールドは、結果リストのリンク、ボタンなどを
	 * クリックした場合、その情報を特定するために使用します。
	 * </pre>
	 * @param field 追加するフィールド。
	 * @return 追加されたフィールド。
	 */
	public Field<?> addPkField(final Field<?> field) {
		// 主キーフィールドはHTMLに出力しない。
		this.addField(field).setHtmlGeneration(false);
		this.pkFieldList.add(field.getId());
		return field;
	}


	/**
	 * 主キーフィールドリストを設定する。
	 * @param fieldList 主キーフィールドリスト.
	 */
	public void addPkFieldList(final FieldList fieldList) {
		if (fieldList != null) {
			for (Field<?> field: fieldList) {
				this.addPkField(field);
			}
		}
	}

	@Override
	public Map<String, Object> getProperties() throws Exception {
		Map<String, Object> ret = super.getProperties();
		ret.put("pkFieldList", this.pkFieldList);
		return ret;
	}

	/**
	 * 問い合わせ結果の指定ページのリストを取得します。
	 * @param data パラメータ。
	 * @param queryFormFieldList QueryFormのフィールドリスト。
	 * @return 指定ページの一覧。
	 * @throws Exception 例外。
	 */
	protected Map<String, Object> queryPage(final Map<String, Object> data, final FieldList queryFormFieldList) throws Exception {
		throw new ApplicationException(this.getPage(), "error.notimplemetmethod");
	}

	/**
	 * 問い合わせ結果のページを変更します。
	 * @param param ページ指定を含むパラメータ。
	 * @return 問い合わせ結果のページ。
	 * @throws Exception 例外。
	 */
	@WebMethod
	public JsonResponse changePage(final Map<String, Object> param) throws Exception {
		JsonResponse result = new JsonResponse(JsonResponse.SUCCESS, "");
		Map<String, Object> data = new HashMap<String, Object>();
		QueryForm qf = (QueryForm) this.getParent().getComponent("queryForm");
		data.putAll(this.convertToServerData(param)); // QueryResultFormのページ指定等のパラメータを変換
		FieldList flist = new FieldList();
		if (qf != null) {
			data.putAll(qf.convertToServerData(param)); // QueryFormの条件パラメータの変換.
			flist = qf.getFieldList();
		}
		Map<String, Object> r = this.queryPage(data, flist);
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> queryResult = (List<Map<String, Object>>) r.get("queryResult");
		if (queryResult.size() == 0) {
			Integer page = (Integer) data.get("pageNo");
			if (page == null) {
				page = Integer.valueOf(0);
			}
			if (page.intValue() > 0) {
				data.put("pageNo", Integer.valueOf(page.intValue() - 1));
				r = this.queryPage(data, flist);
			}
		}
		Map<String, Object> cr = this.convertToClientData(r);
		result = new JsonResponse(JsonResponse.SUCCESS, cr);
		return result;
	}

	/**
	 * データの削除を行ないます。
	 *
	 * <pre>
	 * 検索結果からの削除を行う場合、このメソッドをオーバーライドし、削除処理を実装します。
	 * このメソッドを呼び出すと例外が発生します。
	 * </pre>
	 *
	 * @param data パラメータ。
	 * @throws Exception 例外。
	 */
	protected void deleteData(final Map<String, Object> data) throws Exception {
		throw new ApplicationException(this.getPage(), "error.notimplemetmethod");
	}

	/**
	 * 指定データの削除を行います。
	 *
	 * @param param パラメータ。
	 * @return 応答情報。
	 * @throws Exception 例外。
	 */
	@WebMethod
	public JsonResponse delete(final Map<String, Object> param) throws Exception {
		Map<String, Object> data = this.convertToServerData(param);
		this.deleteData(data);
		JsonResponse result = new JsonResponse(JsonResponse.SUCCESS, "");
		return result;
	}

}



