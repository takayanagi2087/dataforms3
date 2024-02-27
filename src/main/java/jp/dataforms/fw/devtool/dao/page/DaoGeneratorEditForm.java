package jp.dataforms.fw.devtool.dao.page;

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
import jp.dataforms.fw.dao.Query;
import jp.dataforms.fw.dao.QuerySetDao;
import jp.dataforms.fw.dao.SingleTableQuery;
import jp.dataforms.fw.dao.Table;
import jp.dataforms.fw.devtool.base.page.DeveloperPage;
import jp.dataforms.fw.devtool.field.DaoClassNameField;
import jp.dataforms.fw.devtool.field.EditFormTypeField;
import jp.dataforms.fw.devtool.field.FunctionSelectField;
import jp.dataforms.fw.devtool.field.JavaSourcePathField;
import jp.dataforms.fw.devtool.field.OverwriteModeField;
import jp.dataforms.fw.devtool.field.PackageNameField;
import jp.dataforms.fw.devtool.field.QueryOrTableClassNameField;
import jp.dataforms.fw.devtool.query.page.SelectFieldHtmlTable;
import jp.dataforms.fw.field.base.Field;
import jp.dataforms.fw.field.base.FieldList;
import jp.dataforms.fw.field.base.TextField;
import jp.dataforms.fw.htmltable.EditableHtmlTable;
import jp.dataforms.fw.response.JsonResponse;
import jp.dataforms.fw.response.Response;
import jp.dataforms.fw.servlet.DataFormsServlet;
import jp.dataforms.fw.util.ClassNameUtil;
import jp.dataforms.fw.util.FileUtil;
import jp.dataforms.fw.util.ImportUtil;
import jp.dataforms.fw.util.StringUtil;
import jp.dataforms.fw.validator.DisplayedRequiredValidator;
import jp.dataforms.fw.validator.RequiredValidator;
import jp.dataforms.fw.validator.ValidationError;

/**
 * DAO生成編集フォームクラス。
 *
 */
public class DaoGeneratorEditForm extends EditForm {

	/**
	 * Logger。
	 */
	private static Logger logger = LogManager.getLogger(DaoGeneratorEditForm.class);

	/**
	 * 編集フォームタイプ。
	 */
	private static final String ID_EDIT_FORM_TYPE = "editFormType";

	/**
	 * パッケージ名。
	 */
	public static final String ID_PACKAGE_NAME = "packageName";

	/**
	 * Daoクラス名。
	 */
	public static final String ID_DAO_CLASS_NAME = "daoClassName";

	/**
	 * コメント。
	 */
	private static final String ID_COMMENT = "comment";

	/**
	 * 上書きモードフィールドID。
	 */
	public static final String ID_OVERWRITE_MODE = "overwriteMode";
	/**
	 * 一覧問合せの機能選択フィールドID。
	 */
	public static final String ID_LIST_QUERY_FUNCTION_SELECT = "listQueryFunctionSelect";
	/**
	 * 一覧問合せクラス名のフィールドID。
	 */
	public static final String ID_LIST_QUERY_CLASS_NAME = "listQueryClassName";
	/**
	 * 一覧問合せパッケージ名のフィールドID。
	 */
	public static final String ID_LIST_QUERY_PACKAGE_NAME = "listQueryPackageName";
	/**
	 * 編集フォームレコード取得問合せの機能選択フィールドID。
	 */
	public static final String ID_EDIT_FORM_QUERY_FUNCTION_SELECT = "editFormQueryFunctionSelect";
	/**
	 * 編集フォームレコード取得問合せのパッケージ名フィールドID。
	 */
	public static final String ID_EDIT_FORM_QUERY_PACKAGE_NAME = "editFormQueryPackageName";
	/**
	 * 編集フォームレコード取得問合せのクラス名フィールドID。
	 */
	public static final String ID_EDIT_FORM_QUERY_CLASS_NAME = "editFormQueryClassName";
	/**
	 * キーフィールドリストID。
	 */
	public static final String ID_MULTI_RECORD_QUERY_KEY_LIST = "multiRecordQueryKeyList";
	/**
	 * 複数レコード取得問合せリストID。
	 */
	public static final String ID_MULTI_RECORD_QUERY_LIST = "multiRecordQueryList";
	/**
	 * javaソースパスフィールドID。
	 */
	public static final String ID_JAVA_SOURCE_PATH = "javaSourcePath";
	/**
	 * 問合せクラス名のフィールドID。
	 */
	public static final String ID_QUERY_CLASS_NAME = "queryClassName";

