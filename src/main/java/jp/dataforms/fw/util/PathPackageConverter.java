package jp.dataforms.fw.util;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jp.dataforms.fw.controller.WebComponent;
import jp.dataforms.fw.util.PathPackageConverter.PathPackage;
import lombok.Getter;

/**
 *  Pathとパッケージの変換を行うクラス。
 */
public class PathPackageConverter extends ArrayList<PathPackage> {
	
	/**
	 * Logger.
	 */
	private static Logger logger = LogManager.getLogger(PathPackageConverter.class);
	
	/**
	 * Pathに対応するJavaクラスのパッケージ。
	 */
	public static class PathPackage {
		/**
		 * Path。
		 */
		@Getter
		private String path = null;
		/**
		 * 対応するパッケージ。
		 */
		@Getter
		private String basePackage = null;
		/**
		 * コンストラクタ。
		 * @param path
		 * @param basePackage
		 */
		public PathPackage(final String path, final String basePackage) {
			this.path = path;
			this.basePackage = basePackage;
		}
	}
	
	/**
	 * コンストラクタ。
	 */
	public PathPackageConverter() {
		this.add(new PathPackage("/dataforms", WebComponent.BASE_PACKAGE));
	}
	
	/**
	 * pathに対応するPathパッケージ対応表を取得します。
	 * @param path パス。
	 * @return Pathパッケージ対応表。
	 */
	private PathPackage findBasePackage(final String path) {
		PathPackage ret = null;
		for (PathPackage e: this) {
			if (path.indexOf(path) == 0) {
				ret = e;
			}
		}
		return ret;
	}
	
	/**
	 * URIに対応するクラス名を取得します。
	 * @param context コンテキスト。
	 * @param uri リクエストされたURI。
	 * @return URIに対応するクラス名。
	 */
	public String getWebComponentClass(final String context, final String uri) {
		logger.debug("context, url = " + context + "," + uri);
		String path = uri.substring(context.length() + 1);
		int idx = path.lastIndexOf(".");
		if (idx >= 0) {
			path = path.substring(0, idx);
			PathPackage p = this.findBasePackage(path);
			if (p != null) {
				path = path.substring(p.getPath().length());
				logger.debug("*** path = " + path);
				path = p.getBasePackage() + "." + path;
			}
		}
		path = path.replaceAll("/", ".");
		return path;
	}
	
	private PathPackage findPath(final String clazz) {
		PathPackage ret = null;
		for (PathPackage e: this) {
			if (clazz.indexOf(e.getBasePackage()) == 0) {
				ret = e;
			}
		}
		return ret;
	}
	
	public String getWebPath(final String clazz) {
		PathPackage p = this.findPath(clazz);
		String path = clazz;
		if (p != null) {
			path = path.substring(p.getBasePackage().length());
			path = p.getPath() + path; 
		}
		path =  path.replaceAll("\\.", "/");
		logger.debug("*** getWebPath=" + path);
		return path;
		
	}
}
