package jp.dataforms.fw.util;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jp.dataforms.fw.servlet.DataFormsServlet;
import lombok.Data;
import lombok.Getter;

/**
 * Dataforms設定ファイルユーティリティ。
 */
public class ConfUtil {
	
	/**
	 * Logger.
	 */
	private static Logger logger = LogManager.getLogger(ConfUtil.class);
	
	/**
	 * 文字コードの指定。
	 */
	private static final String ENCODING = "utf-8";
	
	/**
	 * 設定情報クラスと判定するためのインターフェース。
	 */
	public static interface  ConfClass {}
	
	/**
	 * 開発ツール設定情報。
	 */
	@Data
	public static class DevelopmentTool implements ConfClass {
		/**
		 * 開発ツール初期化済フラグ。
		 */
		private Boolean initialized = null;
		/**
		 * Javaのソースパス。
		 */
		private String javaSourcePath = null;
		/**
		 * HTML, jsのソースパス。
		 */
		private String webSourcePath = null;
		/**
		 * テストツールのソースパス。
		 */
		private String testSourcePath = null;
		/**
		 * 開発ツールでデータをエクスポート/インポートする際に使用するパスを指定します。
		 */
		private String exportImportDir = null;
		
		/**
		 * ソースコード生成ツール無効設定。
		 */
		private Boolean disableCodeGenerationTool = false;
		/**
		 * DB関連ツール無効設定。
		 */
		private Boolean disableDatabaseTool = false;
		/**
		 * フィールドレイアウト。
		 */
		private String fieldLayout = null;
		
		/**
		 * 移行元のDB設定。
		 */
		private String originDataSource = null;
		
		/**
		 * コンストラクタ。
		 */
		public DevelopmentTool() {
			
		}
		
	}
	
	/**
	 * アプリケーション初期化設定。
	 */
	@Data
	public static class Initialize implements ConfClass {
		/**
		 * 初期化時に作成する特権ユーザのレベル。
		 */
		private String userLevel = null;
		/**
		 *  データベースを定義するパッケージリスト。
		 */
		private List<String> databasePackageList = null;
		/**
		 * trueかつユーザの初期化データが存在した場合、ユーザのインポートを優先します。
		 */
		private Boolean checkUserImport = null;
		/**
		 * このリストに記載されたテーブルはデータベースに実体が作成されません。
		 * 複数のテーブルクラスの基底クラスとして定義し、
		 * テーブルの実体を作成する必要がないテーブルクラスを指定します。
		 * dataforms3.jarに含まれたUserInfoTableに項目を追加する場合、
		 * このコメントを有効にしてUserInfoTableから派生したテーブルクラスに項目を追加します。
		 */
		private List<String> abstractTableList = null;
		/**
		 * UserInfoTableに項目を追加したExtendedUserInfoTableを作成した場合、
		 * 以下の設定を有効にするとExtendedUserInfoTableクラスがユーザテーブルとなります。
		 * このクラスを指定するとUserEditForm.java,UserSelfEditForm.java,UserRegistForm.javaに指定されたテーブルのフィールドが配置されます。
		 * UserEditForm.html,UserSelfEditPage.html,UserRegistPage.htmlをjarファイルからプロジェクトにエクスポートし、
		 * フィールドを追加することにより追加情報を入力することが可能になります。
		 */
		private String userInfoTableClass = null;
		
		
		/**
		 * コンストラクタ。
		 */
		public Initialize() {
			
		}
	}
	
	/**
	 * JNDI Data Source設定。
	 */
	@Data
	public static class JndiDataSource implements ConfClass {
		/**
		 * JNDIデータソースの前につける文字列。
		 */
		private String jndiPrefix = null;
		/**
		 * 使用するJNDIデータソースを指定する。
		 */
		private String dataSource = null;
		/**
		 * コンストラクタ。
		 */
		public JndiDataSource() {
			
		}
		
		/**
		 * コンストラクタ。
		 * @param jndiPrefix JNDIデータソースの前につける文字列。
		 * @param dataSource 使用するJNDIデータソースを指定する。
		 */
		public JndiDataSource(String jndiPrefix, String dataSource) {
			this.jndiPrefix = jndiPrefix;
			this.dataSource = dataSource;
			
		}
	}
	
