package jp.dataforms.fw.devtool.dao.gen;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jp.dataforms.fw.controller.Form;
import jp.dataforms.fw.dao.Query;
import jp.dataforms.fw.dao.SingleTableQuery;
import jp.dataforms.fw.dao.Table;
import jp.dataforms.fw.devtool.field.PagePatternSelectField;
import jp.dataforms.fw.devtool.javasrc.JavaSrcGenerator;
import jp.dataforms.fw.devtool.pageform.page.DaoAndPageGeneratorEditForm;
import jp.dataforms.fw.devtool.query.page.SelectFieldHtmlTable;
import jp.dataforms.fw.field.base.Field;
import jp.dataforms.fw.servlet.DataFormsServlet;
import jp.dataforms.fw.util.FileUtil;
import jp.dataforms.fw.util.ImportUtil;
import jp.dataforms.fw.util.JsonUtil;
import jp.dataforms.fw.util.StringUtil;

/**
 * QuerySetDao生成クラス。
 */
public class QuerySetDaoGenerator extends JavaSrcGenerator {

	/**
	 * Logger.
	 */
	private static Logger logger = LogManager.getLogger(QuerySetDaoGenerator.class);

	/**
	 * コンストラクタ。
	 */
	public QuerySetDaoGenerator() {
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
 		Template tmp = new Template(src);
		tmp.replace("className", cls.getSimpleName());
		tmp.replace("variableName", id + "Field");
		tmp.replace("getterName", "get" + StringUtil.firstLetterToUpperCase(id) + "Field");
		tmp.replace("comment", comment);
		return tmp.getSource();
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
 		Template tmp = new Template(src);
		tmp.replace("className", tableClassName);
		tmp.replace("variableName", StringUtil.firstLetterToLowerCase(tableClassName));
		tmp.replace("comment", comment);
		return tmp.getSource();
	}