	/**
	 * キーフィールドリスト。
	 */
	private static final String ID_KEY_FIELD_LIST = "keyFieldList";


	/**
	 * コンストラクタ。
	 */
	public DaoGeneratorEditForm() {
		this.addField(new JavaSourcePathField());
		this.addField(new EditFormTypeField()).setComment("編集フォーム");
		this.addField(new OverwriteModeField(ID_OVERWRITE_MODE)).setComment("上書きモード");
		FunctionSelectField funcField = new FunctionSelectField();
		funcField.setPackageOption("dao");
		funcField.setCalcEventField(true);
		this.addField(funcField);
		this.addField(new PackageNameField()).addValidator(new RequiredValidator());
		this.addField(new DaoClassNameField()).addValidator(new RequiredValidator());
		this.addField(new TextField(ID_COMMENT));
		//
		this.addField((new FunctionSelectField(ID_LIST_QUERY_FUNCTION_SELECT)).setPackageFieldId(ID_LIST_QUERY_PACKAGE_NAME).setComment("一覧問合せの機能"));
		this.addField((new PackageNameField(ID_LIST_QUERY_PACKAGE_NAME)).setComment("一覧問合せのパッケージ"));
		this.addField((new QueryOrTableClassNameField(ID_LIST_QUERY_CLASS_NAME))
			.setPackageNameFieldId(ID_LIST_QUERY_PACKAGE_NAME))
			.setAutocomplete(true)
			.setRelationDataAcquisition(true);
		//
		this.addField((new FunctionSelectField(ID_EDIT_FORM_QUERY_FUNCTION_SELECT))
			.setPackageFieldId(ID_EDIT_FORM_QUERY_PACKAGE_NAME)
			.setComment("単一レコード取得用問合せの機能"))
			.addValidator(new DisplayedRequiredValidator());
		this.addField((new PackageNameField(ID_EDIT_FORM_QUERY_PACKAGE_NAME))
			.setComment("単一レコード取得用問合せのパッケージ"))
			.addValidator(new DisplayedRequiredValidator());
		this.addField((new QueryOrTableClassNameField(ID_EDIT_FORM_QUERY_CLASS_NAME))
			.setPackageNameFieldId(ID_EDIT_FORM_QUERY_PACKAGE_NAME))
			.setCalcEventField(true)
			.setAutocomplete(true)
			.setRelationDataAcquisition(true)
			.addValidator(new DisplayedRequiredValidator());
		//
		{
			FieldList flist = new FieldList();
			flist.addField(new FunctionSelectField());
			flist.addField(new PackageNameField());
			flist.addField(new QueryOrTableClassNameField(ID_QUERY_CLASS_NAME))
				.setAutocomplete(true)
				.setRelationDataAcquisition(true);
			EditableHtmlTable list = new EditableHtmlTable(ID_MULTI_RECORD_QUERY_LIST, flist);
			this.addHtmlTable(list);
		}
		{
			SelectFieldHtmlTable fieldList = new SelectFieldHtmlTable(ID_KEY_FIELD_LIST, true);
			this.addHtmlTable(fieldList);
		}

	}

	@Override
	public void init() throws Exception {
		super.init();
		this.setFormData(ID_JAVA_SOURCE_PATH, DeveloperPage.getJavaSourcePath());
		this.setFormData(ID_EDIT_FORM_TYPE, "1");
		this.setFormData(ID_OVERWRITE_MODE, "error");
	}

