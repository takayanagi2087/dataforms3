package jp.dataforms.fw.dao.sqlgen.oracle;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.text.SimpleDateFormat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jp.dataforms.fw.annotation.SqlGeneratorImpl;
import jp.dataforms.fw.dao.Query;
import jp.dataforms.fw.dao.QueryPager;
import jp.dataforms.fw.dao.Table;
import jp.dataforms.fw.dao.sqldatatype.SqlBigint;
import jp.dataforms.fw.dao.sqldatatype.SqlChar;
import jp.dataforms.fw.dao.sqldatatype.SqlInteger;
import jp.dataforms.fw.dao.sqldatatype.SqlNumeric;
import jp.dataforms.fw.dao.sqldatatype.SqlSmallint;
import jp.dataforms.fw.dao.sqldatatype.SqlTime;
import jp.dataforms.fw.dao.sqldatatype.SqlVarchar;
import jp.dataforms.fw.dao.sqlgen.SqlGenerator;
import jp.dataforms.fw.dao.sqlgen.SqlParser;
import jp.dataforms.fw.field.base.Field;
import jp.dataforms.fw.servlet.DataFormsServlet;
import jp.dataforms.fw.util.StringUtil;

/**
 * Oracle用SQL Generator。
 *
 */
@SqlGeneratorImpl(databaseProductName = OracleSqlGenerator.DATABASE_PRODUCT_NAME)
public class OracleSqlGenerator extends SqlGenerator {

	/**
	 * Logger.
	 */
	private static Logger logger = LogManager.getLogger(OracleSqlGenerator.class);

	/**
	 * データベースシステムの名称。
	 */
	public static final String DATABASE_PRODUCT_NAME = "Oracle";


	/**
	 * コンストラクタ.
	 * @param conn JDBC接続情報.
	 */
	public OracleSqlGenerator(final Connection conn) {
		super(conn);
	}


	@Override
	public String getDatabaseProductName() {
		return DATABASE_PRODUCT_NAME;
	}

