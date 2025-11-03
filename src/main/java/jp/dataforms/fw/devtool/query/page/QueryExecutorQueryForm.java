package jp.dataforms.fw.devtool.query.page;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jp.dataforms.fw.annotation.WebMethod;
import jp.dataforms.fw.controller.QueryForm;
import jp.dataforms.fw.dao.Dao;
import jp.dataforms.fw.dao.Query;
import jp.dataforms.fw.dao.Table;
import jp.dataforms.fw.dao.sqlgen.SqlGenerator;
import jp.dataforms.fw.devtool.field.FunctionSelectField;
import jp.dataforms.fw.devtool.field.PackageNameField;
import jp.dataforms.fw.devtool.field.QueryClassNameField;
import jp.dataforms.fw.devtool.field.SqlField;
import jp.dataforms.fw.field.base.Field;
import jp.dataforms.fw.field.base.FieldList;
import jp.dataforms.fw.response.JsonResponse;
import jp.dataforms.fw.response.Response;
import jp.dataforms.fw.validator.RequiredValidator;

/**
 * 問い合わせフォームクラス。
 */
public class QueryExecutorQueryForm extends QueryForm {

	/**
	 * Log.
	 */
	private static Logger logger = LogManager.getLogger(QueryExecutorQueryForm.class);

	/**
	 * コンストラクタ。
	 */
	public QueryExecutorQueryForm() {
		this.addField(new FunctionSelectField());
		this.addField(new PackageNameField());
		QueryClassNameField fld = new QueryClassNameField("queryClassName") {
			@Override
			protected String getClassNameSuffix() {
				return "((Query)|(Table))";
			}
		};
		fld.addBaseClass(Table.class);
		this.addField(fld).setAutocomplete(true).setRelationDataAcquisition(true);
		this.addField(new SqlField("sql")).addValidator(new RequiredValidator());
	}

	@Override
	public void init() throws Exception {
		super.init();
		String tableName = (String) this.getPage().getRequest().getSession().getAttribute(QueryExecutorPage.ID_TABLE_NAME);
		if (tableName != null) {
			this.setFormData("sql", "select * from " + tableName);
		}
	}

	/**
	 * 指定されたテーブルまたは合せクラスのインスタンスを作成します。
	 * @param param POSTされたパラメータ。
	 * @return 問合せクラスのインスタンス。
	 * @throws Exception 例外。
	 */
	private Object newTableOrQuery(final Map<String, Object> param) throws Exception {
		String packageName = (String) param.get("packageName");
		String queryClassName = (String) param.get("queryClassName");
		logger.debug("queryClass=" + packageName + "." + queryClassName);
		Class<? extends Object> q = (Class<? extends Object>) Class.forName(packageName + "." + queryClassName);
		Object obj = q.getDeclaredConstructor().newInstance();
		return obj;
	}

	/**
	 * テーブルを選択するSQL。
	 * @param table テーブルクラス。
	 * @return SQL。
	 */
	private String getTableSelectSQL(final Table table) {
		StringBuilder sb = new StringBuilder();
		sb.append("select\n");
		FieldList flist = table.getFieldList();
		for (int i = 0; i < flist.size(); i++) {
			Field<?> f = flist.get(i);
			if (i > 0) {
				sb.append("\t, ");
			} else {
				sb.append("\t");
			}
			sb.append(f.getDbColumnName());
			sb.append("\n");
		}
		sb.append("from\n");
		sb.append("\t" + table.getTableName());
		return sb.toString();
	}
	
	
	/**
	 * 指定された問合せクラスに対応したsqlを取得します。
	 * @param param パラメータ。
	 * @return SQLの応答。
	 * @throws Exception 例外。
	 */
	@WebMethod
	public Response getSql(final Map<String, Object> param) throws Exception {
		Object obj = this.newTableOrQuery(param);
		Dao dao = new Dao(this);
		if (obj instanceof Query) {
			Query query = (Query) obj;
			SqlGenerator gen = dao.getSqlGenerator();
			String sql = gen.generateQuerySql(query);
			Response resp = new JsonResponse(JsonResponse.SUCCESS, sql);
			return resp;
		} else if (obj instanceof Table) {
			Table table = (Table) obj;
			String sql = this.getTableSelectSQL(table);
			Response resp = new JsonResponse(JsonResponse.SUCCESS, sql);
			return resp;
		} else {
			return new JsonResponse(JsonResponse.SUCCESS, "");
		}
	}

	/**
	 * エクスポートデータのフィールドリスト。
	 */
	private FieldList exportFieldList = null;


	@Override
	protected List<Map<String, Object>> queryExportData(final Map<String, Object> data) throws Exception {
		Dao dao = new Dao(this);
		String sql = (String) data.get("sql");
		List<Map<String, Object>> list = dao.executeQuery(sql, new HashMap<String, Object>());
		//
		FieldList flist = dao.getResultSetFieldList();
		for (Field<?> f: flist) {
			f.init();
		}
		this.exportFieldList = flist;
		logger.info("*this.exportFieldList=" + this.exportFieldList);
		return list;
	}

	@Override
	protected FieldList getExportDataFieldList(final Map<String, Object> data) throws Exception {
		logger.info("this.exportFieldList=" + this.exportFieldList);
		return this.exportFieldList;
	}

}
