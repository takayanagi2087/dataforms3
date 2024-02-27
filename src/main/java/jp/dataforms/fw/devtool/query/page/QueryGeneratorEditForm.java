package jp.dataforms.fw.devtool.query.page;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jp.dataforms.fw.annotation.WebMethod;
import jp.dataforms.fw.controller.EditForm;
import jp.dataforms.fw.dao.Dao;
import jp.dataforms.fw.dao.JoinConditionInterface1;
import jp.dataforms.fw.dao.Query;
import jp.dataforms.fw.dao.Query.JoinInfo;
import jp.dataforms.fw.dao.Table;
import jp.dataforms.fw.dao.TableList;
import jp.dataforms.fw.dao.sqlgen.SqlGenerator;
import jp.dataforms.fw.devtool.base.page.DeveloperPage;
import jp.dataforms.fw.devtool.entity.EntityGenerator;
import jp.dataforms.fw.devtool.field.AliasNameField;
import jp.dataforms.fw.devtool.field.FunctionSelectField;
import jp.dataforms.fw.devtool.field.JavaSourcePathField;
import jp.dataforms.fw.devtool.field.JoinTypeField;
import jp.dataforms.fw.devtool.field.OverwriteModeField;
import jp.dataforms.fw.devtool.field.PackageNameField;
import jp.dataforms.fw.devtool.field.QueryClassNameField;
import jp.dataforms.fw.devtool.field.TableOrSubQueryClassNameField;
import jp.dataforms.fw.devtool.javasrc.JavaSrc;
import jp.dataforms.fw.field.base.Field;
import jp.dataforms.fw.field.base.FieldList;
import jp.dataforms.fw.field.base.TextField;
import jp.dataforms.fw.field.common.FlagField;
import jp.dataforms.fw.field.sqlfunc.AliasField;
import jp.dataforms.fw.field.sqlfunc.GroupSummaryField;
import jp.dataforms.fw.field.sqlfunc.SqlField;
import jp.dataforms.fw.htmltable.EditableHtmlTable;
import jp.dataforms.fw.response.JsonResponse;
import jp.dataforms.fw.servlet.DataFormsServlet;
import jp.dataforms.fw.util.ClassNameUtil;
import jp.dataforms.fw.util.FileUtil;
import jp.dataforms.fw.util.ImportUtil;
import jp.dataforms.fw.util.MessagesUtil;
import jp.dataforms.fw.util.StringUtil;
import jp.dataforms.fw.validator.RequiredValidator;
import jp.dataforms.fw.validator.ValidationError;
import net.arnx.jsonic.JSON;

/**
 * 問合せJavaクラス作成編集フォーム。
 *
 */
public class QueryGeneratorEditForm extends EditForm {

	/**
	 * コンストラクタを更新しない。
	 */
	private static final String ID_NOT_UPDATE_CONSTRACTOR = "notUpdateConstractor";

	/**
	 * Entityクラスを生成しない。
	 */
	private static final String ID_NOT_GENERATE_ENTITY = "notGenerateEntity";

	/**
	 * JAVAソースパスフィールドID。
	 */
	private static final String ID_JAVA_SOURCE_PATH = "javaSourcePath";

	/**
	 * パッケージ名フィールドID。
	 */
	private static final String ID_PACKAGE_NAME = "packageName";

	/**
	 * 問合せクラス名フィールドID。
	 */
	private static final String ID_QUERY_CLASS_NAME = "queryClassName";

	/**
	 * 上書きモードフィールドID。
	 */
	private static final String ID_OVERWRITE_MODE = "overwriteMode";


	/**
	 * distinctフラグフィールドID。
	 */
	private static final String ID_DISTINCT_FLAG = "distinctFlag";

	/**
	 * 主テーブル機能選択フィールドID。
	 */
	private static final String ID_MAIN_TABLE_FUNCTION_SELECT = "mainTableFunctionSelect";

	/**
	 * 主テーブルパッケージ名称フィールドID。
	 */
	private static final String ID_MAIN_TABLE_PACKAGE_NAME = "mainTablePackageName";

	/**
	 * 主テーブルクラス名フィールドID。
	 */
	private static final String ID_MAIN_TABLE_CLASS_NAME = "mainTableClassName";

	/**
	 * 別名のフィールドID。
	 */
	private static final String ID_ALIAS_NAME = "aliasName";

	/**
	 * コメントフィールドID。
	 */
	private static final String ID_QUERY_COMMENT = "queryComment";

	/**
	 * 選択フィールドリストのID。
	 */
	private static final String ID_SELECT_FIELD_LIST = "selectFieldList";

	/**
	 * SQLフィールドリストのID。
	 */
	private static final String ID_SQL_FIELD_LIST = "sqlFieldList";

	/**
	 * 結合テーブルリストID。
	 */
	private static final String ID_JOIN_TABLE_LIST = "joinTableList";

	/**
	 * テーブルプロパティ名称。
	 */
	private static final String ID_TABLE_PROP = "tableProp";

	/**
	 * Logger.
	 */
	private static Logger logger = LogManager.getLogger(QueryGeneratorEditForm.class);


	/**
	 * コンストラクタ。
	 */
	public QueryGeneratorEditForm() {
		this.addField(new JavaSourcePathField());
		((FunctionSelectField) this.addField(new FunctionSelectField())).setPackageOption("dao");
		this.addField(new PackageNameField()).addValidator(new RequiredValidator());
		this.addField(new QueryClassNameField()).setAutocomplete(false).addValidator(new RequiredValidator());

		this.addField(new OverwriteModeField());
		this.addField(new FlagField(ID_DISTINCT_FLAG));
		this.addField(new TextField(ID_QUERY_COMMENT));

		this.addField((new FunctionSelectField(ID_MAIN_TABLE_FUNCTION_SELECT)).setPackageFieldId(ID_MAIN_TABLE_PACKAGE_NAME)).setComment("主テーブルの機能");
		this.addField(new PackageNameField(ID_MAIN_TABLE_PACKAGE_NAME)).setComment("主テーブルのパッケージ").addValidator(new RequiredValidator());
		this.addField((new TableOrSubQueryClassNameField(ID_MAIN_TABLE_CLASS_NAME)).setPackageNameFieldId(ID_MAIN_TABLE_PACKAGE_NAME))
			.setAutocomplete(true)
			.setRelationDataAcquisition(true)
			.addValidator(new RequiredValidator())
			.setComment("主テーブルクラス名");
		this.addField(new AliasNameField()).setCalcEventField(true).addValidator(new RequiredValidator());

		EditableHtmlTable joinTableList = new JoinHtmlTable(ID_JOIN_TABLE_LIST);
		joinTableList.setCaption("JOINするテーブルリスト");
		this.addHtmlTable(joinTableList);
		SelectFieldHtmlTable slectFieldList = new SelectFieldHtmlTable(ID_SELECT_FIELD_LIST);
		slectFieldList.setCaption("選択フィールドリスト");
		this.addHtmlTable(slectFieldList);
		SqlFieldHtmlTable sqlFieldList = new SqlFieldHtmlTable(ID_SQL_FIELD_LIST);
		sqlFieldList.setCaption("SQLフィールドリスト");
		this.addHtmlTable(sqlFieldList);

		this.addField(new FlagField(ID_NOT_UPDATE_CONSTRACTOR));
		this.addField(new FlagField(ID_NOT_GENERATE_ENTITY));
	}

