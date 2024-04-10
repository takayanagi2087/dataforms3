package jp.dataforms.fw.util;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.arnx.jsonic.JSON;

/**
 * httpアクセス用のクラス。
 *
 */
public class WebClient {
	/**
	 * ヘッダ名称のキー。
	 */
	private static final String HEADER = "header";

	/**
	 * ヘッダ値のキー。
	 */
	private static final String VALUE = "value";


	/**
	 * Logger.
	 */
	private static Logger logger = LogManager.getLogger(WebClient.class);

	/**
	 * Getメソッド。
	 */
	public static final String METHOD_GET = "GET";
	/**
	 * Postメソッド。
	 */
	public static final String METHOD_POST = "POST";

	/**
	 * URL.
	 */
	private String url = null;

	/**
	 * HTTPメソッド。
	 */
	private String httpMethod = METHOD_GET;

	/**
	 * 送信するデータ形式。
	 * application/x-www-form-urlencoded または application/json
	 */
	private String contentType = null;

	/**
	 * 要求ヘッダのリスト。
	 */
	private List<Map<String, String>> requestHeaderList = new ArrayList<Map<String, String>>();

	/**
	 * 送信ヘッダを追加します。
	 * @param header ヘッダ名称。
	 * @param value 値。
	 */
	public void addRequestHeader(final String header, final String value) {
		Map<String, String> h = new HashMap<String, String>();
		h.put(HEADER, header);
		h.put(VALUE, value);
		this.requestHeaderList.add(h);
	}

	/**
	 * コンストラクタ。
	 * @param url URL。
	 */
	public WebClient(final String url) {
		this.url = url;
		this.httpMethod = METHOD_GET;
	}

	/**
	 * コンストラクタ。
	 * @param url URL。
	 * @param method HTTP method。
	 */
	public WebClient(final String url, final String method) {
		this.url = url;
		this.httpMethod = method;
	}

	/**
	 * URLを取得します。
	 * @return URL。
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * URLを設定します。
	 * @param url URL。
	 */
	public void setUrl(final String url) {
		this.url = url;
	}

	/**
	 * HTTPのメソッドを取得します。
	 * @return HTTPのメソッド。
	 */
	public String getHttpMethod() {
		return httpMethod;
	}

	/**
	 * HTTPのメソッドを設定します。
	 * @param httpMethod HTTPのメソッド。
	 */
	public void setHttpMethod(final String httpMethod) {
		this.httpMethod = httpMethod;
	}

	/**
	 * 送信データのcontent-typeを取得します。
	 * @return 送信データのcontent-type。
	 */
	public String getContentType() {
		return contentType;
	}

	/**
	 * 送信データのcontent-typeを設定します。
	 * @param contentType 送信データのcontent-type。
	 */
	public void setContentType(final String contentType) {
		this.contentType = contentType;
	}

	/**
	 * パラメータを解析します。
	 * @param p パラメータ。
	 * @return 送信データ。
	 */
	protected String parseSendData(final Object p) {
		if (p == null) {
			return null;
		}
		if (p instanceof String) {
			// 文字列の場合はqueryStringの形式。
			this.setContentType("application/x-www-form-urlencoded");
			return (String) p;
		} else {
			// 文字列以外はJSON形式でPOST
			this.setHttpMethod(METHOD_POST);
			this.setContentType("application/json");
			return JSON.encode(p);
		}
	}

	/**
	 * POSTの場合のデータ送信。
	 * @param conn URLConnection。
	 * @param senddata 送信データ。
	 * @throws Exception 例外。
	 */
	private void sendData(HttpURLConnection conn, String senddata) throws Exception {
		if (METHOD_POST.equals(this.httpMethod)) {
			if (senddata != null) {
				logger.debug(() -> "senddata=" + senddata);
				conn.setDoOutput(true);
				conn.setRequestProperty("Content-Type", this.contentType);
				try (OutputStream os = conn.getOutputStream()) {
					FileUtil.writeOutputStream(senddata.getBytes("utf-8"), os);
				}
			}
		}
	}

	/**
	 * 応答ヘッダ。
	 */
	private Map<String, List<String>> responseHeader = null;

	/**
	 * 応答ヘッダを取得します。
	 * @return 応答ヘッダ。
	 */
	public Map<String, List<String>> getResponseHeader() {
		return this.responseHeader;
	}


	/**
	 * HTTP応答ステータスコード。
	 */
	private int httpStatus = 0;

	/**
	 * HTTP応答ステータスコードを取得します。
	 * @return HTTP応答ステータスコード。
	 */
	public int getHttpStatus() {
		return httpStatus;
	}

	/**
	 * 応答データのcontent-type。
	 */
	private String responseContentType = null;

	/**
	 * 応答データのcontent-typeを取得します。
	 * @return 応答データのcontent-type。
	 */
	public String getResponseContentType() {
		return responseContentType;
	}

	/**
	 * 文字コード。
	 */
	private String contentEncoding = "utf-8";

	/**
	 * 文字コードを取得します。
	 * @return 文字コード。
	 */
	public String getContentEncoding() {
		return contentEncoding;
	}

	/**
	 * 変換モード。
	 *
	 */
	public enum Convert {
		/** 受信したバイナリデータをそのまま返します。 */
		BINARY,
		/** 可能であれば文字列に変換します。 */
		TEXT,
		/** 可能であればMap等のJavaオブジェクトに変換します。 */
		OBJECT
	}


