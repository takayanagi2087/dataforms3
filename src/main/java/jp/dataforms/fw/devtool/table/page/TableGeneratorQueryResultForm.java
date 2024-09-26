package jp.dataforms.fw.devtool.table.page;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jp.dataforms.fw.annotation.WebMethod;
import jp.dataforms.fw.controller.Page;
import jp.dataforms.fw.controller.QueryResultForm;
import jp.dataforms.fw.dao.Dao;
import jp.dataforms.fw.devtool.db.dao.TableManagerDao;
import jp.dataforms.fw.devtool.field.ClassNameField;
import jp.dataforms.fw.devtool.field.PackageNameField;
import jp.dataforms.fw.devtool.field.RecordCountField;
import jp.dataforms.fw.devtool.field.TableClassNameField;
import jp.dataforms.fw.devtool.field.TableNameField;
import jp.dataforms.fw.field.base.FieldList;
import jp.dataforms.fw.field.common.PresenceField;
import jp.dataforms.fw.field.common.RowNoField;
import jp.dataforms.fw.field.sqltype.VarcharField;
import jp.dataforms.fw.htmltable.HtmlTable;
import jp.dataforms.fw.response.BinaryResponse;
import jp.dataforms.fw.response.Response;
import jp.dataforms.fw.util.ClassNameUtil;
import jp.dataforms.fw.util.MessagesUtil;

/**
 * 問い合わせ結果フォームクラス。
 */
public class TableGeneratorQueryResultForm extends QueryResultForm {
	/**
	 * Log.
	 */
	private static Logger logger = LogManager.getLogger(TableGeneratorQueryResultForm.class.getName());

	/**
	 * コンストラクタ。
	 */
	public TableGeneratorQueryResultForm() {
		HtmlTable htmltbl = new HtmlTable(Page.ID_QUERY_RESULT
			, (new RowNoField()).setSpanField(true)
			, (new PackageNameField()).setHidden(true)
			, (new TableClassNameField()).setHidden(true)
			, (new ClassNameField("fullClassName")).setSpanField(true).setSortable(true)
			, (new TableNameField()).setSortable(true)
			, (new VarcharField("tableComment", 1024)).setSortable(true)
			, (new PresenceField("status")).setSpanField(true).setComment("テーブル有無").setSortable(true)
			, new PresenceField("statusVal").setSortable(true)
			, (new PresenceField("sequenceGeneration")).setSpanField(true).setComment("シーケンス有無").setSortable(true)
			, (new PresenceField("difference")).setSpanField(true).setComment("構造の差分").setSortable(true)
			, new PresenceField("differenceVal").setSortable(true)
			, (new RecordCountField()).setSpanField(true).setSortable(true)
		);
		this.addHtmlTable(htmltbl);
		this.addPkField(htmltbl.getFieldList().get("packageName"));
		this.addPkField(htmltbl.getFieldList().get("tableClassName"));
	}

	@Override
	protected Map<String, Object> queryPage(final Map<String, Object> data, final FieldList flist) throws Exception {
    	TableManagerDao dao = new TableManagerDao(this);
    	List<Map<String, Object>> queryResult = dao.queryTableClass(data);
    	List<String> clslist = new ArrayList<String>();
    	for (Map<String, Object> r: queryResult) {
    		String className = (String) r.get("className");
    		r.put("packageName", ClassNameUtil.getPackageName(className));
    		r.put("tableClassName", ClassNameUtil.getSimpleClassName(className));
    		r.put("fullClassName", className);
    	}
    	Map<String, Object> result = new HashMap<String, Object>();
    	result.put("checkedClass", clslist);
    	result.put("queryResult", queryResult);
		return result;
	}

	/**
	 * テーブル定義書を作成します。
	 * @param param パラメータ。
	 * @return テーブル定義書Excelイメージ。
	 * @throws Exception 例外。
	 */
	@WebMethod
	public Response print(final Map<String, Object> param) throws Exception {
		Map<String, Object> data = this.convertToServerData(param);
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> list = (List<Map<String, Object>>) data.get(Page.ID_QUERY_RESULT);
		Response ret = null;
		File template = TableReport.makeTemplate(this);
		List<Map<String, Object>> tlist = new ArrayList<Map<String, Object>>();
		try {
			logger.debug("template path=" + template.getAbsolutePath());
			TableReport rep = new TableReport(template.getAbsolutePath(), list.size() - 1);
			rep.setSystemHeader(MessagesUtil.getMessage(this.getPage(), "message.systemname"));
			for (int i = 0; i < list.size(); i++) {
				Map<String, Object> m = list.get(i);
				Map<String, Object> spec = rep.getTableSpec(m, new Dao(this));
				spec.put("no", i + 1);
				tlist.add(spec);
				rep.setSheetName(i + 1, ((String) m.get("tableClassName")));
				rep.setSheetIndex(i + 1);
				rep.print(spec);
			}
			TableListReport lrep = new TableListReport(rep.getWorkbook());
			Map<String, Object> lmap = new HashMap<String, Object>();
			lmap.put("tableList", tlist);
			lrep.setSheetIndex(0);
			lrep.print(lmap);
			ret = new BinaryResponse(rep.getReport(), "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "tableSpec.xlsx");
		} finally {
			template.delete();
		}
		return ret;
	}
}
