package jp.dataforms.fw.util;

import java.util.List;
import java.util.Map;

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
	 * 開発ツール設定情報。
	 */
	@Data
	public static class DevelopmentTool {
		/**
		 * Javaのソースパス。
		 */
		private String javaSourcePath = null;
		/**
		 * HTML, jsのソースパス。
		 */
		private String webSourcePath = null;
		
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
	}
	
	/**
	 * アプリケーション初期化設定。
	 */
	@Data
	public static class Initialize {
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
		 * dataforms2.jarに含まれたUserInfoTableに項目を追加する場合、
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
	}
	
	/**
	 * JNDI Data Source設定。
	 */
	@Data
	public static class JndiDataSource {
		/**
		 * JNDIデータソースの前につける文字列。
		 */
		private String jndiPrefix = null;
		/**
		 * 使用するJNDIデータソースを指定する。
		 */
		private String dataSource = null;
	}
	
	/**
	 * Mail設定。
	 */
	@Data
	public static class Mail {
		/**
		 * mailSessionの名称。
		 */
		private String mailSession = null;
		/**
		 * メール送信者アドレス。
		 */
		private String mailFrom = null;
	}
	
	
	/**
	 * ユーザ編集フォーム設定。
	 * TODO: この項目の構造を健闘。
	 */
	@Data
	public static class UserEditFormConfig {
		/**
		 * メールアドレスの必須チェック設定。
		 */
		private Boolean requiredMailAddress = null;
	}
	
	/**
	 * ユーザ登録ページの設定。
	 */
	@Data
	public static class UserRegistPageConfig {
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
	}
	
	/**
	 * メッセージリソース設定。
	 */
	@Data
	public static class MessageResource {
		/**
		 * クライアントに送信して使用するメッセージリソースの名称を指定します。
		 * このファイルはdataforms2.jar内に存在します。
		 */
		private String clientMessages = null;
		/**
		 * サーバ側で使用するメッセージリソースの名称を指定します。
		 * このファイルはdataforms2.jar内に存在します。
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
	}
	
	/**
	 * 暗号化設定。
	 */
	@Data
	public static class CryptConfig {
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
		
	}
	
	/**
	 * この設定を有効にすると、認証時にユーザのメールアドレスにワンタイムパスワードを送信し
	 * その確認を行います。
	 */
	@Data
	public static class OnetimePasswordConfig {
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
		
	}
	
	/**
	 * ストリーミングブロックサイズ。
	 */
	@Data
	public static class StreamingBlockSize {
		/**
		 * UserAgentパターン。
		 */
		private String uaPattern = null;
		/**
		 * ブロックサイズ。
		 */
		private Integer blockSize = null;
	}
	
	/**
	 * ContentType情報。
	 */
	@Data
	public static class ContentType {
		/**
		 * ファイル名パターン。
		 */
		private String fnPattern = null;
		/**
		 * Content-type。
		 */
		private String contentType = null;
	}
	
	/**
	 * ページオーバーライド情報。
	 */
	@Data
	public static class PageOverride {
		/**
		 * 変更元のページ。
		 */
		private String from = null;
		/**
		 * 変更先のページ。
		 */
		private String to = null;
	}
	
	/**
	 * アプリケーション設定。
	 */
	@Data
	public static class Application {
		/**
		 * WebリソースURL。
		 */
		private String webResourceUrl = null;
		
		/**
		 * サポート言語リスト。
		 */
		private List<String> languageList = null;
		/**
		 * 言語固定設定。
		 */
		private String fixedLanguage = null;
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

	}
	
	/**
	 * DataForms設定情報。
	 */
	@Data
	public static class Conf {
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
	 * アプリケーションのデフォルト設定を読み込みます。
	 * @param servlet DataFormsServlet。
	 * @throws Exception 例外。
	 */
	public void readDefaultConf(DataFormsServlet servlet) {
		try {
			String confPath = servlet.getServletContext().getRealPath("/WEB-INF/dataformsConf.jsonc");
			logger.debug("confPath=" + confPath);
			String jsonc = FileUtil.readTextFile(confPath, ENCODING);
//			logger.debug("dataformsConf.jsonc=\n" + jsonc);
			this.conf  =  (Conf) JsonUtil.decode(jsonc, Conf.class);
/*			logger.debug("java src path=" + conf.developmentTool.getJavaSourcePath());
			logger.debug("java web path=" + conf.developmentTool.getWebSourcePath());
			logger.debug("disable code gen=" + conf.developmentTool.getDisableCodeGenerationTool());
			logger.debug("db package list=" + conf.appInitialize.getDatabasePackageList());
*/
			logger.debug("conf json=\n" + JsonUtil.encode(this.conf));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		
	}
}
