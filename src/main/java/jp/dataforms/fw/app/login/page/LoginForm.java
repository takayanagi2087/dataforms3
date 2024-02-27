package jp.dataforms.fw.app.login.page;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.servlet.http.HttpSession;
import jp.dataforms.fw.annotation.WebMethod;
import jp.dataforms.fw.app.user.dao.UserDao;
import jp.dataforms.fw.app.user.field.LoginIdField;
import jp.dataforms.fw.app.user.field.PasswordField;
import jp.dataforms.fw.controller.Form;
import jp.dataforms.fw.controller.WebEntryPoint;
import jp.dataforms.fw.field.common.FlagField;
import jp.dataforms.fw.response.JsonResponse;
import jp.dataforms.fw.util.AutoLoginCookie;
import jp.dataforms.fw.util.OnetimePasswordUtil;
import jp.dataforms.fw.util.StringUtil;
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
		this.addField(pw).addValidator(new RequiredValidator());
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

}

