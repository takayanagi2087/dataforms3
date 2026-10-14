package jp.dataforms.fw.dao.sqlgen.h2;

import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jp.dataforms.fw.annotation.SqlGeneratorImpl;
import jp.dataforms.fw.dao.QueryPager;
import jp.dataforms.fw.dao.sqlgen.SqlGenerator;
import jp.dataforms.fw.servlet.DataFormsServlet;


//日付の部分取得
//select extract_field('month',timestamp(test_date, '00:00:00')) y from data_type
//日付の加算
//select date({fn timestampadd(sql_tsi_day, 3, timestamp(current_date, '00:00:00'))}) from sysibm.sysdummy1

/**
 * H2用SQL Generator.
 *
 */
@SqlGeneratorImpl(databaseProductName = H2SqlGenerator.DATABASE_PRODUCT_NAME)
public class H2SqlGenerator extends SqlGenerator {
	/**
	 * Logger.
	 */
	private static Logger logger = LogManager.getLogger(H2SqlGenerator.class);

	/**
	 * データベースシステムの名称。
	 */
	public static final String DATABASE_PRODUCT_NAME = "H2";

	/**
	 * コンストラクタ.
	 * @param conn JDBC接続情報.
	 */
	public H2SqlGenerator(final Connection conn) {
		super(conn);
		logger.debug("H2 SQL Generator");
	}

	@Override
	public String getDatabaseProductName() {
		return DATABASE_PRODUCT_NAME;
	}


/*	@Override
	public String getDatabaseType(final Field<?> field) {
		String type = field.getDbDependentType(DATABASE_PRODUCT_NAME);
		if (type != null) {
			return type;
		}
		return super.getDatabaseType(field);
	}*/

	/**
	 * {@inheritDoc}
	 * テーブル、カラムのcommentを構文はサポートされている。
	 *
	 */
	@Override
	protected CommentSyntax getCommentSyntax() {
		// commentはサポートしない.
		return SqlGenerator.CommentSyntax.COMMENT;
	}

	/**
	 * {@inheritDoc}
	 * シーケンスをサポートしているのでtrueを返します。
	 */
	@Override
	public boolean isSequenceSupported() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 * テーブル情報を取得するときには、DatabaseMetadataに対し、大文字のテーブル名を渡す必要があるので
	 * テーブル名を大文字に変換します。
	 */
	@Override
	public String convertTableNameForDatabaseMetaData(final String tblname) {
		return tblname.toUpperCase();
	}

	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String converTypeNameForDatabaseMetaData(final String type) {
		String ret = super.converTypeNameForDatabaseMetaData(type);
		if ("character varying".equals(ret)) {
			return "varchar";
		} else if ("character".equals(ret)) {
			return "char";
		} else if ("character large object".equals(ret)) {
			return "clob";
		} else if ("binary large object".equals(ret)) {
			return "blob";
		}
		
		/*else if ("float".equals(ret)) {
			return "real";
		} else if (ret.indexOf("timestamp") == 0) {
			return "timestamp";
		}*/
		return ret;
	}

	
	/**
	 * カラム名を取得します。
	 * <pre>
	 * 一般的なJDBCドライバーのgetColumnNameメソッドは以下のSQLのcollabelを返す。
	 * しかし、MariaDBのJDBCドライバーはcolnameを返す仕様になっており、
	 * collabelを取得するには、getColumnLabelメソッドを使う必要がある。
	 * select colname as collabel ...
	 * (JDBCの規格からするとこの仕様が正しいと思われる...)
	 * H2ではgetColumnLabelを使用する。
	 * この仕様違いをこのメソッドをオーバーライドして吸収する。
	 * 
	 * </pre>
	 * @param meta 結果集合メタデータ。
	 * @param idx カラムインデックス。
	 * @return カラム名。
	 * @throws Exception 例外。
	 */
	public String getColumnName(final ResultSetMetaData meta, final int idx) throws Exception {
		return meta.getColumnLabel(idx);
	}

	/**
	 * {@inheritDoc}
	 * information_schema.tablesにテーブルが登録されているか確認するSQLを作成します。
	 */
	@Override
	public String generateTableExistsSql() {
		String sql = """
			select count(*) as table_exists
			from information_schema.tables 
			where lower(table_schema) = 'public' and lower(table_name) = :table_name;
		""";
		return sql;
	}

	/**
	 * {@inheritDoc}
	 * information_schema.sequencesにシーケンスが登録されているか確認するSQLを作成します。
	 */
	@Override
	public String generateSequenceExistsSql() {
		String sql = """
			select count(*) as sequence_exists
			from information_schema.sequences 
			where lower(sequence_schema) = 'public' and lower(sequence_name) = :sequence_name	
		""";
		return sql;
	}

	@Override
	public String generateRenameTableSql(final String oldname, final String newname) {
		String sql = "ALTER TABLE " + oldname + " RENAME TO " + newname;
		return sql;
	}


	@Override
	public String generateCreateSequenceSql(final String sequencename, final Long startValue) throws Exception {
		String ret = null;
		ret = "create sequence " + sequencename + " as bigint start with " + startValue;
		return ret;
	}

	@Override
	public String generateDropSequenceSql(final String sequencename) throws Exception {
		String ret = null;
		ret = "drop sequence if exists " + sequencename;
		return ret;
	}

	@Override
	public String generateGetRecordIdSql(final String tablename) throws Exception {
		return "select seq from (values next value for " + tablename + "_seq) t(seq)";
	}

	@Override
	public String generateSysTimestampSql() {
		return "current_timestamp";
	}

	@Override
	public String generateGetPageSql(final QueryPager qp) {
		String orgsql = this.getOrgSql(qp);
		String sql = "select * from (select row_number() over() as row_no, m.* from (" + orgsql + ") as m) as m where (:row_from + 1) <= m.row_no and m.row_no <= (:row_to + 1)";
		return sql;
	}


	@Override
	public String getRebildSqlFolder() {
		return "/WEB-INF/dbRebuild/h2";
	}

	@Override
	public String getConstraintViolationException(SQLException ex) {
		logger.error("getConstraintViolationException:" + ex.getMessage(), ex);
		String ret = null;
		if (ex instanceof SQLIntegrityConstraintViolationException) {
			String msg = ex.getMessage();
			{
				String pat = "INDEX .+\\.(.+?) ON";
				if (DataFormsServlet.getDuplicateErrorMessage() != null) {
					pat = DataFormsServlet.getDuplicateErrorMessage();
				}
				ret = this.getConstraintName(pat, msg);
			}
			{
				String pat = "violation: \"(.+?):";
				if (DataFormsServlet.getForeignKeyErrorMessage() != null) {
					pat = DataFormsServlet.getForeignKeyErrorMessage();
				}
				logger.debug("pat=" + pat);
				ret = this.getConstraintName(pat, msg);
			}
		}
		return ret;
	}
}
