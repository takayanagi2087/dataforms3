package jp.dataforms.fw.validator;

import jp.dataforms.fw.util.MessagesUtil;


/**
 * 日付バリデータクラス。
 *
 */
public class  DateValidator extends DateTimeValidator {
	/**
	 * コンストラクタ。
	 */
	public DateValidator() {
		super("error.date");
	}

	@Override
	public boolean validate(final Object value) {
		String format = MessagesUtil.getMessage(this.getPage(), "format.datefield");
		this.setDateFormat(format);
		return super.validate(value);
	}

}