	/**
	 * DBCP Source設定。
	 */
	@Data
	public static class DbcpDataSource implements ConfClass {
		/**
		 * ドライバークラス名。
		 */
		private String driverClassName = null;
		/**
		 * URL。
		 */
		private String url = null;
		/**
		 * ユーザ。
		 */
		private String user = null;
		/**
		 * パスワード。
		 */
		private String password = null;
		/**
		 * 初期サイズ。
		 */
		private Integer initialSize = null;
		/**
		 * 最大サイズ。
		 */
		private Integer maxTotal = null;
		/**
		 * 最大アイドル。
		 */
		private Integer maxMaxIdle = null;
		
		/**
		 * 一意制約違反のエラーメッセージ。
		 */
		private String duplicateErrorMessage = null;
		
		/**
		 * 外部キー制約違反のエラーメッセージ。
		 */
		private String foreignKeyErrorMessage = null;
		/**
		 * コンストラクタ。
		 */
		public DbcpDataSource() {
			
		}
	}
	
	/**
	 * DBCP設定情報。
	 */
	@Data
	public static class DbcpConfig {
		/**
		 * 主に利用するDBCPデータソース名。
		 */
		private String mainDataSource = null;
		/**
		 * データソースマップ。
		 */
		private Map<String, DbcpDataSource> dataSourceMap = null;
		/**
		 * コンストラクタ。
		 */
		public DbcpConfig() {
			
		}
	}
	
	
	/**
	 * Mail設定。
	 */
	@Data
	public static class Mail implements ConfClass {
		/**
		 * mailSessionの名称。
		 */
		private String mailSession = null;
		/**
		 * メール送信者アドレス。
		 */
		private String mailFrom = null;
		
		/**
		 * コンストラクタ。
		 */
		public Mail() {
			
		}
	}
	
	
	/**
	 * ユーザ編集フォーム設定。
	 * TODO: この項目の構造を健闘。
	 */
	@Data
	public static class UserEditFormConfig implements ConfClass  {
		/**
		 * メールアドレスの必須チェック設定。
		 */
		private Boolean requiredMailAddress = null;
		
		/**
		 * コンストラクタ。
		 */
		public UserEditFormConfig() {
			
		}
	}
	
	/**
	 * ユーザ登録ページの設定。
	 */
	@Data
	public static class UserRegistPageConfig implements ConfClass {
		/**
		 * メールアドレスをログインIDとする。
		 */
		private Boolean loginIdIsMail = null;
		/**
		 * メールアドレスのチェックを行う。
		 */
		private Boolean mailCheck = null; 
		/**
		 * ユーザ有効化ページのURLをメールする。
		 */
		private Boolean sendUserEnableMail = null; 
		
		/**
		 * コンストラクタ。
		 */
		public UserRegistPageConfig() {
			
		}
	}
	
	/**
	 * メッセージリソース設定。
	 */
	@Data
	public static class MessageResource implements ConfClass {
		/**
		 * クライアントに送信して使用するメッセージリソースの名称を指定します。
		 * このファイルはdataforms3.jar内に存在します。
		 */
		private String clientMessages = null;
		/**
		 * サーバ側で使用するメッセージリソースの名称を指定します。
		 * このファイルはdataforms3.jar内に存在します。
		 */
		private String messages = null;
		/**
		 * クライアントに送信して使用するアプリケーション用メッセージリソースの名称を指定します。
		 * このファイルはアプリケーション側で用意します。
		 */
		private String appClientMessages = null;
		/**
		 * サーバ側で使用するアプリケーション用メッセージリソースの名称を指定します。
		 * このファイルはアプリケーション側で用意します。
		 */
		private String appMessages = null;
		
		/**
		 * コンストラクタ。
		 */
		public MessageResource() {
			
		}
	}
	
