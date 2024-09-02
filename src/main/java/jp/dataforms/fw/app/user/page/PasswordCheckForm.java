package jp.dataforms.fw.app.user.page;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jp.dataforms.fw.annotation.WebMethod;
import jp.dataforms.fw.app.user.dao.UserInfoTable;
import jp.dataforms.fw.app.user.field.PasswordField;
import jp.dataforms.fw.controller.Form;
import jp.dataforms.fw.response.JsonResponse;
import jp.dataforms.fw.response.Response;
import jp.dataforms.fw.util.CryptUtil;
import jp.dataforms.fw.util.JsonUtil;
import jp.dataforms.fw.util.MessagesUtil;
import jp.dataforms.fw.validator.RequiredValidator;
import jp.dataforms.fw.validator.ValidationError;

/**
 * パスワードチェックフォーム。
 */
public class PasswordCheckForm extends Form {
	/**
	 * logger.
	 */
	private static Logger logger = LogManager.getLogger(PasswordCheckForm.class);
	
	/**
	 * チェックOKの場合に実行するスクリプト。
	 */
	private String onOkScript = null;
	
	/**
	 * コンストラクタ。
	 * @param onOkScript チェックOKの場合に実行するスクリプト。
	 */
	public PasswordCheckForm(final String onOkScript) {
		this(null, onOkScript);
	}
	
	/**
	 * コンストラクタ。
	 * @param id フォームID。	 
	 * @param onOkScript チェックOKの場合に実行するスクリプト。
	 */
	public PasswordCheckForm(final String id, final String onOkScript) {
		super(id);
		this.onOkScript = onOkScript;
		this.addField(new PasswordField()).addValidator(new RequiredValidator());
	}
	
	/**
	 * 多要素認証設定の前にユーザのパスワードを確認します。
	 * @param p パラメータ。
	 * @return 応答情報。
	 * @throws Exception 例外。
	 */
	@WebMethod
	public Response checkPassword(final Map<String, Object> p) throws Exception {
		String password = (String) p.get(UserInfoTable.Entity.ID_PASSWORD);
		String encpass = CryptUtil.encryptUserPassword(password);
		Map<String, Object> u = this.getPage().getUserInfo();
		String uencpass = (String) u.get(UserInfoTable.Entity.ID_PASSWORD);
		if (encpass.equals(uencpass)) {
			logger.debug("userInfo=" + JsonUtil.encode(u));
			Response resp = new JsonResponse(JsonResponse.SUCCESS, "");
			return resp;
		} else {
			ValidationError err = new ValidationError(UserInfoTable.Entity.ID_PASSWORD, MessagesUtil.getMessage(this.getWebEntryPoint(), "error.badpassword"));
			List<ValidationError> elist = new ArrayList<ValidationError>();
			elist.add(err);
			Response resp = new JsonResponse(JsonResponse.INVALID, elist);
			return resp;
		}
	}
	
	@Override
	public Map<String, Object> getProperties() throws Exception {
		Map<String, Object> prop = super.getProperties();
		prop.put("onOkScript", this.onOkScript);
		return prop;
	}
}
