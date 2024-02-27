package jp.dataforms.fw.controller;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jp.dataforms.fw.annotation.WebMethod;
import jp.dataforms.fw.response.Response;
import jp.dataforms.fw.response.TextResponse;

/**
 * バッチ処理の基本クラス。
 *
 */
public abstract class BatchProcess extends WebProcess {
	/**
	 * Logger.
	 */
	private static Logger logger = LogManager.getLogger(BatchProcess.class);

	/**
	 * コンストラクタ。
	 */
	public BatchProcess() {
		super();
	}

	/**
	 * localhostからのアクセスみ許可。
	 */
	@Override
	public boolean isAuthenticated(Map<String, Object> params) throws Exception {
		String rhost = this.getRequest().getRemoteAddr();
		logger.debug(() -> "rhost=" + rhost);
		if ("0:0:0:0:0:0:0:1".equals(rhost) || "127.0.0.1".equals(rhost)) {
			return true;
		} else {
			return false;
		}
	}

	@WebMethod
	@Override
	public Response exec(final Map<String, Object> params) throws Exception {
		int ret = this.run(params);
		return new TextResponse(Integer.toString(ret));
	}

	/**
	 * バッチ処理。
	 * @param params パラメータ。
	 * @return 終了コード。
	 */
	public abstract int run(final Map<String, Object> params);
}
