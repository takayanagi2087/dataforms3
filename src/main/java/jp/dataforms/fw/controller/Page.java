package jp.dataforms.fw.controller;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jp.dataforms.fw.annotation.WebMethod;
import jp.dataforms.fw.dao.Dao;
import jp.dataforms.fw.dao.Query;
import jp.dataforms.fw.dao.QuerySetDao;
import jp.dataforms.fw.devtool.field.PagePatternSelectField;
import jp.dataforms.fw.field.base.Field;
import jp.dataforms.fw.htmltable.HtmlTable;
import jp.dataforms.fw.menu.Menu;
import jp.dataforms.fw.response.HtmlResponse;
import jp.dataforms.fw.response.JsonResponse;
import jp.dataforms.fw.response.RedirectResponse;
import jp.dataforms.fw.response.Response;
import jp.dataforms.fw.servlet.DataFormsServlet;
import jp.dataforms.fw.util.AutoLoginCookie;
import jp.dataforms.fw.util.ClassFinder;
import jp.dataforms.fw.util.JsonUtil;
import jp.dataforms.fw.util.MessagesUtil;
import jp.dataforms.fw.util.StringUtil;
import jp.dataforms.fw.validator.FieldValidator;


/**
 * ページ制御クラス。
 */
public class Page extends DataForms implements WebEntryPoint {

	/**
     * Logger.
     */
    private static Logger logger = LogManager.getLogger(Page.class.getName());

	/**
	 * dataforms3.jarのバージョン。
	 */
	private static String dataformsVersion = null;

	/**
	 * dataforms3.jarの提供者。
	 */
	private static String dataformsVendor = null;

	/**
	 * dataforms3.jarの作成日時。
	 */
	private static String dataformsCreateDate = null;

	/**
	 * フレーム情報パス。
	 */
	private static String framePath = null;



	/**
	 * ページの拡張子。
	 */
	private String pageExt = null;

	/**
	 * ページの拡張子を取得します。
	 * @return ページの拡張子、
	 */
    public String getPageExt() {
		return pageExt;
	}

    /**
     * ページの拡張子を設定します。
     * <pre>
     * DataFormsServletがServletアノテーションのUrlPatternからURLの拡張子を取得し設定します。
     * </pre>
     * @param pageExt ページの拡張子。
     */
    public void setPageExt(final String pageExt) {
		this.pageExt = pageExt;
	}


	/**
     * 事前に読み込むcssのリスト。
     */
    private List<String> preloadCssList = new ArrayList<String>();

	/**
     * 事前に読み込むcssのリスト(Media対応)。
     */
    private static List<String[]> preloadMediaCssList = new ArrayList<String[]>();

    /**
     * ブラウザの戻るボタンの設定。
     */
    private static String browserBackButton = "enabled";

    /**
     * トップページ。
     */
    private static String topPage = "/dataforms/app/top/page/TopPage.html";


	/**
	 * JDBC接続。
	 */
	private WeakReference<Connection> connection = null;

	/**
	 * メニュー表示フラグ。
	 */
	private boolean menuItem = true;

	/**
	 * フレーム無フラグ.
	 */
	private boolean noFrame = false;


	/**
	 * コンストラクタ。
	 */
	public Page() {
		this.addDialog(new AlertDialog());
		this.addDialog(new ConfirmDialog());
	}


	/**
	 * クラスを継承関係を元にソートします。
	 * @param list クラスリスト。
	 * @param baseclass 基本クラス。
	 * @return ソート結果リスト。
	 */
	protected  List<Class<?>> sortClass(final List<Class<?>> list, final Class<?> baseclass) {
		List<Class<?>> ret = new ArrayList<Class<?>>();
		ret.add(baseclass);
		for (int i = 0; i < ret.size(); i++) {
			Class<?> sc = ret.get(i);
			for (Class<?> c: list) {
				if (c.getSuperclass().equals(sc)) {
					ret.add(c);
				}
			}
		}
		return ret;
	}

