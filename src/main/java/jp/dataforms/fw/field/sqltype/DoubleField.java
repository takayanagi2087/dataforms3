package jp.dataforms.fw.field.sqltype;

import jp.dataforms.fw.dao.sqldatatype.SqlDouble;
import jp.dataforms.fw.field.base.NumberField;
import jp.dataforms.fw.util.StringUtil;


/**
 * 倍精度浮動小数点数フィールドクラス。
 *
 */
public class DoubleField extends NumberField<Double> implements SqlDouble {
	/**
	 * コンストラクタ。
	 * @param fieldId フィールドID。
	 */
	public DoubleField(final String fieldId) {
		super(fieldId, false);
	}

	/**
	 * コンストラクタ。
	 * @param fieldId フィールドID。
	 * @param comma カンマ編集フラグ。
	 */
	public DoubleField(final String fieldId, final boolean comma) {
		super(fieldId, comma);
	}


	/**
	 * {@inheritDoc}
	 * <pre>
	 * 入力された文字列をDoubleに変換します。
	 * </pre>
	 */
	@Override
	public void setClientValue(final Object v) {
		if (!StringUtil.isBlank(v)) {
			this.setValue(Double.parseDouble(((String) v).replaceAll(",", "")));
		} else {
			this.setValue(null);
		}
	}

}