	/**
	 * 暗号化設定。
	 */
	@Data
	public static class CryptConfig implements ConfClass {
		/**
		 * 暗号化アルゴリズムを選択します。("des" or "aes")。
		 */
		private String algorithm = null;
		/**
		 * AESの初期化ベクトルを指定します。
		 * AES初期化ベクトルは16Byteである必要があるため、自動的に保管して使用します。
		 */
		private String aesInitialVector = null;
		/**
		 * ===================================================================================
		 *  以下項目はDESパスワードまたはAESキーを指定します。
		 *  AESキーは16Byteである必要があるため、自動的に保管して使用します。
		 * ===================================================================================
		 *  CryptUtilで使用するデフォルトのDESパスワードまたはAESキーを指定します。
		 *  UserInfoTableのパスワードの暗号化で使用するので、
		 *  変更する場合は事前にUserInfoTableのパスワードの再構築を行う必要があります。
		 *  nullを指定するとライブラリにハードコーディングされたものを使用します。
		 */
		private String defaultPassword = null;
		/**
		 *  QueryStringを暗号化する場合に使用するDESパスワードまたはAESキーを指定します。
		 *  BLOBデータ等のダウンロードパラメータの暗号化に使用しています。
		 *  運用時に変更しても問題ありません。
		 *  nullを指定するとライブラリにハードコーディングされたものを使用します。
		 */
		private String queryStringCryptPassword = null;
		/**
		 * CSRF対策に使用するDESパスワードまたはAESキーを指定します。
		 * 指定されたDESパスワードまたはAESキーでセッションIDを暗号化し、
		 * CSRFトークンとしてブラウザに送信します。
		 * 運用時に変更しても問題ありません。
		 * nullを指定するとCSRF対策を行いません。
		 */
		private String csrfSessionidCryptPassword = null;
		
		/**
		 * コンストラクタ。
		 */
		public CryptConfig() {
			
		}
	}
	
	/**
	 * この設定を有効にすると、認証時にユーザのメールアドレスにワンタイムパスワードを送信し
	 * その確認を行います。
	 */
	@Data
	public static class OnetimePasswordConfig implements ConfClass {
		/**
		 * ワンタイムパスワードを使用するかどうか。
		 */
		private Boolean useOnetimePassword = null;
		/**
		// ワンタイムパスワード文字数。
		 */
		private Integer length = null; 
		/**
		 *  ワンタイムパスワードキャンセルクッキーの有効日数
		 *  0を指定すると毎回ワンタイムパスワードを確認します。
		 *  それ以外の場合場クッキーが存在するブラウザの場合、ワンタイムパスワードのチェックは行われません。
		 */
		private Integer cookieExpiration = null;
		
		/**
		 * コンストラクタ。
		 */
		public OnetimePasswordConfig() {
			
		}
	}
	
	/**
	 * ストリーミングブロックサイズ。
	 */
	@Data
	public static class StreamingBlockSize implements ConfClass {
		/**
		 * UserAgentパターン。
		 */
		private String uaPattern = null;
		/**
		 * ブロックサイズ。
		 */
		private Integer blockSize = null;
		
		/**
		 * コンストラクタ。
		 */
		public StreamingBlockSize() {
			
		}
	}
	
	/**
	 * ContentType情報。
	 */
	@Data
	public static class ContentType implements ConfClass {
		/**
		 * ファイル名パターン。
		 */
		private String fnPattern = null;
		/**
		 * Content-type。
		 */
		private String contentType = null;
		
		/**
		 * コンストラクタ。
		 */
		public ContentType() {
			
		}
	}
	
	/**
	 * ページオーバーライド情報。
	 */
	@Data
	public static class PageOverride implements ConfClass {
		/**
		 * 変更元のページ。
		 */
		private String from = null;
		/**
		 * 変更先のページ。
		 */
		private String to = null;
		
		/**
		 * コンストラクタ。
		 */
		public PageOverride() {
			
		}
	}
	
	/**
	 * アプリケーション設定。
	 */
	@Data
	public static class Application implements ConfClass {
		/**
		 * 多要素認証を必須にするログイン回数。
		 */
		private Integer mfaRequiredCount = 0;
		/**
		 * サポート言語リスト。
		 */
		private List<String> languageList = null;
		/**
		 * 言語固定設定。
		 */
		private String fixedLanguage = null;
		/**
		 * サーバー設定ファイル。
		 */
		private String serverConfigFile = null;
		
		/**
		 * サーバーlog4j2設定ファイル。
		 */
		private String serverLog4j2Xml = null;
		
		/**
		 * WebリソースURL。
		 */
		private String webResourceUrl = null;
		
		/**
		 * 自動ログイン設定。
		 */
		private Boolean autoLogin = null;
		/**
		 * 自動ログインクッキーにSecureを設定します。
		 */
		private Boolean secureAutoLoginCookie = null;
		/**
		 * クライアントログレベルを指定します。
		 */
		private String clientLogLevel = null;
		/**
		 * JNDIデータソース設定。
		 */
		private JndiDataSource jndiDataSource = null;
		
		/**
		 * DBCPデータソース設定。
		 */
		private DbcpConfig dbcpConfig = null;
		
