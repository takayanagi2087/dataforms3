package jp.dataforms.fw.util;

import java.util.Map;

import jp.dataforms.fw.app.user.dao.UserInfoTable;
import jp.dataforms.fw.app.user.dao.WebAuthnTable;
import jp.dataforms.fw.controller.WebEntryPoint;

/**
 * ユーザ活動ログのユーティリティ。
 */
public final class UserLogUtil {
	/**
	 * コンストラクタ。
	 */
	private UserLogUtil() {
		
	}
	
	/**
	 * ログに出力するクライアントを作成します。
	 * @param ep ページ。
	 * @param data データ。
	 * @return ログに出力するユーザ情報。
	 */
	public static String getClientInfo(final WebEntryPoint ep, final Map<String, Object> data) {
		String loginId = (String) data.get(UserInfoTable.Entity.ID_LOGIN_ID);
		String password = (String) data.get(UserInfoTable.Entity.ID_PASSWORD);
		String passkey = (String) data.get(WebAuthnTable.Entity.ID_AUTHENTICATOR_NAME);
		String remoteAddr = ep.getRequest().getRemoteAddr();
		String p = "";
		if (!StringUtil.isBlank(password)) {
			p = "**********";
		}
		String ret= "Client:[" + remoteAddr + " / " + loginId + " / " + p + " / " + passkey + "] ";
		return ret;
	}
	
	

}
