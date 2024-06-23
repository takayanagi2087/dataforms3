package jp.dataforms.fw.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * HTMLユーティリティ。
 */
public final class HtmlUtil {
	
	
	/**
	 * コンストラクタ。
	 */
	private HtmlUtil() {
		
	}
	
	/**
	 * HTMLのタイトルを取得します。
	 * @param html HTML文字列。
	 * @return htmlのタイトル。
	 */
	public static String getTitle(final String html) {
		String title = null;
		Pattern p = Pattern.compile("<title>(.*)</title>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
		Matcher m = p.matcher(html);
		if (m.find()) {
			title = m.group(1);
		}
		return title;
	}
	
//	

	/**
	 * HTMLの説明を取得します。
	 * @param html HTML文字列。
	 * @return htmlのタイトル。
	 */
	public static String getDescription(final String html) {
		String title = null;
		Pattern p = Pattern.compile("<meta name=['\"]description['\"] content=['\"](.*)['\"]>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
		Matcher m = p.matcher(html);
		if (m.find()) {
			title = m.group(1);
		}
		return title;
	}
	
}
