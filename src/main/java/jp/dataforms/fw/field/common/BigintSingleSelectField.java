package jp.dataforms.fw.field.common;

import jp.dataforms.fw.dao.sqldatatype.SqlBigint;
import jp.dataforms.fw.util.NumberUtil;
import jp.dataforms.fw.util.StringUtil;

/**
 * BIGINT型の単一選択フィールドクラス。
 * <pre>
 * optionList中の選択肢を単一選択の&lt;select&gt;や&lt;input type=&quot;radio&quot; ...&gt;に
 * 表示し、選択するためのフィールドです。
 * </pre>
 *
 */
public class BigintSingleSelectField extends SingleSelectField<Long> implements SqlBigint {
	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public BigintSingleSelectField(final String id) {
		super(id);
	}

	@Override
	public void setClientValue(final Object v) {
		if (!StringUtil.isBlank(v)) {
			this.setValue(Long.parseLong(((String) v).replaceAll(",", "")));
		} else {
			this.setValue(null);
		}
	}

	@Override
	public void setDBValue(final Object value) {
		this.setValue(NumberUtil.longValueObject(value));
	}

}