	@Override
	public SqlParser newSqlParser(final String sql) {
		return new OracleSqlParser(sql);
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
	 * nullを返します。
	 */
	@Override
	public String generateAdjustSequenceSql(final Table table) throws Exception {
		return null;
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
		/*if ("varchar2".equals(ret)) {
			return "varchar";
		} else*/ if ("char".equals(ret)) {
				return "char";
		} else if ("float".equals(ret)) {
			return "real";
		} else if (ret.indexOf("timestamp") == 0) {
			return "timestamp";
		}
		return ret;
	}

	/**
	 * {@inheritDoc}
	 * <pre>
	 * SqlClobの実装クラスはtext型のカラムを作成します。
	 * SqlBlobの実装クラスはbytea型のカラムを作成します。
	 * </pre>
	 */
	@Override
	public String getDatabaseType(final Field<?> field) {
		String ret = "";
		String type = field.getDbDependentType(DATABASE_PRODUCT_NAME);
		if (type != null) {
			ret = type;
		} else if (field instanceof SqlVarchar) {
			ret = "nvarchar2(" + field.getLength() + ")";
		} else if (field instanceof SqlChar) {
			ret = "nchar(" + field.getLength() + ")";
		} else if (field instanceof SqlSmallint) {
			ret = "number(38,0)";
		} else if (field instanceof SqlBigint) {
			ret = "number(38,0)";
		} else if (field instanceof SqlInteger) {
			ret = "number(38,0)";
		} else if (field instanceof SqlNumeric) {
			SqlNumeric nf = (SqlNumeric) field;
			ret = "number(" + nf.getPrecision() + "," + nf.getScale() + ")";
		} else if (field instanceof SqlTime) {
			ret = "timestamp";
		} else {
			return super.getDatabaseType(field);
		}
		if (field.isNotNull()) {
			ret += " not null";
		} else {
			//ret += " null";
		}
		return ret;
	}

	/**
	 * {@inheritDoc}
	 * Oracleは標準的なcomment文をサポートするので、COMMENTを返します。
	 *
	 */
	@Override
	protected CommentSyntax getCommentSyntax() {
		return SqlGenerator.CommentSyntax.COMMENT;
	}

	/**
	 * {@inheritDoc}
	 * table_existsにテーブルが登録されているか確認するSQLを作成します。
	 */
	@Override
	public String generateTableExistsSql() {
		String sql = "select count(*) as table_exists from cat where LOWER(table_name) = :table_name";
		return sql;
	}

	/**
	 * {@inheritDoc}
	 * user_sequencesにシーケンスが登録されているか確認するSQLを作成します。
	 */
	@Override
	public String generateSequenceExistsSql() {
		String sql = "select count(*) as SEQUENCE_EXISTS from user_sequences where LOWER(sequence_name)=:sequence_name";
		return sql;
	}


	@Override
	public String generateCreateSequenceSql(final String seqname, final Long startValue) throws Exception {
		String ret = "create sequence " + seqname + " start with " + startValue + " minvalue 0";
		return ret;
	}

	@Override
	public String generateGetRecordIdSql(final String tablename) throws Exception {
		return "select " + tablename + "_seq.nextval as seq from dual";
	}

	@Override
	public String generateGetRecordIdSqlForInsert(final Table table) throws Exception {
		return table.getTableName() + "_seq.nextval";
	}

	@Override
	public String generateSysTimestampSql() {
		return "current_timestamp";
	}

	@Override
	public String generateGetPageSql(final QueryPager qp) {
		String orgsql = this.getOrgSql(qp);
	//	String sql = "select * from (select row_number() over() as row_no, m.* from (" + orgsql + ") as m) as m where (:row_from + 1) <= m.row_no and m.row_no <= (:row_to + 1)";
		String sql = "select * from (select rownum as row_no, m.* from (" + orgsql + ") m) m where (:row_from + 1) <= m.row_no and m.row_no <= (:row_to + 1)";
		return sql;
	}

	@Override
	protected String getAsAliasSql() {
		return " ";
	}

	/**
	 * レコード数をカウントするsqlを作成します。
	 * @param query 問い合わせ。
	 * @return レコード数をカウントするsql。
	 */
	@Override
	public String generateHitCountSql(final Query query) {
		String orgsql = this.generateQuerySql(query, false);
		String sql = "select count(*) as cnt from (" + orgsql + ") m";
		return sql;
	}


	/**
	 * レコード数をカウントするsqlを作成します。
	 * @param orgsql SQL。
	 * @return レコード数をカウントするsql。
	 */
	@Override
	public String generateHitCountSql(final String orgsql) {
		String sql = "select count(*) as cnt from (" + orgsql + ") m";
		return sql;
	}

	/**
	 * BLOB等のファイルフィールドの更新用SQLを作成します。
	 * <pre>
	 * 既にファイルが登録されており、ファイルが送信されない場合は、そのままの値を保持するSQLを生成します。
	 * </pre>
	 * @param id フィールドID.
	 * @return SQL.
	 */
	@Override
	protected String generateUpdateFileFieldSql(final String id) {
		String pid = StringUtil.camelToSnake(id);
//		String ret = "case when TO_CHAR(:" + pid + "_kf) = TO_CHAR('1') then " + pid + " else :" + pid + " end ";
		String ret = "decode(:" + pid + "_kf, '1', " + pid + ",:" + pid + ")";
		return ret;
	}

	@Override
	public String getRebildSqlFolder() {
		return "/WEB-INF/dbRebuild/oracle";
	}

	@Override
	public String getConstraintViolationException(final SQLException ex) {
		if (ex instanceof SQLIntegrityConstraintViolationException) {
			SQLIntegrityConstraintViolationException e = (SQLIntegrityConstraintViolationException) ex;
			logger.debug(() -> "massage=" + e.getMessage());
			logger.debug(() -> "errorCode=" + e.getErrorCode());
			logger.debug(() -> "SQLState=" + e.getSQLState());
			if (e.getErrorCode() == 1) {
				String pat = "unique constraint \\(.+?\\.(.+?)\\) violated";
				if (DataFormsServlet.getDuplicateErrorMessage() != null) {
					pat = DataFormsServlet.getDuplicateErrorMessage();
				}
				return this.getConstraintName(pat, ex.getMessage());
			} else if (e.getErrorCode() == 2292) {
				String pat = "constraint \\(.+?\\.(.+?)\\) violated " ;
				if (DataFormsServlet.getForeignKeyErrorMessage() != null) {
					pat = DataFormsServlet.getForeignKeyErrorMessage();
				}
				return this.getConstraintName(pat, ex.getMessage());
			}
		}
		return null;
	}

	@Override
	protected String getLiteral(Object value) {
		if (value instanceof java.sql.Time) {
			SimpleDateFormat fmt = new SimpleDateFormat("HH:mm:ss.SSS");
			return "to_timestamp(\'" + fmt.format((java.util.Date) value) + "\','hh24:mi:ss.ff3')";
		} else if (value instanceof java.sql.Timestamp) {
			SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
			return "to_timestamp(\'" + fmt.format((java.util.Date) value) + "\','yyyy-mm-dd hh24:mi:ss.ff3')";
		} else if (value instanceof java.util.Date) {
			SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
			return "to_date(\'" + fmt.format((java.util.Date) value) + "\','yyyy-mm-dd')";
		} else {
			return super.getLiteral(value);
		}
	}
}
