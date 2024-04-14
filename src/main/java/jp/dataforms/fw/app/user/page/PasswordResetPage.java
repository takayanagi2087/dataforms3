package jp.dataforms.fw.app.user.page;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jp.dataforms.fw.annotation.WebMethod;
import jp.dataforms.fw.app.base.page.BasePage;
import jp.dataforms.fw.exception.AuthoricationException;
import jp.dataforms.fw.response.Response;
import jp.dataforms.fw.servlet.DataFormsServlet;
import jp.dataforms.fw.util.CryptUtil;
import jp.dataforms.fw.util.JsonUtil;

/**
 * パスワードリセットページ。
 *
 */
public class PasswordResetPage extends BasePage {
	/**
	 * パスワードリセット情報のキー。
	 */
	public static final String PASSWORD_RESET_INFO = "passwordResetInfo";

	/**
	 * Logger.
	 */
	private static Logger logger = LogManager.getLogger(PasswordResetPage.class);

	/**
	 * コンストラクタ。
	 */
	public PasswordResetPage() {
		this.addForm(new ChangePasswordForm(true));
		this.setMenuItem(false);
	}


	@WebMethod
	@Override
	public Response getHtml(final Map<String, Object> p) throws Exception {
		String key = (String) p.get("key");
		if (key != null) {
			try {
				logger.debug(() -> "key=" + key);
				String json = CryptUtil.decrypt(key, DataFormsServlet.getQueryStringCryptPassword());
				logger.debug(() -> "json=" + json);
				@SuppressWarnings("unchecked")
				Map<String, Object> userInfo = (Map<String, Object>) JsonUtil.decode(json, HashMap.class);
				logger.debug(() -> "userInfo=" + userInfo);
				this.getPage().getRequest().getSession().setAttribute(PASSWORD_RESET_INFO, userInfo);
			} catch (java.lang.IllegalArgumentException e) {
				throw new AuthoricationException(this.getWebEntryPoint());
			}
		} else {
			throw new AuthoricationException(this.getWebEntryPoint());
		}
		return super.getHtml(p);
	}
}
