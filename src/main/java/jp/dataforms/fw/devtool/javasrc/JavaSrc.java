package jp.dataforms.fw.devtool.javasrc;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jp.dataforms.fw.util.FileUtil;

/**
 * Java source 操作クラス。
 */
public class JavaSrc {

	/**
	 * Logger.
	 */
	private static Logger logger = LogManager.getLogger(JavaSrc.class);

	/**
	 * ソースパス。
	 */
	private String srcText = null;

	/**
	 * コンストラクタ。
	 * @param srcfile パス。
	 */
	public JavaSrc(final File srcfile) {
		try {
			this.srcText = FileUtil.readTextFile(srcfile.getAbsolutePath(), "utf-8");
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
		}
	}

	/**
	 * コンストラクタ。
	 * @param text パス。
	 */
	public JavaSrc(final String text) {
		this.srcText = text;
	}

	/**
	 * 指定した位置から開始するブロックを取得します。
	 * @param pos 指定位置。
	 * @return ブロックの文字列。
	 */
	private String getBlock(int pos) {
		String ret;
		StringBuffer sb = new StringBuffer();
		int level = 0;
		for (int i = pos; i < this.srcText.length(); i++) {
			char c = this.srcText.charAt(i);
			if (c == '{') {
				level++;
			} else if (c == '}') {
				level--;
			}
			sb.append(c);
			if (level == 0) {
				break;
			}
		}
		ret = sb.toString();
		return ret;
	}

	/**
	 * 開始パターン。
	 * @param pat パターン。
	 * @return ブロック。
	 */
	private String getBlock(String pat) {
		Pattern p = Pattern.compile(pat, Pattern.MULTILINE);
		Matcher m = p.matcher(this.srcText);
		String ret = null;
		if (m.find()) {
			String g = m.group();
			int pos = m.start() + g.length() - 1;
			ret = getBlock(pos);
		} else {
			logger.debug("not found,");
		}
		return ret;
	}


	/**
	 * メソッドの本体の文字列を取得します。
	 * @param methodName メソッド名。
	 * @return メソッドの本体。
	 */
	public String getMethodBody(final String methodName) {
		String pat = methodName + "\\(.*\\).*\\{";
		return this.getBlock(pat);
	}


	/**
	 * クラスの本体の文字列を取得します。
	 * @param className クラス名。
	 * @return クラスの本体。
	 */
	public String getClassBody(final String className) {
		String pat = "class .*" + className + ".*\\{";
		return this.getBlock(pat);
	}


	/**
	 * テスト用メイン処理。
	 * @param arg コマンドライン引数。
	 */
	public static void main(String[] arg) {
		JavaSrc src = new JavaSrc(new File("C:\\eclipse\\workspaceGithub\\dataforms2\\debug\\sample\\dao\\AaaQuery.java"));
		String mbody = src.getMethodBody("AaaQuery");
		logger.debug("constctor mbody=" + mbody);
		String cbody = src.getClassBody("Entity");
		logger.debug("class body=" + cbody);

	}
}
