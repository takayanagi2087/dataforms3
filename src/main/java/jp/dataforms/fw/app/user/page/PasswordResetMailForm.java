package jp.dataforms.fw.app.user.page;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.mail.Session;
import jakarta.servlet.http.HttpServletRequest;
import jp.dataforms.fw.app.user.dao.UserDao;
import jp.dataforms.fw.app.user.dao.UserInfoTable;
import jp.dataforms.fw.app.user.field.MailAddressField;
import jp.dataforms.fw.controller.EditForm;
import jp.dataforms.fw.controller.WebComponent;
import jp.dataforms.fw.mail.MailSender;
import jp.dataforms.fw.mail.MailTemplate;
import jp.dataforms.fw.servlet.DataFormsServlet;
import jp.dataforms.fw.util.CryptUtil;
import jp.dataforms.fw.util.JsonUtil;
import jp.dataforms.fw.util.MessagesUtil;
import jp.dataforms.fw.validator.RequiredValidator;

/**
 * パスワードリセットメール送信フォームクラス。
 *
 */
public class PasswordResetMailForm extends EditForm {

	/**
	 * Logger.
	 */
	private static Logger logger = LogManager.getLogger(PasswordResetMailForm.class);

	/**
	 * パスワードリセットページ。
	 */
	private static String passwordResetPage = null;

	/**
	 * コンストラクタ。
	 */
	public PasswordResetMailForm() {
		this.addField(new MailAddressField()).addValidator(new RequiredValidator()).setComment("メールアドレス");
	}

	/**
	 * パスワードリセットページを取得します。
	 * @return パスワードリセットページ。
	 */
	public static String getPasswordResetPage() {
		return passwordResetPage;
	}

	/**
	 * パスワードリセットページを設定します。
	 * @param passwordResetPage パスワードリセットページ。
	 */
	public static void setPasswordResetPage(final String passwordResetPage) {
		PasswordResetMailForm.passwordResetPage = passwordResetPage;
	}

	@Override
	protected Map<String, Object> queryData(final Map<String, Object> data) throws Exception {
		return new HashMap<String, Object>();
	}

	@Override
	protected boolean isUpdate(final Map<String, Object> data) throws Exception {
		return false;
	}

	@Override
	protected void insertData(final Map<String, Object> data) throws Exception {
		UserInfoTable.Entity e = new UserInfoTable.Entity(data);
		UserDao dao = new UserDao(this);
		List<Map<String, Object>> list = dao.queryUserListByMail(e.getMailAddress());
		logger.debug(() -> "userInfo=" + JsonUtil.encode(list, true));
		String path = this.getAppropriatePath("/mailTemplate/passwordResetMail.txt", this.getPage().getRequest());
		String text = this.getWebResource(path);
		logger.debug(() -> "template=" + text);

		HttpServletRequest req = this.getPage().getRequest();
		String url = req.getRequestURL().toString();
		String uri = req.getRequestURI();
		url = url.replaceAll(uri, req.getContextPath()) + PasswordResetMailForm.getPasswordResetPage() + "." + WebComponent.getServlet().getPageExt();
		MailTemplate template = new MailTemplate(text, null);
		String urllist = "";
		for (Map<String, Object> u: list) {
			UserInfoTable.Entity ue = new UserInfoTable.Entity(u);
			Map<String, Object> m = new HashMap<String, Object>();
			m.put(UserInfoTable.Entity.ID_USER_ID, ue.getUserId());
			m.put(UserInfoTable.Entity.ID_LOGIN_ID, ue.getLoginId());
			m.put(UserInfoTable.Entity.ID_USER_NAME, ue.getUserName());
			m.put(UserInfoTable.Entity.ID_MAIL_ADDRESS, ue.getMailAddress());
			String json = JsonUtil.encode(m);
			String key = CryptUtil.encrypt(json, DataFormsServlet.getQueryStringCryptPassword());
			String enckey = java.net.URLEncoder.encode(key, DataFormsServlet.getEncoding());
			logger.debug("url={}", url);
			urllist += (url + "?key=" + enckey + "\n") ;
		}
		template.setLink("passwordResetPage", urllist, urllist);

		template.addToAddress(e.getMailAddress());
		template.setFrom(MailSender.getMailFrom());
		template.setReplyTo(MailSender.getMailFrom());
//		template.setArg(UserInfoTable.Entity.ID_USER_NAME, e.getUserName());

		Session session = MailSender.getMailSession();
		MailSender sender = new MailSender();
		sender.send(template, session);

	}

	@Override
	protected String getSavedMessage(final Map<String, Object> data) {
		return MessagesUtil.getMessage(this.getPage(), "message.passwordmailsent");
	}

	@Override
	protected void updateData(final Map<String, Object> data) throws Exception {
	}

	@Override
	public void deleteData(final Map<String, Object> data) throws Exception {
	}

}
