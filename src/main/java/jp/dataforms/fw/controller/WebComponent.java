package jp.dataforms.fw.controller;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.servlet.http.HttpServletRequest;
import jp.dataforms.fw.dao.JDBCConnectableObject;
import jp.dataforms.fw.htmltable.HtmlTable;
import jp.dataforms.fw.menu.FunctionMap;
import jp.dataforms.fw.servlet.DataFormsServlet;
import jp.dataforms.fw.util.FileUtil;
import jp.dataforms.fw.util.StringUtil;
import lombok.Getter;
import lombok.Setter;


/**
 * WEBコンポーネントクラス。
 * <pre>
 * WEBブラウザ側に情報を送信するオブジェクトの基本クラスです。
 * </pre>
 */
public class WebComponent implements JDBCConnectableObject {

    /**
     * Logger.
     */
    private static Logger logger = LogManager.getLogger(WebComponent.class.getName());

    /**
     * フレームワークのペースパッケージ。
     */
    public static final String BASE_PACKAGE = "jp.dataforms.fw";
    
	/**
	 * サーブレッド。
	 */
	private static WeakReference<DataFormsServlet> servlet = null;

	/**
	 * WebEntryPointへの参照。
	 * <pre>
	 * スレッドごとに記録します。
	 * </pre>
	 */
	private static ThreadLocal<WebEntryPoint> entryPoint = new ThreadLocal<WebEntryPoint>();

	/**
	 * Pathとパッケージの変換クラス。
	 */
	@Getter
	@Setter
	private static FunctionMap functionMap = null;
	
	/**
	 * コンポーネントマップ。
	 */
	private Map<String, WebComponent> componentMap = new HashMap<String, WebComponent>();


	/**
	 * コンポーネントリスト。
	 * <pre>
	 * コンポーネントの追加順を保持するためのリスト。
	 * </pre>
	 */
	private List<WebComponent> componentList = new ArrayList<WebComponent>();


	/**
	 * 親コンポーネントへのポインタ。
	 */
	private WeakReference<WebComponent> parent = null;

	/**
	 * クラス名からデフォルトのフィールドIDを取得します。
	 * @return デフォルトのフィールドID。
	 */
	public String getDefaultId() {
		String name = this.getClass().getSimpleName();
		StringBuilder sb = new StringBuilder(name.replaceAll("Field$", ""));
		char c = sb.charAt(0);
		sb.setCharAt(0, Character.toLowerCase(c));
		return sb.toString();
	}

	/**
	 * 別名が指定されていた場合それを取得します。
	 * @return 別名。
	 */
	public String getAlias() {
		if (this.id.equals(this.getDefaultId())) {
			return null;
		} else {
			return this.id;
		}
	}


	/**
	 * コンポーネントマップを取得します。
	 * @return コンポーネントマップ。
	 */
	public Map<String, WebComponent> getComponentMap() {
		return componentMap;
	}

	/**
	 * コンポーネントリストを取得します。
	 * <pre>
	 * コンポーネントの追加順を保持したリストを返します。
	 * </pre>
	 * @return コンポーネントリスト。
	 */
	public List<WebComponent> getComponentList() {
		return this.componentList;
	}

	/**
	 * 親コンポーネントに配置されたタイミングで呼ばれます。
	 * <pre>
	 * 親に応じて初期化処理を変更する場合に実装します。
	 * </pre>
	 */
	protected void onBind() {

	}

	/**
	 * コンポーネントを追加します。
	 * @param comp コンポーネント。
	 */
	public final void addComponent(final WebComponent comp) {
		comp.parent = new WeakReference<WebComponent>(this);
		if (this.componentMap.containsKey(comp.getId())) {
			logger.error("Dupulicate component id " + comp.getId());
		}
		this.componentMap.put(comp.getId(), comp);
		this.componentList.add(comp);
		comp.onBind();
	}

	/**
	 * 親となるコンポーネントを取得します。
	 * @return 親となるコンポーネント。
	 */
	public WebComponent getParent() {
		if (this.parent != null) {
			return this.parent.get();
		}
		return null;
	}

