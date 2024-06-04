package jp.dataforms.fw.app.login.page;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.webauthn4j.credential.CredentialRecord;
import com.webauthn4j.data.AuthenticationData;
import com.webauthn4j.data.client.Origin;
import com.webauthn4j.data.client.challenge.Challenge;
import com.webauthn4j.data.client.challenge.DefaultChallenge;
import com.webauthn4j.server.ServerProperty;

import jakarta.servlet.http.HttpSession;
import jp.dataforms.fw.annotation.WebMethod;
import jp.dataforms.fw.app.login.field.PasskeySingleSelectField;
import jp.dataforms.fw.app.user.dao.UserDao;
import jp.dataforms.fw.app.user.dao.UserInfoTable;
import jp.dataforms.fw.app.user.dao.WebAuthnDao;
import jp.dataforms.fw.app.user.dao.WebAuthnTable;
import jp.dataforms.fw.app.user.field.LoginIdField;
import jp.dataforms.fw.app.user.field.PasswordField;
import jp.dataforms.fw.controller.Form;
import jp.dataforms.fw.controller.WebEntryPoint;
import jp.dataforms.fw.exception.ApplicationException;
import jp.dataforms.fw.field.common.FlagField;
import jp.dataforms.fw.response.JsonResponse;
import jp.dataforms.fw.response.Response;
import jp.dataforms.fw.util.AutoLoginCookie;
import jp.dataforms.fw.util.MessagesUtil;
import jp.dataforms.fw.util.OnetimePasswordUtil;
import jp.dataforms.fw.util.StringUtil;
import jp.dataforms.fw.util.WebAuthnUtil;
import jp.dataforms.fw.validator.RequiredValidator;
import jp.dataforms.fw.validator.ValidationError;

/**
 * ログインフォームクラス。
 *
 */
public class LoginForm extends Form {

	/**
	 * Logger.
	 */
	private static Logger logger = LogManager.getLogger(LoginForm.class.getName());

	/**
	 * WebAuthnのGet時のオプション用セッションキー。
	 */
	private static final String WEB_AUTHN_GET_OPTION = "webAuthnGetOption";

	/**
	 * WebAuthn情報用セッションキー。
	 */
	private static final String WEB_AUTHN_INFO = "webAuthnInfo";

	/**
	 * 最後最終ログイン情報を保存フラグのID。
	 */
	private static final String ID_SAVE_LAST_LOGIN = "saveLastLogin";
	
	/**
	 * ユーザ登録ページのアドレス。
	 */
	private static String passwordResetMailPage = null;



	/**
	 * コンストラクタ。
	 */
	public LoginForm() {
		super("loginForm");
		this.addField(new LoginIdField()).addValidator(new RequiredValidator());
		PasswordField pw = new PasswordField();
		this.addField(pw);
		this.addField(new PasskeySingleSelectField(WebAuthnTable.Entity.ID_AUTHENTICATOR_NAME));
		this.addField(new FlagField(AutoLoginCookie.ID_KEEP_LOGIN));
		this.addField(new FlagField(ID_SAVE_LAST_LOGIN));
	}

	@Override
	public void init() throws Exception {
		super.init();
		this.setFormData(AutoLoginCookie.ID_KEEP_LOGIN, AutoLoginCookie.getKeepLoginFlag(this.getPage()));
	}

	/**
	 * 自動ログイン処理を行います。
	 * @throws Exception 例外。
	 */
	public void autoLogin() throws Exception {
		AutoLoginCookie.autoLogin(this.getPage());
	}

	@Override
	protected List<ValidationError> validateForm(Map<String, Object> data) throws Exception {
		List<ValidationError> elist = super.validateForm(data);
		if (elist.size() == 0) {
			String loginId = (String) data.get(UserInfoTable.Entity.ID_LOGIN_ID);
			String password = (String) data.get(UserInfoTable.Entity.ID_PASSWORD);
			String passkey = (String) data.get(WebAuthnTable.Entity.ID_AUTHENTICATOR_NAME);
			if (StringUtil.isBlank(password) && StringUtil.isBlank(passkey)) {
				// Passkey Passwordの両方が入力されなかった場合
				elist.add(new ValidationError(UserInfoTable.Entity.ID_PASSWORD, MessagesUtil.getMessage(getWebEntryPoint(), "error.required")));
				elist.add(new ValidationError(WebAuthnTable.Entity.ID_AUTHENTICATOR_NAME, MessagesUtil.getMessage(getWebEntryPoint(), "error.required")));
			}
			if (elist.size() == 0) {
				// ユーザ情報を取得。
				UserDao udao = new UserDao(this);
				Map<String, Object> userInfo = udao.queryUserInfo(loginId);
				if (userInfo != null) {
					UserInfoTable.Entity ue = new UserInfoTable.Entity(userInfo);
					if ("1".equals(ue.getPasswordRequiredFlag())) {
						// パスワード必須の場合
						if (StringUtil.isBlank(password)) {
							elist.add(new ValidationError(UserInfoTable.Entity.ID_PASSWORD, MessagesUtil.getMessage(getWebEntryPoint(), "error.required")));
						}
					}
					// PassKeyの情報を取得。
					WebAuthnDao pdao = new WebAuthnDao(this);
					List<Map<String, Object>> plist = pdao.query(loginId);
					if ("1".equals(ue.getPasskeyRequiredFlag()) && plist.size() > 0) {
						// Passkeyが必須でかつPasskeyが登録されている場合
						if (StringUtil.isBlank(passkey)) {
							logger.debug("passkey=" + passkey);
							elist.add(new ValidationError(WebAuthnTable.Entity.ID_AUTHENTICATOR_NAME, MessagesUtil.getMessage(getWebEntryPoint(), "error.required")));
						}
					}
				} else {
					throw new ApplicationException(this.getWebEntryPoint(), "error.invaliduserid");
				}
			}
		}
		return elist;
	}
	
