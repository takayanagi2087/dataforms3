package jp.dataforms.fw.dao.sqlgen.mssql;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jp.dataforms.fw.annotation.SqlGeneratorImpl;
import jp.dataforms.fw.dao.Index;
import jp.dataforms.fw.dao.Query;
import jp.dataforms.fw.dao.QueryPager;
import jp.dataforms.fw.dao.Table;
import jp.dataforms.fw.dao.sqldatatype.SqlBlob;
import jp.dataforms.fw.dao.sqldatatype.SqlClob;
import jp.dataforms.fw.dao.sqldatatype.SqlTimestamp;
import jp.dataforms.fw.dao.sqlgen.SqlGenerator;
import jp.dataforms.fw.field.base.Field;
import jp.dataforms.fw.field.base.FieldList;
import jp.dataforms.fw.servlet.DataFormsServlet;
import jp.dataforms.fw.util.StringUtil;

/**
 * MS SQL Server用SQL Generator.
 *
 */
@SqlGeneratorImpl(databaseProductName = MssqlSqlGenerator.DATABASE_PRODUCT_NAME)
public class MssqlSqlGenerator extends SqlGenerator {
	/**
	 * Logger.
	 */
	private static Logger logger = LogManager.getLogger(MssqlSqlGenerator.class);

	/**
	 * データベースシステムの名称。
	 */
	public static final String DATABASE_PRODUCT_NAME = "Microsoft SQL Server";

	/**
	 * コンストラクタ.
	 * @param conn JDBC接続情報.
	 */
	public MssqlSqlGenerator(final Connection conn) {
		super(conn);
	}

	/**
	 * 主要なパラメータのみの表示用に編集する。
	 * <pre>
	 * MS SQL Serverは全てのパスメータが設定されたURLを返すので、
	 * 接続するデータベースの指定のみを返す。
	 * </pre>
	 */
	@Override
	public String getConnectionUrl(Connection conn) throws Exception {
		String orgUrl = super.getConnectionUrl(conn);
		String[] list = orgUrl.split(";");
		StringBuilder sb = new StringBuilder();
		sb.append(list[0]);
		for (int i = 0; i < list.length; i++) {
			String s = list[i];
			if (s.indexOf("databaseName=") == 0) {
				sb.append(";");
				sb.append(s);
			}
		}
		return sb.toString();
	}

	@Override
	public String getDatabaseProductName() {
		return DATABASE_PRODUCT_NAME;
	}

	/**
	 * {@inheritDoc}
	 * テーブル、カラムのcommentは特殊文法です。
	 *
	 */
	@Override
	protected CommentSyntax getCommentSyntax() {
		// commentはサポートしない.
		return SqlGenerator.CommentSyntax.SPECIAL_GRAMMAR;
	}

	@Override
	public String generateTableCommentSql(final Table table) {
		String sql = "sys.sp_addextendedproperty "
				+ "@name = N'MS_Description'"
				+ ", @value = N'" + table.getComment() + "'"
				+ ", @level0type = N'SCHEMA'"
				+ ", @level0name = N'" + this.getSchema() + "'"
				+ ", @level1type = N'TABLE'"
				+ ", @level1name = N'" + table.getTableName() + "'";
		return sql;
	}

