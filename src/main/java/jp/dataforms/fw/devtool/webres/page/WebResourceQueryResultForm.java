package jp.dataforms.fw.devtool.webres.page;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jp.dataforms.fw.controller.Dialog;
import jp.dataforms.fw.controller.Form;
import jp.dataforms.fw.controller.Page;
import jp.dataforms.fw.controller.QueryResultForm;
import jp.dataforms.fw.controller.WebComponent;
import jp.dataforms.fw.devtool.field.ClassNameField;
import jp.dataforms.fw.devtool.field.JavascriptClassField;
import jp.dataforms.fw.devtool.field.WebComponentTypeField;
import jp.dataforms.fw.field.base.Field;
import jp.dataforms.fw.field.base.FieldList;
import jp.dataforms.fw.field.common.PresenceField;
import jp.dataforms.fw.field.common.RowNoField;
import jp.dataforms.fw.htmltable.HtmlTable;
import jp.dataforms.fw.util.ClassFinder;
import jp.dataforms.fw.util.StringUtil;
import jp.dataforms.fw.validator.FieldValidator;

/**
 * 問い合わせ結果フォームクラス。
 */
public class WebResourceQueryResultForm extends QueryResultForm {
	/**
	 * WebコンポーネントタイプフィールドID。
	 */
	private static final String ID_WEB_COMPONENT_TYPE_LIST = "webComponentTypeList";
	/**
	 * Webコンポーネントタイプ。
	 */
	private static final String ID_WEB_COMPONENT_TYPE = "webComponentType";
	/**
	 * HTML有無。
	 */
	private static final String ID_HTML_STATUS = "htmlStatus";
	/**
	 * Javascript有無。
	 */
	private static final String ID_JAVASCRIPT_STATUS = "javascriptStatus";
	/**
	 * Javascriptクラス。
	 */
	private static final String ID_JAVASCRIPT_CLASS = "javascriptClass";

	/**
	 * Logger.
	 */
	private static Logger logger = LogManager.getLogger(WebResourceQueryResultForm.class.getName());


	/**
	 * コンストラクタ。
	 */
	public WebResourceQueryResultForm() {
		HtmlTable htmltbl = new HtmlTable(Page.ID_QUERY_RESULT
			, new RowNoField()
			, new ClassNameField()
			, new WebComponentTypeField()
			, new PresenceField(ID_HTML_STATUS)
			, new PresenceField(ID_JAVASCRIPT_STATUS)
			, new JavascriptClassField()
		);
		htmltbl.getFieldList().get(ID_WEB_COMPONENT_TYPE).setReadonly(true);
		htmltbl.getFieldList().get(ID_HTML_STATUS).setReadonly(true);
		htmltbl.getFieldList().get(ID_JAVASCRIPT_STATUS).setReadonly(true);
		htmltbl.getFieldList().get(ID_JAVASCRIPT_CLASS).setReadonly(true);
		this.addHtmlTable(htmltbl);
	}

	/**
	 * 同一のクラス名を除外するためのSet.
	 */
	private HashSet<String> classNameSet = null;

	/**
	 * 保持するコンポーネントの一覧を取得します。
	 * @param result コンポーネントの一覧。
	 * @param comp コンポーネント。
	 * @throws Exception 例外。
	 */
	private void getComponentList(final List<Map<String, Object>> result, final WebComponent comp) throws Exception {
		Map<String, WebComponent> wcmap = comp.getComponentMap();
		for (String key: wcmap.keySet()) {
			WebComponent c = wcmap.get(key);
			if ((!this.classNameSet.contains(c.getClass().getName())) || c instanceof HtmlTable) {
				Map<String, Object> m = new HashMap<String, Object>();
				m.put("className", c.getClass().getName());
				result.add(m);
				this.getComponentList(result, c);
				this.classNameSet.add(c.getClass().getName());
			}
		}
	}


