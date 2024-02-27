package jp.dataforms.fw.field.sqltype;

import jp.dataforms.fw.dao.sqldatatype.SqlVarchar;
import jp.dataforms.fw.field.base.Field;
import jp.dataforms.fw.field.base.TextField;
import jp.dataforms.fw.validator.MaxLengthValidator;


/**
 * VARCHARフィールドクラス。
 *
 */
public class VarcharField extends TextField implements SqlVarchar {
	/**
	 * コンストラクタ。
	 * @param fieldId フィールドID。
	 * @param length フィールド長。
	 */
	public VarcharField(final String fieldId, final int length) {
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

	@Override
	public MatchType getDefaultMatchType() {
		return MatchType.PART;
	}
}