	@Override
	protected Map<String, Object> queryNewData(final Map<String, Object> data) throws Exception {
		Map<String, Object> ret = super.queryNewData(data);
		ret.put(ID_EDIT_FORM_TYPE, "1");
		ret.put(ID_OVERWRITE_MODE, "error");
		return ret;
	}

	/**
	 * 指定されたaueryがSingleTableQueryの場合、mainTableを返します。
	 * @param query 問合せ。
	 * @return mainTable。
	 */
	private Object getQueryOrTableClass(final Query query) {
		if (query instanceof SingleTableQuery) {
			return query.getMainTable();
		} else {
			return query;
		}
	}

	@Override
	protected Map<String, Object> queryData(final Map<String, Object> data) throws Exception {
		Map<String, Object> ret = new HashMap<String, Object>();
		String pkgName = (String) data.get(ID_PACKAGE_NAME);
		String className = (String) data.get(ID_DAO_CLASS_NAME);
		String daoClassName = pkgName + "." + className;
		logger.debug(() -> "DAO class name=" + daoClassName);
		Class<?> daoclass = Class.forName(daoClassName);
		QuerySetDao dao = (QuerySetDao) daoclass.getConstructor().newInstance();

		ret.put(ID_JAVA_SOURCE_PATH, DeveloperPage.getJavaSourcePath());
		ret.put(ID_OVERWRITE_MODE, "error");
		ret.put(ID_PACKAGE_NAME, ClassNameUtil.getPackageName(daoclass.getName()));
		ret.put(ID_DAO_CLASS_NAME, daoclass.getSimpleName());
		ret.put(ID_COMMENT, dao.getComment());
		if (dao.getListQuery() != null) {
			Object obj = this.getQueryOrTableClass(dao.getListQuery());
			ret.put(ID_LIST_QUERY_PACKAGE_NAME, ClassNameUtil.getPackageName(obj.getClass().getName()));
			ret.put(ID_LIST_QUERY_CLASS_NAME, obj.getClass().getSimpleName());
		}

		if (dao.getSingleRecordQuery() == null && dao.getMultiRecordQueryList() == null) {
			ret.put(ID_EDIT_FORM_TYPE, "0");
		} else if (dao.getSingleRecordQuery() != null) {
			ret.put(ID_EDIT_FORM_TYPE, "1");
			{
				Object obj = this.getQueryOrTableClass(dao.getSingleRecordQuery());
				ret.put(ID_EDIT_FORM_QUERY_PACKAGE_NAME, ClassNameUtil.getPackageName(obj.getClass().getName()));
				ret.put(ID_EDIT_FORM_QUERY_CLASS_NAME, obj.getClass().getSimpleName());
			}
			List<Query> qlist = dao.getMultiRecordQueryList();
			if (qlist != null) {
				List<Map<String, Object>> mqlist = new ArrayList<Map<String, Object>>();
				for (Query q: qlist) {
					Object obj = this.getQueryOrTableClass(q);
					Map<String, Object> m = new HashMap<String, Object>();
					m.put(ID_PACKAGE_NAME, ClassNameUtil.getPackageName(obj.getClass().getName()));
					m.put(ID_QUERY_CLASS_NAME, obj.getClass().getSimpleName());
					mqlist.add(m);
				}
				ret.put(ID_MULTI_RECORD_QUERY_LIST, mqlist);
			}
		} else {
			ret.put(ID_EDIT_FORM_TYPE, "2");
			List<Query> qlist = dao.getMultiRecordQueryList();
			if (qlist != null && qlist.size() > 0) {
				Query q = qlist.get(0);
				Object obj = this.getQueryOrTableClass(q);
				ret.put(ID_EDIT_FORM_QUERY_PACKAGE_NAME, ClassNameUtil.getPackageName(obj.getClass().getName()));
				ret.put(ID_EDIT_FORM_QUERY_CLASS_NAME, obj.getClass().getSimpleName());
				List<Map<String, Object>> list = SelectFieldHtmlTable.getTableData(q.getFieldList(), "");
				list = SelectFieldHtmlTable.selectKey(list, dao.getMultiRecordQueryKeyList());
				ret.put(ID_KEY_FIELD_LIST, list);
			}
		}
		return ret;
	}

