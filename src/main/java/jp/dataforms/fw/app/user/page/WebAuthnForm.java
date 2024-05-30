package jp.dataforms.fw.app.user.page;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.webauthn4j.WebAuthnManager;
import com.webauthn4j.converter.AttestationObjectConverter;
import com.webauthn4j.converter.CollectedClientDataConverter;
import com.webauthn4j.converter.util.ObjectConverter;
import com.webauthn4j.data.RegistrationData;
import com.webauthn4j.data.RegistrationParameters;
import com.webauthn4j.data.RegistrationRequest;
import com.webauthn4j.data.client.Origin;
import com.webauthn4j.data.client.challenge.Challenge;
import com.webauthn4j.data.client.challenge.DefaultChallenge;
import com.webauthn4j.server.ServerProperty;

import jakarta.servlet.http.HttpSession;
import jp.dataforms.fw.annotation.WebMethod;
import jp.dataforms.fw.app.user.dao.UserInfoTable;
import jp.dataforms.fw.app.user.dao.WebAuthnDao;
import jp.dataforms.fw.app.user.dao.WebAuthnTable;
import jp.dataforms.fw.controller.Form;
import jp.dataforms.fw.response.JsonResponse;
import jp.dataforms.fw.response.Response;
import jp.dataforms.fw.util.WebAuthnUtil;

/**
 * 生体認証ページ 用フォームクラス。
 */
public class WebAuthnForm extends Form {
	/**
	 * WebAutn登録時のオプションのセッションキー。
	 */
	private static final String WEB_AUTHN_CREATE_OPTION = "WebAuthnCreateOption";

	/**
	 * logger.
	 */
	private static Logger logger = LogManager.getLogger(WebAuthnForm.class);
	
	/**
	 * コンストラクタ。
	 */
	public WebAuthnForm() {
		super(null);
		// TODO:1.フィールドを追加します。
		// TODO:2.Webリソース作成で、ページのHTMLを作成します。
		// TODO:3.必要に応じて作成したHTMLにボタンを追加します。
		// TODO:4.Webリソース作成でFormのjsを作成し、各種イベント処理を追加します。
	}

	@Override
	public void init() throws Exception {
		super.init();
	}
	
	
	/**
	 * ランダムなchallengeを生成する。
	 * @return ランダムなchallenge。
	 */
	public String generateChallenge() {
		return WebAuthnUtil.generateChallenge();
	}

	/**
	 * Originを取得する。
	 * @return Origin。
	 */
	private String getOrigin() {
		String origin = this.getPage().getRequest().getRequestURL().toString();
		Pattern p = Pattern.compile("^(.+?://.+?)/.*");
		Matcher m = p.matcher(origin);
		if (m.find()) {
			origin = m.group(1);
		}
		return origin;
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
		String challenge = this.generateChallenge();
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
		Response resp = new JsonResponse(opt);
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
	 * 通常 (URL非安全) のBASE64に変換.
	 *
	 * @param base64url
	 * @return base64
	 */
	private String convertBase64UnSafeUrl(final String base64url) {
	    if(base64url == null) {
	        return null;
	    } else {
	        String ret = base64url;
	        ret = ret.replaceAll("-", "+");
	        ret = ret.replaceAll("_", "/");
	        ret += "===";
	        ret = ret.substring(0, ((ret.length() / 4) * 4));
	        return ret;
	    }
	}

	
	/**
	 * 生体情報の登録を行う。
	 * @param p パラメータ。
	 * @return 応答情報。
	 * @throws Exception 例外。
	 */
	@WebMethod
	public Response regist(final Map<String, Object> p) throws Exception {
//		String json = JsonUtil.encode(p, true);
		String id = (String) p.get("id");
		String authenticatorAttachment = (String) p.get("authenticatorAttachment");
		String type = (String) p.get("type");
		String attestationObject = (String) p.get("attestationObject");
		String clientDataJSON = (String) p.get("clientDataJSON");
		logger.debug("id=" + id);
		logger.debug("authenticatorAttachment=" + authenticatorAttachment);
		logger.debug("type=" + type);
		logger.debug("attestationObject=" + attestationObject);
		logger.debug("clientDataJSON=" + clientDataJSON);
		byte[] ao = Base64.getDecoder().decode(this.convertBase64UnSafeUrl(attestationObject));
		logger.debug("ao.length=" + ao.length);
		byte[] cdj = Base64.getDecoder().decode(this.convertBase64UnSafeUrl(clientDataJSON));
		logger.debug("cdj.length=" + cdj.length);
		
		HttpSession session = this.getPage().getRequest().getSession();
		@SuppressWarnings("unchecked")
		Map<String, Object> opt= (Map<String, Object>) session.getAttribute("WebAuthnOption");
		logger.debug("opt=" + opt);
		WebAuthnManager webAuthnManager = WebAuthnManager.createNonStrictWebAuthnManager();		
		ServerProperty serverProperty = this.getServerProperty();
		boolean userVerificationRequired = false;
		boolean userPresenceRequired = true;
		RegistrationRequest registrationRequest = new RegistrationRequest(ao, cdj);
		RegistrationParameters registrationParameters = new RegistrationParameters(serverProperty, null, userVerificationRequired, userPresenceRequired);
		RegistrationData registrationData = webAuthnManager.parse(registrationRequest);
		webAuthnManager.validate(registrationData, registrationParameters);
		logger.debug("AttestationObject:" + registrationData.getAttestationObject().toString());
		logger.debug("CollectedClientData:" + registrationData.getCollectedClientData().toString());
		AttestationObjectConverter attestationObjectConverter = new AttestationObjectConverter(new ObjectConverter());
		String attestationObjectBase64 = attestationObjectConverter.convertToBase64urlString(registrationData.getAttestationObject());
		CollectedClientDataConverter collectedClientDataConverter = new CollectedClientDataConverter(new ObjectConverter());
		String collectedClientDataBase64 = collectedClientDataConverter.convertToBase64UrlString(registrationData.getCollectedClientData());
	    logger.debug("getAttestationObjectBytes() = " + attestationObjectBase64);
	    logger.debug("getCollectedClientDataBytes() = " + collectedClientDataBase64);
	    logger.debug("getClientExtensions() = " + registrationData.getClientExtensions());
	    logger.debug("getTransports() = " + registrationData.getTransports());
	    Long userId = this.getPage().getUserId();
	    WebAuthnDao dao = new WebAuthnDao(this);
	    List<Map<String, Object>> list = dao.query(userId);
	    WebAuthnTable.Entity e = new WebAuthnTable.Entity();
	    if (list.size() > 0) {
	    	e.setMap(list.get(0));
	    }
	    e.setWebAuthName("default");
	    e.setUserId(userId);
	    e.setAuthId(id);
	    e.setAuthType(type);
	    e.setAuthenticatorAttachment(authenticatorAttachment);
	    e.setAttestationObject(attestationObjectBase64);
	    e.setCollectedClientData(collectedClientDataBase64);
	    e.setCreateUserId(userId);
	    e.setUpdateUserId(userId);
	    dao.regist(e.getMap());
	    
	    
		Response resp = new JsonResponse(JsonResponse.SUCCESS, "");
		return resp;
		
	}

}
