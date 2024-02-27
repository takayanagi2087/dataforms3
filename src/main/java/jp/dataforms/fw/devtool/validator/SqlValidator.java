package jp.dataforms.fw.devtool.validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.dataforms.fw.util.MessagesUtil;
import jp.dataforms.fw.validator.FieldValidator;

/**
 * 更新系SQLのバリデーター。
 */
public class SqlValidator extends FieldValidator {

	/**
	 * パラメータ。
	 */
	private String param = null;

	/**
	 * コンストラクタ。
	 */
	public SqlValidator() {
		super("error.parametersremain");
	}

	@Override
	public String getMessage() {
		return MessagesUtil.getMessage(this.getPage(), this.getMessageKey(), "{0}", this.param);
	}

	@Override
	public boolean validate(Object value) throws Exception {
		String sql = (String) value;
		Pattern p = Pattern.compile(":[a-zA-Z][0-9a-zA-Z_]*");
		Matcher m = p.matcher(sql);
		if (m.find()) {
			this.param = m.group();
			return false;
		}
		return true;
	}
}
