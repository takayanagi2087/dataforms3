package jp.dataforms.fw.app.user.page;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.webauthn4j.data.client.Origin;
import com.webauthn4j.data.client.challenge.Challenge;
import com.webauthn4j.data.client.challenge.DefaultChallenge;
import com.webauthn4j.server.ServerProperty;

import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import jakarta.servlet.http.HttpSession;
import jp.dataforms.fw.annotation.WebMethod;
import jp.dataforms.fw.app.user.dao.UserDao;
import jp.dataforms.fw.app.user.dao.UserInfoTable;
import jp.dataforms.fw.app.user.dao.WebAuthnDao;
import jp.dataforms.fw.app.user.dao.WebAuthnTable;
import jp.dataforms.fw.app.user.field.AuthenticatorNameField;
import jp.dataforms.fw.app.user.field.PasswordField;
import jp.dataforms.fw.app.user.field.PlatformField;
import jp.dataforms.fw.app.user.field.SharedPasskeyField;
import jp.dataforms.fw.app.user.field.WebAuthnIdField;
import jp.dataforms.fw.controller.Form;
import jp.dataforms.fw.field.base.FieldList;
import jp.dataforms.fw.field.common.FlagField;
import jp.dataforms.fw.field.common.RowNoField;
import jp.dataforms.fw.htmltable.HtmlTable;
import jp.dataforms.fw.response.JsonResponse;
import jp.dataforms.fw.response.Response;
import jp.dataforms.fw.util.WebAuthnUtil;
import jp.dataforms.fw.validator.RequiredValidator;

/**
 * 多要素認証設定フォームクラス。
 */
public class MfaForm extends Form {


	/**
	 * WebAutn登録時のオプションのセッションキー。
	 */
	private static final String WEB_AUTHN_CREATE_OPTION = "WebAuthnCreateOption";

	/**
	 * 認証器リストのID。
	 */
	private static final String ID_AUTHENTICATOR_LIST = "authenticatorList";

	/**
	 * クラウド共有フラグのID。
	 */
	private static final String ID_REQUIRE_RESIDENT_KEY = "requireResidentKey";

	/**
	 * logger.
	 */
	private static Logger logger = LogManager.getLogger(MfaForm.class);
	
	/**
	 * コンストラクタ。
	 */
	public MfaForm() {
		super(null);
		this.addField(new PasswordField()).addValidator(new RequiredValidator());
		this.addField(new AuthenticatorNameField()).addValidator(new RequiredValidator());
		this.addField(new FlagField(ID_REQUIRE_RESIDENT_KEY));
		FieldList flist = new FieldList();
		flist.addField(new RowNoField());
		flist.addField(new WebAuthnIdField());
		flist.addField(new AuthenticatorNameField());
		flist.addField(new PlatformField());
		flist.addField(new SharedPasskeyField());
//		flist.addField(new TextField("beFlag"));
//		flist.addField(new TextField("bsFlag"));
		HtmlTable authenticatorList = new HtmlTable(ID_AUTHENTICATOR_LIST, flist);
		this.addHtmlTable(authenticatorList);
	}
	
	
	@Override
	public void init() throws Exception {
		super.init();
		WebAuthnDao dao = new WebAuthnDao(this);
		List<Map<String, Object>> authenticatorList = dao.query(this.getPage().getUserId());
		this.setFormData(ID_AUTHENTICATOR_LIST, authenticatorList);
	}
	
	
	
	/**
	 * 生体情報の公開キー作成オプションを取得します。
	 * @param p パラメータ。
	 * @return 応答情報。
	 * @throws Exception 例外。
	 */
	@WebMethod
	public Response getOption(final Map<String, Object> p) throws Exception {
		Map<String, String> opt = new HashMap<String, String>();
		String challenge = WebAuthnUtil.generateChallenge();
		String origin = this.getOrigin();
		logger.debug("origin=" + origin);
		String serverName = this.getPage().getRequest().getServerName();
		opt.put("challenge", challenge);
		opt.put("origin", origin);
		UserInfoTable.Entity e = new UserInfoTable.Entity(this.getPage().getUserInfo());
		opt.put("name", e.getLoginId());
		opt.put("displayName", e.getUserName());
		opt.put("serverName", serverName);
		opt.put("rpName", "dataforms3");
		HttpSession session = this.getPage().getRequest().getSession();
		session.setAttribute(WEB_AUTHN_CREATE_OPTION, opt);
		Response resp = new JsonResponse(JsonResponse.SUCCESS, opt);
		return resp;
	}
	
