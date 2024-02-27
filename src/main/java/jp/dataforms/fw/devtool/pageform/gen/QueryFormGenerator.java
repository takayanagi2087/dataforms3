package jp.dataforms.fw.devtool.pageform.gen;

import java.util.Map;

import jp.dataforms.fw.dao.Table;
import jp.dataforms.fw.devtool.pageform.page.DaoAndPageGeneratorEditForm;
import jp.dataforms.fw.field.base.Field;
import jp.dataforms.fw.field.base.Field.MatchType;
import jp.dataforms.fw.field.base.FieldList;
import jp.dataforms.fw.util.ImportUtil;
import jp.dataforms.fw.util.StringUtil;

/**
 * ページジェネレータ。
 */
public class QueryFormGenerator extends FormSrcGenerator {
	/**
	 * Logger.
	 */
//	private static Logger logger = LogManager.getLogger(QueryFormGenerator.class);

	/**
	 * コンストラクタ。
	 */
	public QueryFormGenerator() {

	}


	/**
	 * 編集対象取得問合せのフィールドリストを取得します。
	 * @param data POSTされたデータ。
	 * @return 編集対象取得問合せのフィールドリスト。
	 * @throws Exception 例外。
	 */
	protected FieldList getEditQueryFieldList(final Map<String, Object> data) throws Exception {
		String pkg = (String) data.get(DaoAndPageGeneratorEditForm.ID_EDIT_QUERY_PACKAGE_NAME);
		String cls = (String) data.get(DaoAndPageGeneratorEditForm.ID_EDIT_QUERY_CLASS_NAME);
		return getFieldList(pkg, cls);
	}

	/**
	 * 条件フィールドの追加処理コードを作成します。
	 * @param data POSTデータ。
	 * @param implist インポートリスト。
	 * @return 条件フィールドの追加処理コード。
	 * @throws Exception 例外。
	 */
	private String getQueryFormFieldList(final Map<String, Object> data, final ImportUtil implist) throws Exception {
		implist.add("java.util.List");
		implist.add("java.util.Map");
		implist.add("dataforms.report.ExportDataFile");
		implist.add("dataforms.field.base.FieldList");
		implist.add("dataforms.field.base.Field.MatchType");

		FieldList flist = this.getQueryFormFieldList(data);
		StringBuilder sb = new StringBuilder();
		String qscn = null;
		for (Field<?> f: flist) {
			Table tbl = f.getTable();
			String scn = tbl.getClass().getSimpleName();
			if (qscn != null) {
				scn = qscn;
			} else {
				implist.add(tbl.getClass());
			}
			MatchType type = f.getMatchType();
			if (type == MatchType.NONE) {
				;
			} else if (type == MatchType.RANGE_FROM) {
				String id = f.getId();
				String cname = f.getClass().getSimpleName();
				String cmt = f.getComment();
				sb.append("\t\tthis.addField(new " + cname + "(" + scn + ".Entity.ID_" + StringUtil.camelToUpperCaseSnake(id) + " + \"From\")).setMatchType(MatchType.RANGE_FROM).setComment(\"" + cmt + "(from)\");\n");
				sb.append("\t\tthis.addField(new " + cname + "(" + scn + ".Entity.ID_" + StringUtil.camelToUpperCaseSnake(id) + " + \"To\")).setMatchType(MatchType.RANGE_TO).setComment(\"" + cmt + "(to)\");\n");
				implist.add(f.getClass());
			} else {
				String id = f.getId();
				String mt = type.name();
				String cname = f.getClass().getSimpleName();
				String cmt = f.getComment();
				sb.append("\t\tthis.addField(new " + cname + "(" + scn + ".Entity.ID_" + StringUtil.camelToUpperCaseSnake(id) + ")).setMatchType(MatchType." + mt + ").setComment(\"" + cmt + "\");\n");
				implist.add(f.getClass());
			}
		}
		return sb.toString();
	}

	/**
	 * 問合せフォームのテンプレートを取得します。
	 */
	@Override
	protected Template getTemplate() throws Exception {
		Template tmp = new Template(this.getClass(), "template/QueryForm.java.template");
		return tmp;
	}

	/**
	 * 問合せフォームクラス名を取得します。
	 */
	@Override
	protected String getFormClassName(final Map<String, Object> data) {
		String formClassName = (String) data.get(DaoAndPageGeneratorEditForm.ID_QUERY_FORM_CLASS_NAME);
		return formClassName;
	}


	/**
	 * 問合せフォーム用のコンポーネントの配置処理を実装します。
	 */
	@Override
	protected void setFormComponent(Template tmp, String formClassName, Map<String, Object> data) throws Exception {
		tmp.replace(DaoAndPageGeneratorEditForm.ID_QUERY_FORM_CLASS_NAME, formClassName);
		ImportUtil implist = new ImportUtil();
		String queryFormFieldList = this.getQueryFormFieldList(data, implist);
		tmp.replace("queryFormFieldList", queryFormFieldList);
		tmp.replace("queryFormImportList", implist.getImportText());
	}

}
