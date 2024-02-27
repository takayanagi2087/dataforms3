package jp.dataforms.fw.exception;

import java.util.HashMap;
import java.util.Map;

import jp.dataforms.fw.controller.WebEntryPoint;
import jp.dataforms.fw.response.JsonResponse;
import jp.dataforms.fw.util.MessagesUtil;

/**
 * アプリケーションレベルで発生する例外クラス。
 *
 */
public class ApplicationException extends Exception {
	/**
	 * UID。
	 */
	private static final long serialVersionUID = 7025730004513064767L;

	/**
	 * メッセージキー。
	 */
	private String messageKey = null;


	/**
	 * 応答モード。
	 */
	public enum ResponseMode {
		/** エラー情報をJSONで送る。 */
		JSON
		/** エラーページへのリダイレクト。 */
		, REDIRECT_TO_ERROR_PAGE
	}

	/**
	 * 応答モード。
	 */
	private ResponseMode responseMode = ResponseMode.JSON;

	/**
	 * コンストラクタ。
	 * @param epoint エラーが発生したページ。
	 * @param msgkey メッセージのキー。
	 * @param args メッセージ引数。
	 */
	public ApplicationException(final WebEntryPoint epoint, final String msgkey, final String... args) {
		super(MessagesUtil.getMessage(epoint, msgkey, args));
		this.messageKey = msgkey;
	}


	/**
	 * コンストラクタ。
	 *
	 * @param msgkey メッセージのキー。
	 * @param message メッセージのテキスト。
	 * @deprecated
	 */
	public ApplicationException(final String msgkey, final String message) {
		super(message);
		this.messageKey = msgkey;
	}

	/**
	 * メッセージキーを取得します。
	 * @return メッセージキー。
	 */
	public String getMessageKey() {
		return messageKey;
	}

	/**
	 * メッセージキーを設定します。
	 * @param messageKey メッセージキー。
	 */
	public void setMessageKey(final String messageKey) {
		this.messageKey = messageKey;
	}

	/**
	 * 応答モードを取得します。
	 * @return 応答モード。
	 */
	public ResponseMode getResponseMode() {
		return responseMode;
	}

	/**
	 * 応答モードを設定します。
	 * @param responseMode 応答モード。
	 */
	public void setResponseMode(final ResponseMode responseMode) {
		this.responseMode = responseMode;
	}


	/**
	 * 例外の情報を持ったJsonResponseを取得します。
	 * @return JsonResponseのインスタンス。
	 */
	public JsonResponse getJsonResponse() {
		Map<String, Object> einfo = new HashMap<String, Object>();
		einfo.put("key", this.getMessageKey());
		einfo.put("message", this.getMessage());
		JsonResponse ret = new JsonResponse(JsonResponse.APPLICATION_EXCEPTION, einfo);
		return ret;
	}
}
