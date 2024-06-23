package jp.dataforms.fw.controller;

import java.lang.ref.WeakReference;
import java.sql.Connection;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


/**
 * バッチ処理,WEB APIの基本クラス。
 *
 */
public abstract class WebProcess extends WebComponent implements WebEntryPoint {
    /**
     * 要求情報への弱参照。
     */
    private WeakReference<HttpServletRequest> request = null;

    /**
     * 応答情報への弱参照。
     */
    private WeakReference<HttpServletResponse> response = null;

    /**
     * コンストラクタ。
     */
    public WebProcess() {
    	
    }

	/**
	 * 要求情報を取得します。
	 * @return 要求情報。
	 */
    @Override
	public final HttpServletRequest getRequest() {
		return request.get();
	}

	/**
	 * 要求情報を設定します。
	 * @param request 要求情報。
	 */
    @Override
	public void setRequest(final HttpServletRequest request) {
		this.request = new WeakReference<HttpServletRequest>(request);
	}

	/**
	 * 応答情報を取得します。
	 * @return 応答情報。
	 */
    @Override
	public final HttpServletResponse getResponse() {
		return response.get();
	}

	/**
	 * 応答情報を設定します。
	 * @param response 応答情報。
	 */
    @Override
	public void setResponse(final HttpServletResponse response) {
		this.response = new WeakReference<HttpServletResponse>(response);
	}


	/**
	 * QueryStringをマップに展開したものを保持します。
	 */
	private Map<String, Object> queryString = null;


	/**
	 * QueryStringマップを設定します。
	 * @param map マップ。
	 */
	@Override
	public void setQueryString(final Map<String, Object> map) {
		this.queryString = map;
	}

	/**
	 * QueryStringマップを取得します。
	 * @return QueryStringマップ。
	 */
	@Override
	public Map<String, Object> getQueryString() {
		return this.queryString;
	}

	/**
	 * JDBC接続。
	 */
	private WeakReference<Connection> connection = null;

	/**
	 * {@inheritDoc}
	 * DB接続を取得します。
	 */
	@Override
	public Connection getConnection() {
		if (this.connection != null) {
			return this.connection.get();
		} else {
			return null;
		}
	}

	/**
	 * JDBC接続を設定します。
	 * @param connection JDBC接続。
	 */
	@Override
	public void setConnection(final Connection connection) {
		this.connection = new WeakReference<Connection>(connection);
	}

}
