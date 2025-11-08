package jp.dataforms.fw.dbtool.query.page;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jp.dataforms.fw.annotation.WebMethod;
import jp.dataforms.fw.controller.Page;
import jp.dataforms.fw.controller.QueryResultForm;
import jp.dataforms.fw.dao.Dao;
import jp.dataforms.fw.field.base.Field;
import jp.dataforms.fw.field.base.FieldList;
import jp.dataforms.fw.field.base.NumberField;
import jp.dataforms.fw.field.common.SortOrderField;
import jp.dataforms.fw.field.sqltype.BigintField;
import jp.dataforms.fw.field.sqltype.CharField;
import jp.dataforms.fw.field.sqltype.DateField;
import jp.dataforms.fw.field.sqltype.IntegerField;
import jp.dataforms.fw.field.sqltype.NumericField;
import jp.dataforms.fw.field.sqltype.SmallintField;
import jp.dataforms.fw.field.sqltype.TimestampField;
import jp.dataforms.fw.htmltable.PageScrollHtmlTable;
import jp.dataforms.fw.response.JsonResponse;
import jp.dataforms.fw.util.JsonUtil;
import jp.dataforms.fw.util.StringUtil;


/**
 * 問い合わせ結果フォームクラス。
 */
public class QueryExecutorQueryResultForm extends QueryResultForm {

	/**
	 * Logger.
	 */
	private static Logger logger = LogManager.getLogger(QueryExecutorQueryResultForm.class);

	/**
	 * HTMLテーブル。
	 */
	private PageScrollHtmlTable htmlTable = null;

	/**
	 * コンストラクタ。
	 */
	public QueryExecutorQueryResultForm() {
		// TODO: jsでimportを利用して改善したい。
		this.addField(new DateField("dummyDate"));
		this.addField(new TimestampField("dummyTimestamp"));
		this.addField(new SmallintField("dummySmallint"));
		this.addField(new IntegerField("dummyInteger"));
		this.addField(new BigintField("dummyBigint"));
		this.addField(new NumericField("dummyNumeric", 16, 2));
		this.addField(new CharField("dummyChar", 1));
		this.addField(new SortOrderField("dummySortOrder"));
		
		this.htmlTable = new PageScrollHtmlTable(Page.ID_QUERY_RESULT);
//		this.htmlTable.setFixedColumns(2);
		this.addHtmlTable(this.htmlTable);
	}

	/**
	 * Order byに指定するカラムリストを取得します。
	 * @param sortOrder ソート順指定。
	 * @return order by指定のカラムリスト。
	 */
	private String getOrderByString(final String sortOrder) {
		StringBuilder sb = new StringBuilder();
		if (!StringUtil.isBlank(sortOrder)) {
			String[] sp = sortOrder.split("\\,");
			for (String f: sp) {
				logger.debug("f=" + f);
				String[] fsp = f.split("\\:");
				logger.debug("fsp[0]=" + fsp[0]);
				String id = fsp[0];
				String order = fsp[1];
				if (sb.length() > 0) {
					sb.append(",");
				}
				sb.append(StringUtil.camelToSnake(id));
				sb.append(" ");
				sb.append(order);
			}
		}
		return sb.toString();
	}

	@Override
	protected Map<String, Object> queryPage(final Map<String, Object> data, final FieldList queryFormFieldList) throws Exception {
		String sql = (String) data.get("sql");
		String sortOrder = (String) data.get("sortOrder");
		String orderby = this.getOrderByString(sortOrder);
		logger.debug("orderby=" + orderby);
		if (sortOrder.length() > 0) {
			sql = "select * from (" + sql + ") m order by " + orderby;
		}
		Dao dao = new Dao(this);
		Map<String, Object> ret = dao.executePageQuery(sql, data);
		FieldList flist = dao.getResultSetFieldList();
		for (Field<?> f: flist) {
			f.init();
		}
		this.htmlTable.getFieldList().clear();
		this.htmlTable.getFieldList().addAll(flist);
		for (Field<?> f: this.htmlTable.getFieldList()) {
			if (!"rowNo".equals(f.getId())) {
				f.setSortable(true);
			}
		}
		this.htmlTable.getFieldList().getOrderByFieldList(sortOrder);
		ret.put("htmlTable", htmlTable.getProperties());
		logger.debug("result=" + JsonUtil.encode(ret, true));
		return ret;
	}

	/**
	 * テーブルのヘッダを作成します。
	 * @return テーブルのヘッダ。
	 */
	private String getHeaderHtml() {
		StringBuilder sb = new StringBuilder();
		for (Field<?> f: this.htmlTable.getFieldList()) {
			sb.append("<th>");
			sb.append(f.getId());
			sb.append("</th>");
		}
		return sb.toString();
	}

	/**
	 * テーブルのデータ行を作成します。
	 * @return テーブルのデータ行。
	 */
	private String getDataHtml() {
		StringBuilder sb = new StringBuilder();
//		boolean flg = true;
		for (Field<?> f: this.htmlTable.getFieldList()) {
			if (f instanceof NumberField) {
				sb.append("<td style='text-align: right;'>");
//				flg = false;
			} else {
				sb.append("<td>");
			}
			if (Page.getUseUniqueId()) {
				sb.append("<span data-id=\"queryResult[0].");
				sb.append(f.getId());
				sb.append("\" ");
				sb.append(" id=\"mainDiv.queryResultForm.queryResult[0].");
				sb.append(f.getId());
				sb.append("\" ");
				sb.append("></span>");
			} else {
				sb.append("<span id=\"queryResult[0].");
				sb.append(f.getId());
				sb.append("\"></span>");
			}
			sb.append("</td>");
		}
		return sb.toString();
	}

	@WebMethod
	@Override
	public JsonResponse changePage(final Map<String, Object> param) throws Exception {
		try {
			JsonResponse ret = super.changePage(param);
			@SuppressWarnings("unchecked")
			Map<String, Object> r = (Map<String, Object>) ret.getResult();
			r.put("headerHtml", this.getHeaderHtml());
			r.put("dataHtml", this.getDataHtml());
			r.put("htmlTable", this.htmlTable.getProperties());
			return ret;
		} catch (Exception e) {
			Map<String, Object> einfo = new HashMap<String, Object>();
			einfo.put("message", e.getMessage());
			JsonResponse ret = new JsonResponse(JsonResponse.APPLICATION_EXCEPTION, einfo);
			return ret;
		}
	}

	@Override
	protected void deleteData(final Map<String, Object> data) throws Exception {
	}
}
