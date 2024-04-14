package jp.dataforms.fw.app.user.page;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.mail.Session;
import jakarta.servlet.http.HttpServletRequest;
import jp.dataforms.fw.app.user.dao.UserAttributeTable;
import jp.dataforms.fw.app.user.dao.UserDao;
import jp.dataforms.fw.app.user.dao.UserInfoTable;
import jp.dataforms.fw.app.user.field.MailAddressField;
import jp.dataforms.fw.app.user.field.PasswordField;
import jp.dataforms.fw.controller.EditForm;
import jp.dataforms.fw.controller.WebComponent;
import jp.dataforms.fw.field.base.Field;
import jp.dataforms.fw.field.base.FieldList;
import jp.dataforms.fw.mail.MailSender;
import jp.dataforms.fw.mail.MailTemplate;
import jp.dataforms.fw.servlet.DataFormsServlet;
import jp.dataforms.fw.util.CryptUtil;
import jp.dataforms.fw.util.JsonUtil;
import jp.dataforms.fw.util.UserAdditionalInfoTableUtil;
import jp.dataforms.fw.util.UserInfoTableUtil;
import jp.dataforms.fw.validator.MailAddressValidator;
import jp.dataforms.fw.validator.RequiredValidator;
import jp.dataforms.fw.validator.ValidationError;

/**
 * 外部ユーザ登録フォーム。
 *
 */
public class UserRegistForm extends EditForm {
	/**
	 * Logger.
	 */
	private static Logger logger = LogManager.getLogger(UserRegistForm.class);


	/**
	 * ユーザ有効化ページ。
	 */
	private static String userEnablePage = null;

	/**
	 * ユーザ登録時のメール送信確認フラグ。
	 */
	private static Map<String, Object> config = null;

	/**
	 * コンストラクタ。
	 */
	public UserRegistForm() {
		UserInfoTable table = UserInfoTableUtil.newUserInfoTable(); //new UserInfoTable();
		Field<?> loginIdField = this.addField(table.getLoginIdField())
			.setRelationDataAcquisition(false).setAutocomplete(false);
		Boolean loginIdIsMail = (Boolean) config.get("loginIdIsMail");
		if (loginIdIsMail) {
			loginIdField.addValidator(new MailAddressValidator());
		} else {
			loginIdField.addValidator(new RequiredValidator());
		}
		this.addField(table.getUserNameField()).addValidator(new RequiredValidator()).setAutocomplete(false);
		this.addField(table.getMailAddressField()).addValidator(new RequiredValidator());
		Boolean mailCheck = (Boolean) config.get("mailCheck");
		if (mailCheck) {
			this.addField(new MailAddressField("mailAddressCheck")).addValidator(new RequiredValidator());
		} /*else {
			this.addField(new MailAddressField("mailAddressCheck")).addValidator(new RequiredValidator());
		}*/
		this.addField(table.getPasswordField()).addValidator(new RequiredValidator());
		this.addField(new PasswordField("passwordCheck")).addValidator(new RequiredValidator());
		// ユーザテーブルを拡張した場合のフィールド追加。
		UserInfoTable btable = new UserInfoTable();
		for (int i = btable.getFieldList().size(); i < table.getFieldList().size(); i++) {
			this.addField(table.getFieldList().get(i));
		}
		// ユーザ追加情報テーブルのフィールドを追加します。
		FieldList flist = UserAdditionalInfoTableUtil.getFieldList();
		if (flist != null) {
			this.addFieldList(flist);
		}

	}

	/**
	 * ユーザ有効化ページのパスを取得します。
	 * @return ユーザ有効化ページ。
	 */
	public static String getUserEnablePage() {
		return userEnablePage;
	}

	/**
	 * ユーザ有効化ページのパスを設定します。
	 * @param userEnablePage ユーザ有効化ページ。
	 */
	public static void setUserEnablePage(final String userEnablePage) {
		UserRegistForm.userEnablePage = userEnablePage;
	}


	/**
	 * 設定情報を取得します。
	 * @return 設定情報。
	 */
	public static Map<String, Object> getConfig() {
		return config;
	}

	/**
	 * 設定情報を設定します。
	 * @param config 設定情報。
	 */
	public static void setConfig(final Map<String, Object> config) {
		UserRegistForm.config = config;
	}

