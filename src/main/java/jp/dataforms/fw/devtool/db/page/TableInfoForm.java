package jp.dataforms.fw.devtool.db.page;

import java.util.Map;

import jp.dataforms.fw.annotation.WebMethod;
import jp.dataforms.fw.controller.Form;
import jp.dataforms.fw.devtool.db.dao.TableManagerDao;
import jp.dataforms.fw.field.common.PresenceField;
import jp.dataforms.fw.field.sqltype.IntegerField;
import jp.dataforms.fw.field.sqltype.VarcharField;
import jp.dataforms.fw.response.JsonResponse;
import jp.dataforms.fw.servlet.DataFormsServlet;

/**
 * テーブル情報フォームクラス。
 *
 */
public class TableInfoForm extends Form {
	/**
	 * Log.
	 */
//	private static Logger logger = Logger.getLogger(TableInfoForm.class.getName());
	/**
	 * コンストラクタ。
	 */
	public TableInfoForm() {
		super(null);
		this.addField(new VarcharField("className", 256));
		this.addField(new VarcharField("tableName", 256));
		this.addField(new PresenceField("status"));
		this.addField(new PresenceField("statusVal"));
		this.addField(new PresenceField("sequenceGeneration"));
		this.addField(new PresenceField("difference"));
		this.addField(new PresenceField("differenceVal"));
		this.addField(new IntegerField("recordCount"));
		this.addField(new VarcharField("createTableSql", 4096));
	}

	/**
	 * テーブルを初期化します。
	 * @param p パラメータ。
	 * @return 作成結果。
	 * @throws Exception 例外。
	 */
	@WebMethod
	public JsonResponse initTable(final Map<String, Object> p) throws Exception {
		Map<String, Object> arg = this.convertToServerData(p);
		String className = (String) arg.get("className");
		TableManagerDao dao = new TableManagerDao(this);
		dao.executeBeforeRebuildSql();
		dao.dropAllForeignKeys();
		dao.initTable(className);
		dao.createAllForeignKeys();
		dao.executeAfterRebuildSql();
		JsonResponse r = new JsonResponse(JsonResponse.SUCCESS, dao.getTableInfo(className));
		return r;
	}

	/**
	 * テーブルを削除します。
	 * @param p パラメータ。
	 * @return 削除結果。
	 * @throws Exception 例外。
	 */
	@WebMethod
	public JsonResponse dropTable(final Map<String, Object> p) throws Exception {
		Map<String, Object> arg = this.convertToServerData(p);
		String className = (String) arg.get("className");
		TableManagerDao dao = new TableManagerDao(this);
		dao.dropTable(className);
		JsonResponse r = new JsonResponse(JsonResponse.SUCCESS, dao.getTableInfo(className));
		return r;
	}

	/**
	 * テーブルを更新します。
	 * @param p パラメータ。
	 * @return 更新結果。
	 * @throws Exception 例外。
	 */
	@WebMethod
	public JsonResponse updateTable(final Map<String, Object> p) throws Exception {
		Map<String, Object> arg = this.convertToServerData(p);
		String className = (String) arg.get("className");
		TableManagerDao dao = new TableManagerDao(this);
		dao.executeBeforeRebuildSql();
		dao.dropAllForeignKeys();
		dao.updateTable(className);
		dao.createAllForeignKeys();
		dao.executeAfterRebuildSql();
		JsonResponse r = new JsonResponse(JsonResponse.SUCCESS, dao.getTableInfo(className));
		return r;
	}

	/**
	 * データをエクスポートします。
	 * @param p パラメータ。
	 * @return テーブル情報。
	 * @throws Exception 例外。
	 */
	@WebMethod
	public JsonResponse exportData(final Map<String, Object> p) throws Exception {
		Map<String, Object> arg = this.convertToServerData(p);
		String className = (String) arg.get("className");
		TableManagerDao dao = new TableManagerDao(this);
		String filename = dao.exportData(className, DataFormsServlet.getExportImportDir());
		Map<String, Object> m = dao.getTableInfo(className);
		m.put("exportDataPath", filename);
		JsonResponse r = new JsonResponse(JsonResponse.SUCCESS, m);
		return r;
	}

	/**
	 * データをインポートします。
	 * @param p パラメータ。
	 * @return テーブル情報。
	 * @throws Exception 例外。
	 */
	/*
	@WebMethod
	public JsonResponse importData(final Map<String, Object> p) throws Exception {
		this.methodStartLog(logger, p);
		Map<String, Object> arg = this.convertToServerData(p);
		String className = (String) arg.get("className");
		TableManagerDao dao = new TableManagerDao(this);
		dao.importIntialData(className);
		JsonResponse r = new JsonResponse(JsonResponse.SUCCESS, dao.getTableInfo(className));
		this.methodFinishLog(logger, r);
		return r;
	}*/

}