	@Override
	public String generateFieldCommentSql(final Table table, final Field<?> field) {
		String sql = "sys.sp_addextendedproperty @name = N'MS_Description'"
				+ ", @value = N'" + field.getComment() + "'"
				+ ", @level0type = N'SCHEMA'"
				+ ", @level0name = N'" + this.getSchema() + "'"
				+ ", @level1type = N'TABLE'"
				+ ", @level1name = N'" + table.getTableName() + "'"
				+ ", @level2type = N'COLUMN'"
				+ ", @level2name = N'" + field.getDbColumnName() + "'";
		return sql;
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
		} else if ("varbinary".equals(ret)) {
			return "varbinary(max)";
		}
		return ret;
	}

	@Override
	public String convertColumnSize(final int size) {
		if (2147483647 == size) {
			return "max";
		} else {
	    	return Integer.toString(size);
		}
    }



	/**
	 * {@inheritDoc}
	 * SYS.OBJECTSにテーブルが登録されているか確認するSQLを作成します。
	 */
	@Override
	public String generateTableExistsSql() {
		String sql = "select count(*) as TABLE_EXISTS from SYS.OBJECTS where type_desc='USER_TABLE' and  LOWER(NAME)=:table_name";
		return sql;
	}

	/**
	 * {@inheritDoc}
	 * SYS.OBJECTSにシーケンスが登録されているか確認するSQLを作成します。
	 */
	@Override
	public String generateSequenceExistsSql() {
		String sql = "select count(*) as SEQUENCE_EXISTS from SYS.OBJECTS where type_desc='SEQUENCE_OBJECT' and LOWER(name)=:sequence_name";
		return sql;
	}

	@Override
	public String generateRenameTableSql(final String oldname, final String newname) {
		String sql = "sp_rename " + oldname + ", " + newname;
		return sql;
	}

	@Override
	public String generateCreateSequenceSql(final String sequencename, final Long startValue) throws Exception {
		String ret = null;
		ret = "create sequence " + sequencename + " as bigint start with " + startValue;
		return ret;
	}

	@Override
	public String generateGetRecordIdSql(final String tablename) throws Exception {
		return "select next value for " + tablename + "_seq as seq";
	}

	@Override
	public String generateGetRecordIdSqlForInsert(final Table table) throws Exception {
		return "next value for " + table.getTableName() + "_seq";
	}

	@Override
	public String generateSysTimestampSql() {
		return "current_timestamp";
	}

	/**
	 * MS SQL Serverのrow_number用のorder byを生成します。
	 * @param query 問い合わせ。
	 * @return order by句。
	 */
	private String getRowNumberOrderBy(final Query query) {
		FieldList flist = query.getOrderByFieldList();
		if (flist == null || flist.size() == 0) {
			// order byが無かった場合はPK順。
			Table mt = query.getMainTable();
			flist = mt.getPkFieldList();
			if (flist.size() == 0) {
				// PKがなかった場合先頭フィールド順。
				flist = new FieldList(query.getFieldList().get(0));
			}
		}
		StringBuilder sb = new StringBuilder();
		for (Field<?> field: flist) {
			if (sb.length() > 0) {
				sb.append(",");
			}
			sb.append("m.");
			sb.append(StringUtil.camelToSnake(field.getId()));
			if (field.getSortOrder() == Field.SortOrder.ASC) {
				sb.append(" asc");
			} else {
				sb.append(" desc");
			}
		}
		return "order by " + sb.toString();
	}


	@Override
	public String generateGetPageSql(final QueryPager qp) {
		Query query  = qp.getQuery();
		String orderBy = this.getRowNumberOrderBy(query);
		String orgsql = this.getOrgSql(qp, false);
		String sql = "select * from (select row_number() over(" + orderBy + ") as row_no, m.* from (" + orgsql + ") as m) as m where (:row_from + 1) <= m.row_no and m.row_no <= (:row_to + 1)";
		return sql;
	}


	@Override
	public String getRebildSqlFolder() {
		return "/WEB-INF/dbRebuild/mssql";
	}

	@Override
	public String getConstraintViolationException(final SQLException ex) {
		logger.debug(() -> "message=" + ex.getMessage());
		logger.debug(() -> "errorCode=" + ex.getErrorCode());
		logger.debug(() -> "getSQLState=" + ex.getSQLState());
		if (2601 == ex.getErrorCode()) {
			String pat = "一意インデックス '(.+?)' を含むオブジェクト";
			if (DataFormsServlet.getDuplicateErrorMessage() != null) {
				pat = DataFormsServlet.getDuplicateErrorMessage();
			}
			logger.debug("DuplicateErrorMessage={}", pat);
			// ERROR: 重複キーが一意性制約"enum_index"に違反しています
			return this.getConstraintName(pat, ex.getMessage());
		} else if (ex.getErrorCode() == 547) {
			// ERROR: テーブル"enum"の更新または削除は、テーブル"enum"の外部キー制約"fk_enum_table01"に違反します
			String pat = "制約 \"(.+?)\" と競合しています。";
			if (DataFormsServlet.getForeignKeyErrorMessage() != null) {
				pat = DataFormsServlet.getForeignKeyErrorMessage();
			}
			logger.debug("ForeignKeyErrorMessage={}", pat);
			return this.getConstraintName(pat, ex.getMessage());
		}
		return null;
	}

	@Override
	public String getDatabaseType(Field<?> field) {
		String type = super.getDatabaseType(field);
		if (field instanceof SqlTimestamp) {
			type = "datetime";
		} else if (field instanceof SqlBlob) {
			type = "varbinary(max)";
		} else if (field instanceof SqlClob) {
			type = "varchar(max)";
		}

		return type;
	}

	@Override
	public String generateDropIndexSql(Index index) {
		String tableName = index.getTable().getTableName();
		return "drop index " + tableName + "." + index.getIndexName();
	}

	@Override
	public String generateDropIndexSql(String indexName, String tableName) {
		return "drop index " + tableName + "." + indexName;
	}


	@Override
	public String generateAddUniqueSql(Index index) {
		return null;
	}

	@Override
	public String generateDropUniqueSql(Index index) {
		return null;
	}

	@Override
	public String generateDropUniqueSql(Table table, String idxName) {
		return null;
	}
}
