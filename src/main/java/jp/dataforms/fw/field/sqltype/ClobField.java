package jp.dataforms.fw.field.sqltype;

import jp.dataforms.fw.dao.sqldatatype.SqlClob;
import jp.dataforms.fw.dao.sqlgen.mysql.MysqlSqlGenerator;
import jp.dataforms.fw.dao.sqlgen.pgsql.PgsqlSqlGenerator;
import jp.dataforms.fw.field.base.TextField;


/**
 * CLOBフィールドクラス。
 *
 */
public class ClobField extends TextField implements SqlClob {
	/**
	 * コンストラクタ。
	 * @param fieldId フィールドID。
	 */
	public ClobField(final String fieldId) {
		super(fieldId);
		this.setDbDependentType(PgsqlSqlGenerator.DATABASE_PRODUCT_NAME, "text");
		this.setDbDependentType(MysqlSqlGenerator.DATABASE_PRODUCT_NAME, "longtext");
	}

	@Override
	public void setClientValue(final Object v) {
		this.setValue((String) v);
	}

}
