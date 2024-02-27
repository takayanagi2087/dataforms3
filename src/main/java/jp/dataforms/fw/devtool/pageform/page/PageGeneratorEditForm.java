package jp.dataforms.fw.devtool.pageform.page;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jp.dataforms.fw.controller.EditForm;
import jp.dataforms.fw.controller.Page;
import jp.dataforms.fw.controller.QueryForm;
import jp.dataforms.fw.controller.QueryResultForm;
import jp.dataforms.fw.controller.WebComponent;
import jp.dataforms.fw.dao.Dao;
import jp.dataforms.fw.dao.Query;
import jp.dataforms.fw.dao.QuerySetDao;
import jp.dataforms.fw.dao.SingleTableQuery;
import jp.dataforms.fw.dao.Table;
import jp.dataforms.fw.devtool.base.page.DeveloperPage;
import jp.dataforms.fw.devtool.field.DaoClassNameField;
import jp.dataforms.fw.devtool.field.EditFormClassNameField;
import jp.dataforms.fw.devtool.field.FunctionSelectField;
import jp.dataforms.fw.devtool.field.JavaSourcePathField;
import jp.dataforms.fw.devtool.field.OverwriteModeField;
import jp.dataforms.fw.devtool.field.PackageNameField;
import jp.dataforms.fw.devtool.field.PageClassNameField;
import jp.dataforms.fw.devtool.field.PageNameField;
import jp.dataforms.fw.devtool.field.QueryFormClassNameField;
import jp.dataforms.fw.devtool.field.QueryResultFormClassNameField;
import jp.dataforms.fw.exception.ApplicationException;
import jp.dataforms.fw.field.base.Field;
import jp.dataforms.fw.field.base.Field.MatchType;
import jp.dataforms.fw.field.base.FieldList;
import jp.dataforms.fw.field.common.CreateTimestampField;
import jp.dataforms.fw.field.common.CreateUserIdField;
import jp.dataforms.fw.field.common.DeleteFlagField;
import jp.dataforms.fw.field.common.FileField;
import jp.dataforms.fw.field.common.FlagField;
import jp.dataforms.fw.field.common.UpdateTimestampField;
import jp.dataforms.fw.field.common.UpdateUserIdField;
import jp.dataforms.fw.servlet.DataFormsServlet;
import jp.dataforms.fw.util.ClassNameUtil;
import jp.dataforms.fw.util.FileUtil;
import jp.dataforms.fw.util.ImportUtil;
import jp.dataforms.fw.util.MessagesUtil;
import jp.dataforms.fw.util.SequentialProperties;
import jp.dataforms.fw.util.StringUtil;
import jp.dataforms.fw.validator.RequiredValidator;
import jp.dataforms.fw.validator.ValidationError;

/**
 * ページ作成フォームクラス。
 *
 */
public class PageGeneratorEditForm extends EditForm {
	/**
	 * JavaソースパスフィールドID。
	 */
	private static final String ID_JAVA_SOURCE_PATH = "javaSourcePath";
	/**
	 * 機能選択フィールドID。
	 */
	private static final String ID_FUNCTION_SELECT = "functionSelect";
	/**
	 * ページ名フィールドID。
	 */
	private static final String ID_PAGE_NAME = "pageName";
	/**
	 * ページパッケージ名フィールドID。
	 */
	private static final String ID_PACKAGE_NAME = "packageName";
	/**
	 * ページクラス名フィールドID。
	 */
	private static final String ID_PAGE_CLASS_NAME = "pageClassName";
	/**
	 * 上書きモードフィールドID。
	 */
	private static final String ID_PAGE_CLASS_OVERWRITE_MODE = "pageClassOverwriteMode";

	/**
	 * DAOの機能選択フィールドID。
	 */
	private static final String ID_DAO_FUNCTION_SELECT = "daoFunctionSelect";
	/**
	 * DAOパッケージ名フィールドID。
	 */
	private static final String ID_DAO_PACKAGE_NAME = "daoPackageName";
	/**
	 * DAOクラス名フィールドID。
	 */
	private static final String ID_DAO_CLASS_NAME = "daoClassName";

