package jp.dataforms.fw.dbtool.db.page;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jp.dataforms.fw.annotation.WebMethod;
import jp.dataforms.fw.controller.Page;
import jp.dataforms.fw.controller.QueryResultForm;
import jp.dataforms.fw.devtool.base.page.DeveloperPage;
import jp.dataforms.fw.devtool.db.dao.TableManagerDao;
import jp.dataforms.fw.devtool.db.table.DbTableListHtmlTable;
import jp.dataforms.fw.exception.ApplicationException;
import jp.dataforms.fw.field.base.FieldList;
import jp.dataforms.fw.field.common.MultiSelectField;
import jp.dataforms.fw.field.sqltype.VarcharField;
import jp.dataforms.fw.response.JsonResponse;
import jp.dataforms.fw.servlet.DataFormsServlet;
import jp.dataforms.fw.util.MessagesUtil;
import jp.dataforms.fw.util.StringUtil;

/**
 * DB管理ページの検索結果フォームクラス。
 *
 */
public class TableManagementQueryResultForm extends QueryResultForm {
	/**
	 * Log.
	 */
	private static Logger logger = LogManager.getLogger(TableManagementQueryResultForm.class.getName());
	/**
	 * コンストラクタ。
	 */
	public TableManagementQueryResultForm() {
		this.addField(new VarcharField("className", 256));
		this.addField(new MultiSelectField<String>("checkedClass"));
		DbTableListHtmlTable htmltbl = new DbTableListHtmlTable(Page.ID_QUERY_RESULT);
		this.addHtmlTable(htmltbl);
	}

	@Override
	public void init() throws Exception {
		super.init();
	}

	@Override
	protected Map<String, Object> queryPage(final Map<String, Object> data, final FieldList flist) throws Exception {
		TableManagerDao dao = new TableManagerDao(this);
		List<Map<String, Object>> queryResult = dao.queryTableClass(data);
		List<String> clslist = new ArrayList<String>();
		for (Map<String, Object> r : queryResult) {
			String statusVal = (String) r.get("statusVal");
			if ("0".equals(statusVal)) {
				clslist.add((String) r.get("className"));
			}
		}
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("checkedClass", clslist);
		result.put("queryResult", queryResult);
		return result;
	}

	/**
	 * テーブル情報を取得します。
	 * @param params パラメータ。
	 * @return SQL。
	 * @throws Exception 例外。
	 */
	@WebMethod
	public JsonResponse getTableInfo(final Map<String, Object> params) throws Exception {
		TableManagerDao dao = new TableManagerDao(this);
		Map<String, Object> p = this.convertToServerData(params);
		String classname = (String) p.get("className");
		JsonResponse ret = new JsonResponse(JsonResponse.SUCCESS, dao.getTableInfo(classname));
		return ret;
	}

	/**
	 * テーブルの初期化処理を行います。
	 * @param params パラメータ。
	 * @return 更新結果。
	 * @throws Exception 例外。
	 */
	@WebMethod
	public JsonResponse initTable(final Map<String, Object> params) throws Exception {
		Map<String, Object> p = this.convertToServerData(params);

		TableManagerDao dao = new TableManagerDao(this);
		dao.executeBeforeRebuildSql();
		dao.dropAllForeignKeys();
		@SuppressWarnings("unchecked")
		List<String> classlist = (List<String>) p.get("checkedClass");
		for (String cls : classlist) {
			dao.initTable(cls);
		}
		dao.createAllForeignKeys();
		dao.executeAfterRebuildSql();
		List<Map<String, Object>> result = dao.getTableInfoList(classlist);
		JsonResponse ret = new JsonResponse(JsonResponse.SUCCESS, result);
		return ret;
	}

	/**
	 * テーブルの更新処理を行います。
	 * @param params パラメータ。
	 * @return 更新結果。
	 * @throws Exception 例外。
	 */
	@WebMethod
	public JsonResponse updateTable(final Map<String, Object> params) throws Exception {
		Map<String, Object> p = this.convertToServerData(params);

		TableManagerDao dao = new TableManagerDao(this);
		dao.executeBeforeRebuildSql();
		dao.dropAllForeignKeys();
		@SuppressWarnings("unchecked")
		List<String> classlist = (List<String>) p.get("checkedClass");
		for (String cls: classlist) {
			dao.updateTable(cls);
		}
		dao.createAllForeignKeys();
		dao.executeAfterRebuildSql();
		List<Map<String, Object>> result = dao.getTableInfoList(classlist);
		JsonResponse ret = new JsonResponse(JsonResponse.SUCCESS, result);
		return ret;
	}

