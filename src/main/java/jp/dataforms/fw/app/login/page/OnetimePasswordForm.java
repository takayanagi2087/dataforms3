package jp.dataforms.fw.app.login.page;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.mail.Session;
import jakarta.servlet.http.HttpSession;
import jp.dataforms.fw.annotation.WebMethod;
import jp.dataforms.fw.app.user.dao.UserInfoTable;
import jp.dataforms.fw.controller.Form;
import jp.dataforms.fw.controller.WebEntryPoint;
import jp.dataforms.fw.field.base.TextField;
import jp.dataforms.fw.mail.MailSender;
import jp.dataforms.fw.mail.MailTemplate;
import jp.dataforms.fw.response.JsonResponse;
import jp.dataforms.fw.response.Response;
import jp.dataforms.fw.util.AutoLoginCookie;
import jp.dataforms.fw.util.MessagesUtil;
import jp.dataforms.fw.util.OnetimePasswordUtil;
import jp.dataforms.fw.util.UserLogUtil;
import jp.dataforms.fw.validator.RequiredValidator;
import jp.dataforms.fw.validator.ValidationError;

/**
 * ワンタイムパスワード確認フォーム。
 *
 */
public class OnetimePasswordForm extends Form {

	/**
	 * Logger.
	 */
	private static Logger logger = LogManager.getLogger(OnetimePasswordForm.class);

	/**
	 * コンストラクタ。
	 */
	public OnetimePasswordForm() {
		super(null);
		this.addField(new TextField("onetimePassword")).addValidator(new RequiredValidator());
	}

	/**
	 * ワンタイムパスワードメールを送信する。
	 * @param userInfo ユーザ情報。
	 * @return 生成したワンタイムパスワード。
	 */
	private String sendOnetimeMail(Map<String, Object> userInfo) throws Exception {
		String path = this.getAppropriatePath("/mailTemplate/onetimePassMail.txt", this.getPage().getRequest());
		String text = this.getWebResource(path);
		logger.debug(() -> "template=" + text);
		String onetime = OnetimePasswordUtil.generateOnetimePassword();
		logger.debug("onetime=" + onetime);
		UserInfoTable.Entity e = new UserInfoTable.Entity(userInfo);
		logger.debug("mailAddress=" + e.getMailAddress());
		MailTemplate template = new MailTemplate(text, null);
		template.setArg("onetimePassword", onetime);
		template.addToAddress(e.getMailAddress());
		template.setFrom(MailSender.getMailFrom());
		template.setReplyTo(MailSender.getMailFrom());
		Session session = MailSender.getMailSession();
		MailSender sender = new MailSender();
		sender.send(template, session);
		String ui = UserLogUtil.getClientInfo(getPage(), userInfo, "", "");
		logger.info(ui + "A one-time password has been sent.");
		return onetime;
	}

	@Override
	public void init() throws Exception {
		super.init();
		HttpSession session = this.getPage().getRequest().getSession();
		@SuppressWarnings("unchecked")
		Map<String, Object> userInfo = (Map<String, Object>) session.getAttribute(OnetimePasswordUtil.USERINFO);
		String onetime = this.sendOnetimeMail(userInfo);
		session.setAttribute(OnetimePasswordUtil.ONETIME, onetime);
	}

	/**
	 * ワンタイムパスワードの再尊信。
	 * @param p パラメータ。
	 * @return 応答情報。
	 * @throws Exception 例外。
	 */
	@WebMethod
	public Response sendOnetimePassword(final Map<String, Object> p) throws Exception {
		HttpSession session = this.getPage().getRequest().getSession();
		@SuppressWarnings("unchecked")
		Map<String, Object> userInfo = (Map<String, Object>) session.getAttribute(OnetimePasswordUtil.USERINFO);
		String onetime = this.sendOnetimeMail(userInfo);
		session.setAttribute(OnetimePasswordUtil.ONETIME, onetime);
		Response r = new JsonResponse(JsonResponse.SUCCESS, MessagesUtil.getMessage(getWebEntryPoint(), "message.sent"));
		return r;
	}

	/**
	 * ワンタイムパスワードの確認を行います。
	 * @param p パラメータ。
	 * @return 応答情報。
	 * @throws Exception 例外。
	 */
	@WebMethod
	public Response login(final Map<String, Object> p) throws Exception {
		String onetime = (String) p.get("onetimePassword");
		HttpSession session = this.getPage().getRequest().getSession();
		String pass = (String) session.getAttribute(OnetimePasswordUtil.ONETIME);
		if (onetime.equals(pass)) {
			@SuppressWarnings("unchecked")
			Map<String, Object> userInfo = (Map<String, Object>) session.getAttribute(OnetimePasswordUtil.USERINFO);
			session.setAttribute(WebEntryPoint.USER_INFO, userInfo);
			session.removeAttribute(OnetimePasswordUtil.USERINFO);
			OnetimePasswordUtil.setSkipOnetimeCookie(getPage(), userInfo);
			AutoLoginCookie.setAutoLoginCookie(this.getPage(), userInfo);
			String ui = UserLogUtil.getClientInfo(getPage(), userInfo, "", "");
			logger.info(ui + "Authenticated with one-time password.");
			Response r = new JsonResponse(JsonResponse.SUCCESS, "");
			return r;
		} else {
			@SuppressWarnings("unchecked")
			Map<String, Object> userInfo = (Map<String, Object>) session.getAttribute(OnetimePasswordUtil.USERINFO);
			String ui = UserLogUtil.getClientInfo(getPage(), userInfo, "", "");
			logger.info(ui + "One-time passwords do not match.");
			List<ValidationError> list = new ArrayList<ValidationError>();
			list.add(new ValidationError(
				"onetimePassword"
				,MessagesUtil.getMessage(getWebEntryPoint(), "error.ontimepasswordnotmatch")
			));
			Response r = new JsonResponse(JsonResponse.INVALID, list);
			return r;
		}
	}

}
