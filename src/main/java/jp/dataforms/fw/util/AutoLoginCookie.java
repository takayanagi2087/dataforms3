package jp.dataforms.fw.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpSession;
import jp.dataforms.fw.app.user.dao.UserDao;
import jp.dataforms.fw.app.user.dao.UserInfoTable;
import jp.dataforms.fw.controller.Page;
import jp.dataforms.fw.controller.WebEntryPoint;
import jp.dataforms.fw.devtool.db.dao.TableManagerDao;
import jp.dataforms.fw.exception.ApplicationException;
import jp.dataforms.fw.servlet.DataFormsServlet;

/**
 * 自動ログイン制御クラス。
 *
 */
public final class AutoLoginCookie {

	/**
	 * Logger.
	 */
	private static Logger logger = LogManager.getLogger(AutoLoginCookie.class);

	/**
	 * 自動ログイン情報のクッキーID。
	 */
	private static final String ID_AUTO_LOGIN_INFO = "loginInfo";

	/**
	 * Login維持フラグのID。
	 */
	public static final String ID_KEEP_LOGIN = "keepLogin";

	/**
	 * Cookieの有効期限。
	 */
	private static final int COOKIE_MAX_AGE = 10 * 365 * 24 * 60 * 60;


	/**
	 * 自動ログイン。
	 */
	private static boolean autoLogin = false;

	/**
	 * クッキーのセキュアフラグ。
	 */
	private static boolean secure = false;


	/**
	 * コンストラクタ。
	 */
	private AutoLoginCookie() {

	}

	/**
	 * 自動ログインの許可状況を取得します。
	 * @return 許可されている場合true。
	 */
	public static boolean isAutoLogin() {
		return autoLogin;
	}

	/**
	 * 自動ログインの許可状況を設定します。
	 * @param autoLogin 許可する場合true。
	 */
	public static void setAutoLogin(final boolean autoLogin) {
		AutoLoginCookie.autoLogin = autoLogin;
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
		AutoLoginCookie.secure = secure;
	}

	/**
	 * 自動ログイン処理を行います。
	 * @param page ページ。
	 * @throws Exception 例外。
	 */
	public static void autoLogin(final Page page) throws Exception {
		if (AutoLoginCookie.autoLogin) {
			Cookie cookie = page.getCookie(ID_AUTO_LOGIN_INFO);
			if (cookie != null) {
				String val = cookie.getValue();
				if (!StringUtil.isBlank(val)) {
					logger.debug(() -> "autoLogin userInfo=" + val);
					String json = null;
					try {
						json = CryptUtil.decrypt(val, DataFormsServlet.getQueryStringCryptPassword());
					} catch (Exception e) {
						logger.debug(e.getMessage());
					}
					logger.debug("userInfo json={}", json);
					if (json != null) {
						TableManagerDao tmdao = new TableManagerDao(page);
						if (tmdao.isDatabaseInitialized()) {
							UserDao dao = new UserDao(page);
							@SuppressWarnings("unchecked")
							Map<String, Object> p = (Map<String, Object>) JsonUtil.decode(json, HashMap.class);
							UserInfoTable.Entity pe = new UserInfoTable.Entity(p);
							try {
								Map<String, Object> userInfo = dao.login(pe.getMap(), false);
								HttpSession session = page.getRequest().getSession();
								session.setAttribute(WebEntryPoint.USER_INFO, userInfo);
								String clientInfo = UserLogUtil.getClientInfo(page, userInfo);
								logger.info(clientInfo + "Automatically logged in");
							} catch (ApplicationException ex) {
								;
							}
						}
					}
				}
			}
		}
	}

	/**
	 * 自動ログイン用クッキーを設定します。
	 * @param page ページ。
	 * @param p ログインパラメータ。
	 * @throws Exception 例外。
	 */
	public static void setAutoLoginCookie(final Page page, final Map<String, Object> p) throws Exception {
		logger.debug(() -> "secure cookie=" + secure);
		String keepLoginFlag = "0";
		Cookie cookie = new Cookie(ID_AUTO_LOGIN_INFO, "");
		if (AutoLoginCookie.autoLogin) {
			String keepLogin = (String) p.get(ID_KEEP_LOGIN);
			if ("1".equals(keepLogin)) {
				keepLoginFlag = "1";
				String loginId = (String) p.get(UserInfoTable.Entity.ID_LOGIN_ID);
				String password = (String) p.get(UserInfoTable.Entity.ID_PASSWORD);
				Map<String, String> loginInfo = new HashMap<String, String>();
				loginInfo.put(UserInfoTable.Entity.ID_LOGIN_ID, loginId);
				loginInfo.put(UserInfoTable.Entity.ID_PASSWORD, password);
				String json = JsonUtil.encode(loginInfo);
				String userInfo = CryptUtil.encrypt(json, DataFormsServlet.getQueryStringCryptPassword());
				logger.debug(() -> "json=" + json + ",userInfo=" + userInfo);
				cookie = new Cookie(ID_AUTO_LOGIN_INFO, userInfo);
			}
		}
		cookie.setHttpOnly(true);
		cookie.setMaxAge(COOKIE_MAX_AGE);
		cookie.setPath(page.getRequest().getContextPath());
		cookie.setSecure(secure);
		page.getResponse().addCookie(cookie);

		Cookie flgCookie = new Cookie(ID_KEEP_LOGIN, keepLoginFlag);
		flgCookie.setHttpOnly(true);
		flgCookie.setMaxAge(COOKIE_MAX_AGE);
		flgCookie.setPath(page.getRequest().getContextPath());
		flgCookie.setSecure(secure);
		page.getResponse().addCookie(flgCookie);

	}

	/**
	 * 自動ログインクッキーをクリアします。
	 * @param page ページ。
	 */
	public static void clearAutoLoginCookie(final Page page) {
		Cookie cookie = new Cookie(ID_AUTO_LOGIN_INFO, "");
		cookie.setHttpOnly(true);
		cookie.setMaxAge(COOKIE_MAX_AGE);
		cookie.setPath(page.getRequest().getContextPath());
		cookie.setSecure(secure);
		page.getResponse().addCookie(cookie);
	}

	/**
	 * ログイン保持フラグを取得します。
	 * @param page ページ。
	 * @return ログイン保持フラグ。
	 */
	public static String getKeepLoginFlag(final Page page) {
		Cookie cookie = page.getCookie(ID_KEEP_LOGIN);
		if (cookie != null) {
			return cookie.getValue();
		}
		return "0";
	}

	/**
	 * 自動ログインクッキーの存在チェックを行います。
	 * @param page ページ。
	 * @return 存在する場合true。
	 */
	public static boolean exists(final Page page) {
		Cookie cookie = page.getCookie(ID_AUTO_LOGIN_INFO);
		if (cookie != null) {
			if (cookie.getValue().length() > 0) {
				return true;
			}
		}
		return false;
	}
}
