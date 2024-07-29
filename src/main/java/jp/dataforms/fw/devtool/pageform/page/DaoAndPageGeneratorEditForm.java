package jp.dataforms.fw.devtool.pageform.page;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jp.dataforms.fw.annotation.WebMethod;
import jp.dataforms.fw.app.login.page.LoginInfoForm;
import jp.dataforms.fw.app.menu.page.SideMenuForm;
import jp.dataforms.fw.controller.EditForm;
import jp.dataforms.fw.controller.Form;
import jp.dataforms.fw.controller.Page;
import jp.dataforms.fw.controller.QueryForm;
import jp.dataforms.fw.controller.QueryResultForm;
import jp.dataforms.fw.controller.WebComponent;
import jp.dataforms.fw.dao.Dao;
import jp.dataforms.fw.dao.Query;
import jp.dataforms.fw.dao.QuerySetDao;
import jp.dataforms.fw.dao.SingleTableQuery;
import jp.dataforms.fw.devtool.base.page.DeveloperPage;
import jp.dataforms.fw.devtool.dao.gen.QuerySetDaoGenerator;
import jp.dataforms.fw.devtool.field.DaoClassNameField;
import jp.dataforms.fw.devtool.field.EditFormClassNameField;
import jp.dataforms.fw.devtool.field.FormClassNameField;
import jp.dataforms.fw.devtool.field.FunctionSelectField;
import jp.dataforms.fw.devtool.field.JavaSourcePathField;
import jp.dataforms.fw.devtool.field.OverwriteModeField;
import jp.dataforms.fw.devtool.field.PackageNameField;
import jp.dataforms.fw.devtool.field.PageClassNameField;
import jp.dataforms.fw.devtool.field.PageNameField;
import jp.dataforms.fw.devtool.field.PagePatternSelectField;
import jp.dataforms.fw.devtool.field.QueryFormClassNameField;
import jp.dataforms.fw.devtool.field.QueryOrTableClassNameField;
import jp.dataforms.fw.devtool.field.QueryResultFormClassNameField;
import jp.dataforms.fw.devtool.pageform.gen.DbPageGenerator;
import jp.dataforms.fw.devtool.pageform.gen.EditFormGenerator;
import jp.dataforms.fw.devtool.pageform.gen.QueryFormGenerator;
import jp.dataforms.fw.devtool.pageform.gen.QueryResultFormGenerator;
import jp.dataforms.fw.devtool.pageform.gen.SimpleFormGenerator;
import jp.dataforms.fw.devtool.pageform.gen.SimplePageGenerator;
import jp.dataforms.fw.devtool.query.page.SelectFieldHtmlTable;
import jp.dataforms.fw.exception.ApplicationException;
import jp.dataforms.fw.field.base.Field;
import jp.dataforms.fw.field.base.Field.MatchType;
import jp.dataforms.fw.field.base.FieldList;
import jp.dataforms.fw.field.base.TextField;
import jp.dataforms.fw.field.common.SingleSelectField.HtmlFieldType;
import jp.dataforms.fw.htmltable.EditableHtmlTable;
import jp.dataforms.fw.htmltable.HtmlTable;
import jp.dataforms.fw.response.JsonResponse;
import jp.dataforms.fw.response.Response;
import jp.dataforms.fw.util.ClassNameUtil;
import jp.dataforms.fw.util.JsonUtil;
import jp.dataforms.fw.util.MessagesUtil;
import jp.dataforms.fw.validator.RequiredValidator;
import jp.dataforms.fw.validator.ValidationError;

/**
 * ページDbPageGenerator作成フォームクラス。
 *
 */
public class DaoAndPageGeneratorEditForm extends EditForm {
	/**
	 * Logger.
	 */
	private static Logger logger = LogManager.getLogger(DaoAndPageGeneratorEditForm.class);

	/**
	 * ページの動作パターン。
	 */
	public static final String ID_PAGE_PATTERN = "pagePattern";

	/**
	 * JavaソースパスフィールドID。
	 */
	public static final String ID_JAVA_SOURCE_PATH = "javaSourcePath";

	/**
	 * ページ名フィールドID。
	 */
	public static final String ID_FUNCTION_SELECT = "functionSelect";

