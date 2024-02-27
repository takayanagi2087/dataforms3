package jp.dataforms.fw.devtool.field;

import jp.dataforms.fw.devtool.validator.SqlValidator;
import jp.dataforms.fw.field.sqltype.ClobField;

/**
 * 更新SQLフィールド。
 *
 */
public class SqlField extends ClobField {
	/**
	 * コンストラクタ。
	 */
	public SqlField() {
		this(null);
	}

	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public SqlField(final String id) {
		super(id);
	}

	@Override
	protected void onBind() {
		super.onBind();
		this.addValidator(new SqlValidator());
	}
}
