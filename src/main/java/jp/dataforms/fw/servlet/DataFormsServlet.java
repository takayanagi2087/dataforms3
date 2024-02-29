package jp.dataforms.fw.servlet;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URLDecoder;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.sql.DataSource;

import org.apache.commons.fileupload2.core.DiskFileItem;
import org.apache.commons.fileupload2.core.DiskFileItemFactory;
import org.apache.commons.fileupload2.core.FileItem;
import org.apache.commons.fileupload2.jakarta.JakartaServletDiskFileUpload;
import org.apache.commons.fileupload2.jakarta.JakartaServletFileUpload;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jp.dataforms.fw.annotation.WebMethod;
import jp.dataforms.fw.app.backuprestore.page.BackupForm;
import jp.dataforms.fw.app.errorpage.ConfigErrorPage;
import jp.dataforms.fw.app.errorpage.ErrorPage;
import jp.dataforms.fw.app.login.page.LoginForm;
import jp.dataforms.fw.app.login.page.LoginInfoForm;
import jp.dataforms.fw.app.user.dao.UserInfoTable;
import jp.dataforms.fw.app.user.page.PasswordResetMailForm;
import jp.dataforms.fw.app.user.page.UserEditForm;
import jp.dataforms.fw.app.user.page.UserRegistForm;
import jp.dataforms.fw.controller.Page;
import jp.dataforms.fw.controller.WebComponent;
import jp.dataforms.fw.controller.WebEntryPoint;
import jp.dataforms.fw.dao.Constraint;
import jp.dataforms.fw.dao.ForeignKey;
import jp.dataforms.fw.dao.Index;
import jp.dataforms.fw.dao.JDBCConnectableObject;
import jp.dataforms.fw.dao.SubQuery;
import jp.dataforms.fw.dao.Table;
import jp.dataforms.fw.dao.TableRelation;
import jp.dataforms.fw.dao.UniqueKey;
import jp.dataforms.fw.dao.file.BlobFileStore;
import jp.dataforms.fw.dao.file.FileObject;
import jp.dataforms.fw.devtool.base.page.DeveloperPage;
import jp.dataforms.fw.devtool.db.dao.TableManagerDao;
import jp.dataforms.fw.devtool.db.page.DeveloperEditForm;
import jp.dataforms.fw.exception.ApplicationException;
import jp.dataforms.fw.exception.ApplicationException.ResponseMode;
import jp.dataforms.fw.exception.AuthoricationException;
import jp.dataforms.fw.mail.MailSender;
import jp.dataforms.fw.menu.FunctionMap;
import jp.dataforms.fw.menu.SideMenu;
import jp.dataforms.fw.response.JsonResponse;
import jp.dataforms.fw.response.Response;
import jp.dataforms.fw.util.AutoLoginCookie;
import jp.dataforms.fw.util.ClassFinder;
import jp.dataforms.fw.util.CryptUtil;
import jp.dataforms.fw.util.HttpRangeInfo;
import jp.dataforms.fw.util.MessagesUtil;
import jp.dataforms.fw.util.MessagesUtil.ClientMessageTransfer;
import jp.dataforms.fw.util.OnetimePasswordUtil;
import jp.dataforms.fw.util.StringUtil;
import net.arnx.jsonic.JSON;

/**
 * DataForms用サーブレットクラスです。
 * <pre>
 * *.dfにマッピングします。
 * /hoge/hogehoge/XxxPage.dfをアクセスすると、
 * ページクラスhoge.hogehoge.XxxPageクラスのインスタンスを作成し、getHtmlメソッドを呼び出します。
 * getHtmlは/hoge/hogehoge/XxxPage.htmlや/hoge/hogehoge/XxxPage.jsからページを構成し表示します。
 * 次のようにメソッドを指定してアクセスすると、
 * /hoge/hogehoge/XxxPage.df?dfMethod=compid.method
 * hoge.hogehoge.XxxPageのインスタンスを作成した後、compidを持つコンポーネントを検索し、
 * そのmethodを呼び出します。
 * </pre>
 *
 */
@WebServlet(name = "DataFormsServlet", displayName = "DataFormsServlet", urlPatterns = {"*.df" })
public class DataFormsServlet extends HttpServlet {

	/**
	 * IE許可のシンボル。
	 */
	public static final String ALLOW_IE = "allowIE";

	/**
	 * BABELコマンドのシンボル。
	 */
	public static final String BABEL_COMMAND = "babelCommand";

	/**
	 * BABEL standalineのシンボル。
	 */
	public static final String BABEL_STANDALONE = "standalone";

	/**
	 * BABELの作業領域。
	 */
	public static final String BABEL_WORK = "babelWork";

	/**
	 * 実行のメソッド名。
	 */
	private static final String EXEC_METHOD = "exec";

	/**
	 * UID.
	 */
	private static final long serialVersionUID = -8576472991434646040L;

	/**
	 * Logger.
	 */
//	private static Logger logger = Logger.getLogger(DataFormsServlet.class.getName());
	private static Logger logger = LogManager.getLogger(DataFormsServlet.class.getName());

	/**
	 * jndi-prefix.
	 */
	private static String jndiPrefix = null;

	/**
	 * データソース名称.
	 */
	private static String dataSourceName = null;

	/**
	 * WEBリソースアクセス用URL。
	 */
	private static String webResourceUrl = null;

	/**
	 * 文字コード.
	 */
	private static String encoding = null;

	/**
	 * JSONの出力モード指定.
	 */
	private static boolean jsonDebug = false;

	/**
	 * 一時ファイル領域.
	 */
	private static String tempDir = null;

	/**
	 * データエクスポート、インポートディレクトリ.
	 */
	private static String exportImportDir = null;

	/**
	 * CSSとSCRIPT.
	 */
	private static String cssAndScript = null;

	/**
	 * エラーページ.
	 */
	private static String errorPage = null;

	/**
	 * javascriptでのバリデーションを有効にする.
	 */
	private static boolean clientValidation = true;


	/**
	 * javascriptのログレベルを設定.
	 */
	private static String clientLogLevel = "info";

	/**
	 * アップロードデータフォルダ.
	 */
	private static String uploadDataFolder = null;


	/**
	 * データソースのオブジェクト.
	 */
	private DataSource dataSource = null;


	/**
	 * サポート言語.
	 */
	private static String supportLanguage = null;

	/**
	 * 固定言語.
	 */
	private static String fixedLanguage = null;

	/**
	 * 開発ツールの無効化フラグ。
	 */
	private static boolean disableDeveloperTools = true;


	/**
	 * QueryStringを暗号化する際に使用するパスワード。
	 */
	private static String queryStringCryptPassword = null;

	/**
	 * 設定の状態.
	 */
	private static String configStatus = null;

	/**
	 * ページオーバーライドマップ。
	 */
	private static Map<String, String> pageOverrideMap = new HashMap<String, String>();

	/**
	 * サーブレットインスタンス設定Bean.
	 */
	private static ServletInstanceBean servletInstanceBean = null;


