package jp.dataforms.fw.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 相対パス、絶対パス変換ユーティリティクラス。
 */
public final class PathUtil {

	/**
	 * Logger.
	 */
	private static Logger logger = LogManager.getLogger(PathUtil.class);
	/**
	 * コンストラクタ。
	 */
	private PathUtil() {
		
	}
	
	/**
	 * "../"のパターンが幾つあったかを数えます。
	 * @param sp pathを分解した配列。
	 * @return "../"の数。
	 */
	private static int countParent(final String[] sp) {
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
	public static String getAbsolutePath(final String bpath, final String path) {
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
			int pcnt = PathUtil.countParent(sp);
			
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
	 * 指定された元になるパスに対する絶対パスの相対パスを作成します。
	 * @param bpath 元になるパス
	 * @param path 絶対パス。
	 * @return pathを相対パスに変換したもの。
	 */
	public static String getRelativePath(final String bpath, final String path) {
		String[] ssp = path.split("/");
		String[] sp = bpath.split("/");
		int sidx = 0;
		StringBuilder psb = new StringBuilder();
		for (int i = 0; i < ssp.length && i < sp.length; i++) {
			if (!ssp[i].equals(sp[i])) {
				sidx = i;
				break;
			}
		}
		for (int i = sidx; i < sp.length - 1; i++) {
			psb.append("../");
		}
		if (psb.length() == 0) {
			psb.append("./");
		}
		for (int i = sidx; i < ssp.length; i++) {
			if (i > sidx) {
				psb.append("/");
			}
			psb.append(ssp[i]);
		}
		return psb.toString();
	}
}
