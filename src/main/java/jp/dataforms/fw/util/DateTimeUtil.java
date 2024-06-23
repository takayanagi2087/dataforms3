package jp.dataforms.fw.util;

import java.util.Calendar;

/**
 * 日付時刻ユーティリティ。
 *
 */
public final class DateTimeUtil {
	
	/**
	 * コンストラクタ。
	 */
	private DateTimeUtil() {
		
	}
	
	/**
	 * 現在の日付を取得します。
	 * @return 現在の日付。
	 */
	public static java.sql.Date getCurrentDate() {
		java.util.Date date = new java.util.Date();
		java.sql.Date ret = new java.sql.Date(date.getTime());
		return ret;
	}

	/**
	 * 現在の日時を取得します。
	 * @return 現在の日時。
	 */
	public static java.sql.Timestamp getCurrentTimestamp() {
		java.util.Date date = new java.util.Date();
		java.sql.Timestamp ret = new java.sql.Timestamp(date.getTime());
		return ret;
	}

	/**
	 * 現在の時刻を取得します。
	 * @return 現在の時刻。
	 */
	public static java.sql.Time getCurrentTime() {
		java.util.Date date = new java.util.Date();
		java.sql.Time ret = new java.sql.Time(date.getTime());
		return ret;
	}

	/**
	 * 現在日時をCalendar形式で取得。
	 * @return Calendar。
	 */
	public static Calendar getCurrentCalendar() {
		java.util.Date date = new java.util.Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal;
	}

	/**
	 * Calendarからjava.sql.Dateを取得する。
	 * @param cal java.util.Calendar。
	 * @return java.sql.Date。
	 */
	public static java.sql.Date getDate(final java.util.Calendar cal) {
		java.sql.Date ret = new java.sql.Date(cal.getTime().getTime());
		return ret;
	}

	/**
	 * Calendarからjava.sql.Dateを取得する。
	 * @param cal java.util.Calendar。
	 * @return java.sql.Timestamp。
	 */
	public static java.sql.Time getTimestamp(final java.util.Calendar cal) {
		java.sql.Time ret = new java.sql.Time(cal.getTime().getTime());
		return ret;
	}

	/**
	 * Calendarからjava.sql.Dateを取得する。
	 * @param cal java.util.Calendar。
	 * @return java.sql.Times。
	 */
	public static java.sql.Time getTime(final java.util.Calendar cal) {
		java.sql.Time ret = new java.sql.Time(cal.getTime().getTime());
		return ret;
	}

}
