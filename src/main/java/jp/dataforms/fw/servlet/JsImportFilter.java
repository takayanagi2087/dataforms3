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
	 * コンストラクタ。
	 */
	public JsImportFilter() {
		
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
//			logger.debug("JsPath:" + path);
			StringBuilder sb = new StringBuilder();
			int idx0 = 0;
			while (m.find()) {
				sb.append(js.substring(idx0, m.start()));
				idx0 = m.end();
				logger.debug("JsImport:" + m.group(1).trim() + ":" + m.group(2));
//				String abspath = PathUtil.getAbsolutePath(path, m.group(2));
/*				String ts = this.getTimestamp(abspath);
				if (ts == null) {
					this.readWebResource(abspath);
					ts = this.getTimestamp(abspath);
				}*/
				String ts = DataFormsServlet.getAppUpdateTime();
				String imp = "import { " + m.group(1).trim() + " } from '" + m.group(2) + "?t=" + ts + "'";
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
		String ret = this.readWebResource(path);
		ret = this.rewriteImport(req, path, ret);
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
				String context = sreq.getContextPath();
				String fname = sreq.getRequestURI().substring(context.length());
				logger.debug(() -> "doFilter filename=" + fname);
				// queryStringにskip=trueが指定された場合、filterしない。
				logger.debug("fname=" + fname);
				if (fname.indexOf("/jslib/") < 0 
					&& fname.indexOf("/doc/javadoc/") < 0
					&& fname.indexOf("/doc/jsdoc/") < 0) {
					String contents = this.readJs(sreq, fname);
					if (contents != null) {
//						contents = this.rewriteImport(sreq, fname, contents);
						// logger.debug("contents=" + contents);
						sresp.setContentType("text/javascript; charset=utf-8");
						Long ts = this.getLastUpdate(fname);
						logger.debug("fname=" + fname + ", ts=" + ts);
						if (ts != null) {
							sresp.setDateHeader("Last-Modified", ts);
						}
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