	/**
	 * 問合せフォームクラス名フィールドID。
	 */
	private static final String ID_QUERY_FORM_CLASS_NAME = "queryFormClassName";
	/**
	 * 問合せフォーム上書きモードフィールドID。
	 */
	private static final String ID_QUERY_FORM_CLASS_OVERWRITE_MODE = "queryFormClassOverwriteMode";
	/**
	 * 問合せフォーム生成フラグフィールドID。
	 */
	private static final String ID_QUERY_FORM_CLASS_FLAG = "queryFormClassFlag";
	/**
	 * 問合せ結果フォームクラス名フィールドID。
	 */
	private static final String ID_QUERY_RESULT_FORM_CLASS_NAME = "queryResultFormClassName";
	/**
	 * 問合せ結果フォーム上書きモードフィールドID。
	 */
	private static final String ID_QUERY_RESULT_FORM_CLASS_OVERWRITE_MODE = "queryResultFormClassOverwriteMode";
	/**
	 * 問合せ結果フォーム生成フラグフィールドID。
	 */
	private static final String ID_QUERY_RESULT_FORM_CLASS_FLAG = "queryResultFormClassFlag";
	/**
	 * 編集フォームクラス名フィールドID。
	 */
	private static final String ID_EDIT_FORM_CLASS_NAME = "editFormClassName";
	/**
	 * 編集フォーム上書きモードフィールドID。
	 */
	private static final String ID_EDIT_FORM_CLASS_OVERWRITE_MODE = "editFormClassOverwriteMode";
	/**
	 * 編集フォーム生成フラグフィールドID。
	 */
	private static final String ID_EDIT_FORM_CLASS_FLAG = "editFormClassFlag";
	/**
	 * Logger。
	 */
	private static Logger logger = LogManager.getLogger(PageGeneratorEditForm.class.getName());

	/**
	 * コンストラクタ。
	 */
	public PageGeneratorEditForm() {
		this.addField(new JavaSourcePathField());
		FunctionSelectField funcField = new FunctionSelectField();
		funcField.setPackageOption("page");
		funcField.setCalcEventField(true);
		this.addField(funcField);

		this.addField(new PageNameField()).addValidator(new RequiredValidator());
		this.addField(new PackageNameField()).addValidator(new RequiredValidator());
		this.addField(new PageClassNameField())
			.addValidator(new RequiredValidator())
			.setCalcEventField(true)
			.setAutocomplete(false);
		this.addField(new OverwriteModeField(ID_PAGE_CLASS_OVERWRITE_MODE));

		this.addField((new FunctionSelectField(ID_DAO_FUNCTION_SELECT)).setPackageFieldId(ID_DAO_PACKAGE_NAME));
		this.addField(new PackageNameField(ID_DAO_PACKAGE_NAME));
		DaoClassNameField daocls = new DaoClassNameField();
		daocls.setCalcEventField(true);
		daocls.setAutocomplete(true);
		daocls.setRelationDataAcquisition(true);
		daocls.setPackageNameFieldId(ID_DAO_PACKAGE_NAME);
		this.addField(daocls);
		this.addField(new QueryFormClassNameField());
		this.addField(new FlagField(ID_QUERY_FORM_CLASS_FLAG));
		this.addField(new OverwriteModeField(ID_QUERY_FORM_CLASS_OVERWRITE_MODE));
		this.addField(new QueryResultFormClassNameField());
		this.addField(new FlagField(ID_QUERY_RESULT_FORM_CLASS_FLAG));
		this.addField(new OverwriteModeField(ID_QUERY_RESULT_FORM_CLASS_OVERWRITE_MODE));
		this.addField(new EditFormClassNameField());
		this.addField(new OverwriteModeField(ID_EDIT_FORM_CLASS_OVERWRITE_MODE));
		this.addField(new FlagField(ID_EDIT_FORM_CLASS_FLAG));
	}

