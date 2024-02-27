package jp.dataforms.fw.dao.sqlgen.mysql;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jp.dataforms.fw.annotation.SqlGeneratorImpl;
import jp.dataforms.fw.dao.Index;
import jp.dataforms.fw.dao.QueryPager;
import jp.dataforms.fw.dao.Table;
import jp.dataforms.fw.dao.sqldatatype.SqlTimestamp;
import jp.dataforms.fw.dao.sqlgen.SqlGenerator;
import jp.dataforms.fw.field.base.Field;
import jp.dataforms.fw.servlet.DataFormsServlet;

/**
 * MySQL用SQL Generator.
 *
 */
@SqlGeneratorImpl(databaseProductName = MysqlSqlGenerator.DATABASE_PRODUCT_NAME)
public class MysqlSqlGenerator extends SqlGenerator {
    /**
     * Logger.
     */
    private static Logger logger = LogManager.getLogger(MysqlSqlGenerator.class.getName());

	/**
	 * データベースシステムの名称。
	 */
	public static final String DATABASE_PRODUCT_NAME = "MySQL";

	/**
	 * コンストラクタ.
	 * @param conn JDBC接続情報.
	 */
	public MysqlSqlGenerator(final Connection conn) {
		super(conn);
	}

	@Override
	public String getDatabaseProductName() {
		return DATABASE_PRODUCT_NAME;
	}

	/**
	 * 主要なパラメータのみの表示用に編集する。
	 * <pre>
	 * My SQLはQueryStringを削除する。
	 * </pre>
	 */
	@Override
	public String getConnectionUrl(Connection conn) throws Exception {
		String orgUrl = super.getConnectionUrl(conn);
		String[] list = orgUrl.split("\\?");
		return list[0];
	}


	/**
	 * {@inheritDoc}
	 * MySQLはシーケンスをサポートしていないのでfalseを返します。
	 */
	@Override
	public boolean isSequenceSupported() {
		return false;
	}


	/**
	 * {@inheritDoc}
	 * テーブル情報を取得するときには、DatabaseMetadataに対し、小文字のテーブル名を渡す必要があるので
	 * テーブル名を大文字に変換します。
	 */
	@Override
	public String convertTableNameForDatabaseMetaData(final String tblname) {
		return tblname.toLowerCase();
	}

	/**
	 * {@inheritDoc}
	 * <pre>
	 * 以下のタイプ名を変換します。
	 * int -&gt; integer
	 * double -&gt; real
	 * decimal -&gt; numeric
	 * &lt;/pre&gt;
	 * </pre>
	 */
	@Override
	public String converTypeNameForDatabaseMetaData(final String type) {
		String ret = super.converTypeNameForDatabaseMetaData(type);
		if ("int".equals(ret)) {
			return "integer";
		} else if ("double".equals(ret)) {
			return "real";
		} else if ("decimal".equals(ret)) {
			return "numeric";
		}
		return ret;
	}


	/**
	 * {@inheritDoc}
	 * <pre>
	 * "auto_increment"を返します。
	 * シーケンスはサポートされないため、レコードIDのカラムに自動生成属性を指定します。
	 * </pre>
	 */
	@Override
	protected String generateAutoIncrementSql() {
		return "auto_increment";
	}

	/**
	 * {@inheritDoc}
	 * <pre>
	 * シーケンスはサポートされないため、最後に生成されたauto_incrementカラムを取得するSQLを作成します。
	 * </pre>
	 */
	@Override
	public String generateGetAutoIncrementValueSql(final Table table) {
		return "select last_insert_id() from " + table.getTableName();
	}

	/**
	 * {@inheritDoc}
	 * <pre>
	 * 	MySQLはcreate table 文中のコメントをサポートするので、CREATE_COMMENTを返します。
	 * </pre>
	 */
	@Override
	protected CommentSyntax getCommentSyntax() {
		// mysqlはcreate中にコメントを含める.
		return SqlGenerator.CommentSyntax.CREATE_COMMENT;
	}

	/**
	 * {@inheritDoc}
	 * INFORMATION_SCHEMA.TABLESにテーブルが登録されているか確認するSQLを作成します。
	 */
	@Override
	public String generateTableExistsSql() {
		String schema = this.getCatalog();
		String sql = "select count(*) as TABLE_EXISTS from INFORMATION_SCHEMA.TABLES where LOWER(TABLE_SCHEMA)='" + schema + "' and  LOWER(TABLE_NAME)=:table_name";
		return sql;
	}