	/**
	 * 編集対象を限定するキー項目のリストを取得します。
	 * @param p パラメータ。
	 * @return フィールドリストのJsonResponse。
	 * @throws Exception 例外。
	 */
	@WebMethod
	public Response getKeyList(final Map<String, Object> p) throws Exception {
		Map<String, Object> data = this.convertToServerData(p);
		String packageName = (String) data.get(ID_EDIT_FORM_QUERY_PACKAGE_NAME);
		String className = (String) data.get(ID_EDIT_FORM_QUERY_CLASS_NAME);
		FieldList flist = new FieldList();
		if (!StringUtil.isBlank(className)) {
			flist = this.getFieldList(packageName + "." + className);
		}
		List<Map<String, Object>> list = SelectFieldHtmlTable.getTableData(flist, "");
		Response resp = new JsonResponse(JsonResponse.SUCCESS, list);
		return resp;
	}


	@Override
	protected List<ValidationError> validateForm(Map<String, Object> data) throws Exception {
		List<ValidationError> list = super.validateForm(data);
		if (list.size() == 0) {
			String path = (String) data.get(ID_JAVA_SOURCE_PATH);
			String packageName = (String) data.get(ID_PACKAGE_NAME);
			String daoClassName = (String) data.get(ID_DAO_CLASS_NAME);
			String daoclass = packageName + "." + daoClassName;
			String srcPath = path + "/" + daoclass.replaceAll("\\.", "/") + ".java";
			String overwriteMode = (String) data.get("overwriteMode");
			if (OverwriteModeField.ERROR.equals(overwriteMode)) {
				File tbl = new File(srcPath);
				if (tbl.exists()) {
					list.add(new ValidationError(ID_DAO_CLASS_NAME, this.getPage().getMessage("error.sourcefileexist", daoClassName + ".java")));
				}
			}
		}
		return list;
	}

	@Override
	protected boolean isUpdate(Map<String, Object> data) throws Exception {
		return false;
	}



	/**
	 * 問合せに使用するテーブルプロパティ設定ソースを作成します。
	 * @param packageName パッケージ名。
	 * @param tableClassName テーブルクラス名。
	 * @return テーブルプロパティ設定ソース。
	 * @throws Exception 例外。
	 */
	private String getProperty(final String packageName, final String tableClassName) throws Exception {
		String src = "	/**\n" +
				"	 * ${comment}。\n" +
				"	 */\n" +
				"	private ${className} ${variableName} = null;\n\n" +
				"	/**\n" +
				"	 * ${comment}を取得します。\n" +
				"	 * @return ${comment}。\n" +
				"	 */\n" +
				"	public ${className} get${className}() {\n" +
				"		return this.${variableName};\n" +
				"	}\n\n";
		Class<?> c = Class.forName(packageName + "." + tableClassName);
		Object obj = (Object) c.getConstructor().newInstance();
		String comment = "";
		if (obj instanceof Table) {
			Table table = (Table) obj;
			comment = table.getComment();
		} else if (obj instanceof Query){
			Query query = (Query) obj;
			comment = query.getComment();
		}
		String ret = src.replaceAll("\\$\\{className\\}", tableClassName);
		ret = ret.replaceAll("\\$\\{variableName\\}", StringUtil.firstLetterToLowerCase(tableClassName));
		ret = ret.replaceAll("\\$\\{comment\\}", comment);
		return ret;

	}

