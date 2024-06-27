package jp.dataforms.fw.devtool.javasrc;

import java.io.InputStream;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jp.dataforms.fw.controller.Form;
import jp.dataforms.fw.util.FileUtil;

/**
 * Javaソースジェネレータの基本クラス。
 */
public abstract class JavaSrcGenerator {
	/**
	 * Logger.
	 */
	private static Logger logger = LogManager.getLogger(JavaSrcGenerator.class);

	/**
	 * コンストラクタ。
	 */
	public JavaSrcGenerator() {
		
	}
	
	/**
	 * テンプレートを取得します。
	 * @return テンプレート。
	 * @throws Exception 例外。
	 */
	protected abstract Template getTemplate() throws Exception;

	/**
	 * ソースを生成します。
	 * @param form フォーム。
	 * @param data POSTされたデータ。
	 * @throws Exception 例外。
	 */
	public abstract void generage(final Form form, final Map<String, Object> data) throws Exception;


	/**
	 * ソーステンプレート。
	 */
	public static class Template {
		/**
		 * ソース。
		 */
		private String src = null;

		/**
		 * コンストラクタ。
		 * @param src ソースの文字列。
		 */
		public Template(final String src) {
			this.src = src;
		}

		/**
		 * コンストラクタ。
		 * @param cls 起点となるクラス。
		 * @param path リソースのパス。
		 * @throws Exception 例外。
		 */
		public Template(final Class<?> cls, final String path) throws Exception {
			this.src = this.getStringResourse(cls, path);
		}

		/**
		 * 指定された文字列リソースを取得します。
		 * @param cls クラス。
		 * @param path リソースパス。
		 * @return 文字列。
		 * @throws Exception 例外。
		 */
		protected String getStringResourse(final Class<?> cls, final String path) throws Exception {
			logger.info("getResource:" + cls.getName() + "," + path);
			InputStream is = cls.getResourceAsStream(path);
			String text = new String(FileUtil.readInputStream(is), "utf-8");
			return text;
		}

		/**
		 * テンプレートの文字列を置き換えます。
		 * @param key 置き換える文字列のキー。
		 * @param value 置き換える文字列。
		 */
		public void replace(final String key, final String value) {
			this.src = this.src.replaceAll("\\$\\{" + key + "\\}", value);
		}

		/**
		 * ソースファイルを取得します。
		 * @return ソースファイル。
		 */
		public String getSource() {
			return this.src;
		}
	}

}
