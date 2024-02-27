package jp.dataforms.fw.dao.sqlgen.mysql;

import java.sql.ParameterMetaData;

import jp.dataforms.fw.dao.sqlgen.SqlParser;

/**
 * MariaDB用SQLパーサー。
 *
 */
public class MariaDbSqlParser extends SqlParser {
	/**
	 * コンストラクタ。
	 * @param sql SQL。
	 */
	public MariaDbSqlParser(final String sql) {
		super(sql);
	}

/*	@Override
	protected ParameterMetaData getParameterMetaData(final PreparedStatement st) throws SQLException {
		return null;
	}*/

	@Override
	protected int getParameterType(final ParameterMetaData meta, final int idx) throws Exception {
		// MariaDBの場合BLOB以外は0を指定しておけば問題ないみたい。
		return 0;
	}

}