	/**
	 * ページ名フィールドID。
	 */
	public static final String ID_PAGE_NAME = "pageName";
	/**
	 * 説明フィールドID。
	 */
	public static final String ID_DESCRIPTION = "description";
	/**
	 * ページパッケージ名フィールドID。
	 */
	public static final String ID_PACKAGE_NAME = "packageName";

	/**
	 * DAOパッケージ名フィールドID。
	 */
	public static final String ID_DAO_PACKAGE_NAME = "daoPackageName";

	/**
	 * DAOクラス名フィールドID。
	 */
	public static final String ID_DAO_CLASS_NAME = "daoClassName";

	/**
	 * ページクラス名フィールドID。
	 */
	public static final String ID_PAGE_CLASS_NAME = "pageClassName";

	/**
	 * 上書きモードフィールドID。
	 */
	public static final String ID_PAGE_CLASS_OVERWRITE_MODE = "pageClassOverwriteMode";

	/**
	 * 上書きモードフィールドID。
	 */
	public static final String ID_DAO_CLASS_OVERWRITE_MODE = "daoClassOverwriteMode";


	/**
	 * 空のフォームクラス名フィールドID。
	 */
	public static final String ID_FORM_CLASS_NAME = "formClassName";

	/**
	 * フォームの上書きモードフィールドID。
	 */
	private static final String ID_FORM_CLASS_OVERWRITE_MODE = "formClassOverwriteMode";

	/**
	 * 問合せフォーム上書きモードフィールドID。
	 */
	public static final String ID_QUERY_FORM_CLASS_OVERWRITE_MODE = "queryFormClassOverwriteMode";

	/**
	 * 問合せ結果フォーム上書きモードフィールドID。
	 */
	public static final String ID_QUERY_RESULT_FORM_CLASS_OVERWRITE_MODE = "queryResultFormClassOverwriteMode";

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
	 * 編集フォーム上書きモードフィールドID。
	 */
	public static final String ID_EDIT_FORM_CLASS_OVERWRITE_MODE = "editFormClassOverwriteMode";
	/**
	 * 編集フォームレコード取得問合せの機能選択フィールドID。
	 */
	public static final String ID_EDIT_QUERY_FUNCTION_SELECT = "editQueryFunctionSelect";
	/**
	 * 編集フォームレコード取得問合せのパッケージ名フィールドID。
	 */
	public static final String ID_EDIT_QUERY_PACKAGE_NAME = "editQueryPackageName";
	/**
	 * 編集フォームレコード取得問合せのクラス名フィールドID。
	 */
	public static final String ID_EDIT_QUERY_CLASS_NAME = "editQueryClassName";
	/**
	 * キーフィールドリストID。
	 */
	public static final String ID_MULTI_RECORD_QUERY_KEY_LIST = "multiRecordQueryKeyList";
	/**
	 * 複数レコード取得問合せリストID。
	 */
	public static final String ID_MULTI_RECORD_QUERY_LIST = "multiRecordQueryList";
	/**
	 * 問合せクラス名のフィールドID。
	 */
	public static final String ID_QUERY_CLASS_NAME = "queryClassName";

	/**
	 * 問合せフォームクラス名フィールドID。
	 */
	public static final String ID_QUERY_FORM_CLASS_NAME = "queryFormClassName";

	/**
	 * 問合せ結果フォームクラス名フィールドID。
	 */
	public static final String ID_QUERY_RESULT_FORM_CLASS_NAME = "queryResultFormClassName";
	/**
	 * 編集フォームクラス名フィールドID。
	 */
	public static final String ID_EDIT_FORM_CLASS_NAME = "editFormClassName";

	/**
	 * 一覧問合せ設定フィールドの設定情報。
	 */
	public static final String ID_LIST_QUERY_CONFIG = "listQueryConfig";

	/**
	 * 編集問合せフィールドの設定情報。
	 */
	public static final String ID_EDIT_QUERY_CONFIG = "editQueryConfig";

	/**
	 * 複数レコード編集問合せのフィールド設定。
	 */
	public static final String ID_QUERY_CONFIG = "queryConfig";

