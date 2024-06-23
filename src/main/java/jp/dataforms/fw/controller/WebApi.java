package jp.dataforms.fw.controller;

import java.util.Map;

import jp.dataforms.fw.response.Response;

/**
 * WEB APIの基本クラス。
 *
 */
public abstract class WebApi extends WebProcess {
	/**
	 * コンストラクタ。
	 */
	public WebApi() {
		
	}
	
	/**
	 * {@inheritDoc}
	 * <pre>
	 * APIを使用できるユーザを判定する処理を実装します。
	 * </pre>
	 */
	@Override
	public abstract boolean isAuthenticated(final Map<String, Object> params) throws Exception;

	/**
	 * {@inheritDoc}
	 *
	 * <pre>
	 * APIの処理を実装します。
	 * </pre>
	 *
	 */
	@Override
	public abstract Response exec(final Map<String, Object> p) throws Exception;
}
