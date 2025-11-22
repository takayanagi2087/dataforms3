package jp.dataforms.fw.servlet;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.HttpURLConnection;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
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
import jp.dataforms.fw.response.HtmlResponse;
import jp.dataforms.fw.response.JsonResponse;
import jp.dataforms.fw.response.Response;
import jp.dataforms.fw.util.AutoLoginCookie;
import jp.dataforms.fw.util.ClassFinder;
import jp.dataforms.fw.util.ConfUtil;
import jp.dataforms.fw.util.ConfUtil.Conf;
import jp.dataforms.fw.util.ConfUtil.CryptConfig;
import jp.dataforms.fw.util.ConfUtil.DbcpConfig;
import jp.dataforms.fw.util.ConfUtil.DbcpDataSource;
import jp.dataforms.fw.util.ConfUtil.OnetimePasswordConfig;
import jp.dataforms.fw.util.CryptUtil;
import jp.dataforms.fw.util.FileUtil;
import jp.dataforms.fw.util.HttpRangeInfo;
import jp.dataforms.fw.util.JsonUtil;
import jp.dataforms.fw.util.MessagesUtil;
import jp.dataforms.fw.util.MessagesUtil.ClientMessageTransfer;
import jp.dataforms.fw.util.OnetimePasswordUtil;
import jp.dataforms.fw.util.StringUtil;
import jp.dataforms.fw.util.WebResourceUtil;

/**
 * DataForms用サーブレットクラスです。
 * <pre>
 * *.dfにマッピングします。
 * /hoge/hogehoge/XxxPage.htmlをアクセスすると、
 * ページクラスhoge.hogehoge.XxxPageクラスのインスタンスを作成し、getHtmlメソッドを呼び出します。
 * getHtmlは/hoge/hogehoge/XxxPage.htmlや/hoge/hogehoge/XxxPage.jsからページを構成し表示します。
 * 次のようにメソッドを指定してアクセスすると、
 * /hoge/hogehoge/XxxPage.html?dfMethod=compid.method
 * hoge.hogehoge.XxxPageのインスタンスを作成した後、compidを持つコンポーネントを検索し、
 * そのmethodを呼び出します。
 * </pre>
 *
 */
@MultipartConfig
@WebServlet(name = "DataFormsServlet", displayName = "DataFormsServlet", urlPatterns = { "*.html", "*.api", "*.bat", "*.df" })
public class DataFormsServlet extends HttpServlet {
	/**
	 * Content-Type : FORM_URLENCODED。
	 */
	public static final String CONTENT_TYPE_X_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded";
	
	/**
	 * Content-Type : multipart/form-data。
	 */
	public static final String CONTENT_TYPE_MULTIPART_FORM_DATA = "multipart/form-data";

	/**
	 * Content-Type : application/json。
	 */
	public static final String CONTENT_TYPE_APPLICATION_JSON = "application/json";
	
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
	private static Logger logger = LogManager.getLogger(DataFormsServlet.class.getName());

	/**
	 * データソースのオブジェクト.
	 */
	private DataSource dataSource = null;
	