	/**
	 * コンストラクタ。
	 */
	public DaoAndPageGeneratorEditForm() {
		this.addField(new JavaSourcePathField());
		FunctionSelectField funcField = new FunctionSelectField();
		funcField.setPackageOption("page", "dao");
		funcField.setPackageFieldId(ID_PACKAGE_NAME, ID_DAO_PACKAGE_NAME);
		funcField.setCalcEventField(true);
		// ページの機能
		this.addField(new PagePatternSelectField(ID_PAGE_PATTERN)
				.setHtmlFieldType(HtmlFieldType.SELECT))
				.addValidator(new RequiredValidator())
				.setComment("ページの動作");
		// 生成するクラス
		this.addField(funcField);
		this.addField(new PageNameField()).addValidator(new RequiredValidator());
		this.addField(new TextField(ID_DESCRIPTION));
		this.addField(new PackageNameField()).addValidator(new RequiredValidator()).setComment("ページパッケージ名");
		this.addField(new PageClassNameField())	.addValidator(new RequiredValidator()).setCalcEventField(true).setAutocomplete(false);
		this.addField(new OverwriteModeField(ID_PAGE_CLASS_OVERWRITE_MODE));
		this.addField(new PackageNameField(ID_DAO_PACKAGE_NAME)).setComment("DAOパッケージ名");
		this.addField(new DaoClassNameField()).setComment("DAOクラス名");
		this.addField(new OverwriteModeField(ID_DAO_CLASS_OVERWRITE_MODE));

		this.addField(new FormClassNameField());
		this.addField(new OverwriteModeField(ID_FORM_CLASS_OVERWRITE_MODE));
		this.addField(new QueryFormClassNameField());
		this.addField(new OverwriteModeField(ID_QUERY_FORM_CLASS_OVERWRITE_MODE));
		this.addField(new QueryResultFormClassNameField());
		this.addField(new OverwriteModeField(ID_QUERY_RESULT_FORM_CLASS_OVERWRITE_MODE));

		this.addField((new FunctionSelectField(ID_LIST_QUERY_FUNCTION_SELECT)).setPackageFieldId(ID_LIST_QUERY_PACKAGE_NAME).setComment("一覧問合せの機能"));
		this.addField((new PackageNameField(ID_LIST_QUERY_PACKAGE_NAME)).setComment("一覧問合せのパッケージ"));
		this.addField((new QueryOrTableClassNameField(ID_LIST_QUERY_CLASS_NAME))
			.setPackageNameFieldId(ID_LIST_QUERY_PACKAGE_NAME))
			.setAutocomplete(true)
			.setRelationDataAcquisition(true)
			.setCalcEventField(true);
		this.addField(new TextField(ID_LIST_QUERY_CONFIG));	// 一覧問合せの設定情報
		this.addField(new EditFormClassNameField());
		this.addField(new OverwriteModeField(ID_EDIT_FORM_CLASS_OVERWRITE_MODE));
		//
		this.addField((new FunctionSelectField(ID_EDIT_QUERY_FUNCTION_SELECT))
			.setPackageFieldId(ID_EDIT_QUERY_PACKAGE_NAME)
			.setComment("単一レコード取得用問合せの機能"));
		this.addField((new PackageNameField(ID_EDIT_QUERY_PACKAGE_NAME))
			.setComment("単一レコード取得用問合せのパッケージ"));
		this.addField((new QueryOrTableClassNameField(ID_EDIT_QUERY_CLASS_NAME))
			.setPackageNameFieldId(ID_EDIT_QUERY_PACKAGE_NAME))
			.setCalcEventField(true)
			.setAutocomplete(true)
			.setRelationDataAcquisition(true)
			.setCalcEventField(true);
		this.addField(new TextField(ID_EDIT_QUERY_CONFIG));	// 編集対象取得問合せの設定情報
		//
		{
			FieldList flist = new FieldList();
			flist.addField(new FunctionSelectField());
			flist.addField(new PackageNameField());
			flist.addField(new QueryOrTableClassNameField(ID_QUERY_CLASS_NAME))
				.setAutocomplete(true)
				.setRelationDataAcquisition(true);
			flist.addField(new TextField(ID_QUERY_CONFIG));
			EditableHtmlTable list = new EditableHtmlTable(ID_MULTI_RECORD_QUERY_LIST, flist);
			this.addHtmlTable(list);
		}
	}

