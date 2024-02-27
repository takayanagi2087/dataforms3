package jp.dataforms.fw.app.user.page;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.servlet.http.HttpSession;
import jp.dataforms.fw.annotation.WebMethod;
import jp.dataforms.fw.app.base.page.BasePage;
import jp.dataforms.fw.app.user.dao.UserInfoTable;
import jp.dataforms.fw.response.Response;
import jp.dataforms.fw.servlet.DataFormsServlet;
import jp.dataforms.fw.util.CryptUtil;
import jp.dataforms.fw.util.NumberUtil;
import net.arnx.jsonic.JSON;

/**
 * 外部ユーザ登録ページ。
 *
 */
public class UserEnablePage extends BasePage {

	/**
	 * Logger.
	 */
	private static Logger log = LogManager.getLogger(UserEnablePage.class);

	/**
	 * コンストラクタ。
	 */
	public UserEnablePage() {
		this.addForm(new UserEnableForm());
		this.setMenuItem(false);
	}

	@Override
	@WebMethod
	public Response getHtml(final Map<String, Object> params) throws Exception {
		String key = (String) params.get("key");
		String json = CryptUtil.decrypt(key, DataFormsServlet.getQueryStringCryptPassword());
		@SuppressWarnings("unchecked")
		Map<String, Object> m = (Map<String, Object>) JSON.decode(json, HashMap.class);
		Long userId = NumberUtil.longValueObject(m.get(UserInfoTable.Entity.ID_USER_ID));
		String mailAddress = (String) m.get(UserInfoTable.Entity.ID_MAIL_ADDRESS);
		log.debug("userId=" + userId + ",mailAddress=" + mailAddress);
		HttpSession session = this.getPage().getRequest().getSession();
		session.setAttribute(UserInfoTable.Entity.ID_USER_ID, userId);
		session.setAttribute(UserInfoTable.Entity.ID_MAIL_ADDRESS, mailAddress);
		return super.getHtml(params);
	}
}