	@Override
	public void init() throws Exception {
		super.init();
		this.setFormData(ID_JAVA_SOURCE_PATH, DeveloperPage.getJavaSourcePath());
		this.setFormData(ID_PAGE_CLASS_OVERWRITE_MODE, OverwriteModeField.ERROR);
		this.setFormData(ID_QUERY_FORM_CLASS_FLAG, "1");
		this.setFormData(ID_QUERY_FORM_CLASS_OVERWRITE_MODE, OverwriteModeField.ERROR);
		this.setFormData(ID_QUERY_RESULT_FORM_CLASS_FLAG, "1");
		this.setFormData(ID_QUERY_RESULT_FORM_CLASS_OVERWRITE_MODE, OverwriteModeField.ERROR);
		this.setFormData(ID_EDIT_FORM_CLASS_FLAG, "1");
		this.setFormData(ID_EDIT_FORM_CLASS_OVERWRITE_MODE, OverwriteModeField.ERROR);

	}


	@Override
	protected Map<String, Object> queryData(final Map<String, Object> data) throws Exception {
		logger.debug("data=" + data);
		String pkg = (String) data.get(ID_PACKAGE_NAME);
		String cls = (String) data.get(ID_PAGE_CLASS_NAME);
		String classname = pkg + "." + cls;
		Class<?> clazz = Class.forName(classname);
		Page p = (Page) clazz.getDeclaredConstructor().newInstance();
		PageClassInfo pi = new PageClassInfo(p);
		// Class<? extends Table> tblcls = pi.getTableClass();
		Class<? extends Dao> daocls = pi.getDaoClass();
		String functionPath = pi.getFunctionPath();
		if (daocls == null || functionPath == null) {
			throw new ApplicationException(this.getPage(), "error.notgeneratedpage");
		}
		Map<String, Object> ret = new HashMap<String, Object>();
		ret.putAll(data);
		if (daocls.getName().equals(jp.dataforms.fw.dao.Dao.class.getName())) {
			ret.put(ID_DAO_PACKAGE_NAME, "");
			ret.put(ID_DAO_CLASS_NAME, "");
		} else {
			ret.put(ID_DAO_PACKAGE_NAME, ClassNameUtil.getPackageName(daocls.getName()));
			ret.put(ID_DAO_CLASS_NAME, daocls.getSimpleName());
		}
		ret.put(ID_PAGE_CLASS_OVERWRITE_MODE, OverwriteModeField.ERROR);
		ret.put(ID_FUNCTION_SELECT, functionPath);


		String funcprop = this.getFunctionPropertiesPath(functionPath);
		SequentialProperties prop = this.readFunctionProperties(funcprop);
		String name = (String) prop.get(pkg + "." + cls);
		ret.put(ID_PAGE_NAME, name);

		Map<String, WebComponent> fm = p.getFormMap();
		ret.put(ID_QUERY_FORM_CLASS_FLAG, "0");
		ret.put(ID_QUERY_RESULT_FORM_CLASS_FLAG, "0");
		ret.put(ID_EDIT_FORM_CLASS_FLAG, "0");
		for (String key: fm.keySet()) {
			logger.debug("*** key=" + key);
			WebComponent cmp = fm.get(key);
			if (cmp != null) {
				if (cmp instanceof QueryForm) {
					ret.put(ID_QUERY_FORM_CLASS_NAME, cmp.getClass().getSimpleName());
					ret.put(ID_QUERY_FORM_CLASS_FLAG, "1");
					ret.put(ID_QUERY_FORM_CLASS_OVERWRITE_MODE, OverwriteModeField.ERROR);
				} else if (cmp instanceof QueryResultForm) {
					ret.put(ID_QUERY_RESULT_FORM_CLASS_NAME, cmp.getClass().getSimpleName());
					ret.put(ID_QUERY_RESULT_FORM_CLASS_FLAG, "1");
					ret.put(ID_QUERY_RESULT_FORM_CLASS_OVERWRITE_MODE, OverwriteModeField.ERROR);
				} else if (cmp instanceof EditForm) {
					ret.put(ID_EDIT_FORM_CLASS_NAME, cmp.getClass().getSimpleName());
					ret.put(ID_EDIT_FORM_CLASS_FLAG, "1");
					ret.put(ID_EDIT_FORM_CLASS_OVERWRITE_MODE, OverwriteModeField.ERROR);
				}
			}
		}
		ret.put(ID_JAVA_SOURCE_PATH, DeveloperPage.getJavaSourcePath());
		return ret;
	}

