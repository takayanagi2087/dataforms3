package jp.dataforms.fw.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


/**
 * Javascript *.jsフィルター。
 * <pre>
 * *.js更新時のキャッシュ対策フィルターです。
 * Javascript中のimport文のパスに、元ソースファイルのタイムスタンプを追加します。
 * 
 * import { EditForm} from '../../../controller/EditForm.js';
 *       ↓
 * import { EditForm} from '../../../controller/EditForm.js?t=1715688354000';
 * </pre>
 */
@WebFilter(filterName="js-filter", urlPatterns = "*.js")
public class JsImportFilter extends DataFormsFilter implements Filter {
	/**
	 * Logger.
	 */
	private static Logger logger = LogManager.getLogger(JsImportFilter.class);
	
	/**
	 * js Map.
	 */
	private static Map<String, String> jsMap = Collections.synchronizedMap(new HashMap<String, String>());

	/**
	 * "../"のパターンが幾つあったかを数えます。
	 * @param sp pathを分解した配列。
	 * @return "../"の数。
	 */
	private int countParent(final String[] sp) {
		int cnt = 0;
		for (String s: sp) {
			if ("..".equals(s)) {
				cnt++;
			}
		}
		return cnt;
	}

	/**
	 * 指定された相対パスを絶対パスに変換します。
	 * @param bpath 元のURIの絶対パス。
	 * @param path 相対パス。
	 * @return 絶対パス。
	 */
	private String getAbsolutePath(final String bpath, final String path) {
		if (path.indexOf("./") == 0) {
			// 同じパス
			String[] bsp = bpath.split("/");
			StringBuilder sb = new StringBuilder();
			for (int i = 1; i < bsp.length - 1; i++) {
				sb.append('/');
				sb.append(bsp[i]);
			}
			sb.append(path.substring(1));
			return sb.toString();
		} else if (path.indexOf("../") == 0) {
			logger.debug("getAbsolutePath:" + bpath + "," + path);
			String[] sp = path.split("/");
			int pcnt = this.countParent(sp);
			
			String[] bsp = bpath.split("/");
			StringBuilder sb = new StringBuilder();
			for (int i = 1; i < bsp.length - pcnt - 1; i++) {
				sb.append("/");
				sb.append(bsp[i]);
			}
			for (int i = pcnt; i < sp.length; i++) {
				sb.append("/");
				sb.append(sp[i]);
			}
			String abspath = sb.toString();
			logger.debug("getAbsolutePath:" + abspath);
			return abspath;
		} else {
			// 絶対パス設定のはず。
			return path;
		}
	}
	
	/**
	 * import文のキャッシュ対策を行う。
	 * @param req 要求情報。
	 * @param path Javascriptのパス。
	 * @param js Javsscriptのソース。
	 * @return import文のキャッシュ対策済のソース。
	 * @throws Exception 例外。
	 */
	private String rewriteImport(final HttpServletRequest req, final String path, final String js) throws Exception {
		if (js != null) {
			Pattern p = Pattern.compile("import.*\\{(.+?)}.*from.*['\\\"](.+?)['\\\"]");
			Matcher m = p.matcher(js);
			logger.debug("JsPath:" + path);
			StringBuilder sb = new StringBuilder();
			int idx0 = 0;
			while (m.find()) {
				sb.append(js.substring(idx0, m.start()));
				idx0 = m.end();
				logger.debug("JsImport:" + m.group(1).trim() + ":" + m.group(2));
				String abspath = this.getAbsolutePath(path, m.group(2));
				Long t = this.getLastUpdate(abspath);
				if (t == null) {
					this.readWebResource(req, abspath);
					t = this.getLastUpdate(abspath);
				}
				String imp = "import { " + m.group(1).trim() + "} from '" + m.group(2) + "?t=" + t + "'";
				logger.debug("Abs path:" + abspath + ", t=" + t);
				sb.append(imp);
			}
			sb.append(js.substring(idx0));
			return sb.toString();
		} else {
			logger.warn("js not found=" + path);
		}
		return js;
	}
	
	
	/**
	 * Javascript(*.js)を読み込みます。
	 * @param req 要求情報。
	 * @param path リソースのパス。
	 * @return Webリソースの文字列。
	 * @throws Exception 例外。
	 */
	private String readJs(final HttpServletRequest req, final String path) throws Exception {
		logger.debug("readJs path={}", path);
		String js = JsImportFilter.jsMap.get(path);
		if (js != null) {
			return js;
		}
		String ret = this.readWebResource(req, path + "?skip=true");
		JsImportFilter.jsMap.put(path, ret);
		return ret;
	}

	
	
	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
		logger.debug("----------------------------------");
		if (req instanceof HttpServletRequest) {
			HttpServletRequest sreq = (HttpServletRequest) req;
			HttpServletResponse sresp = (HttpServletResponse) resp;
			try {
				String fname = sreq.getRequestURI();
				logger.debug(() -> "doFilter filename=" + fname);
				// queryStringにskip=trueが指定された場合、filterしない。
				String skip = sreq.getParameter("skip");
				if (!"true".equals(skip)) {
					String contents = this.readJs(sreq, fname);
					if (contents != null) {
						contents = this.rewriteImport(sreq, fname, contents);
						logger.debug("contents=" + contents);
						sresp.setContentType("text/javascript; charset=utf-8");
						Long ts = DataFormsFilter.getWebResourceTimestampCache().get(fname);
						logger.debug("ts=" + ts);
						sresp.setDateHeader("Last-Modified", ts);
						try (PrintWriter out = resp.getWriter()) {
							out.print(contents);
						}
					} else {
						sresp.setStatus(HttpURLConnection.HTTP_NOT_FOUND);
					}
				}
			} catch (Exception e) {
				logger.error(() -> e.getMessage(), e);
			}
		}
		chain.doFilter(req, resp);
	}
}
