package jp.dataforms.fw.field.sqltype;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jp.dataforms.fw.dao.sqldatatype.SqlDate;
import jp.dataforms.fw.exception.ApplicationError;
import jp.dataforms.fw.field.base.DateTimeField;
import jp.dataforms.fw.util.MessagesUtil;
import jp.dataforms.fw.util.StringUtil;
import jp.dataforms.fw.validator.DateValidator;

/**
 * 日付フィールドクラス。
 *
 */
public class DateField extends DateTimeField<Date> implements SqlDate {
    /**
     * Logger.
     */
    private static Logger logger = LogManager.getLogger(DateField.class.getName());

    /**
     * Datepickerを有効フラグします。
     */
    private boolean datepickerEnabled = true;

	/**
	 * コンストラクタ。
	 * @param id ID。
	 */
	public DateField(final String id) {
		super(id);
		this.addValidator(new DateValidator());
		this.setDateFormat("format.datefield");
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
		String dfmt = MessagesUtil.getMessage(this.getPage(), this.getDateFormat());
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
		String dfmt = MessagesUtil.getMessage(this.getPage(), this.getDateFormat()); // , "format.datefield");
		SimpleDateFormat fmt = new SimpleDateFormat(dfmt);
		if (this.getValue() != null) {
			return fmt.format(this.getValue());
		} else {
			return null;
		}
	}

	/**
	 * Datepickerの有効/無効を取得します。
	 * @return 有効な場合true。
	 */
	public boolean isDatepickerEnabled() {
		return datepickerEnabled;
	}


	/**
	 * Datepickerの有効/無効を設定します。
	 * @param datepickerEnabled 有効な場合true。
	 */
	public void setDatepickerEnabled(final boolean datepickerEnabled) {
		this.datepickerEnabled = datepickerEnabled;
	}

	@Override
	public Map<String, Object> getProperties() throws Exception {
		Map<String, Object> ret = super.getProperties();
		ret.put("datepickerEnabled", this.datepickerEnabled);
		return ret;
	}

}