	@Override
	protected boolean isUpdate(final Map<String, Object> data) throws Exception {
		return false;
	}

	/**
	 * DAOクラスのインスタンスを取得します。
	 * @param data フォームデータ。
	 * @return DAOクラスのインスタンス。
	 * @throws Exception 例外。
	 */
	private QuerySetDao getDao(final Map<String, Object> data) throws Exception {
		String daoPackageName = (String) data.get(ID_DAO_PACKAGE_NAME);
		String daoClassName = (String) data.get(ID_DAO_CLASS_NAME);
		if (StringUtil.isBlank(daoPackageName) == false && StringUtil.isBlank(daoClassName) == false) {
			String className = daoPackageName + "." + daoClassName;
			@SuppressWarnings("unchecked")
			Class<? extends QuerySetDao> daoclass = (Class<? extends QuerySetDao>) Class.forName(className);
			return daoclass.getConstructor().newInstance();
		} else {
			return null;
		}
	}

	@Override
	public List<ValidationError> validate(final Map<String, Object> param) throws Exception {
		List<ValidationError> ret =  super.validate(param);
		if (ret.size() == 0) {
			Map<String, Object> data = this.convertToServerData(param);
			String javaSrc = (String) data.get(ID_JAVA_SOURCE_PATH);
			String packageName = (String) data.get(ID_PACKAGE_NAME);

			QuerySetDao dao = this.getDao(data);

			String queryFormClassName = (String) data.get(ID_QUERY_FORM_CLASS_NAME);
			String queryResultFormClassName = (String) data.get(ID_QUERY_RESULT_FORM_CLASS_NAME);
			String editFormClassName = (String) data.get(ID_EDIT_FORM_CLASS_NAME);

			if (dao != null) {
				logger.debug(() -> "dao classname=" + dao.getClass().getName());
				logger.debug(() -> "getListQuery=" + dao.getListQuery());
				logger.debug(() -> "getSingleRecordQuery=" + dao.getSingleRecordQuery());
				logger.debug(() -> "getMultiRecordQueryList=" + dao.getMultiRecordQueryList());
				if (dao.getListQuery() == null) {
					if (!StringUtil.isBlank(queryFormClassName)) {
						ret.add(new ValidationError(ID_QUERY_FORM_CLASS_NAME, this.getPage().getMessage("error.listqueryundefined", "{0}", dao.getClass().getSimpleName())));
					}
					if (!StringUtil.isBlank(queryResultFormClassName)) {
						ret.add(new ValidationError(ID_QUERY_RESULT_FORM_CLASS_NAME, this.getPage().getMessage("error.listqueryundefined", "{0}", dao.getClass().getSimpleName())));
					}
				}
				if (dao.getSingleRecordQuery() == null && dao.getMultiRecordQueryList() == null) {
					if (!StringUtil.isBlank(editFormClassName)) {
						ret.add(new ValidationError(ID_EDIT_FORM_CLASS_NAME, this.getPage().getMessage("error.editqueryundefined", "{0}", dao.getClass().getSimpleName())));
					}
				}
			}

			String pageClassName = (String) data.get(ID_PAGE_CLASS_NAME);
			String srcPath = javaSrc + "/" + packageName.replaceAll("\\.", "/");
			String pageClassOverwriteMode = (String) data.get(ID_PAGE_CLASS_OVERWRITE_MODE);
			File f3 = new File(srcPath + "/" + pageClassName + ".java");
			if (OverwriteModeField.ERROR.equals(pageClassOverwriteMode)) {
				if (f3.exists()) {
					ret.add(new ValidationError(ID_PAGE_CLASS_NAME, this.getPage().getMessage("error.sourcefileexist", pageClassName + ".java")));
				}
			}

			String queryFormClassOverwriteMode = (String) data.get(ID_QUERY_FORM_CLASS_OVERWRITE_MODE);
			if (OverwriteModeField.ERROR.equals(queryFormClassOverwriteMode)) {
				File f0 = new File(srcPath + "/" + queryFormClassName + ".java");
				if (f0.exists()) {
					ret.add(new ValidationError(ID_QUERY_FORM_CLASS_NAME, this.getPage().getMessage("error.sourcefileexist", queryFormClassName + ".java")));
				}
			}
			String queryResultFormClassOverwriteMode = (String) data.get(ID_QUERY_RESULT_FORM_CLASS_OVERWRITE_MODE);
			if (OverwriteModeField.ERROR.equals(queryResultFormClassOverwriteMode)) {
				File f1 = new File(srcPath + "/" + queryResultFormClassName + ".java");
				if (f1.exists()) {
					ret.add(new ValidationError(ID_QUERY_RESULT_FORM_CLASS_NAME, this.getPage().getMessage("error.sourcefileexist", queryResultFormClassName + ".java")));
				}
			}
			String editFormClassOverwriteMode = (String) data.get(ID_EDIT_FORM_CLASS_OVERWRITE_MODE);
			if (OverwriteModeField.ERROR.equals(editFormClassOverwriteMode)) {
				File f2 = new File(srcPath + "/" + editFormClassName + ".java");
				if (f2.exists()) {
					ret.add(new ValidationError(ID_EDIT_FORM_CLASS_NAME, this.getPage().getMessage("error.sourcefileexist", editFormClassName + ".java")));
				}
			}
		}
		return ret;

	}

