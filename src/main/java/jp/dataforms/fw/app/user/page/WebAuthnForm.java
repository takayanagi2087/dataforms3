package jp.dataforms.fw.app.user.page;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.webauthn4j.WebAuthnManager;
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
import jp.dataforms.fw.controller.Form;
import jp.dataforms.fw.response.JsonResponse;
import jp.dataforms.fw.response.Response;

/**
 * 生体認証ページ 用フォームクラス。
 */
public class WebAuthnForm extends Form {
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
/*		long seed = (new java.util.Date()).getTime();
		Random r = new Random(seed);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 16; i++) {
			int v = r.nextInt(10);
			sb.append(Integer.toString(v));
		}
		return sb.toString();*/
		return "0123456789012345";
	}

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
	 * 生体情報の登録を行う。
	 * @param p パラメータ。
	 * @return 応答情報。
	 * @throws Exception 例外。
	 */
	@WebMethod
	public Response getCreateOption(final Map<String, Object> p) throws Exception {
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
		session.setAttribute("WebAuthnOption", opt);
		Response resp = new JsonResponse(opt);
		return resp;
	}
	
	private ServerProperty getServerProperty() {
		HttpSession session = this.getPage().getRequest().getSession();
		@SuppressWarnings("unchecked")
		Map<String, Object> opt= (Map<String, Object>) session.getAttribute("WebAuthnOption");
		Origin origin = new Origin(this.getOrigin());
		String rpId = this.getPage().getRequest().getServerName();
		String ch = (String) opt.get("challenge");
		Challenge challenge = new DefaultChallenge(ch.getBytes());
		byte[] tokenBindingId = null;
		ServerProperty ret = new ServerProperty(origin, rpId, challenge, tokenBindingId);
		return ret;
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
		byte[] ao = Base64.getDecoder().decode(attestationObject);
		logger.debug("ao.length=" + ao.length);
		byte[] cdj = Base64.getDecoder().decode(clientDataJSON);
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
		RegistrationParameters registrationParameters 
			= new RegistrationParameters(serverProperty, null, userVerificationRequired, userPresenceRequired);
		RegistrationData registrationData = null;
	    registrationData = webAuthnManager.parse(registrationRequest);
	    webAuthnManager.validate(registrationData, registrationParameters);

	    logger.debug("getAttestationObjectBytes() = " + Base64.getEncoder().encodeToString(registrationData.getAttestationObjectBytes()));
	    logger.debug("getCollectedClientDataBytes() = " + Base64.getEncoder().encodeToString(registrationData.getCollectedClientDataBytes()));
	    logger.debug("getClientExtensions() = " + registrationData.getClientExtensions());
	    logger.debug("getTransports() = " + registrationData.getTransports());
		Response resp = new JsonResponse(JsonResponse.SUCCESS, "");
		return resp;
		
	}

}