	@Override
	public Map<String, Object> getProperties() throws Exception {
		Map<String, Object> ret = super.getProperties();
		ret.put("userEnablePage", userEnablePage);
		ret.put("config", config);
		return ret;
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
	protected List<ValidationError> validateForm(final Map<String, Object> data) throws Exception {
		List<ValidationError> list = super.validateForm(data);
		if (list.size() == 0) {
			Boolean mailCheck = (Boolean) config.get("mailCheck");
			if (mailCheck) {
				String mailAddress = (String) data.get("mailAddress");
				String mailAddressCheck = (String) data.get("mailAddressCheck");
				if (!mailAddress.equals(mailAddressCheck)) {
					String msg = this.getPage().getMessage("error.mailaddressnotmatch");
					ValidationError err = new ValidationError("mailAddressCheck", msg);
					list.add(err);
				}
			}
			String password = (String) data.get("password");
			String passwordCheck = (String) data.get("passwordCheck");
			if (!password.equals(passwordCheck)) {
				String msg = this.getPage().getMessage("error.passwordnotmatch");
				ValidationError err = new ValidationError("passwordCheck", msg);
				list.add(err);
			}
			UserDao dao = new UserDao(this);
			if (dao.existLoginId(data, this.isUpdate(data))) {
				String msg = this.getPage().getMessage("error.duplicate");
				ValidationError err = new ValidationError(UserInfoTable.Entity.ID_LOGIN_ID, msg);
				list.add(err);
			}
		}
		return list;
	}

	/**
	 * 確認メール送信。
	 *
	 * @param data 登録データ。
	 * @throws Exception 例外。
	 */
	protected void sendMail(final Map<String, Object> data) throws Exception {
		String path = this.getAppropriatePath("/mailTemplate/userRegistMail.txt", this.getPage().getRequest());
		String text = this.getWebResource(path);
		logger.debug(() -> "mailTemplate=" + text);
		MailTemplate template = new MailTemplate(text, null);
		UserInfoTable.Entity e = new UserInfoTable.Entity(data);
		template.addToAddress(e.getMailAddress());
		template.setFrom(MailSender.getMailFrom());
		template.setReplyTo(MailSender.getMailFrom());
		template.setArg(UserInfoTable.Entity.ID_USER_NAME, e.getUserName());

		HttpServletRequest req = this.getPage().getRequest();
		String url = req.getRequestURL().toString();
		String uri = req.getRequestURI();
		url = url.replaceAll(uri, req.getContextPath()) + UserRegistForm.getUserEnablePage() +
				"." + WebComponent.getServlet().getPageExt();

		Map<String, Object> m = new HashMap<String, Object>();
		m.put(UserInfoTable.Entity.ID_USER_ID, data.get(UserInfoTable.Entity.ID_USER_ID));
//		m.put(UserInfoTable.Entity.ID_LOGIN_ID, data.get(UserInfoTable.Entity.ID_LOGIN_ID));
		m.put(UserInfoTable.Entity.ID_MAIL_ADDRESS, data.get(UserInfoTable.Entity.ID_MAIL_ADDRESS));
//		m.put(UserInfoTable.Entity.ID_USER_NAME, data.get(UserInfoTable.Entity.ID_USER_NAME));
		String json = JsonUtil.encode(m);
		String key = CryptUtil.encrypt(json, DataFormsServlet.getQueryStringCryptPassword());
		String enckey = java.net.URLEncoder.encode(key, DataFormsServlet.getEncoding());
		url += "?key=" + enckey;
		template.setLink("enableUserPage", url, url);
		Session session = MailSender.getMailSession();
		MailSender sender = new MailSender();
		sender.send(template, session);
	}


	@Override
	protected void insertData(final Map<String, Object> data) throws Exception {
		this.setUserInfo(data);
		data.put(UserInfoTable.Entity.ID_EXTERNAL_USER_FLAG, "1");
		data.put(UserInfoTable.Entity.ID_DELETE_FLAG, "0");
		Boolean sendUserEnableMail = (Boolean) config.get("sendUserEnableMail");
		if (sendUserEnableMail) {
			data.put(UserInfoTable.Entity.ID_ENABLED_FLAG, "0");
		} else {
			data.put(UserInfoTable.Entity.ID_ENABLED_FLAG, "1");
		}
		List<Map<String, Object>> attTable = new ArrayList<Map<String, Object>>();
		UserAttributeTable.Entity e = new UserAttributeTable.Entity();
		e.setUserAttributeType("userLevel");
		e.setUserAttributeValue("user");
		this.setUserInfo(e.getMap());
		attTable.add(e.getMap());
		data.put("attTable", attTable);
		UserDao dao = new UserDao(this);
		dao.insertUser(data);
		if (sendUserEnableMail) {
			this.sendMail(data);
		}
	}

	@Override
	protected void updateData(final Map<String, Object> data) throws Exception {
	}

	@Override
	public void deleteData(final Map<String, Object> data) throws Exception {
	}
}