	/**
	 * CSRF対策用暗号化キー。
	 *
	 * <pre>
	 * CSRF対策のため送信する照合情報は、セッションIDを以下のパスワードで暗号化して送信します。
	 * </pre>
	 */
	private static String csrfSessionidCrypPassword = null;


	/**
	 * Apache-FOPの設定ファイルのバス。
	 */
	private static String apacheFopConfig = "/WEB-INF/apachefop/fop.xconf";

	/**
	 * Pageの拡張子を取得します。
	 * <pre>
	 * Servletアノテーションの先頭のURLパターンから、拡張子を取得します。
	 * </pre>
	 * @return Pageの拡張子。
	 */
	public String getPageExt() {
		WebServlet an = this.getClass().getAnnotation(WebServlet.class);
		String[] uplist = an.urlPatterns();
		return uplist[0].substring(2);
	}


	/**
	 * CSRF対策用暗号化キーを取得します。
	 * @return CSRF対策用暗号化キー。
	 */
	public static String getCsrfSessionidCrypPassword() {
		return csrfSessionidCrypPassword;
	}

	/**
	 * ページオーバーライドマップを考慮したクラス名を取得します。
	 * @param name クラス名。
	 * @return 変換後のクラス名。
	 */
	public static String convertPageClassName(final String name) {
		String classname = name;
		if (pageOverrideMap.containsKey(name)) {
			classname = pageOverrideMap.get(name);
			logger.info("page-override=" + name + "->" + classname);
		}
		return classname;
	}

	/**
	 * ページオーバーライドマップを初期化します。
	 */
	private void initPageOverrideMap() {
		String json = this.getServletContext().getInitParameter("page-override");
		ArrayList<ArrayList<String>> list = JSON.decode(json);
		for (int i = 0; i < list.size(); i++) {
			DataFormsServlet.pageOverrideMap.put(list.get(i).get(0), list.get(i).get(1));
		}
	}


	/**
	 * WEBリソースアクセス用のURLを取得します。
	 *
	 * @return WEBリソースアクセス用のURL。
	 */
	public static String getWebResourceUrl() {
		return webResourceUrl;
	}

