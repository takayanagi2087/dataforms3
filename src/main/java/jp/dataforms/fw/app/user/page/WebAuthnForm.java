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
	 * 生体情報の登録を行う。
	 * @param p パラメータ。
	 * @return 応答情報。
	 * @throws Exception 例外。
	 */
	@WebMethod
	public Response regist(final Map<String, Object> p) throws Exception {
	    Long userId = this.getPage().getUserId();
		ServerProperty serverProperty = this.getServerProperty();
		Map<String, Object> regData = WebAuthnUtil.getRegistDataMap(p, serverProperty);
	    WebAuthnDao dao = new WebAuthnDao(this);
	    List<Map<String, Object>> list = dao.query(userId);
	    WebAuthnTable.Entity e = new WebAuthnTable.Entity();
	    if (list.size() > 0) {
	    	e.setMap(list.get(0));
		    e.setUpdateUserId(userId);
	    } else {
		    e.setCreateUserId(userId);
		    e.setUpdateUserId(userId);
	    }
    	e.getMap().putAll(regData);
	    e.setWebAuthName("default");
	    e.setUserId(userId);
	    dao.regist(e.getMap());
		Response resp = new JsonResponse(JsonResponse.SUCCESS, "");
		return resp;
		
	}

}