	/**
	 * パスワードログインの処理を行います。
	 * @param params パラメータ。
	 * @return ログイン結果。
	 * @throws Exception 例外。
	 */
	@WebMethod
	public JsonResponse login(final Map<String, Object> params) throws Exception {
		// ログからパスワードを削除
		Map<String, Object> nopass = new HashMap<String, Object>();
		nopass.putAll(params);
		nopass.remove("password");
		JsonResponse ret = null;
		List<ValidationError> elist = this.validate(params);
		if (elist.size() > 0) {
			ret = new JsonResponse(JsonResponse.INVALID, elist);
			logger.warn("login fail");
		} else {
			UserDao dao = new UserDao(this);
			Map<String, Object> userInfo = dao.login(params);
			if (OnetimePasswordUtil.needConfirmation(this.getPage(), userInfo)) {
				// ワンタイムパスワード確認モード。
				String keepLogin = (String) params.get(AutoLoginCookie.ID_KEEP_LOGIN);
				userInfo.put(AutoLoginCookie.ID_KEEP_LOGIN, keepLogin);
				HttpSession session = this.getPage().getRequest().getSession();
				session.setAttribute(OnetimePasswordUtil.USERINFO, userInfo);
				ret = new JsonResponse(JsonResponse.SUCCESS, "onetime");
			} else {
				HttpSession session = this.getPage().getRequest().getSession();
				session.setAttribute(WebEntryPoint.USER_INFO, userInfo);
				logger.info(() -> "login success=" + userInfo.get("loginId") + "(" + userInfo.get("userId") + ")");
				AutoLoginCookie.setAutoLoginCookie(this.getPage(), params);
				ret = new JsonResponse(JsonResponse.SUCCESS, "");
			}
		}
		return ret;
	}

	@Override
	public Map<String, Object> getProperties() throws Exception {
		Map<String, Object> ret = super.getProperties();
		if (!StringUtil.isBlank(LoginForm.getPasswordResetMailPage())) {
			ret.put("passwordResetMailPage", LoginForm.getPasswordResetMailPage() + "." + this.getPage().getPageExt());
		}
		ret.put("autoLogin", AutoLoginCookie.isAutoLogin());
		return ret;
	}

	/**
	 * パスワードリセットメール送信ページを取得します。
	 * @return パスワードリセットメール送信ページ。
	 */
	public static String getPasswordResetMailPage() {
		return passwordResetMailPage;
	}

	/**
	 * パスワードリセットメール送信ページを設定します。
	 * @param passwordResetMailPage パスワードリセットメール送信ページ。
	 *
	 */
	public static void setPasswordResetMailPage(final String passwordResetMailPage) {
		LoginForm.passwordResetMailPage = passwordResetMailPage;
	}

	/**
	 * ユーザが登録したパスキーの一覧を取得します。
	 * @param p POSTされたパラメータ。
	 * @return ユーザが登録したパスキーの一覧。
	 * @throws Exception 例外。
	 */
	@WebMethod
	public Response getPassKeyList(final Map<String, Object> p) throws Exception {
		String loginId = (String) p.get(UserInfoTable.Entity.ID_LOGIN_ID);
		List<String> ret = new ArrayList<String>();
		WebAuthnDao dao = new WebAuthnDao(this);
		List<Map<String, Object>> list = dao.query(loginId);
		for (Map<String, Object> m: list) {
			WebAuthnTable.Entity e = new WebAuthnTable.Entity(m);
			ret.add(e.getAuthenticatorName());
		}
		Response resp = new JsonResponse(JsonResponse.SUCCESS, ret);
		return resp;
	}

	
	/**
	 * PassKey作成時のオプションを取得します。
	 * @param p パラメータ。
	 * @return 認証時のオプション。
	 * @throws Exception 例外。
	 */
	@WebMethod
	public Response getOption(final Map<String, Object> p) throws Exception {
		JsonResponse resp = null;
		List<ValidationError> elist = this.validate(p);
		if (elist.size() > 0) {
			resp = new JsonResponse(JsonResponse.INVALID, elist);
			logger.warn("getOption fail");
		} else {
			String loginId = (String) p.get(UserInfoTable.Entity.ID_LOGIN_ID);
			String authenticatorName = (String) p.get(WebAuthnTable.Entity.ID_AUTHENTICATOR_NAME);
			logger.debug("authenticatorName=" + authenticatorName);
			WebAuthnDao dao = new WebAuthnDao(this);
			Map<String, Object> m = dao.queryWebAuthnInfo(loginId, authenticatorName);
			WebAuthnTable.Entity e = new WebAuthnTable.Entity(m);
			Map<String, Object> ret = new HashMap<String, Object>();
			ret.put("id", e.getAuthId());
			ret.put("challenge", WebAuthnUtil.generateChallenge());
			
			HttpSession session = this.getPage().getRequest().getSession();
			// 送信したオプションをDBに保存しておく。
			session.setAttribute(WEB_AUTHN_GET_OPTION, ret);
			// DBから取得した確認情報レコードをセッションに和損しておく
			session.setAttribute(WEB_AUTHN_INFO, m);
			resp = new JsonResponse(JsonResponse.SUCCESS, ret);
		}
		return resp;
	}
	
