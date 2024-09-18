package jp.dataforms.fw.app.login.page;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.webauthn4j.converter.exception.DataConversionException;
import com.webauthn4j.credential.CredentialRecord;
import com.webauthn4j.data.AuthenticationData;
import com.webauthn4j.data.client.Origin;
import com.webauthn4j.data.client.challenge.Challenge;
import com.webauthn4j.data.client.challenge.DefaultChallenge;
import com.webauthn4j.server.ServerProperty;

import dev.samstevens.totp.code.CodeGenerator;
import dev.samstevens.totp.code.CodeVerifier;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.DefaultCodeVerifier;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.time.TimeProvider;
import jakarta.servlet.http.HttpSession;
import jp.dataforms.fw.annotation.WebMethod;
import jp.dataforms.fw.app.login.field.AuthMethodSelectField;
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
import jp.dataforms.fw.field.base.TextField;
import jp.dataforms.fw.field.common.FlagField;
import jp.dataforms.fw.response.JsonResponse;
import jp.dataforms.fw.response.Response;
import jp.dataforms.fw.util.AutoLoginCookie;
import jp.dataforms.fw.util.MessagesUtil;
import jp.dataforms.fw.util.OnetimePasswordUtil;
import jp.dataforms.fw.util.StringUtil;
import jp.dataforms.fw.util.UserLogUtil;
import jp.dataforms.fw.util.WebAuthnUtil;
import jp.dataforms.fw.validator.DisplayedRequiredValidator;
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
	 * 認証情報。
	 */
	private static final String ID_AUTH_METHOD = "authMethod";

	/**
	 * WebAuthn情報用セッションキー。
	 */
	private static final String WEB_AUTHN_INFO = "webAuthnInfo";

	/**
	 * TOTPフィールドID。
	 */
	private static final String ID_TOTP = "totp";
	
	/**
	 * リカバリーコードの入力フィールド。
	 */
	private static final String ID_RECOVERY_CODE = "recoveryCode";

	/**
	 * リカバリーコード使用するチェックボックスのフィールドID。
	 */
	private static final String ID_RECOVERY_CODE_CHECK = "recoveryCodeCheck";

	/**
	 * 最後最終ログイン情報を保存フラグのID。
	 */
	private static final String ID_SAVE_LAST_LOGIN = "saveLastLogin";
	
	/**
	 * ユーザ登録ページのアドレス。
	 */
	private static String passwordResetMailPage = null;

	/**
	 * 多要素認証必須に切り替える回数。
	 */
	private static int mfaRequiredCount = 0;

	/**
	 * 多要素認証必須に切り替える回数を設定します。
	 * @param mfaRequiredCount 多要素認証必須に切り替える回数。
	 */
	public static void setMfaRequiredCount(int mfaRequiredCount) {
		LoginForm.mfaRequiredCount = mfaRequiredCount;
	}

	/**
	 * コンストラクタ。
	 */
	public LoginForm() {
		super("loginForm");
		this.addField(new LoginIdField()).addValidator(new RequiredValidator());

		
		this.addField(new AuthMethodSelectField(ID_AUTH_METHOD));
		PasswordField pw = new PasswordField();
		this.addField(pw);
		this.addField(new TextField(ID_TOTP)).addValidator(new DisplayedRequiredValidator());
		this.addField(new TextField(ID_RECOVERY_CODE)).addValidator(new DisplayedRequiredValidator());
		this.addField(new PasskeySingleSelectField(WebAuthnTable.Entity.ID_AUTHENTICATOR_NAME)).addValidator(new DisplayedRequiredValidator());
		this.addField(new FlagField(ID_RECOVERY_CODE_CHECK));
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
			String totp = (String) data.get(ID_TOTP);
			String recoveryCode = (String) data.get(ID_RECOVERY_CODE);
			String method = (String) data.get(ID_AUTH_METHOD);
			if (elist.size() == 0) {
				// ユーザ情報を取得。
				UserDao udao = new UserDao(this);
				Map<String, Object> userInfo = udao.queryUserInfo(loginId);
				if (userInfo != null) {
					UserInfoTable.Entity ue = new UserInfoTable.Entity(userInfo);
					if ("0".equals(method)) {
						// パスワードのみの認証
						if (this.isMfaEnabled(ue)) {
							if ("1".equals(ue.getMfaRequiredFlag())) {
								// 多要素認証が有効でかつ多要素認証が必須に設定された場合、パスワードのみの認証は許可されない。
								throw new ApplicationException(this.getPage(), "error.invalidauth");
							}
						}
						// パスワードを必須
						if (StringUtil.isBlank(password)) {
							elist.add(new ValidationError(UserInfoTable.Entity.ID_PASSWORD, MessagesUtil.getMessage(getWebEntryPoint(), "error.required")));
						}
					} else if ("1".equals(method)) {
						// パスワード + TOTPの認証
						if (StringUtil.isBlank(ue.getTotpSecret())) {
							throw new ApplicationException(this.getPage(), "error.invalidauth");
						}
						// パスワードを必須
						if (StringUtil.isBlank(password)) {
							elist.add(new ValidationError(UserInfoTable.Entity.ID_PASSWORD, MessagesUtil.getMessage(getWebEntryPoint(), "error.required")));
						}
						// TOTP必須
						if (StringUtil.isBlank(totp)) {
							elist.add(new ValidationError(ID_TOTP, MessagesUtil.getMessage(getWebEntryPoint(), "error.required")));
						}
					} else if ("2".equals(method)) {
						// パスキーの認証
						// PassKeyの情報を取得。
						WebAuthnDao pdao = new WebAuthnDao(this);
						List<Map<String, Object>> plist = pdao.query(loginId);
						if (plist.size() == 0) {
							throw new ApplicationException(this.getPage(), "error.invalidauth");
						}
						if (StringUtil.isBlank(passkey)) {
							logger.debug("passkey=" + passkey);
							elist.add(new ValidationError(WebAuthnTable.Entity.ID_AUTHENTICATOR_NAME, MessagesUtil.getMessage(getWebEntryPoint(), "error.required")));
						}
					} else if ("3".equals(method)) {
						// リカバリーコート認証モード
						List<Map<String, Object>> rclist = udao.queryRecoveryCode(ue.getUserId());
						if (rclist.size() == 0) {
							throw new ApplicationException(this.getPage(), "error.invalidauth");
						}
						if (StringUtil.isBlank(recoveryCode)) {
							elist.add(new ValidationError(ID_RECOVERY_CODE, MessagesUtil.getMessage(getWebEntryPoint(), "error.required")));
						}
					}
				} else {
					String ui = UserLogUtil.getClientInfo(this.getPage(), data, "", "");
					ApplicationException ex = new ApplicationException(this.getWebEntryPoint(), "error.invaliduserid");
					logger.error(ui + ex.getMessage(), ex);
					throw ex;
				}
			}
		}
		if (elist.size() > 0) {
			String ui = UserLogUtil.getClientInfo(this.getPage(), data, "", "");
			for (ValidationError e: elist) {
				logger.warn(ui + "ValidationError " + e.getFieldId() + " / " + e.getMessage());
			}
		}
		return elist;
	}
	
	/**
	 * TOTPの確認を行います。
	 * @param secret TOTP secret。
	 * @param totp 入力されたTOTP
	 * @return チェックOKであれはtrue。
	 * @throws Exception 例外。
	 */
	private Boolean checkTotp(final String secret, final String totp) throws Exception {
		logger.debug("totp=" + totp);
		TimeProvider timeProvider = new SystemTimeProvider();
		CodeGenerator codeGenerator = new DefaultCodeGenerator();
		CodeVerifier verifier = new DefaultCodeVerifier(codeGenerator, timeProvider);
		Boolean ret = verifier.isValidCode(secret, totp);
		logger.debug("totp check=" + ret);
		return ret;
	}
	
	/**
	 * 多要素認証をONに設定する。
	 * @param userInfo ユーザ情報。
	 * @throws Exception 例外。
	 */
	private void setMfaRequired(final Map<String, Object> userInfo) throws Exception {
		UserInfoTable.Entity ue = new UserInfoTable.Entity(userInfo);
		int c = LoginForm.mfaRequiredCount;
		Integer mfaSuccess = ue.getMfaLoginCount();
		if (mfaSuccess == null) {
			mfaSuccess = 0;
		}
		mfaSuccess++;
		if (c > 0) {
			// 自動的に多要素認証をONに設定する。
			if (mfaSuccess >= c) {
				ue.setMfaRequiredFlag("1");
			}
		} else {
			// 自動的に多要素認証はONにしない。
		}
		ue.setMfaLoginCount(mfaSuccess);
		ue.setUpdateUserId(ue.getUserId());
		UserDao dao = new UserDao(this);
		dao.updateMfaInfo(userInfo);
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
		} else {
			UserDao dao = new UserDao(this);
			Map<String, Object> userInfo = dao.login(params); // パスワードの認証
			if (OnetimePasswordUtil.needConfirmation(this.getPage(), userInfo)) {
				// 旧 ワンタイムパスワード確認モード(Not TOTP)。
				String keepLogin = (String) params.get(AutoLoginCookie.ID_KEEP_LOGIN);
				userInfo.put(AutoLoginCookie.ID_KEEP_LOGIN, keepLogin);
				HttpSession session = this.getPage().getRequest().getSession();
				session.setAttribute(OnetimePasswordUtil.USERINFO, userInfo);
				ret = new JsonResponse(JsonResponse.SUCCESS, "onetime");
				String ui = UserLogUtil.getClientInfo(getPage(), userInfo, "OTP", "******");
				logger.info(ui + "Authenticated with password.");
			} else {
				// TOTPのチェック
				HttpSession session = this.getPage().getRequest().getSession();
				logger.info(() -> "login success=" + userInfo.get("loginId") + "(" + userInfo.get("userId") + ")");
				UserInfoTable.Entity e = new UserInfoTable.Entity(userInfo);
				String authMethod = (String) params.get(ID_AUTH_METHOD);
				if ("1".equals(authMethod)) {
					if (this.checkTotp(e.getTotpSecret(), (String) params.get(ID_TOTP))) {
						this.setMfaRequired(userInfo);	// 多要素認証必須設定
						// TOTPのチェックOKとなりユーザ情報をセッションに保存
						AutoLoginCookie.setAutoLoginCookie(this.getPage(), params);
						session.setAttribute(WebEntryPoint.USER_INFO, userInfo);
						ret = new JsonResponse(JsonResponse.SUCCESS, "");
						String ui = UserLogUtil.getClientInfo(getPage(), userInfo, "TOTP", "******");
						logger.info(ui + "Authenticated with password + TOTP.");
					} else {
						elist.add(new ValidationError(ID_TOTP, MessagesUtil.getMessage(getWebEntryPoint(), "error.badonetimepassword")));
						ret = new JsonResponse(JsonResponse.INVALID, elist);
					}
				} else if ("3".equals(authMethod)) {
					if (dao.checkRecoveryCode(e.getUserId(), (String) params.get(ID_RECOVERY_CODE))) {
						// リカバリーコードチェックOkとなりユーザ情報をセッションに保存
						AutoLoginCookie.setAutoLoginCookie(this.getPage(), params);
						session.setAttribute(WebEntryPoint.USER_INFO, userInfo);
						ret = new JsonResponse(JsonResponse.SUCCESS, "");
						String ui = UserLogUtil.getClientInfo(getPage(), userInfo, "Recovery code", "");
						logger.info(ui + "Authenticated with password + recovery code");
					} else {
						elist.add(new ValidationError(ID_RECOVERY_CODE, MessagesUtil.getMessage(getWebEntryPoint(), "error.badrecoverycode")));
						ret = new JsonResponse(JsonResponse.INVALID, elist);
					}
				} else {
					// パスワードのみの認証OKとなりユーザ情報をセッションに保存
					String ui = UserLogUtil.getClientInfo(getPage(), userInfo, "Password", "******");
					logger.info(ui + "Authenticated with password only.");
					AutoLoginCookie.setAutoLoginCookie(this.getPage(), params);
					session.setAttribute(WebEntryPoint.USER_INFO, userInfo);
					ret = new JsonResponse(JsonResponse.SUCCESS, "");
				}
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
	 * PassKeyの名称を取得します。
	 * @return PassKeyの名称。
	 */
	private String getAuthenticatorName() {
		HttpSession session = this.getPage().getRequest().getSession();
		@SuppressWarnings("unchecked")
		Map<String, Object> webAuthnInfo = (Map<String, Object>) session.getAttribute(WEB_AUTHN_INFO);
		WebAuthnTable.Entity e = new WebAuthnTable.Entity(webAuthnInfo);
		return e.getAuthenticatorName();
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
		} else {
			CredentialRecord credentialRecord = this.getCredentialRecord(); 
			ServerProperty serverProperty = this.getServerProperty();
			try {
				AuthenticationData authenticationData = WebAuthnUtil.checkAuthenticationData(p, credentialRecord, serverProperty);
				logger.debug("authenticationData=" + authenticationData.toString());
				HttpSession session = this.getPage().getRequest().getSession();
				@SuppressWarnings("unchecked")
				Map<String, Object> webauthnRec = (Map<String, Object>) session.getAttribute(WEB_AUTHN_INFO);
				WebAuthnTable.Entity e = new WebAuthnTable.Entity(webauthnRec);
				logger.debug("webauthnRec=" + webauthnRec);
				WebAuthnDao dao = new WebAuthnDao(this);
				dao.updateSignCount(e.getWebAuthnId(), authenticationData.getAuthenticatorData().getSignCount());
			} catch (DataConversionException e) {
				throw new ApplicationException(getPage(), "error.badpasskey");
			}

			// ここまで来たらPassKeyの確認OK
			UserDao dao = new UserDao(this);
//			String loginId = (String) p.get("loginId");
			String loginId = (String) p.get(UserInfoTable.Entity.ID_LOGIN_ID);
			Map<String, Object> userInfo = dao.queryUserInfo(loginId);
//			UserInfoTable.Entity ue = new UserInfoTable.Entity(userInfo);
			String logmsg = null;
			// パスワードチェックが不要の場合DB中のパスワードを使用して認証。
			String password = (String) dao.queryPassword(loginId);
			Map<String, Object> loginInfo = new HashMap<String, Object>();
			loginInfo.put(UserInfoTable.Entity.ID_LOGIN_ID, loginId);
			loginInfo.put(UserInfoTable.Entity.ID_PASSWORD, password);
			logger.debug("loginId=" + loginId);
			userInfo = dao.login(loginInfo, false);
			logmsg = "Authenticated with passkey.";
			String passkey = this.getAuthenticatorName();
			userInfo.put(WebAuthnTable.Entity.ID_AUTHENTICATOR_NAME, passkey);
			userInfo.put(AutoLoginCookie.ID_KEEP_LOGIN, p.get(AutoLoginCookie.ID_KEEP_LOGIN));
			String ui = UserLogUtil.getClientInfo(getPage(), userInfo, "Passkey", passkey);
			logger.info(ui + logmsg);
			this.setMfaRequired(userInfo);	// 多要素認証必須設定
			// セッションにユーザ情報を保存する。
			HttpSession session = this.getPage().getRequest().getSession();
			session.setAttribute(WebEntryPoint.USER_INFO, userInfo);
			AutoLoginCookie.setAutoLoginCookie(this.getPage(), userInfo);
			resp = new JsonResponse(JsonResponse.SUCCESS, "");
		}
		return resp;
	}

	/**
	 * 多要素認証が有効かどうかをチェックします。
	 * @param ui ユーザ情報。
	 * @return 多要素認証が有効な場合true。
	 * @throws Exception 例外。
	 */
	private Boolean isMfaEnabled(UserInfoTable.Entity ui) throws Exception {
		String secret = ui.getTotpSecret();
		WebAuthnDao wdao = new WebAuthnDao(this);
		Long userId = ui.getUserId();
		List<Map<String, Object>> list = wdao.query(userId);
		if (secret != null || list.size() > 0) {
			return Boolean.TRUE;
		} else {
			return Boolean.FALSE;
		}
	}
	
	/**
	 * 使用できる認証オプションを取得します。
	 * @param p パラメータ。
	 * @return 応答情報。
	 * @throws Exception 例外。
	 */
	@WebMethod
	public Response getAuthOption(final Map<String, Object> p) throws Exception {
		String loginId = (String) p.get(UserInfoTable.Entity.ID_LOGIN_ID);
		Map<String, Object> opt = new HashMap<String, Object>();
		UserDao dao = new UserDao(this);
		Map<String, Object> userInfo = dao.queryUserInfo(loginId);
		if (userInfo != null) {
			UserInfoTable.Entity ui = new UserInfoTable.Entity(userInfo);
			if (StringUtil.isBlank(ui.getTotpSecret())) {
				opt.put("useTotp", Boolean.FALSE);
			} else {
				opt.put("useTotp", Boolean.TRUE);
			}
			String mfaRequired = ui.getMfaRequiredFlag();
			if ("1".equals(mfaRequired)) {
				opt.put("mfaRequired", this.isMfaEnabled(ui));
			} else {
				opt.put("mfaRequired", Boolean.FALSE);
			}
			List<Map<String, Object>> list = dao.queryRecoveryCode(ui.getUserId());
			if (list.size() > 0) {
				opt.put("recoveryCode", Boolean.TRUE);
			} else {
				opt.put("recoveryCode", Boolean.FALSE);
			}
		}
		Response resp = new JsonResponse(JsonResponse.SUCCESS, opt);
		return resp;

	}

}

