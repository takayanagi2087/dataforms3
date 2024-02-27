package jp.dataforms.fw.servlet;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jp.dataforms.fw.util.FileUtil;
import jp.dataforms.fw.util.StringUtil;


/**
 * BABELスクリプト変換フィルター。
 *
 */
@WebFilter(filterName="babel-filter", urlPatterns = "*.jsb")
public class BabelFilter extends DataFormsFilter implements Filter {
	/**
	 * Logger.
	 */
	private static Logger logger = LogManager.getLogger(BabelFilter.class);

	/**
	 * ES6をIEで実行できるように変換する。
	 * @param sreq 要求情報。
	 * @param es6js ES6のスクリプト。
	 * @return ES5への変換結果。
	 * @throws Exception 例外。
	 */
	private String convertToES5(final HttpServletRequest sreq, final String es6js) throws Exception {
		String babel = DataFormsServlet.getBabelCommand();
		Process p = Runtime.getRuntime().exec(babel);
		try (OutputStream os = p.getOutputStream()) {
			FileUtil.writeOutputStream(es6js.getBytes(DataFormsServlet.getEncoding()), os);
		}
		String contents = null;
		try (InputStream is = p.getInputStream()) {
			byte [] buf = FileUtil.readInputStream(is);
			contents = new String(buf, DataFormsServlet.getEncoding());
		}
//		logger.debug("es5script={}", contents);
		return contents;
	}

	/**
	 * ES5対応スクリプトを取得します。
	 * @param sreq 要求情報。
	 * @param fname ファイル名。
	 * @return ES5対応スクリプト。
	 * @throws Exception 例外。
	 */
	private String getES5Script(final HttpServletRequest sreq, final String fname) throws Exception {
		String path = DataFormsServlet.getBabelWork();
		if (StringUtil.isBlank(path)) {
			return this.convertToES5(sreq, fname);
		} else {
			File dir = new File(path);
			// 作業領域が無い場合作成。
			if (!dir.exists()) {
				dir.mkdirs();
			}
			String es5scriptPath = path + fname;
			File es5script = new File(es5scriptPath);
			if (!es5script.exists()) {
				// 変換スクリプトが存在しない。
				// 親ディレクトリがない場合作成。
				if (!es5script.getParentFile().exists()) {
					es5script.getParentFile().mkdirs();
				}
				String es6js = this.readWebResource(sreq, fname);
				String es5text = this.convertToES5(sreq, es6js);
				FileUtil.writeTextFile(es5scriptPath, es5text, DataFormsServlet.getEncoding());
				return es5text;
			} else {
				String es6js = this.readWebResource(sreq, fname);
//				logger.debug("timestamp={},{}", es5script.lastModified(), this.getLastUpdate(fname));
				if (es5script.lastModified() > this.getLastUpdate(fname)) {
					String es5text = FileUtil.readTextFile(es5scriptPath, DataFormsServlet.getEncoding());
					return es5text;
				} else {
					String es5text = this.convertToES5(sreq, es6js);
					FileUtil.writeTextFile(es5scriptPath, es5text, DataFormsServlet.getEncoding());
					return es5text;
				}
			}
		}
	}


	/**
	 * JS Map.
	 */
	private static Map<String, String> jsMap = Collections.synchronizedMap(new HashMap<String, String>());


	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
		if (req instanceof HttpServletRequest) {
			HttpServletRequest sreq = (HttpServletRequest) req;
			try {
				String fname = sreq.getRequestURI().replaceAll("\\.jsb$", ".js");
				logger.debug(() -> "filename=" + fname);
				String contents = BabelFilter.jsMap.get(fname);
				if (contents == null) {
					contents = this.getES5Script(sreq, fname);
					BabelFilter.jsMap.put(fname, contents);
				}
				resp.setContentType("text/css; charset=utf-8");
				try (PrintWriter out = resp.getWriter()) {
					out.print(contents);
				}
			} catch (Exception e) {
				logger.error(() -> e.getMessage(), e);
			}
		}
	}

}
