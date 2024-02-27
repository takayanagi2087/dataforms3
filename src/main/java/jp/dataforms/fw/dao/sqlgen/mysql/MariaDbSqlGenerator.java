package jp.dataforms.fw.dao.sqlgen.mysql;

import java.sql.Connection;

import jp.dataforms.fw.annotation.SqlGeneratorImpl;
import jp.dataforms.fw.dao.sqlgen.SqlParser;;

/**
 * MadiaDB用SQL Generator.
 *
 * <pre>
 * MariaDB用のJDBCドライバがMySQLと仕様が異なるので、それを吸収する。
 * </pre>
 *
 */
@SqlGeneratorImpl(databaseProductName = MariaDbSqlGenerator.DATABASE_PRODUCT_NAME)
public class MariaDbSqlGenerator extends MysqlSqlGenerator {
	/**
	 * データベースシステムの名称。
	 */
	public static final String DATABASE_PRODUCT_NAME = "MariaDB";

	/**
	 * コンストラクタ。
	 * @param conn JDBC接続情報。
	 */
	public MariaDbSqlGenerator(final Connection conn) {
		super(conn);
	}

	@Override
	public SqlParser newSqlParser(final String sql) {
		return new MariaDbSqlParser(sql);
	}
}