	// TODO:FKを設定したテーブルを削除すると問題が発生する可能性がある。要確認。
	/**
	 * テーブルを削除します。
	 * @param params パラメータ。
	 * @return 各テーブル情報。
	 * @throws Exception 例外。
	 */
	@WebMethod
	public JsonResponse dropTable(final Map<String, Object> params) throws Exception {
		Map<String, Object> p = this.convertToServerData(params);

		TableManagerDao dao = new TableManagerDao(this);
		@SuppressWarnings("unchecked")
		List<String> classlist = (List<String>) p.get("checkedClass");
		for (String cls : classlist) {
			dao.moveToBackupTable(cls);
		}
		List<Map<String, Object>> result = dao.getTableInfoList(classlist);
		JsonResponse ret = new JsonResponse(JsonResponse.SUCCESS, result);
		return ret;
	}

	/**
	 * テーブルのデータをエクスポートします。
	 * @param params パラメータ。
	 * @return 出力パス情報。
	 * @throws Exception 例外。
	 */
	@WebMethod
	public JsonResponse exportTable(final Map<String, Object> params) throws Exception {
		Map<String, Object> p = this.convertToServerData(params);

		TableManagerDao dao = new TableManagerDao(this);
		@SuppressWarnings("unchecked")
		List<String> classlist = (List<String>) p.get("checkedClass");
		for (String cls : classlist) {
			dao.exportData(cls, DataFormsServlet.getExportImportDir());
		}
		JsonResponse ret = new JsonResponse(JsonResponse.SUCCESS, DataFormsServlet.getExportImportDir());
		return ret;
	}

	/**
	 * 選択されたテーブルの初期化データを作成します。
	 * @param params パラメータ。
	 * @return 出力パス情報。
	 * @throws Exception 例外。
	 */
	@WebMethod
	public JsonResponse exportTableAsInitialData(final Map<String, Object> params) throws Exception {
		Map<String, Object> p = this.convertToServerData(params);

		TableManagerDao dao = new TableManagerDao(this);
		@SuppressWarnings("unchecked")
		List<String> classlist = (List<String>) p.get("checkedClass");
		if (StringUtil.isBlank(DeveloperPage.getWebSourcePath())) {
			throw new ApplicationException(this.getPage(), "error.webresourcepathnotfound");
		}
		String initialDataPath =  DeveloperPage.getExportInitalDataPath(this.getPage()); // DeveloperPage.getWebSourcePath() + "/WEB-INF/initialdata";
		for (String cls : classlist) {
			dao.exportData(cls, initialDataPath);
		}
		JsonResponse ret = new JsonResponse(JsonResponse.SUCCESS, initialDataPath);
		return ret;
	}

	/**
	 * テーブルのデータをインポートします。
	 * @param params パラメータ。
	 * @return 各テーブル情報。
	 * @throws Exception 例外。
	 */
	@WebMethod
	public JsonResponse importTable(final Map<String, Object> params) throws Exception {
		Map<String, Object> p = this.convertToServerData(params);
		String datapath = (String) params.get("datapath");
		TableManagerDao dao = new TableManagerDao(this);
		@SuppressWarnings("unchecked")
		List<String> classlist = (List<String>) p.get("checkedClass");
		for (String cls : classlist) {
			dao.importData(cls, datapath);
		}
		List<Map<String, Object>> result = dao.getTableInfoList(classlist);
		JsonResponse ret = new JsonResponse(JsonResponse.SUCCESS, result);
		return ret;
	}

	/**
	 * バックアップテーブルを削除します。
	 * @param param パラメータ。
	 * @return 応答情報。
	 * @throws Exception 例外。
	 */
	@WebMethod
	public JsonResponse dropBackupTable(final Map<String, Object> param) throws Exception {
		String table = (String) param.get("table");
		logger.debug("table=" + table);
		TableManagerDao dao = new TableManagerDao(this);
		dao.dropBackupTable(table);
		JsonResponse ret = new JsonResponse(JsonResponse.SUCCESS, MessagesUtil.getMessage(this.getPage(), "message.backupTableDroped"));
		return ret;
	}
}
