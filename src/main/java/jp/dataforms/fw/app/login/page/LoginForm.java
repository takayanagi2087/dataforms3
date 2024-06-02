package jp.dataforms.fw.app.login.page;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.webauthn4j.converter.AttestationObjectConverter;
import com.webauthn4j.converter.CollectedClientDataConverter;
import com.webauthn4j.converter.util.ObjectConverter;
import com.webauthn4j.credential.CredentialRecord;
import com.webauthn4j.credential.CredentialRecordImpl;
import com.webauthn4j.data.AuthenticationData;
import com.webauthn4j.data.attestation.AttestationObject;
import com.webauthn4j.data.client.CollectedClientData;
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
import jp.dataforms.fw.field.common.FlagField;
import jp.dataforms.fw.response.JsonResponse;
import jp.dataforms.fw.response.Response;
import jp.dataforms.fw.util.AutoLoginCookie;
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

	/**
	 * ログインの処理を行います。
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
	 * 認証時のオプションを取得します。
	 * @param p パラメータ。
	 * @return 認証時のオプション。
	 * @throws Exception 例外。
	 */
	@WebMethod
	public Response getOption(final Map<String, Object> p) throws Exception {
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
		Response resp = new JsonResponse(JsonResponse.SUCCESS, ret);
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
		CredentialRecord ret = null;
		HttpSession session = this.getPage().getRequest().getSession();
		@SuppressWarnings("unchecked")
		Map<String, Object> webAuthnInfo = (Map<String, Object>) session.getAttribute(WEB_AUTHN_INFO);
		WebAuthnTable.Entity e = new WebAuthnTable.Entity(webAuthnInfo);
		//
		String attestationObjectBase64 = e.getAttestationObject();
		AttestationObjectConverter attestationObjectConverter = new AttestationObjectConverter(new ObjectConverter());
		AttestationObject ao = attestationObjectConverter.convert(attestationObjectBase64);
		//
		String collectedClientDataBase64 = e.getCollectedClientData();
		CollectedClientDataConverter collectedClientDataConverter = new CollectedClientDataConverter(new ObjectConverter());
		CollectedClientData cd = collectedClientDataConverter.convert(collectedClientDataBase64);
		ret = new CredentialRecordImpl(ao, cd, null, null);
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
		CredentialRecord credentialRecord = this.getCredentialRecord(); 
		ServerProperty serverProperty = this.getServerProperty();
		AuthenticationData authenticationData = WebAuthnUtil.checkAuthenticationData(p, credentialRecord, serverProperty);
		logger.debug("authenticationData=" + authenticationData.toString());
		
		UserDao dao = new UserDao(this);
		String loginId = (String) p.get("loginId");
		String password = (String) dao.queryPassword(loginId);
		Map<String, Object> loginInfo = new HashMap<String, Object>();
		loginInfo.put(UserInfoTable.Entity.ID_LOGIN_ID, loginId);
		loginInfo.put(UserInfoTable.Entity.ID_PASSWORD, password);
		logger.debug("loginId=" + loginId);
		Map<String, Object> userInfo = dao.login(loginInfo, false);
		HttpSession session = this.getPage().getRequest().getSession();
		session.setAttribute(WebEntryPoint.USER_INFO, userInfo);
		Response resp = new JsonResponse(JsonResponse.SUCCESS, "");
		return resp;
	}
	
}