	/**
	 * 問い合わせフォームのフィールドリストソースの作成。
	 * @param dao DAO。
	 * @param implist フィールドのインポートリスト。
	 * @return 問い合わせフォームのフィールドリストソース。
	 * @throws Exception 例外。
	 */
	private String getQueryFieldList(final QuerySetDao dao, final ImportUtil implist) throws Exception {
		String qscn = null;
		FieldList flist = null;
		if (dao.getListQuery() != null) {
			flist = dao.getListQuery().getFieldList();
			Query listQuery = dao.getListQuery();
			if (!(listQuery instanceof SingleTableQuery)) {
				qscn = listQuery.getClass().getSimpleName();
				implist.add(listQuery.getClass().getName());
			}
		} else if (dao.getMultiRecordQueryKeyList() != null) {
			flist = dao.getMultiRecordQueryKeyList();
		} else {
			flist = new FieldList();
		}
		implist.add("java.util.List");
		implist.add("java.util.Map");
		implist.add("dataforms.report.ExportDataFile");
		implist.add("dataforms.field.base.FieldList");
		implist.add("dataforms.field.base.Field.MatchType");

		StringBuilder sb = new StringBuilder();
		for (Field<?> f: flist) {
			Table tbl = f.getTable();
			String scn = tbl.getClass().getSimpleName();
			if (qscn != null) {
				scn = qscn;
			} else {
				implist.add(tbl.getClass());
			}
			if (tbl.getPkFieldList().get(f.getId()) != null) {
				continue;
			}
			if (f instanceof DeleteFlagField
				|| f instanceof CreateUserIdField
				|| f instanceof CreateTimestampField
				|| f instanceof UpdateUserIdField
				|| f instanceof UpdateTimestampField
				|| f instanceof FileField
				) {
				;
			} else {
				MatchType dt = f.getDefaultMatchType();
				if (dt == MatchType.NONE) {
					;
				} else if (dt == MatchType.RANGE_FROM) {
					String id = f.getId();
					String cname = f.getClass().getSimpleName();
					String cmt = f.getComment();
					sb.append("\t\tthis.addField(new " + cname + "(" + scn + ".Entity.ID_" + StringUtil.camelToUpperCaseSnake(id) + " + \"From\")).setMatchType(MatchType.RANGE_FROM).setComment(\"" + cmt + "(from)\");\n");
					sb.append("\t\tthis.addField(new " + cname + "(" + scn + ".Entity.ID_" + StringUtil.camelToUpperCaseSnake(id) + " + \"To\")).setMatchType(MatchType.RANGE_TO).setComment(\"" + cmt + "(to)\");\n");
					implist.add(f.getClass());
				} else {
					String id = f.getId();
					String mt = dt.name();
					String cname = f.getClass().getSimpleName();
					String cmt = f.getComment();
					sb.append("\t\tthis.addField(new " + cname + "(" + scn + ".Entity.ID_" + StringUtil.camelToUpperCaseSnake(id) + ")).setMatchType(MatchType." + mt + ").setComment(\"" + cmt + "\");\n");
					implist.add(f.getClass());
				}
			}
		}
		return sb.toString();
	}


