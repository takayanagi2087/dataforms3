package jp.dataforms.fw.devtool.pageform.page;


import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jp.dataforms.fw.controller.Page;
import jp.dataforms.fw.controller.QueryResultForm;
import jp.dataforms.fw.devtool.field.ClassNameField;
import jp.dataforms.fw.devtool.field.PackageNameField;
import jp.dataforms.fw.devtool.field.PageClassNameField;
import jp.dataforms.fw.devtool.field.PagePatternSelectField;
import jp.dataforms.fw.field.base.FieldList;
import jp.dataforms.fw.field.common.RowNoField;
import jp.dataforms.fw.htmltable.HtmlTable;
import jp.dataforms.fw.util.ClassFinder;
import jp.dataforms.fw.util.StringUtil;

/**
 * Pageクラス問い合わせ結果フォーム。
 *
 */
public class PageGeneratorQueryResultForm extends QueryResultForm {

	/**
	 * Log.
	 */
	private Logger logger = LogManager.getLogger(PageGeneratorQueryResultForm.class);

	/**
	 * コンストラクタ。
	 */
	public PageGeneratorQueryResultForm() {
		HtmlTable htmltbl = new HtmlTable(Page.ID_QUERY_RESULT
			, (new RowNoField()).setSpanField(true)
			, (new PackageNameField()).setHidden(true)
			, (new PageClassNameField()).setHidden(true)
			, (new ClassNameField("fullClassName")).setSpanField(true)
			, (new ClassNameField("daoClassName")).setSpanField(true)
			, (new PagePatternSelectField("pagePattern")).setSpanField(true)
		);
		this.addHtmlTable(htmltbl);
		this.addPkField(htmltbl.getFieldList().get("packageName"));
		this.addPkField(htmltbl.getFieldList().get("pageClassName"));
	}


	/**
	 * ページクラスの一覧を取得します。
	 * @param data パラメータ。
	 * @return クエリ結果。
	 * @throws Exception 例外。
	 */
	private List<Map<String, Object>> queryPageClass(final Map<String, Object> data) throws Exception {
		String packageName = (String) data.get("packageName");
		String classname = (String) data.get("className");
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		ClassFinder finder = new ClassFinder();
		List<Class<?>> pageList = finder.findClasses(packageName, Page.class);
		int no = 1;
		for (Class<?> pagecls : pageList) {
			Map<String, Object> m = new HashMap<String, Object>();
			if (Page.class.getName().equals(pagecls.getName())) {
				continue;
			}
			if (!StringUtil.isBlank(classname)) {
				if (pagecls.getName().indexOf(classname) < 0) {
					continue;
				}
			}
			if ((pagecls.getModifiers() & Modifier.ABSTRACT) != 0) {
				continue;
			}
			m.put("rowNo", Integer.valueOf(no));
			m.put("packageName", pagecls.getPackage().getName());
			m.put("pageClassName", pagecls.getSimpleName());
			m.put("fullClassName", pagecls.getName());
			Page p = (Page) pagecls.getConstructor().newInstance();
			m.put("pagePattern", p.getPagePattern());

			result.add(m);
			no++;
		}
		return result;
	}


	@Override
	protected Map<String, Object> queryPage(final Map<String, Object> data, final FieldList flist) throws Exception {
		List<Map<String, Object>> queryResult = this.queryPageClass(data);
		for (Map<String, Object> r: queryResult) {
			String className = (String) r.get("fullClassName");
			Class<?> cls = Class.forName(className);
			Page p = (Page) cls.getDeclaredConstructor().newInstance();
			PageClassInfo pi = new PageClassInfo(p);
			Class<?> daoclass = (Class<?>) pi.getDaoClass();
			if (daoclass != null) {
				r.put("daoClassName", daoclass.getName());
			}
		}
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("queryResult", queryResult);
		logger.info("queryResult=" + queryResult);
		return result;
	}

}