	@Override
	public void init() throws Exception {
		super.init();
		this.setFormData(ID_JAVA_SOURCE_PATH, DeveloperPage.getJavaSourcePath());
		this.setFormData(ID_OVERWRITE_MODE, OverwriteModeField.ERROR);
		this.setFormData(ID_ALIAS_NAME, "m");
	}

	/**
	 * JOINテーブルリストのデータを取得します。
	 * @param list JOINテーブルリスト。
	 * @return JOINテーブルリストのデータ。
	 */
	private List<Map<String, Object>> getJoinTableData(final List<Query.JoinInfo> list) {
		List<Map<String, Object>> ret = new ArrayList<Map<String, Object>>();
		if (list != null) {
			for (Query.JoinInfo jinfo: list) {
				Table t = jinfo.getJoinTable();
				Map<String, Object> m = new HashMap<String, Object>();
				if (JoinInfo.INNER_JOIN.equals(jinfo.getJoinType())) {
					m.put(JoinHtmlTable.ID_JOIN_TYPE, JoinTypeField.INNER_JOIN);
				} else if (JoinInfo.LEFT_JOIN.equals(jinfo.getJoinType())) {
					m.put(JoinHtmlTable.ID_JOIN_TYPE, JoinTypeField.LEFT_JOIN);
				} else if (JoinInfo.RIGHT_JOIN.equals(jinfo.getJoinType())) {
					m.put(JoinHtmlTable.ID_JOIN_TYPE, JoinTypeField.RIGHT_JOIN);
				}
				m.put(ID_PACKAGE_NAME, ClassNameUtil.getPackageName(t.getClass().getName()));
				m.put(JoinHtmlTable.ID_TABLE_CLASS_NAME, t.getClass().getSimpleName());
				m.put(JoinHtmlTable.ID_ALIAS_NAME, t.getAlias());
				m.put(JoinHtmlTable.ID_JOIN_CONDITION, jinfo.getGeneratedCondition());
				logger.debug("generatedCondition=" + jinfo.getGeneratedCondition());
				ret.add(m);
			}
		}
		return ret;
	}

	/**
	 * 問合せのインスタンスを取得します。
	 * @param packageName パッケージ名。
	 * @param queryClassName 問合せクラス名。
	 * @return 問合せインスタンス。
	 * @throws Exception 例外。
	 */
	private Query getQueryInstance(final String packageName, final String queryClassName) throws Exception {
		logger.debug("packageName=" + packageName + ",queryClassName=" + queryClassName);
		Query q = null;
		try {
			@SuppressWarnings("unchecked")
			Class<? extends Query> clazz  = (Class<? extends Query>) Class.forName(packageName + "." + queryClassName);
			q = clazz.getDeclaredConstructor().newInstance();
			q.buildJoinInfoList();
			Dao dao = new Dao(this);
			dao.getSqlGenerator().generateQuerySql(q);
		} catch (ClassNotFoundException e) {
			logger.error(e.getMessage(), e);
		}
		return q;
	}

	/**
	 * Queryクラスの各種フラグを取得する。
	 * @param q 問い合わせクラス。
	 * @param ret フラグを設定するマップ。
	 */
	private void setFlags(final Query q, final Map<String, Object> ret) {
		Class<?> cls = q.getClass();
		String entityClassName = cls.getName() + "$Entity";
		try {
			Class.forName(entityClassName);
			ret.put(ID_NOT_GENERATE_ENTITY, "0");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			ret.put(ID_NOT_GENERATE_ENTITY, "1");
		}
	}

