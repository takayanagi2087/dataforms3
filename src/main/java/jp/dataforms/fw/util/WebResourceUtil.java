package jp.dataforms.fw.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jp.dataforms.fw.controller.Page;
import jp.dataforms.fw.servlet.DataFormsServlet;

/**
 * 各種Webリソース読み込みユーティリティ。
 */
public final class WebResourceUtil {
	/**
	 * Logger.
	 */
	private static Logger logger =  LogManager.getLogger(WebResourceUtil.class);
	
	/**
	 * Webリソースのキャッシュ。
	 */
	private static Map<String, String> webResourceCache = Collections.synchronizedMap(new HashMap<String, String>());

	/**
	 * Webリソースのタイムスタンプキャッシュ。
	 */
	private static Map<String, String> webResourceTimestampCache = Collections.synchronizedMap(new HashMap<String, String>());

	/**
	 * Webリソースのタイムスタンプキャッシュ。
	 */
	private static Map<String, Long> webResourceTimestampLongCache = Collections.synchronizedMap(new HashMap<String, Long>());

	/**
	 * コンストラクタ。
	 */
	private WebResourceUtil() {
		
	}
	
	/**
	 * 実ファイルを読み込みます。
	 * @param rpath 実ファイルのパス。
	 * @return ファイルの内容。
	 * @throws Exception 例外。
	 */
	private static String readRealFile(final String rpath) throws Exception {
		String ret = null;
		try (InputStream is = new FileInputStream(rpath)) {
			byte[] b = FileUtil.readInputStream(is);
			ret = new String(b, DataFormsServlet.getEncoding());
		}
		return ret;
	}


	/**
	 * Jarからファイルを読み込みます。
	 * @param path ファイルのバス。
	 * @return ファイルの内容。
	 * @throws Exception 例外。
	 */
	private static String readJarFile(final String path) throws Exception {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		URL url = classLoader.getResource("/META-INF/resources" + path);
		logger.debug("readJarFile path=" + path);
		logger.debug("readJarFile url=" + url);
		String ret = null;
		if (url != null) {
			JarURLConnection conn = (JarURLConnection) url.openConnection();
			long d = conn.getLastModified();
			webResourceTimestampLongCache.put(path, d);
			SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmss");
			webResourceTimestampCache.put(path, fmt.format(new Date(d)));
			try (InputStream is = conn.getInputStream()) {
				byte[] b = FileUtil.readInputStream(is);
				ret = new String(b, DataFormsServlet.getEncoding());
			}
		}
		return ret;
	}


	/**
	 * ファイルの内容を取得する。
	 * @param path パス。
	 * @return ファイルの内容。
	 * @throws Exception 例外。
	 */
	private static String readAppFile(final String path) throws Exception {
		String ret = null;
		String rpath = Page.getServlet().getServletContext().getRealPath(path);
		logger.debug("readFile rpath=" + rpath);
		if (rpath != null) {
			File f = new File(rpath);
			if (f.exists()) {
				long d = f.lastModified();
				webResourceTimestampLongCache.put(path, d);
				SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmss");
				webResourceTimestampCache.put(path, fmt.format(new Date(d)));
				ret = WebResourceUtil.readRealFile(rpath);
			} else {
				ret = WebResourceUtil.readJarFile(path);
			}
		} else {
			ret = WebResourceUtil.readJarFile(path);
		}
		return ret;
	}


	/**
	 * Webリソースを読み込みます。
	 * @param path リソースのパス。
	 * @return Webリソースの文字列。
	 * @throws Exception 例外。
	 */
	private static String readWebResource(final String path) throws Exception {
		logger.debug("readWebResource path=" + path);
		String ret =  WebResourceUtil.readAppFile(path);
		logger.debug("ret=" + ret);
		if (ret == null) {
			ret = "";
		}
		return ret;
	}

	
	/**
	 * html,js,css等のWEBリソースを取得します。
	 * @param path パス。
	 * @return リソース。
	 * @throws Exception 例外。
	 */
	public static String getWebResource(final String path) throws Exception {
		if (!WebResourceUtil.webResourceCache.containsKey(path)) {
			String ret = WebResourceUtil.readWebResource(path);
			WebResourceUtil.webResourceCache.put(path, ret);
			//log.debug("getWebResource:text=" + ret);
		}
		String ret = WebResourceUtil.webResourceCache.get(path);
		return ret;
	}

