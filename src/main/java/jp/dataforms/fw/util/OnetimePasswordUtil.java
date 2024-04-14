package jp.dataforms.fw.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.servlet.http.Cookie;
import jp.dataforms.fw.app.user.dao.UserInfoTable;
import jp.dataforms.fw.controller.Page;
import jp.dataforms.fw.servlet.DataFormsServlet;

/**
 * ワンタイムパスワードユーティリティ。
 *
 */
public class OnetimePasswordUtil {

	/**
	 * Logger.
	 */
	private static Logger logger = LogManager.getLogger(OnetimePasswordUtil.class);

	/**
	 * 設定キー。
	 */
	public static final String CONFIG_KEY = "onetime-password-config";

	/**
	 * ワンタイムパスワードのセッションキー。
	 */
	public static final String ONETIME = "onetime";

	/**
	 * ワンタイムパスワード確認時のユーザ情報を保持するセッションキー。
	 */
	public static final String USERINFO = "userInfo.onetime";

	/**
	 * Onetimeパスワード確認スキップクッキーの名称。
	 */
	private static final String SKIP_ONETIME = "skipOnetime";

	/**
	 * ワンタイムパスワード利用フラグ。
	 */
	private static boolean useOnetimePassword = false;
	/**
	 * ワンタイムパスワードの長さ。
	 */
	private static int onetimePasswordLength = 6;
	/**
	 * ワンタイムパスワードキャンセルクッキーの有効日数。
	 */
	private static int cookieExpiration = 20;

	/**
	 * クッキーのセキュアフラグ。
	 */
	private static boolean secure = false;

	/**
	 * コンストラクタ。
	 */
	private OnetimePasswordUtil() {

	}

	/**
	 * クッキーのSecureフラグを取得します。
	 * @return クッキーのSecureフラグ。
	 */
	public static boolean isSecure() {
		return secure;
	}

	/**
	 * クッキーのSecureフラグを設定します。
	 * @param secure クッキーのSecureフラグ。
	 */
	public static void setSecure(final boolean secure) {
		OnetimePasswordUtil.secure = secure;
	}

	/**
	 * ワンタイムパスワード利用フラグを取得します。
	 * @return ワンタイムパスワード利用フラグ。
	 */
	public static boolean isUseOnetimePassword() {
		return useOnetimePassword;
	}

	/**
	 * ワンタイムパスワード利用フラグを設定します。
	 * @param useOnetimePassword ワンタイムパスワード利用フラグ。
	 */
	public static void setUseOnetimePassword(final boolean useOnetimePassword) {
		OnetimePasswordUtil.useOnetimePassword = useOnetimePassword;
	}

	/**
	 * ワンタイムパスワードの長さを取得します。
	 * @return ワンタイムパスワードの長さ。
	 */
	public static int getOnetimePasswordLength() {
		return onetimePasswordLength;
	}

	/**
	 * ワンタイムパスワードの長さを設定します。
	 * @param onetimePasswordLength ワンタイムパスワードの長さ。
	 */
	public static void setOnetimePasswordLength(final int onetimePasswordLength) {
		OnetimePasswordUtil.onetimePasswordLength = onetimePasswordLength;
	}

	/**
	 * ワンタイムパスワードキャンセルクッキーの有効日数を取得します。
	 * @return ワンタイムパスワードキャンセルクッキーの有効日数。
	 */
	public static int getCookieExpiration() {
		return cookieExpiration;
	}

	/**
	 * ワンタイムパスワードキャンセルクッキーの有効日数を設定します。
	 * @param cookieExpiration ワンタイムパスワードキャンセルクッキーの有効日数。
	 */
	public static void setCookieExpiration(final int cookieExpiration) {
		OnetimePasswordUtil.cookieExpiration = cookieExpiration;
	}