	@Override
	protected Map<String, Object> queryData(final Map<String, Object> data) throws Exception {
		String packageName = (String) data.get(ID_PACKAGE_NAME);
		String queryClassName = (String) data.get(ID_QUERY_CLASS_NAME);
		Query q = this.getQueryInstance(packageName, queryClassName);
		Map<String, Object> ret = new HashMap<String, Object>();
		this.setFlags(q, ret);

		logger.debug("flags=" + JSON.encode(ret));

		ret.put(ID_JAVA_SOURCE_PATH, DeveloperPage.getJavaSourcePath());
		ret.put(ID_PACKAGE_NAME, packageName);
		ret.put(ID_QUERY_CLASS_NAME, queryClassName);
		if (q.isDistinct()) {
			ret.put(ID_DISTINCT_FLAG, "1");
		} else {
			ret.put(ID_DISTINCT_FLAG, "0");
		}
		ret.put(ID_QUERY_COMMENT, q.getComment());
//		ret.put(ID_FORCE_OVERWRITE, "0");
		ret.put(ID_OVERWRITE_MODE, OverwriteModeField.ERROR);

		ret.put(ID_MAIN_TABLE_PACKAGE_NAME, q.getMainTable().getClass().getPackage().getName());
		ret.put(ID_MAIN_TABLE_CLASS_NAME, q.getMainTable().getClass().getSimpleName());
		ret.put(ID_ALIAS_NAME, q.getMainTable().getAlias());
		ret.put(ID_JOIN_TABLE_LIST, this.getJoinTableData(q.getJoinInfoList()));
		List<Map<String, Object>> flist = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> sflist = new ArrayList<Map<String, Object>>();
		flist.addAll(this.queryTableFieldList(ret));
		FieldList qfl = q.getFieldList();
		List<Map<String, Object>> selflist = new ArrayList<Map<String, Object>>();
		for (Field<?> f: qfl) {
			if (f instanceof SqlField) {
				SqlField sf = (SqlField) f;
				Field<?> tf = sf.getTargetField();
				Map<String, Object> sfmap = new HashMap<String, Object>();
				sfmap.put(SqlFieldHtmlTable.ID_FIELD_ID, tf.getId());
				sfmap.put(SqlFieldHtmlTable.ID_FIELD_CLASS_NAME, tf.getClass().getName());
				if (tf.hasLengthParameter()) {
					sfmap.put(SqlFieldHtmlTable.ID_FIELD_LENGTH, tf.getLengthParameter());
				}
				sfmap.put(SqlFieldHtmlTable.ID_SQL, sf.getSql());
				sfmap.put(SqlFieldHtmlTable.ID_COMMENT, sf.getComment());
				sflist.add(sfmap);
			} else {
				for (Map<String, Object> m: flist) {
					String fieldId = (String) m.get(SelectFieldHtmlTable.ID_FIELD_ID);
					logger.debug("fieldId=" + fieldId + ", f=" + f.getClass().getName());
					if (f instanceof AliasField) {
						AliasField af = (AliasField) f;
						String id = af.getTargetField().getId();
						if (id.equals(fieldId)) {
							Map<String, Object> fmap = new HashMap<String, Object>();
							fmap.putAll(m);
							fmap.put(SelectFieldHtmlTable.ID_ALIAS, af.getId());
							fmap.put(SelectFieldHtmlTable.ID_SEL, "1");
							fmap.put(SqlFieldHtmlTable.ID_COMMENT, f.getComment());
							selflist.add(fmap);
							break;
						}
					} else if (f instanceof GroupSummaryField) {
						GroupSummaryField<?> sf = (GroupSummaryField<?>) f;
						String id = sf.getTargetField().getId();
						if (id.equals(fieldId)) {
							Map<String, Object> fmap = new HashMap<String, Object>();
							fmap.putAll(m);
							if (!sf.getId().equals(sf.getTargetField().getId())) {
								fmap.put(SelectFieldHtmlTable.ID_ALIAS, sf.getId());
							}
							fmap.put(SelectFieldHtmlTable.ID_SEL, sf.getClass().getName());
							fmap.put(SqlFieldHtmlTable.ID_COMMENT, f.getComment());
							selflist.add(fmap);
							break;
						}
					} else {
						String id = f.getId();
						if (id.equals(fieldId)) {
							Map<String, Object> fmap = new HashMap<String, Object>();
							fmap.putAll(m);
							fmap.put(SelectFieldHtmlTable.ID_SEL, "1");
							fmap.put(SqlFieldHtmlTable.ID_COMMENT, f.getComment());
							selflist.add(fmap);
							flist.remove(m);
							break;
						}
					}
				}
			}
		}
		for (Map<String, Object> m: flist) {
			m.put(SelectFieldHtmlTable.ID_SEL, "0");
			selflist.add(m);
		}
		ret.put(ID_SELECT_FIELD_LIST, selflist);
		ret.put(ID_SQL_FIELD_LIST, sflist);


		return ret;
	}


	/**
	 * クラスの存在チェック。
	 * @param name クラス名。
	 * @return 存在する場合true。
	 */
	private boolean classExists(final String name) {
		try {
			Class.forName(name);
			return true;
		} catch (ClassNotFoundException e) {
			return false;
		}
	}

	/**
	 * 各種JOINテーブルのバリデーションを行います。
	 * @param id テーブルID。
	 * @param list テーブルの入力内容リスト。
	 * @return バリデーション結果。
	 */
	private List<ValidationError> validateJoinTable(final String id, final List<Map<String, Object>> list) {
		List<ValidationError> ret = new ArrayList<ValidationError>();
		for (int i = 0; i < list.size(); i++) {
			Map<String, Object> ent = list.get(i);
			String packageName = (String) ent.get(ID_PACKAGE_NAME);
			String className = (String) ent.get(JoinHtmlTable.ID_TABLE_CLASS_NAME);
			String tClass = packageName + "." + className;
			if (!this.classExists(tClass)) {
				ret.add(new ValidationError(id + "[" + i + "].tableClassName", MessagesUtil.getMessage(this.getPage(), "error.tableclassnotfound", "{0}", tClass)));
			}
		}
		return ret;
	}


	/**
	 * フィールドIDの多重登録チェックを行います。
	 * @param data フォームデータ。
	 * @return チェック結果。
	 */
	private List<ValidationError> validateFieldId(final Map<String, Object> data) {
		Set<String> set = new HashSet<String>();
		List<ValidationError> ret = new ArrayList<ValidationError>();
		{
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> sellist = (List<Map<String, Object>>) data.get(ID_SELECT_FIELD_LIST);
			for (int i = 0; i < sellist.size(); i++) {
				Map<String, Object> m = sellist.get(i);
				String sel = (String) m.get(SelectFieldHtmlTable.ID_SEL);
				if (!StringUtil.isBlank(sel)) {
					if ((!"0".equals(sel))) {
						String fieldId = (String) m.get(SelectFieldHtmlTable.ID_FIELD_ID);
						if (!StringUtil.isBlank(m.get(SelectFieldHtmlTable.ID_ALIAS))) {
							fieldId = (String) m.get(SelectFieldHtmlTable.ID_ALIAS);
						}
						if (!set.contains(fieldId)) {
							set.add(fieldId);
						} else {
							String fid = ID_SELECT_FIELD_LIST + "[" + i + "].fieldId";
							ret.add(new ValidationError(fid, MessagesUtil.getMessage(this.getPage(), "error.duplicatefieldid", fieldId)));
						}
					}
				}
			}
		}
		{
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> sqllist = (List<Map<String, Object>>) data.get(ID_SQL_FIELD_LIST);
			for (int i = 0; i < sqllist.size(); i++) {
				Map<String, Object> m = sqllist.get(i);
				String fieldId = (String) m.get(SqlFieldHtmlTable.ID_FIELD_ID);
				if (!set.contains(fieldId)) {
					set.add(fieldId);
				} else {
					String fid = ID_SQL_FIELD_LIST + "[" + i + "].fieldId";
					ret.add(new ValidationError(fid, MessagesUtil.getMessage(this.getPage(), "error.duplicatefieldid", fieldId)));
				}
			}
		}
		return ret;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected List<ValidationError> validateForm(final Map<String, Object> data) throws Exception {
		List<ValidationError> ret = new ArrayList<ValidationError>();
		String mtClass = (String) data.get(ID_MAIN_TABLE_PACKAGE_NAME) + "." + (String) data.get(ID_MAIN_TABLE_CLASS_NAME);
		if (!this.classExists(mtClass)) {
			ret.add(new ValidationError(ID_MAIN_TABLE_CLASS_NAME, MessagesUtil.getMessage(this.getPage(), "error.tableclassnotfound", "{0}", mtClass)));
		}
		ret.addAll(this.validateJoinTable(ID_JOIN_TABLE_LIST, (List<Map<String, Object>>) data.get(ID_JOIN_TABLE_LIST)));
		ret.addAll(this.validateFieldId(data));
		// 問合せソースファイルの存在チェック
		String packageName = (String) data.get(ID_PACKAGE_NAME);
		String queryClassName = (String) data.get(ID_QUERY_CLASS_NAME);
		String javaSrc = (String) data.get(ID_JAVA_SOURCE_PATH);
		String srcPath = javaSrc + "/" + packageName.replaceAll("\\.", "/");
		String query = srcPath + "/" + queryClassName + ".java";
		String forceOverwrite = (String) data.get(ID_OVERWRITE_MODE);
		if (OverwriteModeField.ERROR.equals(forceOverwrite)) {
			File f = new File(query);
			if (f.exists()) {
				ret.add(new ValidationError(ID_QUERY_CLASS_NAME, this.getPage().getMessage("error.sourcefileexist", queryClassName + ".java")));
			}
		}
		return ret;
	}

	/**
	 * 主テーブル中のフィールドリストを取得します。
	 * @param tableClass 主テーブルクラス名。
	 * @param alias 別名。
	 * @return フィールドリスト。
	 * @throws Exception 例外。
	 */
	private List<Map<String, Object>> queryTableFieldList(final String tableClass, final String alias) throws Exception {
		@SuppressWarnings("unchecked")
		Class<? extends Table> clazz = (Class<? extends Table>) Class.forName(tableClass);
		Table table = clazz.getDeclaredConstructor().newInstance();
		FieldList flist = table.getFieldList();
		List<Map<String, Object>> ret = SelectFieldHtmlTable.getTableData(flist, alias);
		return ret;

	}

	/**
	 * JOINテーブルのフィールドリストを取得します。
	 * @param list JOINテーブルリスト。
	 * @return フィールドリスト。
	 * @throws Exception 例外。
	 */
	private List<Map<String, Object>> queryJoinTableFieldList(final List<Map<String, Object>> list) throws Exception {
		List<Map<String, Object>> ret = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < list.size(); i++) {
			Map<String, Object> ent = list.get(i);
			String packageName = (String) ent.get(ID_PACKAGE_NAME);
			String className = (String) ent.get(JoinHtmlTable.ID_TABLE_CLASS_NAME);
			String alias = (String) ent.get(JoinHtmlTable.ID_ALIAS_NAME);
			String tClass = packageName + "." + className;
			ret.addAll(this.queryTableFieldList(tClass, alias));
		}
		return ret;
	}


