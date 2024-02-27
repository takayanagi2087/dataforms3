package jp.dataforms.fw.field.sqltype;


import jp.dataforms.fw.dao.sqldatatype.SqlChar;
import jp.dataforms.fw.field.base.Field;
import jp.dataforms.fw.field.base.TextField;
import jp.dataforms.fw.validator.MaxLengthValidator;



/**
 * CHARフィールドクラス。
 *
 */
public class CharField extends TextField implements SqlChar {
	/**
	 * コンストラクタ。
	 * @param fieldId フィールドID。
	 * @param length フィールド長。
	 */
	public CharField(final String fieldId, final int length) {
		super(fieldId, length);
	}


	@Override
	public String getFieldOption() {
		if (Field.hasLengthParameter(this.getClass().getSuperclass())) {
			return Integer.toString(this.getLength());
		} else {
			return null;
		}
	}
	
	@Override
	protected void onBind() {
		super.onBind();
		this.addValidator(new MaxLengthValidator(this.getLength()));
	}
}