	/**
	 * ServerPropertyを取得します。
	 * @return ServerProperty。
	 */
	private ServerProperty getServerProperty() {
		HttpSession session = this.getPage().getRequest().getSession();
		@SuppressWarnings("unchecked")
		Map<String, Object> opt= (Map<String, Object>) session.getAttribute(WEB_AUTHN_GET_OPTION);
		Origin origin = new Origin(this.getOrigin());
		String rpId = this.getPage().getRequest().getServerName();
		String ch = (String) opt.get("challenge");
		logger.debug("challenge=" + ch);
		Challenge challenge = new DefaultChallenge(ch.getBytes());
		byte[] tokenBindingId = null;
		ServerProperty ret = new ServerProperty(origin, rpId, challenge, tokenBindingId);
		return ret;
	}

	/**
	 * セッションから確認情報レコードを取得する。
	 * @return 確認情報レコード。
	 */
	private CredentialRecord getCredentialRecord() {
		HttpSession session = this.getPage().getRequest().getSession();
		@SuppressWarnings("unchecked")
		Map<String, Object> webAuthnInfo = (Map<String, Object>) session.getAttribute(WEB_AUTHN_INFO);
		CredentialRecord ret = WebAuthnUtil.getCredentialRecord(webAuthnInfo);
		return ret;
	}

	/**
	 * WebAuthnによるログインを行います。
	 * @param p パラメータ。
	 * @return 認証結果。
	 * @throws Exception 例外。
	 */
	@WebMethod
	public Response passKeyAuth(final Map<String, Object> p) throws Exception {
		JsonResponse resp = null;
		List<ValidationError> elist = this.validate(p);
		if (elist.size() > 0) {
			resp = new JsonResponse(JsonResponse.INVALID, elist);
			logger.warn("passKeyAuth fail");
		} else {
			CredentialRecord credentialRecord = this.getCredentialRecord(); 
			ServerProperty serverProperty = this.getServerProperty();
			AuthenticationData authenticationData = WebAuthnUtil.checkAuthenticationData(p, credentialRecord, serverProperty);
			logger.debug("authenticationData=" + authenticationData.toString());
			// ここまで来たらPassKeyの確認OK
			UserDao dao = new UserDao(this);
			String loginId = (String) p.get("loginId");
			Map<String, Object> userInfo = dao.queryUserInfo(loginId);
			UserInfoTable.Entity ue = new UserInfoTable.Entity(userInfo);
			if ("1".equals(ue.getPasswordRequiredFlag())) {
				// パスワードチェックが必須の場合、POSTされたパスワードでもチェックする。
				String password = (String) p.get(UserInfoTable.Entity.ID_PASSWORD);
				Map<String, Object> loginInfo = new HashMap<String, Object>();
				loginInfo.put(UserInfoTable.Entity.ID_LOGIN_ID, loginId);
				loginInfo.put(UserInfoTable.Entity.ID_PASSWORD, password);
				logger.debug("loginId=" + loginId);
				userInfo = dao.login(loginInfo, true);
			} else {
				// パスワードチェックが不要の場合DB中のパスワードを使用して認証。
				String password = (String) dao.queryPassword(loginId);
				Map<String, Object> loginInfo = new HashMap<String, Object>();
				loginInfo.put(UserInfoTable.Entity.ID_LOGIN_ID, loginId);
				loginInfo.put(UserInfoTable.Entity.ID_PASSWORD, password);
				logger.debug("loginId=" + loginId);
				userInfo = dao.login(loginInfo, false);
			}
			HttpSession session = this.getPage().getRequest().getSession();
			session.setAttribute(WebEntryPoint.USER_INFO, userInfo);
			resp = new JsonResponse(JsonResponse.SUCCESS, "");
		}
		return resp;
	}
	
}