	/**
	 * 問合せ対象のフィールドリストを取得します。
	 * @param data フォームデータ。
	 * @return 問合せ対象のフィールドリスト
	 * @throws Exception 例外。
	 */
	@SuppressWarnings("unchecked")
	private List<Map<String, Object>> queryTableFieldList(final Map<String, Object> data) throws Exception {
		List<Map<String, Object>> ret = new ArrayList<Map<String, Object>>();
		String mtClass = (String) data.get(ID_MAIN_TABLE_PACKAGE_NAME) + "." + (String) data.get(ID_MAIN_TABLE_CLASS_NAME);
		logger.debug("mainTable=" + mtClass);
		String mAlias = (String) data.get(ID_ALIAS_NAME);
		ret.addAll(this.queryTableFieldList(mtClass, mAlias));
		ret.addAll(this.queryJoinTableFieldList((List<Map<String, Object>>) data.get(ID_JOIN_TABLE_LIST)));
		return ret;

	}

	/**
	 * テーブルのフィールドリストを取得します。
	 * @param param パラメータ。
	 * @return フィールドリスト。
	 * @throws Exception 例外。
	 */
	@WebMethod
	public JsonResponse getFieldList(final Map<String, Object> param) throws Exception {
		JsonResponse ret = null;
		Map<String, Object> p = new HashMap<String, Object>();
		p.putAll(param);
		p.put(ID_OVERWRITE_MODE, "skip");
		List<ValidationError> vlist = this.validate(p);
		if (vlist.size() == 0) {
			Map<String, Object> data = this.convertToServerData(param);
			List<Map<String, Object>> list = this.queryTableFieldList(data);
			ret = new JsonResponse(JsonResponse.SUCCESS, list);
		} else {
			ret = new JsonResponse(JsonResponse.INVALID, vlist);
		}
		return ret;
	}

	/**
	 * 問合せクラスから結合条件を取得します。
	 * @param query 問合せ。
	 * @param t テーブル。
	 * @return 結合条件。
	 */
	private String getJoinCondition(final Query query, final Table t) {
		String ret = null;
		if (query != null) {
			List<Query.JoinInfo> list =query.getJoinInfoList();
			String className = t.getClass().getName();
			for (Query.JoinInfo ji: list) {
				if (ji.getJoinTable().getClass().getName().equals(className)) {
					ret = ji.getGeneratedCondition();
				}
			}
		}
		return ret;
	}


	/**
	 * 各JOINテーブルの結合条件を取得します。
	 * @param tlist 関連テーブルの全リスト。
	 * @param jlist JOINテーブルリスト。
	 * @param query 問合せクラスのインスタンス。
	 * @return 結合条件。
	 * @throws Exception 例外。
	 */
	private List<Map<String, Object>> queryJoinCondition(final List<Query.JoinInfo> tlist, final TableList jlist, final Query query) throws Exception {
		List<Map<String, Object>> ret = new ArrayList<Map<String, Object>>();
		Table mainTable = tlist.get(0).getJoinTable();
		for (int i = 0; i < jlist.size(); i++) {
			Table t =jlist.get(i);
			String joinCondition = mainTable.getJoinCondition(t, t.getAlias());
			logger.debug("joinConditon=" + joinCondition);
			Map<String, Object> rec = new HashMap<String, Object>();
			if (StringUtil.isBlank(joinCondition)) {
				Dao dao = new Dao(this);
				SqlGenerator gen = dao.getSqlGenerator();
				joinCondition = gen.getJoinConditionOtherThamMainTable(tlist, new Query.JoinInfo(Query.JoinInfo.INNER_JOIN, t, (JoinConditionInterface1) null));
				if (StringUtil.isBlank(joinCondition)) {
					joinCondition = this.getJoinCondition(query, t);
					if (joinCondition == null) {
						joinCondition = MessagesUtil.getMessage(this.getPage(), "message.joinconditionnotfound");
					}
				}
			}
			rec.put(JoinHtmlTable.ID_JOIN_CONDITION, joinCondition);
			ret.add(rec);
		}
		return ret;
	}