		/**
		 * メール設定。
		 */
		private Mail mail = null;
		/**
		 * フレームレイアウトのパス。
		 */
		private String framePath = null;
		/**
		 * ページオーバーライド。
		 */
		private Map<String, String> pageOverride = null;
		/**
		 * トップページを指定します。
		 */
		private String topPage = null;
		/**
		 * 外部ユーザ登録ページのパスを指定します。
		 */
		private String userRegistPage = null;
		/**
		 *  ユーザ情報編集フォームのメールアドレスを必須にするかどうかを設定します。
		 */
		private UserEditFormConfig userEditFormConfig = null;
		/**
		 * 外部ユーザ登録ページ設定情報。
		 */
		private UserRegistPageConfig userRegistPageConfig = null;
		/**
		 * 外部ユーザ有効化ページのパスを指定します。
		 * 外部ユーザ登録ページ設定でユーザ有効化ページのURLをメールするように設定した場合、このページのURLをメールで送信します。
		 */
		private String userEnablePage  = null;
		/**
		 * パスワードリセットメールページのパスを指定します。
		 * パスワードリセットメールで、このページのURLを通知します。
		 */
		private String passwordResetMailPage = null;
		/**
		 * パスワードリセットページのパスを指定します。
		 * パスワードリセットメールで、このページのURLを通知します。
		 */
		private String passwordResetPage = null;
		/**
		 * 一時ファイルを置くパスを指定します。
		 */
		private String tempDir = null;
		/**
		 * jQueryなどの標準ライブラリなどを読み込むhtmlを指定します。
		 */
		private String cssAndScripts = null;
		/**
		 * エラーページのパスを指定します。
		 */
		private String errorPage = null;
		/**
		 * クライアントバリデーションの有無を指定します。
		 * これをfalseに設定するとJavascriptでのバリデーションが停止します。
		 */
		private Boolean clientValidation = null;
		/**
		 * アップロードデータを保存するフォルダを指定します。
		 */
		private String uploadDataFolder = null;
		/**
		 *	可逆パスワード:
		 *	cryptConfigで設定したdefaultPasswordを使用してパスワードを暗号化。
		 */
		private String passwordType = null;
		/**
		 *	ハッシュアルゴリズム
		 * SHA-1 or SHA-256 or SHA-384 or SHA-512。
		 */
		private String hashAlgorithm = null;
		/**
		 * 暗号化設定。
		 */
		private CryptConfig cryptConfig = null;
		
		/**
		 * この設定を有効にすると、認証時にユーザのメールアドレスにワンタイムパスワードを送信し
		 * その確認を行います。
		 */
		private OnetimePasswordConfig onetimePasswordConfig = null;
		/**
		 *  ブラウザがクッキーを許可していることを確認する場合、trueを指定してください。
		 */
		private Boolean cookieCheck = null;
		/**
		 * メッセージリソース設定。
		 */
		private MessageResource messageResource = null;
		/**
		 *  DataFormsServlet#init,destoryで何らかの処理が必要な場合、ServletInstanceBeanから派生したクラスを作成し
		 *  そのクラスを設定してください。
		 */
		private String servletInstanceBean = null;
		/**
		 * ストリーミングのブラウザ毎の送信パターンを指定します。
		 * html5のvideo,audioタグからの部分リクエストは大抵 "Range:bytes=0-"のように転送開始位置のみが指定されてきます。
		 * この場合適切なサイズに区切って転送しないと、シーク機能が利用できません。
		 * しかしFirefoxの場合、先頭のブロックのみを再生したところで止まってしまうので、指定された位置から最後まで転送するようにしないとうまく動作しないようです。
		 */
		private List<StreamingBlockSize> streamingBlockSize = null;
		/**
		 * FileFieldの中に保存されたファイルをダウンロードする際に出力するcontent-typeを設定します。
		 */
		private List<ContentType> contentTypeList = null;
		/**
		 * データベースのバックアップファイル名を指定します。
		 */
		private String backupFileName = null;
		/**
		 * apache-fopの設定ファイルのパス。
		 */
		private String apacheFopConfig = null;
		/**
		 *  trueを設定すると複数のメニューを開いた状態にすることができ、
		 *  falseを設定すると、開いたメニュー以外は自動的に閉じるようになります。
		 */
		private Boolean multiOpenMenu = null;
		/**
		 * debugログに出力するjsonを整形するかどうかを指定します。
		 * trueの場合成形されたjsonをログ出力に出力します。
		 */
		private Boolean jsonDebug = null;
		/**
		 * 	使用する文字コードを指定します。
		 */
		private String encoding = null;

		
		/**
		 * コンストラクタ。
		 */
		public Application() {
			
		}
	}
	
