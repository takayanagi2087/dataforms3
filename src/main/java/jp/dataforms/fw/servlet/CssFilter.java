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
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jp.dataforms.fw.util.JsonUtil;
import jp.dataforms.fw.util.WebResourceUtil;

/**
 * CSSのフィルター。
 * <pre>
 *
 * Hoge.cssをアクセスすると、Hoge.cssを読み込み変換して出力するフィルターです。
 *
 * 今のところVariables.cssに定義した変数を展開したcssを作成する機能しかありません。
 * この機能は標準cssではできない以下の記述に対応する為に作りました。
 *
 * &#x40;media screen and (max-width: var(--sp-screen-max))
 *
 * そのうちcssの記述性を上げる機能を追加するかもしれません。
 *
 * </pre>
 */
@WebFilter(filterName="css-filter", urlPatterns = "*.css")
public class CssFilter extends DataFormsFilter implements Filter {
	/**
	 * Logger.
	 */
	private static Logger logger = LogManager.getLogger(CssFilter.class);

	/**
	 * CSS Map.
	 */
	private static Map<String, String> cssMap = Collections.synchronizedMap(new HashMap<String, String>());

	/**
	 * コンストラクタ。
	 */
	public CssFilter() {
		
	}
	
	/**
	 * 初期化処理。
	 */
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
/*		logger.debug("CssFilter: init");
		try {
			this.readVar("/frame/flex/Variables.css");
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
		}*/
		Filter.super.init(filterConfig);
	}
	
	/**
	 * スタイルシートを読み込みます。
	 * @param req 要求情報。
	 * @param path リソースのパス。
	 * @return Webリソースの文字列。
	 * @throws Exception 例外。
	 */
	private static String readCss(final HttpServletRequest req, final String path) throws Exception {
		logger.debug("readWebResource path={}", path);
		String css = CssFilter.cssMap.get(path);
		if (css != null) {
			return css;
		}
		String ret = WebResourceUtil.getWebResource(path);
		CssFilter.cssMap.put(path, ret);
		return ret;
	}

	/**
	 * 変数マップ。
	 */
	private static Map<String, Map<String, String>> varMap = new HashMap<String, Map<String, String>>();

	/**
	 * 親のパスを取得します。
	 * @param path パス。
	 * @return 親のパス。
	 */
	private static String getParentPath(final String path) {
		int idx = path.lastIndexOf('/');
		return path.substring(0, idx);
	}

	/**
	 * css内の:rootのカスタムプロパティを取得します。
	 * @param path CSSのファイル名。
	 * @param css CSSの文字列。
	 */
	private static void parseVar(final String path, final String css) {
		logger.debug("parseVar=" + path + "\n" + css);
		String key = CssFilter.getParentPath(path);
		if (CssFilter.varMap.get(key) != null) {
			return;
		}
		Pattern p = Pattern.compile(":root[\\s\\S]\\{([\\s\\S]*?)\\}", Pattern.MULTILINE);
		Matcher m = p.matcher(css);
		if (m.find()) {
			Map<String, String> varMap = new HashMap<String, String>();
			CssFilter.varMap.put(key, varMap);
			String g = m.group(1);
			String[] lines = g.split("[\r\n]");
			Pattern vp = Pattern.compile("(--.*):(.*);");
			for (String line : lines) {
				line = line.replaceAll("/\\*.*\\*/", "");
				Matcher vm = vp.matcher(line);
				if (vm.find()) {
					logger.debug(() -> "var=" + vm.group(1) + "," + vm.group(2));
					varMap.put(vm.group(1), CssFilter.replaceVar(path, vm.group(2)));
				}
			}
		}
	}

	/**
	 * 変数の置き換えを行います。
	 * @param path CSSのファイル名。
	 * @param css スタイルシートのテキスト。
	 * @return 変数を置き換えたcss。
	 */
	private static String replaceVar(final String path, final String css) {
		logger.debug("replaceVar:" + path);
		String ppath = CssFilter.getParentPath(path);
		Map<String, String> map = CssFilter.varMap.get(ppath);
		if (map != null) {
			String ret = css;
			for (String key: map.keySet()) {
				String p = "var\\s*\\(\\s*?" + key + "\\s*?\\)";
				String v = map.get(key);
				ret = ret.replaceAll(p, v);
			}
			return ret;
		} else {
			return css;
		}

	}

	/**
	 * cssの変数設定を読み込みます。
	 * @param fname ファイル。
	 * @throws Exception 例外。
	 */
	public static void readVar(final String fname) throws Exception {
		String ppath = CssFilter.getParentPath(fname);
		if (CssFilter.varMap.get(ppath) != null) {
			return;
		}
		String vpath = ppath + "/Variables.css";
		logger.debug("readVar=" + vpath);
		String contents = WebResourceUtil.getWebResource(vpath);
		if (contents != null) {
			CssFilter.parseVar(vpath, contents);
		}
		String json = JsonUtil.encode(CssFilter.varMap, true);
		logger.debug("CssFilter.varMap=" + json);
	}
	
	@Override
	public void doFilter(final ServletRequest req, final ServletResponse resp, final FilterChain chain) throws IOException, ServletException {
		if (req instanceof HttpServletRequest) {
			HttpServletRequest sreq = (HttpServletRequest) req;
			HttpServletResponse sresp = (HttpServletResponse) resp;
			try {
				String context = sreq.getContextPath();
				String fname = sreq.getRequestURI().substring(context.length());
				logger.debug(() -> "filename=" + fname);
				CssFilter.readVar(fname);
				String contents = CssFilter.readCss(sreq, fname);
				if (contents != null) {
					contents = CssFilter.replaceVar(fname, contents);
					sresp.setContentType("text/css; charset=utf-8");
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
			} catch (Exception e) {
				logger.error(() -> e.getMessage(), e);
			}
		}
		chain.doFilter(req, resp);
	}

}