	/**
	 * フィールドのプロパティを作成します。
	 * @param id フィールドID。
	 * @param fieldClassName フィールドクラス名。
	 * @return プロパティのソースコード。
	 * @throws Exception 例外。
	 */
	private String getFieldProperty(final String id, final String fieldClassName) throws Exception {
		String src = "	/**\n" +
				"	 * ${comment}。\n" +
				"	 */\n" +
				"	private ${className} ${variableName} = null;\n\n" +
				"	/**\n" +
				"	 * ${comment}を取得します。\n" +
				"	 * @return ${comment}。\n" +
				"	 */\n" +
				"	public ${className} ${getterName}() {\n" +
				"		return this.${variableName};\n" +
				"	}\n\n";
		@SuppressWarnings("unchecked")
		Class<? extends Field<?>> cls = (Class<? extends Field<?>>) Class.forName(fieldClassName);
		Field<?> field = Field.newFieldInstance(cls);
		String comment = field.getComment() + "フィールド";
		String ret = src.replaceAll("\\$\\{className\\}", cls.getSimpleName());
		ret = ret.replaceAll("\\$\\{variableName\\}", id + "Field");
		ret = ret.replaceAll("\\$\\{getterName\\}", "get" + StringUtil.firstLetterToUpperCase(id) + "Field");
		ret = ret.replaceAll("\\$\\{comment\\}", comment);
		return ret;

	}

	/**
	 * テーブルまたは問合せのプロパティコードを作成します。
	 * @param data データ。
	 * @param implist インポートリスト。
	 * @return プロパティのソース。
	 * @throws Exception 例外。
	 */
	private String getProperties(final Map<String, Object> data, final ImportUtil implist) throws Exception {
		Set<String> set = new HashSet<String>();
		StringBuilder sb = new StringBuilder();
		{
			String packageName = (String) data.get(ID_LIST_QUERY_PACKAGE_NAME);
			String className = (String) data.get(ID_LIST_QUERY_CLASS_NAME);
			String fullClassName = packageName + "." + className;
			if (!StringUtil.isBlank(className)) {
				if (!set.contains(fullClassName)) {
					sb.append(this.getProperty(packageName, className));
					set.add(fullClassName);
				}
			}
		}
		{
			String packageName = (String) data.get(ID_EDIT_FORM_QUERY_PACKAGE_NAME);
			String className = (String) data.get(ID_EDIT_FORM_QUERY_CLASS_NAME);
			String fullClassName = packageName + "." + className;
			if (!StringUtil.isBlank(className)) {
				if (!set.contains(fullClassName)) {
					sb.append(this.getProperty(packageName, className));
					set.add(fullClassName);
				}
				@SuppressWarnings("unchecked")
				Class<? extends Query> qcls = (Class<? extends Query>) Class.forName(fullClassName);
				Object obj = qcls.getConstructor().newInstance();
				if (obj instanceof Query) {
					Query query = (Query) obj;
					Table table = query.getMainTable();
					String tableClassName = table.getClass().getName();
					logger.debug("tableClassName=" + tableClassName);
					if (!set.contains(tableClassName)) {
						implist.add(tableClassName);
						set.add(tableClassName);
					}
				}
			}
		}
		{
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> list = (List<Map<String, Object>>) data.get(ID_MULTI_RECORD_QUERY_LIST);
			for (Map<String, Object> m: list) {
				String packageName = (String) m.get(ID_PACKAGE_NAME);
				String className = (String) m.get(ID_QUERY_CLASS_NAME);
				String fullClassName = packageName + "." + className;
				if (!StringUtil.isBlank(className)) {
					if (!set.contains(fullClassName)) {
						sb.append(this.getProperty(packageName, className));
						set.add(fullClassName);
					}
				}
			}
		}
		{
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> list = (List<Map<String, Object>>) data.get(ID_KEY_FIELD_LIST);
			for (Map<String, Object> m: list) {
				String sel = (String) m.get("sel");
				if ("1".equals(sel)) {
					String fieldId = (String) m.get("fieldId");
					String fullClassName = (String) m.get("fieldClassName");
					int idx = fullClassName.lastIndexOf(".");
					if (idx >= 0) {
						if (!set.contains(fullClassName)) {
							sb.append(this.getFieldProperty(fieldId, fullClassName));
							set.add(fullClassName);
							implist.add(fullClassName);
							// implist.add(fullClassName);
						}
					}
				}
			}
		}
		return sb.toString();
	}

