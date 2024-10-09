package jp.dataforms.fw.calendar;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jp.dataforms.fw.annotation.WebMethod;
import jp.dataforms.fw.exception.ApplicationError;
import jp.dataforms.fw.field.base.Field;
import jp.dataforms.fw.response.JsonResponse;
import jp.dataforms.fw.response.Response;
import jp.dataforms.fw.util.JsonUtil;
import jp.dataforms.fw.util.MessagesUtil;
import jp.dataforms.fw.util.StringUtil;
import lombok.Getter;
import lombok.Setter;

/**
 * カレンダーフィールド。
 */
public class CalendarField extends Field<java.sql.Date> {
	/**
	 * Logger.
	 */
	private static Logger logger = LogManager.getLogger(CalendarField.class);
	
	/**
	 * 現在の年月。
	 */
	private static final String ID_CURRENT_MONTH = "currentMonth";

	/**
	 * 現在の年月の表示名。
	 */
	private static final String ID_MONTH_YEAR = "monthYear";
	
	/**
	 * 日付フィールドのID。
	 */
	private static final String ID_DATE_INFO = "dateInfo";

	/**
	 * カレンダーHTMLテーブル。
	 */
	private static final String CALENDAR_HTML = """
		<table class="calendarTable">
			<thead>
				<tr>
					<th colspan="7">
						<button type="button" class="prevMonthButton">&nbsp;<&nbsp;</button>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						<span class="monthYear"></span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						<button type="button" class="nextMonthButton">&nbsp;>&nbsp;</button>
					</th>
				</tr>
				<tr>
					<th style="color:red;" class="week0"></th>
					<th class="week1"></th>
					<th class="week2"></th>
					<th class="week3"></th>
					<th class="week4"></th>
					<th class="week5"></th>
					<th style="color:blue;" class="week6"></th>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td style="color:red;">
					</td>
					<td>
					</td>
					<td>
					</td>
					<td>
					</td>
					<td>
					</td>
					<td>
					</td>
					<td style="color:blue;">
					</td>
				</tr>
				<tr>
					<td style="color:red;">
					</td>
					<td>
					</td>
					<td>
					</td>
					<td>
					</td>
					<td>
					</td>
					<td>
					</td>
					<td style="color:blue;">
					</td>
				</tr>
				<tr>
					<td style="color:red;">
					</td>
					<td>
					</td>
					<td>
					</td>
					<td>
					</td>
					<td>
					</td>
					<td>
					</td>
					<td style="color:blue;">
					</td>
				</tr>
				<tr>
					<td style="color:red;">
					</td>
					<td>
					</td>
					<td>
					</td>
					<td>
					</td>
					<td>
					</td>
					<td>
					</td>
					<td style="color:blue;">
					</td>
				</tr>
				<tr class="line5">
					<td style="color:red;">
					</td>
					<td>
					</td>
					<td>
					</td>
					<td>
					</td>
					<td>
					</td>
					<td>
					</td>
					<td style="color:blue;">
					</td>
				</tr>
				<tr class="line6">
					<td style="color:red;">
					</td>
					<td>
					</td>
					<td>
					</td>
					<td>
					</td>
					<td>
					</td>
					<td>
					</td>
					<td style="color:blue;">
					</td>
				</tr>
			</tbody>
		</table>
	""";
	
	
	/**
	 * 現在月。
	 */
	@Setter
	@Getter
	private Integer currentMonth = null;
	
	/**
	 * 現在年。
	 */
	@Getter
	@Setter
	private int currentYear = 0;
	
	/**
	 * 年月のフォーマット。
	 */
	@Setter
	private String yearMonthFormat = "yyyy/MM";
	
	/**
	 * カレンダーのロケール指定。
	 */
	@Setter
	private Locale locale = Locale.JAPAN;
	
	/**
	 * 日付フォーマット。
	 */
	private String dateFormat = null;
	
	
	/**
	 * カレンダーのセル数。
	 */
	private static final int CALENDAR_CELLS = (7 * 6);
	
	/**
	 * コンストラクタ。
	 */
	public CalendarField() {
		this(null);
	}
	
	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public CalendarField(final String id) {
		super(id);
		this.setDateFormat("format.datefield");
	}
	

	/**
	 * 日付フォーマットを取得します。
	 * @return 日付フォーマット。
	 */
	public String getDateFormat() {
		return dateFormat;
	}

	/**
	 * 日付フォーマットを設定します。
	 * @param dateFormat 日付フォーマット。
	 * @return 設定したフィールド。
	 *
	 */
	public CalendarField setDateFormat(final String dateFormat) {
		this.dateFormat = dateFormat;
		return this;
	}
	
	
	/**
	 * Formの初期化処理。
	 */
	@Override
	public void init() throws Exception {
		super.init();
//		java.sql.Date today = this.getFirstDateOfManth(DateTimeUtil.getCurrentDate());
//		Map<String, Object> map = this.getCalendarInfo(today);
	}
	