	/**
	 * 指定されたIDのコンポーネントを取得します。
	 * @param id コンポーネントID。
	 * @return コンポーネント。
	 */
	public WebComponent getComponent(final String id) {
		if (this instanceof HtmlTable) {
			HtmlTable tbl = (HtmlTable) this;
			return tbl.getFieldList().get(id);
		} else {
			WebComponent cmp = this.componentMap.get(id);
			return cmp;
		}
	}

	/**
	 * FormのID。
	 */
	private String id = null;

	/**
	 * IDを取得します。
	 * @return ID.
	 */
	public String getId() {
		return id;
	}

	/**
	 * IDを設定します。
	 * @param id ID.
	 */
	public void setId(final String id) {
		this.id = id;
	}

	/**
	 * Viewのパスを取得します。
	 * @return Viewのパス。
	 */
	protected String getViewPath() {
		String clsname = this.getClass().getName();
//		return clsname.replaceAll("\\.", "/");
		
		FunctionMap conv = WebComponent.getFunctionMap();
		return conv.getWebPath(clsname);
	}

	/**
	 * デフォルトのhtml,jsパスを取得します。
	 * <pre>
	 * 基本的にクラス名に対応したHTMLのパスを返します。
	 * </pre>
	 * @param cls パスを取得するクラス。
	 * @return デフォルトのhtml,jsパス。
	 */
	public String getWebResourcePath(final Class<?> cls) {
		String clsname = cls.getName();
		FunctionMap conv = WebComponent.getFunctionMap();
		return conv.getWebPath(clsname);
	}

	/**
	 * クラスに対応するスクリプトのパスを取得します。
	 * <pre>
	 * このクラスに対応しスクリプトファイルが存在しない場合、
	 * 親クラスのスクリプトファイルを再帰的に検索します。
	 * </pre>
	 * @param cls クラス。
	 * @return クラスに対応するスクリプトのパス。
	 * @throws Exception 例外。
	 */
	private String getScriptPath(final Class<?> cls) throws Exception {
		String jspath = this.getWebResourcePath(cls) + ".js";
		logger.info("getScriptPath:jspath = " + jspath);
		String script = this.getWebResource("/" + jspath);
		if (StringUtil.isBlank(script)) {
			jspath = this.getScriptPath(cls.getSuperclass());
		}
		return jspath;
	}

	/**
	 * スクリプトパスを取得します。
	 * <pre>
	 * スクリプトファイルが存在しない場合、スーパークラスのスクリプトを検索します。
	 * </pre>
	 * @return スクリプトパス。
	 * @throws Exception 例外。
	 */
	protected String getScriptPath() throws Exception {
		return this.getScriptPath(this.getClass());
	}

	/**
	 * 指定クラスのスクリプトパスを取得します。
	 * <pre>
	 * スクリプトファイルが存在しなくても、そのパスを返します。
	 * </pre>
	 * @param cls 指定クラス。
	 * @return 自分自身のスクリプトパス。
	 */
	protected String getClassScriptPath(final Class<?> cls) throws Exception {
		logger.debug("getClassScriptPath:cls=" + cls.getName());
/*		if (MenuForm.class.isAssignableFrom(cls) || LoginInfoForm.class.isAssignableFrom(cls)) {
			// MenuForm等はframeのパスにある。
			WebComponent cmp = null;
			try {
				cmp = (WebComponent) cls.getDeclaredConstructor().newInstance();
			} catch (Exception ex) {
				logger.error(ex.getMessage(), ex);
				throw new ApplicationError(ex);
			}
			return "/" + cmp.getWebResourcePath(cls) + ".js";
		} else {
*/			String clsname = cls.getName();
			FunctionMap conv = FunctionMap.getAppFunctionMap();
			String ret = conv.getWebPath(clsname) + ".js";
			logger.debug("getClassScriptPath() = " + ret);
			return ret;
			// String jspath = clsname.replaceAll("\\.", "/") + ".js";
			// return "/" + jspath;
//		}
	}