	/**
	 * DataForms設定情報。
	 */
	@Data
	public static class Conf implements ConfClass {
		/**
		 * 開発ツール設定情報。
		 */
		private DevelopmentTool developmentTool = null;
		
		/**
		 * アプリケーション初期化設定。
		 */
		private Initialize initialize = null;
		
		/**
		 * アプリケーション動作設定。
		 */
		private Application application = null;
		
		/**
		 * コンストラクタ。
		 */
		public Conf() {
			
		}
		
		/**
		 * 設定ファイルを読み込みます。
		 * @param confFile 設定ファイル。
		 * @return 設定字情報。
		 * @throws Exception 例外。
		 */
		public static Conf read(final String confFile) throws Exception {
			String jsonc = FileUtil.readTextFile(confFile, ENCODING);
			return readJson(jsonc);
		}

		/**
		 * JSON形式のテキストを読み込みます。
		 * @param jsonc JSON形式の文字列。
		 * @return 設定情報。
		 */
		private static Conf readJson(final String jsonc) {
			Conf appConf  =  (Conf) JsonUtil.decode(jsonc, Conf.class);
			return appConf;
		}
		
		/**
		 * 移行元のJNDIデータソースを取得します。
		 * @return 移行元のJNDIデータソース。
		 */
		public JndiDataSource getOriginalJndiDataSource() {
			String prefix = this.getApplication().getJndiDataSource().getJndiPrefix();
			String dataSource = this.getApplication().getJndiDataSource().getDataSource();
			if (this.getDevelopmentTool().getOriginDataSource() != null) {
				dataSource = this.getDevelopmentTool().getOriginDataSource();
			}
			
			JndiDataSource ret = new JndiDataSource(prefix, dataSource);
			return ret;
		}
	}
	
	/**
	 * 設定情報マップ。
	 */
	@Getter
	private  Conf conf = null;

	/**
	 * コンストラクタ。
	 */
	public ConfUtil() {
		
	}

	/**
	 * オブジェクトのプロパティを取得します。
	 * @param obj オブジェクト。
	 * @param pname プロパティ名。
	 * @return プロパティの値。
	 * @throws Exception 例外。
	 */
	private Object getProperty(final Object obj, final String pname) throws Exception {
		if ("originalJndiDataSource".equals(pname)) {
			return null;
		}
		String mname = "get" + pname.substring(0, 1).toUpperCase() + pname.substring(1);
		logger.debug("getProperty() obj=" + obj.getClass().getName() + " -> " + mname);
		Class<?>[] arg = new Class<?>[0]; 
		Method m = obj.getClass().getMethod(mname, arg);
		Object[] p = new Object[0]; 
		Object ret = m.invoke(obj, p);
		return ret;
	}
	

	/**
	 * jarの中のデフォルト設定ファイルを取得する。
	 * @return jarの中のデフォルト設定ファイル。
	 * @throws Exception 例外。
	 */
	public String getDefaultConfFile() throws Exception {
		Class<?> cls = this.getClass();
		InputStream is = cls.getResourceAsStream("./conf/dataforms.conf.jsonc");
		String ret = new String(FileUtil.readInputStream(is), "utf-8");
//		logger.debug("*** デフォルト設定 = \n" + ret);
		return ret;
	}
	
	/**
	 * 設定ファイルを取得します。
	 * @param path パス。
	 * @return 設定ファイルの内容。
	 * @throws Exception 例外。
	 */
	public String getConfFile(final String path) throws Exception {
		Class<?> cls = this.getClass();
		InputStream is = cls.getResourceAsStream(path);
		String ret = new String(FileUtil.readInputStream(is), "utf-8");
//		logger.debug("*** デフォルトweb.xml = \n" + ret);
		return ret;
	}
	
	/**
	 * デフォルトのweb.xmlを取得します。
	 * @return web.xmlの内容。
	 * @throws Exception 例外。
	 */
	public String getWebXML() throws Exception {
		Class<?> cls = this.getClass();
		InputStream is = cls.getResourceAsStream("./conf/web.xml");
		String ret = new String(FileUtil.readInputStream(is), "utf-8");
		logger.debug("*** デフォルトweb.xml = \n" + ret);
		return ret;
	}
	