	@Override
	public void init() throws Exception {
		super.init();
		this.setFormData(ID_JAVA_SOURCE_PATH, DeveloperPage.getJavaSourcePath());
		this.setFormData(ID_PAGE_CLASS_OVERWRITE_MODE, OverwriteModeField.ERROR);
		this.setFormData(ID_DAO_CLASS_OVERWRITE_MODE, OverwriteModeField.ERROR);
		this.setFormData(ID_FORM_CLASS_OVERWRITE_MODE, OverwriteModeField.ERROR);
		this.setFormData(ID_QUERY_FORM_CLASS_OVERWRITE_MODE, OverwriteModeField.ERROR);
		this.setFormData(ID_QUERY_RESULT_FORM_CLASS_OVERWRITE_MODE, OverwriteModeField.ERROR);
		this.setFormData(ID_EDIT_FORM_CLASS_OVERWRITE_MODE, OverwriteModeField.ERROR);
		this.setFormData(ID_PAGE_PATTERN, "p2111");
	}

	@Override
	protected Map<String, Object> queryData(final Map<String, Object> data) throws Exception {

		String pkg = (String) data.get(ID_PACKAGE_NAME);
		String cls = (String) data.get(ID_PAGE_CLASS_NAME);
		String classname = pkg + "." + cls;

		logger.debug("classname=" + classname);
		logger.debug("data=" + JsonUtil.encode(data, true));
		
		
		Class<?> clazz = Class.forName(classname);
		Page p = (Page) clazz.getDeclaredConstructor().newInstance();
		PageClassInfo pi = new PageClassInfo(p);
		// Class<? extends Table> tblcls = pi.getTableClass();
		Class<? extends Dao> daocls = pi.getDaoClass();
		String functionPath = pi.getFunctionPath();
		if (functionPath == null) {
			throw new ApplicationException(this.getPage(), "error.notgeneratedpage");
		}
		Map<String, Object> ret = new HashMap<String, Object>();
		ret.putAll(data);
		ret.put(ID_JAVA_SOURCE_PATH, DeveloperPage.getJavaSourcePath());
		if (daocls != null) {
			if (daocls.getName().equals(jp.dataforms.fw.dao.Dao.class.getName())) {
				ret.put(ID_DAO_PACKAGE_NAME, "");
				ret.put(ID_DAO_CLASS_NAME, "");
			} else {
				ret.put(ID_DAO_PACKAGE_NAME, ClassNameUtil.getPackageName(daocls.getName()));
				ret.put(ID_DAO_CLASS_NAME, daocls.getSimpleName());
				Dao dao = daocls.getConstructor().newInstance();
				if (dao instanceof QuerySetDao) {
					this.getQueryInfo(dao, p, ret);
				}
			}
		} else {
			ret.put(ID_DAO_PACKAGE_NAME, "");
			ret.put(ID_DAO_CLASS_NAME, "");
		}
		ret.put(ID_PAGE_CLASS_OVERWRITE_MODE, OverwriteModeField.ERROR);
		ret.put(ID_FUNCTION_SELECT, functionPath);

		ret.put(ID_PAGE_NAME, p.getPageName());
		ret.put(ID_DESCRIPTION, p.getPageDescription());
		ret.put(ID_PACKAGE_NAME, pkg);

		this.getFormInfo(p, ret);

		ret.put(ID_PAGE_CLASS_OVERWRITE_MODE, OverwriteModeField.ERROR);
		ret.put(ID_DAO_CLASS_OVERWRITE_MODE, OverwriteModeField.ERROR);
		ret.put(ID_FORM_CLASS_OVERWRITE_MODE, OverwriteModeField.ERROR);
		ret.put(ID_QUERY_FORM_CLASS_OVERWRITE_MODE, OverwriteModeField.ERROR);
		ret.put(ID_QUERY_RESULT_FORM_CLASS_OVERWRITE_MODE, OverwriteModeField.ERROR);
		ret.put(ID_EDIT_FORM_CLASS_OVERWRITE_MODE, OverwriteModeField.ERROR);



		return ret;
	}

