package jp.dataforms.fw.devtool.update.page;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jp.dataforms.fw.annotation.WebMethod;
import jp.dataforms.fw.controller.Form;
import jp.dataforms.fw.dao.Dao;
import jp.dataforms.fw.dao.Table;
import jp.dataforms.fw.dao.sqlgen.SqlGenerator;
import jp.dataforms.fw.devtool.field.FunctionSelectField;
import jp.dataforms.fw.devtool.field.PackageNameField;
import jp.dataforms.fw.devtool.field.SqlField;
import jp.dataforms.fw.devtool.field.TableClassNameField;
import jp.dataforms.fw.devtool.field.UpdateSqlTypeField;
import jp.dataforms.fw.devtool.field.UpdateSqlTypeField.UpdateSqlType;
import jp.dataforms.fw.field.base.FieldList;
import jp.dataforms.fw.field.common.RecordIdField;
import jp.dataforms.fw.response.JsonResponse;
import jp.dataforms.fw.response.Response;
import jp.dataforms.fw.util.MessagesUtil;
import jp.dataforms.fw.util.StringUtil;
import jp.dataforms.fw.validator.RequiredValidator;
import jp.dataforms.fw.validator.ValidationError;

/**
 * 更新系SQL実行フォームクラス。
 */
public class UpdateSqlForm extends Form {
	/**
	 * Logger.
	 */
	private static Logger logger = LogManager.getLogger(UpdateSqlForm.class);

	/**
	 * SQLフィールドID。
	 */
	public static final String ID_SQL = "sql";

	/**
	 * 更新SQLTypeフィールド。
	 */
	private UpdateSqlTypeField updateSqlTypeField = null;

	/**
	 * コンストラクタ。
	 */
	public UpdateSqlForm() {
		super(null);
		this.addField(new FunctionSelectField());
		this.addField(new PackageNameField());
		this.addField(new TableClassNameField()).setAutocomplete(true).setRelationDataAcquisition(true);
		this.addField(this.updateSqlTypeField = new UpdateSqlTypeField());
		this.addField(new SqlField(ID_SQL)).addValidator(new RequiredValidator());
	}

	@Override
	public void init() throws Exception {
		super.init();
		String id = this.updateSqlTypeField.getId();
		this.setFormData(id, UpdateSqlType.INSERT.getString());
	}

	/**
	 * 指定されたテーブルクラスのインスタンスを作成します。
	 * @param param POSTされたパラメータ。
	 * @return 問合せクラスのインスタンス。
	 * @throws Exception 例外。
	 */
	private Table newTable(final Map<String, Object> param) throws Exception {
		String packageName = (String) param.get("packageName");
		String tableClassName = (String) param.get("tableClassName");
		logger.debug("tableClass=" + packageName + "." + tableClassName);
		@SuppressWarnings("unchecked")
		Class<? extends Table> q = (Class<? extends Table>) Class.forName(packageName + "." + tableClassName);
		return q.getDeclaredConstructor().newInstance();
	}

	/**
	 * 新規レコードIDの値を設定します。
	 * @param dao Dao。
	 * @param table データ。
	 * @param sql SQL。
	 * @return ID値を設定したSQL。
	 * @throws Exception 例外。
	 */
	private String setIdValue(final Dao dao, final Table table, final String sql) throws Exception {
		String ret = sql;
		FieldList pklist = table.getPkFieldList();
		if (pklist.size() == 1 && pklist.get(0) instanceof RecordIdField) {
			SqlGenerator gen = dao.getSqlGenerator();
			RecordIdField pkf = (RecordIdField) pklist.get(0);
			String colname = StringUtil.camelToSnake(pkf.getId());
			if (gen.isSequenceSupported()) {
				String genseq = gen.generateGetRecordIdSqlForInsert(table);
				ret = ret.replace(":" + colname, genseq);
			} else {
				ret = ret.replace(":" + colname, "null");
			}
		}
		return ret;
	}


	/**
	 * 登録者、更新者のユーザIDを設定します。
	 * @param sql SQL。
	 * @return 登録者、更新者のユーザIDを設定したSQL。
	 */
	private String setUserId(final String sql) {
		Long userId = this.getPage().getUserId();
		String ret = sql.replace(":create_user_id", userId.toString());
		ret = ret.replace(":update_user_id", userId.toString());
		return ret;
	}

	/**
	 * SQLを作成します。
	 * @param type 更新SQLタイプ。
	 * @param table テーブル。
	 * @return SQL。
	 * @throws Exception 例外。
	 */
	private String generateSql(final String type, final Table table) throws Exception {
		Dao dao = new Dao(this);
		SqlGenerator gen = dao.getSqlGenerator();
		String sql = null;
		if (UpdateSqlType.INSERT.getString().equals(type)) {
			String seq = gen.generateGetRecordIdSql(table);
			logger.debug("seq=" + seq);
			sql = gen.generateInsertSql(table);
			sql = sql.replaceAll("\\(", "(\n\t");
			sql = sql.replaceAll(",", "\n\t, ");
			sql = sql.replaceAll("\\)", "\n)");
			sql = this.setIdValue(dao, table, sql);
		} else if (UpdateSqlType.UPDATE.getString().equals(type)) {
			sql = gen.generateUpdateSql(table);
			sql = sql.replaceAll(" set \n", " set\n\t");
			sql = sql.replaceAll(", ", "\t, ");
			sql = sql.replaceAll("where\n", "where\n\t");
		} else if (UpdateSqlType.DELETE.getString().equals(type)) {
			sql = gen.generateDeleteSql(table);
		}
		sql = this.setUserId(sql);
		return sql;
	}

	/**
	 * 指定された問合せクラスに対応したsqlを取得します。
	 * @param param パラメータ。
	 * @return SQLの応答。
	 * @throws Exception 例外。
	 */
	@WebMethod
	public Response generateSql(final Map<String, Object> param) throws Exception {
		Table table = this.newTable(param);
		String type = (String) param.get("updateSqlType");
		String sql = this.generateSql(type, table);
		Response resp = new JsonResponse(JsonResponse.SUCCESS, sql);
		return resp;
	}


	/**
	 * 更新系のSQLを実行します。
	 * @param param パラメータ。
	 * @return 更新結果。
	 * @throws Exception 例外。
	 */
	@WebMethod
	public Response update(final Map<String, Object> param) throws Exception {
		Response resp = null;
		List<ValidationError> list = this.validate(param);
		if (list.size() == 0) {
			Dao dao = new Dao(this);
			String sql = (String) param.get(UpdateSqlForm.ID_SQL);
			long cnt = dao.executeUpdate(sql, new HashMap<String, Object>());
			resp = new JsonResponse(JsonResponse.SUCCESS, MessagesUtil.getMessage(this.getPage(), "message.updatesuccessfull", Long.toString(cnt)));
		} else {
			resp = new JsonResponse(JsonResponse.INVALID, list);
		}
		return resp;
	}
}