	/**
	 * テンプレートのパスを取得します。
	 * @param data POSTされたデータ。
	 * @return テンプレートのパス。
	 */
	private String getTemplatePath(final Map<String, Object> data) {
		return (this.isUpdateTable(data) ? "template/" : "notabletemplate/");
	}


	/**
	 * 問い合わせ結果フォームのソートカラム指定コードを生成します。
	 * @param dao フィールドリスト。
	 * @param implist フィールドのインポートリスト。
	 * @return 生成されたコード。
	 * @throws Exception 例外。
	 */
	private String getQueryResultFieldSetting(final QuerySetDao dao, final ImportUtil implist) throws Exception {
		String qscn = null;
		FieldList flist = new FieldList();
		if (dao.getListQuery() != null) {
			flist = dao.getListQuery().getFieldList();
			Query listQuery = dao.getListQuery();
			if (!(listQuery instanceof SingleTableQuery)) {
				qscn = listQuery.getClass().getSimpleName();
				implist.add(listQuery.getClass().getName());
			}
		} else if (dao.getMultiRecordQueryKeyList() != null) {
			flist = dao.getMultiRecordQueryKeyList();
		}

		StringBuilder sb = new StringBuilder();
		for (Field<?> f: flist) {
			Table tbl = f.getTable();
			String scn = tbl.getClass().getSimpleName();
			if (qscn != null) {
				scn = qscn;
			} else {
				implist.add(tbl.getClass());
			}
			if (f.isHidden() || f instanceof DeleteFlagField) {
				continue;
			}
//			implist.add(tbl.getClass());
			sb.append("\t\thtmltable.getFieldList().get(" + scn + ".Entity.ID_" + StringUtil.camelToUpperCaseSnake(f.getId()) + ").setSortable(true);\n");
		}
		return sb.toString();
	}