	@Override
	protected void insertData(final Map<String, Object> data) throws Exception {
		ImportUtil implist = new ImportUtil();
		String javasrc = this.getStringResourse("template/QuerySetDao.java.template");
		//logger.debug("template=" + javasrc);
		String packageName = (String) data.get(ID_PACKAGE_NAME);
		String daoClassName = (String) data.get(ID_DAO_CLASS_NAME);
		javasrc = javasrc.replaceAll("\\$\\{packageName\\}", packageName);
		javasrc = javasrc.replaceAll("\\$\\{daoClassName\\}", daoClassName);
		javasrc = javasrc.replaceAll("\\$\\{properties\\}", this.getProperties(data, implist));

		String daoclass = packageName + "." + daoClassName;
		String comment = (String) data.get(ID_COMMENT);
		javasrc = javasrc.replaceAll("\\$\\{comment\\}", comment);
		{
			String queryPackage = (String) data.get("listQueryPackageName");
			String queryClass = (String) data.get("listQueryClassName");
			if (!StringUtil.isBlank(queryClass)) {
				if (!packageName.equals(queryPackage)) {
					String qc = queryPackage + "." + queryClass;
					implist.add(qc);
				}
				javasrc = javasrc.replaceAll("\\$\\{listQuery\\}", "this." + StringUtil.firstLetterToLowerCase(queryClass) + " = new " + queryClass + "()");
			} else {
				implist.add(Query.class.getName());
				javasrc = javasrc.replaceAll("\\$\\{listQuery\\}", "(Query) null");
			}
		}
		String editFormType = (String) data.get(ID_EDIT_FORM_TYPE);
		if ("0".equals(editFormType)) {
			javasrc = this.noEditForm(javasrc, implist);
		} else if ("1".equals(editFormType)) {
			javasrc = this.singleRecordEditForm(data, implist, javasrc);
		} else {
			javasrc = this.multiRecordEditForm(data, implist, javasrc);
		}
		javasrc = javasrc.replaceAll("\\$\\{importTables\\}", implist.getImportText());
		logger.debug("javasrc={}", javasrc);

		String path = (String) data.get(ID_JAVA_SOURCE_PATH);
		String srcPath = path + "/" + daoclass.replaceAll("\\.", "/") + ".java";
		FileUtil.writeTextFileWithBackup(srcPath, javasrc, DataFormsServlet.getEncoding());

	}

	/**
	 * 選択フィールドリストのソースを作成します。
	 * @param data フォームデータ。
	 * @param implist インポートリスト。
	 * @param packagename パッケージ名。
	 * @param classname クラス名。
	 * @return 選択フィールドリストのソース。
	 * @throws Exception 例外。
	 */
	private String getKeyListSource(final Map<String, Object> data, final ImportUtil implist, final String packagename, final String classname) throws Exception {
		int cnt = 0;
		StringBuilder sb = new StringBuilder();
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> list = (List<Map<String, Object>>) data.get(ID_KEY_FIELD_LIST);
		Class<?> cls = Class.forName(packagename + "." + classname);
		if (Table.class.isAssignableFrom(cls)) {
			sb.append("\t\tQuery query = new SingleTableQuery(new " + classname + "());\n");
		} else {
			sb.append("\t\tQuery query = new " + classname + "();\n");
		}
		sb.append("\t\tthis.setMultiRecordQueryKeyList(new FieldList(\n");
		StringBuilder fsb = new StringBuilder();
		for (Map<String, Object> m: list) {
			String sel = (String) m.get("sel");
			if ("1".equals(sel)) {
				if (fsb.length() > 0) {
					fsb.append("\t\t\t, ");
				} else {
					fsb.append("\t\t\t");
				}
				String fieldId = (String) m.get("fieldId");
				String tbl = (String) m.get("tableClassName");
				String fieldClassName = (String) m.get("fieldClassName");
				int idx = fieldClassName.lastIndexOf(".");
				if (idx >= 0) {
					fieldClassName = fieldClassName.substring(idx + 1);
				}
				String fld = "this." + fieldId + "Field = (" + fieldClassName + ") query.getFieldList().get(" + tbl + ".Entity.ID_" + StringUtil.camelToSnake(fieldId).toUpperCase() + ")\n";
				fsb.append(fld);
				cnt++;
			}
		}
		sb.append(fsb.toString());
		sb.append("\n\t\t));\n");
		if (cnt > 0) {
			if (Table.class.isAssignableFrom(cls)) {
				implist.add(SingleTableQuery.class);
			}
			implist.add("dataforms.field.base.FieldList");
			return sb.toString();
		} else {
			return "";
		}
	}

