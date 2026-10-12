package jp.dataforms.fw.dao.sqlgen.derby;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Apache derbyがサポートしていない関数提供クラス。
 */
public class DerbyFunctions {
	
	/**
	 * Logger.
	 */
	private static Logger logger = LogManager.getLogger(DerbyFunctions.class);
	
	/**
	 * コンストラクタ。
	 */
	public DerbyFunctions() {
	}

	/**
	 * REGEXP_LIKE の実体となるJavaメソッド。
	 * 
	 * @param source 対象の文字列。
	 * @param pattern 正規表現パターン
	 * @param flags マッチング引数 (例: 'i', 'm', 's'、複数指定時は 'im' など)。む
	 * @return マッチした場合は true、しない場合は false。いずれかが null の場合は null。
	 */
	public static Boolean regexpLike(String source, String pattern, String flags) {
		// 引数のいずれかが null の場合は SQLの仕様に従い null を返す
		if (source == null || pattern == null) {
			return null;
		}

		int compileFlags = 0;

		// フラグ文字列が指定されている場合の処理
		if (flags != null && !flags.trim().isEmpty()) {
			String lowerFlags = flags.toLowerCase();
			if (lowerFlags.contains("i")) {
				compileFlags |= Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE;
			}
			if (lowerFlags.contains("m")) {
				compileFlags |= Pattern.MULTILINE;
			}
			if (lowerFlags.contains("s")) {
				compileFlags |= Pattern.DOTALL;
			}
		}

		try {
			// パターンをコンパイルしてマッチングを実行
			Pattern p = Pattern.compile(pattern, compileFlags);
			// 部分一致で判定（多くのRDBMSのREGEXP_LIKEに挙動を合わせるため find() を使用）
			return p.matcher(source).find();
		} catch (PatternSyntaxException e) {
			// 正規表現の構文エラーがある場合は false を返すか、必要に応じて例外をスロー
			return false;
		}
	}

	/**
	 * REGEXP_REPLACEの実体となるJavaメソッド。
	 * @param source　元の戻列。
	 * @param pattern　パターン。
	 * @param replacement　置き換える文字列。
	 * @return 置き換え結果。
	 */
	public static String regexpReplace(String source, String pattern, String replacement) {
		logger.debug("regexpReplace=" +  source + "," +  pattern + "," + replacement);
		if (source == null || pattern == null || replacement == null) {
			return null;
		}
		try {
			// $1, $2 だけでなく Oracle互換の \1, \2 も自動置換
			// String javaRep = replacement.replaceAll("\\\\([0-9])", "\\$$1");
			String ret = source.replaceAll(pattern, replacement);
			logger.debug("ret=" + ret);
			return ret;
		} catch (Exception e) {
			return source;
		}
	}
}