	/**
	 * 一覧取得問合せ設定情報を取得します。
	 * @param data POSTされたデータ。
	 * @return 一覧取得問合せ設定情報。
	 */
/*	private List<Map<String, Object>> getListQueryConfig(final Map<String, Object> data) {
		String json = (String) data.get(DaoAndPageGeneratorEditForm.ID_LIST_QUERY_CONFIG);
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> list = JSON.decode(json, ArrayList.class);
		return list;
	}
*/
	/**
	 * 編集対象取得問合せ設定情報を取得します。
	 * @param data POSTされたデータ。
	 * @return 編集対象取得問合せ設定情報。
	 */
	private List<Map<String, Object>> getEditQueryConfig(final Map<String, Object> data) {
		String json = (String) data.get(DaoAndPageGeneratorEditForm.ID_EDIT_QUERY_CONFIG);
		logger.debug("*** editQueryConfig=" + json);
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> list = (List<Map<String, Object>>) JsonUtil.decode(json, ArrayList.class);
		return list;
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
			String packageName = (String) data.get(DaoAndPageGeneratorEditForm.ID_LIST_QUERY_PACKAGE_NAME);
			String className = (String) data.get(DaoAndPageGeneratorEditForm.ID_LIST_QUERY_CLASS_NAME);
			String fullClassName = packageName + "." + className;
			if (!StringUtil.isBlank(className)) {
				if (!set.contains(fullClassName)) {
					sb.append(this.getProperty(packageName, className));
					set.add(fullClassName);
				}
			}
		}
		{
			String packageName = (String) data.get(DaoAndPageGeneratorEditForm.ID_EDIT_QUERY_PACKAGE_NAME);
			String className = (String) data.get(DaoAndPageGeneratorEditForm.ID_EDIT_QUERY_CLASS_NAME);
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
			List<Map<String, Object>> list = (List<Map<String, Object>>) data.get(DaoAndPageGeneratorEditForm.ID_MULTI_RECORD_QUERY_LIST);
			for (Map<String, Object> m: list) {
				String packageName = (String) m.get(DaoAndPageGeneratorEditForm.ID_PACKAGE_NAME);
				String className = (String) m.get(DaoAndPageGeneratorEditForm.ID_QUERY_CLASS_NAME);
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
			if (!this.isAllRecordEditForm(data)) {
				String pagePattern = (String) data.get(DaoAndPageGeneratorEditForm.ID_PAGE_PATTERN);
				String ef = PagePatternSelectField.getEditFormFlag(pagePattern);
				if ("2".equals(ef)) {
					List<Map<String, Object>> list = this.getEditQueryConfig(data);
					for (Map<String, Object> m: list) {
						String sel = (String) m.get(SelectFieldHtmlTable.ID_EDIT_KEY);
						if ("1".equals(sel)) {
							String fieldId = (String) m.get(SelectFieldHtmlTable.ID_FIELD_ID);
							String fullClassName = (String) m.get(SelectFieldHtmlTable.ID_FIELD_CLASS_NAME);
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
			}
		}
		return sb.toString();
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
	 * 1レコード編集フォーム用DAOソース生成を行います。
	 * @param data フォームデータ。
	 * @param implist インポートリスト。
	 * @param javasrc javaソーステキスト。
	 * @return javaソーステキスト。
	 * @throws Exception 例外。
	 */
	private String singleRecordEditForm(final Map<String, Object> data, final ImportUtil implist, String javasrc) throws Exception {
		Template tmp = new Template(javasrc);
		String p = (String) data.get(DaoAndPageGeneratorEditForm.ID_DAO_PACKAGE_NAME);
		{
			String listQueryPackage = (String) data.get(DaoAndPageGeneratorEditForm.ID_LIST_QUERY_PACKAGE_NAME);
			String listQueryClass = (String) data.get(DaoAndPageGeneratorEditForm.ID_LIST_QUERY_CLASS_NAME);
			String queryPackage = (String) data.get(DaoAndPageGeneratorEditForm.ID_EDIT_QUERY_PACKAGE_NAME);
			String queryClass = (String) data.get(DaoAndPageGeneratorEditForm.ID_EDIT_QUERY_CLASS_NAME);
			if (!StringUtil.isBlank(queryClass)) {
				if (!StringUtil.isBlank(queryClass)) {
					if (!p.equals(queryPackage)) {
						implist.add(queryPackage + "." + queryClass);
					}
					logger.debug("listQuery=" + listQueryPackage + "," + listQueryClass);
					logger.debug("editQuery=" + queryPackage + "," + queryClass);
					if (queryPackage.equals(listQueryPackage) && queryClass.equals(listQueryClass)) {
						tmp.replace("singleRecordQuery", "this." + StringUtil.firstLetterToLowerCase(queryClass));
					} else {
						tmp.replace("singleRecordQuery", "this." + StringUtil.firstLetterToLowerCase(queryClass) + " = new " + queryClass + "()");
					}
				} else {
					implist.add(Query.class.getName());
					tmp.replace("singleRecordQuery", "(Query) null");
				}
			}
			if (!StringUtil.isBlank(queryClass)) {
				String className = queryPackage + "." + queryClass;
				Table mainTable = this.getMainTable(className);
				tmp.replace("mainTable", mainTable.getClass().getSimpleName());
			} else {
				implist.add(Table.class.getName());
				tmp.replace("mainTable", "Table");
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
		tmp.replace("addMultiRecordQueryList", sb.toString());
		return tmp.getSource();
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
		List<Map<String, Object>> list = this.getEditQueryConfig(data);

		Class<?> cls = Class.forName(packagename + "." + classname);
		if (Table.class.isAssignableFrom(cls)) {
			sb.append("\t\tQuery query = new SingleTableQuery(new " + classname + "());\n");
		} else {
			sb.append("\t\tQuery query = new " + classname + "();\n");
		}
		sb.append("\t\tthis.setMultiRecordQueryKeyList(new FieldList(\n");
		StringBuilder fsb = new StringBuilder();
		for (Map<String, Object> m: list) {
			String fieldId = (String) m.get(SelectFieldHtmlTable.ID_FIELD_ID);
			String sel = (String) m.get(SelectFieldHtmlTable.ID_EDIT_KEY);
			logger.debug("fieldId={}, sel={}", fieldId, sel);
			if ("1".equals(sel)) {
				if (fsb.length() > 0) {
					fsb.append("\t\t\t, ");
				} else {
					fsb.append("\t\t\t");
				}
				String tbl = (String) m.get(SelectFieldHtmlTable.ID_TABLE_CLASS_NAME);
				String fieldClassName = (String) m.get(SelectFieldHtmlTable.ID_FIELD_CLASS_NAME);
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
	 * 全レコード編集フォームかどうかの判定を行います。
	 * @param data POSTデータ。
	 * @return 全レコード編集フォームの場合true;
	 */
	private boolean isAllRecordEditForm(final Map<String, Object> data) {
		String pagePattern = (String) data.get(DaoAndPageGeneratorEditForm.ID_PAGE_PATTERN);
		String qf = PagePatternSelectField.getQueryFormFlag(pagePattern);
		String qrf = PagePatternSelectField.getQueryResultFormFlag(pagePattern);
		String ef = PagePatternSelectField.getEditFormFlag(pagePattern);
		if ("0".equals(qf) && "0".equals(qrf) && "2".equals(ef)) {
			return true; //
		} else {
			return false;
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
		Template tmp = new Template(javasrc);
		String p = (String) data.get(DaoAndPageGeneratorEditForm.ID_PACKAGE_NAME);
		String packagename = (String) data.get(DaoAndPageGeneratorEditForm.ID_EDIT_QUERY_PACKAGE_NAME);
		String classname = (String) data.get(DaoAndPageGeneratorEditForm.ID_EDIT_QUERY_CLASS_NAME);

		String src = "\t\tthis.addMultiRecordQueryList(this." + StringUtil.firstLetterToLowerCase(classname) + " = new " + classname + "());\n";
		if (!this.isAllRecordEditForm(data) ) {
			src += this.getKeyListSource(data, implist, packagename, classname);
		}
		tmp.replace("addMultiRecordQueryList", src);
		implist.add(Query.class.getName());
		tmp.replace("singleRecordQuery", "(Query) null");
		if (!p.equals(packagename)) {
			implist.add(packagename + "." + classname);
		}

		Table mainTable = this.getMainTable(packagename + "." + classname);
		tmp.replace("mainTable", mainTable.getClass().getSimpleName());
		return tmp.getSource();
	}


	@Override
	protected Template getTemplate() throws Exception {
		Template tmp = new Template(this.getClass(), "../page/template/QuerySetDao.java.template");
		return tmp;
	}

	/**
	 * ソースを生成します。
	 * @param form フォーム。
	 * @param data POSTされたデータ。
	 * @throws Exception 例型。
	 */
	public void generage(final Form form, final Map<String, Object> data) throws Exception {
/*		List<Map<String, Object>> list = this.getListQueryConfig(data);
		logger.debug("fieldList=" + list.getClass().getName());
		for (int i = 0; i < list.size(); i++) {
			logger.debug("fieldInfo=" + JSON.encode(list.get(i)));
		}*/
//		String javasrc = this.getStringResourse(this.getClass(), "../page/template/QuerySetDao.java.template");
		Template tmp = this.getTemplate(); //new Template(this.getClass(), "../page/template/QuerySetDao.java.template");
		//logger.debug("template=" + javasrc);
		String packageName = (String) data.get(DaoAndPageGeneratorEditForm.ID_DAO_PACKAGE_NAME);
		String daoClassName = (String) data.get(DaoAndPageGeneratorEditForm.ID_DAO_CLASS_NAME);
		ImportUtil implist = new ImportUtil(packageName);
		tmp.replace("packageName", packageName);
		tmp.replace("daoClassName", daoClassName);
		tmp.replace("properties", this.getProperties(data, implist));

		String daoclass = packageName + "." + daoClassName;
		String comment = (String) data.get(DaoAndPageGeneratorEditForm.ID_PAGE_NAME) + "用DAOクラス";
		logger.debug("comment=" + comment);
		tmp.replace("comment", comment);
		{
			String queryPackage = (String) data.get("listQueryPackageName");
			String queryClass = (String) data.get("listQueryClassName");
			if (!StringUtil.isBlank(queryClass)) {
				if (!packageName.equals(queryPackage)) {
					String qc = queryPackage + "." + queryClass;
					implist.add(qc);
				}
				tmp.replace("listQuery", "this." + StringUtil.firstLetterToLowerCase(queryClass) + " = new " + queryClass + "()");
			} else {
				implist.add(Query.class.getName());
				tmp.replace("listQuery", "(Query) null");
			}
		}

		String javasrc = null;
		String pagePattern = (String) data.get(DaoAndPageGeneratorEditForm.ID_PAGE_PATTERN);
		String ef = PagePatternSelectField.getEditFormFlag(pagePattern);

		logger.debug("*** editFormFlag=" + ef);

		if ("0".equals(ef)) {
			tmp.replace("singleRecordQuery", "(Query) null");
			tmp.replace("addMultiRecordQueryList", "");
			tmp.replace("mainTable", "Table");
			implist.add(jp.dataforms.fw.dao.Query.class);
			implist.add(jp.dataforms.fw.dao.Table.class);
			javasrc = tmp.getSource();
		} else if ("1".equals(ef)) {
			javasrc = tmp.getSource();
			javasrc = this.singleRecordEditForm(data, implist, javasrc);
		} else {
			javasrc = tmp.getSource();
			javasrc = this.multiRecordEditForm(data, implist, javasrc);
		}
		tmp = new Template(javasrc);
		tmp.replace("importTables", implist.getImportText());
		javasrc = tmp.getSource();
		logger.debug("javasrc={}", javasrc);
		String path = (String) data.get(DaoAndPageGeneratorEditForm.ID_JAVA_SOURCE_PATH);
		String srcPath = path + "/" + daoclass.replaceAll("\\.", "/") + ".java";
		logger.debug("srcPath=" + srcPath);
		FileUtil.writeTextFileWithBackup(srcPath, javasrc, DataFormsServlet.getEncoding());
	}
}
