package jp.dataforms.fw.exception;

import jp.dataforms.fw.controller.WebEntryPoint;

/**
 * 認証例外のクラスです。
 *
 */
public class AuthoricationException extends ApplicationException {
	/**
	 * UID.
	 */
	private static final long serialVersionUID = 3608280697807645027L;

	/**
	 * コンストラクタ。
	 * @param page ページ。
	 */
	public AuthoricationException(final WebEntryPoint page) {
		super(page, "error.auth");
		this.setResponseMode(ResponseMode.REDIRECT_TO_ERROR_PAGE);
	}
}