	/**
	 * 指定されたパスのWebリソースの更新日付を取得します。
	 * @param path パス。
	 * @return 更新日付(yyyyMMddHHmmss形式)。
	 * @throws Exception 例外。
	 */
	public static String getLastUpdate(final String path) throws Exception {
		String ret = webResourceTimestampCache.get(path);
		if (ret == null) {
			WebResourceUtil.readWebResource(path);
			ret = webResourceTimestampCache.get(path);
		}
		return ret;
	}

	/**
	 * Long形式の更新日時を所得します。
	 * @param path パス。
	 * @return 最終更新日時。
	 * @throws Exception 例外。
	 */
	public static Long getLastUpdateLong(final String path) throws Exception {
		Long ret = webResourceTimestampLongCache.get(path);
		if (ret == null) {
			WebResourceUtil.readWebResource(path);
			ret = webResourceTimestampLongCache.get(path);
		}
		return ret;
	}
	
	/**
	 * ファイルの集合を取得する。
	 * @param basePath 基本パス。
	 * @param name ファイル名。
	 * @param set ファイルの集合。
	 * @throws IOException 例外。
	 */
	private static void getFileList(final String basePath, final String name, Set<String> set) throws IOException {
		String rpath = Page.getServlet().getServletContext().getRealPath(basePath);
		logger.debug("getFileList rpath=" + rpath);
		Path p = Paths.get(rpath);
		Files.walk(p).forEach((e) -> {
			if (e.toFile().isFile()) {
				if (name.equals(e.toFile().getName())) {
					logger.debug("getFileList path=" + e.toFile().getAbsolutePath() + ", isFile=" + e.toFile().isFile());
					String webpath = e.toFile().getAbsolutePath().substring(rpath.length());
					webpath = basePath + webpath.replaceAll("\\\\", "/");
					logger.debug("getFilesList webpath=" + webpath);
					set.add(webpath);
				}
			}
		});
	}

	/**
	 * ファイルの集合をJarファイルから取得する。
	 * @param basePath 基本パス。
	 * @param name ファイル名。
	 * @param set ファイルの集合。
	 * @throws IOException 例外。
	 */
	private static void getFileListFormJar(final String basePath, final String name, Set<String> set)
			throws IOException {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		String respath = "/META-INF/resources" + basePath;
		URL url = classLoader.getResource(respath);
		logger.debug("getFilesList url=" + url);
		JarURLConnection jarUrlConnection = (JarURLConnection) url.openConnection();
		JarFile jarFile = jarUrlConnection.getJarFile();
		try {
			Enumeration<JarEntry> jarEnum = jarFile.entries();
			while (jarEnum.hasMoreElements()) {
				JarEntry jarEntry = jarEnum.nextElement();
				String path = "/" + jarEntry.getName();
				if (path.startsWith(respath)) {
					if (!jarEntry.isDirectory()) {
						if (Pattern.matches(".*/" + name + "$", path)) {
							set.add(path.substring("/META-INF/resources".length()));
						}
					}
				}
			}
		} finally {
			jarFile.close();
		}
	}

	/**
	 * 基本パス以下の指定ファイル名の一覧を取得します。
	 * @param basePath 基本パス。
	 * @param name ファイル名。
	 * @return ファイルリスト。
	 * @throws Exception 例外。
	 */
	public static Set<String> getFileList(final String basePath, final String name) throws Exception {
		Set<String> set = new HashSet<String>();
		try {
			WebResourceUtil.getFileList(basePath, name, set);
		} catch (Exception ex) {
			logger.error(ex.getMessage());
		}
		// 
		WebResourceUtil.getFileListFormJar(basePath, name, set);
		return set;
	}




}