	/**
	 * Javascriptのクラス名を取得します。
	 * @return Javascriptのクラス。
	 * @throws Exception 例外。
	 */
	protected String getJsClass() throws Exception {
		String jspath = this.getScriptPath();
		String jsclass = jspath.replaceAll("\\.js", "");
		int idx = jsclass.lastIndexOf('/');
		if (idx > 0) {
			jsclass = jsclass.substring(idx + 1);
		}
		return jsclass;
	}


	/**
	 * コンポーネント周辺に追加するタグを記録したHTMLファイル。
	 */
	private String additionalHtml = null;

	/**
	 * コンポーネント周辺に追加するタグを記録したHTMLファイルを取得します。
	 * @return コンポーネント周辺に追加するタグを記録したHTMLファイル。
	 */
	public String getAdditionalHtml() {
		return additionalHtml;
	}

	/**
	 * コンポーネント周辺に追加するタグを記録したHTMLファイルを設定します。
	 * @param additionalHtml コンポーネント周辺に追加するタグを記録したHTMLファイル。
	 */
	public void setAdditionalHtml(final String additionalHtml) {
		this.additionalHtml = additionalHtml;
	}

	/**
	 * 追加HTMLタグを取得します。
	 * @return 追加HTMLタグの文字列。
	 * @throws Exception 例外。
	 */
	private String getAdditionalHtmlText() throws Exception {
		if (this.additionalHtml != null) {
			logger.debug(() -> "additionalHtml=" + this.additionalHtml);
			String htmlpath = this.getAppropriatePath(this.additionalHtml, this.getPage().getRequest());
			String htmltext = this.getWebResource(htmlpath);
			if (htmltext != null) {
				htmltext = this.getHtmlBody(htmltext);
			}
			return htmltext;
		} else {
			return null;
		}
	}


	/**
	 * 各オブジェクトのプロパティマップを作成します。。
	 * <pre>
	 * 各クラスの情報をJSON形式で渡すため必要なプロパティのマップを作成します。
	 * </pre>
	 * @return クライアントに渡すプロパティ情報。
	 * @throws Exception 例外。
	 */
	public Map<String, Object> getProperties() throws Exception {
		Map<String, Object> obj = new HashMap<String, Object>();
		if (this.id != null) {
			obj.put("id", this.id);
		}
//		String context = this.getPage().getRequest().getContextPath();
		obj.put("className", this.getClass().getSimpleName());
		obj.put("path", this.getViewPath());
		String jspath = this.getScriptPath();
		String t = this.getLastUpdate(jspath);
		obj.put("jsPath", jspath + "?t=" + t);
		obj.put("jsClass", this.getJsClass());
		String additionalHtmlText = this.getAdditionalHtmlText();
		if (additionalHtmlText != null) {
//			logger.debug("id変換後 html=" + this.convertIdArrtibute(additionalHtmlText));
			obj.put("additionalHtmlText", this.convertIdAttribute(additionalHtmlText));
		}
		obj.put("realId", this.getRealId());
		return obj;
	}

	/**
	 * サーブレットを設定します。
	 * @param servlet サーブレット。
	 */
	public static void setServlet(final DataFormsServlet servlet) {
		WebComponent.servlet = new WeakReference<DataFormsServlet>(servlet);
	}

	/**
	 * サーブレットを取得します。
	 * @return サーブレット。
	 */
	public static DataFormsServlet getServlet() {
		return WebComponent.servlet.get();
	}

    /**
     * WEBエントリーポイントを設定します。
     * @param epoint WEBエントリーポイント。
     */
	public void setWebEntryPoint(final WebEntryPoint epoint) {
		WebComponent.entryPoint.set(epoint);
	}

	/**
     * WEBエントリーポイントを取得します。
	 * @return WEBエントリーポイント。
	 */
	public WebEntryPoint getWebEntryPoint() {
		return (WebEntryPoint) WebComponent.entryPoint.get();
	}

	/**
	 * ページを取得します。
	 * @return ページ。
	 */
	public Page getPage() {
		return (Page) WebComponent.entryPoint.get();
	}

	/**
	 * WEBエントリーポイントを削除します。
	 */
	public void releaseWebEntryPoint() {
		WebComponent.entryPoint.remove();
	}