	/**
	 * 各種JOINテーブルのリストを取得します。
	 * @param list POSTされたJOINテーブル情報。
	 * @param defaultAlias 	デフォルト別名。
	 * @return 各種JOINテーブルのリスト。
	 * @throws Exception 例外。
	 */
	private TableList getJoinTableList(final List<Map<String, Object>> list, final String defaultAlias) throws Exception {
		TableList ret = new TableList();
		for (int i= 0; i < list.size(); i++) {
			Map<String, Object> m = list.get(i);
			String packageName = (String) m.get(ID_PACKAGE_NAME);
			String className = (String) m.get(JoinHtmlTable.ID_TABLE_CLASS_NAME);
			String alias = (String) m.get(ID_ALIAS_NAME);
			if (StringUtil.isBlank(alias)) {
				alias = defaultAlias + i;
			}
			String tcn = packageName + "." + className;
			@SuppressWarnings("unchecked")
			Class<? extends Table> tc = (Class<? extends Table>) Class.forName(tcn);
			Table jt = tc.getDeclaredConstructor().newInstance();
			jt.setAlias(alias);
			ret.add(jt);
		}
		return ret;
	}

	/**
	 * 関連する全てのテーブルリストを取得します。
	 * <pre>
	 * リストの先頭はmainTableとなります。
	 * </pre>
	 * @param data POSTされたデータ。
	 * @return 関連する全デーブルリスト。
	 * @throws Exception 例外。
	 */
	@SuppressWarnings("unchecked")
	private List<Query.JoinInfo> getTableList(final Map<String, Object> data) throws Exception {
		TableList list = new TableList();
		String packageName = (String) data.get(ID_MAIN_TABLE_PACKAGE_NAME);
		String mainTableClassName = (String) data.get(ID_MAIN_TABLE_CLASS_NAME);
		String aliasName = (String) data.get(ID_ALIAS_NAME);
		if (StringUtil.isBlank(aliasName)) {
			aliasName = "m";
		}
		Class<? extends Table> clazz = (Class<? extends Table>) Class.forName(packageName + "." + mainTableClassName);
		Table mainTable = clazz.getDeclaredConstructor().newInstance();
		mainTable.setAlias(aliasName);
		list.add(mainTable);

		List<Map<String, Object>> join = (List<Map<String, Object>>) data.get(ID_JOIN_TABLE_LIST);
		list.addAll(this.getJoinTableList(join, "j"));
		List<Query.JoinInfo> ret = new ArrayList<Query.JoinInfo>();
		for (Table t: list) {
			ret.add(new Query.JoinInfo(Query.JoinInfo.INNER_JOIN, t, (JoinConditionInterface1) null));
		}
		return ret;
	}

	/**
	 * 各テーブルのJOIN条件式を取得します。
	 * @param data POSTされたデータ。
	 * @return JOIN条件式が設定されたリスト。
	 * @throws Exception 例外。
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Object> queryJoinCondition(final Map<String, Object> data) throws Exception {
		Map<String, Object> ret = new HashMap<String, Object>();
		List<Map<String, Object>> join = (List<Map<String, Object>>) data.get(ID_JOIN_TABLE_LIST);
		if (join != null && join.size() > 0) {
			String packageName = (String) data.get(ID_PACKAGE_NAME);
			String queryClassName = (String) data.get(ID_QUERY_CLASS_NAME);
			Query q = this.getQueryInstance(packageName, queryClassName);
			List<Query.JoinInfo> list = this.getTableList(data);
			ret.put(ID_JOIN_TABLE_LIST, this.queryJoinCondition(list, this.getJoinTableList(join, "j"), q));
		}
		return ret;
	}

	/**
	 * 各テーブルのJOIN条件式を取得します。
	 * @param param POSTされたデータ。
	 * @return JOIN条件式が設定されたリスト。
	 * @throws Exception 例外。
	 */
	@WebMethod
	public JsonResponse getJoinCondition(final Map<String, Object> param) throws Exception {
		JsonResponse ret = null;
		Map<String, Object> p = new HashMap<String, Object>();
		p.putAll(param);
		Map<String, Object> data = this.convertToServerData(param);
		Map<String, Object> cond = this.queryJoinCondition(data);
		ret = new JsonResponse(JsonResponse.SUCCESS, cond);
		return ret;

	}


	@Override
	protected boolean isUpdate(final Map<String, Object> data) throws Exception {
		return false;
	}


	/**
	 * 各JOINリストのインポート文を作成します。
	 * @param queryPackageName テーブルのパッケージ名。
	 * @param list JOINテーブルリスト。
	 * @param tableSet テーブルセット。
	 * @return インポート文。
	 */
	private String generateImportTableList(final String queryPackageName, final List<Map<String, Object>> list, final Set<String> tableSet) {
		StringBuilder sb = new StringBuilder();
		for (Map<String, Object> m:list) {
			String packageName = (String) m.get(ID_PACKAGE_NAME);
			String tableClassName = (String) m.get(JoinHtmlTable.ID_TABLE_CLASS_NAME);
			String fullClassName = packageName + "." + tableClassName;
			if (!queryPackageName.equals(packageName)) {
				if (!tableSet.contains(fullClassName)) {
					sb.append("import " + fullClassName + ";\n");
					tableSet.add(fullClassName);
				}
			}
		}
		return sb.toString();
	}

	/**
	 * テーブルクラスのインポート文を作成。
	 * @param data POSTされたデータ。
	 * @return インポート文。
	 */
	@SuppressWarnings("unchecked")
	private String generateImportTables(final Map<String, Object> data) {
		Set<String> tableSet = new HashSet<String>();
		StringBuilder sb = new StringBuilder();
		String queryPackageName = (String) data.get(ID_PACKAGE_NAME);
		String packageName = (String) data.get(ID_MAIN_TABLE_PACKAGE_NAME);
		String mainTableClassName = (String) data.get(ID_MAIN_TABLE_CLASS_NAME);
		if (!queryPackageName.equals(packageName)) {
			String fullClassName = packageName + "." + mainTableClassName;
			sb.append("import " + fullClassName + ";\n");
			tableSet.add(fullClassName);
		}
		sb.append(this.generateImportTableList(queryPackageName, (List<Map<String, Object>>) data.get(ID_JOIN_TABLE_LIST), tableSet));
		return sb.toString();
	}

	/**
	 * テーブルクラスのインスタンス変数名を取得します。
	 * @param classname クラス名。
	 * @return テーブルクラスのインスタンス変数名。
	 */
	private String getTableVariableName(final String classname) {
		return StringUtil.firstLetterToLowerCase(classname);
	}

	/**
	 * 各JOINリストのインポート文を作成します。
	 * @param list  POSTされたデータ。
	 * @return インポート文。
	 */
	private String generateNewTableList(final List<Map<String, Object>> list) {
		StringBuilder sb = new StringBuilder();
		for (Map<String, Object> m:list) {
			String tableClassName = (String) m.get(JoinHtmlTable.ID_TABLE_CLASS_NAME);
			String propName = (String) m.get(ID_TABLE_PROP);
			sb.append("\t\t" + tableClassName + " " + this.getTableVariableName(propName) + " = new " + tableClassName + "();\n");
			String aliasName = (String) m.get(ID_ALIAS_NAME);
			if (!StringUtil.isBlank(aliasName)) {
				sb.append("\t\t" + this.getTableVariableName(propName) + ".setAlias(\"" + aliasName + "\");\n");
			}
		}
		return sb.toString();
	}