	/**
	 * 一覧取得問合せのフィールド設定を取得する。
	 * @param flist フィールドリスト。
	 * @param qform 問合せフォーム。
	 * @param qrform 問合せ結果フォーム。
	 * @param dao Daoクラスのインスタンス。
	 * @return 一覧取得問合せのフィールド設定。
	 */
	private List<Map<String, Object>> getListQueryFieldConf(final FieldList flist, final QueryForm qform, final QueryResultForm qrform, final QuerySetDao dao) {
		List<Map<String, Object>> list = SelectFieldHtmlTable.getTableData(flist, "");
		if (qform != null) {
			// QueryFormからMatchTypeを取得する。
			FieldList qflist = qform.getFieldList();
			for (Map<String, Object> m: list) {
				String fid = (String) m.get(SelectFieldHtmlTable.ID_FIELD_ID);
				Field<?> qf = qflist.get(fid);
				if (qf == null) {
					qf = qflist.get(fid + "From");
				}
				if (qf == null) {
					m.put(SelectFieldHtmlTable.ID_MATCH_TYPE, MatchType.NONE);
				} else {
					m.put(SelectFieldHtmlTable.ID_MATCH_TYPE, qf.getMatchType());
				}
			}
		}
		if (qrform != null) {
			// 検索結果Formのフィールド表示情報を取得する。
			HtmlTable table = (HtmlTable) qrform.getComponent(Page.ID_QUERY_RESULT);
			if (table != null) {
				FieldList qrflist = table.getFieldList();
				for (Map<String, Object> m: list) {
					String fid = (String) m.get(SelectFieldHtmlTable.ID_FIELD_ID);
					Field<?> qrf = qrflist.get(fid);
					if (qrf == null) {
						m.put(SelectFieldHtmlTable.ID_LIST_FIELD_DISPLAY, Field.Display.NONE);
					} else {
						m.put(SelectFieldHtmlTable.ID_LIST_FIELD_DISPLAY, qrf.getQueryResultFormDisplay());
					}
				}
			}
		}
		if (dao != null) {
			// 編集対象を特定するキーを読み込む。
			FieldList keyList = dao.getMultiRecordQueryKeyList();
			if (keyList != null) {
				for (Map<String, Object> m: list) {
					String fid = (String) m.get(SelectFieldHtmlTable.ID_FIELD_ID);
					Field<?> kf = keyList.get(fid);
					if (kf != null) {
						Field.Display disp = (Field.Display) m.get(SelectFieldHtmlTable.ID_LIST_FIELD_DISPLAY);
						if (disp != Field.Display.INPUT_HIDDEN) {
							m.put(SelectFieldHtmlTable.ID_LIST_FIELD_DISPLAY, Field.Display.INPUT_READONLY);
						}
						m.put(SelectFieldHtmlTable.ID_EDIT_KEY, "1");
					}
				}
			}
		}
		return list;
	}

	/**
	 * 編集対象取得問合せのフィールド設定を取得する。
	 * @param query 問合せ。
	 * @param eform 編集フォーム。
	 * @return 編集問合せのフィールド設定。
	 */
	private List<Map<String, Object>> getEditQueryFieldConf(final Query query, final EditForm eform) {
		FieldList flist = query.getFieldList();
		List<Map<String, Object>> list = SelectFieldHtmlTable.getTableData(flist, "");
		if (eform != null) {
			String tableId = query.getListId();
			FieldList eflist = null;
			WebComponent comp = eform.getComponent(tableId);
			if (comp instanceof HtmlTable) {
				eflist = ((HtmlTable) comp).getFieldList();
			}
			if (eflist == null) {
				eflist = eform.getFieldList();
			}
			for (Map<String, Object> m: list) {
				String fid = (String) m.get(SelectFieldHtmlTable.ID_FIELD_ID);
				Field<?> ef = eflist.get(fid);
				if (ef == null) {
					m.put(SelectFieldHtmlTable.ID_EDIT_FIELD_DISPLAY, Field.Display.NONE);
				} else {
					m.put(SelectFieldHtmlTable.ID_EDIT_FIELD_DISPLAY, ef.getEditFormDisplay());
				}
			}
		}
		return list;
	}

