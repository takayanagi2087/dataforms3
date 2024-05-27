package jp.dataforms.fw.controller;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jp.dataforms.fw.app.user.dao.UserAttributeTable;
import jp.dataforms.fw.response.Response;
import jp.dataforms.fw.servlet.DataFormsServlet;
import jp.dataforms.fw.util.CryptUtil;
import jp.dataforms.fw.util.StringUtil;

/**
 * WEBエントリーポイントクラス。
 * <pre>
 * Page、バッチ処理、API等サーブレットから直接呼び出されるWebComponentは、
 * このインターフェースを実装する必要があります。
 * </pre>
 *
 */
public interface WebEntryPoint {

	/**
	 * ユーザ情報のセッションキー。
	 */
	public static final String USER_INFO = "userInfo";

	//	Response getHtml(final Map<String, Object> params) throws Exception;
	/**
	 * Webアプリケーションの処理を実行します。
	 * @param p パラメータ。
	 * @return 応答情報。
	 * @throws Exception 例外。
	 */
	Response exec(final Map<String, Object> p) throws Exception;

	/**
	 * 要求情報を取得します。
	 * @return 要求情報。
	 */
	HttpServletRequest getRequest();

	/**
	 * 要求情報を設定します。
	 * @param request 要求情報。
	 */
	void setRequest(final HttpServletRequest request);

	/**
	 * 応答情報を取得します。
	 * @return 応答情報。
	 */
	HttpServletResponse getResponse();

	/**
	 * 応答情報を設定します。
	 * @param response 応答情報。
	 */
	void setResponse(final HttpServletResponse response);

	/**
	 * QueryStringマップを設定します。
	 * @param map マップ。
	 */
	void setQueryString(final Map<String, Object> map);

	/**
	 * QueryStringマップを取得します。
	 * @return QueryStringマップ。
	 */
	Map<String, Object> getQueryString();


	/**
	 * JDBC接続を設定します。
	 * @param connection JDBC接続。
	 */
	void setConnection(final Connection connection);

	/**
	 * JDBC接続を取得します。
	 * @return JDBC接続。
	 */
	Connection getConnection();

	/**
	 * 認証済みかどうかを返します。
	 * @param params POSTされたパラメータ。
	 * @return 認証済みの場合true。
	 * @throws Exception 例外。
	 */
	default boolean isAuthenticated(final Map<String, Object> params) throws Exception {
		return false;
	}

	/**
	 * セッションからユーザ情報を取得します。
	 * @return ユーザ情報。
	 */
	default Map<String, Object> getUserInfo() {
		@SuppressWarnings("unchecked")
		Map<String, Object> userInfo = (Map<String, Object>) this.getRequest().getSession().getAttribute(USER_INFO);
		return userInfo;
	}

    /**
     * ログイン中のユーザIDを取得する。
     * @return ログイン中のID。
     */
	default long getUserId() {
    	long userid = -1L;
    	@SuppressWarnings("unchecked")
		Map<String, Object> userInfo = (Map<String, Object>) this.getRequest().getSession().getAttribute(USER_INFO);
    	if (userInfo != null) {
        	userid = (Long) userInfo.get("userId");
    	}
    	return userid;
    }

	/**
	 * エラーページのURLを取得します。
	 * @return エラーページ。
	 */
	default String getErrorPage() {
		return DataFormsServlet.getErrorPage() + "." + Page.getServlet().getPageExt();
	}



	/**
	 * 該当するユーザ属性を持つかをチェックします。
	 * @param t ユーザ属性。
	 * @param v ユーザ属性値。
	 * @return 指定されたユーザ属性を持つ場合true。
	 */
	@SuppressWarnings("unchecked")
	default boolean checkUserAttribute(final String t, final String v) {
		Map<String, Object> userInfo = (Map<String, Object>) this.getRequest().getSession().getAttribute(USER_INFO);
		if (userInfo != null) {
			List<Map<String, Object>> attlist = (List<Map<String, Object>>) userInfo.get("attTable");
			for (Map<String, Object> m: attlist) {
				String type = (String) m.get("userAttributeType");
				String value = (String) m.get("userAttributeValue");
				if (t.equals(type) && v.equals(value)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * ユーザ属性を取得します。
	 * @param attrib 属性名称。
	 * @return 属性値。
	 */
	default String getUserArribute(final String attrib) {
		String ret = null;
		Map<String, Object> userInfo = this.getUserInfo();
		if (userInfo != null) {
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> attlist = (List<Map<String, Object>>) userInfo.get("attTable");
			for (Map<String, Object> m: attlist) {
				UserAttributeTable.Entity e = new UserAttributeTable.Entity();
				e.setMap(m);
				if (attrib.equals(e.getUserAttributeType())) {
					ret = e.getUserAttributeValue();
					break;
				}
			}
		}
		return ret;
	}



    /**
     * CSRF対策のTOKENを取得します。
     * @return CSRF対策のTOKEN。
     * @throws Exception 例外。
     */
    default String getCsrfToken() throws Exception{
		String token = null;
		String pass = DataFormsServlet.getCsrfSessionidCrypPassword();
		if (pass != null) {
			String sid = this.getRequest().getSession().getId();
			String key = CryptUtil.encrypt(sid, pass);
			token = key;
		}
		return token;
    }

    /**
     * CSRF対策のリクエストチェックを行います。
     * @param param パラメータ。
     * @return 問題ない場合true。
     * @throws Exception 例外。
     */
    default boolean isValidRequest(final Map<String, Object> param) throws Exception {
    	boolean ret = false;
    	if (DataFormsServlet.getCsrfSessionidCrypPassword() == null) {
    		ret = true;
    	} else {
    		String csrfToken = (String) param.get("csrfToken");
    		String token = this.getCsrfToken();
    		if (!StringUtil.isBlank(csrfToken)) {
    			String etoken = java.net.URLEncoder.encode(token, DataFormsServlet.getEncoding());
    			if (csrfToken.equals(token) || csrfToken.equals(etoken)) {
    				ret = true;
    			}
    		}
    	}
    	return ret;
    }

    /**
     * ContextのURLを取得します。
     * <pre>
     * "http[s]://server[:port]/context"の文字列を返します。
     * </pre>
     * @return ContextのURL。
     */
    default String getContextUrl() {
    	HttpServletRequest req = this.getRequest();
		String scheme = req.getScheme();
		String sv = req.getServerName();
		int port = req.getServerPort();
		String p = ":" + port;
		if ((port == 80 && "http".equals(scheme)) || (port == 443 && "https".equals(scheme))) {
			p = "";
		}
		String context = req.getContextPath();
		String url = scheme + "://" + sv + p + context;
		return url;
    }
}