	/**
	 * ServerPropertyを取得します。
	 * @return ServerProperty。
	 */
	private ServerProperty getServerProperty() {
		HttpSession session = this.getPage().getRequest().getSession();
		@SuppressWarnings("unchecked")
		Map<String, Object> opt= (Map<String, Object>) session.getAttribute(WEB_AUTHN_CREATE_OPTION);
		Origin origin = new Origin(this.getOrigin());
		String rpId = this.getPage().getRequest().getServerName();
		String ch = (String) opt.get("challenge");
		Challenge challenge = new DefaultChallenge(ch.getBytes());
		byte[] tokenBindingId = null;
		ServerProperty ret = new ServerProperty(origin, rpId, challenge, tokenBindingId);
		return ret;
	}
	
	/**
	 * 認証器の登録を行う。
	 * @param p パラメータ。
	 * @return 応答情報。
	 * @throws Exception 例外。
	 */
	@WebMethod
	public Response registAuthenticator(final Map<String, Object> p) throws Exception {
	    Long userId = this.getPage().getUserId();
		ServerProperty serverProperty = this.getServerProperty();
		Map<String, Object> regData = WebAuthnUtil.getRegistDataMap(p, serverProperty);
	    WebAuthnDao dao = new WebAuthnDao(this);
	    String an = (String) p.get(WebAuthnTable.Entity.ID_AUTHENTICATOR_NAME);
	    String platform = (String) p.get(WebAuthnTable.Entity.ID_PLATFORM);
	    List<Map<String, Object>> list = dao.query(userId, an);
	    WebAuthnTable.Entity e = new WebAuthnTable.Entity();
	    if (list.size() > 0) {
	    	e.setMap(list.get(0));
		    e.setUpdateUserId(userId);
	    } else {
		    e.setCreateUserId(userId);
		    e.setUpdateUserId(userId);
	    }
    	e.getMap().putAll(regData);
    	e.setAuthenticatorName(an);
    	e.setPlatform(platform);
    	e.setUserId(userId);
	    dao.regist(e.getMap());
	    list = dao.query(userId);
	    Response resp = new JsonResponse(JsonResponse.SUCCESS, list);
		return resp;
	}

	/**
	 * 認証器の削除を行います。
	 * @param p 認証器の名称を含むパラメータ。
	 * @return 削除結果。
	 * @throws Exception 例外。
	 */
	@WebMethod
	public Response deleteAuthenticator(final Map<String, Object> p) throws Exception {
	    Long userId = this.getPage().getUserId();
	    WebAuthnDao dao = new WebAuthnDao(this);
	    String an = (String) p.get(WebAuthnTable.Entity.ID_AUTHENTICATOR_NAME);
	    List<Map<String, Object>> list = dao.query(userId, an);
	    if (list.size() > 0) {
	    	// ヒットしても1件のはず。
	    	WebAuthnTable.Entity e = new WebAuthnTable.Entity(list.get(0));
	    	Long webAuthnId = e.getWebAuthnId();
	    	dao.delete(webAuthnId);
	    }
	    list = dao.query(userId);
	    Response resp = new JsonResponse(JsonResponse.SUCCESS, list);
	    return resp;
	}

	/**
	 * TOTP QRコードの剪定。
	 * @param p パラメータ。
	 * @return 応答情報。
	 * @throws Exception 例外。
	 */
	@WebMethod
	public Response generateTotpQr(final Map<String, Object> p) throws Exception {
		SecretGenerator secretGenerator = new DefaultSecretGenerator();
		String secret = secretGenerator.generate();
		logger.debug("TOPT secret=" + secret);
		UserDao udao = new UserDao(this);
		udao.updateTotpSecret(this.getPage().getUserId(), secret);
		
/*		QrData data = new QrData.Builder()
				   .label("developer@localhost")
				   .secret(secret)
				   .issuer("dataforms3app")
				   .algorithm(HashingAlgorithm.SHA1) // More on this below
				   .digits(6)
				   .period(30)
				   .build();
		logger.debug("qrdata=" + JsonUtil.encode(data));
*/		
/*		QrGenerator generator = new ZxingPngQrGenerator();
		byte[] imageData = generator.generate(data);
		String mimeType = generator.getImageMimeType();
*/		
		Response resp = new JsonResponse(JsonResponse.SUCCESS, "");
	    return resp;
	}

}