	/**
	 * コンポーネントタイプのチェックを行います。
	 * @param cls クラス。
	 * @param tlist コンポーネントタイプリスト。
	 * @return マッチする場合true。
	 * @throws Exception 例外。
	 */
	private boolean checkTypeCondition(final Class<?> cls, final List<String> tlist) throws Exception {
		boolean ret = false;
		for (String t: tlist) {
			Class<?> baseclass = WebComponent.class;
			if ("page".equals(t)) {
				baseclass = Page.class;
			} else if ("dialog".equals(t)) {
				baseclass = Dialog.class;
			} else if ("form".equals(t)) {
				baseclass = Form.class;
			} else if ("table".equals(t)) {
				baseclass = HtmlTable.class;
			} else if ("field".equals(t)) {
				baseclass = Field.class;
			} else if ("validator".equals(t)) {
				baseclass = FieldValidator.class;
			}
			if (baseclass.isAssignableFrom(cls)) {
				ret = true;
				break;
			}
		}
		return ret;
	}

	/**
	 * コンポーネントタイプ名称を取得します。
	 * @param c コンポーネントクラス。
	 * @return コンポーネントタイプ名称。
	 * @throws Exception 例外。
	 */
	private String getComponentTypeName(final Class<?> c) throws Exception {
		String type = null;
		if (Page.class.isAssignableFrom(c)) {
			type = "page";
		} else if (Dialog.class.isAssignableFrom(c)) {
			type = "dialog";
		} else if (Form.class.isAssignableFrom(c)) {
			type = "form";
		} else if (HtmlTable.class.isAssignableFrom(c)) {
			type = "table";
		} else if (Field.class.isAssignableFrom(c)) {
			type = "field";
		} else if (FieldValidator.class.isAssignableFrom(c)) {
			type = "validator";
		}
		return type;
	}

	/**
	 * HTMLの状態を取得します。
	 * @param c クラス。
	 * @param webResourcePath Webリソース保存先フォルダ。
	 * @return 状態。
	 * @throws Exception 例外。
	 */
	private String getHtmlStatus(final Class<?> c, final String webResourcePath) throws Exception {
		if (Page.class.isAssignableFrom(c) || Dialog.class.isAssignableFrom(c) || Form.class.isAssignableFrom(c)) {
			String respath =  "/" + this.getWebResourcePath(c) + ".html";
			String ret = this.getWebResource(respath);
			File resfile = new File(webResourcePath + respath);
			if ((!resfile.exists()) && StringUtil.isBlank(ret)) {
				return "0";
			} else {
				return "1";
			}
		} else {
			return null;
		}
	}

	/**
	 * Javascriptの状態を取得します。
	 * @param c クラス。
	 * @return javasciptの状態。
	 * @param webResourcePath Webリソース保存先フォルダ。
	 * @throws Exception 例外。
	 */
	private String getJavascriptStatus(final Class<?> c, final String webResourcePath) throws Exception {
		String respath = "/" + this.getWebResourcePath(c) + ".js";
		File resfile = new File(webResourcePath + respath);
		String ret = this.getWebResource(respath);
		if ((!resfile.exists()) && StringUtil.isBlank(ret)) {
			return "0";
		} else {
			return "1";
		}
	}

	/**
	 * スクリプトが存在する親クラス名を取得します。
	 * @param c クラス。
	 * @return スクリプトが存在する親クラス名。
	 * @throws Exception 例外。
	 */
	private String getJavascriptClass(final Class<?> c) throws Exception {
		Class<?> sc = c;
		String classname = sc.getSimpleName();
		String respath = "/" + this.getWebResourcePath(sc) + ".js";
		String ret = this.getWebResource(respath);
		while (StringUtil.isBlank(ret)) {
			sc = sc.getSuperclass();
			respath = "/" + this.getWebResourcePath(sc) + ".js";
			ret = this.getWebResource(respath);
			classname = sc.getSimpleName();
		}
		return classname;
	}