	/**
	 * JDBC接続を取得します。
	 * @return JDBC接続。
	 */
	@Override
	public Connection getConnection() {
		return WebComponent.entryPoint.get().getConnection();
	}

	/**
	 * 初期化処理を行います。
	 * <pre>
	 * ページが表示されると、対応するjavascriptページクラスのinitメソッドが呼び出されます。
	 * この処理の中でページjavaクラスのPageクラスの情報取得メソッド(getPageInfo)を呼び出します。
	 * getPageInfoは、Pageクラスのinitを呼び出し、ページ内の全コンポーネントのinitが呼び出されます。
	 * コンストラクタの中ではJDBC接続が利用できないため、DBの利用が必要な初期化はこのメソッドで行います。
	 * </pre>
	 * @throws Exception 例外。
	 */
	public void init() throws Exception {
		Map<String, WebComponent> m = this.getComponentMap();
		for (String key: m.keySet()) {
			WebComponent obj = m.get(key);
			obj.init();
		}
	}




	/**
	 * HtmlTable中の要素のIDかどうかをチェックします。
	 * @param id ID。
	 * @return {Boolean} HtmlTable中の要素の場合true。
	 */
	public boolean isHtmlTableElementId(final String id) {
		boolean ret = false;
		if (Pattern.matches("^[A-Za-z0-9]+\\[[0-9]+\\]\\.[A-Za-z0-9]+$", id)) {
			ret = true;
		}
		return ret;
	}


	/**
	 * テーブルのID部分を取得します。
	 * @param id HtmlTable中の各要素のID。
	 * @return テーブルのID。
	 */
	public String getHtmlTableId(final String id) {
		if (this.isHtmlTableElementId(id)) {
			String[] sp = id.split("[\\[\\]\\.]");
			return sp[0];
		} else {
			return null;
		}
	}

	/**
	 * テーブルのカラムID部分を取得します。
	 * @param id HtmlTable中の各要素のID。
	 * @return テーブルのカラムID。
	 */
	public String getHtmlTableColumnId(final String id) {
		if (this.isHtmlTableElementId(id)) {
			String[] sp = id.split("[\\[\\]\\.]");
			return sp[3];
		} else {
			return id;
		}
	}

	/**
	 * パラメータのキーから"xxx[x]"の部分を取得します。
	 * @param id フィールドID。
	 * @return テーブルの行ID。
	 */
	public String getHtmlTableRowId(final String id) {
		Pattern p = Pattern.compile("^.+\\[[0-9]+\\]");
		Matcher m = p.matcher(id);
		if (m.find()) {
			return m.group();
		} else {
			return null;
		}
	}


	/**
	 * パラメータのキーから"xxx[x]"の部分を取得します。
	 * @param param パラメータ。
	 * @return キー。
	 */
	public String getHtmlTableRowId(final Map<String, Object> param) {
		String key = param.keySet().iterator().next();
		return this.getHtmlTableRowId(key);
	}

	/**
	 * 親となるフォームを取得します。
	 * @return 親となるフォーム。
	 */
	public Form getParentForm() {
		WebComponent ret = this.getParent();
		while (ret != null) {
			if (ret instanceof Form) {
				return (Form) ret;
			}
			if (ret instanceof DataForms) {
				return null;
			}
			ret = ret.getParent();
		}
		return null;
	}

	/**
	 * Webリソースのキャッシュ。
	 */
	private static Map<String, String> webResourceCache = Collections.synchronizedMap(new HashMap<String, String>());

	/**
	 * Webリソースのタイムスタンプキャッシュ。
	 */
	private static Map<String, String> webResourceTimestampCache = Collections.synchronizedMap(new HashMap<String, String>());

	/**
	 * html,js,css等のWEBリソースを取得します。
	 * @param path パス。
	 * @return リソース。
	 * @throws Exception 例外。
	 */
	public String getWebResource(final String path) throws Exception {
		if (!WebComponent.webResourceCache.containsKey(path)) {
			String ret = readWebResource(path);
			WebComponent.webResourceCache.put(path, ret);
			//log.debug("getWebResource:text=" + ret);
		}
		String ret = WebComponent.webResourceCache.get(path);
		return ret;
	}