	/**
	 * 問合せに使用するテーブルプロパティ設定ソースを作成します。
	 * @param packageName パッケージ名。
	 * @param tableClassName テーブルクラス名。
	 * @param propName プロパティ名。
	 * @param alias 別名。
	 * @return テーブルプロパティ設定ソース。
	 * @throws Exception 例外。
	 */
	private String getTableProperty(final String packageName, final String tableClassName, final String propName, final String alias) throws Exception {
		String src = "	/**\n" +
					"	 * ${comment}を取得します。\n" +
					"	 * @return ${comment}。\n" +
					"	 */\n" +
					"	public ${className} get${PropName}() {\n" +
					"		return (${className}) this.getTable(${className}.class, \"${alias}\");\n" +
					"	}\n\n";

		Class<?> c = Class.forName(packageName + "." + tableClassName);
		Table table = (Table) c.getConstructor().newInstance();
		String ret = src.replaceAll("\\$\\{className\\}", tableClassName);
		ret = ret.replaceAll("\\$\\{propName\\}", propName);
		ret = ret.replaceAll("\\$\\{PropName\\}", StringUtil.firstLetterToUpperCase(propName));
		ret = ret.replaceAll("\\$\\{comment\\}", (table.getComment() == null ? "" : table.getComment()));
		ret = ret.replaceAll("\\$\\{alias\\}", alias);
		return ret;
	}

	/**
	 * プロパティ名称マップ。
	 */
	private Map<String, String> propertyMap = new HashMap<String, String>();

	/**
	 * テーブルクラスのインポート文を作成。
	 * @param data POSTされたデータ。
	 * @return インポート文。
	 * @throws Exception 例外。
	 */
	@SuppressWarnings("unchecked")
	private String generateProperties(final Map<String, Object> data) throws Exception {
		StringBuilder sb = new StringBuilder();
		{
			String mainTablePackageName = (String) data.get(ID_MAIN_TABLE_PACKAGE_NAME);
			String mainTableClassName = (String) data.get(ID_MAIN_TABLE_CLASS_NAME);
			String alias = (String) data.get(ID_ALIAS_NAME);
			String propName = (String) data.get(ID_TABLE_PROP);
			this.propertyMap.put(mainTableClassName + alias, propName);
			sb.append(this.getTableProperty(mainTablePackageName, mainTableClassName, propName, alias));
		}
		List<Map<String, Object>> list = (List<Map<String, Object>>) data.get(ID_JOIN_TABLE_LIST);
		for (Map<String, Object> m: list) {
			String packageName = (String) m.get(ID_PACKAGE_NAME);
			String tableClassName = (String) m.get(JoinHtmlTable.ID_TABLE_CLASS_NAME);
			String alias = (String) m.get(JoinHtmlTable.ID_ALIAS_NAME);
			String propName = (String) m.get(ID_TABLE_PROP);
			this.propertyMap.put(tableClassName + alias, propName);
			sb.append(this.getTableProperty(packageName, tableClassName, propName, alias));
		}
		return sb.toString();
	}


	/**
	 * テーブルクラスのインポート文を作成。
	 * @param data POSTされたデータ。
	 * @return インポート文。
	 */
	@SuppressWarnings("unchecked")
	private String generateNewTables(final Map<String, Object> data) {
		StringBuilder sb = new StringBuilder();
		String mainTableClassName = (String) data.get(ID_MAIN_TABLE_CLASS_NAME);
		String propName = (String) data.get(ID_TABLE_PROP);
		sb.append("\t\t" + mainTableClassName + " "  + this.getTableVariableName(propName) + " = new " + mainTableClassName + "();\n");
		String aliasName = (String) data.get(ID_ALIAS_NAME);
		if (!StringUtil.isBlank(aliasName)) {
			sb.append("\t\t" + this.getTableVariableName(propName) + ".setAlias(\"" + aliasName + "\");\n");
		}
		sb.append(this.generateNewTableList((List<Map<String, Object>>) data.get(ID_JOIN_TABLE_LIST)));
		return sb.toString();
	}

	/**
	 * フィールド取得メソッドの文字列を作成します。
	 * @param fieldId フィールドID。
	 * @return フィールド取得メソッドの文字列。
	 */
	private String getFieldMethod(final String fieldId) {
		String uFieldId = StringUtil.firstLetterToUpperCase(fieldId);
		return "get" + uFieldId + "Field()";
	}


	private String getCommentCode(final Map<String, Object> m) throws Exception {
		String fieldClassName = (String) m.get("fieldClassName");
		@SuppressWarnings("unchecked")
		Class<? extends Field<?>> cls = (Class<? extends Field<?>>) Class.forName(fieldClassName);
		Field<?> f = Field.newFieldInstance(cls);
		String comment0 = f.getComment();
		String comment = (String) m.get(SelectFieldHtmlTable.ID_COMMENT);
		if (!StringUtil.isBlank(comment0)) {
			if (!comment0.equals(comment)) {
				return ".setComment(\"" + comment + "\")";
			}
		}
		return "";
	}