	/**
	 * フォームクラスの生成処理。
	 * @param data 入力データ。
	 * @param packageNameId パッケージ名フィールドのID。
	 * @param formFieldId フォーム名称のフィールドID。
	 * @param template テンプレートリソース。
	 * @param overWriteMode 上書きモード。
	 * @return 作成されたクラス名。
	 * @throws Exception 例外。
	 *
	 */
	private String generateFormClass(final Map<String, Object> data, final String packageNameId, final String formFieldId, final String template, final String overWriteMode) throws Exception {
		String javaSrc = (String) data.get(ID_JAVA_SOURCE_PATH);
		String daoPackageName = (String) data.get(ID_DAO_PACKAGE_NAME);
		String daoClassName = (String) data.get(ID_DAO_CLASS_NAME);
		String packageName = (String) data.get(packageNameId);
		String formClassName = (String) data.get(formFieldId);
		if (!StringUtil.isBlank(formClassName)) {
			String javasrc = this.getStringResourse(this.getTemplatePath(data) + template);
			javasrc = javasrc.replaceAll("\\$\\{packageName\\}", packageName);
			javasrc = javasrc.replaceAll("\\$\\{daoClassFullName\\}", daoPackageName + "." + daoClassName);
			javasrc = javasrc.replaceAll("\\$\\{" + formFieldId + "\\}", formClassName);
			javasrc = javasrc.replaceAll("\\$\\{daoClassName\\}", daoClassName);

			String validateForm = this.getStringResourse("template/validateForm.java.template");
			String webMethod = this.getStringResourse("template/webMethod.java.template");
			javasrc = javasrc.replaceAll("\\$\\{validateForm\\}", validateForm);
			javasrc = javasrc.replaceAll("\\$\\{webMethod\\}", webMethod);

			if (this.isUpdateTable(data)) {
				@SuppressWarnings("unchecked")
				Class<? extends QuerySetDao> daoclass = (Class<? extends QuerySetDao>) Class.forName(daoPackageName + "." + daoClassName);
				QuerySetDao dao = daoclass.getDeclaredConstructor().newInstance();
/*				FieldList flist = new FieldList();
				if (dao.getListQuery() != null) {
					flist = dao.getListQuery().getFieldList();
				} else if (dao.getMultiRecordQueryKeyList() != null) {
					flist = dao.getMultiRecordQueryKeyList();
				}*/
				{
					ImportUtil implist0 = new ImportUtil();
					javasrc = javasrc.replaceAll("\\$\\{queryFormFieldList\\}", this.getQueryFieldList(dao, implist0));
					javasrc = javasrc.replaceAll("\\$\\{queryFormImportList\\}", implist0.getImportText());

				}
				{
					ImportUtil implist1 = new ImportUtil();
					javasrc = javasrc.replaceAll("\\$\\{queryResultFieldSetting\\}", this.getQueryResultFieldSetting(dao, implist1));
					javasrc = javasrc.replaceAll("\\$\\{queryResultFormImportList\\}", implist1.getImportText());
				}
			}
			String srcPath = javaSrc + "/" + packageName.replaceAll("\\.", "/");
			File dir = new File(srcPath);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			String srcfile = srcPath + "/" + formClassName + ".java";
			logger.debug("srcpath=" + srcfile);
			logger.debug("src=" + javasrc);
			if (!OverwriteModeField.SKIP.equals(overWriteMode)) {
				FileUtil.writeTextFileWithBackup(srcfile, javasrc, DataFormsServlet.getEncoding());
			}
		}
		return formClassName;
	}

	/**
	 * テーブル更新処理生成フラグを取得する。
	 * @param data POSTされたデータ。
	 * @return テーブル操作を行う場合true。
	 */
	private boolean isUpdateTable(final Map<String, Object> data) {
		String daoClassName = (String) data.get(ID_DAO_CLASS_NAME);
		return !StringUtil.isBlank(daoClassName);
	}


	@Override
	protected void insertData(final Map<String, Object> data) throws Exception {
		String javaSrc = (String) data.get(ID_JAVA_SOURCE_PATH);
		String functionPath = (String) data.get(ID_FUNCTION_SELECT);
		String packageName = (String) data.get(ID_PACKAGE_NAME);
		String pageClassName = (String) data.get(ID_PAGE_CLASS_NAME);
		String daoPackageName = (String) data.get(ID_DAO_PACKAGE_NAME);
		String daoClassName = (String) data.get(ID_DAO_CLASS_NAME);

		String queryFormClassOverwriteMode = (String) data.get(ID_QUERY_FORM_CLASS_OVERWRITE_MODE);
		String queryResultFormClassOverwriteMode = (String) data.get(ID_QUERY_RESULT_FORM_CLASS_OVERWRITE_MODE);
		String editFormClassOverwriteMode = (String) data.get(ID_EDIT_FORM_CLASS_OVERWRITE_MODE);
		String pageClassOverwriteMode = (String) data.get(ID_PAGE_CLASS_OVERWRITE_MODE);


		String queryFormClass = this.generateFormClass(data, ID_PACKAGE_NAME, ID_QUERY_FORM_CLASS_NAME, "QueryForm.java.template", queryFormClassOverwriteMode);
		String queryResultFormClass = this.generateFormClass(data, ID_PACKAGE_NAME, ID_QUERY_RESULT_FORM_CLASS_NAME, "QueryResultForm.java.template", queryResultFormClassOverwriteMode);
		String editFormClass = this.generateFormClass(data, ID_PACKAGE_NAME, ID_EDIT_FORM_CLASS_NAME, "EditForm.java.template", editFormClassOverwriteMode);


		StringBuilder sb = new StringBuilder();
		if (!StringUtil.isBlank(queryFormClass)) {
			sb.append("\t\tthis.addForm(new " + queryFormClass + "());\n");
		}
		if (!StringUtil.isBlank(queryResultFormClass)) {
			sb.append("\t\tthis.addForm(new " + queryResultFormClass + "());\n");
		}
		if (!StringUtil.isBlank(editFormClass)) {
			sb.append("\t\tthis.addForm(new " + editFormClass + "());\n");
		}

		String javasrc = this.getStringResourse(this.getTemplatePath(data) + "Page.java.template");
		javasrc = javasrc.replaceAll("\\$\\{packageName\\}", packageName);
		javasrc = javasrc.replaceAll("\\$\\{daoClassFullName\\}", daoPackageName + "." + daoClassName);
		javasrc = javasrc.replaceAll("\\$\\{pageClassName\\}", pageClassName);
		javasrc = javasrc.replaceAll("\\$\\{formList\\}", sb.toString());
		javasrc = javasrc.replaceAll("\\$\\{daoClass\\}", daoClassName);
		javasrc = javasrc.replaceAll("\\$\\{functionPath\\}", functionPath);
		String srcPath = javaSrc + "/" + packageName.replaceAll("\\.", "/");
		String srcfile = srcPath + "/" + pageClassName + ".java";
//		log.debug("srcpath=" + srcfile);
//		log.debug("src=" + javasrc);
		if (!OverwriteModeField.SKIP.equals(pageClassOverwriteMode)) {
			FileUtil.writeTextFileWithBackup(srcfile, javasrc, DataFormsServlet.getEncoding());
		}
		String pageName = (String) data.get(ID_PAGE_NAME);
		this.updatePageName(functionPath, packageName, pageClassName, pageName);

	}