	/**
	 * デフォルトのweb.xmlを取得します。
	 * @return web.xmlの内容。
	 * @throws Exception 例外。
	 */
	public String getContextXML() throws Exception {
		Class<?> cls = this.getClass();
		InputStream is = cls.getResourceAsStream("./conf/context.xml");
		String ret = new String(FileUtil.readInputStream(is), "utf-8");
		logger.debug("*** デフォルトcontext.xml = \n" + ret);
		return ret;
	}
	
	/**
	 * デフォルトのweb.xmlを取得します。
	 * @return web.xmlの内容。
	 * @throws Exception 例外。
	 */
	public String getLog4j2XML() throws Exception {
		Class<?> cls = this.getClass();
		InputStream is = cls.getResourceAsStream("./conf/log4j2.xml");
		String ret = new String(FileUtil.readInputStream(is), "utf-8");
		logger.debug("*** デフォルトlog4j2.xml = \n" + ret);
		return ret;
	}
	
	/**
	 * デフォルトのweb.xmlを取得します。
	 * @return web.xmlの内容。
	 * @throws Exception 例外。
	 */
	public String getFopXconf() throws Exception {
		Class<?> cls = this.getClass();
		InputStream is = cls.getResourceAsStream("./conf/fop.xconf");
		String ret = new String(FileUtil.readInputStream(is), "utf-8");
		logger.debug("*** デフォルトfop.xconf = \n" + ret);
		return ret;
	}
	

	/**
	 * Confをコピーします。
	 * @param src コピー元。
	 * @param dst コピー先。
	 * @throws Exception 例外。
	 */
	public void copyConf(final Object src, final Object dst) throws Exception {
		if (src == null) {
			return;
		}
//		logger.debug("*** className = " + src.getClass().getName() + " -> " + dst.getClass().getName());
		Map<String, Object> map = this.getPropertyMap(src);
		for (String key: map.keySet()) {
			Object obj = map.get(key);
			Object odst = this.getProperty(dst, key);
			if (obj != null) {
				if (obj instanceof ConfClass) {
					this.copyConf(obj, odst);
				} else {
/*					logger.debug("*** key = " + key);
					logger.debug("*** obj = " + obj.getClass().getName());
					if (odst != null) {
						logger.debug("*** odst = " +  odst.getClass().getName());
					}*/
					String name = key;
					Object value = map.get(key);
					logger.debug("copyConf name=" + name + ",value=" + value);
					BeanUtils.setProperty(dst, key, value);
				}
			}
		}
	}	

	/**
	 * プロパティマップを取得します。
	 * @param src ソース。
	 * @return プロパティマップ。
	 * @throws Exception 例外。
	 */
	public Map<String, Object> getPropertyMap(final Object src) throws Exception {
		Map<String, Object> ret = new HashMap<String, Object>();
		Method[] mlist = src.getClass().getMethods();
		for (Method m: mlist) {
			String fn = m.getName();
			if ("getOriginalJndiDataSource".equals(fn)) {
				continue;
			}
			if (fn.indexOf("get") == 0) {
				String pname = fn.substring(3, 4).toLowerCase() + fn.substring(4);
				logger.debug("pname=" + pname);
				Object obj = m.invoke(src);
				ret.put(pname, obj);
			}
		}
		return ret;
	}
	
	
	
	/**
	 * アプリケーションのデフォルト設定を読み込みます。
	 * @param servlet DataFormsServlet。
	 */
	public void readDefaultConf(DataFormsServlet servlet) {
		try {
			// jarのリソース中のデフォルト設定を取得する。
 			String defaultJsonc = this.getDefaultConfFile();
			this.conf  =  (Conf) JsonUtil.decode(defaultJsonc, Conf.class);
			{
				// アプリケーション設定ファイルの読み込み
				String confPath = servlet.getServletContext().getRealPath("/WEB-INF/dataforms.conf.jsonc");
				File cf = new File(confPath);
				if (cf.exists()) {
					logger.debug("confPath=" + confPath);
					Conf appConf  =  Conf.read(confPath);
					this.conf = appConf;
				}
			}
			{
				// サーバ設定ファイルを読み込む
				if (this.conf != null) {
					String serverConf = this.conf.getApplication().getServerConfigFile();
					if (serverConf != null) {
						File cf = new File(serverConf);
						if (cf.exists()) {
							Conf appConf = Conf.read(serverConf);
							this.copyConf(appConf, this.conf);
						}
					}
				}
			}
			logger.debug("conf json=\n" + JsonUtil.encode(this.conf, true));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		
	}
}
