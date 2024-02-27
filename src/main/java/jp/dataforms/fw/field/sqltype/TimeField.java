package jp.dataforms.fw.field.sqltype;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jp.dataforms.fw.dao.sqldatatype.SqlTime;
import jp.dataforms.fw.exception.ApplicationError;
import jp.dataforms.fw.field.base.DateTimeField;
import jp.dataforms.fw.util.MessagesUtil;
import jp.dataforms.fw.util.StringUtil;
import jp.dataforms.fw.validator.TimeValidator;

/**
 * 時間フィールドクラス。
 *
 */
public class TimeField extends DateTimeField<Time> implements SqlTime {
    /**
     * Logger.
     */
    private static Logger logger = LogManager.getLogger(TimeField.class.getName());


	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public TimeField(final String id) {
		super(id);
		this.addValidator(new TimeValidator());
		this.setDateFormat("format.timefield");
	}


	/**
	 * {@inheritDoc}
	 * <pre>
	 * 時刻フォーマットで入力された文字列を、java.sql.Timeに変換します。
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
			Time t = new Time(fmt.parse((String) v).getTime());
			this.setValue(t);
		} catch (ParseException e) {
			logger.error(e.getMessage(), e);
			throw new ApplicationError(e);
		}
	}

	/**
	 * {@inheritDoc}
	 * <pre>
	 * java.sql.Timeを時刻フォーマットの文字列に変換します。
	 * </pre>
	 */
	@Override
	public Object getClientValue() {
		if (this.getValue() != null) {
			String dfmt = MessagesUtil.getMessage(this.getPage(), this.getDateFormat());
			SimpleDateFormat fmt = new SimpleDateFormat(dfmt);
			return fmt.format(this.getValue());
		} else {
			return this.getValue();
		}
	}

}
