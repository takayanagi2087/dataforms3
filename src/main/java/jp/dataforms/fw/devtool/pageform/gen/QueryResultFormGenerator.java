package jp.dataforms.fw.devtool.pageform.gen;

import java.util.Map;

import jp.dataforms.fw.devtool.pageform.page.DaoAndPageGeneratorEditForm;
import jp.dataforms.fw.field.base.Field;
import jp.dataforms.fw.field.base.Field.Display;
import jp.dataforms.fw.field.base.FieldList;
import jp.dataforms.fw.field.common.DeleteFlagField;
import jp.dataforms.fw.util.ImportUtil;
import jp.dataforms.fw.util.StringUtil;

/**
 * 検索結果フォームのJavaソースジェネレータ。
 */
public class QueryResultFormGenerator extends FormSrcGenerator {

	/**
	 * コンストラクタ。
	 */
	public QueryResultFormGenerator() {
		
	}
	
	/**
	 * 問合せ結果フォームのテンプレートを取得します。
	 */
	@Override
	protected Template getTemplate() throws Exception {
		Template tmp = new Template(this.getClass(), "template/QueryResultForm.java.template");
		return tmp;
	}


	/**
	 * 問合せ結果フォームクラス名を取得します。
	 */
	@Override
	protected String getFormClassName(Map<String, Object> data) {
		String formClassName = (String) data.get(DaoAndPageGeneratorEditForm.ID_QUERY_RESULT_FORM_CLASS_NAME);
		return formClassName;
	}



	/**
	 * 問い合わせ結果フォームのソートカラム指定コードを生成します。
	 * @param data フィールドリスト。
	 * @param implist フィールドのインポートリスト。
	 * @return 生成されたコード。
	 * @throws Exception 例外。
	 */
	private String getQueryResultFormFieldList(final Map<String, Object> data, final ImportUtil implist) throws Exception {
		FieldList flist = this.getQueryFormFieldList(data);
		StringBuilder sb = new StringBuilder();
		String packageName = (String) data.get(DaoAndPageGeneratorEditForm.ID_LIST_QUERY_PACKAGE_NAME);
		String queryClass = (String) data.get(DaoAndPageGeneratorEditForm.ID_LIST_QUERY_CLASS_NAME);
		implist.add(packageName + "." + queryClass);
		for (Field<?> f : flist) {
//			Table tbl = f.getTable();
//			implist.add(tbl.getClass());
			if (f instanceof DeleteFlagField) {
				continue;
			}
			if (f.getQueryResultFormDisplay() == Display.INPUT_HIDDEN) {
				String text = "\t\thtmltable.getFieldList().get(" + queryClass + ".Entity.ID_" + StringUtil.camelToUpperCaseSnake(f.getId()) + ")."
						+ "setQueryResultFormDisplay(Display." + f.getQueryResultFormDisplay().toString() + ");\n";
				sb.append(text);
			} else {
				String text = "\t\thtmltable.getFieldList().get(" + queryClass + ".Entity.ID_" + StringUtil.camelToUpperCaseSnake(f.getId()) + ")."
						+ "setQueryResultFormDisplay(Display." + f.getQueryResultFormDisplay().toString() + ")."
						+ "setSortable(true);\n";
				sb.append(text);
			}
		}
		return sb.toString();
	}


	@Override
	protected void setFormComponent(Template tmp, String formClassName, Map<String, Object> data) throws Exception {
		tmp.replace(DaoAndPageGeneratorEditForm.ID_QUERY_RESULT_FORM_CLASS_NAME, formClassName);
		ImportUtil implist = new ImportUtil();
		String queryFormFieldList = this.getQueryResultFormFieldList(data, implist);
		tmp.replace("queryResultFieldSetting", queryFormFieldList);
		tmp.replace("queryResultFormImportList", implist.getImportText());

	}

}