	/**
	 * 複数レコード編集フォーム用DAOソース生成を行います。
	 *
	 * @param data フォームデータ。
	 * @param implist インポートリスト。
	 * @param javasrc javaソーステキスト。
	 * @return javaソーステキスト。
	 * @throws Exception 例外。
	 */
	private String multiRecordEditForm(final Map<String, Object> data, final ImportUtil implist, String javasrc) throws Exception {
		String p = (String) data.get(ID_PACKAGE_NAME);
		String packagename = (String) data.get(ID_EDIT_FORM_QUERY_PACKAGE_NAME);
		String classname = (String) data.get(ID_EDIT_FORM_QUERY_CLASS_NAME);
		String src = "\t\tthis.addMultiRecordQueryList(this." + StringUtil.firstLetterToLowerCase(classname) + " = new " + classname + "());\n"
					+ this.getKeyListSource(data, implist, packagename, classname);
		javasrc = javasrc.replaceAll("\\$\\{addMultiRecordQueryList\\}", src);
		implist.add(Query.class.getName());
		javasrc = javasrc.replaceAll("\\$\\{singleRecordQuery\\}", "(Query) null");
		if (!p.equals(packagename)) {
			implist.add(packagename + "." + classname);
		}

		Table mainTable = this.getMainTable(packagename + "." + classname);
		javasrc = javasrc.replaceAll("\\$\\{mainTable\\}", mainTable.getClass().getSimpleName());
		return javasrc;
	}