	/**
	 * ページ名の更新。
	 * @param functionPath 機能パス。
	 * @param packageName パッケージ名。
	 * @param pageClassName ページクラス名。
	 * @param pageName ページ名。
	 * @throws Exception 例外。
	 */
	private void updatePageName(final String functionPath, final String packageName, final String pageClassName, final String pageName) throws Exception {
		String funcprop = this.getFunctionPropertiesPath(functionPath);
		SequentialProperties prop = this.readFunctionProperties(funcprop);
		prop.put(packageName + "." + pageClassName, pageName);
		String str = prop.getSaveText();
		logger.debug("str=" + str);
		FileUtil.writeTextFileWithBackup(funcprop, str, DataFormsServlet.getEncoding());

	}

	/**
	 * Function.propertiesのパスを取得します。
	 * @param functionPath 機能のパス。
	 * @return Function.propertiesのパス。
	 * @throws Exception 例外。
	 */
	private String getFunctionPropertiesPath(final String functionPath) throws Exception {
		String webResourcePath = DeveloperPage.getWebSourcePath();
		String funcprop = this.getPage().getAppropriatePath(functionPath + "/Function.properties", this.getPage().getRequest());
		if (funcprop == null) {
			funcprop = functionPath + "/Function.properties";
		}
		funcprop = webResourcePath + funcprop;
		return funcprop;
	}

	/**
	 * Function.propertiesを読み込みます。
	 * @param funcprop Function.propertiesのパス。
	 * @return 読み込んだ内容。
	 * @throws Exception 例外。
	 */
	private SequentialProperties readFunctionProperties(final String funcprop) throws Exception {
		String text = "";
		File propfile = new File(funcprop);
		if (propfile.exists()) {
			text = FileUtil.readTextFile(funcprop, DataFormsServlet.getEncoding());
		}
		SequentialProperties prop = new SequentialProperties();
		prop.loadText(text);
		return prop;
	}


	@Override
	protected void updateData(final Map<String, Object> data) throws Exception {
		// 何もしない
	}

	@Override
	public void deleteData(final Map<String, Object> data) throws Exception {
		// 何もしない
	}


	@Override
	protected String getSavedMessage(final Map<String, Object> data) {
		return MessagesUtil.getMessage(this.getPage(), "message.javasourcecreated");
	}
}