	/**
	 * 画像の等のバイナリWebリソースを取得します。
	 * @param path パス。
	 * @return バイナリWebリソース。
	 * @throws Exception 例外。
	 */
	public byte[] getBinaryWebResource(final String path) throws Exception {
		byte[] ret = null;
		URI uri = new URI(getWebResourceUrl(path));
		URL url = uri.toURL();
		logger.debug("webResourceUrl=" + url.toString());
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.connect();
		try {
			if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
				InputStream is = conn.getInputStream();
				ret = FileUtil.readInputStream(is);
			}
		} finally {
			conn.disconnect();
		}
		return ret;
	}

	/**
	 * Webリソースを読み込みます。
	 * @param path リソースのパス。
	 * @return Webリソースの文字列。
	 * @throws Exception 例外。
	 */
	private String readWebResource(final String path) throws Exception {
		URI uri = new URI(getWebResourceUrl(path));
		URL url = uri.toURL();
		String ret = "";
		logger.debug(() -> "webResourceUrl=" + url.toString());
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.connect();
		try {
			if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
				InputStream is = conn.getInputStream();
				byte[] buf = FileUtil.readInputStream(is);
				ret = new String(buf, DataFormsServlet.getEncoding());
				long d = conn.getLastModified();
				SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmss");
				//log.debug("timestamp:" + path + "=" + fmt.format(new Date(d)));
				webResourceTimestampCache.put(path, fmt.format(new Date(d)));
			}
		} finally {
			conn.disconnect();
		}
		return ret;
	}

	/**
	 * 指定されたパスのWebリソースの更新日付を取得します。
	 * @param path パス。
	 * @return 更新日付(yyyyMMddHHmmss形式)。
	 */
	protected String getLastUpdate(final String path) throws Exception {
		String ret = webResourceTimestampCache.get(path);
		if (ret == null) {
			this.readWebResource(path);
			ret = webResourceTimestampCache.get(path);
		}
		return ret;
	}

	/**
	 * Webリソースをバイナリ形式で読み込みます。
	 * @param path リソースのパス。
	 * @return Webリソース。
	 * @throws Exception 例外。
	 */
	public byte[] readBinaryWebResource(final String path) throws Exception {
		URI uri = new URI(getWebResourceUrl(path));
		URL url = uri.toURL();
		byte[] ret = null;
		logger.debug(() -> "webResourceUrl=" + url.toString());
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.connect();
		try {
			if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
				InputStream is = conn.getInputStream();
				byte[] buf = FileUtil.readInputStream(is);
				ret = buf;
			}
		} finally {
			conn.disconnect();
		}
		return ret;
	}

	/**
	 * WebリソースのURLを取得します。
	 * @param path パス。
	 * @return URL。
	 * @throws Exception 例外。
	 */
	public String getWebResourceUrl(final String path) throws Exception {
		HttpServletRequest req = this.getWebEntryPoint().getRequest();
		URI uri = new URI(req.getRequestURL().toString());
		URL accessurl = uri.toURL();
		String url = null;
		if (StringUtil.isBlank(DataFormsServlet.getWebResourceUrl())) {
			url = accessurl.getProtocol() + "://" + req.getServerName() + ":" + req.getServerPort() + req.getContextPath() + path;
		} else {
			url = DataFormsServlet.getWebResourceUrl() + req.getContextPath() + path;
		}
		logger.debug(() -> "getWebResourceUrl:path=" + path);
		logger.debug("getWebResourceUrl:url={}", url);
		return url;
	}

    /**
     * HTMLテキスト中のHeadの内容を取得します。
     * <pre>
     * HEAD TAGが無い場合html全体を返します。
     * </pre>
     * @param htmltext HTMLのテキスト。
     * @return Bodyの中のテキスト。
     */
    protected String getHtmlHead(final String htmltext) {
    	Pattern p = Pattern.compile("<head[\\s\\S]*>[\\s\\S]*</head>",
    			Pattern.MULTILINE
    	);
    	String ret = null;
    	Matcher m = p.matcher(htmltext);
    	if (m.find()) {
    		ret =  m.group().replaceAll("(<[Hh][Ee][Aa][Dd][\\s\\S]*?>)|(</[Hh][Ee][Aa][Dd]>)", "");
    	} else {
    		ret = htmltext;
    	}
    	StringBuffer sb = new StringBuffer();
    	String []lines = ret.split("[\r\n]");
    	for (String line: lines) {
    		line = line.trim();
    		if (StringUtil.isBlank(line.trim())) {
    			continue;
    		}
    		Matcher mat = Pattern.compile("<meta.*charset=[\"']UTF\\-8[\"'].*>", Pattern.CASE_INSENSITIVE).matcher(line);
    		if (mat.find()) {
    			continue;
    		}
    		mat = Pattern.compile("<link.*href=\"Frame.*\\.css\".*>", Pattern.CASE_INSENSITIVE).matcher(line);
    		if (mat.find()) {
    			continue;
    		}
    		mat = Pattern.compile("<title>.*</title>", Pattern.CASE_INSENSITIVE).matcher(line);
    		if (mat.find()) {
    			continue;
    		}
    		sb.append(line);
    		sb.append("\n");
    	}
    	return sb.toString();
    }


	/**
     * HTMLテキスト中のBodyの内容を取得します。
     * <pre>
     * BODY TAGが無い場合html全体を返します。
     * </pre>
     * @param htmltext HTMLのテキスト。
     * @return Bodyの中のテキスト。
     */
    protected String getHtmlBody(final String htmltext) {
    	Pattern p = Pattern.compile("<body[\\s\\S]*>[\\s\\S]*</body>",
    			Pattern.MULTILINE
    	);
    	Matcher m = p.matcher(htmltext);
    	if (m.find()) {
    		return m.group().replaceAll("(<[Bb][Oo][Dd][Yy][\\s\\S]*?>)|(</[Bb][Oo][Dd][Yy]>)", "");
    	} else {
    		return htmltext;
    	}
    }

	/**
	 * 指定された文字列リソースを取得します。
	 * @param path リソースパス。
	 * @return 文字列。
	 * @throws Exception 例外。
	 */
	protected String getStringResourse(final String path) throws Exception {
		Class<?> cls = this.getClass();
		InputStream is = cls.getResourceAsStream(path);
		String text = new String(FileUtil.readInputStream(is), DataFormsServlet.getEncoding());
		return text;
	}

	/**
	 * 言語に応じた適切なファイルパスを検索します。
	 * <pre>
     * ブラウザの言語に応じた適切なファイルのPathを取得します。
     * pathに/home/hoge.htmlが指定された場合、以下のような優先順位でパスを選択します。
     * 言語がjaの場合以下の順序でファイルの存在を確認し、最初に見つけたファイルのパスを返します。
     * 1. /home/hoge_ja.html
     * 2. /home/hoge.html
     * </pre>
     * @param path パス。
     * @param req HTTP要求情報。
     * @return html等のパス。
     * @throws Exception 例外。
	 */
	protected String getAppropriateLangPath(final String path, final HttpServletRequest req) throws Exception {
 		String spath = path;
 		if (spath.charAt(0) != '/') {
 			spath = "/" + spath;
 		}
 		String lang = DataFormsServlet.getFixedLanguage();
 		if (lang == null) {
 			lang = req.getLocale().getLanguage();
 		}
		String ext = FileUtil.getExtention(spath);
    	String p = spath.replaceAll("\\." + ext + "$", "_" + lang + "." + ext);
    	if (StringUtil.isBlank(this.getWebResource(p))) {
        	p = spath.replaceAll("\\." + ext + "$", "." + ext);
           	if (StringUtil.isBlank(this.getWebResource(p))) {
            	spath = null;
        	} else {
        		spath = p;
        	}
    	} else {
    		spath = p;
    	}
   		return spath;
	}


    /**
     * リクエストに応じた適切なファイルpathを取得します。
     * <pre>
     * ブラウザの言語、ユーザエージェントに応じた適切なファイルのPathを取得します。
     * pathに/home/hoge.htmlが指定された場合、以下のような優先順位でパスを選択します。
     * UserAgentが携帯電話で言語がjaの場合以下の順序でファイルの存在を確認し、最初に見つけたファイルのパスを返します。
     * 1. /home/hoge_phone_ja.html
     * 2. /home/hoge_phone.html
     * 3. /home/hoge_ja.html
     * 4. /home/hoge.html
     * </pre>
     * @param path パス。
     * @param req HTTP要求情報。
     * @return html等のパス。
     * @throws Exception 例外。
     */
	public String getAppropriatePath(final String path, final HttpServletRequest req) throws Exception {
		String ua = req.getHeader("user-agent");
//		log.debug("user-agent:" + ua);
		String ret = null;
	    if (ua.indexOf("iPhone") > 0 || ua.indexOf("iPod") > 0 || ua.indexOf("Android") > 0 && ua.indexOf("Mobile") > 0) {
			String ext = FileUtil.getExtention(path);
	    	String htmlpath = path.replaceAll("\\." + ext + "$", "_phone." + ext);
			ret = getAppropriateLangPath(htmlpath, req);
	    }
	    if (ret == null) {
			ret = getAppropriateLangPath(path, req);
	    }
	    return ret;
	}

	/**
	 * ユニークidア使用フラグ。
	 * <pre>
	 * 全てのidアトリビュートをdata-idに変換し、idにはユニークなidを設定する場合true。
	 * </pre>
	 */
	private static Boolean useUniqueId = true;


	/**
	 * ユニークidア使用フラグを取得します。
	 * @return ユニークidア使用フラグ。
	 */
	public static Boolean getUseUniqueId() {
		return useUniqueId;
	}


	/**
	 * ユニークidア使用フラグを設定します。
	 * @param useUniqueId ユニークidア使用フラグ使用フラグ。
	 */