	/**
	 * {@inheritDoc}
	 * <pre>
	 * 日付フォーマットで入力された文字列をjava.sql.Dataに変換します。
	 * </pre>
	 */
	@Override
	public void setClientValue(final Object v) {
		if (StringUtil.isBlank(v)) {
			this.setValue(null);
			return;
		}
		String dfmt = MessagesUtil.getMessage(this.getWebEntryPoint(), this.getDateFormat());
		SimpleDateFormat fmt = new SimpleDateFormat(dfmt);
		try {
			Date d = new Date(fmt.parse((String) v).getTime());
			this.setValue(d);
		} catch (ParseException e) {
			logger.error(e.getMessage(), e);
			throw new ApplicationError(e);
		}
	}

	/**
	 * {@inheritDoc}
	 * <pre>
	 * java.sql.Dataを日付フォーマットの文字列に変換します。
	 * </pre>
	 */
	@Override
	public Object getClientValue() {
		String dfmt = MessagesUtil.getMessage(this.getWebEntryPoint(), this.getDateFormat()); // , "format.datefield");
		SimpleDateFormat fmt = new SimpleDateFormat(dfmt);
		if (this.getValue() != null) {
			return fmt.format(this.getValue());
		} else {
			return null;
		}
	}
	
	/**
	 * 各日の情報を取得します。
	 * @param d 日。
	 * @return 日の情報。
	 * @throws Exception 例外。
	 */
	protected Map<String, Object> getDateInfo(final java.sql.Date d) throws Exception {
		Map<String, Object> ret = new HashMap<String, Object>();
		ret.put("date", d);
		Calendar ical = Calendar.getInstance();
		ical.setTime(d);
		ret.put("day", ical.get(Calendar.DATE));
		return ret;
	}
	
	/**
	 * カレンダー情報を作成する。
	 * @param currentDate 現在の日付。
	 * @return カレンダー情報。
	 * @throws Exception 例外。
	 */
	private Map<String, Object> getCalendarInfo(final java.sql.Date currentDate) throws Exception {
		Map<String, Object> ret = new HashMap<String, Object>();
		ret.put(ID_CURRENT_MONTH, currentDate);
		SimpleDateFormat fmt = new SimpleDateFormat(this.yearMonthFormat, this.locale);
		String text = fmt.format(currentDate);
		ret.put(ID_MONTH_YEAR, text);
		logger.debug("month text=" + text);
		Calendar cal = Calendar.getInstance();
		cal.setTime(currentDate);
		
		int week = cal.get(Calendar.DAY_OF_WEEK) - 1;
		int lastDate = cal.getActualMaximum(Calendar.DATE);
		for (int i = 0; i < CALENDAR_CELLS; i++) {
			int date = (i - week) + 1;
			if (date < 1 || lastDate < date) {
				ret.put(ID_DATE_INFO + i, null);
			} else {
				Calendar ical = Calendar.getInstance();
				ical.setTime(currentDate);
				ical.add(Calendar.DATE, (date - 1));
				java.sql.Date d = new java.sql.Date(ical.getTime().getTime());
				Map<String, Object> info = this.getDateInfo(d);
				ret.put(ID_DATE_INFO + i, info);
			}
		}
		return ret;
	}
	
	
	/**
	 * 指定した日の月内の1日を取得する。
	 * @param date 日付。
	 * @return 指定した日の月内の1日。
	 */
	private java.sql.Date getFirstDateOfManth(final java.sql.Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.DATE, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		java.sql.Date ret = new java.sql.Date(cal.getTime().getTime());
		return ret;
	}
	
	/**
	 * カレンダ情報マップを作成する。
	 * @param p パラメータ。
	 * @return カレンダ情報マップ。
	 * @throws Exception 例外。
	 */
	@WebMethod
	public Response getCalenderInfo(final Map<String, Object> p) throws Exception {
		logger.debug("p=" + JsonUtil.encode(p));
		String d = (String) p.get("date");
		this.setClientValue(d);
		java.sql.Date date = this.getValue();
		logger.debug("currentMonth=" + date);
		java.sql.Date currentMonth = this.getFirstDateOfManth(date);
		Map<String, Object> ret = this.getCalendarInfo(currentMonth);
		Response resp = new JsonResponse(JsonResponse.SUCCESS, ret);
		return resp;
	}
	
	
	@Override
	public Map<String, Object> getProperties() throws Exception {
		Locale loc = this.getPage().getRequest().getLocale();
		logger.debug("loc=" + loc);
		Calendar cal = Calendar.getInstance(loc);
		cal.set(Calendar.YEAR, 2024);
		cal.set(Calendar.MONTH, 8);
		cal.set(Calendar.DATE, 29); // 日曜日の日付
		SimpleDateFormat fmt = new SimpleDateFormat("E", loc);
		ArrayList<String> weekList = new ArrayList<String>();
		for (int i = 0; i < 7; i++) {
			java.util.Date d = cal.getTime();
			String week = fmt.format(d);
			logger.debug("week=" + week);
			weekList.add(week);
			cal.add(Calendar.DATE, 1);
		}
		Map<String, Object> prop =  super.getProperties();
		prop.put("dateFormat", this.getDateFormat());
		prop.put("calendarHtml", CALENDAR_HTML);
		prop.put("weekList", weekList);
		return prop;
	}
}
