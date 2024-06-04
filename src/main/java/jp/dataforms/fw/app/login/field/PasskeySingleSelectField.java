package jp.dataforms.fw.app.login.field;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jp.dataforms.fw.annotation.WebMethod;
import jp.dataforms.fw.app.user.dao.UserInfoTable;
import jp.dataforms.fw.app.user.dao.WebAuthnDao;
import jp.dataforms.fw.app.user.dao.WebAuthnTable;
import jp.dataforms.fw.field.common.SelectField;
import jp.dataforms.fw.field.common.SingleSelectField;
import jp.dataforms.fw.response.JsonResponse;
import jp.dataforms.fw.response.Response;
import jp.dataforms.fw.util.MessagesUtil;
import jp.dataforms.fw.util.WebAuthnUtil;

/**
 * パスキーの名称選択フィールド。
 */
public class PasskeySingleSelectField extends SingleSelectField<String> {

	/**
	 * Logger.
	 */
	private static Logger logger = LogManager.getLogger(PasskeySingleSelectField.class);
	
	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public PasskeySingleSelectField(final String id) {
		super(id);
		this.setBlankOption(true);
	}
	
	/**
	 * アカウントに登録されているパスキーのリストを取得します。
	 * @param p Postされたパラメータ。
	 * @return パスキーのリスト。
	 * @throws Exception 例外。
	 */
	@WebMethod
	public Response getPasskeyList(final Map<String, Object> p) throws Exception {
		String loginId = (String) p.get(UserInfoTable.Entity.ID_LOGIN_ID);
		logger.debug("loginId=" + loginId);
		WebAuthnDao dao = new WebAuthnDao(this);
		List<Map<String, Object>> list = dao.query(loginId);
		List<Map<String, Object>> olist = new ArrayList<Map<String, Object>>();
		String croudShared = MessagesUtil.getMessage(getWebEntryPoint(), "messge.croudshared");
		for (Map<String, Object> m: list) {
			WebAuthnTable.Entity e = new WebAuthnTable.Entity(m);
			SelectField.OptionEntity oe = new SelectField.OptionEntity();
			oe.setValue(e.getAuthenticatorName());
			Boolean shared = WebAuthnUtil.shared(m);
			oe.setName(e.getAuthenticatorName() + " ( platform:" + e.getPlatform() + " " + (shared ? " / " + croudShared: "") + " )");
			olist.add(oe.getMap());
		}
		Response r = new JsonResponse(JsonResponse.SUCCESS, olist);
		return r;
	}
}