	/**
	 * 編集対象を特定するキーを設定する。
	 * @param dao DAOのインスタンス。
	 * @param list フィールドの設定情報。
	 */
	private void setEditKey(final QuerySetDao dao, final List<Map<String, Object>> list) {
		FieldList kflist = dao.getEditFormKeyList();
		if (kflist != null) {
			for (Map<String, Object> m: list) {
				String id = (String) m.get(SelectFieldHtmlTable.ID_FIELD_ID);
				if (kflist.get(id) != null) {
					m.put(SelectFieldHtmlTable.ID_EDIT_KEY, "1");
				} else {
					m.put(SelectFieldHtmlTable.ID_EDIT_KEY, "0");
				}
			}
		}
	}

	/**
	 * 問合せ情報を設定する。
	 * @param dao DAOクラスのインスタンス。
	 * @param p ページクラスのインスタンス。
	 * @param ret フォームに表示するデータマップ。
	 */
	private void getQueryInfo(final Dao dao, final Page p, final Map<String, Object> ret) {
		QuerySetDao querySetDao = (QuerySetDao) dao;
		Query listQuery = querySetDao.getListQuery();
		if (listQuery != null) {
			logger.debug("listQuery package=" + listQuery.getClass().getPackageName());
			logger.debug("listQuery class=" + listQuery.getClass().getName());
			if (listQuery instanceof SingleTableQuery) {
				ret.put(ID_LIST_QUERY_PACKAGE_NAME, ((SingleTableQuery) listQuery).getMainTable().getClass().getPackageName());
				ret.put(ID_LIST_QUERY_CLASS_NAME, ((SingleTableQuery) listQuery).getMainTable().getClass().getSimpleName());
			} else {
				ret.put(ID_LIST_QUERY_PACKAGE_NAME, listQuery.getClass().getPackageName());
				ret.put(ID_LIST_QUERY_CLASS_NAME, listQuery.getClass().getSimpleName());
			}
			FieldList flist = listQuery.getFieldList();
			QueryForm qf = (QueryForm) p.getComponent(Page.ID_QUERY_FORM);
			QueryResultForm qrf = (QueryResultForm) p.getComponent(Page.ID_QUERY_RESULT_FORM);
			List<Map<String, Object>> list = this.getListQueryFieldConf(flist, qf, qrf, querySetDao);
			ret.put(ID_LIST_QUERY_CONFIG, JsonUtil.encode(list));

		}
		Query editQuery = querySetDao.getSingleRecordQuery();
		if (editQuery == null) {
			List<Query> list = querySetDao.getMultiRecordQueryList();
			if (list != null && list.size() > 0) {
				editQuery = list.get(0);
			}
		}
		if (editQuery != null) {
			if (editQuery instanceof SingleTableQuery) {
				ret.put(ID_EDIT_QUERY_PACKAGE_NAME, ((SingleTableQuery) editQuery).getMainTable().getClass().getPackageName());
				ret.put(ID_EDIT_QUERY_CLASS_NAME, ((SingleTableQuery) editQuery).getMainTable().getClass().getSimpleName());
			} else {
				ret.put(ID_EDIT_QUERY_PACKAGE_NAME, editQuery.getClass().getPackageName());
				ret.put(ID_EDIT_QUERY_CLASS_NAME, editQuery.getClass().getSimpleName());
			}
			EditForm ef = (EditForm) p.getComponent(Page.ID_EDIT_FORM);
			List<Map<String, Object>> list = this.getEditQueryFieldConf(editQuery, ef);
			this.setEditKey(querySetDao, list);
			ret.put(ID_EDIT_QUERY_CONFIG, JsonUtil.encode(list));
			List<Query> qlist = querySetDao.getMultiRecordQueryList();
			List<Map<String, Object>> multiRecordQueryList = new ArrayList<Map<String, Object>>();
			if (qlist != null && qlist.size() > 0) {
				for (Query q: qlist) {
					String pkg = q.getClass().getPackageName();
					String cls = q.getClass().getSimpleName();
					if (q instanceof SingleTableQuery) {
						pkg = ((SingleTableQuery) q).getMainTable().getClass().getPackageName();
						cls = ((SingleTableQuery) q).getMainTable().getClass().getSimpleName();
					}
					Map<String, Object> m = new HashMap<String, Object>();
					m.put(ID_PACKAGE_NAME, pkg);
					m.put(ID_QUERY_CLASS_NAME, cls);
					List<Map<String, Object>> mflist = this.getEditQueryFieldConf(q, ef);
					m.put(ID_QUERY_CONFIG, JsonUtil.encode(mflist));
					multiRecordQueryList.add(m);
				}
				ret.put(ID_MULTI_RECORD_QUERY_LIST, multiRecordQueryList);
			}
		}
	}