	/**
	 * 設定情報を設定します。
	 * @param conf 設定情報。
	 */
	public static void setConfig(Map<String, Object> conf) {
		Boolean useOnetime = (Boolean) conf.get("useOnetimePassword");
		OnetimePasswordUtil.useOnetimePassword = useOnetime;
		int length = ((Double) conf.get("length")).intValue();
		OnetimePasswordUtil.onetimePasswordLength = length;
		int expiration = ((Double) conf.get("cookieExpiration")).intValue();
		OnetimePasswordUtil.cookieExpiration = expiration;
		logger.debug("useOnetimePassword=" + OnetimePasswordUtil.useOnetimePassword);
		logger.debug("onetimePasswordLength=" + OnetimePasswordUtil.onetimePasswordLength);
		logger.debug("cookieExpiration=" + OnetimePasswordUtil.cookieExpiration);
	}


	/**
	 * ワンタイムパスワードを生成する。
	 * @return ワンタイムパスワード。
	 */
	public static String generateOnetimePassword() {
		long seed = (new java.util.Date()).getTime();
		Random r = new Random(seed);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < OnetimePasswordUtil.onetimePasswordLength; i++) {
			int v = r.nextInt(10);
			sb.append(Integer.toString(v));
		}
		return sb.toString();
	}

	/**
	 * ワンタイムパスワードスキップクッキーを設定する。
	 * @param page ページ。
	 * @param p ログインパラメータ。
	 * @throws Exception 例外。
	 */
	public static void setSkipOnetimeCookie(final Page page, final Map<String, Object> p) throws Exception {
		logger.debug("setSkipOnetimeCookie");
		logger.debug(() -> "secure cookie=" + secure);
		Cookie cookie = new Cookie(OnetimePasswordUtil.SKIP_ONETIME, "");
		if (OnetimePasswordUtil.isUseOnetimePassword()) {
			String loginId = (String) p.get(UserInfoTable.Entity.ID_LOGIN_ID);
//			String password = (String) p.get(UserInfoTable.Entity.ID_PASSWORD);
			String mailAddress = (String) p.get(UserInfoTable.Entity.ID_MAIL_ADDRESS);
			Map<String, String> loginInfo = new HashMap<String, String>();
			loginInfo.put(UserInfoTable.Entity.ID_LOGIN_ID, loginId);
//			loginInfo.put(UserInfoTable.Entity.ID_PASSWORD, password);
			loginInfo.put(UserInfoTable.Entity.ID_MAIL_ADDRESS, mailAddress);
			String json = JsonUtil.encode(loginInfo);
			String userInfo = CryptUtil.encrypt(json, DataFormsServlet.getQueryStringCryptPassword());
			logger.debug(() -> "json=" + json + ",userInfo=" + userInfo);
			cookie = new Cookie(SKIP_ONETIME, userInfo);
		}
		cookie.setHttpOnly(true);
		cookie.setMaxAge(OnetimePasswordUtil.cookieExpiration * 24 * 60 * 60);
//		cookie.setMaxAge(60);
		cookie.setPath(page.getRequest().getContextPath());
		cookie.setSecure(secure);
		page.getResponse().addCookie(cookie);
	}

	/**
	 * ワンタイムパスワードの確認が必要かどうかを確認する。
	 * @param page ページ。
	 * @param userInfo ユーザ情報。
	 * @return ワンタイムパスワードの確認が必要な場合true。
	 * @throws Exception 例外。
	 */
	public static boolean needConfirmation(final Page page, final Map<String, Object> userInfo) throws Exception {
		boolean ret = true;
		if (!OnetimePasswordUtil.isUseOnetimePassword()) {
			// ワンタイムパスワード使用しないアプリケーションの場合
			ret = false;
		} else {
			Cookie cookie = page.getCookie(SKIP_ONETIME);
			if (cookie != null) {
				String value = cookie.getValue();
				String json = CryptUtil.decrypt(value, DataFormsServlet.getQueryStringCryptPassword());
				logger.debug("skipOnetime=" + json);
				@SuppressWarnings("unchecked")
				Map<String, Object> map = (Map<String, Object>) JsonUtil.decode(json, HashMap.class);
				String loginId = (String) userInfo.get(UserInfoTable.Entity.ID_LOGIN_ID);
				String loginId0 = (String) map.get(UserInfoTable.Entity.ID_LOGIN_ID);
				logger.debug(loginId + ":" + loginId0);
				if (loginId.equals(loginId0)) {
					ret = false;
				}
			}
		}
		return ret;
	}
}