	/**
	 * 指定されたページ内のコンポーネントの一覧を取得します。
	 * @param packageName ページのパッケージ名。
	 * @param pageClassName ページクラス名。
	 * @return ページ内のコンポーネントの一覧。
	 * @throws Exception 例外。
	 */
	private List<Map<String, Object>> queryPageCompoentList(final String packageName, final String pageClassName) throws Exception {
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		@SuppressWarnings("unchecked")
		Class<? extends Page> cls = (Class<? extends Page>) Class.forName(packageName + "." + pageClassName);
		Page p = cls.getDeclaredConstructor().newInstance();
		this.classNameSet = new HashSet<String>();
		Map<String, Object> r = new HashMap<String, Object>();
		r.put("className", p.getClass().getName());
		result.add(r);
		this.getComponentList(result, p);
		return result;
	}

	/**
	 * 指定されたパッケージ内のコンポーネントの一覧を取得します。
	 * @param packageName ページのパッケージ名。
	 * @return ページ内のコンポーネントの一覧。
	 * @throws Exception 例外。
	 */
	private List<Map<String, Object>> queryCompoentList(final String packageName) throws Exception {
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		ClassFinder finder = new ClassFinder();
		List<Class<?>> list = finder.findClasses(packageName, WebComponent.class);
		for (Class<?> cls: list) {
			Map<String, Object> r = new HashMap<String, Object>();
			logger.debug("class=" + cls.getName());
			r.put("className", cls.getName());
			result.add(r);
		}
		logger.debug("result.size()=" + result.size());
		return result;
	}

	@Override
	protected Map<String, Object> queryPage(final Map<String, Object> data, final FieldList queryFormFieldList) throws Exception {
		String webResourcePath = (String) data.get("webSourcePath");
		String className = (String) data.get("className");
		Map<String, Object> ret = new HashMap<String, Object>();
		String packageName = (String) data.get("packageName");
		String pageClassName = (String) data.get("pageClassName");
		String generatableOnly = (String) data.get("generatableOnly");
		List<Map<String, Object>> result = null;
		if (!StringUtil.isBlank(pageClassName)) {
			// 指定されたページクラス以下のコンポーネント一覧を取得する。
			result = this.queryPageCompoentList(packageName, pageClassName);
		} else {
			result = this.queryCompoentList(packageName);
		}
		List<Map<String, Object>> queryResult = new ArrayList<Map<String, Object>>();
		@SuppressWarnings("unchecked")
		List<String> tlist = (List<String>) data.get(ID_WEB_COMPONENT_TYPE_LIST);
		int no = 1;
		for (Map<String, Object> m: result) {
			String classname = (String) m.get("className");
			if ("dataforms.app.menu.page.SideMenuForm".equals(classname)
				|| "dataforms.app.login.page.LoginInfoForm".equals(classname)
				|| "dataforms.app.field.user.LoginIdField".equals(classname)
				|| "dataforms.app.field.user.UserNameField".equals(classname)) {
				continue;
			}
			if (!StringUtil.isBlank(className)) {
				if (classname.indexOf(className) < 0) {
					continue;
				}
			}
			Class<?> c = Class.forName(classname);
			if (this.checkTypeCondition(c, tlist)) {
				String htmlStatus = this.getHtmlStatus(c, webResourcePath);
				String javascriptStatus = this.getJavascriptStatus(c, webResourcePath);
				if ("1".equals(generatableOnly)) {
					if (("1".equals(htmlStatus) || StringUtil.isBlank(htmlStatus)) && "1".equals(javascriptStatus)) {
						continue;
					}
				}
				m.put("rowNo", Integer.valueOf(no++));
				m.put(ID_WEB_COMPONENT_TYPE, this.getComponentTypeName(c));
				m.put(ID_HTML_STATUS, htmlStatus);
				m.put(ID_JAVASCRIPT_STATUS, javascriptStatus);
				m.put(ID_JAVASCRIPT_CLASS, this.getJavascriptClass(c));
				queryResult.add(m);
			}
		}
		Set<String> set = new HashSet<String>();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> m: queryResult) {
			String name = (String) m.get("className");
			if (!set.contains(name)) {
				set.add(name);
				list.add(m);
			}
		}
		ret.put(Page.ID_QUERY_RESULT, list);
		return ret;
	}
}