	/**
	 * {@inheritDoc}
	 * <pre>
	 * シーケンスはサポートされないため、nullを返します。
	 * </pre>
	 */
	@Override
	public String generateSequenceExistsSql() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 * <pre>
	 * シーケンスはサポートされないため、nullを返します。
	 * </pre>
	 */
	@Override
	public String generateGetRecordIdSql(final String tablename) throws Exception {
		return null;
	}

	@Override
	public String generateSysTimestampSql() {
		return "current_timestamp";
	}

	@Override
	public String generateGetPageSql(final QueryPager qp) {
		String orgsql = this.getOrgSql(qp);
		String sql = "select * from (select  @i:=@i+1 as row_no, m.* from (select @i:=0) as r,(" + orgsql + ") as m) as m where (:row_from + 1) <= m.row_no and m.row_no <= (:row_to + 1)";
		return sql;
	}

	/**
	 * {@inheritDoc}
	 * <pre>
	 * SqlClobの実装クラスはlongtext型のカラムを作成します。
	 * SqlBlobの実装クラスはlongblob型のカラムを作成します。
	 * </pre>
	 */
	@Override
	public String getDatabaseType(final Field<?> field) {
		if (field instanceof SqlTimestamp) {
			// MySQLのTimestampフィールドは何故かNOT NULLになる。
			field.setNotNull(true);
			return super.getDatabaseType(field);
		} else {
			return super.getDatabaseType(field);
		}
	}

	@Override
	public String generateDropIndexSql(final Index index) {
		return " drop index " + index.getIndexName() + " on " + index.getTable().getTableName();
	}

	@Override
	public String generateDropIndexSql(final String indexName, final String tableName) {
		return "drop index " + indexName + " on " + tableName;
	}


	@Override
	public String generateDropForeignKeySql(final String tableName, final String constraintName) {
		StringBuilder sb = new StringBuilder();
		sb.append("alter table " + tableName + " drop foreign key " + constraintName);
		return sb.toString();

	}


	/*
	@Override
	public String generateAddUniqueSql(final Index index) {
		return null;
	}
*/
	@Override
	public String generateDropUniqueSql(final Index index) {
		StringBuilder sb = new StringBuilder();
		sb.append("alter table ");
		sb.append(index.getTable().getTableName());
		sb.append(" drop index ");
		sb.append(index.getIndexName().replaceAll("_index$", "_unique"));
		return sb.toString();
	}

	@Override
	public String generateDropUniqueSql(final Table table, final String idxName) {
		StringBuilder sb = new StringBuilder();
		sb.append("alter table ");
		sb.append(table.getTableName());
		sb.append(" drop index ");
		sb.append(idxName.replaceAll("_index$", "_unique"));
		return sb.toString();
	}


	/**
	 * Like文のEscape指定文字列を取得します。
	 * @return Like文のEscape指定文字列。
	 */
	@Override
	protected String getLikeEscape() {
		return "";
	}

	@Override
	public String getRebildSqlFolder() {
		return "/WEB-INF/dbRebuild/mysql";
	}


	@Override
	public String getConstraintViolationException(SQLException ex) {
		if (ex instanceof SQLIntegrityConstraintViolationException) {
			SQLIntegrityConstraintViolationException cex = (SQLIntegrityConstraintViolationException) ex;
			logger.debug(() -> "message=" + cex.getMessage());
			logger.debug(() -> "errorCode=" + cex.getErrorCode());
			if (cex.getErrorCode() == 1062) {
				// cex.getErrorCode() == 1062 一意制約違反
				// Duplicate entry 'e01' for key 'enum_index'
				String pat = "for key '(.+?)'$";
				if (DataFormsServlet.getDuplicateErrorMessage() != null) {
					pat = DataFormsServlet.getDuplicateErrorMessage();
				}
				return this.getConstraintName(pat, ex.getMessage());
			} else if (cex.getErrorCode() == 1451) {
				// cex.getErrorCode() == 1451 外部キーのエラー
				// Cannot delete or update a parent row: a foreign key constraint fails (`dfdb`.`enum`, CONSTRAINT `fk_enum_table01` FOREIGN KEY (`parent_id`) REFERENCES `enum` (`enum_id`))
				String pat = "CONSTRAINT `(.+?)` FOREIGN KEY";
				if (DataFormsServlet.getForeignKeyErrorMessage() != null) {
					pat = DataFormsServlet.getForeignKeyErrorMessage();
				}
				return this.getConstraintName(pat, ex.getMessage());
			}

		}
		return null;
	}
}
