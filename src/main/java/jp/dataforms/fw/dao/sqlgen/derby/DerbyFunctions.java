package jp.dataforms.fw.dao.sqlgen.derby;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// 日付の部分取得
// select extract_field('month',timestamp(test_date, '00:00:00')) y from data_type
//　日付の加算
// select date({fn timestampadd(sql_tsi_day, 3, timestamp(current_date, '00:00:00'))}) from sysibm.sysdummy1


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
/*			if (lowerFlags.contains("s")) {
				compileFlags |= Pattern.DOTALL;
			}*/
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
			return ret;
		} catch (Exception e) {
			return source;
		}
	}
	
	/**
	 * Apache Derby用：日時から特定の要素を抽出するカスタム関数。
	 * 
	 * @param unit 抽出したい単位 ('YEAR', 'MONTH', 'DAY', 'HOUR', 'MINUTE', 'SECOND')
	 * @param target 対象の日時データ
	 * @return 抽出された数値（対象または単位が空なら null）
	 */
	public static Integer extractField(String unit, java.sql.Timestamp target) {
		if (target == null || unit == null) {
			return null;
		}

		Calendar cal = Calendar.getInstance();
		cal.setTime(target);

		switch (unit.toUpperCase()) {
			case "YEAR":
				return cal.get(Calendar.YEAR);
			case "MONTH":
				// Calendar.MONTHは0から始まるため、SQLの仕様(1〜12)に合わせて+1する
				return cal.get(Calendar.MONTH) + 1;
			case "DAY":
				return cal.get(Calendar.DAY_OF_MONTH);
			case "HOUR":
				return cal.get(Calendar.HOUR_OF_DAY);
			case "MINUTE":
				return cal.get(Calendar.MINUTE);
			case "SECOND":
				return cal.get(Calendar.SECOND);
			default:
				throw new IllegalArgumentException("Unsupported unit: " + unit);
		}
	}
	
	/**
	 * Apache Derby用：日時に特定の期間を加減算するカスタム関数。
	 * 
	 * @param unit  単位 ('YEAR', 'MONTH', 'DAY', 'HOUR', 'MINUTE', 'SECOND')
	 * @param value 加算する量（負の数であれば減算）
	 * @param target 対象の日時データ
	 * @return 計算後のTimestamp（入力がnullならnull）
	 */
	public static Timestamp dateAdd(String unit, Integer value, Timestamp target) {
		if (target == null || unit == null || value == null) {
			return null;
		}

		Calendar cal = Calendar.getInstance();
		cal.setTime(target);

		switch (unit.toUpperCase()) {
		case "YEAR":
			cal.add(Calendar.YEAR, value);
			break;
		case "MONTH":
			cal.add(Calendar.MONTH, value);
			break;
		case "DAY":
			cal.add(Calendar.DATE, value);
			break;
		case "HOUR":
			cal.add(Calendar.HOUR_OF_DAY, value);
			break;
		case "MINUTE":
			cal.add(Calendar.MINUTE, value);
			break;
		case "SECOND":
			cal.add(Calendar.SECOND, value);
			break;
		default:
			throw new IllegalArgumentException("Unsupported unit: " + unit);
		}

		return new Timestamp(cal.getTimeInMillis());
	}
	
}
