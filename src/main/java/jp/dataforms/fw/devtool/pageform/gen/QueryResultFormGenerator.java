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
	 * 問合せ結果フォームのコンポーネントを配置します。
	 * @param htmlSetting HTML出力設定コード。
	 * @param soetSetting ソート設定コード。
	 * @param implist インポートパッケージリスト。
	 */
	private record FieldSetting(String htmlSetting	, String sortSetting, ImportUtil implist) {}

	/**
	 * 問い合わせ結果フォームのソートカラム指定コードを生成します。
	 * @param data フィールドリスト。
	 * @return 生成されたコード。
	 * @throws Exception 例外。
	 */
	private FieldSetting getQueryResultFormFieldList(final Map<String, Object> data) throws Exception {
		FieldList flist = this.getQueryFormFieldList(data);
		StringBuilder sb = new StringBuilder();
		StringBuilder ssb = new StringBuilder();
		String packageName = (String) data.get(DaoAndPageGeneratorEditForm.ID_LIST_QUERY_PACKAGE_NAME);
		String queryClass = (String) data.get(DaoAndPageGeneratorEditForm.ID_LIST_QUERY_CLASS_NAME);
		ImportUtil implist = new ImportUtil();
		implist.add(packageName + "." + queryClass);
		for (Field<?> f : flist) {
//			Table tbl = f.getTable();
//			implist.add(tbl.getClass());
			if (f instanceof DeleteFlagField) {
				continue;
			}
			if (f.getQueryResultFormDefaultDisplay() == f.getQueryResultFormDisplay()) {
				String text = "// \t\thtmltable.getFieldList().get(" + queryClass + ".Entity.ID_" + StringUtil.camelToUpperCaseSnake(f.getId()) + ")."
						+ "setQueryResultFormDisplay(Display." + f.getQueryResultFormDisplay().toString() + "); // " + f.getComment() + "\n";
				sb.append(text);
			} else {
				String text = "\t\thtmltable.getFieldList().get(" + queryClass + ".Entity.ID_" + StringUtil.camelToUpperCaseSnake(f.getId()) + ")."
						+ "setQueryResultFormDisplay(Display." + f.getQueryResultFormDisplay().toString() + "); // " + f.getComment() + "\n";
				sb.append(text);
			}
			if (f.getQueryResultFormDisplay() != Display.INPUT_HIDDEN) {
				String src = "\t\thtmltable.getFieldList().get(" + queryClass + ".Entity.ID_" + StringUtil.camelToUpperCaseSnake(f.getId()) + ")."
						+ "setSortable(true); // " + f.getComment() + "\n";
				ssb.append(src);
			} 
		}
		return new FieldSetting(sb.toString(), ssb.toString(), implist);
	}


	@Override
	protected void setFormComponent(Template tmp, String formClassName, Map<String, Object> data) throws Exception {
		tmp.replace(DaoAndPageGeneratorEditForm.ID_QUERY_RESULT_FORM_CLASS_NAME, formClassName);
		FieldSetting fieldSetting = this.getQueryResultFormFieldList(data);
		tmp.replace("queryResultFieldSetting", fieldSetting.htmlSetting());
		tmp.replace("queryResultFieldSortSetting", fieldSetting.sortSetting());
		tmp.replace("queryResultFormImportList", fieldSetting.implist().getImportText());

	}

}