	/**
	 * 1レコード編集フォーム用DAOソース生成を行います。
	 * @param data フォームデータ。
	 * @param implist インポートリスト。
	 * @param javasrc javaソーステキスト。
	 * @return javaソーステキスト。
	 * @throws Exception 例外。
	 */
	private String singleRecordEditForm(final Map<String, Object> data, final ImportUtil implist, String javasrc) throws Exception {
		String p = (String) data.get(ID_PACKAGE_NAME);
		{
			String listQueryPackage = (String) data.get(ID_LIST_QUERY_PACKAGE_NAME);
			String listQueryClass = (String) data.get(ID_LIST_QUERY_CLASS_NAME);
			String queryPackage = (String) data.get(ID_EDIT_FORM_QUERY_PACKAGE_NAME);
			String queryClass = (String) data.get(ID_EDIT_FORM_QUERY_CLASS_NAME);
			if (!StringUtil.isBlank(queryClass)) {
				if (!StringUtil.isBlank(queryClass)) {
					if (!p.equals(queryPackage)) {
						implist.add(queryPackage + "." + queryClass);
					}
					logger.debug("listQuery=" + listQueryPackage + "," + listQueryClass);
					logger.debug("editQuery=" + queryPackage + "," + queryClass);
					if (listQueryPackage.equals(queryPackage) && listQueryClass.equals(queryClass)) {
						javasrc = javasrc.replaceAll("\\$\\{singleRecordQuery\\}", "this." + StringUtil.firstLetterToLowerCase(queryClass));
					} else {
						javasrc = javasrc.replaceAll("\\$\\{singleRecordQuery\\}", "this." + StringUtil.firstLetterToLowerCase(queryClass) + " = new " + queryClass + "()");
					}
				} else {
					implist.add(Query.class.getName());
					javasrc = javasrc.replaceAll("\\$\\{singleRecordQuery\\}", "(Query) null");
				}
			}
			if (!StringUtil.isBlank(queryClass)) {
				String className = queryPackage + "." + queryClass;
				Table mainTable = this.getMainTable(className);
				javasrc = javasrc.replaceAll("\\$\\{mainTable\\}", mainTable.getClass().getSimpleName());
			} else {
				implist.add(Table.class.getName());
				javasrc = javasrc.replaceAll("\\$\\{mainTable\\}", "Table");
			}
		}
		StringBuilder sb = new StringBuilder();
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> list = (List<Map<String, Object>>) data.get("multiRecordQueryList");
		for (Map<String, Object> m: list) {
			String pkgname = (String) m.get("packageName");
			String clsname = (String) m.get("queryClassName");
			if (!p.equals(pkgname)) {
				implist.add(pkgname + "." + clsname);
			}
			sb.append("\t\tthis.addMultiRecordQueryList(this." + StringUtil.firstLetterToLowerCase(clsname) + " = new " + clsname + "());\n");
		}
		javasrc = javasrc.replaceAll("\\$\\{addMultiRecordQueryList\\}", sb.toString());
		return javasrc;
	}

	/**
	 * Mainテーブルを取得します。
	 * @param className QueryまたはTableのクラス名。
	 * @return 主テーブルのインスタンス。
	 * @throws Exception 例外。
	 */
	private Table getMainTable(final String className) throws Exception {
		Table mainTable = null;
		if (!StringUtil.isBlank(className)) {
			Class<?> qclass = Class.forName(className);
			Object obj = qclass.getConstructor().newInstance();
			if (obj instanceof Query) {
				Query q = (Query) obj;
				mainTable = q.getMainTable();
			} else if (obj instanceof Table){
				mainTable = (Table) obj;
			}
		}
		return mainTable;
	}


	/**
	 * 指定されたクラスのフィールドリストを取得します。
	 * @param className QueryまたはTableのクラス名。
	 * @return 主テーブルのインスタンス。
	 * @throws Exception 例外。
	 */
	private FieldList getFieldList(final String className) throws Exception {
		FieldList fieldList = null;
		if (!StringUtil.isBlank(className)) {
			Class<?> qclass = Class.forName(className);
			Object obj = qclass.getConstructor().newInstance();
			if (obj instanceof Query) {
				Query q = (Query) obj;
				fieldList = q.getFieldList();
			} else if (obj instanceof Table){
				Table t = (Table) obj;
				fieldList = t.getFieldList();
			}
		}
		return fieldList;
	}

	/**
	 * 編集フォーム無DAOソース生成を行います。
	 * @param javasrc javaソース文字列。
	 * @param implist インポートリスト。
	 * @return javaソース文字列。
	 */
	private String noEditForm(String javasrc, final ImportUtil implist) {
		implist.add(Table.class.getName());
		javasrc = javasrc.replaceAll("\\$\\{singleRecordQuery\\}", "(Query) null");
		javasrc = javasrc.replaceAll("\\$\\{addMultiRecordQueryList\\}", "");
		javasrc = javasrc.replaceAll("\\$\\{mainTable\\}", "Table");
		implist.add("dataforms.dao.Query");
		return javasrc;
	}

	@Override
	protected void updateData(Map<String, Object> data) throws Exception {
		// 使用しない。
	}

	@Override
	public void deleteData(Map<String, Object> data) throws Exception {
		// 使用しない。
	}

}
