package jp.dataforms.fw.servlet;

import java.net.URI;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.servlet.http.HttpServletRequest;
import jp.dataforms.fw.util.StringUtil;
import jp.dataforms.fw.util.WebResourceUtil;

/**
 * dataforms2.jarで定義するフィールターのベースクラス。
 *
 */
public class DataFormsFilter {

	/**
	 * Logger.
	 */
	private static Logger logger = LogManager.getLogger(DataFormsFilter.class);

	/**
	 * コンストラクタ。
	 */
	public DataFormsFilter() {
		
	}
	
	/**
	 * Webリソースのタイムスタンプキャッシュ。
	 */
	private static Map<String, Long> webResourceTimestampCache = Collections.synchronizedMap(new HashMap<String, Long>());

	/**
	 * タイムスタンプキャッシュを取得します。
	 * @return タイムスタンプキャッシュ。
	 */
	public static Map<String, Long> getWebResourceTimestampCache() {
		return webResourceTimestampCache;
	}

	/**
	 * WebリソースのURLを取得します。
	 *
	 * @param req 要求情報。
	 * @param path パス。
	 * @return URL。
	 * @throws Exception 例外。
	 */
	public String getWebResourceUrl(final HttpServletRequest req, final String path) throws Exception {
		URI uri = new URI(req.getRequestURL().toString());
		URL accessurl = uri.toURL();
		String url = null;
		if (StringUtil.isBlank(DataFormsServlet.getWebResourceUrl())) {
			url = accessurl.getProtocol() + "://" + req.getServerName() + ":" + req.getServerPort() + path;
		} else {
			url = DataFormsServlet.getWebResourceUrl() + path;
		}
		logger.debug("getWebResourceUrl:path={}", path);
		logger.debug("getWebResourceUrl:url={}", url);
		return url;
	}

	/**
	 * Webリソースを読み込みます。
	 * @param path リソースのパス。
	 * @return Webリソースの文字列。
	 * @throws Exception 例外。
	 */
	protected String readWebResource(final String path) throws Exception {
		return WebResourceUtil.getWebResource(path);
	}

	/**
	 * 指定ファイルのタイムスタンプを取得します。
	 * @param path 取得するファイルのパス。
	 * @return タイムスタンプ。
	 */
	protected Long getLastUpdate(final String path) throws Exception {
		Long ret =  WebResourceUtil.getLastUpdateLong(path);
		return ret;
	}

}
