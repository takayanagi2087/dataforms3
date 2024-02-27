package jp.dataforms.fw.field.sqltype;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jp.dataforms.fw.dao.sqldatatype.SqlTimestamp;
import jp.dataforms.fw.exception.ApplicationError;
import jp.dataforms.fw.field.base.DateTimeField;
import jp.dataforms.fw.util.MessagesUtil;
import jp.dataforms.fw.util.StringUtil;
import jp.dataforms.fw.validator.TimestampValidator;

/**
 * 日時フィールドクラス。
 *
 */
public class TimestampField extends DateTimeField<Timestamp> implements SqlTimestamp {
    /**
     * Logger.
     */
    private static Logger logger = LogManager.getLogger(TimestampField.class.getName());


	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public TimestampField(final String id) {
		super(id);
		this.addValidator(new TimestampValidator());
		this.setDateFormat("format.timestampfield");
	}


	/**
	 * {@inheritDoc}
	 * <pre>
	 * 入力された文字列をjava.sql.Timestampに変換します。
	 * </pre>
	 */
	@Override
	public void setClientValue(final Object v) {
		if (StringUtil.isBlank(v)) {
			this.setValue(null);
			return;
		}
		SimpleDateFormat fmt = new SimpleDateFormat(MessagesUtil.getMessage(this.getWebEntryPoint(), this.getDateFormat()));
		try {
			Timestamp t = new Timestamp(fmt.parse((String) v).getTime());
			this.setValue(t);
		} catch (ParseException e) {
			logger.error(e.getMessage(), e);
			throw new ApplicationError(e);
		}
	}

	/**
	 * {@inheritDoc}
	 * <pre>
	 * java.sql.Timestampを日付時刻フォーマット形式の文字列に変換します。
	 * </pre>
	 */
	@Override
	public String getClientValue() {
		if (this.getValue() == null) {
			return null;
		}
		SimpleDateFormat fmt = new SimpleDateFormat(MessagesUtil.getMessage(this.getWebEntryPoint(), this.getDateFormat()));
		return fmt.format(this.getValue());
	}
}