	private InputStream getInputStream(final HttpURLConnection conn) {
		try {
			InputStream is = conn.getInputStream();
			return is;
		} catch (Exception e) {
			InputStream is = conn.getErrorStream();
			return is;
		}
	}

	/**
	 * 応答情報を読み込みます。
	 * @param conn URLConnection。
	 * @param convert 変換モード。
	 * @return 応答情報。
	 * @throws Exception 例外。
	 */
	protected Object readResponse(final HttpURLConnection conn, final Convert convert) throws Exception {
		Object ret = null;
		try (InputStream is = this.getInputStream(conn)) {
			this.contentEncoding = conn.getContentEncoding();
			if (null == this.contentEncoding) {
				this.contentEncoding = "utf-8";
			}
			byte[] buf = FileUtil.readInputStream(is);
			this.responseContentType = this.getContentType(conn);
			ret = this.convert(buf, convert, this.responseContentType, this.contentEncoding);
		}
		return ret;
	}

	/**
	 * Content-Typeを取得します。
	 * @param conn 接続情報。
	 * @return Content-type。
	 */
	protected String getContentType(final HttpURLConnection conn) {
		String ret = conn.getContentType();
		if (ret != null) {
			return conn.getContentType().toLowerCase();
		} else {
			return "application/json";
		}
	}

	/**
	 * テキストのコンテントタイプ。
	 */
	private static final String[] TEXT_CONTENT_TYPE_PATTERN = {
		"application/json"
		, "image/svg+xml"
		, "application/xml"
	};

	/**
	 * テキストに変換できるcontent-typeを判定します。
	 * @param contentType content-type。
	 * @return テキストに変換できるcontentTypeの場合true。
	 */
	protected Boolean isTextContentType(final String contentType) {
		Boolean ret = Boolean.FALSE;
		if (Pattern.matches("text/.+", contentType)) {
			ret = Boolean.TRUE;
		} else {
			for (String ct: TEXT_CONTENT_TYPE_PATTERN) {
				if (contentType.indexOf(ct) >= 0) {
					ret = Boolean.TRUE;
				}
			}
		}
		return ret;
	}

	/**
	 * テキストに変換できるオブジェクトの場合テキストに変換します。
	 * @param buf バッファ。
	 * @param contentType conetnt-type。
	 * @param encoding 文字コード。
	 * @return 変換結果。
	 * @throws Exception 例外。
	 */
	protected Object convertToText(final byte[] buf, final String contentType, final String encoding) throws Exception {
		Object ret = buf;
		if (this.isTextContentType(contentType)) {
			ret = new String(buf, encoding);
		}
		return ret;
	}

	/**
	 * 応答情報を適切に変換します。
	 * @param buf 受信データ。
	 * @param convert 変換モード。
	 * @param contentType content-type。
	 * @param encoding テキストの場合の文字コード。
	 * @return 変換結果。
	 * @throws Exception 例外。
	 */
	protected Object convert(final byte[] buf, final Convert convert, final String contentType, final String encoding) throws Exception {
		Object ret = buf;
		if (convert == Convert.BINARY) {
			return ret;
		} else {
			if (convert == Convert.TEXT) {
				ret = this.convertToText(buf, contentType, encoding);
				return ret;
			} else {
				ret = this.convertToObject(buf, contentType, encoding);
				return ret;
			}
		}
	}

	/**
	 * 応答情報を適切に変換します。
	 * @param buf 受信データ。
	 * @param contentType content-type。
	 * @param encoding 文字コード。
	 * @return 変換結果。
	 * @throws Exception 例外。
	 */
	protected Object convertToObject(final byte[] buf, final String contentType, final String encoding) throws Exception {
		Object ret = this.convertToText(buf, contentType, encoding);
		if (contentType != null) {
			if (contentType.indexOf("application/json") >= 0) {
				ret = JSON.decode((String) ret, HashMap.class);
			}
		}
		return ret;
	}


	/**
	 * APIを呼び出します。
	 * @param p パラメータ。
	 * @param convert 変換モード。
	 * @return 応答情報。
	 * @throws Exception 例外。
	 */
	public Object call(final Object p, final Convert convert) throws Exception {
		Object ret = null;
		String senddata = this.parseSendData(p);
		String url = this.url;
		if (METHOD_GET.equals(this.httpMethod)) {
			// GET
			if (senddata != null) {
				url += "?" + senddata;
			}
		}
		logger.debug("url={}", url);
		URI uri = new URI(url);
		URL u = uri.toURL();
		HttpURLConnection conn = (HttpURLConnection) u.openConnection();
		conn.setRequestMethod(this.httpMethod);
		for (Map<String, String> m: this.requestHeaderList) {
			String header = m.get(HEADER);
			String value = m.get(VALUE);
			conn.setRequestProperty(header, value);
		}
		this.sendData(conn, senddata);
		conn.connect();
		try {
			this.responseHeader = conn.getHeaderFields();
			// HTTPレスポンスコード
			this.httpStatus = conn.getResponseCode();
			ret = this.readResponse(conn, convert);
		} finally {
			conn.disconnect();
		}
		return ret;
	}

	/**
	 * APIを呼び出します。
	 * @param p パラメータ。
	 * @return 応答情報。
	 * @throws Exception 例外。
	 */
	public Object call(final Object p) throws Exception {
		return this.call(p, Convert.OBJECT);
	}

	/**
	 * APIを呼び出します。
	 * @return 応答情報。
	 * @throws Exception 例外。
	 */
	public Object call() throws Exception {
		return this.call(null, Convert.OBJECT);
	}
}