	/**
	 * 初期化パラメータを取得します。
	 * @throws ServletException 例外。
	 */
	@Override
	public void init() throws ServletException {
		this.initPageOverrideMap();
		DataFormsServlet.jndiPrefix = this.getServletContext().getInitParameter("jndi-prefix");
		if (DataFormsServlet.jndiPrefix == null) {
			DataFormsServlet.jndiPrefix = "java:/comp/env/";
		}
		DataFormsServlet.dataSourceName = this.getServletContext().getInitParameter("data-source");
		logger.info(() -> "init:dataSourceName=" + DataFormsServlet.dataSourceName);
		String webresurl= this.getServletContext().getInitParameter("web-resource-url");
		if (!StringUtil.isBlank(webresurl)) {
			DataFormsServlet.webResourceUrl = webresurl;
		}
		logger.info(() -> "init:webResourceUrl=" + DataFormsServlet.webResourceUrl);
		DataFormsServlet.encoding = this.getServletContext().getInitParameter("encoding");
		if (DataFormsServlet.encoding == null) {
			DataFormsServlet.encoding = "utf-8";
		}
		logger.info(() -> "init:encoding=" + DataFormsServlet.encoding);
		DataFormsServlet.jsonDebug = Boolean
				.parseBoolean(this.getServletContext().getInitParameter("json-debug") == null ? "false"
						: this.getServletContext().getInitParameter("json-debug"));
		logger.info(() -> "init:jsonDebug=" + DataFormsServlet.jsonDebug);
		DataFormsServlet.tempDir = this.getServletContext().getInitParameter("temp-dir");
		if (DataFormsServlet.tempDir == null) {
			DataFormsServlet.tempDir = "/tmp";
		}
		// 一時フォルダがない場合作成する。
		File tmp = new File(DataFormsServlet.tempDir);
		if (!tmp.exists()) {
			tmp.mkdirs();
		}
		logger.info(() -> "init:tempDir=" + DataFormsServlet.tempDir);
		DataFormsServlet.exportImportDir = this.getServletContext().getInitParameter("export-import-dir");
		if (DataFormsServlet.exportImportDir == null) {
			DataFormsServlet.exportImportDir = "/tmp/data";
		}
		logger.info(() -> "init:exportImportDir=" + DataFormsServlet.exportImportDir);
		DataFormsServlet.cssAndScript = this.getServletContext().getInitParameter("css-and-scripts");
		if (DataFormsServlet.cssAndScript == null) {
			DataFormsServlet.cssAndScript = "/frame/jslib.html";
		}
		logger.info(() -> "init:cssAndScript=" + DataFormsServlet.cssAndScript);
		DataFormsServlet.errorPage = this.getServletContext().getInitParameter("error-page");
		if (DataFormsServlet.errorPage == null) {
			DataFormsServlet.errorPage = "/dataforms/app/errorpage/ErrorPage." + this.getPageExt();
		} else {
			DataFormsServlet.errorPage += ("." + this.getPageExt());
		}
		logger.info(() -> "init:errorPage=" + DataFormsServlet.errorPage);
		DataFormsServlet.clientValidation = Boolean
				.parseBoolean(this.getServletContext().getInitParameter("client-validation") == null ? "true"
						: this.getServletContext().getInitParameter("client-validation"));
		logger.info(() -> "init:clientValidation=" + DataFormsServlet.clientValidation);
		DataFormsServlet.clientLogLevel = this.getServletContext().getInitParameter("client-log-level");
		if (DataFormsServlet.clientLogLevel == null) {
			DataFormsServlet.clientLogLevel = "info";
		}
		logger.info(() -> "init:clientLogLevel=" + DataFormsServlet.clientLogLevel);
		DataFormsServlet.uploadDataFolder = this.getServletContext().getInitParameter("upload-data-folder");
		if (DataFormsServlet.uploadDataFolder == null) {
			DataFormsServlet.uploadDataFolder = "/uploadData";
		}
		logger.info(() -> "init:uploadDataFolder=" + DataFormsServlet.uploadDataFolder);
		DataFormsServlet.supportLanguage = this.getServletContext().getInitParameter("support-language");
		if (DataFormsServlet.supportLanguage == null) {
			DataFormsServlet.supportLanguage = "ja";
		}
		logger.info(() -> "init:supportLanguage=" + DataFormsServlet.supportLanguage);
		DataFormsServlet.fixedLanguage = this.getServletContext().getInitParameter("fixed-language");
		logger.info(() -> "init:fixedLanguage=" + DataFormsServlet.fixedLanguage);
		DataFormsServlet.disableDeveloperTools = Boolean
				.parseBoolean(this.getServletContext().getInitParameter("disable-developer-tools") == null ? "true"
						: this.getServletContext().getInitParameter("disable-developer-tools"));
		logger.info(() -> "init:disableDeveloperTools=" + DataFormsServlet.disableDeveloperTools);

		this.initPassword();
		this.initOnetimePassword();

		DataFormsServlet.cookieCheck = Boolean.parseBoolean(
				this.getServletContext().getInitParameter("cookie-check") == null ? "false"
				: this.getServletContext().getInitParameter("cookie-check")
		);
		Page.setFramePath(this.getServletContext().getInitParameter("frame-path") == null ? "/frame/default"
				: this.getServletContext().getInitParameter("frame-path"));
		logger.info(() -> "init:framePath=" + Page.getFramePath());
		Page.setAppcacheFile(this.getServletContext().getInitParameter("appcache-file"));
		this.getMessageProperties();
		String topPage = this.getServletContext().getInitParameter("top-page");
		if (!StringUtil.isBlank(topPage)) {
			topPage = topPage.replaceAll("\\.df$", "");
			Page.setTopPage(topPage);
		}
		String backButton = this.getServletContext().getInitParameter("browser-back-button");
		if (!StringUtil.isBlank(backButton)) {
			Page.setBrowserBackButton(backButton);
		}
		String autoLogin = this.getServletContext().getInitParameter("auto-login");
		if (!StringUtil.isBlank(autoLogin)) {
			AutoLoginCookie.setAutoLogin("enabled".equals(autoLogin));
		}
		String secureAutoLoginCookie = this.getServletContext().getInitParameter("secure-auto-login-cookie");
		if (!StringUtil.isBlank(secureAutoLoginCookie)) {
			AutoLoginCookie.setSecure("true".equals(secureAutoLoginCookie));
			OnetimePasswordUtil.setSecure("true".equals(secureAutoLoginCookie));
		}
		DeveloperPage.setJavaSourcePath(this.getServletContext().getInitParameter("java-source-path"));
		DeveloperPage.setWebSourcePath(this.getServletContext().getInitParameter("web-source-path"));
		String streamingBlockSize = this.getServletContext().getInitParameter("streaming-block-size");
		logger.debug(() -> "streamingBlockSize=" + streamingBlockSize);
		if (!StringUtil.isBlank(streamingBlockSize)) {
			@SuppressWarnings("unchecked")
			List<LinkedHashMap<String, Object>> bslist = (List<LinkedHashMap<String, Object>>) JSON.decode(streamingBlockSize, ArrayList.class);
			HttpRangeInfo.setBlockSizeList(bslist);
		}

		String contentTypeList = this.getServletContext().getInitParameter("content-type-list");
		logger.debug(() -> "contentTypeList=" + contentTypeList);
		if (!StringUtil.isBlank(contentTypeList)) {
			@SuppressWarnings("unchecked")
			List<LinkedHashMap<String, String>> ctlist = (List<LinkedHashMap<String, String>>) JSON.decode(contentTypeList, ArrayList.class);
			FileObject.setContentTypeList(ctlist);
		}
		String backupFileName = this.getServletContext().getInitParameter("backup-file-name");
		if (backupFileName == null) {
			backupFileName = "backup";
		}
		BackupForm.setBackupFileName(backupFileName);
		String apacheFopConfig = this.getServletContext().getInitParameter("apache-fop-config");
		logger.debug(() -> "apacheFopConfig=" + contentTypeList);
		if (apacheFopConfig != null) {
			DataFormsServlet.setApacheFopConfig(apacheFopConfig);
		}

		Boolean multiOpenMenu = Boolean.parseBoolean(
				this.getServletContext().getInitParameter("multi-open-menu") == null
				? "true"
				: this.getServletContext().getInitParameter("multi-open-menu")
		);
		SideMenu.setMultiOpenMenu(multiOpenMenu);

		Boolean useUniqueId = Boolean.parseBoolean(
				this.getServletContext().getInitParameter("use-unique-id") == null
				? "true"
				: this.getServletContext().getInitParameter("use-unique-id")
		);
		WebComponent.setUseUniqueId(useUniqueId);

		Boolean checkUserImport = Boolean.parseBoolean(this.getServletContext().getInitParameter("check-user-import") == null ? "true"
						: this.getServletContext().getInitParameter("check-user-import"));
		logger.info(() -> "init:checkUserImport=" + checkUserImport);
		DeveloperEditForm.setCheckUserImport(checkUserImport);

		String ieSupportJson = this.getServletContext().getInitParameter("ie-support");
		logger.debug(() -> "ieSupport=" + ieSupportJson);
		if (!StringUtil.isBlank(ieSupportJson)) {
			@SuppressWarnings("unchecked")
			Map<String, Object> p = JSON.decode(ieSupportJson, HashMap.class);
			DataFormsServlet.setIeSupport(p);
		}
		this.getUserEditFormConf();
		this.getUserRegistConf();
		super.init();
		WebComponent.setServlet(this);
		// DB存在チェック。
		this.checkDbConnection();
		// DBの存在チェック。
		this.checkDBStructure();
		// 制約マップを作成します。
		this.makeConstraintMap();
		this.setupServletInstanceBean();
		// パスとパッケージの対応表を設定する。
		WebComponent.setFunctionMap(new FunctionMap());
		
		{
/*			logger.debug("function json=" + JSON.encode(WebComponent.getFunctionMap(), true));
			
			String json = """
			// comment
			{
				"key1": "text1", "key2": "text2"
			}
			""";
			logger.debug("json=" + json);
			@SuppressWarnings("unchecked")
			Map<String, Object> map = (Map<String, Object>) JSON.decode(json, HashMap.class);
			logger.debug("map=" + map);
*/			
/*			ObjectMapper mapper = new ObjectMapper();
			try {
				Map<String, Object> map = mapper.readValue(json, new TypeReference<Map<String, Object>>(){});
				logger.debug("map=" + map);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
*/
			
		}

	}

	/**
	 * IEサポート情報。
	 */
	private static Map<String, Object> ieSupport = null;


	/**
	 * IEサポート情報を取得します。
	 * @return IEサポート情報。
	 */
	public static Map<String, Object> getIeSupport() {
		if (DataFormsServlet.ieSupport == null) {
			DataFormsServlet.ieSupport = new HashMap<String, Object>();
			DataFormsServlet.ieSupport.put(ALLOW_IE, false);
			DataFormsServlet.ieSupport.put(BABEL_COMMAND, BABEL_STANDALONE);
		}
		return DataFormsServlet.ieSupport;
	}

	/**
	 * IE許可フラグを取得します。
	 * @return IE許可フラグ。
	 */
	public static Boolean allowIe() {
		Boolean ret = (Boolean) DataFormsServlet.getIeSupport().get(ALLOW_IE);
		return ret;
	}

	/**
	 * BABELのコマンドを取得します。
	 * @return BABELのコマンド。
	 */
	public static String getBabelCommand() {
		String ret = (String) DataFormsServlet.getIeSupport().get(BABEL_COMMAND);
		return ret;
	}


	/**
	 * BABELの作業領域を取得します。
	 * @return BABELの作業領域。
	 */
	public static String getBabelWork() {
		String ret = (String) DataFormsServlet.getIeSupport().get(BABEL_WORK);
		return ret;
	}