	/**
	 * DBCP使用フラグ。
	 */
	private static boolean isDbcp = false;

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
	 * コンストラクタ。
	 */
	public DataFormsServlet() {
		
	}
	
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
		return DataFormsServlet.getConf().getApplication().getCryptConfig().getCsrfSessionidCryptPassword();
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
		if (DataFormsServlet.getConf() != null) {
			DataFormsServlet.pageOverrideMap.putAll(DataFormsServlet.getConf().getApplication().getPageOverride());
		}
	}


	/**
	 * WEBリソースアクセス用のURLを取得します。
	 *
	 * @return WEBリソースアクセス用のURL。
	 */
	public static String getWebResourceUrl() {
		return DataFormsServlet.confUtil.getConf().getApplication().getWebResourceUrl();
	}

	/**
	 * 設定情報ユーティリティ。
	 */
	private static ConfUtil confUtil = new ConfUtil();
	
	/**
	 * 設定情報を取得します。
	 * @return 設定情報。
	 */
	public static Conf getConf() {
		return DataFormsServlet.confUtil.getConf();
	}
	
	/**
	 * アプリケーション更新日付。
	 */
	private static String appUpdateTime = null;
	
	/**
	 * アプリケーション更新日付を取得します。
	 * @return アプリケーション更新日付。
	 */
	public static String getAppUpdateTime() {
		return appUpdateTime;
	}
	
	
	/**
	 * システムの更新日付を取得します。
	 */
	private void getUpdateTime() {
		String appPath = this.getServletContext().getRealPath("/");
		logger.info("appPath=" + appPath);
		Path folderPath = Paths.get(appPath);
        try {
            Optional<Path> latestFile = Files.walk(folderPath)
                    .filter(Files::isRegularFile)
                    .max(Comparator.comparing(path -> {
                        try {
                            return Files.getLastModifiedTime(path);
                        } catch (IOException e) {
                            e.printStackTrace();
                            return FileTime.fromMillis(0);
                        }
                    }));

            if (latestFile.isPresent()) {
                Path file = latestFile.get();
                FileTime lastModifiedTime = Files.getLastModifiedTime(file);
                SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        		logger.info("system newest file=" + file);
        		DataFormsServlet.appUpdateTime = fmt.format(new java.util.Date(lastModifiedTime.toMillis()));
        		logger.info("lastupdate=" + DataFormsServlet.appUpdateTime);
            } else {
            	logger.error("App file not found.");
            }
        } catch (IOException e) {
        	logger.error(e.getMessage(), e);
        }

	}
	
	
	/**
	 * 初期化パラメータを取得します。
	 * @throws ServletException 例外。
	 */
	@Override
	public void init() throws ServletException {
		
		this.getUpdateTime();
		
		
		
		DataFormsServlet.confUtil = new ConfUtil();
		DataFormsServlet.confUtil.readDefaultConf(this);
		
		this.initPageOverrideMap();
		
		if (DataFormsServlet.confUtil.getConf() != null) {
			LoginForm.setMfaRequiredCount(DataFormsServlet.confUtil.getConf().getApplication().getMfaRequiredCount());
		}

		// 一時フォルダがない場合作成する。
		File tmp = new File(DataFormsServlet.getTempDir());
		if (!tmp.exists()) {
			tmp.mkdirs();
		}
		logger.info(() -> "init:tempDir=" + DataFormsServlet.getTempDir());
		logger.info(() -> "init:exportImportDir=" + DataFormsServlet.getExportImportDir());
		logger.info(() -> "init:cssAndScript=" + DataFormsServlet.getCssAndScript());
		logger.info(() -> "init:errorPage=" + DataFormsServlet.getErrorPage());
		logger.info(() -> "init:clientValidation=" + DataFormsServlet.isClientValidation());
		logger.info(() -> "init:clientLogLevel=" + DataFormsServlet.getClientLogLevel());
		logger.info(() -> "init:uploadDataFolder=" + DataFormsServlet.getUploadDataFolder());
		logger.info(() -> "init:supportLanguage=" + DataFormsServlet.getSupportLanguage());
		logger.info(() -> "init:fixedLanguage=" + DataFormsServlet.getFixedLanguage());
		this.initPassword();
		this.initOnetimePassword();
		Page.setFramePath(DataFormsServlet.getConf().getApplication().getFramePath());
		logger.info(() -> "init:framePath=" + Page.getFramePath());
		this.getMessageProperties();
		String topPage = DataFormsServlet.getConf().getApplication().getTopPage();
		if (!StringUtil.isBlank(topPage)) {
			Page.setTopPage(topPage);
		}
		Boolean autoLogin = DataFormsServlet.getConf().getApplication().getAutoLogin();
		AutoLoginCookie.setAutoLogin(autoLogin);
		
		Boolean secureAutoLoginCookie = DataFormsServlet.getConf().getApplication().getSecureAutoLoginCookie();
		if (!StringUtil.isBlank(secureAutoLoginCookie)) {
			AutoLoginCookie.setSecure(secureAutoLoginCookie);
			OnetimePasswordUtil.setSecure(secureAutoLoginCookie);
		}
		DeveloperPage.setJavaSourcePath(DataFormsServlet.getConf().getDevelopmentTool().getJavaSourcePath());
		DeveloperPage.setWebSourcePath(DataFormsServlet.getConf().getDevelopmentTool().getWebSourcePath());
		HttpRangeInfo.setBlockSizeList(DataFormsServlet.getConf().getApplication().getStreamingBlockSize());
		FileObject.setContentTypeList(DataFormsServlet.getConf().getApplication().getContentTypeList());
		BackupForm.setBackupFileName(DataFormsServlet.getConf().getApplication().getBackupFileName());
		SideMenu.setMultiOpenMenu(DataFormsServlet.getConf().getApplication().getMultiOpenMenu());
		DeveloperEditForm.setCheckUserImport(DataFormsServlet.getConf().getInitialize().getCheckUserImport());
		this.getUserEditFormConf();
		this.getUserRegistConf();
		super.init();
		WebComponent.setServlet(this);
		this.setupServletInstanceBean();
		// パスとパッケージの対応表を設定する。
		try {
			WebComponent.setFunctionMap(FunctionMap.getAppFunctionMap());
			List<String> pkglist = WebComponent.getFunctionMap().getDBPackageList();
			DataFormsServlet.confUtil.getConf().getInitialize().setDatabasePackageList(pkglist);
			// CSSフィルターの初期化。
			Set<String> cssList = WebResourceUtil.getFileList("/frame", "Variables.css");
			for (String css: cssList) {
				logger.debug("Variables.css:" + css);
				CssFilter.readVar(css);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		// DB存在チェック。
		this.checkDbConnection();
		// DBの存在チェック。
		this.checkDBStructure();
		// 制約マップを作成します。
		this.makeConstraintMap();
	}

	/**
	 * パスワード関連の初期化。
	 *
	 */
	private void initPassword() {
		CryptUtil.initPasswordType(this.getServletContext());
		CryptConfig conf = DataFormsServlet.getConf().getApplication().getCryptConfig();
		CryptUtil.setAlgorithm(conf.getAlgorithm());
		CryptUtil.setAesInitialVector(conf.getAesInitialVector());
		String defaultPassword = conf.getDefaultPassword();
		if (defaultPassword != null) {
			CryptUtil.setCryptPassword(defaultPassword);
		} else {
			CryptUtil.setCryptPassword(CryptUtil.DES_PASSWORD_OR_AES_KEY);
		}
		
	}


	/**
	 * ワンタイムパスワード関連情報を取得します。
	 */
	private void initOnetimePassword() {
		OnetimePasswordConfig conf = DataFormsServlet.getConf().getApplication().getOnetimePasswordConfig();
		OnetimePasswordUtil.setConfig(conf);
	}

	/**
	 * ユーザ情報編集フォームの設定情報を取得します。
	 */
	public void getUserEditFormConf() {
		UserEditForm.setConfig(DataFormsServlet.getConf().getApplication().getUserEditFormConfig());
	}

	/**
	 * ユーザ登録関連設定を取得します。
	 */
	public void getUserRegistConf() {
		// ユーザ登録ページ関連設定
		LoginInfoForm.setUserRegistPage(DataFormsServlet.getConf().getApplication().getUserRegistPage());
		UserRegistForm.setUserEnablePage(DataFormsServlet.getConf().getApplication().getUserEnablePage());
		LoginForm.setPasswordResetMailPage(DataFormsServlet.getConf().getApplication().getPasswordResetMailPage());
		PasswordResetMailForm.setPasswordResetPage(DataFormsServlet.getConf().getApplication().getPasswordResetPage());
		UserRegistForm.setConfig(DataFormsServlet.getConf().getApplication().getUserRegistPageConfig());
		// メール関連設定。
		String mailSession = DataFormsServlet.getConf().getApplication().getMail().getMailSession();
		if (mailSession != null) {
			MailSender.setJndiPrefix(DataFormsServlet.getConf().getApplication().getJndiDataSource().getJndiPrefix());
			MailSender.setMailSessionName(mailSession);
			MailSender.setMailFrom(DataFormsServlet.getConf().getApplication().getMail().getMailFrom());
		}
	}

	/**
	 * メッセージプロパティの設定情報を取得します。
	 */
	private void getMessageProperties() {
		String clientMessages = DataFormsServlet.getConf().getApplication().getMessageResource().getClientMessages();
		String appClientMessages = DataFormsServlet.getConf().getApplication().getMessageResource().getAppClientMessages();
		String messages = DataFormsServlet.getConf().getApplication().getMessageResource().getMessages();
		String appMessages = DataFormsServlet.getConf().getApplication().getMessageResource().getAppMessages();
		logger.info("init:clientMessages={}", clientMessages);
		logger.info("init:appClientMessages={}", appClientMessages);
		logger.info("init:messages={}", messages);
		logger.info("init:appMessages={}", appMessages);
		MessagesUtil.setClientMessagesName(clientMessages);
		MessagesUtil.setAppClientMessagesName(appClientMessages);
		MessagesUtil.setMessagesName(messages);
		MessagesUtil.setAppMessagesName(appMessages);
		MessagesUtil.setClientMessageTransfer(ClientMessageTransfer.valueOf("CLIENT_ONLY"));
	}

	/**
	 * ServletInstanceBeanの設定を行います。
	 */
	private void setupServletInstanceBean() {
		String beanClass = DataFormsServlet.getConf().getApplication().getServletInstanceBean();
		logger.info(() -> "ServletInstanceBean = " + beanClass);
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
	 * データソースを取得します。
	 * @param jndiPrefix JNDIデータソースの前につける文字列。
	 * @param dataSourceName JNDIデータソース名称。
	 * @return データソース。
	 * @throws Exception 例外。
	 */
	public static DataSource getJndiDataSource(final String jndiPrefix, final String dataSourceName) throws Exception {
		Context initContext = new InitialContext();
		String dspath = jndiPrefix + dataSourceName;
		logger.info(() -> "jndi data source=" + dspath);
		DataSource dataSource = (DataSource) initContext.lookup(dspath);
		try {
			DataFormsServlet.duplicateErrorMessage = (String) initContext.lookup(jndiPrefix + "duplicateErrorMessage");
		} catch (Exception ex) {
			logger.debug(() -> ex.getMessage());
		}
		try {
			DataFormsServlet.foreignKeyErrorMessage = (String) initContext.lookup(jndiPrefix + "foreignKeyErrorMessage");
		} catch (Exception ex) {
			logger.debug(() -> ex.getMessage());
		}
		logger.info(() -> "DataFormsServlet.duplicateErrorMessage=" + DataFormsServlet.duplicateErrorMessage);
		logger.info(() -> "DataFormsServlet.foreignKeyErrorMessage=" + DataFormsServlet.foreignKeyErrorMessage);
		DataFormsServlet.isDbcp = false;
		return dataSource;
	}

	/**
	 * DBCPデータソースを取得します。
	 * @param dbcpConfig DBCP設定情報。
	 * @return データソース。
	 * @throws Exception 例外。
	 */
	public static DataSource getDbcpDataSource(final DbcpConfig dbcpConfig) throws Exception {
		String dataSourceName = dbcpConfig.getMainDataSource();
		DbcpDataSource conf = dbcpConfig.getDataSourceMap().get(dataSourceName);
		logger.info("dbcp data source=" + JsonUtil.encode(conf, true));
		BasicDataSource ds = getDbcpDataSource(conf);
		DataFormsServlet.isDbcp = true;
		return ds;
	}

	/**
	 * DBCPデータソースを取得します。
	 * @param conf DBCPデータソース設定情報。
	 * @return DBCPデータソース。
	 */
	public static BasicDataSource getDbcpDataSource(DbcpDataSource conf) {
		BasicDataSource ds = new BasicDataSource();
		ds.setDriverClassName(conf.getDriverClassName());
		ds.setUrl(conf.getUrl());
		ds.setUsername(conf.getUser());
		ds.setPassword(conf.getPassword());
		ds.setInitialSize(conf.getInitialSize());
		ds.setMaxTotal(conf.getMaxTotal());
		ds.setMaxIdle(conf.getMaxMaxIdle());
		// ds.setMaxWaitMillis(conf.getMaxWaitMillis());
		DataFormsServlet.duplicateErrorMessage = conf.getDuplicateErrorMessage();
		DataFormsServlet.foreignKeyErrorMessage = conf.getForeignKeyErrorMessage();
		logger.info(() -> "DataFormsServlet.duplicateErrorMessage=" + DataFormsServlet.duplicateErrorMessage);
		logger.info(() -> "DataFormsServlet.foreignKeyErrorMessage=" + DataFormsServlet.foreignKeyErrorMessage);
		return ds;
	}
	
	/**
	 * データソースを取得します。
	 * @param jndiPrefix JNDIデータソースの前につける文字列。
	 * @param dataSourceName JNDIデータソース名称。
	 * @param dbcpConfig DBCP設定情報。
	 * @return データソース。
	 * @throws Exception 例外。
	 */
	public static DataSource getDataSource(final String jndiPrefix, final String dataSourceName, final DbcpConfig dbcpConfig) throws Exception {
		if (dbcpConfig != null && dbcpConfig.getMainDataSource() != null) {
			return DataFormsServlet.getDbcpDataSource(dbcpConfig);
		} else {
			return DataFormsServlet.getJndiDataSource(jndiPrefix, dataSourceName);
		}
		
	}
	
	/**
	 * DBの接続チェックを行ないます。
	 */
	private void checkDbConnection() {
		String dataSourceName = DataFormsServlet.getConf().getApplication().getJndiDataSource().getDataSource();
		String jndiPrefix = DataFormsServlet.getConf().getApplication().getJndiDataSource().getJndiPrefix();
		DbcpConfig dbcpConfig = DataFormsServlet.getConf().getApplication().getDbcpConfig();
		if (dataSourceName == null && dbcpConfig == null) {
			// DataFormsServlet.configStatus = "error.notfounddatasourcesetting";
		} else {
			try {
				if (dataSourceName != null) {
					this.dataSource = DataFormsServlet.getDataSource(jndiPrefix, dataSourceName, dbcpConfig);
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
	 * jp.dataforms.fw.app以下のパッケージのテーブル構造の違いがあった場合、最新の構造に変更します。
	 * </pre>
	 *
	 */
	private void checkDBStructure() {
		try {
			Connection conn = this.getConnection();
			if (conn == null) {
				return;
			}
			try {
				JDBCConnectableObject cobj = new JDBCConnectableObject() {
					@Override
					public final Connection getConnection() {
						return conn;
					}
				};
				TableManagerDao dao = new TableManagerDao(cobj);
				if (dao.isDatabaseInitialized()) {
					List<Map<String, Object>> list = dao.queryTableClass(WebComponent.BASE_PACKAGE + ".app", "");
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
		return DataFormsServlet.getConf().getInitialize().getDatabasePackageList();
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
					logger.info(() -> "table class= " + cls.getName());
					if ((cls.getModifiers() & Modifier.ABSTRACT) != 0) {
						logger.info(() -> "skip abstrct class = " + cls.getName());
						continue;
					}
					if (cls.isAnonymousClass()) {
						logger.info(() -> "skip anonymous class = " + cls.getName());
						continue;
					}
					if (SubQuery.class.isAssignableFrom(cls)) {
						logger.info(() -> "skip SubQuery class = " + cls.getName());
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
		String dataSourceName = DataFormsServlet.getConf().getApplication().getJndiDataSource().getDataSource();
		if (dataSourceName != null) {
			if (this.dataSource != null) {
				Connection conn = this.dataSource.getConnection();
				conn.setAutoCommit(false);
				return conn;
			} 
		} 
		return null;
	}

	/**
	 * エラーページを取得します。
	 * @return エラーページ。
	 */
	public static String getErrorPage() {
//		return errorPage;
		return DataFormsServlet.getConf().getApplication().getErrorPage();
	}

	/**
	 * 一時ファイル領域を取得します。
	 * @return 一時ファイル領域。
	 */
	public static String getTempDir() {
		return DataFormsServlet.getConf().getApplication().getTempDir();
	}

	/**
	 * Export/Inputで使用するデフォルトフォルダを取得します。
	 * @return Export/Inputで使用するデフォルトフォルダ。
	 */
	public static String getExportImportDir() {
		return DataFormsServlet.getConf().getDevelopmentTool().getExportImportDir();
	}

	/**
	 * クライアントバリデーションの有無を取得します。
	 * @return クライアントバリデーション有りの場合true。
	 */
	public static boolean isClientValidation() {
		return DataFormsServlet.getConf().getApplication().getClientValidation();
	}


	/**
	 * 文字コードを取得します。
	 * @return 文字コード。
	 */
	public static String getEncoding() {
		return DataFormsServlet.getConf().getApplication().getEncoding();
	}


	/**
	 * アップロードデータフォルダを取得します。
	 * @return アップロードデータフォルダ。
	 */
	public static String getUploadDataFolder() {
		return DataFormsServlet.getConf().getApplication().getUploadDataFolder();
	}


	/**
	 * Jsonのデバックを行うかどうかを取得します。
	 * <pre>
	 * trueの場合jsonを読みやすい形式に整形します。
	 * </pre>
	 * @return Jsonデバックを行う場合true。
	 */
	public static boolean isJsonDebug() {
		return DataFormsServlet.getConf().getApplication().getJsonDebug();
	}

	/**
	 * CSSとSCRIPTの読み込み設定ファイルを取得します。
	 * @return CSSとSCRIPTの読み込み設定ファイル。
	 */
	public static String getCssAndScript() {
		return DataFormsServlet.getConf().getApplication().getCssAndScripts();
	}

	/**
	 * サポート言語を取得します。
	 * @return サポート言語。
	 */
	public static List<String> getSupportLanguage() {
		return DataFormsServlet.getConf().getApplication().getLanguageList();
	}

	/**
	 * 言語固定設定を行った場合、その言語を返します。
	 * @return 固定された言語。
	 */
	public static String getFixedLanguage() {
		return DataFormsServlet.getConf().getApplication().getFixedLanguage();
				
	}

	/**
	 * QueryStringを暗号化する際のパスワードを取得します。
	 * @return QueryStringを暗号化する際のパスワード。
	 */
	public static String getQueryStringCryptPassword() {
		return DataFormsServlet.getConf().getApplication().getCryptConfig().getQueryStringCryptPassword();
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
		return ret;
	}

	/**
	 * 指定したクラスのインスタンスを作成します。
	 * @param classname クラス名。
	 * @return クラスのインスタンス。
	 */
	private WebEntryPoint newWebEntryPointInstance(final String classname) throws Exception {
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
			try {
				String uri = req.getRequestURI();
				String context = req.getContextPath();
				logger.info(() -> "context=" + context + ", uri=" + uri);
				String classname = DataFormsServlet.convertPageClassName(this.getTargetClassName(context, uri));
				logger.info(() -> "classname=" + classname);
				if (!StringUtil.isBlank(classname)) {
					WebEntryPoint ep = this.newWebEntryPointInstance(classname);
					if (ep instanceof Page) {
						Page page = (Page) ep;
						page.setPageExt(pageext);
					}
					WebComponent wc = (WebComponent) ep;
					wc.setWebEntryPoint(ep);
					ep.setRequest(req);
					return ep;
				}
			} catch (ClassNotFoundException ex) {
				logger.info("ClassNotFoundException:" + ex.getMessage());
			}
		}
		return null;
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
		logger.debug(() -> "queryString=" + JsonUtil.encode(ret, true));
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
//		accessLogger.debug("{}:{}", userId, resp.toString());
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
		req.setCharacterEncoding(DataFormsServlet.getEncoding());
		String contextPath = req.getContextPath();
		String uri = req.getRequestURI();

		logger.debug("contextPath=" + contextPath);
		logger.debug("uri=" + uri);
		WebEntryPoint epoint = null;
		try {
			try {
				// *.htmlに対応するJavaのクラスを取得する。
				epoint = this.getWebEntryPoint(req);
				if (epoint == null) {
					// クラスが無い場合対応する*.htmlを取得する。
					String url = req.getRequestURI().toString().substring(contextPath.length());
					if (StringUtil.isBlank(url) || "/".equals(url)) {
						url = "/index.html";
					}
					logger.debug("url=" + url);
					String html = WebResourceUtil.getWebResource(url);
					if (html != null) {
						// 対応する*.htmlが存在する場合htmlを送信。
						HtmlResponse htmlresp = new HtmlResponse(html);
						htmlresp.send(resp);
						return;
					} else {
						// *.htmlも存在しない場合、エラーページを送信。
						String pageext = this.getPageExt();
						ConfigErrorPage page = new ConfigErrorPage(DataFormsServlet.configStatus);
						page.setWebEntryPoint(page);
						page.setRequest(req);
						page.setPageExt(pageext);
						epoint = page;
					}
				}
				try {
					// ページの処理を行う。
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
	 * MULTIPART_FORM_DATAかどうかをチェックします。
	 * @param req チェックする要求情報。
	 * @return MULTIPART_FORM_DATAの場合true。
	 */
	protected boolean isMultipartContent(final HttpServletRequest req) {
		boolean ret = false;
		String contentType = req.getHeader("Content-Type");
		if (contentType != null) {
			contentType = contentType.toLowerCase();
			if (contentType.indexOf(CONTENT_TYPE_MULTIPART_FORM_DATA) >= 0) {
				ret = true;
			}
		}
		return ret;
	}
	

	/**
	 * パラメータを取得します。
	 * @param req HTTP要求情報。
	 * @return パラメータマップ。
	 * @throws Exception 例外。
	 */
	@SuppressWarnings("unchecked")
	protected Map<String, Object> getParameterMap(final HttpServletRequest req) throws Exception {
		String contentType = req.getHeader("Content-Type");
		logger.debug("post data content-type=" + contentType);
		if (this.isMultipartContent(req)) {
			return this.getParameterMapForMultipart(req);
		} else {
			if (contentType != null && contentType.indexOf("application/json") >= 0) {
				Map<String, Object> ret = new HashMap<String, Object>();
				try (InputStream is = req.getInputStream()) {
					ret = (Map<String, Object>) JsonUtil.decode(is, HashMap.class);
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
	private void addParamMap(final Map<String, Object> map, final String key, final Part value) {
//		log.info("paramater:" + key + "=" + value);
		if (map.containsKey(key)) {
			Object o = map.get(key);
			if (o instanceof List<?>) {
				@SuppressWarnings("unchecked")
				List<Part> list = (List<Part>) o;
				list.add(value);
				map.put(key, list);
			} else {
				List<Part> list = new ArrayList<Part>();
				list.add((Part) o);
				list.add(value);
				map.put(key, list);
			}
		} else {
			map.put(key, value);
		}
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
	 * 文字列を読み込みます。
	 * @param p Part。
	 * @return 文字列。
	 * @throws Exception 例外。
	 */
	private String readString(final Part p) throws Exception {
		String ret = null;
		try (InputStream is = p.getInputStream()) {
			byte [] buf = FileUtil.readInputStream(is);
			ret = new String(buf, DataFormsServlet.getEncoding());
		}
		return ret;
	}
	
	/**
	 * File Uploadがある場合のパラメータ解析を行います。
	 * @param req HTTPリクエスト。
	 * @return パラメータ解析結果。
	 * @throws Exception 例外。
	 */
	private Map<String, Object> getParameterMapForMultipart(final HttpServletRequest req) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
    	Collection<Part> list = req.getParts();
    	for (Part p: list) {
			String filename = p.getSubmittedFileName();
    		logger.debug("---------- " + p.getClass().getName());
    		logger.debug("content-type:" + p.getContentType());
    		logger.debug("file name   :" + filename);
    		logger.debug("param name  :" + p.getName());
    		if (filename != null) {
				String name = p.getName();
				if (filename.length() > 0) {
					this.addParamMap(map, name, p);
//					map.put(name, p);
				} else {
					map.put(name, null);
				}
    		} else {
				String value = this.readString(p);
	    		logger.debug("value  :" + value);
				String name = p.getName();
				this.addParamMap(map, name, value);
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
		String errorPage = DataFormsServlet.getErrorPage() + "." + this.getPageExt();
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
		return DataFormsServlet.getConf().getApplication().getClientLogLevel();
	}
	
	private void deregisterDrivers() throws Exception {
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		Enumeration<Driver> drivers = DriverManager.getDrivers();
		while (drivers.hasMoreElements()) {
			Driver driver = drivers.nextElement();
			if (driver.getClass().getClassLoader() == cl) {
				try {
					DriverManager.deregisterDriver(driver);
					System.out.println("Deregistered JDBC driver: " + driver);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

	}

	@Override
	public void destroy() {
		logger.info("DataFormsServlet destroy");
		if (DataFormsServlet.servletInstanceBean != null) {
			try {
				DataFormsServlet.servletInstanceBean.destroy();
			} catch (Exception e) {
				logger.error(() -> e.getMessage(), e);
			}
		}
		if (DataFormsServlet.isDbcp) {
			if (this.dataSource instanceof BasicDataSource) {
				try {
					logger.info("cleanup dbcp data source");
					((BasicDataSource) this.dataSource).close();
					this.deregisterDrivers();
				} catch (Exception e) {
					logger.error(() -> e.getMessage(), e);
				}
			}
		}
		
		//LoggerContext context = (LoggerContext) LogManager.getContext(false);
		//context.stop();
//		LogManager.shutdown();
		super.destroy();
		BlobFileStore.cleanup();
	}


	/**
	 * ブラウザのクッキー受け入れチェックフラグを取得します。
	 * @return ブラウザのクッキー受け入れチェックフラグ。
	 */
	public static boolean isCookieCheck() {
		return DataFormsServlet.getConf().getApplication().getCookieCheck();
	}


	/**
	 * Apache-FOPの設定ファイルバスを取得します。
	 * @return Apache-FOPの設定ファイルバス。
	 */
	public static String getApacheFopConfig() {
		return DataFormsServlet.getConf().getApplication().getApacheFopConfig();
	}

}