	/**
	 * フィールドリストの作成します。
	 * @param list 選択フィールドリスト。
	 * @param sb ソースを展開する文字列バッファ。
	 * @param implist インポートリスト。
	 * @return フィールドリスト。
	 * @throws Exception 例外。
	 */
	private List<Map<String, Object>> genSelectFieldList(List<Map<String, Object>> list, StringBuilder sb, final ImportUtil implist) throws Exception {
		logger.debug("this.propertyMap={}", this.propertyMap);
		List<Map<String, Object>> ret = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> m: list) {
			String sel = (String) m.get(SelectFieldHtmlTable.ID_SEL);
			if (sel != null && (!"0".equals(sel))) {
				String fieldClassName = (String) m.get(SelectFieldHtmlTable.ID_FIELD_CLASS_NAME);
				if (sel.length() <= 1) {
					implist.add(fieldClassName);
				}
				ret.add(m);
				if (sb.length() > 0) {
					sb.append("\t\t\t, ");
				} else {
					sb.append("\t\t\t");
				}
				String tableClassName = (String) m.get(SelectFieldHtmlTable.ID_TABLE_CLASS_NAME);
				String tableAlias = (String) m.get(SelectFieldHtmlTable.ID_TABLE_ALIAS);
				String propName = this.propertyMap.get(tableClassName + tableAlias);
				logger.debug("propName={}", propName);
				if (propName == null) {
					propName = tableClassName;
				}
				String alias = (String) m.get(SelectFieldHtmlTable.ID_ALIAS);
				if ("1".equals(sel)) {
					logger.debug("alias=" + alias);
					if (StringUtil.isBlank(alias)) {
						String fieldId = (String) m.get(SelectFieldHtmlTable.ID_FIELD_ID);
						sb.append(this.getTableVariableName(propName) + ".");
						sb.append(this.getFieldMethod(fieldId) + this.getCommentCode(m) + "\n");
					} else {
						String fieldId = (String) m.get(SelectFieldHtmlTable.ID_FIELD_ID);
						implist.add(AliasField.class.getName());
						sb.append("new AliasField(\"" + alias + "\", ");
						sb.append(this.getTableVariableName(propName) + ".");
						sb.append(this.getFieldMethod(fieldId) + this.getCommentCode(m) + ")\n");
					}
				} else {
					String fieldId = (String) m.get(SelectFieldHtmlTable.ID_FIELD_ID);
					if (alias.length() > 0) {
						fieldId = alias;
					}
					Class<?> cls = Class.forName(sel);
					implist.add(cls.getName());
					sb.append("new " + cls.getSimpleName() + "(\"" + fieldId + "\", " + this.getTableVariableName(propName)
						+ "." + this.getFieldMethod((String) m.get(SelectFieldHtmlTable.ID_FIELD_ID)) + ")"
						+ this.getCommentCode(m) + "\n");
				}
			}
		}
		return ret;
	}

	/**
	 * SQLフィールドリストを作成します。
	 * @param sqllist SQLフィールドリスト。
	 * @param sb ソースを展開する文字列バッファ。
	 * @param implist インポートリスト。
	 * @return フィールドリスト。
	 * @throws Exception 例外。
	 */
	private List<Map<String, Object>> genSqlFieldList(List<Map<String, Object>> sqllist, StringBuilder sb, final ImportUtil implist) throws Exception {
		for (Map<String, Object> m: sqllist) {
			implist.add(SqlField.class.getName());
			if (sb.length() > 0) {
				sb.append("\t\t\t, ");
			} else {
				sb.append("\t\t\t");
			}
			String fieldId = (String) m.get(SqlFieldHtmlTable.ID_FIELD_ID);
			String fieldClassName = (String) m.get(SqlFieldHtmlTable.ID_FIELD_CLASS_NAME);
			implist.add(fieldClassName);
			@SuppressWarnings("unchecked")
			Class<? extends Field<?>> cls = (Class<? extends Field<?>>) Class.forName(fieldClassName);
			String fieldLength = (String) m.get(SqlFieldHtmlTable.ID_FIELD_LENGTH);
			String sql = (String) m.get(SqlFieldHtmlTable.ID_SQL);
			String len = "";
			if (!StringUtil.isBlank(fieldLength)) {
				len = "," + fieldLength;
			}
			String comment = (String) m.get(SqlFieldHtmlTable.ID_COMMENT);
			sb.append("new SqlField(new " + cls.getSimpleName() + "(\"" + fieldId + "\"" + len + "), \"" + sql + "\")"
					+ (StringUtil.isBlank(comment) ? "" : (".setComment(\"" + comment + "\")"))
					+ "\n");
		}
		return sqllist;
	}

	/**
	 * 選択フィールド設定ソースを生成します。
	 * @param data POSTされたデータ。
	 * @param implist インポート必要なクラス。
	 * @param fieldList フィールドリスト。
	 * @return 選択フィールド設定ソース。
	 * @throws Exception 例外。
	 */
	private String generateSelectFieldList(final Map<String, Object> data, final ImportUtil implist, final List<Map<String, Object>> fieldList) throws Exception {
		StringBuilder sb = new StringBuilder();
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> sellist = (List<Map<String, Object>>) data.get(ID_SELECT_FIELD_LIST);
		fieldList.addAll(this.genSelectFieldList(sellist, sb, implist));
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> sqllist = (List<Map<String, Object>>) data.get(ID_SQL_FIELD_LIST);
		fieldList.addAll(this.genSqlFieldList(sqllist, sb, implist));
		return "\t\tthis.setFieldList(new FieldList(\n" + sb.toString() + "\t\t));";
	}


	/**
	 * JOINの設定ソースを生成します。
	 * @param data POSTされたデータ。
	 * @return JOINの設定ソース。
	 */
	@SuppressWarnings("unchecked")
	private String generateJoinTables(final Map<String, Object> data) {
		StringBuilder sb = new StringBuilder();
		List<Map<String, Object>> list = (List<Map<String, Object>>) data.get(ID_JOIN_TABLE_LIST);
		for (Map<String, Object> m: list) {
			String joinType = (String) m.get(JoinHtmlTable.ID_JOIN_TYPE);
			String tableName = this.getTableVariableName((String) m.get(ID_TABLE_PROP));
			if (JoinTypeField.INNER_JOIN.equals(joinType)) {
				sb.append("\t\tthis.addInnerJoin(" + tableName + ");\n");
			} else if (JoinTypeField.LEFT_JOIN.equals(joinType)) {
				sb.append("\t\tthis.addLeftJoin(" + tableName + ");\n");
			} else if (JoinTypeField.RIGHT_JOIN.equals(joinType)) {
				sb.append("\t\tthis.addRightJoin(" + tableName + ");\n");
			}
		}
		return sb.toString();
	}

	/**
	 * テーブルプロパティを生成する。
	 * @param data POSTされたデータ。
	 */
	private void setTableProperties(final Map<String, Object> data) {
		HashMap<String, Integer> classCount = new HashMap<String, Integer>();
		{
			String mainTablePackageName = (String) data.get(ID_MAIN_TABLE_PACKAGE_NAME);
			String mainTableClassName = (String) data.get(ID_MAIN_TABLE_CLASS_NAME);
			String cls = mainTablePackageName + "." + mainTableClassName;
			Integer cnt = (Integer) classCount.get(cls);
			if (cnt == null) {
				classCount.put(cls, Integer.valueOf(1));
			} else {
				cnt = cnt.intValue() + 1;
				classCount.put(cls, cnt);
			}
			data.put(ID_TABLE_PROP, this.getTableVariableName(mainTableClassName));
		}

		@SuppressWarnings("unchecked")
		List<Map<String, Object>> list = (List<Map<String, Object>>) data.get(ID_JOIN_TABLE_LIST);
		for (Map<String, Object> m: list) {
			String packageName = (String) m.get(ID_PACKAGE_NAME);
			String tableClassName = (String) m.get(JoinHtmlTable.ID_TABLE_CLASS_NAME);
			String cls = packageName + "." + tableClassName;
			Integer cnt = (Integer) classCount.get(cls);
			if (cnt == null) {
				classCount.put(cls, Integer.valueOf(1));
			} else {
				cnt = cnt.intValue() + 1;
				classCount.put(cls, cnt);
			}
		}
		logger.debug("cset=" + JSON.encode(classCount, true));
		for (Map<String, Object> m: list) {
			String packageName = (String) m.get(ID_PACKAGE_NAME);
			String tableClassName = (String) m.get(JoinHtmlTable.ID_TABLE_CLASS_NAME);
			String cls = packageName + "." + tableClassName;
			logger.debug("cls=" + cls);
			Integer cnt = (Integer) classCount.get(cls);
			if (cnt > 1) {
				String aliasName = (String) m.get(ID_ALIAS_NAME);
				if (aliasName != null) {
					aliasName = StringUtil.firstLetterToUpperCase(aliasName);
				}
				m.put(ID_TABLE_PROP, this.getTableVariableName(tableClassName + aliasName));
			} else {
				m.put(ID_TABLE_PROP, this.getTableVariableName(tableClassName));
			}
		}
	}

	/**
	 * フィールドIDを取得します。
	 * @param m フィールドリスト。
	 * @return フィールドID。
	 */
	private String getFieldId(final Map<String, Object> m) {
		String ret = (String) m.get(SelectFieldHtmlTable.ID_FIELD_ID);
		String alias = (String) m.get(SelectFieldHtmlTable.ID_ALIAS);
		if (!StringUtil.isBlank(alias)) {
			ret = alias;
		}
		return ret;
	}


	@Override
	protected void insertData(final Map<String, Object> data) throws Exception {

		String notUpdateConstractor = (String) data.get(ID_NOT_UPDATE_CONSTRACTOR);
		String notGenerateEntity = (String) data.get(ID_NOT_GENERATE_ENTITY);

		String javasrc = this.getStringResourse("template/Query.java.template");
		String packageName = (String) data.get(ID_PACKAGE_NAME);
		String queryClassName = (String) data.get(ID_QUERY_CLASS_NAME);

		String javaSrc = (String) data.get(ID_JAVA_SOURCE_PATH);
		String srcPath = javaSrc + "/" + packageName.replaceAll("\\.", "/");
		String querySrc = srcPath + "/" + queryClassName + ".java";

		this.setTableProperties(data);

		javasrc = javasrc.replaceAll("\\$\\{queryComment\\}", (String) data.get(ID_QUERY_COMMENT));
		javasrc = javasrc.replaceAll("\\$\\{packageName\\}", packageName);
		javasrc = javasrc.replaceAll("\\$\\{queryClassName\\}", queryClassName);
		javasrc = javasrc.replaceAll("\\$\\{importTables\\}", this.generateImportTables(data));
		javasrc = javasrc.replaceAll("\\$\\{properties\\}", this.generateProperties(data));

		ImportUtil implist = new ImportUtil();
		implist.add(Map.class.getName());
		List<Map<String, Object>> fieldList = new ArrayList<Map<String, Object>>();
		logger.debug("notUpdateConstractor=" + notUpdateConstractor);
		String constructor = this.generateConstructor(data, fieldList, implist);
		if ("0".equals(notUpdateConstractor)) {
			javasrc = javasrc.replaceAll("\\$\\{constructor\\}", constructor);
		} else {
			JavaSrc src = new JavaSrc(new File(querySrc));
			javasrc = javasrc.replaceAll("\\$\\{constructor\\}", src.getMethodBody(queryClassName));
		}
		if ("0".equals(notGenerateEntity)) {
			EntityGenerator gen = new EntityGenerator(fieldList);
			String entityClass = gen.generate(
				implist, implist
				, (Map<String, Object> m) -> {
					return this.getFieldId(m);
				}
				, (Map<String, Object> m) -> {
					String fcls =  (String) m.get(SelectFieldHtmlTable.ID_FIELD_CLASS_NAME);
					String sel = (String) m.get(SelectFieldHtmlTable.ID_SEL);
					if ("dataforms.field.sqlfunc.CountField".equals(sel)) {
						fcls = "dataforms.field.sqltype.BigintField";
					}
					logger.debug("fcls=" + fcls);
					return fcls;
				}
			);
			javasrc = javasrc.replaceAll("\\$\\{entity\\}", entityClass);
		} else {
			javasrc = javasrc.replaceAll("\\$\\{entity\\}", "");
		}

		javasrc = javasrc.replaceAll("\\$\\{importList\\}", implist.getImportText(javasrc));

		FileUtil.writeTextFileWithBackup(querySrc, javasrc, DataFormsServlet.getEncoding());
		logger.debug("javasrc=\n" + javasrc);
	}

	/**
	 * コンストラクタを生成します。
	 * @param data データ。
	 * @param fieldList フィールドリスト。
	 * @param implist インポートリスト。
	 * @return コンストラクタ。
	 * @throws Exception 例外。
	 */
	private String generateConstructor(final Map<String, Object> data, final List<Map<String, Object>> fieldList, final ImportUtil implist) throws Exception {
		String javasrc = this.getStringResourse("template/Constructor.java.template");
		javasrc = javasrc.replaceAll("\\$\\{newTables\\}", this.generateNewTables(data));
		javasrc = javasrc.replaceAll("\\$\\{selectFields\\}", this.generateSelectFieldList(data, implist, fieldList));
		String mainTableClassName = (String) data.get(ID_MAIN_TABLE_CLASS_NAME);
		javasrc = javasrc.replaceAll("\\$\\{mainTable\\}", this.getTableVariableName(mainTableClassName));
		String distinctFlag = (String) data.get(ID_DISTINCT_FLAG);
		if ("1".equals(distinctFlag)) {
			javasrc = javasrc.replaceAll("\\$\\{distinctFlag\\}", "true");
		} else {
			javasrc = javasrc.replaceAll("\\$\\{distinctFlag\\}", "false");
		}
		javasrc = javasrc.replaceAll("\\$\\{queryComment\\}", (String) data.get(ID_QUERY_COMMENT));
		javasrc = javasrc.replaceAll("\\$\\{joinTables\\}", this.generateJoinTables(data));
		return javasrc;
	}

	@Override
	protected String getSavedMessage(final Map<String, Object> data) {
		return MessagesUtil.getMessage(this.getPage(), "message.javasourcecreated");
	}

	@Override
	protected void updateData(final Map<String, Object> data) throws Exception {
		// 何もしない
	}

	@Override
	public void deleteData(final Map<String, Object> data) throws Exception {
		//　何もしない。
	}


}