	/**
	 * IEサポート情報を設定します。
	 * @param ieSupport IEサポート設定。
	 */
	public static void setIeSupport(final Map<String, Object> ieSupport) {
		DataFormsServlet.ieSupport = ieSupport;
	}



	/**
	 * デフォルト設定。
	 */
	private static final String DEFAULT_CRYPT_CONFIG = "			{\r\n" +
			"				\"algorithm\": \"des\",\r\n" +
			"				\"aesInitialVector\": \"Initi@lVect0r\",\r\n" +
			"				\"defaultPassword\": null,\r\n" +
			"				\"queryStringCryptPassword\": \"QueryStringPassword\",\r\n" +
			"				\"csrfSessionidCryptPassword\": \"CSRFpassword\"\r\n" +
			"			}\r\n" +
			"";


	/**
	 * パスワード関連の初期化。
	 *
	 */
	private void initPassword() {
		CryptUtil.initPasswordType(this.getServletContext());
		String conf = this.getServletContext().getInitParameter("crypt-config");
		if (conf == null) {
			conf = DEFAULT_CRYPT_CONFIG;
		}
		Map<String, Object> m = JSON.decode(conf);
		String algorithm = (String) m.get(CryptUtil.ALGORITHM);
		CryptUtil.setAlgorithm(algorithm);
		String initialVector = (String) m.get(CryptUtil.AES_INITIAL_VECTOR);
		CryptUtil.setAesInitialVector(initialVector);
		String defaultPassword = (String) m.get(CryptUtil.DEFAULT_PASSWORD);
		if (defaultPassword != null) {
			CryptUtil.setCryptPassword(defaultPassword);
		} else {
			CryptUtil.setCryptPassword(CryptUtil.DES_PASSWORD_OR_AES_KEY);
		}
		String queryStringCryptPassword = (String) m.get(CryptUtil.QUERY_STRING_CRYPT_PASSWORD);
		if (queryStringCryptPassword != null) {
			DataFormsServlet.setQueryStringCryptPassword(queryStringCryptPassword);
		} else {
			CryptUtil.setCryptPassword(CryptUtil.DES_PASSWORD_OR_AES_KEY);
		}
		String csrfSessionidCryptPassword = (String) m.get(CryptUtil.CSRF_SESSIONID_CRYPT_PASSWORD);
		if (csrfSessionidCryptPassword != null) {
			DataFormsServlet.csrfSessionidCrypPassword = csrfSessionidCryptPassword;
		} else {
			DataFormsServlet.csrfSessionidCrypPassword = null;
		}
	}


	/**
	 * ワンタイムパスワード関連情報を取得します。
	 */
	private void initOnetimePassword() {
		String conf = this.getServletContext().getInitParameter(OnetimePasswordUtil.CONFIG_KEY);
		if (conf != null) {
			Map<String, Object> m = JSON.decode(conf);
			OnetimePasswordUtil.setConfig(m);
		}
	}

	/**
	 * サポートする言語リストを取得します。
	 * @return サポートする言語リスト。
	 */
	public static List<String> getSupportLanguageList() {
		String langs = DataFormsServlet.getSupportLanguage();
		String[] langArray = langs.split(",");
		List<String> ret = new ArrayList<String>();
		for (String lang: langArray) {
			ret.add(lang.trim());
		}
		return ret;
	}


	/**
	 * ユーザ情報編集フォームの設定情報を取得します。
	 */
	public void getUserEditFormConf() {
		String conf = this.getServletContext().getInitParameter("user-edit-form-config");
		if (conf == null) {
			conf = "{\"requiredMailAddress\": true}";
		}
		Map<String, Object> m = JSON.decode(conf);
		UserEditForm.setConfig(m);
	}

	/**
	 * ユーザ登録関連設定を取得します。
	 */
	public void getUserRegistConf() {
		// ユーザ登録ページ関連設定
		LoginInfoForm.setUserRegistPage(this.getServletContext().getInitParameter("user-regist-page"));
		UserRegistForm.setUserEnablePage(this.getServletContext().getInitParameter("user-enable-page"));
		LoginForm.setPasswordResetMailPage(this.getServletContext().getInitParameter("password-reset-mail-page"));
		PasswordResetMailForm.setPasswordResetPage(this.getServletContext().getInitParameter("password-reset-page"));
		String conf = this.getServletContext().getInitParameter("user-regist-page-config");
		if (conf == null) {
			conf = "{\"loginIdIsMail\": true, \"mailCheck\": true, \"sendUserEnableMail\": true}";
			logger.debug("user-regist-page-config is missing. use default. " + conf);
		}
		Map<String, Object> m = JSON.decode(conf);
		logger.debug(() -> "m.class=" + m.getClass().getName());
		logger.debug(() -> "conf=" + m.toString());
		UserRegistForm.setConfig(m);
		// メール関連設定。
		String mailSession = this.getServletContext().getInitParameter("mail-session");
		if (mailSession != null) {
			MailSender.setJndiPrefix(this.getServletContext().getInitParameter("jndi-prefix"));
			MailSender.setMailSessionName(mailSession);
			MailSender.setMailFrom(this.getServletContext().getInitParameter("mail-from"));
		}
	}

	/**
	 * メッセージプロパティの設定情報を取得します。
	 */
	private void getMessageProperties() {
		String clientMessages = this.getServletContext().getInitParameter("client-messages");
		if (clientMessages == null) {
			clientMessages = "/frame/messages/ClientMessages";
		}
		String appClientMessages = this.getServletContext().getInitParameter("app-client-messages");
		if (appClientMessages == null) {
			appClientMessages = "/frame/messages/AppClientMessages";
		}
		String messages = this.getServletContext().getInitParameter("messages");
		if (messages == null) {
			messages = "/frame/messages/Messages";
		}
		String appMessages = this.getServletContext().getInitParameter("app-messages");
		if (appMessages == null) {
			appMessages = "/frame/messages/AppMessages";
		}
		logger.info("init:clientMessages={}", clientMessages);
		logger.info("init:appClientMessages={}", appClientMessages);
		logger.info("init:messages={}", messages);
		logger.info("init:appMessages={}", appMessages);
		MessagesUtil.setClientMessagesName(clientMessages);
		MessagesUtil.setAppClientMessagesName(appClientMessages);
		MessagesUtil.setMessagesName(messages);
		MessagesUtil.setAppMessagesName(appMessages);

		String clientMessagesTransfer = this.getServletContext().getInitParameter("client-message-transfer");
		if (clientMessagesTransfer == null) {
			clientMessagesTransfer = "CLIENT_ONLY";
		}
		MessagesUtil.setClientMessageTransfer(ClientMessageTransfer.valueOf(clientMessagesTransfer));
	}

