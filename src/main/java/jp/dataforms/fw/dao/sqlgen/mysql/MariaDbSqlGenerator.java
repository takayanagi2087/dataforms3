package jp.dataforms.fw.dao.sqlgen.mysql;

import java.sql.Connection;
import java.sql.ResultSetMetaData;

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
	
	
	/**
	 * カラム名を取得します。
	 * <pre>
	 * 一般的なJDBCドライバーのgetColumnNameメソッドは以下のSQLのcollabelを返す。
	 * しかし、MariaDBのJDBCドライバーはcolnameを返す仕様になっており、
	 * collabelを取得するには、getColumnLabelメソッドを使う必要がある。
	 * select colname as colbabel ...
	 * (JDBCの規格からするとこの仕様が正しいと思われる...)
	 * 
	 * この仕様違いをこのメソッドをオーバーライドして吸収する。
	 * 
	 * </pre>
	 * @param meta 結果集合メタデータ。
	 * @param idx カラムインデックス。
	 * @return カラム名。
	 * @throws Exception 例外。
	 */
	@Override
	public String getColumnName(ResultSetMetaData meta, int idx) throws Exception {
		return meta.getColumnLabel(idx);
	}
}
