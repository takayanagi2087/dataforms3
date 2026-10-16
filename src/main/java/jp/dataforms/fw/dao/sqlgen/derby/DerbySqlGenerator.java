package jp.dataforms.fw.dao.sqlgen.derby;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

import org.apache.derby.shared.common.error.DerbySQLIntegrityConstraintViolationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jp.dataforms.fw.annotation.SqlGeneratorImpl;
import jp.dataforms.fw.dao.sqlgen.SqlGenerator;
import jp.dataforms.fw.field.sqlfunc.ExtractField;
import jp.dataforms.fw.field.sqltype.DateField;
import jp.dataforms.fw.field.sqltype.TimeField;


//日付の部分取得
//select extract_field('month',timestamp(test_date, '00:00:00')) y from data_type
//日付の加算
//select date({fn timestampadd(sql_tsi_day, 3, timestamp(current_date, '00:00:00'))}) from sysibm.sysdummy1

/**
 * Apache Derby用SQL Generator.
 *
 */
@SqlGeneratorImpl(databaseProductName = DerbySqlGenerator.DATABASE_PRODUCT_NAME)
public class DerbySqlGenerator extends SqlGenerator {
	/**
	 * Logger.
	 */
	private static Logger logger = LogManager.getLogger(DerbySqlGenerator.class);

	/**
	 * データベースシステムの名称。
	 */
	public static final String DATABASE_PRODUCT_NAME = "Apache Derby";

	/**
	 * コンストラクタ.
	 * @param conn JDBC接続情報.
	 */
	public DerbySqlGenerator(final Connection conn) {
		super(conn);
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
	 * テーブル、カラムのcommentはサポートされていません。
	 *
	 */
	@Override
	protected CommentSyntax getCommentSyntax() {
		// commentはサポートしない.
		return SqlGenerator.CommentSyntax.NONE;
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
	 * SYS.SYSTABLESにテーブルが登録されているか確認するSQLを作成します。
	 */
	@Override
	public String generateTableExistsSql() {
		String sql = "select count(*) as TABLE_EXISTS from SYS.SYSTABLES where LOWER(TABLENAME)=:table_name";
		return sql;
	}

	/**
	 * {@inheritDoc}
	 * SYS.SYSSEQUENCESにシーケンスが登録されているか確認するSQLを作成します。
	 */
	@Override
	public String generateSequenceExistsSql() {
		String sql = "select count(*) as SEQUENCE_EXISTS from SYS.SYSSEQUENCES where LOWER(sequencename)=:sequence_name";
		return sql;
	}

	@Override
	public String generateRenameTableSql(final String oldname, final String newname) {
		String sql = "RENAME TABLE " + oldname + " TO " + newname;
		return sql;
	}


	@Override
	public String generateCreateSequenceSql(final String sequencename, final Long startValue) throws Exception {
		String ret = null;
		ret = "create sequence " + sequencename + " as bigint start with " + startValue;
		return ret;
	}

	@Override
	public String generateDropSequenceSql(final String tablename) throws Exception {
		String ret = null;
		ret = "drop sequence " + tablename + " restrict";
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
	public String getRebildSqlFolder() {
		return "/WEB-INF/dbRebuild/derby";
	}

	@Override
	public String getConstraintViolationException(SQLException ex) {
		if (ex instanceof SQLIntegrityConstraintViolationException) {
			if ("org.apache.derby.shared.common.error.DerbySQLIntegrityConstraintViolationException".equals(ex.getClass().getName())) {
				DerbySQLIntegrityConstraintViolationException dex = (DerbySQLIntegrityConstraintViolationException) ex;
				logger.debug(() -> "dex.getTableName()=" + dex.getTableName());
				logger.debug(() -> "dex.getConstraintName()=" + dex.getConstraintName());
				return dex.getConstraintName();
			}
		}
		return null;
	}
	
	/**
	 * Extract関数を作成します。
	 * @param field ExtractField。
	 * @return Extract関数。
	 */
	public String generateExtractFunction(final ExtractField field) {
		String type = field.getType().name().toLowerCase();
		String tf = field.getTargetField().getDbColumnName();
		if (field.getTable() != null) {
			tf = field.getTable().getAlias() + "." + tf;
		}
		if (field.getTargetField() instanceof DateField) {
			tf = "timestamp(" + tf + " || ' 00:00:00')";
		} else if (field.getTargetField() instanceof TimeField) {
			tf = "timestamp(current_date || ' ' || " + tf + ")";
		}
		String sql = "extract_field('" + type + "', " + tf + ")";
		return sql;
	}
}