	/**
	 * ServletInstanceBeanの設定を行います。
	 */
	private void setupServletInstanceBean() {
		String beanClass = this.getServletContext().getInitParameter("servlet-instance-bean");
		logger.info(() -> "beanClass = " + beanClass);
		if (beanClass != null) {
			try {
				@SuppressWarnings("unchecked")
				Class<? extends ServletInstanceBean> clazz = (Class<? extends ServletInstanceBean>) Class.forName(beanClass);
				DataFormsServlet.servletInstanceBean = clazz.getDeclaredConstructor().newInstance();
				DataFormsServlet.servletInstanceBean.init();
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
	}


	/**
	 * 一意制約違反のエラーメッセージ。
	 */
	private static String duplicateErrorMessage = null;

	/**
	 * 外部キー違反のエラーメッセージ。
	 */
	private static String foreignKeyErrorMessage = null;

	/**
	 * 一意制約違反のエラーメッセージを取得します。
	 * @return 一意制約違反のエラーメッセージ。
	 */
	public static String getDuplicateErrorMessage() {
		return duplicateErrorMessage;
	}

	/**
	 * 外部キー違反のエラーメッセージを取得します。
	 * @return 外部キー違反のエラーメッセージ。
	 */
	public static String getForeignKeyErrorMessage() {
		return foreignKeyErrorMessage;
	}

	/**
	 * DBの接続チェックを行ないます。
	 */
	private void checkDbConnection() {
		if (DataFormsServlet.dataSourceName == null) {
			// web.xmlにデータソースの指定が無い場合。
			DataFormsServlet.configStatus = "error.notfounddatasourcesetting";
		} else {
			try {
				if (DataFormsServlet.dataSourceName != null) {
					Context initContext = new InitialContext();
					String dspath = DataFormsServlet.jndiPrefix + DataFormsServlet.dataSourceName;
					logger.info(() -> "lookup data source=" + dspath);
					this.dataSource = (DataSource) initContext.lookup(dspath);

					try {
						DataFormsServlet.duplicateErrorMessage = (String) initContext.lookup(DataFormsServlet.jndiPrefix + "duplicateErrorMessage");
					} catch (Exception ex) {
						logger.debug(() -> ex.getMessage());
					}
					try {
						DataFormsServlet.foreignKeyErrorMessage = (String) initContext.lookup(DataFormsServlet.jndiPrefix + "foreignKeyErrorMessage");
					} catch (Exception ex) {
						logger.debug(() -> ex.getMessage());
					}

					logger.debug(() -> "DataFormsServlet.duplicateErrorMessage=" + DataFormsServlet.duplicateErrorMessage);
					logger.debug(() -> "DataFormsServlet.foreignKeyErrorMessage=" + DataFormsServlet.foreignKeyErrorMessage);
				}
			} catch (NameNotFoundException e) {
				// アプリケーションサーバにデータソースの設定が無い場合。
				logger.error(() -> e.getMessage(), e);
				DataFormsServlet.configStatus = "error.notfounddatasource";
			} catch (Exception e) {
				// DBサーバが動作していない場合のエラーであるため、運用時トラブルで発生する可能性がある。
				// そのため設定エラーとしない。
				//DataFormsServlet.dbStatus = "error.cannotconnectdatasource";
				logger.error(() -> e.getMessage(), e);
			}
		}
	}

	/**
	 * データベースの構造チェック。
	 * <pre>
	 * dataforms.app以下のパッケージのテーブル構造の違いがあった場合、
	 * 最新の構造に変更します。
	 * </pre>
	 *
	 */
	private void checkDBStructure() {
		try {
			Connection conn = this.getConnection();
			try {
				JDBCConnectableObject cobj = new JDBCConnectableObject() {
					@Override
					public final Connection getConnection() {
						return conn;
					}
				};
				TableManagerDao dao = new TableManagerDao(cobj);
				if (dao.isDatabaseInitialized()) {
					List<Map<String, Object>> list = dao.queryTableClass("dataforms.app", "");
					for (Map<String, Object> m: list) {
						String differenceVal = (String) m.get("differenceVal");
						if ("1".equals(differenceVal)) {
							String className = (String) m.get("className");
							logger.info(() -> "update table structure=" + className);
							dao.dropAllForeignKeys();
							dao.updateTable(className);
							dao.createAllForeignKeys();
						}
					}
				}
				conn.commit();
			} finally {
				conn.close();
			}
		} catch (Exception e) {
			logger.error(() -> e.getMessage(), e);
		}
	}

	/**
	 * 初期化するパッケージリストを取得します。
	 * @return 初期化するパッケージリスト。
	 */
	public List<String> getInitializePackageList() {
		List<String> ret = new ArrayList<String>();
		String plist = Page.getServlet().getServletContext().getInitParameter("initialize-package-list");
		String[] a = plist.split(",");
		for (String pkg: a) {
			ret.add(pkg.trim());
		}
		return ret;
	}



	/**
	 * 	制約マップ。
	 */
	private static Map<String, Constraint> constraintMap = null;

	/**
	 * 制約マップを取得します。
	 * @return 制約マップ。
	 */
	public static Map<String, Constraint> getConstraintMap() {
		return constraintMap;
	}


	/**
	 * 制約マップを作成します。
	 */
	private void makeConstraintMap() {
		List<String> list = this.getInitializePackageList();
		logger.debug("makeConstraintMap list=" + list);
		DataFormsServlet.constraintMap = new HashMap<String, Constraint>();
		try {
			for (String pkg: list) {
				// テーブルから外部キー制約を取り出す。
				ClassFinder f = new ClassFinder();
				List<Class<?>> tblist = f.findClasses(pkg, Table.class);
				for (Class<?> cls: tblist) {
					logger.debug(() -> "table class=" + cls.getName());
					if (cls.isAnonymousClass()) {
						continue;
					}
					if (SubQuery.class.isAssignableFrom(cls)) {
						continue;
					}
					Table tbl = (Table) cls.getDeclaredConstructor().newInstance();
					TableRelation rel = tbl.getTableRelation();
					if (rel != null) {
						for (ForeignKey fk: rel.getForeignKeyList()) {
							DataFormsServlet.constraintMap.put(fk.getConstraintName(), fk);
							logger.debug(() -> "ForeignKey:" + fk.getConstraintName() + ":" + fk.getViolationMessageKey());
						}
					}
				}
				// インデックスから一意制約を取り出す。
				List<Class<?>> idxlist = f.findClasses(pkg, Index.class);
				for (Class<?> cls: idxlist) {
					Index tbl = (Index) cls.getDeclaredConstructor().newInstance();
					UniqueKey uk = tbl.getUniqueKey();
					if (uk != null) {
						DataFormsServlet.constraintMap.put(uk.getConstraintName(), uk);
						logger.debug(() -> "UniqueKey:" + uk.getConstraintName() + ":" + uk.getViolationMessageKey());
					}
				}
			}
		} catch (Exception e) {
			logger.debug(() -> e.getMessage(), e);
		}
	}

	/**
	 * JDBC接続を取得します。
	 * @return JDBC接続。
	 * @throws Exception 例外。
	 */
	public final Connection getConnection() throws Exception {
		if (DataFormsServlet.dataSourceName != null) {
			Connection conn = this.dataSource.getConnection();
			conn.setAutoCommit(false);
			return conn;
		} else {
			return null;
		}

	}

	/**
	 * エラーページを取得します。
	 * @return エラーページ。
	 */
	public static String getErrorPage() {
		return errorPage;
	}

	/**
	 * 一時ファイル領域を取得します。
	 * @return 一時ファイル領域。
	 */
	public static String getTempDir() {
		return tempDir;
	}

	/**
	 * Export/Inputで使用するデフォルトフォルダを取得します。
	 * @return Export/Inputで使用するデフォルトフォルダ。
	 */
	public static String getExportImportDir() {
		return exportImportDir;
	}

	/**
	 * クライアントバリデーションの有無を取得します。
	 * @return クライアントバリデーション有りの場合true。
	 */
	public static boolean isClientValidation() {
		return clientValidation;
	}


	/**
	 * 文字コードを取得します。
	 * @return 文字コード。
	 */
	public static String getEncoding() {
		return encoding;
	}


	/**
	 * アップロードデータフォルダを取得します。
	 * @return アップロードデータフォルダ。
	 */
	public static String getUploadDataFolder() {
		return uploadDataFolder;
	}

	/**
	 * アップロードデータフォルダを指定します.
	 * @param uploadDataFolder アップロードデータフォルダ.
	 */
	public static void setUploadDataFolder(final String uploadDataFolder) {
		DataFormsServlet.uploadDataFolder = uploadDataFolder;
	}


	/**
	 * Jsonのデバックを行うかどうかを取得します。
	 * <pre>
	 * trueの場合jsonを読みやすい形式に整形します。
	 * </pre>
	 * @return Jsonデバックを行う場合true。
	 */
	public static boolean isJsonDebug() {
		return jsonDebug;
	}

	/**
	 * Jsonのデバックを行うかどうかを設定します。
	 * <pre>
	 * trueの場合jsonを読みやすい形式に整形します。
	 * </pre>
	 * @param jsonDebug Jsonデバックを行う場合true。
	 */
	public static void setJsonDebug(final boolean jsonDebug) {
		DataFormsServlet.jsonDebug = jsonDebug;
	}

	/**
	 * CSSとSCRIPTの読み込み設定ファイルを取得します。
	 * @return CSSとSCRIPTの読み込み設定ファイル。
	 */
	public static String getCssAndScript() {
		return cssAndScript;
	}

	/**
	 * サポート言語を取得します。
	 * @return サポート言語。
	 */
	public static String getSupportLanguage() {
		return supportLanguage;
	}

	/**
	 * 言語固定設定を行った場合、その言語を返します。
	 * @return 固定された言語。
	 */
	public static String getFixedLanguage() {
		return fixedLanguage;
	}

	/**
	 * 開発ツール無効フラグを取得します。
	 * @return 開発ツール無効フラグ。
	 */
	public static boolean isDisableDeveloperTools() {
		return disableDeveloperTools;
	}

	/**
	 * 開発ツール無効フラグを設定します。
	 * @param disableDeveloperTools 開発ツール無効フラグ。
	 */
	public static void setDisableDeveloperTools(final boolean disableDeveloperTools) {
		DataFormsServlet.disableDeveloperTools = disableDeveloperTools;
	}

	/**
	 * QueryStringを暗号化する際のパスワードを取得します。
	 * @return QueryStringを暗号化する際のパスワード。
	 */
	public static String getQueryStringCryptPassword() {
		return queryStringCryptPassword;
	}

	/**
	 * QueryStringを暗号化する際のパスワードを設定します。
	 * @param queryStringCryptPassword QueryStringを暗号化する際のパスワード。
	 */
	public static void setQueryStringCryptPassword(final String queryStringCryptPassword) {
		DataFormsServlet.queryStringCryptPassword = queryStringCryptPassword;
	}

	/**
	 * リクエストに対応したFormクラス名を取得します。
	 * @param context コンテキスト。
	 * @param uri URI。
	 * @return クラス名。
	 */
	private String getTargetClassName(final String context, final String uri) {
		FunctionMap conv = WebComponent.getFunctionMap();
		String ret = conv.getWebComponentClass(context, uri);
		logger.debug("*** path=" + ret);
		return ret;
	}

	/**
	 * 指定したクラスのインスタンスを作成します。
	 * @param classname クラス名。
	 * @return クラスのインスタンス。
	 * @throws Exception 例外。
	 */
	private WebEntryPoint newWebEntryPointInstance(final String classname) throws Exception {
		logger.debug("c=" + classname);
		@SuppressWarnings("unchecked")
		Class<? extends Page> clazz = (Class<? extends Page>) Class.forName(classname);
		WebEntryPoint dataforms = clazz.getDeclaredConstructor().newInstance();
		return dataforms;
	}


	/**
	 * リクエストに対応したPageのインスタンスを取得します。
	 * @param req HTTP要求情報。
	 * @return DataFormsのインスタンス。
	 * @throws Exception 例外。
	 */
	protected final WebEntryPoint getWebEntryPoint(final HttpServletRequest req) throws Exception {
		String pageext = this.getPageExt();
		if (DataFormsServlet.configStatus == null) {
			String uri = req.getRequestURI();
			String context = req.getContextPath();
			logger.info(() -> "context=" + context + ", uri=" + uri);
			String classname = DataFormsServlet.convertPageClassName(this.getTargetClassName(context, uri));
			logger.info(() -> "classname=" + classname);
			WebEntryPoint ep = this.newWebEntryPointInstance(classname);
			if (ep instanceof Page) {
				Page page = (Page) ep;
				page.setPageExt(pageext);
			}
			WebComponent wc = (WebComponent) ep;
			wc.setWebEntryPoint(ep);
			ep.setRequest(req);
			return ep;
		} else {
			ConfigErrorPage page = new ConfigErrorPage(DataFormsServlet.configStatus);
			page.setWebEntryPoint(page);
			page.setRequest(req);
			page.setPageExt(pageext);
			return page;
		}
	}


	/**
	 * メソッドを呼び出します。
	 * <pre>
	 * InvocationTargetExceptionが発生した場合、その原因をスローするようにします。
	 * </pre>
	 * @param comp コンポーネント。
	 * @param param パラメータ。
	 * @param m メソッド。
	 * @return 実行結果。
	 * @throws Exception 例外。
	 */
	private Response callMethod(final WebComponent comp, final Map<String, Object> param, final Method m) throws Exception {
		Response ret = null;
		try {
			ret =(Response) m.invoke(comp, param);
		} catch (InvocationTargetException e) {
			Throwable th = e.getCause();
			if (th != null) {
				if (th instanceof ApplicationException) {
					throw (ApplicationException) th;
				}
			}
			throw e;
		}
		return ret;
	}

	@Override
	protected void doGet(final HttpServletRequest req, final HttpServletResponse resp)
			throws ServletException, IOException {
		this.doProcess(req, resp);
	}


	@Override
	protected void doPost(final HttpServletRequest req, final HttpServletResponse resp)
			throws ServletException, IOException {
		this.doProcess(req, resp);
	}

	/**
	 * QueryStringを解析しMapに展開します。
	 * @param queryString 問合せ文字列。
	 * @return 解析結果のMap。
	 * @throws Exception 例外。
	 */
	private Map<String, Object> parseQueryString(final String queryString) throws Exception {
		Map<String, Object> ret = new HashMap<String, Object>();
		if (!StringUtil.isBlank(queryString)) {
			String[] pairs = queryString.split("&");
			for (String pair: pairs) {
				String[] sp = pair.split("=");
				if (sp.length == 2) {
					this.addParamMap(ret, URLDecoder.decode(sp[0], DataFormsServlet.getEncoding()), URLDecoder.decode(sp[1], DataFormsServlet.getEncoding()));
				}
			}
		}
		logger.debug(() -> "queryString=" + JSON.encode(ret, true));
		return ret;

	}

	/**
	 * QyeryStringをマップに展開します。
	 * @param req 要求情報。
	 * @param param パラメータ。
	 * @return QueryStringを展開したマップ。
	 * @throws Exception 例外。
	 */
	private Map<String, Object> getQueryString(final HttpServletRequest req, final Map<String, Object> param) throws Exception {
		// ajax呼び出しの場合はヘッダにqueryStringが設定されてくる。
		String queryString = req.getHeader("queryString");
		logger.debug("header queryString={}", queryString);
		if (queryString == null) {
			// formのsubmitの場合はPOSTされた情報にqueryStringを入れておく。
			queryString = (String) param.get("dfQueryString");
		}
		logger.debug("other queryString={}", queryString);
		return this.parseQueryString(queryString);
	}

	/**
	 * httpRequest Logger.
	 */
	private static Logger accessLogger = LogManager.getLogger("HttpRequest");


	/**
	 * メソッドの開始ログを出力します。
	 * @param userInfo ユーザ情報。
	 * @param comp Webコンポーネント。
	 * @param param パラメータ。
	 * @param m メソッド。
	 */
	private void methodStartLog(final Map<String, Object> userInfo, final WebComponent comp, final Map<String, Object> param, final Method m) {
		String userId = "";
		if (userInfo != null) {
			UserInfoTable.Entity e = new UserInfoTable.Entity(userInfo);
			userId = e.getLoginId() + "(" + e.getUserId() + ")";
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.putAll(param);
		map.remove("password");
		accessLogger.info("{}:{}#{} start.", userId, comp.getClass().getName(), m.getName());
		accessLogger.debug("{}:{}", userId, map);
	}

	/**
	 * メソッドの終了ログを出力します。
	 * @param userInfo ユーザ情報。
	 * @param comp Webコンポーネント。
	 * @param resp 応答情報。
	 * @param m メソッド。
	 */
	private void methodFinishLog(final Map<String, Object> userInfo, final WebComponent comp, final Response resp, final Method m) {
		String userId = "";
		if (userInfo != null) {
			UserInfoTable.Entity e = new UserInfoTable.Entity(userInfo);
			userId = e.getLoginId() + "(" + e.getUserId() + ")";
		}
		accessLogger.info("{}:{}#{} finish.", userId, comp.getClass().getName(), m.getName());
		accessLogger.debug("{}:{}", userId, resp.toString());
	}


	/**
	 * servletの処理を実装します。
	 * @param req 要求情報。
	 * @param resp 応答情報。
	 * @throws ServletException サーブレット例外。
	 * @throws IOException IO例外。
	 */
	protected void doProcess(final HttpServletRequest req, final HttpServletResponse resp)
			throws ServletException, IOException {
//		boolean isJsonResponse = false;
		req.setCharacterEncoding(encoding);
		String contextPath = req.getContextPath();
		String uri = req.getRequestURI();

		logger.debug("contextPath=" + contextPath);
		logger.debug("uri=" + uri);
		WebEntryPoint epoint = null;
		try {
			try {
				epoint = this.getWebEntryPoint(req);
				try {
					Map<String, Object> param = this.getParameterMap(req);

					String method = (String) param.get("dfMethod");
					if (method == null) {
						method = EXEC_METHOD;
						String queryString = req.getQueryString();
						logger.debug(() -> "getHtml queryString=" + queryString);
						epoint.setQueryString(this.parseQueryString(queryString));
					} else {
						epoint.setQueryString(this.getQueryString(req, param));
					}
					logger.info("method={}", method);
					Map<String, Object> userinfo = epoint.getUserInfo();
					if (userinfo != null) {
						logger.info("access user={}({})", () -> userinfo.get("loginId"), () -> userinfo.get("userId"));
					}
//					String[] split = method.split("\\.");
					int lidx = method.lastIndexOf(".");
					Method m = null;
					WebComponent obj = (WebComponent) epoint;
					if (lidx < 0) {
						// 該当ページのメソッドを呼び出す。
						m = obj.getClass().getMethod(method, Map.class);
					} else {
						// '.'を含むメソッドの場合ページから対応オブジェクトを取得する.
						method = method.replaceAll("\\[[0-9]+\\]", "");
						// log.info("method=" + method);
						String[] sp = method.split("\\.");
						for (int i = 0; i < sp.length - 1; i++) {
							obj = obj.getComponent(sp[i]);
						}
						m = obj.getClass().getMethod(sp[sp.length - 1], Map.class);
					}
/*					Class<?> mt = m.getReturnType();
					if (JsonResponse.class.getName().equals(mt.getName())) {
						isJsonResponse = true;
					}
*/
					if (!EXEC_METHOD.equals(method)) {
						if (!epoint.isValidRequest(param)) {
							throw new ApplicationException(epoint, "error.csrftoken");
						}
					}

					WebMethod wma = m.getAnnotation(WebMethod.class);
					if (wma == null) {
						logger.error(MessagesUtil.getMessage(epoint, "error.notwebmethod", method));
						throw new Exception();
					}
					Connection conn = null;
					if (wma.useDB()) {
						conn = this.getConnection();
					}
					try {
						epoint.setConnection(conn);
						if (epoint instanceof Page) {
							((Page) epoint).autoLogin();
						}
						if (!wma.everyone()) {
							if (!epoint.isAuthenticated(param)) {
								throw new AuthoricationException(epoint);
							}
						}
						epoint.setRequest(req);
						epoint.setResponse(resp);
						param.remove("dfMethod");
						this.methodStartLog(userinfo, obj, param, m);
						Response r = this.callMethod(obj, param, m);
						this.methodFinishLog(userinfo, obj, r, m);
						r.send(resp);
						if (conn != null) {
							conn.commit();
						}
					} catch (Exception e) {
						if (conn != null) {
							conn.rollback();
						}
						logger.error(() -> e.getMessage(), e);
						throw e;
					} finally {
						if (conn != null) {
							conn.close();
						}
					}
				} finally {
					((WebComponent) epoint).releaseWebEntryPoint();
				}
			} catch (ApplicationException e) {
				logger.error(() -> e.getMessageKey() + ":" + e.getMessage(), e);
				if (e.getResponseMode() == ResponseMode.JSON) {
					// JsonResponseを返すメソッドで発生した場合、JsonResponseでエラー情報を返す。
					this.sendApplicationExceptionJson(resp, e);
				} else {
					// JsonResponse以外を返すメソッドの場合、エラーページにリダイレクションする。
					this.redirectErrorPage((WebEntryPoint) epoint, req, resp, e.getMessage());
				}
			}
		} catch (Exception e) {
			logger.error(() -> e.getMessage(), e);
			resp.sendError(HttpURLConnection.HTTP_INTERNAL_ERROR);
		}
	}


	/**
	 * ApplicationErrorの内容をJSONで返す。
	 * @param resp 応答情報。
	 * @param e 発生した例外。
	 * @throws Exception 例外。
	 */
	private void sendApplicationExceptionJson(final HttpServletResponse resp, final ApplicationException e) throws Exception {
/*		Map<String, Object> einfo = new HashMap<String, Object>();
		einfo.put("key", e.getMessageKey());
		einfo.put("message", e.getMessage());
		JsonResponse r = new JsonResponse(JsonResponse.APPLICATION_EXCEPTION, einfo);*/
		JsonResponse r = e.getJsonResponse();
		r.send(resp);
	}


	/**
	 * パラメータを取得します。
	 * @param req HTTP要求情報。
	 * @return パラメータマップ。
	 * @throws Exception 例外。
	 */
	@SuppressWarnings("unchecked")
	protected Map<String, Object> getParameterMap(final HttpServletRequest req) throws Exception {
		if (JakartaServletFileUpload.isMultipartContent(req)) {
			return this.getParameterMapForMultipart(req);
		} else {
			String contentType = req.getHeader("Content-Type");
			if (contentType != null && contentType.indexOf("application/json") >= 0) {
				Map<String, Object> ret = new HashMap<String, Object>();
				try (InputStream is = req.getInputStream()) {
					ret = JSON.decode(is, HashMap.class);
				}
				return ret;
			} else {
				return this.getParameterMapForUrlencoded(req);
			}
		}
	}



	/**
	 * File Uploadが無い場合のパラメータ解析を行います。
	 * @param req HTTPリクエスト。
	 * @return パラメータ解析結果。
	 * @throws Exception 例外。
	 */
	private Map<String, Object> getParameterMapForUrlencoded(final HttpServletRequest req) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, String[]> m = req.getParameterMap();
		for (String key : m.keySet()) {
			String[] value = m.get(key);
			if (value == null) {
				map.put(key, null);
			} else {
				if (value.length > 1) {
					List<String> list = new ArrayList<String>();
					for (String s : value) {
						list.add(s);
					}
					map.put(key, list);
				} else {
					map.put(key, value[0]);
				}
			}
		}
		return map;
	}


	/**
	 * パラメータマップに値を追加します。
	 * @param map パラメータマップ。
	 * @param key キー。
	 * @param value 値。
	 */
	private void addParamMap(final Map<String, Object> map, final String key, final String value) {
//		log.info("paramater:" + key + "=" + value);
		if (map.containsKey(key)) {
			Object o = map.get(key);
			if (o instanceof List<?>) {
				@SuppressWarnings("unchecked")
				List<String> list = (List<String>) o;
				list.add(value);
				map.put(key, list);
			} else {
				List<String> list = new ArrayList<String>();
				list.add((String) o);
				list.add(value);
				map.put(key, list);
			}
		} else {
			map.put(key, value);
		}
	}


	/**
	 * File Uploadがある場合のパラメータ解析を行います。
	 * @param req HTTPリクエスト。
	 * @return パラメータ解析結果。
	 * @throws Exception 例外。
	 */
	private Map<String, Object> getParameterMapForMultipart(final HttpServletRequest req) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		DiskFileItemFactory factory = new DiskFileItemFactory.Builder().get();
//		factory.setRepository(new File(tempDir)); //一時的に保存する際のディレクトリ
		JakartaServletDiskFileUpload upload = new JakartaServletDiskFileUpload(factory);
//		upload.setHeaderEncoding(encoding);
//		@SuppressWarnings("unchecked")
		List<DiskFileItem> list = upload.parseRequest(req);
		for (FileItem<?> item : list) {
			String key = item.getFieldName();
			if (key != null) {
				if (item.isFormField()) {
					String value = item.getString(); // item.getString(encoding);
					this.addParamMap(map, key, value);
				} else {
					String filename = item.getName();
					if (StringUtil.isBlank(filename)) {
						map.put(key, null);
					} else {
						map.put(key, item);
					}
				}
			}
		}
		return map;
	}


	/**
	 * エラーページにリダイレクトします。
	 * @param page ページ。
	 * @param req 要求情報。
	 * @param resp 応答情報。
	 * @param message メッセージ。
	 * @throws Exception 例外。
	 */
	private void redirectErrorPage(final WebEntryPoint page, final HttpServletRequest req, final HttpServletResponse resp, final String message) throws Exception {
		String context = req.getContextPath();
		String errorPage = DataFormsServlet.errorPage;
		if (page != null) {
			errorPage = page.getErrorPage();
		}
//		String url = context + errorPage + "?msg=" + java.net.URLEncoder.encode(message, DataFormsServlet.encoding);
		String url = context + errorPage;
		logger.info(() -> "errorPage=" + url);
		try {
			req.getSession().setAttribute(ErrorPage.ID_ERROR_MESSAGE, message);
			resp.sendRedirect(url);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * クライアントログレベルを取得します。
	 * @return クライアントログレベル。
	 */
	public static String getClientLogLevel() {
		return clientLogLevel;
	}

	@Override
	public void destroy() {
		logger.debug("DataFormsServlet destroy");
		if (DataFormsServlet.servletInstanceBean != null) {
			try {
				DataFormsServlet.servletInstanceBean.destroy();
			} catch (Exception e) {
				logger.error(() -> e.getMessage(), e);
			}
		}
		super.destroy();
		BlobFileStore.cleanup();
	}

	/**
	 * ブラウザのクッキー受け入れチェックフラグ。
	 *
	 */
	private static boolean cookieCheck = false;

	/**
	 * ブラウザのクッキー受け入れチェックフラグを取得します。
	 * @return ブラウザのクッキー受け入れチェックフラグ。
	 */
	public static boolean isCookieCheck() {
		return cookieCheck;
	}


	/**
	 * ブラウザのクッキー受け入れチェックフラグを指定します。
	 * @param cookieCheck ブラウザのクッキー受け入れチェックフラグ。
	 */
	public static void setCookieCheck(final boolean cookieCheck) {
		DataFormsServlet.cookieCheck = cookieCheck;
	}

	/**
	 * Apache-FOPの設定ファイルバスを取得します。
	 * @return Apache-FOPの設定ファイルバス。
	 */
	public static String getApacheFopConfig() {
		return apacheFopConfig;
	}


	/**
	 * Apache-FOPの設定ファイルのパスを取得します。
	 * @param apacheFopConfig Apache-FOPの設定ファイルバス。
	 */
	public static void setApacheFopConfig(final String apacheFopConfig) {
		DataFormsServlet.apacheFopConfig = apacheFopConfig;
	}

}