/*	public static void setUseUniqueId(final Boolean useUniqueId) {
		WebComponent.useUniqueId = useUniqueId;
	}
*/

	/**
	 * htmlのidアトリビュートをdata-idに変更する。
	 * <pre>
	 * web.xmlのuse-data-id-attributeがtrueの場合、idアトリビュートをdata-idに変換します。
	 * </pre>
	 *
	 * @param htmltext htmlのテキスト。
	 * @return idアトリビュートを変換したhtml。
	 */
	public String convertIdAttribute(final String htmltext) {
		if (WebComponent.useUniqueId) {
			return htmltext.replaceAll("(<.+?)(\\s+)(id)(\\s*?=)(.*?>)", "$1$2data-id$4$5");
		} else {
			return htmltext;
		}
	}

	/**
	 * ページ中でユニークなID。
	 */
	private String realId = null;

	/**
	 * ページ中でユニークな実際のIDを取得します。
	 * @return ページ中でユニークなID。
	 */
	public String getRealId() {
		return realId;
	}


	/**
	 * ページ中でユニークな実際のIDを設定します。
	 * @param realId ページ中でユニークな実際のID。
	 */
	public void setRealId(final String realId) {
		this.realId = realId;
	}

	/**
	 * ページにユニークな実際のIDを初期設定します。
	 * @param realId ユニーク実際のID。
	 */
	public void initRealId(final String realId) {
		String pid = realId;
		this.setRealId(pid);
		List<WebComponent> list = this.getComponentList();
		if (list != null) {
			for (WebComponent c: list) {
				if (this instanceof HtmlTable) {
					String uid = pid + "[0]." + c.getId();
					c.initRealId(uid);
				} else {
					String uid = pid + "." + c.getId();
					c.initRealId(uid);
				}
			}
		}
	}
	
	/**
	 * Originを取得する。
	 * @return Origin。
	 */
	public String getOrigin() {
		String origin = this.getPage().getRequest().getRequestURL().toString();
		Pattern p = Pattern.compile("^(.+?://.+?)/.*");
		Matcher m = p.matcher(origin);
		if (m.find()) {
			origin = m.group(1);
		}
		return origin;
	}

}