	/**
	 * フォームの情報を設定する。
	 * @param p ページクラスのインスタンス。
	 * @param ret フォームに表示するデータマップ。
	 */
	private void getFormInfo(final Page p, final Map<String, Object> ret) throws Exception {
		Map<String, WebComponent> fm = p.getFormMap();
		String pkg = (String) ret.get(ID_PACKAGE_NAME);
		for (String key: fm.keySet()) {
			WebComponent cmp = fm.get(key);
			if (cmp != null) {
				if (cmp instanceof QueryForm) {
					ret.put(ID_QUERY_FORM_CLASS_NAME, cmp.getClass().getSimpleName());
					ret.put(ID_QUERY_FORM_CLASS_OVERWRITE_MODE, OverwriteModeField.ERROR);
				} else if (cmp instanceof QueryResultForm) {
					ret.put(ID_QUERY_RESULT_FORM_CLASS_NAME, cmp.getClass().getSimpleName());
					ret.put(ID_QUERY_RESULT_FORM_CLASS_OVERWRITE_MODE, OverwriteModeField.ERROR);
				} else if (cmp instanceof EditForm) {
					ret.put(ID_EDIT_FORM_CLASS_NAME, cmp.getClass().getSimpleName());
					ret.put(ID_EDIT_FORM_CLASS_OVERWRITE_MODE, OverwriteModeField.ERROR);
				} else if (cmp instanceof LoginInfoForm || cmp instanceof SideMenuForm) {
					;
				} else if (cmp instanceof Form){
					if (cmp.getClass().getPackageName().equals(pkg)) {
						ret.put(ID_FORM_CLASS_NAME, cmp.getClass().getSimpleName());
						ret.put(ID_FORM_CLASS_OVERWRITE_MODE, OverwriteModeField.ERROR);
					}
				}
			}
		}
		ret.put(ID_PAGE_PATTERN, p.getPagePattern());
	}

	@Override
	protected List<ValidationError> validateForm(Map<String, Object> data) throws Exception {
		List<ValidationError> list = super.validateForm(data);
		if (list.size() == 0) {
			{
				String path = (String) data.get(ID_JAVA_SOURCE_PATH);
				String packageName = (String) data.get(ID_DAO_PACKAGE_NAME);
				String daoClassName = (String) data.get(ID_DAO_CLASS_NAME);
				String daoclass = packageName + "." + daoClassName;
				String srcPath = path + "/" + daoclass.replaceAll("\\.", "/") + ".java";
				String overwriteMode = (String) data.get(ID_DAO_CLASS_OVERWRITE_MODE);
				if (OverwriteModeField.ERROR.equals(overwriteMode)) {
					File tbl = new File(srcPath);
					if (tbl.exists()) {
						list.add(new ValidationError(ID_DAO_CLASS_NAME, this.getPage().getMessage("error.sourcefileexist", daoClassName + ".java")));
					}
				}
			}
			{
				String javaSrc = (String) data.get(ID_JAVA_SOURCE_PATH);
				String packageName = (String) data.get(ID_PACKAGE_NAME);
				String queryFormClassName = (String) data.get(ID_QUERY_FORM_CLASS_NAME);
				String queryResultFormClassName = (String) data.get(ID_QUERY_RESULT_FORM_CLASS_NAME);
				String editFormClassName = (String) data.get(ID_EDIT_FORM_CLASS_NAME);

				String pageClassName = (String) data.get(ID_PAGE_CLASS_NAME);
				String srcPath = javaSrc + "/" + packageName.replaceAll("\\.", "/");
				String pageClassOverwriteMode = (String) data.get(ID_PAGE_CLASS_OVERWRITE_MODE);
				File f3 = new File(srcPath + "/" + pageClassName + ".java");
				if (OverwriteModeField.ERROR.equals(pageClassOverwriteMode)) {
					if (f3.exists()) {
						list.add(new ValidationError(ID_PAGE_CLASS_NAME, this.getPage().getMessage("error.sourcefileexist", pageClassName + ".java")));
					}
				}

				String queryFormClassOverwriteMode = (String) data.get(ID_QUERY_FORM_CLASS_OVERWRITE_MODE);
				if (OverwriteModeField.ERROR.equals(queryFormClassOverwriteMode)) {
					File f0 = new File(srcPath + "/" + queryFormClassName + ".java");
					if (f0.exists()) {
						list.add(new ValidationError(ID_QUERY_FORM_CLASS_NAME, this.getPage().getMessage("error.sourcefileexist", queryFormClassName + ".java")));
					}
				}
				String queryResultFormClassOverwriteMode = (String) data.get(ID_QUERY_RESULT_FORM_CLASS_OVERWRITE_MODE);
				if (OverwriteModeField.ERROR.equals(queryResultFormClassOverwriteMode)) {
					File f1 = new File(srcPath + "/" + queryResultFormClassName + ".java");
					if (f1.exists()) {
						list.add(new ValidationError(ID_QUERY_RESULT_FORM_CLASS_NAME, this.getPage().getMessage("error.sourcefileexist", queryResultFormClassName + ".java")));
					}
				}
				String editFormClassOverwriteMode = (String) data.get(ID_EDIT_FORM_CLASS_OVERWRITE_MODE);
				if (OverwriteModeField.ERROR.equals(editFormClassOverwriteMode)) {
					File f2 = new File(srcPath + "/" + editFormClassName + ".java");
					if (f2.exists()) {
						list.add(new ValidationError(ID_EDIT_FORM_CLASS_NAME, this.getPage().getMessage("error.sourcefileexist", editFormClassName + ".java")));
					}
				}
			}

		}
		return list;
	}