	/**
	 * 指定パッケージの指定クラスから派生したクラスのリストを継承順に取得します。
	 * @param pkg パッケージ。
	 * @param baseclass 基本クラス。
	 * @return クラスリスト。
	 */
	protected List<Class<?>> findClassTree(final String pkg, final Class<?> baseclass) {
		List<Class<?>> list = null;
		ClassFinder finder = new ClassFinder();
		try {
			 list = this.sortClass(finder.findClasses(pkg, baseclass), baseclass);
/*			 logger.debug("findClassTree pkg=" + pkg);
			 logger.debug("findClassTree baseclass=" + baseclass);
			 logger.debug("list=" + JSON.encode(list, true));
*/
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 指定パッケージの指定クラスから派生したクラスのjavascriptファイルを継承順に取得します。
	 * @param pkg パッケージ。
	 * @param baseclass 基本クラス。
	 * @return javascriptリスト。
	 * @throws Exception 例外。
	 */
	protected List<String> findJsClassTree(final String pkg, final Class<?> baseclass) throws Exception {
		List<Class<?>> list = this.findClassTree(pkg, baseclass);
		List<String> ret = new ArrayList<String>();
		for (Class<?> cls: list) {
			String jspath = this.getClassScriptPath(cls);
			String script = this.getWebResource(jspath);
			if (!StringUtil.isBlank(script)) {
				ret.add(jspath);
			}
		}
//		logger.debug("findJsClassTree ret=" + JSON.encode(ret, true));
		return ret;
	}


	/**
	 * 基本javascriptリスト。
	 */
	private static List<String> basicJsCache = null;

	/**
	 * 基本的javascriptのリストを取得します。
	 * @return 基本的jvascriptのリスト。
	 * @throws Exception 例外。
	 */
	private synchronized List<String> getBasicJsCache() throws Exception {
		if (basicJsCache == null) {
			basicJsCache = Collections.synchronizedList(new ArrayList<String>());
//			basicJsCache.add(this.getPageFramePath() + "/Frame.js");
			basicJsCache.add("/dataforms/util/MessagesUtil.js");
			basicJsCache.add("/dataforms/util/QueryStringUtil.js");
			basicJsCache.add("/dataforms/util/StringUtil.js");
			basicJsCache.add("/dataforms/util/NumberUtil.js");
			basicJsCache.add("/dataforms/response/JsonResponse.js");
			basicJsCache.add("/dataforms/util/WebMethod.js");
			basicJsCache.add("/dataforms/util/SimpleDateFormat.js");

			basicJsCache.addAll(this.findJsClassTree(WebComponent.BASE_PACKAGE + ".controller", WebComponent.class));
			basicJsCache.addAll(this.findJsClassTree(WebComponent.BASE_PACKAGE + ".htmltable", HtmlTable.class));
			basicJsCache.addAll(this.findJsClassTree(WebComponent.BASE_PACKAGE + ".menu", Menu.class));
			basicJsCache.addAll(this.findJsClassTree(WebComponent.BASE_PACKAGE + ".field", Field.class));

			basicJsCache.add("/dataforms/validator/ValidationError.js");
			basicJsCache.addAll(this.findJsClassTree(WebComponent.BASE_PACKAGE + ".validator", FieldValidator.class));
		}
//		logger.debug("basicJsCache=" + JSON.encode(basicJsCache, true));
		return basicJsCache;
	}

	/**
	 * 指定されたクラスを使用するのに必要なスクリプトリストを取得します。
	 * @param list スクリプトリスト。
	 * @param cls クラス。
	 * @return 指定されたクラスを使用するのに必要なスクリプトリスト。
	 * @throws Exception 例外。
	 */
	protected List<String> getScriptTree(final List<String> list, final Class<?> cls) throws Exception {
		Class<?> c = cls;
		List<String> l = new ArrayList<String>();
		while (true) {
			String js = this.getClassScriptPath(c);
			if (basicJsCache.contains(js)) {
				break;
			}
			String script = this.getWebResource(js);
			if (!StringUtil.isBlank(script)) {
				l.add(0, js);
			}
			c = c.getSuperclass();
		}
		for (String js: l) {
			if (!list.contains(js)) {
				list.add(js);
			}
		}
		return list;
	}


	/**
	 * フィールド関連スクリプトリストを取得する。
	 * @param list スクリプトリスト。
	 * @param field フィール。
	 * @throws Exception 例外。
	 */
	protected void getFieldAppScripts(final List<String> list, final Field<?> field) throws Exception {
		this.getScriptTree(list, field.getClass());
		for (FieldValidator v: field.getValidatorList()) {
			this.getScriptTree(list, v.getClass());
		}
	}


	/**
	 * テーブル関連スクリプトリストを取得する。
	 * @param list スクリプトリスト。
	 * @param table テーブル。
	 * @throws Exception 例外。
	 */
	protected void getTableAppScripts(final List<String> list, final HtmlTable table) throws Exception {
		this.getScriptTree(list, table.getClass());
		for (Field<?> f: table.getFieldList()) {
			this.getFieldAppScripts(list, f);
		}
	}

	/**
	 * フォーム関連スクリプトリストを取得する。
	 * @param list スクリプトリスト。
	 * @param form フォーム。
	 * @throws Exception 例外。
	 */
	protected void getFormAppScripts(final List<String> list, final Form form) throws Exception {
		this.getScriptTree(list, form.getClass());
		for (Field<?> f: form.getFieldList()) {
			this.getFieldAppScripts(list, f);
		}
		List<HtmlTable> tlist = form.getHtmlTableList();
		for (HtmlTable table: tlist) {
			this.getTableAppScripts(list, table);
		}
	}


	/**
	 * ダイアログ関連スクリプトリストを取得する。
	 * @param list スクリプトリスト。
	 * @param dialog ダイアログ。
	 * @throws Exception 例外。
	 */
	protected void getDialogAppScripts(final List<String> list, final Dialog dialog) throws Exception {
		this.getScriptTree(list, dialog.getClass());
		Map<String, WebComponent> map = dialog.getFormMap();
		for (String key: map.keySet()) {
			WebComponent f = (WebComponent) map.get(key);
			if (f instanceof Form) {
				this.getFormAppScripts(list, (Form) f);
			}
		}
	}

	/**
	 * Dataforms関連スクリプトリストを取得する。
	 * @param list スクリプトリスト。
	 * @param df DataForms。
	 * @throws Exception 例外。
	 */
	protected void getDataformsAppScripts(final List<String> list, final DataForms df) throws Exception {
		this.getScriptTree(list, this.getClass());
	}

	/**
	 * ページスクリプトを取得します。
	 * @return アプリケーションスクリプトリスト。
	 * @throws Exception 例外。
	 */
	protected String getPageScript() throws Exception {
		List<String> list = new ArrayList<String>();
		this.getDataformsAppScripts(list, this);
		logger.debug("getAppScript=" + JsonUtil.encode(list));
		return list.get(list.size() - 1);
	}

	private String getJsClass(final String p) {
		Pattern pat = Pattern.compile(".*/(.+?).js$");
		Matcher m = pat.matcher(p);
		if (m.find()) {
			return m.group(1);
		}
		return null;
	}
	
	/**
	 * ImportScriptタグを追加します。
	 * @param context コンテキスト。
	 * @param js javascriptのパス。
	 * @param sb 追加する文字列バッファ。
	 * @throws Exception 例外。
	 */
	private void addImportScriptTag(final String context, final String js, final StringBuilder sb) throws Exception {
		String jspath = this.getAppropriatePath(js, this.getRequest());
		if (jspath != null) {
//			String t = this.getLastUpdate(jspath);
			String p = context + jspath;
			String cls = this.getJsClass(p);
			if (cls != null) {
				sb.append("\t\timport { " + cls + " } from '" + p + "';\n");
			}
		}
	}

	/**
	 * Scriptタグを追加します。
	 * @param context コンテキスト。
	 * @param js javascriptのパス。
	 * @param sb 追加する文字列バッファ。
	 * @throws Exception 例外。
	 */
	private void addScriptTag(final String context, final String js, final StringBuilder sb) throws Exception {
		String jspath = this.getAppropriatePath(js, this.getRequest());
		if (jspath != null) {
			String t = this.getLastUpdate(jspath);
			sb.append("\t\t<script src=\"" + context + jspath + "?t=" + t + "\"></script>\n");
		}
	}

	/**
	 * ページに必要なスタイルシートの一覧を作成します。
	 */
	protected void buildCssList() {
		this.addPreloadCss(this.getPageFramePath() + "/Variables.css");
		this.addPreloadCss(this.getPageFramePath() + "/Frame.css");
		this.addPreloadCss(this.getPageFramePath() + "/Menu.css");
		this.addPreloadCss(this.getPageFramePath() + "/Form.css");
		this.addPreloadCss(this.getPageFramePath() + "/Field.css");
		this.addPreloadCss(this.getPageFramePath() + "/HtmlTable.css");
		this.addPreloadCss(this.getPageFramePath() + "/AppFrame.css");
		this.addPreloadCss(this.getPageFramePath() + "/AppMenu.css");
		this.addPreloadCss(this.getPageFramePath() + "/AppForm.css");
		this.addPreloadCss(this.getPageFramePath() + "/AppField.css");
		this.addPreloadCss(this.getPageFramePath() + "/AppHtmlTable.css");
	}

	/**
	 * cssとjavascriptのロードタグを取得します。
	 * @return cssとjavascriptのロードタグ。
	 * @throws Exception 例外.
	 */
	public String getPreloadTags() throws Exception {
		this.buildCssList();
		String context = this.getRequest().getContextPath();
		StringBuilder sb = new StringBuilder();
		for (String css : this.preloadCssList) {
			String csspath = this.getAppropriatePath(css, this.getRequest());
			if (csspath != null) {
				String t = this.getLastUpdate(csspath);
				sb.append("\t\t<link type=\"text/css\" href=\"" + context + csspath + "?t=" + t + "\" rel=\"stylesheet\" />\n");
			}
		}

		this.getBasicJsCache();
		this.addScriptTag(context, this.getPageFramePath() + "/Frame.js", sb);
		return sb.toString();
	}

	/**
	 * {@inheritDoc}
	 * DB接続を取得します。
	 */
	@Override
	public Connection getConnection() {
		if (this.connection != null) {
			return this.connection.get();
		} else {
			return null;
		}
	}

	/**
	 * JDBC接続を設定します。
	 * @param connection JDBC接続。
	 */
	@Override
	public void setConnection(final Connection connection) {
		this.connection = new WeakReference<Connection>(connection);
	}

	/**
	 * 事前にロードするcssを追加します。
	 * @param css CSSファイルのパス。
	 */
    protected void addPreloadCss(final String css) {
    	this.preloadCssList.add(css);
    }


	/**
	 * 事前にロードするcssを追加します。
	 * @param css CSSファイルのパス。
	 * @param media media指定。
	 */
    public static void addPreloadCss(final String css, final String media) {
    	String []ent = new String[2];
    	ent[0] = css;
    	ent[1] = media;
    	Page.preloadMediaCssList.add(ent);
    }

    /**
     * フォーム初期化メソッド0。
     */
    private static final  String INIT_SCRIPT0 =
       	"\t\t<script>\n" + 
    	"\t\tcurrentPage = null;\n" + 
    	"\t\tlogger = null;\n" + 
       	"\t\t</script>\n" + 
    	"\t\t<script type=\"module\">\n"; 

    /**
     * フォーム初期化メソッド1。
     */
    private static final  String INIT_SCRIPT1 =
		"\t\t\tcurrentPage = page;\n" +
		"\t\t\tpage.init();\n" +
		"\t\t});\n" +
		"\t\t</script>\n";

	/**
	 * HTMLにcssとscriptを追加します。
	 * @param html HTML。
	 * @param scripts 追加するscript。
	 * @param context コンテキスト。
	 * @return scriptを追加したhtml。
	 * @throws Exception 例外。
	 */
	protected String editHtml(final String html, final String scripts, final String context) throws Exception {
		//String scriptPath = this.getScriptPath();
		StringBuilder sb = new StringBuilder(scripts);
		String framepath = this.getAppropriatePath(this.getPageFramePath() + "/Frame.css", this.getRequest());
		if (framepath.indexOf("_phone.") > 0) {
			// スマートフォンの場合の設定.
			sb.append("<meta name=\"viewport\" content=\"width=device-width\">\n");
		}
		sb.append(this.getPreloadTags());
		String pageclass = this.getJsClass();
		String csrfToken = this.getCsrfToken();
		this.buildInitScript(sb, pageclass, csrfToken);
		String s = sb.toString();
//		log.info("scriptPath=" + scriptPath);
		Pattern pat = Pattern.compile("</head>");
		Matcher m = pat.matcher(html);
		StringBuilder htmlbuffer = new StringBuilder();
		if (m.find()) {
			int start = m.start();
			int end = m.end();
			htmlbuffer.append(html.substring(0, start));
			htmlbuffer.append(s);
			htmlbuffer.append("\t" + m.group());
			htmlbuffer.append(html.substring(end));
		}
		return htmlbuffer.toString();
	}

	/**
	 * ページの初期化スクリプトを出力します。
	 * @param sb 出力する文字列バッファ。
	 * @param pageclass ページクラス。
	 * @param csrfToken CSFR対策TOKEN。
	 * @throws UnsupportedEncodingException 例外。
	 */
	protected void buildInitScript(final StringBuilder sb, final String pageclass, final String csrfToken) throws Exception {
		sb.append(INIT_SCRIPT0);
		
		String context = this.getRequest().getContextPath();
		String pageScript = this.getPageScript();
		this.addImportScriptTag(context, pageScript, sb);
		sb.append("\t\t$(() => {\n");
		sb.append("\t\t\tlet page = new " + pageclass + "();\n");
		if (csrfToken != null) {
			sb.append("\t\t\tpage.csrfToken=\"" + java.net.URLEncoder.encode(csrfToken, DataFormsServlet.getEncoding()) + "\";\n");
		}
		sb.append(INIT_SCRIPT1);
	}


	/**
	 * QueryStringを処理メソッド。
	 * <pre>
	 * 各コンポーネントのinit()メソッド等は初期Html取得時のリクエストとは
	 * 別のリクエストで呼び出されるため、初期Html取得時のQueryStringを参照できません。
	 * これらのinit()メソッド等でQueryStringを参照したい場合、このメソッドを実装し、
	 * QueryStringの情報をセッションに記録してください。
	 * </pre>
	 * @param p QueryStringを展開したMap。
	 * @throws Exception 例外。
	 */
	protected void processQueryString(final Map<String, Object> p) throws Exception {

	}

	/**
	 * htmlに対応するPageクラスを返します。
	 * <pre>
	 * 基本的に自分自身のクラス(this.getClass()の値)を返します。
	 * このメソットが返すクラスに対応した*.htmlファイルをページのHTMLとします。
	 * ページクラスと異なるクラスのHTMLを使用したい場合、このメソッド
	 * をオーバーライドします。
	 * </pre>
	 * @return Pageクラス。
	 */
	protected Class<?> getHtmlPageClass() {
		return this.getClass();
	}

	/**
	 * HTMLのパスを取得します。
	 * @return HTMLのパス。
	 */
	protected String getHtmlPath() {
		String htmlpath = this.getWebResourcePath(this.getHtmlPageClass()) + ".html";
		return htmlpath;
	}

	/**
	 * htmlのformタグを置き換える。
	 * @param html HTML文字列。
	 * @param formId フォームID。
	 * @param form フォーム。
	 * @return 返還後のHTML文字列。
	 * @throws Exception 例外。
	 */
	protected String replaceFormHtml(final String html, final String formId, final Form form) throws Exception {
//		Form form = (Form) this.getFormMap().get(formId);
		String htmlpath = this.getAppropriatePath(form.getHtmlPath(), this.getPage().getRequest());
		String htmltext = this.getWebResource(htmlpath); // FileUtil.readTextFile(htmlpath,
		if (htmltext != null) {
			htmltext = this.getHtmlBody(htmltext);
			// 先頭のFormタグを削除
			Pattern p = Pattern.compile("\\<form.*?\\>", Pattern.DOTALL);
			Matcher m = p.matcher(htmltext);
			htmltext = m.replaceAll("");
		}
		String orgFormTag = "<form id=\"" + formId + "\">";
		{
			Pattern p = Pattern.compile("\\<form.*?id=\"" + formId + "\".*?\\>", Pattern.DOTALL);
			Matcher m = p.matcher(html);
			if (m.find()) {
				orgFormTag = m.group();
			}
		}
		Pattern p = Pattern.compile("\\<form.*?id=\"" + formId + "\".*?\\>.*?\\</form\\>", Pattern.DOTALL);
		Matcher m = p.matcher(html);
		String ret  = m.replaceAll(orgFormTag + htmltext);
		return ret;
	}


	/**
	 * このページのHTMLを取得します。
	 * @param req HTTP要求情報。
	 * @param context コンテキスト。
	 * @return HTMLのテキスト。
	 * @throws Exception 例外。
	 */
	protected String getHtmlText(final HttpServletRequest req, final String context) throws Exception {
		logger.debug("getHtmlText");
		//String htmlpath = this.getWebResourcePath(this.getHtmlPageClass()) + ".html";
		String htmlpath = this.getHtmlPath();
		htmlpath = this.getAppropriatePath(htmlpath, req);
		logger.info("sendHtml={}", htmlpath);
		String htmltext = this.getWebResource(htmlpath);
//		htmltext = this.addAppcacheFile(htmltext, context);
		// langアトリビュートの属性
		Pattern p = Pattern.compile("\\<html.*?lang\\=.*?\\>");
		Matcher m = p.matcher(htmltext);
		if (!m.find()) {
			htmltext = htmltext.replaceAll("<html" , "<html lang=\"" + req.getLocale().getLanguage() + "\" ");
		}
		htmltext = htmltext.replaceAll("\\<[Tt][Ii][Tt][Ll][Ee]>.*\\</[Tt][Ii][Tt][Ll][Ee]\\>", "<title>" + this.getPageTitle() + "</title>");
		htmltext = htmltext.replaceAll("\\</[Bb][Oo][Dd][Yy]\\>", "\t<noscript><br/><div class='noscriptDiv'><b>" + MessagesUtil.getMessage(this.getPage(), "message.noscript") + "</b></div></noscript>\n\t</body>");
		return this.convertIdAttribute(htmltext);
	}

	/**
	 * QueryStringをマップに展開したものを保持します。
	 */
	private Map<String, Object> queryString = null;


	/**
	 * QueryStringマップを設定します。
	 * @param map マップ。
	 */
	@Override
	public void setQueryString(final Map<String, Object> map) {
		this.queryString = map;
	}

	/**
	 * QueryStringマップを取得します。
	 * @return QueryStringマップ。
	 */
	@Override
	public Map<String, Object> getQueryString() {
		return this.queryString;
	}

	@WebMethod(useDB = true)
	@Override
	public Response exec(final Map<String, Object> p) throws Exception {
		return this.getHtml(p);
	}

	/**
	 * IEかどうかを判定します。
	 * @return IEの場合TRUE。
	 */
	public boolean isIE() {
		HttpServletRequest req = this.getRequest();
		return Page.BROWSER_IE.equals(Page.getBrowserType(req));
	}

	/**
	 * Microsoft EDGE。
	 */
	public static String BROWSER_EDGE = "edge";
	/**
	 * Microsoft Chromium EDGE。
	 */
	public static String BROWSER_EDG = "edg";
	/**
	 * Microsoft Internet Explorer。
	 */
	public static String BROWSER_IE = "ie";
	/**
	 * Google chrome。
	 */
	public static String BROWSER_CHOROME = "chrome";
	/**
	 * Safari。
	 */
	public static String BROWSER_SAFARI = "safari";
	/**
	 * Firefox。
	 */
	public static String BROWSER_FIREFOX = "firefox";
	/**
	 * Opera。
	 */
	public static String BROWSER_OPERA = "opera";
	/**
	 * 其の他ブラウザ。
	 */
	public static String BROWSER_OTHER = "other";

	/**
	 * ブラウザタイプを取得します。
	 * @param req HTTP要求情報。
	 * @return ブラウザタイプ。
	 */
	public static String getBrowserType(final HttpServletRequest req) {
		String ua = req.getHeader("user-agent");
		if (ua != null) {
			ua = ua.toLowerCase();
		}
		logger.debug("ua={}", ua);
		if (ua.indexOf("edge") >= 0) {
			return Page.BROWSER_EDGE;
		} else if (ua.indexOf("edg") >= 0) {
			return Page.BROWSER_EDG;
		} else if (ua.indexOf("msie") >= 0 || ua.indexOf("trident") >= 0) {
			return Page.BROWSER_IE;
		} else if (ua.indexOf(" opr") >= 0) {
			return Page.BROWSER_OPERA;
		} else if (ua.indexOf("chrome") >= 0) {
			return Page.BROWSER_CHOROME;
		} else if (ua.indexOf("firefox") >= 0) {
			return Page.BROWSER_FIREFOX;
		} else if (ua.indexOf("safari") >= 0) {
			return Page.BROWSER_SAFARI;
		} else {
			return Page.BROWSER_OTHER;
		}
	}

	/**
     * ページのHTMLを取得します。
     * @param params パラメータ。
     * @return HTMLページ応答。
     * @throws Exception 例外。
     */
	public Response getHtml(final Map<String, Object> params) throws Exception {
		HttpServletRequest req = this.getRequest();
		if (this.isIE()) {
			String url = this.getAppropriateLangPath("/dataforms/app/errorpage/IeNotSupport.html", req);
			logger.debug(() -> "url=" + url);
			return new RedirectResponse(req.getContextPath() + url);
		}
    	this.processQueryString(params);
		String uri = req.getRequestURI();
		String context = req.getContextPath();
		logger.info(() -> "context=" + context + ", uri=" + uri);
		logger.info(() -> "path=" + req.getServletPath());
		logger.info(() -> "user-agent=" + req.getHeader("user-agent"));

		String htmltext = this.getHtmlText(req, context);

		String scripts = this.getWebResource(DataFormsServlet.getCssAndScript());
		scripts = scripts.replaceAll("\\$\\{context\\}", req.getContextPath());

    	HtmlResponse resp = new HtmlResponse(this.editHtml(htmltext, scripts, context));
    	return resp;
    }


    /**
     * ログインダイアログのID。
     */
    public static final String ID_LOGIN_DIALOG = "loginDialog";

    /**
     * クライアント用メッセージを取得します。
	 * @param params パラメータ。
	 * @return 応答。
	 * @throws Exception 例外。
	 */
    @WebMethod(useDB = false)
	public JsonResponse getClientMessages(final Map<String, Object> params) throws Exception {
		Map<String, String> map = getMessageMap();
		JsonResponse ret = new JsonResponse(JsonResponse.SUCCESS, map);
		return ret;
	}

    /**
     * ページ毎のクライアントメッセージ送信モードを取得します。
     * <pre>
     * ページ単位にメッセージの送信モード変更したい場合は、このメソッドをオーバーライドしてください。
     * </pre>
     * @return ページ毎のクライアントメッセージ送信モード。
     */
    public MessagesUtil.ClientMessageTransfer getClientMessageTransfer() {
    	return MessagesUtil.getClientMessageTransfer();
    }


    /**
     * ページに対応したメッセージマップを取得します。
     * @return ページに対応したメッセージマップ。
     */
	protected Map<String, String> getMessageMap() {
		Map<String, String> map = MessagesUtil.getClientMessageMap(this);
		return map;
	}

	/**
	 * メッセージを取得します。
	 * @param key メッセージキー。
	 * @return メッセージ。
	 * @throws Exception 例外。
	 */
    public String getMessage(final String key) throws Exception {
    	return MessagesUtil.getMessage(this, key);
    }


    /**
	 * メッセージを取得する.
	 * @param key メッセージキー.
     * @param args メッセージ引数.
     * @return メッセージ.
     * @throws Exception 例外.
     */
    public String getMessage(final String key, final String... args) throws Exception {
    	return MessagesUtil.getMessage(this, key, args);
    }

    /**
     * メッセージの取得.
	 * @param params パラメータ.
	 * @return 応答.
	 * @throws Exception 例外.
	 */
    @WebMethod(useDB = false)
	public JsonResponse getServerMessage(final Map<String, Object> params) throws Exception {
		String key = (String) params.get("key");
		String msg = MessagesUtil.getMessage(this, key);
		JsonResponse ret = new JsonResponse(JsonResponse.SUCCESS, msg);
		return ret;
	}

    /**
     * ダイアログの一覧.
     */
    private Map<String, Dialog> dialogMap = new HashMap<String, Dialog>();

    /**
     * ダイアログ一覧を取得する.
     * @return ダイアログ一覧.
     */
	public Map<String, Dialog> getDialogMap() {
		return dialogMap;
	}

	/**
	 * ダイアログを追加する.
	 * @param dlg ダイアログ.
	 */
	protected void addDialog(final Dialog dlg) {
		this.dialogMap.put(dlg.getId(), dlg);
		this.addComponent(dlg);
	}

	/**
	 * クッキーチェックフラグを取得します。
	 * <pre>
	 * ページ毎にクッキーチェックフラグを変更する場合は、このメソッドをオーバーライドしてください。
	 * </pre>
	 * @return クッキーチェックフラグ。
	 */
	public Boolean getCookieCheck() {
		return DataFormsServlet.isCookieCheck();
	}

	@Override
	public Map<String, Object> getProperties() throws Exception {
		logger.debug("getProperties");
		Map<String, Object> map = super.getProperties();
		map.put("messageMap", this.getMessageMap());
		map.put("clientMessageTransfer", this.getClientMessageTransfer());
		map.put("cookieCheck", this.getCookieCheck());
		// フレーム情報の設定。
		if (!this.isNoFrame()) {
			String htmlpath = this.getAppropriatePath(this.getPage().getPageFramePath() + "/Frame.html", this.getPage().getRequest());
			String htmltext = this.getWebResource(htmlpath);
			if (htmltext != null) {
				htmltext = htmltext.replaceAll("\\$\\{context\\}", this.getPage().getRequest().getContextPath());
				String frameHead = this.getHtmlHead(htmltext);
				map.put("frameHead", frameHead);
				String frameBody = this.convertIdAttribute(this.getHtmlBody(htmltext));
				map.put("frameBody", frameBody);
			}
		}
		map.put("clientLogLevel", DataFormsServlet.getClientLogLevel());
		Map<String, Object> dlgmap = new HashMap<String, Object>();
		for (String key : this.dialogMap.keySet()) {
			dlgmap.put(key, this.dialogMap.get(key).getProperties());
		}
		map.put("dialogMap", dlgmap);
		map.put("contextPath", this.getRequest().getContextPath());
		map.put("framePath", this.getPageFramePath());
		map.put("noFrame", this.isNoFrame());
		map.put("pageExt", this.getPageExt());
		map.put("errorPage", this.getErrorPage());
		map.put("pageTitle", this.getPageTitle());
		map.put("topPage", Page.getTopPage());
		map.put("browserBackButton", Page.getBrowserBackButton());
		map.put("dataformsVersion", Page.dataformsVersion);
		map.put("dataformsVendor", Page.dataformsVendor);
		Map<String, Object> uinfo = this.getUserInfo();
		Map<String, Object> userInfo = new HashMap<String, Object>();
		if (uinfo != null) {
			userInfo.put("userId", uinfo.get("userId"));
			userInfo.put("loginId", uinfo.get("loginId"));
			userInfo.put("userName", uinfo.get("userName"));
			userInfo.put("mailAddress", uinfo.get("mailAddress"));
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> att = (List<Map<String, Object>>) uinfo.get("attTable");
			for (Map<String, Object> a: att) {
				userInfo.put((String) a.get("userAttributeType"), a.get("userAttributeValue"));
			}
		}
		map.put(WebEntryPoint.USER_INFO, userInfo);
		map.put("useUniqueId", WebComponent.getUseUniqueId());
		return map;
	}

    /**
     * 要求情報への弱参照。
     */
    private WeakReference<HttpServletRequest> request = null;

    /**
     * 応答情報への弱参照。
     */
    private WeakReference<HttpServletResponse> response = null;



	/**
	 * 要求情報を取得します。
	 * @return 要求情報。
	 */
    @Override
	public HttpServletRequest getRequest() {
		return request.get();
	}

	/**
	 * 要求情報を設定します。
	 * @param request 要求情報。
	 */
    @Override
	public void setRequest(final HttpServletRequest request) {
		this.request = new WeakReference<HttpServletRequest>(request);
	}

	/**
	 * 応答情報を取得します。
	 * @return 応答情報。
	 */
    @Override
	public HttpServletResponse getResponse() {
		return response.get();
	}

	/**
	 * 応答情報を設定します。
	 * @param response 応答情報。
	 */
    @Override
	public void setResponse(final HttpServletResponse response) {
		this.response = new WeakReference<HttpServletResponse>(response);
	}

	/**
	 * dataforms.jarのバージョンを取得します。
	 * @throws Exception 例外。
	 *
	 */
	public void initDataformsVersion() throws Exception {
		if (Page.dataformsVersion == null) {
			byte[] manifest = this.getBinaryWebResource("/dataforms.mf");
			if (manifest != null) {
				ByteArrayInputStream is = new ByteArrayInputStream(manifest);
				try {
					Manifest m = new Manifest(is);
					Attributes a = m.getMainAttributes();
					Page.dataformsVersion = a.getValue("Implementation-Version");
					Page.dataformsVendor = a.getValue("Implementation-Vendor");
					Page.dataformsCreateDate = a.getValue("CreatedTime");
				} finally {
					is.close();
				}
			}
		}
	}

	@Override
	public void init() throws Exception {
		WebComponent.getFunctionMap().init();
		this.initDataformsVersion();
		super.init();
		this.initRealId("mainDiv");
	}


    /**
	 * ページ情報を取得します。
	 * @param params パラメータ。
	 * @return 応答。
	 * @throws Exception 例外。
	 */
    @WebMethod(useDB = true)
	public JsonResponse getPageInfo(final Map<String, Object> params) throws Exception {
		this.init();
		JsonResponse ret = new JsonResponse(JsonResponse.SUCCESS, this.getProperties());
		return ret;
	}

	/**
	 * 現在の言語コードを取得する。
	 * <pre>
	 * アクセスしたブラウザの言語コードがサポートされていない場合、
	 * "default"を設定する。
	 * </pre>
	 * @return 現在の言語コード。
	 */
	public String getCurrentLanguage() {
		String lang = DataFormsServlet.getFixedLanguage();
		if (lang == null) {
			lang = this.getRequest().getLocale().getLanguage();
		}
//		log.debug("request language=" + lang);
		if (DataFormsServlet.getSupportLanguage().indexOf(lang) < 0) {
			lang = "default";
		}
		return lang;
	}

	/**
	 * フレームのパスを取得します。
	 * @return レイアウトのパス。
	 */
    public static String getFramePath() {
		return framePath;
	}

    /**
     * フレームのパスを設定する。
     * @param framePath レイアウトのパス。
     */
	public static void setFramePath(final String framePath) {
		Page.framePath = framePath;
	}

	/**
	 * ページのフレームパスを取得します。
	 * <pre>
	 * 	基本的にPage.getFramePathの値を返します。
	 *  各ページごとに異なるフレームを使いたい場合、このメソッドをオーバーライドします。
	 * </pre>
	 * @return ページのフレームパス。
	 */
	public String getPageFramePath() {
		return Page.getFramePath();
	}

	/**
	 * メニュー項目フラグを取得します。
	 * @return メニュー項目フラグ。
	 */
	public boolean isMenuItem() {
		return menuItem;
	}

	/**
	 * メニュー項目フラグを設定します。
	 * @param menuItem メニュー項目フラグ。
	 */
	public void setMenuItem(final boolean menuItem) {
		this.menuItem = menuItem;
	}



	/**
	 * フレーム無フラグを取得します。
	 * @return フレーム無フラグ。
	 */
	public boolean isNoFrame() {
		return noFrame;
	}

	/**
	 * フレーム無フラグを設定します。
	 * @param noFrame フレーム無フラグ。
	 */
	public void setNoFrame(final boolean noFrame) {
		this.noFrame = noFrame;
	}

	/**
	 * ページ名称を取得します。
	 * @return ページ名称。
	 * @throws Exception 例外。
	 */
	protected String getPageTitle() throws Exception {
		String ret = WebComponent.getFunctionMap().getPageName(this);
		logger.debug("getPageTitle()=" + ret);
		return ret;
	}

	/**
	 * メニューの表示名を飾る場合オーバーライドします。
	 * <pre>
	 * メニュー名称にアイコン等を追加する場合にオーバーライドします。
	 * </pre>
	 * @param menuName メニュー名称。
	 * @return 変換後のメニュー名称。
	 */
	public String decorateMenuName(final String menuName) {
		return menuName;
	}

	/**
	 * ブラウザの戻るボタンの許可状態を取得します。
	 * @return ブラウザの戻るボタンの許可状態。
	 */
	public static String getBrowserBackButton() {
		return browserBackButton;
	}

	/**
	 * ブラウザの戻るボタン許可状態を設定します。
	 * @param browserBackButton ブラウザの戻るボタンの許可状態。
	 */
	public static void setBrowserBackButton(final String browserBackButton) {
		Page.browserBackButton = browserBackButton;
	}

	/**
	 * トップページのURLを取得します。
	 * @return トップページのURL。
	 */
	public static String getTopPage() {
		return topPage;
	}

	/**
	 * トップページのURLを設定します。
	 * @param topPage トップページのURL。
	 */
	public static void setTopPage(final String topPage) {
		Page.topPage = topPage;
	}

	/**
	 * dataformsのバージョンを取得します。
	 * @return dataformsのバージョン。
	 */
	public static String getDataformsVersion() {
		return dataformsVersion;
	}

	/**
	 * dataformsのベンダーを取得します。
	 * @return dataformsのベンダー。
	 *
	 */
	public static String getDataformsVendor() {
		return dataformsVendor;
	}

	/**
	 * dataformsの作成日時を取得します。
	 * @return dataformsの作成日時。
	 *
	 */
	public static String getDataformsCreateDate() {
		return dataformsCreateDate;
	}

	/**
	 * 指定された名前のクッキーを取得します。
	 * @param name クッキーの名称。
	 * @return 取得されたクッキー。
	 */
	public Cookie getCookie(final String name) {
		Cookie ret = null;
		Cookie[] cookies = this.getRequest().getCookies();
		if (cookies != null) {
			for (Cookie c: cookies) {
				if (c.getName().equals(name)) {
					ret = c;
					break;
				}
			}
		}
		return ret;
	}

	/**
	 * 自動ログインを実行します。
	 *
	 * <pre>
	 * Cookieに自動ログイン情報が設定されている場合、自動的にログインします。
	 * </pre>
	 *
	 *
	 * @throws Exception 例外。
	 */
	public void autoLogin() throws Exception {
		AutoLoginCookie.autoLogin(this);
	}


	/**
	 * メニューに設定するURLを取得します。
	 * <pre>
	 * メニューに設定するURLを強制的に変更したい場合、
	 * このメソッドをオーバライドします。
	 * メニューから他サイトなどのURLに遷移させたい場合、
	 * このこのメソットで所定のURLを指定します。
	 * </pre>
	 * @return メニューに設定するURL。
	 */
	public String getMenuUrl() {
		return null;
	}

	/**
	 * メニューの&lt;a&gt;&lt;/a&gt;タグのtargetアトリビュートを取得します。
	 * <pre>
	 * メニューから別ウィンドウへのページ表示を行いたい場合指定します。
	 * </pre>
	 * @return メニューの&lt;a&gt;&lt;/a&gt;タグのtargetアトリビュート。
	 *
	 */
	public String getMenuTarget() {
		return null;
	}

	/**
	 * Daoのインスタンスを取得します。
	 * @return Daoのインスタンス。
	 * @throws Exception 例外。
	 */
	private Dao getDaoInstance() throws Exception {
		try {
			Method m = this.getClass().getMethod("getDaoClass");
			if (m != null) {
				@SuppressWarnings("unchecked")
				Class<? extends Dao> cl = (Class<? extends Dao>) m.invoke(this);
				if (cl != null) {
					return cl.getConstructor().newInstance();
				} else {
					return null;
				}
			} else {
				return null;
			}
		} catch (NoSuchMethodException e) {
			return null;
		}
	}
	/**
	 * ページパータンを取得します。
	 * @return ページパータン。
	 * @throws Exception 例外。
	 */
	public String getPagePattern() throws Exception {
		String qf = "0";
		String qrf = "0";
		String ef = "0";
		Map<String, WebComponent> fm = this.getFormMap();
		for (String key: fm.keySet()) {
			WebComponent cmp = fm.get(key);
			if (cmp != null) {
				if (cmp instanceof QueryForm) {
					qf = "1";
				} else if (cmp instanceof QueryResultForm) {
					qrf = "1";
				} else if (cmp instanceof EditForm) {
					ef = "1";
				}
			}
		}
		Dao dao = this.getDaoInstance();
		if (dao != null) {
			if ("1".equals(ef)) {
				logger.debug("page dao=" + dao.getClass().getName());
				if (dao instanceof QuerySetDao) {
					QuerySetDao querySetDao = (QuerySetDao) dao;
					Query sq = querySetDao.getSingleRecordQuery();
					List<Query> mql = querySetDao.getMultiRecordQueryList();
					if (sq == null && mql != null) {
						ef = "2";
					}
				}
			}
		}
		String pp = PagePatternSelectField.getPagePattern(this.getPage(), qf,  qrf, ef);
		return pp;
	}

	/**
	 * ページの名称を取得します。
	 * @return ページの名称。
	 */
	public String getPageName() {
		return "";
	}
	
	/**
	 * ページの説明を取得します。
	 * @return ページの説明。
	 */
	public String getPageDescription() {
		return "";
	}
	
}