	@Override
	protected boolean isUpdate(final Map<String, Object> data) throws Exception {
		return false;
	}


	@Override
	public List<ValidationError> validate(final Map<String, Object> param) throws Exception {
		List<ValidationError> ret =  super.validate(param);
		if (ret.size() == 0) {

		}
		return ret;

	}



	@Override
	protected void insertData(Map<String, Object> data) throws Exception {
		String pagePattern = (String) data.get(ID_PAGE_PATTERN);
		String qf = PagePatternSelectField.getQueryFormFlag(pagePattern);
		String qrf = PagePatternSelectField.getQueryResultFormFlag(pagePattern);
		String ef = PagePatternSelectField.getEditFormFlag(pagePattern);
		logger.debug("qf={}, qrf={}, ef={}", qf, qrf, ef);
		if ("0".equals(qf) && "0".equals(qrf) && "0".equals(ef)) {
			SimpleFormGenerator fgen = new SimpleFormGenerator();
			fgen.generage(data);
			SimplePageGenerator pgen = new SimplePageGenerator();
			pgen.generage(data);
		} else {
			QuerySetDaoGenerator gen = new QuerySetDaoGenerator();
			gen.generage(data);
			if ("1".equals(qf)) {
				QueryFormGenerator qfgen = new QueryFormGenerator();
				qfgen.generage(data);
			}
			if ("1".equals(qrf)) {
				QueryResultFormGenerator qrfgen = new QueryResultFormGenerator();
				qrfgen.generage(data);
			}
			if ("1".equals(ef) || "2".equals(ef)) {
				EditFormGenerator efgen = new EditFormGenerator();
				efgen.generage(data);
			}
			DbPageGenerator pgen = new DbPageGenerator();
			pgen.generage(data);
		}
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

	/**
	 * フィールドのデフォルト設定情報を取得する。
	 * @param p パラメータ。
	 * @return フィールドのデフォルト設定情報。
	 * @throws Exception 例外。
	 */
	@WebMethod
	public Response getFieldConfig(final Map<String, Object> p) throws Exception  {
		String cls = (String) p.get("c");
		FieldList flist = SelectFieldHtmlTable.getFieldList(cls);
		List<Map<String, Object>> list = SelectFieldHtmlTable.getTableData(flist, "");
		return new JsonResponse(JsonResponse.SUCCESS, list);
	}
}
