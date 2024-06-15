package jp.dataforms.fw.dao;

import java.io.File;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jp.dataforms.fw.controller.Page;
import jp.dataforms.fw.controller.WebComponent;
import jp.dataforms.fw.controller.WebEntryPoint;
import jp.dataforms.fw.dao.file.BlobFileStore;
import jp.dataforms.fw.dao.file.FileObject;
import jp.dataforms.fw.dao.sqldatatype.SqlVarchar;
import jp.dataforms.fw.dao.sqlgen.SqlGenerator;
import jp.dataforms.fw.dao.sqlgen.SqlParser;
import jp.dataforms.fw.exception.ApplicationException;
import jp.dataforms.fw.exception.ConstraintViolationException;
import jp.dataforms.fw.field.base.Field;
import jp.dataforms.fw.field.base.FieldList;
import jp.dataforms.fw.field.common.BlobStoreFileField;
import jp.dataforms.fw.field.common.FileObjectField;
import jp.dataforms.fw.field.sqlfunc.MaxField;
import jp.dataforms.fw.field.sqltype.BigintField;
import jp.dataforms.fw.field.sqltype.CharField;
import jp.dataforms.fw.field.sqltype.ClobField;
import jp.dataforms.fw.field.sqltype.DateField;
import jp.dataforms.fw.field.sqltype.DoubleField;
import jp.dataforms.fw.field.sqltype.IntegerField;
import jp.dataforms.fw.field.sqltype.NumericField;
import jp.dataforms.fw.field.sqltype.SmallintField;
import jp.dataforms.fw.field.sqltype.TimeField;
import jp.dataforms.fw.field.sqltype.TimestampField;
import jp.dataforms.fw.field.sqltype.VarcharField;
import jp.dataforms.fw.servlet.DataFormsServlet;
import jp.dataforms.fw.util.JsonUtil;
import jp.dataforms.fw.util.NumberUtil;
import jp.dataforms.fw.util.StringUtil;

/**
 * データアクセスクラス。
 *
 */
public class Dao implements JDBCConnectableObject {

    /**
     * Logger.
     */
    private static Logger logger = LogManager.getLogger(Dao.class.getName());


    /**
     * コメント。
     */
    private String comment = null;


	/**
	 * JDBC接続可能オブジェクト。
	 */
	private WeakReference<JDBCConnectableObject> jdbcConnectableObject = null;

	/**
	 * SQLジェネレータ。
	 */
	private SqlGenerator sqlGenerator = null;


	/**
	 * BLOBの読み込みモード。
	 *
	 *
	 */
	public enum BlobReadMode {
		/**
		 * Webページに表示するための読み込みモードです(デフォルト)
		 * <pre>
		 * BLOBに記録されたファイル情報ヘッダのみを読み込み、FileObjectのインスタンスに記録します。
		 * ファイルの内容は読み込みません。
		 * </pre>
		 */
		FOR_DISPLAY_FILE_INFO,
		/**
		 * WebページからBLOB中のファイルをダウンロードする際の読み込みモードです。
		 * <pre>
		 * BLOBに記録されたファイル情報ヘッダのみを読み込み、FileObjectのインスタンスに記録し、
		 * ヘッダ部を除いたファイルの本体を一時ファイルに出力します。
		 * </pre>
		 */
		FOR_DOWNLOAD,
		/**
		 * BLOBを含むテーブルのレコードを他のレコードにコピーする際の読み込みモードです。
		 * <pre>
		 * BLOBに記録されたファイル情報ヘッダのみを読み込み、FileObjectのインスタンスに記録し、
		 * ヘッダ部を含んだBLOBの内容全体を一時ファイルに出力します。。
		 * </pre>
		 */
		FOR_DB_WRITING
	};

	/**
	 * BLOB読み込みモード。
	 */
	private BlobReadMode blobReadMode = BlobReadMode.FOR_DISPLAY_FILE_INFO;


	/**
	 * JDBC接続可能オブジェクトを設定設定します。
	 * @param cobj JDBC接続可能オブジェクト。
	 */
	public final void setJDBCConnectableObject(final JDBCConnectableObject cobj) {
		this.jdbcConnectableObject = new WeakReference<JDBCConnectableObject>(cobj);
	}


	@Override
	public Connection getConnection() {
		return this.jdbcConnectableObject.get().getConnection();
	}


	/**
	 * コンストラクタ。
	 */
	public Dao() {

	}


	/**
	 * コンストラクタ。
	 * @param cobj JDBC接続可能Object。
	 * @throws Exception 例外。
	 */
	public Dao(final JDBCConnectableObject cobj) throws Exception {
		init(cobj);
	}

	/**
	 * DB接続環境を初期化します。
	 * @param cobj JDBC接続可能Object。
	 * @throws Exception 例外。
	 */
	public void init(final JDBCConnectableObject cobj) throws Exception {
		this.setJDBCConnectableObject(cobj);
		this.sqlGenerator = SqlGenerator.getInstance(this.getConnection());
	}


	/**
	 * コメントを取得します。
	 * @return コメント。
	 */
	public String getComment() {
		return this.comment;
	}


	/**
	 * コメントを設定します。
	 * @param comment コメント。
	 */
	public void setComment(final String comment) {
		this.comment = comment;
	}


	/**
	 * 現在のページを取得する。
	 * @return ページ。
	 *
	 */
	protected Page getPage() {
		JDBCConnectableObject cobj = this.jdbcConnectableObject.get();
		while (cobj instanceof Dao) {
			Dao dao = (Dao) cobj;
			cobj = dao.jdbcConnectableObject.get();
		}
		WebComponent comp = (WebComponent) cobj;
		return comp.getPage();
	}

	/**
	 * WebEntryPointを取得します。
	 * @return WebEntryPoint。
	 */
	protected WebEntryPoint getWebEntryPoint() {
		JDBCConnectableObject cobj = this.jdbcConnectableObject.get();
		while (cobj instanceof Dao) {
			Dao dao = (Dao) cobj;
			cobj = dao.jdbcConnectableObject.get();
		}
		WebComponent comp = (WebComponent) cobj;
		return comp.getWebEntryPoint();
	}


	/**
	 * JDBC接続可能オブジェクトを取得します。
	 * @return JDBC接続可能オブジェクト。
	 */
	protected JDBCConnectableObject getJDBCConnectableObject() {
		return this.jdbcConnectableObject.get();
	}


	/**
	 * Blobフィールドの読み込みモードを取得します。
	 * @return Blobフィールドの読み込みモード。
	 */
	public BlobReadMode getBlobReadMode() {
		return blobReadMode;
	}

	/**
	 * Blobフィールドの読み込みモードを設定します。
	 * @param blobReadMode Blobフィールドの読み込みモード。
	 */
	public void setBlobReadMode(final BlobReadMode blobReadMode) {
		this.blobReadMode = blobReadMode;
	}

	/**
	 * SQLジェネレータを取得します。
	 * @return SQLジェネレータ。
	 */
	public SqlGenerator getSqlGenerator() {
		return sqlGenerator;
	}


	/**
	 * SQLジェネレータを設定します。
	 * @param sqlGenerator SQLジェネレータ。
	 */
	protected void setSqlGenerator(final SqlGenerator sqlGenerator) {
		this.sqlGenerator = sqlGenerator;
	}


	/**
	 * カラム情報。
	 *
	 */
	private static class ColumnInfo {
		/**
		 * フィールドID。
		 */
		private String id;
		/**
		 * データ型。
		 */
		private int type;

		/**
		 * フィールド長。
		 */
		private int precision = 0;

		/**
		 * 小数点以下の桁数。
		 */
		private int scale = 0;

		/**
		 * コンストラクタ。
		 * @param id フィールドID。
		 * @param type データタイプ。
		 * @param precision データタイプ。
		 * @param scale データタイプ。
		 *
		 */
		public ColumnInfo(final String id, final int type, final int precision, final int scale) {
			this.id = StringUtil.snakeToCamel(id);
			this.type = type;
			this.precision = precision;
			this.scale = scale;
		}

		/**
		 * フィールドIDを取得します。
		 * @return フィールドID。
		 */
		public String getId() {
			return id;
		}

		/**
		 * データタイプを取得します。
		 * @return データタイプ。
		 */
		public int getType() {
			return type;
		}


		/**
		 * フィールド長を取得します。
		 * @return フィールド長。
		 */
		public int getPrecision() {
			return precision;
		}

		/**
		 * 小数点以下の桁数を取得します。
		 * @return 小数点以下の桁数。
		 */
		public int getScale() {
			return scale;
		}

		/**
		 * データ型に応じたフィールドクラスのインスタンスを取得します。
		 * @return デフォルトフィールド。
		 */
		public Field<?> getDefaultFieldInstance() {
			Field<?> ret = null;
			if (this.getType() == Types.BIGINT) {
				ret = new BigintField(this.getId());
			} else if (this.getType() == Types.BLOB
					 || this.getType() == Types.BINARY
					 || this.getType() == Types.LONGVARBINARY
					 || this.getType() == Types.VARBINARY) {
				ret = new BlobStoreFileField(this.getId());
			} else if (this.getType() == Types.CHAR) {
				ret = new CharField(this.getId(), this.getPrecision());
			} else if (this.getType() == Types.CLOB) {
				ret = new ClobField(this.getId());
			} else if (this.getType() == Types.DATE) {
				ret = new DateField(this.getId());
			} else if (this.getType() == Types.DOUBLE || this.getType() == Types.REAL) {
				DoubleField df = new DoubleField(this.getId());
				df.setScale(5);
				ret = df;
			} else if (this.getType() == Types.INTEGER) {
				ret = new IntegerField(this.getId());
			} else if (this.getType() == Types.NUMERIC) {
				ret = new NumericField(this.getId(), this.getPrecision(), this.getScale());
			} else if (this.getType() == Types.SMALLINT) {
				ret = new SmallintField(this.getId());
			} else if (this.getType() == Types.TIME) {
				ret = new TimeField(this.getId());
			} else if (this.getType() == Types.TIMESTAMP) {
				ret = new TimestampField(this.getId());
			} else if (this.getType() == Types.VARCHAR) {
				ret = new VarcharField(this.getId(), this.getPrecision());
			}
			return ret;
		}
	}

	/**
	 * 問合せ結果のカラムリスト。
	 */
	private List<ColumnInfo> resultSetColumnList = null;

	/**
	 * 問合せ結果のメタデータを取得します。
	 * @param meta メタデータ。
	 * @throws Exception 例外。
	 */
	private void setResultSetMetaData(final ResultSetMetaData meta) throws Exception {
		this.resultSetColumnList = new ArrayList<ColumnInfo>();
		for (int i = 1; i <= meta.getColumnCount(); i++) {
			String name = meta.getColumnName(i);
			int type = meta.getColumnType(i);
			ColumnInfo ci = new ColumnInfo(name, type, meta.getPrecision(i), meta.getScale(i));
			this.resultSetColumnList.add(ci);
		}
	}

	/**
	 * 直前に実行した問い合わせのフィールドリストを取得します。
	 * <pre>
	 * ResultSetMetaDataの各カラムの情報から想定されるフィールドを作成し、そのリストを返します。
	 * </pre>
	 *
	 * @return フィールドリスト。
	 */
	public FieldList getResultSetFieldList() {
		FieldList ret = new FieldList();
		for (ColumnInfo ci: this.resultSetColumnList) {
			Field<?> f = ci.getDefaultFieldInstance();
			if (f != null) {
				ret.add(ci.getDefaultFieldInstance());
			}
		}
		return ret;
	}

	/**
	 * Queryを実行し、その結果の各レコードをRecordProcessorに渡します。
	 * @param sql SQL。
	 * @param data パラメータ。
	 * @param processor レコード処理クラス。
	 * @throws Exception 例外。
	 */
	public void executeQuery(final String sql, final Map<String, Object> data, final RecordProcessor processor) throws Exception {
		SqlParser p = this.getSqlGenerator().newSqlParser(sql);
		Connection conn = this.getConnection();
		PreparedStatement st = conn.prepareStatement(p.getParsedSql());
		try {
			p.setParameter(st, data);
			ResultSet rset = st.executeQuery();
			ResultSetMetaData meta = rset.getMetaData();
			this.setResultSetMetaData(meta);
			try {
				while (rset.next()) {
					Map<String, Object> m = new HashMap<String, Object>();
					for (int i = 1; i <= meta.getColumnCount(); i++) {
						String name = meta.getColumnName(i);
						if (meta.getColumnType(i) == Types.BLOB) {
							Blob blob = rset.getBlob(i);
							FileObject obj = null;
							if (blob != null) {
								InputStream is = blob.getBinaryStream();
								BlobFileStore fs = new BlobFileStore(this.jdbcConnectableObject.get());
								obj = readBlob(is, fs);
							}
							m.put(StringUtil.snakeToCamel(name), obj);
						} else if (meta.getColumnType(i) == Types.BINARY
								 || meta.getColumnType(i) == Types.LONGVARBINARY
								 || meta.getColumnType(i) == Types.VARBINARY) {
							InputStream is = rset.getBinaryStream(i);
							BlobFileStore fs = new BlobFileStore(this.jdbcConnectableObject.get());
							FileObject obj = null;
							if (is != null) {
								obj = readBlob(is, fs);
							}
							m.put(StringUtil.snakeToCamel(name), obj);
						} else if (meta.getColumnType(i) == Types.CLOB) {
							Clob clob = rset.getClob(i);
							if (clob != null) {
								String val = clob.getSubString(1, (int) clob.length());
								m.put(StringUtil.snakeToCamel(name), val);
							}
						} else if (meta.getColumnType(i) == Types.TIMESTAMP) {
							Timestamp ts = rset.getTimestamp(i);
							if (ts != null) {
								m.put(StringUtil.snakeToCamel(name), ts);
							}
						} else if (meta.getColumnType(i) == Types.REAL
								|| meta.getColumnType(i) == Types.DOUBLE) {
							Double d = rset.getDouble(i);
							if (d != null) {
								m.put(StringUtil.snakeToCamel(name), d);
							}
						} else {
							Object obj = rset.getObject(i);
							m.put(StringUtil.snakeToCamel(name), obj);
						}
					}
					if (!processor.process(m)) {
						break;
					}
				}
			} finally {
				rset.close();
			}
		} finally {
			st.close();
		}
	}

	/**
	 * 条件フィールドリストに対応する条件式を取得します。
	 * @param query 問合せ。
	 * @param cflist 条件フィールドリスト。
	 * @param p 条件パラメータ。
	 * @return 条件式。
	 */
	public String getWhereCondition(final Query query, final FieldList cflist, final Map<String, Object> p) {
		return this.getSqlGenerator().getWhereCondition(query, cflist, p);
	}

	/**
	 * BLOBフィールドの読み込みを行います。
	 * @param is BLOBの入力ストリーム。
	 * @param fs ファイルストア。
	 * @return 読み込み結果。
	 * @throws Exception 例外。
	 */
	public FileObject readBlob(final InputStream is, final BlobFileStore fs) throws Exception {
		FileObject obj;
		if (this.getBlobReadMode() == BlobReadMode.FOR_DOWNLOAD) {
			obj = fs.readForDownload(is);
		} else if (this.getBlobReadMode() == BlobReadMode.FOR_DB_WRITING) {
			obj = fs.readForDbWriting(is);
		} else {
			obj = fs.readFileInfo(is);
		}
		return obj;
	}



	/**
	 * SQLを実行し、その結果リストを返します。
	 * <pre>
	 * 各カラムのデータ変換は行わず、DBの保存形式のまま取得します。
	 * </pre>
	 * @param sql SQL。
	 * @param data パラメータ。
	 * @return 検索結果。
	 * @throws Exception 例外。
	 */
	public List<Map<String, Object>> executeQuery(final String sql, final Map<String, Object> data) throws Exception {
		final List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		this.executeQuery(sql, data, new RecordProcessor() {
			@Override
			public boolean process(final Map<String, Object> rec) {
				list.add(rec);
				return true;
			}
		});
		return list;
	}

	/**
	 * 問い合わせを実行し、ヒットしたレコードを処理します。
	 * @param query 問い合わせ。
	 * @param p レコード処理関数。
	 * @throws Exception 例外。
	 */
	public void executeQuery(final Query query, final RecordProcessor p) throws Exception {
		String sql = this.getSqlGenerator().generateQuerySql(query);
		this.executeQuery(sql, this.convertToDBValue(query.getConditionFieldList(), query.getConditionData()), p);
	}


	/**
	 * 問い合わせを実行し、その結果リストを返します。
	 * <pre>
	 * 問い合わせ結果の各カラムはフィールドの機能で変換(DBValue→Value)を行います。
	 * </pre>
	 * @param query 問い合わせ。
	 * @return 問い合わせ結果。
	 * @throws Exception 例外。
	 */
	public List<Map<String, Object>> executeQuery(final Query query) throws Exception {
		String sql = this.getSqlGenerator().generateQuerySql(query);
		final List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		this.executeQuery(sql, this.convertToDBValue(query.getConditionFieldList(), query.getConditionData()), new RecordProcessor() {
			@Override
			public boolean process(final Map<String, Object> rec) {
				list.add(rec);
				return true;
			}
		});
		return this.convertFromDBValue(query.getFieldList(), list);
	}

	/**
	 * SQLの実行結果の先頭レコードを取得します。
	 * <pre>
	 * 各カラムのデータ変換は行わず、DBの保存形式のまま取得します。
	 * </pre>
	 * @param sql SQL。
	 * @param param パラメータ。
	 * @return 先頭レコード。
	 * @throws Exception 例外。
	 */
	public Map<String, Object> executeRecordQuery(final String sql, final Map<String, Object> param) throws Exception {
		List<Map<String, Object>> list = this.executeQuery(sql, param);
		if (list.size() > 0) {
			return list.get(0);
		} else {
			return null;
		}
	}


	/**
	 * 問い合わせ結果の先頭レコードを取得します。
	 * <pre>
	 * 問い合わせ結果の各カラムはフィールドの機能で変換(DBValue→Value)を行います。
	 * </pre>
	 * @param query 問い合わせ。
	 * @return 先頭レコード。
	 * @throws Exception 例外。
	 */
	public Map<String, Object> executeRecordQuery(final Query query) throws Exception {
		List<Map<String, Object>> list = this.executeQuery(query);
		if (list.size() > 0) {
			return list.get(0);
		} else {
			return null;
		}
	}

	/**
	 * クエリ結果の先頭レコードの先頭の値を取得します。
	 * <pre>
	 * カラムのデータ変換は行わず、DBの保存形式のまま取得します。
	 * </pre>
	 * @param sql SQL。
	 * @param param パラメータ。
	 * @return 先頭レコードの先頭項目。
	 * @throws Exception 例外。
	 */
	public Object executeScalarQuery(final String sql, final Map<String, Object> param) throws Exception {
		Map<String, Object> rec = this.executeRecordQuery(sql, param);
		if (rec != null) {
			Collection<?> col = rec.values();
			return col.toArray()[0];
		} else {
			return null;
		}
	}


	/**
	 * クエリ結果の先頭レコードの先頭の値を取得します。
	 * <pre>
	 * 問い合わせ結果のカラムはフィールドの機能で変換(DBValue→Value)を行います。
	 * </pre>
	 * @param query 問い合わせ。
	 * @return 先頭レコードの先頭項目。
	 * @throws Exception 例外。
	 */
	public Object executeScalarQuery(final Query query) throws Exception {
		Map<String, Object> rec = this.executeRecordQuery(query);
		if (rec != null) {
			Collection<?> col = rec.values();
			return col.toArray()[0];
		} else {
			return null;
		}
	}


	/**
	 * 更新系SQLを実行します。
	 * <pre>
	 * insert, update, delete等の更新系SQLを実行します。
	 * dataの変換は行わず、そのまま保存します。
	 * </pre>
	 * @param sql 実行するSQL。
	 * @param data パラメータ。
	 * @return 更新件数。
	 * @throws Exception 例外。
	 */
	public int executeUpdate(final String sql, final Map<String, Object> data) throws Exception {
		int ret = 0;
		SqlParser p = this.getSqlGenerator().newSqlParser(sql);
		Connection conn = this.getConnection();
		PreparedStatement st = conn.prepareStatement(p.getParsedSql());
		try {
			p.setParameter(st, data);
			try {
				ret = st.executeUpdate();
			} finally {
				p.removeBlobTempFile(data);
			}
		} catch (SQLException e) {
			ConstraintViolationException cne = this.getConstraintViolationException(e);
			if (cne != null) {
				throw cne;
			}
			throw e;
		} finally {
			st.close();
		}
		return ret;
	}

	/**
	 * 制約違反に対応した例外を取得します。
	 * @param e 例外。
	 * @return 制約違反の場合、ConstraintViolationExceptionのインスタンス。
	 */
	protected ConstraintViolationException getConstraintViolationException(final SQLException e) {
		SqlGenerator gen = this.getSqlGenerator();
		String cn = gen.getConstraintViolationException(e);
		if (cn != null) {
			return new ConstraintViolationException(this.getWebEntryPoint(), cn);
		}
		return null;
	}

	/**
	 * 更新系SQLを実行します。
	 * <pre>
	 * insert, update, delete等の更新系SQLを実行します。
	 * dataの変換は行わず、そのまま保存します。
	 * dataList中の全Mapを指定されたSQLに渡して繰り返し実行します。
	 * </pre>
	 * @param sql 実行するSQL。
	 * @param dataList データリスト。
	 * @return 更新件数のリスト。
	 * @throws Exception 例外。
	 */
	public List<Integer> executeUpdate(final String sql, final List<Map<String, Object>> dataList) throws Exception {
		List<Integer> ret = new ArrayList<Integer>();
		SqlParser p = this.getSqlGenerator().newSqlParser(sql);
		Connection conn = this.getConnection();
		PreparedStatement st = conn.prepareStatement(p.getParsedSql());
		try {
			for (Map<String, Object> m: dataList) {
				p.setParameter(st, m);
				try {
					ret.add(st.executeUpdate());
				} finally {
					p.removeBlobTempFile(m);
				}
			}
		} catch (SQLException e) {
			ConstraintViolationException cne = this.getConstraintViolationException(e);
			if (cne != null) {
				throw cne;
			}
			throw e;
		} finally {
			st.close();
		}
		return ret;
	}


	/**
	 * 新規登録用のレコードIDを取得します。
	 * <pre>
	 * シーケンスを使用して、レコードIDを生成します。
	 * </pre>
	 * @param tbl レコードIDを取得するテーブル。
	 * @param data 登録するデータマップ。
	 * @throws Exception 例外。
	 */
	private void setNewSequenceValue(final Table tbl, final Map<String, Object> data) throws Exception {
		SqlGenerator gen = this.getSqlGenerator();
		if (tbl.recordIdExists() && tbl.isAutoIncrementId() && gen.isSequenceSupported()) {
			String id = tbl.getIdField().getId();
			if (StringUtil.isBlank(data.get(id))) {
				String sql = gen.generateGetRecordIdSql(tbl);
				Long value = Long.valueOf(NumberUtil.longValue(this.executeScalarQuery(sql, null)));
				data.put(id, value);
			}
		}
	}


	/**
	 * 自動加算されたIDを取得する。
	 *
	 * @param table テーブル。
	 * @param data データマップ。
	 * @throws Exception 例外.
	 */
	private void getAutoIncrementValue(final Table table, final Map<String, Object> data) throws Exception {
		SqlGenerator gen = this.getSqlGenerator();
		String keysql = gen.generateGetAutoIncrementValueSql(table);
		if (keysql != null) {
			if (table.isAutoIncrementId()) {
				String id = table.getIdField().getId();
				Object val = this.executeScalarQuery(keysql, null);
				data.put(id, Long.valueOf(NumberUtil.longValue(val)));
			}
		}
	}


	/**
	 * 指定されたテーブルに対して、データを挿入します。
	 * <pre>
	 * dataはフィールドの機能で変換(Value→DBValue)した後保存します。
	 * ID自動生成フラグがtrueの場合、シーケンスまたはauto_Incrementカラム属性で
	 * IDを自動生成し、data中にidフィールドの値が設定されます。
	 * </pre>
	 * @param table テーブル。
	 * @param data データ。
	 * @return 処理結果。
	 * @throws Exception 例外。
	 */
	public int executeInsert(final Table table, final Map<String, Object> data) throws Exception {
		SqlGenerator gen = this.getSqlGenerator();
		String sql = gen.generateInsertSql(table);
		this.setNewSequenceValue(table, data);
		int ret = this.executeUpdate(sql, this.convertToDBValue(table.getFieldList(), data));
		this.getAutoIncrementValue(table, data);
		return ret;
	}

	/**
	 * 指定されたテーブルに対して、データを挿入します。
	 * <pre>
	 * dataはフィールドの機能で変換(Value→DBValue)した後保存します。
	 * ID自動生成フラグがtrueの場合、シーケンスまたはauto_Incrementカラム属性で
	 * IDを自動生成し、data中にidフィールドの値が設定されます。
	 * </pre>
	 * @param table テーブル。
	 * @param data データ。
	 * @return 処理結果。
	 * @throws Exception 例外。
	 */
	public int executeInsert0(final Table table, final Map<String, Object> data) throws Exception {
		SqlGenerator gen = this.getSqlGenerator();
		String sql = gen.generateInsertSql0(table);
		this.setNewSequenceValue(table, data);
		int ret = this.executeUpdate(sql, this.convertToDBValue(table.getFieldList(), data));
		this.getAutoIncrementValue(table, data);
		return ret;
	}


	/**
	 * 指定されたテーブルに対して、dataList中の全データをデータを挿入します。
	 * <pre>
	 * dataList中のデータはフィールドの機能で変換(Value→DBValue)した後保存します。
	 * </pre>
	 * @param table テーブル。
	 * @param dataList データリスト。
	 * @return 結果リスト。
	 * @throws Exception 例外。
	 */
	public List<Integer> executeInsert(final Table table, final List<Map<String, Object>> dataList) throws Exception {
		SqlGenerator gen = this.getSqlGenerator();
		String sql = gen.generateInsertSql(table);
		List<Integer> ret = new ArrayList<Integer>();
		for (Map<String, Object> data: dataList) {
			this.setNewSequenceValue(table, data);
			ret.add(this.executeUpdate(sql, this.convertToDBValue(table.getFieldList(), data)));
			this.getAutoIncrementValue(table, data);
		}
		return ret;
	}

	/**
	 * 削除ファイルリスト。
	 * @param table テーブル。
	 * @param data データ。
	 * @param forDelete 削除用フラグ。
	 * @return 削除ファイルリスト。
	 * @throws Exception 例外。
	 */
	private List<String> getOldFileList(final Table table, final Map<String, Object> data, final boolean forDelete) throws Exception {
		List<String> ret = new ArrayList<String>();
		for (Field<?> f: table.getFieldList()) {
			if (f instanceof FileObjectField && f instanceof SqlVarchar) {
				String kf = (String) data.get(f.getId() + "Kf");
				if ("0".equals(kf) || forDelete) {
					FileObjectQuery query = new FileObjectQuery(table, f.getId(), data);
					Map<String, Object> map = this.executeRecordQuery(query);
					FileObject oldfile = (FileObject) map.get(f.getId());
					if (oldfile != null) {
						File tf = oldfile.getTempFile();
						if (tf != null) {
							ret.add(tf.getAbsolutePath());
							logger.info(() -> "deleteFile=" + tf.getAbsolutePath() + "," + kf);
						}
					}
				}
			}
		}
		return ret;
	}

	/**
	 * 古いファイルの削除を行います。
	 * @param list 古いファイルリスト。
	 * @throws Exception 例外。
	 */
	private void deleteOldFile(final List<String> list) throws Exception {
		for (String f: list) {
			File file = new File(f);
			file.delete();
		}
	}

	/**
	 * 指定テーブル更新を行います。
	 * <pre>
	 * テーブルに対応した、適切なupdate文を作成し実行します。
	 * Where句はテーブルの全PKがマッチするように作成します。
	 * dataはフィールドの機能で変換(Value→DBValue)した後保存します。
	 * テーブル中にFolderStoreFileFieldが存在し、そのフィールドが更新された場合、
	 * 既に記録されていたファイルは削除されます。
	 * </pre>
	 * @param table テーブル。
	 * @param data 更新データ。
	 * @return 更新結果。
	 * @throws Exception 例外。
	 */
	public int executeUpdate(final Table table, final Map<String, Object> data) throws Exception {
		String sql = this.getSqlGenerator().generateUpdateSql(table);
		Map<String, Object> dbdata = this.convertToDBValue(table.getFieldList(), data);
		List<String> dellist = this.getOldFileList(table, dbdata, false);
		int ret = this.executeUpdate(sql, dbdata);
		this.deleteOldFile(dellist);
		return ret;
	}

	/**
	 * 指定テーブル更新を行います。
	 * <pre>
	 * テーブルに対応した、適切なupdate文を作成し実行します。
	 * Where句はテーブルの全PKがマッチするように作成します。
	 * dataはフィールドの機能で変換(Value→DBValue)した後保存します。
	 * テーブル中にFolderStoreFileFieldが存在し、そのフィールドが更新された場合、
	 * 既に記録されていたファイルは削除されます。
	 * </pre>
	 * @param table テーブル。
	 * @param data 更新データ。
	 * @return 更新結果。
	 * @throws Exception 例外。
	 */
	public int executeUpdate0(final Table table, final Map<String, Object> data) throws Exception {
		String sql = this.getSqlGenerator().generateUpdateSql0(table);
		Map<String, Object> dbdata = this.convertToDBValue(table.getFieldList(), data);
		List<String> dellist = this.getOldFileList(table, dbdata, false);
		int ret = this.executeUpdate(sql, dbdata);
		this.deleteOldFile(dellist);
		return ret;
	}
	/**
	 * 指定テーブルの更新を行ないます。
	 * <pre>
	 * テーブルに対応した、適切なupdate文を作成し、dataListの全要素に対して実行すします。
	 * Where句はテーブルの全PKがマッチするように作成します。
	 * dataはフィールドの機能で変換(Value→DBValue)した後保存します。
	 * テーブル中にFolderStoreFileFieldが存在し、そのフィールドが更新された場合、
	 * 既に記録されていたファイルは削除されます。
	 * </pre>
	 * @param table テーブル。
	 * @param dataList 更新データリスト。
	 * @return 更新結果。
	 * @throws Exception 例外。
	 */
	public List<Integer> executeUpdate(final Table table, final List<Map<String, Object>> dataList) throws Exception {
		List<Integer> ret = new ArrayList<Integer>();
		String sql = this.getSqlGenerator().generateUpdateSql(table);
		for (Map<String, Object> data: dataList) {
			Map<String, Object> dbdata = this.convertToDBValue(table.getFieldList(), data);
			List<String> dellist = this.getOldFileList(table, dbdata, false);
			ret.add(this.executeUpdate(sql, dbdata));
			this.deleteOldFile(dellist);
		}
		return ret;
	}


	/**
	 * テーブル中のレコードを更新します。
	 * <pre>
	 * updateFieldList,condFieldListの内容から指定テーブルの更新SQLを作成し実行します。
	 * dataはフィールドの機能で変換(Value→DBValue)した後保存します。
	 * </pre>
	 * @param table テーブル。
	 * @param updateFieldList 更新対象のフィールドリスト。
	 * @param condFieldList 更新条件フィールドリスト。
	 * @param data データ。
	 * @param fullmatch 完全マッチフラグ。
	 * <pre>
	 * 	trueの場合condFieldList中の全項目に対する完全一致のwhere句が作成されます。
	 *  falseの場合condFieldList中の項目の内、data中に存在する項目のwhere句が作成されます。
	 *  テーブル中にFolderStoreFileFieldが存在し、そのフィールドが更新されても、
	 *  既に記録されていたファイルは削除されません。
	 * </pre>
	 * @return 更新結果。
	 * @throws Exception 例外。
	 */
	public int executeUpdate(final Table table, final FieldList updateFieldList, final FieldList condFieldList, final Map<String, Object> data, final boolean fullmatch) throws Exception {
		int ret = 0;
		Map<String, Object> m = this.convertToDBValue(table.getFieldList(), data);
		if (fullmatch) {
			String sql = this.getSqlGenerator().generateUpdateSql(table, updateFieldList, condFieldList);
			ret = this.executeUpdate(sql, m);
		} else {
			String sql = this.getSqlGenerator().generateUpdateSql(table, updateFieldList, condFieldList, m);
			ret = this.executeUpdate(sql, m);
		}
		return ret;
	}

	/**
	 * テーブル中の指定レコードを削除します。
	 * <pre>
	 * テーブルに対応した、適切なdelete文を作成し実行します。
	 * Where句はテーブルの全PKがマッチするように作成します。
	 * テーブル中にFolderStoreFileFieldが存在した場合、そのフィールドに対応したファイルを削除します。
	 * </pre>
	 * @param table テーブル.
	 * @param data 削除対象のレコードを示すデータ.
	 * @return 更新結果.
	 * @throws Exception 例外.
	 */
	public int executeDelete(final Table table, final Map<String, Object> data) throws Exception {
		Map<String, Object> m = this.convertToDBValue(table.getFieldList(), data);
		List<String> dellist = this.getOldFileList(table, m, true);
		String sql = this.getSqlGenerator().generateDeleteSql(table);
		int ret = this.executeUpdate(sql, m);
		this.deleteOldFile(dellist);
		return ret;
	}

	/**
	 * 指定されたテーブルに対して、dataList中の全データに対応するレコードを削除します。
	 * <pre>
	 * テーブルに対応した適切なdelete文を作成し、dataListの全要素に対して実行します。
	 * Where句はテーブルの全PKがマッチするように作成します。
	 * テーブル中にFolderStoreFileFieldが存在した場合、そのフィールドに対応したファイルを削除します。
	 * </pre>
	 * @param table テーブル。
	 * @param dataList 更新データリスト。
	 * @return 更新結果。
	 * @throws Exception 例外。
	 */

	public List<Integer> executeDelete(final Table table, final List<Map<String, Object>> dataList) throws Exception {
		List<Integer> ret = new ArrayList<Integer>();
		String sql = this.getSqlGenerator().generateDeleteSql(table);
		for (Map<String, Object> data: dataList) {
			Map<String, Object> m = this.convertToDBValue(table.getFieldList(), data);
			List<String> dellist = this.getOldFileList(table, m, true);
			ret.add(this.executeUpdate(sql, m));
			this.deleteOldFile(dellist);
		}
		return ret;
	}


	/**
	 * テーブル中のレコードを削除します。
	 * <pre>
	 * condFieldListの内容とdataから指定テーブルの削除SQLを作成し実行します。
	 * </pre>
	 * @param table テーブル。
	 * @param condFieldList 更新条件フィールドリスト。
	 * @param data データ。
	 * @param fullmatch 完全マッチフラグ。
	 * <pre>
	 * trueの場合condFieldList中の全項目に対する完全一致のwhere句が作成されます。
	 * falseの場合condFieldList中の項目の内、data中に存在する項目のwhere句が作成されます。
	 * テーブル中にFolderStoreFileFieldが存在した場合、そのフィールドに対応したファイルは削除されません。
	 * </pre>
	 * @return 更新結果。
	 * @throws Exception 例外。
	 */
	public int executeDelete(final Table table, final FieldList condFieldList, final Map<String, Object> data, final boolean fullmatch) throws Exception {
		int ret = 0;
		Map<String, Object> m = this.convertToDBValue(table.getFieldList(), data);
		if (fullmatch) {
			String sql = this.getSqlGenerator().generateDeleteSql(table, condFieldList);
			ret =this.executeUpdate(sql, m);
		} else {
			String sql = this.getSqlGenerator().generateDeleteSql(table, condFieldList, data);
			ret = this.executeUpdate(sql, m);
		}
		return ret;
	}

	/**
	 * フォルダ保存ファイルフィールドが存在するテーブルを判定します。
	 * @param table テーブル。
	 * @return フォルダ保存ファイルフィールドが存在するテーブルの場合true。
	 */
	private boolean folderStoreFileExists(final Table table) {
		boolean ret = false;
		for (Field<?> f: table.getFieldList()) {
			if (f instanceof FileObjectField && f instanceof SqlVarchar) {
				ret = true;
				break;
			}
		}
		return ret;
	}


	/**
	 * 指定されたファイルストアを削除します。
	 * @param f ファイルストアを指すフォルダ。
	 */
	private void deleteFolderFileStore(final File f) {
		if (!f.exists()) {
			return;
		}
		if (f.isFile()) {
			f.delete();
		} else if (f.isDirectory()) {
			File[] files = f.listFiles();
			for (int i = 0; i < files.length; i++) {
				deleteFolderFileStore(files[i]);
			}
			f.delete();
		}
	}

	/**
	 * 全レコードの削除を行います。
	 * @param table テーブル。
	 * @return 削除件数。
	 * @throws Exception 例外。
	 *
	 */
	public int deleteAllRecord(final Table table) throws Exception {
		if (this.folderStoreFileExists(table)) {
			String store = DataFormsServlet.getUploadDataFolder() + "/" + table.getClass().getSimpleName();
			this.deleteFolderFileStore(new File(store));
		}
		String sql = this.getSqlGenerator().generateDeleteAllSql(table.getTableName());
		return this.executeUpdate(sql, (Map<String, Object>) null);
	}


	/**
	 * 指定レコードの削除フラグを設定します。
	 * <pre>
	 * Where句はテーブルの全PKがマッチするように作成します。
	 * </pre>
	 * @param table テーブル.
	 * @param data 削除対象のレコードを示すデータ.
	 * @return 更新結果.
	 * @throws Exception 例外.
	 */
	public int executeRemove(final Table table, final Map<String, Object> data) throws Exception {
		String sql = this.getSqlGenerator().generateRemoveSql(table);
		return this.executeUpdate(sql, this.convertToDBValue(table.getFieldList(), data));
	}

	/**
	 * 指定レコードの削除フラグを設定します。
	 * <pre>
	 * Where句はテーブルの全PKがマッチするように作成します。
	 * </pre>
	 * @param table テーブル。
	 * @param dataList 更新データリスト。
	 * @return 更新結果。
	 * @throws Exception 例外。
	 */

	public List<Integer> executeRemove(final Table table, final List<Map<String, Object>> dataList) throws Exception {
		List<Integer> ret = new ArrayList<Integer>();
		String sql = this.getSqlGenerator().generateRemoveSql(table);
		for (Map<String, Object> data: dataList) {
			ret.add(this.executeUpdate(sql, this.convertToDBValue(table.getFieldList(), data)));
		}
		return ret;
	}


	/**
	 * 指定レコードの削除フラグを設定します。
	 *
	 * @param table テーブル。
	 * @param condFieldList 更新条件フィールドリスト。
	 * @param data データ。
	 * @param fullmatch 完全マッチフラグ。
	 * <pre>
	 * 	trueの場合condFieldList中の全項目に対する完全一致のwhere句が作成されます。
	 *  falseの場合condFieldList中の項目の内、data中に存在する項目のwhere句が作成されます。
	 * </pre>
	 * @return 更新結果。
	 * @throws Exception 例外。
	 */
	public int executeRemove(final Table table, final FieldList condFieldList, final Map<String, Object> data, final boolean fullmatch) throws Exception {
		Map<String, Object> m = this.convertToDBValue(table.getFieldList(), data);
		if (fullmatch) {
			String sql = this.getSqlGenerator().generateRemoveSql(table, condFieldList);
			return this.executeUpdate(sql, m);
		} else {
			String sql = this.getSqlGenerator().generateRemoveSql(table, condFieldList, m);
			return this.executeUpdate(sql, m);
		}
	}


	/**
	 * カラム情報を取得します。
	 * @param rs 結果セット。
	 * @return カラム情報。
	 * @throws Exception 例外。
	 */
	private Map<String, Object> getColiumnInfo(final ResultSet rs) throws Exception {
		SqlGenerator gen = this.getSqlGenerator();
		Map<String, Object> colinfo = new HashMap<String, Object>();
		String cat = rs.getString("TABLE_CAT");
		String schem = rs.getString("TABLE_SCHEM");
		String name = rs.getString("COLUMN_NAME");
		String type = rs.getString("TYPE_NAME");
		int size = rs.getInt("COLUMN_SIZE");
		int scale = rs.getInt("DECIMAL_DIGITS");
		int nullable = rs.getInt("NULLABLE");
		String dataType = gen.converTypeNameForDatabaseMetaData(type);
		if ("char".equals(dataType) || "nchar".equals(dataType) || "varchar".equals(dataType) || "varchar2".equals(dataType) || "nvarchar".equals(dataType) || "nvarchar2".equals(dataType)) {
			dataType += "(" + gen.convertColumnSize(size) + ")";
		} else if ("numeric".equals(dataType) || "number".equals(dataType)) {
			dataType += "(" + size + "," + scale + ")";
		}
		if (nullable == 0) {
			dataType += " not null";
		}
		logger.debug("{} {} {} {}", cat, schem, name, dataType);
		colinfo.put("columnName", name.toLowerCase());
		colinfo.put("dataType", dataType);
		return colinfo;
	}

	/**
	 * Schemaを取得します。
	 * @param conn JDBC接続情報。
	 * @return 接続しているSchema。
	 */
	protected String getSchema(final Connection conn) {
		String schema = null;
		try {
			schema = conn.getSchema();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return schema;
	}

	/**
	 * テーブル情報クラス。
	 *
	 */
	public static class TableInfoEntity extends Entity {
		/**
		 * テーブル名。
		 */
		public static final String ID_TABLE_NAME = "tableName";
		/**
		 * 注釈。
		 */
		public static final String ID_REMARKS = "remarks";

		/**
		 * コンストラクタ。
		 */
		public TableInfoEntity() {

		}

		/**
		 * コンストラクタ。
		 * @param m マップ。
		 */
		public TableInfoEntity(final Map<String, Object> m) {
			super(m);
		}

		/**
		 * テーブル名を取得します。
		 * @return テーブル名。
		 */
		public String getTableName() {
			return (String) this.getMap().get(ID_TABLE_NAME);
		}

		/**
		 * テーブル名を設定します。
		 * @param tableName テーブル名。
		 */
		public void setTableName(final String tableName) {
			this.getMap().put(ID_TABLE_NAME, tableName);
		}

		/**
		 * 注釈を取得します。
		 * @return 注釈。
		 */
		public String getRemarks() {
			return (String) this.getMap().get(ID_REMARKS);
		}

		/**
		 * 注釈を設定します。
		 * @param remarks 注釈。
		 */
		public void setRemarks(final String remarks) {
			this.getMap().put(ID_REMARKS, remarks);
		}

	}


	/**
	 * データベース中に存在するテーブルリストを取得します。
	 *
	 * @return テーブルリスト。
	 * @throws Exception 例外。
	 */
	public List<Map<String, Object>> queryTableInfo() throws Exception {
		Connection conn = this.getConnection();
		DatabaseMetaData md = conn.getMetaData();
		logger.debug("currentCatalog={}", conn.getCatalog());
		String schema = conn.getSchema();
		logger.debug("currentSchema={}", schema);
		List<Map<String, Object>> tableList = new ArrayList<Map<String, Object>>();
		try (ResultSet rs = md.getTables(conn.getCatalog(), schema, "%", new String[]{"TABLE"})) {
			while (rs.next()) {
				TableInfoEntity e = new TableInfoEntity();
				e.setTableName(rs.getString("TABLE_NAME"));
				e.setRemarks(rs.getString("REMARKS"));
				tableList.add(e.getMap());
			}
		}
		return tableList;
	}

	/**
	 * 指定されたテーブルの情報を取得します。
	 * @param table テーブル名。
	 * @return 指定されたテーブルの情報。
	 * @throws Exception 例外。
	 */
	public Map<String, Object> queryTableInfo(final String table) throws Exception {
		Connection conn = this.getConnection();
		DatabaseMetaData md = conn.getMetaData();
		logger.debug("currentCatalog={}", conn.getCatalog());
		String schema = conn.getSchema();
		logger.debug("currentSchema={}", schema);
		TableInfoEntity e = new TableInfoEntity();
		try (ResultSet rs = md.getTables(conn.getCatalog(), schema, table, new String[]{"TABLE"})) {
			if (rs.next()) {
				e.setTableName(rs.getString("TABLE_NAME"));
				e.setRemarks(rs.getString("REMARKS"));
			}
		}
		return e.getMap();
	}

	/**
	 * テーブル構造取得します。
	 * @param tblname テーブル名。
	 * @return テーブル構造。
	 * @throws Exception 例外。
	 */
	public List<Map<String, Object>> getTableColumnList(final String tblname) throws Exception {
		SqlGenerator gen = this.getSqlGenerator();
		Connection conn = this.getConnection();
		DatabaseMetaData md = conn.getMetaData();
		logger.debug("currentCatalog={}" + conn.getCatalog());
		String schema = getSchema(conn);
		logger.debug("currentSchema={}" + schema);
		List<Map<String, Object>> collist = new ArrayList<Map<String, Object>>();
		ResultSet rs = md.getColumns(conn.getCatalog(), schema, gen.convertTableNameForDatabaseMetaData(tblname), "%");
		logger.debug("----\n");
		try {
			while (rs.next()) {
				Map<String, Object> m = this.getColiumnInfo(rs);
				collist.add(m);
			}
		} finally {
			rs.close();
		}
		logger.debug("----\n");
		return collist;
	}


	/**
	 * 非一意フラグを取得します。
	 * <pre>
	 * OracleのみnonUniqueが数値型(Booleanではない)ので、
	 * このメソッドでその違いを吸収しています。
	 * </pre>
	 * @param nonUnique nonUniqueカラムのオブジェクト。
	 * @return Boolean型のフラグ。
	 */
	private Boolean getNonUnique(final Object nonUnique) {
		if (nonUnique == null) {
			// MS SQL-Serverはnullを返してくる。
			return false;
		}
		if (nonUnique instanceof BigDecimal) {
			BigDecimal v = (BigDecimal) nonUnique;
			return v.compareTo(BigDecimal.valueOf(0.0)) != 0;
		} else if (nonUnique instanceof Short) {
			Short v = (Short) nonUnique;
			return v != 0;
		} else if (nonUnique instanceof Long) {
			Long v = (Long) nonUnique;
			return v != 0;
		} else {
			return (Boolean) nonUnique;
		}
	}

	/**
	 * 指定されたテーブルのインデックスを取得します。
	 * @param md データベースメタデータ。
	 * @param catalog カタログ。
	 * @param schema スキーマ。
	 * @param table テーブル。
	 * @param unique ユニークフラグ。
	 * @return インデックス情報。
	 * @throws Exception 例外。
	 */
	private List<Map<String, Object>> getCurrentDBIndexInfo(final DatabaseMetaData md, final String catalog, final String schema, final String table, final boolean unique) throws Exception {
		List<Map<String, Object>> ret = new ArrayList<Map<String, Object>>();
		logger.debug(() -> "catalog=" + catalog + ", schema=" + schema + ", table=" + table + ", unique=" + unique);
		ResultSet rset = md.getIndexInfo(catalog, schema, table, unique, false);
		try {
			ResultSetMetaData rmd = rset.getMetaData();
			while (rset.next()) {
				Map<String, Object> m = new HashMap<String, Object>();
				for (int i = 0; i < rmd.getColumnCount(); i++) {
					String name = StringUtil.snakeToCamel(rmd.getColumnName(i + 1).toLowerCase());
					Object value = rset.getObject(i + 1);
					m.put(name, value);
				}
				logger.info("md=" + JsonUtil.encode(m, true));
				Object nu = m.get("nonUnique");
				Boolean nonUnique = this.getNonUnique(nu);
				logger.debug(() -> "nu=" + nu + ", nonUnique=" + nonUnique);
				if (nonUnique != unique) {
					ret.add(m);
				}
			}
		} finally {
			rset.close();
		}
		return ret;

	}


	/**
	 * 指定されたテーブルのデータベース中のインデックス情報を取得します。
	 * @param table テーブル。
	 * @return インデックス情報。
	 * @throws Exception 例外。
	 */
	public List<Map<String, Object>> getCurrentDBIndexInfo(final Table table) throws Exception {
		SqlGenerator gen = this.getSqlGenerator();
		Connection conn = this.getConnection();
		DatabaseMetaData md = conn.getMetaData();
		String catalog = conn.getCatalog();
		String schema = getSchema(conn);
		logger.debug(() -> "currentSchema=" + schema);
		String tablename = gen.convertTableNameForDatabaseMetaData(table.getTableName());
		List<Map<String, Object>> ret = this.getCurrentDBIndexInfo(md, catalog, schema, tablename, true);
		ret.addAll(this.getCurrentDBIndexInfo(md, catalog, schema, tablename, false));
		logger.debug(() -> "indexInfo=" + JsonUtil.encode(ret, true));
		return ret;
	}


	/**
	 * 指定されたテーブルの外部キー情報を取得します。
	 * @param md データベースメタデータ。
	 * @param catalog カタログ。
	 * @param schema スキーマ。
	 * @param table テーブル。
	 * @return インデックス情報。
	 * @throws Exception 例外。
	 */
	private List<Map<String, Object>> getCurrentDBForeignKeyInfo(final DatabaseMetaData md, final String catalog, final String schema, final String table) throws Exception {
		List<Map<String, Object>> ret = new ArrayList<Map<String, Object>>();
		logger.debug(() -> "catalog=" + catalog + ", schema=" + schema + ", table=" + table);
		ResultSet rset = md.getImportedKeys(catalog, schema, table);
		try {
			ResultSetMetaData rmd = rset.getMetaData();
			while (rset.next()) {
				Map<String, Object> m = new HashMap<String, Object>();
				for (int i = 0; i < rmd.getColumnCount(); i++) {
					String name = StringUtil.snakeToCamel(rmd.getColumnName(i + 1).toLowerCase());
					Object value = rset.getObject(i + 1);
					logger.debug(() -> "name=" + name + ", value=" + value);
					m.put(name, value);
				}
				ret.add(m);
			}
		} finally {
			rset.close();
		}
		return ret;

	}



	/**
	 * 指定されたテーブルのデータベース中の外部キー情報を取得します。
	 * @param table テーブル。
	 * @return インデックス情報。
	 * @throws Exception 例外。
	 */
	public List<Map<String, Object>> getCurrentDBForeignKeyInfo(final Table table) throws Exception {
		SqlGenerator gen = this.getSqlGenerator();
		Connection conn = this.getConnection();
		DatabaseMetaData md = conn.getMetaData();
		String catalog = conn.getCatalog();
		String schema = getSchema(conn);
		logger.debug(() -> "currentSchema=" + schema);
		String tablename = gen.convertTableNameForDatabaseMetaData(table.getTableName());
		List<Map<String, Object>> ret = this.getCurrentDBForeignKeyInfo(md, catalog, schema, tablename);
		logger.debug(() -> "ForeignKey Info=" + JsonUtil.encode(ret, true));
		return ret;
	}

	/**
	 * 外部キーの名前の集合を取得します。
	 * @param table テーブル。
	 * @return 外部キーの名前の集合。
	 * @throws Exception 例外。
	 */
	protected Set<String> getForeignKeyNameSet(final Table table) throws Exception {
		List<Map<String, Object>> fklist = this.getCurrentDBForeignKeyInfo(table);
		Set<String> fkset = new HashSet<String>();
		for (Map<String, Object> m: fklist) {
			String fkName = (String) m.get("fkName");
			fkset.add(fkName);
		}
		return fkset;
	}


	/**
	 * インデックスが存在するかどうかを確認します。
	 * @param table テーブル。
	 * @param fkname インデックス名。
	 * @return インデックスが存在する場合true。
	 * @throws Exception 例外。
	 */
	protected boolean foreignKeyExists(final Table table, final String fkname) throws Exception {
		boolean ret = false;
		List<Map<String, Object>> list = this.getCurrentDBForeignKeyInfo(table);
		for (Map<String, Object> m: list) {
			String indexName = (String) m.get("fkName");
			if (fkname.equalsIgnoreCase(indexName)) {
				ret = true;
			}
		}
		return ret;
	}


	/**
	 * インデックスが存在するかどうかを確認します。
	 * @param table テーブル。
	 * @param idxname インデックス名。
	 * @return インデックスが存在する場合true。
	 * @throws Exception 例外。
	 */
	protected boolean indexExists(final Table table, final String idxname) throws Exception {
		boolean ret = false;
		List<Map<String, Object>> list = this.getCurrentDBIndexInfo(table);
		for (Map<String, Object> m: list) {
			String indexName = (String) m.get("indexName");
			if (idxname.equalsIgnoreCase(indexName)) {
				ret = true;
			}
		}
		return ret;
	}

	/**
	 * 指定されたインデックスのフィールドリストを取得します。
	 * @param table テーブル。
	 * @param idxname インデックス名称。
	 * @return インデックスのフィールド情報。
	 * @throws Exception 例外。
	 */
	public List<Map<String, Object>> getIndexFieldList(final Table table, final String idxname) throws Exception {
		List<Map<String, Object>> ret = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> list = this.getCurrentDBIndexInfo(table);
		for (Map<String, Object> m: list) {
			String indexName = (String) m.get("indexName");
			if (idxname.equalsIgnoreCase(indexName)) {
				ret.add(m);
			}
		}
		return ret;
	}


	/**
	 * PKのリストを取得します。
	 * @param tbl テーブル。
	 * @return PKリスト。
	 * @throws Exception 例外。
	 */
	public List<String> getTablePkList(final Table tbl) throws Exception {
		SqlGenerator gen = this.getSqlGenerator();
		Connection conn = this.getConnection();
		DatabaseMetaData md = conn.getMetaData();
		List<String> collist = new ArrayList<String>();
		List<Short> seqlist = new ArrayList<Short>();
		String schema = getSchema(conn);
		logger.debug(() -> "currentSchema=" + schema);

		ResultSet rs = md.getPrimaryKeys(conn.getCatalog(), schema, gen.convertTableNameForDatabaseMetaData(tbl.getTableName()));
//		ResultSet rs = md.getPrimaryKeys("", "", tbl.getTableName().toLowerCase());
		try {
			while (rs.next()) {
				String name = rs.getString("COLUMN_NAME");
				collist.add(name.toLowerCase());
				short seq = rs.getShort("KEY_SEQ");
				seqlist.add(Short.valueOf((short) (seq - 1)));
			}
		} finally {
			rs.close();
		}
		List<String> pklist = new ArrayList<String>();
		for (int i = 0; i < collist.size(); i++) {
			pklist.add("");
		}
		for (int i = 0; i < collist.size(); i++) {
			pklist.set(seqlist.get(i), collist.get(i));
		}
		return pklist;
	}

	/**
	 * PKの最大値+1を取得します。
	 * <pre>
	 * 最下位PKの最大値+1を取得します。
	 * 最下位PKのタイプはBIGINTを想定します。
	 * </pre>
	 * @param tbl テーブル。
	 * @param param 上位のPKを指定するパラメータ。
	 * @return PKの最大値+1。
	 * @throws Exception 例外。
	 */
	public Long getGetNextPrimaryKey(final Table tbl, final Map<String, Object> param) throws Exception {
		SqlGenerator gen = this.getSqlGenerator();
		String sql = gen.generateGetLastPrimaryKeySql(tbl, param);
		Long ret = (Long) this.executeScalarQuery(sql, param);
		return ret.longValue() + 1;
	}


	/**
	 * 更新可能なレコードかどうかをチェックします。
	 * <pre>
	 * 楽観ロック用の更新可能チェック処理です。
	 * paramに含まれるupdateTimestampが一致することをチェックします。
	 * </pre>
	 * @param table テーブル。
	 * @param param パラメータ。
	 * @return 更新可能な場合true。
	 * @throws Exception 例外。
	 */
	public boolean isUpdatable(final Table table, final Map<String, Object> param) throws Exception {
		SqlGenerator gen = this.getSqlGenerator();
		if (table.getField(Entity.ID_UPDATE_TIMESTAMP) == null) {
			logger.warn("There is no updateUserId, updateTimestamp on this page.Therefore the exclusive control does not work.");
			return true;
		}
		String sql = gen.generateIsUpdatableSql(table);
		Map<String, Object> uinfo = this.executeRecordQuery(sql, param);
		if (uinfo != null) {
			Entity p = new Entity(param);
			java.sql.Timestamp ut0 = p.getUpdateTimestamp(); //(java.sql.Timestamp) param.get("updateTimestamp");
			if (/*uid0 != null &&*/ ut0 != null) {
				Entity u = new Entity(uinfo);
				java.sql.Timestamp ut1 = u.getUpdateTimestamp(); //(java.sql.Timestamp) uinfo.get("updateTimestamp");
//				return /*uid0.equals(uid1) &&*/ ut0.equals(ut1);
				logger.debug(() -> "isUpdatable:ut0=" + ut0.toString() + ",ut1=" + ut1.toString());
				return ut0.getTime() == ut1.getTime();
			} else {
				logger.warn("There is no updateUserId, updateTimestamp on this page.Therefore the exclusive control does not work.");
				return true;
			}
		} else {
			// TODO:この判定で良いか検討をする必要がある。
			return true;
		}
	}

	/**
	 * Query結果の指定ページを取得します。
	 * @param query 問い合わせ。
	 * @return 検索結果。
	 * @throws Exception 例外。
	 */
	public Map<String, Object> executePageQuery(final Query query) throws Exception {
		return this.executePageQuery(query, 10);
	}

	/**
	 * Query結果の指定ページを取得します。
	 * @param query 問い合わせ。
	 * @param defaultLines 1ページの行数のデフォルト値。
	 * @return 検索結果。
	 * @throws Exception 例外。
	 */
	public Map<String, Object> executePageQuery(final Query query, final int defaultLines) throws Exception {
		Map<String, Object> param = query.getConditionData();
		Map<String, Object> ret = new HashMap<String, Object>();
		int linesPerPage = defaultLines;
		if (param.get("linesPerPage") != null) {
			linesPerPage = ((Integer) param.get("linesPerPage")).intValue();
		}
		int pageNo = 0;
		if (param.get("pageNo") != null) {
			pageNo = ((Integer) param.get("pageNo")).intValue();
		}
		QueryPager qp = new QueryPager(query, linesPerPage);
		param.putAll(qp.getPageParameter(pageNo));
		Long hitCount = this.countQueryResoult(query);
		ret.put("hitCount", hitCount);
		ret.put("linesPerPage", linesPerPage);
		ret.put("pageNo", pageNo);
		//
		String psql = this.getSqlGenerator().generateGetPageSql(qp);
		FieldList flist = query.getFieldList();
		Map<String, Object> data = this.convertToDBValue(query.getConditionFieldList(), query.getConditionData());
		List<Map<String, Object>> list = this.convertFromDBValue(flist, this.executeQuery(psql, data));
		ret.put("queryResult", list);
		return ret;
	}


	/**
	 * 問い合わせ結果の件数を求める。
	 * @param query 問い合わせ。
	 * @return 結果件数。
	 * @throws Exception 例外。
	 */
	public Long countQueryResoult(final Query query) throws Exception {
		String csql = this.getSqlGenerator().generateHitCountSql(query);
		// FieldList qflist = query.getQueryFormFieldList();
		// Long hitCount = NumberUtil.longValue(this.executeScalarQuery(csql, qflist.convertServerToDb(query.getQueryFormData())));
		Map<String, Object> data = this.convertToDBValue(query.getConditionFieldList(), query.getConditionData());
		Long hitCount = NumberUtil.longValue(this.executeScalarQuery(csql, data));
		return hitCount;
	}

	/**
	 * 問合せ結果の指定ページを取得します。
	 * @param sql SQL。
	 * @param param パラメータ。
	 * @return 問合せ結果。
	 * @throws Exception 例外。
	 */
	public Map<String, Object> executePageQuery(final String sql, final Map<String, Object> param) throws Exception {
		return this.executePageQuery(sql, param, 10);
	}

	/**
	 * 問合せ結果の指定ページを取得します。
	 *
	 * @param sql SQL。
	 * @param param パラメータ。
	 * @param defaultLines 1ページの行数のデフォルト値。
	 *
	 * @return 問合せ結果。
	 * @throws Exception 例外。
	 */
	public Map<String, Object> executePageQuery(final String sql, final Map<String, Object> param, final int defaultLines) throws Exception {
		Map<String, Object> ret = new HashMap<String, Object>();
		int linesPerPage = defaultLines;
		if (param.get("linesPerPage") != null) {
			linesPerPage = ((Integer) param.get("linesPerPage")).intValue();
		}
		int pageNo = 0;
		if (param.get("pageNo") != null) {
			pageNo = ((Integer) param.get("pageNo")).intValue();
		}
		QueryPager qp = new QueryPager(sql, linesPerPage);
		param.putAll(qp.getPageParameter(pageNo));
		Long hitCount = this.countQueryResoult(sql, param);
		ret.put("hitCount", hitCount);
		ret.put("linesPerPage", linesPerPage);
		ret.put("pageNo", pageNo);
		//
		String psql = this.getSqlGenerator().generateGetPageSql(qp);
		List<Map<String, Object>> list = this.executeQuery(psql, param);
		ret.put("queryResult", list);
		return ret;
	}


	/**
	 * 問い合わせ結果の件数を求める。
	 * @param sql SQL。
	 * @param param パラメータ。
	 * @return 結果件数。
	 * @throws Exception 例外。
	 */
	private Long countQueryResoult(final String sql, final Map<String, Object> param) throws Exception {
		String csql = this.getSqlGenerator().generateHitCountSql(sql);
		Long hitCount = NumberUtil.longValue(this.executeScalarQuery(csql, param));
		return hitCount;
	}

	/**
	 * レコードの存在チェックを行います。
	 * @param table テーブル。
	 * @param flist フィールドリスト。
	 * @param data フォームデータ。
	 * @param forUpdate 更新用の存在チェックの場合true。
	 * 更新用の場合、更新対象以外のレコードに同一値があるかどうかをチェック。
	 * @return 存在する場合true。
	 * @throws Exception 例外。
	 */
	protected boolean existRecord(final Table table, final FieldList flist, final Map<String, Object> data, final boolean forUpdate) throws Exception {
		Query q = new Query();
		q.setFieldList(table.getPkFieldList());
		q.setMainTable(table);
		if (forUpdate) {
			StringBuilder sb = new StringBuilder();
			for (Field<?> f: table.getPkFieldList()) {
				if (sb.length() > 0) {
					sb.append(" or ");
				}
				sb.append("m.");
				sb.append(StringUtil.camelToSnake(f.getId()));
				sb.append("<>:");
				sb.append(StringUtil.camelToSnake(f.getId()));
				sb.append(" ");

			}
			q.setCondition("(" + sb.toString() + ")");
		}
		q.setConditionFieldList(flist);
		q.setConditionData(data);
//		String sql = this.getSqlGenerator().generateQuerySql(q);
//		List<Map<String, Object>> list = this.executeQuery(sql, data);
		List<Map<String, Object>> list = this.executeQuery(q);

		return (list.size() > 0);
	}


	/**
	 * レコードの存在チェックを行います。
	 * @param table テーブル。
	 * @param flist フィールドリスト。
	 * @param data フォームデータ。
	 * @return 存在する場合true。
	 * @throws Exception 例外。
	 */
	protected boolean existRecord(final Table table, final FieldList flist, final Map<String, Object> data) throws Exception {
		return this.existRecord(table, flist, data, false);
	}

	/**
	 * テーブルの存在チェックを行います。
	 * @param tablename テーブル名。
	 * @return 存在する場合true。
	 * @throws Exception 例外。
	 */
	public boolean tableExists(final String tablename) throws Exception {
		final SqlGenerator gen = this.getSqlGenerator();
		Dao dao = new Dao(this);
		String sql = gen.generateTableExistsSql();
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("tableName", tablename);
		int tblcnt = NumberUtil.intValue(dao.executeScalarQuery(sql, param));
		return (tblcnt > 0);
	}




	/**
	 * DBから読み込んだマップをアプリケーションで処理しやすい形式に変換します(DBValue→Value変換)。
	 * @param flist フィールドリスト。
	 * @param m DBから読み込んだマップ。
	 * @return 返還後のマップ。
	 */
	protected Map<String, Object> convertFromDBValue(final FieldList flist, final Map<String, Object> m) {
		if (m == null) {
			return m;
		}
		Map<String, Object> ret = new HashMap<String, Object>();
		// 一旦全要素をコピーする。
		ret.putAll(m);
		if (flist != null) {
			// 変換が必要なものは変換してコピーする。
			ret.putAll(flist.convertDbToServer(m));
		}
		return ret;
	}

	/**
	 * DBから読み込んだリストをアプリケーションで処理しやすい形式に変換します(DBValue→Value変換)。
	 * @param flist フィールドリスト。
	 * @param list リスト。
	 * @return 変換後のリスト。
	 */
	protected List<Map<String, Object>> convertFromDBValue(final FieldList flist, final List<Map<String, Object>> list) {
		List<Map<String, Object>> ret = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> m: list) {
			ret.add(this.convertFromDBValue(flist, m));
		}
		return ret;
	}
	/**
	 * マップの内容をDBの保存形式に変換します(Value→DBValue変換)。
	 * @param flist フィールドリスト。
	 * @param m マップ。
	 * @return 返還後のマップ。
	 */
	protected Map<String, Object> convertToDBValue(final FieldList flist, final Map<String, Object> m) {
		if (m == null) {
			return m;
		}
		Map<String, Object> ret = new HashMap<String, Object>();
		// 一旦全要素をコピーする。
		ret.putAll(m);
		if (flist != null) {
			// 変換が必要なものは変換してコピーする。
			ret.putAll(flist.convertServerToDb(m));
		}
		// TODO:条件がテーブルになる場合の変換を検討。
		return ret;
	}

	/**
	 * リストの内容をDBの保存形式に変換します(Value→DBValue変換)。
	 * @param flist フィールドリスト。
	 * @param list リスト。
	 * @return 返還後のリスト。
	 */
	protected List<Map<String, Object>> convertToDBValue(final FieldList flist, final List<Map<String, Object>> list) {
		List<Map<String, Object>> ret = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> m: list) {
			ret.add(this.convertToDBValue(flist, m));
		}
		return ret;
	}

	/**
	 * BLOBデータを取得する問い合わせを行います。
	 *
	 */
	private class FileObjectQuery extends Query {
		/**
		 * コンストラクタ。
		 * @param table テーブル。
		 * @param fieldId BLOBフィールドID。
		 * @param data パラメータ。
		 */
		public FileObjectQuery(final Table table, final String fieldId, final Map<String, Object> data) {
			FieldList flist = new FieldList();
			flist.addAll(table.getPkFieldList());
			flist.add(table.getField(fieldId));
			this.setFieldList(flist);
			this.setMainTable(table);
			this.setConditionFieldList(table.getPkFieldList());
			this.setConditionData(data);
		}
	}

	/**
	 * BLOBデータを取得します。
	 * @param table テーブル。
	 * @param fieldId BLOBフィールドID。
	 * @param data パラメータ。
	 * @return BLOBデータ。
	 * @throws Exception 例外。
	 */
	public FileObject queryBlobFileObject(final Table table, final String fieldId, final Map<String, Object> data) throws Exception {
		this.setBlobReadMode(BlobReadMode.FOR_DOWNLOAD);
		FileObjectQuery query = new FileObjectQuery(table, fieldId, data);
		Map<String, Object> ret = (Map<String, Object>) this.executeRecordQuery(query);
		this.setBlobReadMode(BlobReadMode.FOR_DISPLAY_FILE_INFO);
		return (FileObject) ret.get(fieldId);
	}


	/**
	 * BLOBデータの情報のみを取得します。
	 * @param table テーブル。
	 * @param fieldId BLOBフィールドID。
	 * @param data パラメータ。
	 * @return BLOBデータ。
	 * @throws Exception 例外。
	 */
	public FileObject queryBlobFileInfo(final Table table, final String fieldId, final Map<String, Object> data) throws Exception {
		FileObjectQuery query = new FileObjectQuery(table, fieldId, data);
		Map<String, Object> ret = (Map<String, Object>) this.executeRecordQuery(query);
		return (FileObject) ret.get(fieldId);
	}



	/**
	 * 明細テーブルの保存を行います。
	 * <pre>
	 * PKの配置がヘッダID,明細IDとなっていることを前提条件として、明細を保存します。
	 * ヘッダテーブルに対応した明細テーブルの保存は、通常対応レコードの
	 * 全削除、挿入で実装するのが簡単です。
	 * しかしBLOB項目等を含む場合の更新は毎回ファイルをやり取りするわけでは
	 * ないので、複雑な更新処理が必要になります。
	 * このメソッドは以下のロジックでBLOBを含む明細テーブルの更新に対応します。
	 *
	 * 1.listに含まなれない既存明細レコードを削除します。
	 * 2.明細のIDがnullの場合、明細のIDを作成し挿入します。
	 * 3.明細のIDがnullでない場合、対応レコードを更新します。
	 *
	 * </pre>
	 * @param table 明細テーブル。
	 * @param list 保存する明細リスト。
	 * @param data ヘッダ情報。
	 * @throws Exception 例外。
	 */
	protected void saveTable(final Table table, final List<Map<String, Object>> list, final Map<String, Object> data) throws Exception {
		SqlGenerator gen = this.getSqlGenerator();
		String delsql = gen.generateSelectNotInListSql(table, list);
		if (delsql != null) {
//			this.executeUpdate(delsql, data);
			List<Map<String, Object>> dellist = this.executeQuery(delsql, data);
			for (Map<String, Object> m: dellist) {
				this.executeDelete(table, m);
			}
		}
		String maxsql = gen.generateGetLastPrimaryKeySql(table, data);
		FieldList pklist = table.getPkFieldList();
		Field<?> lastpk = pklist.get(pklist.size() - 1);
		for (Map<String, Object> m: list) {
			if (m.get(lastpk.getId()) == null) {
				if (lastpk instanceof SmallintField) {
					short key = (short) (NumberUtil.shortValue(this.executeScalarQuery(maxsql, data)) + 1);
					m.put(lastpk.getId(), Short.valueOf(key));
				} else if (lastpk instanceof IntegerField) {
					int key = (int) (NumberUtil.intValue(this.executeScalarQuery(maxsql, data)) + 1);
					m.put(lastpk.getId(), Integer.valueOf(key));
				} else if (lastpk instanceof BigintField) {
					long key = (long) (NumberUtil.longValue(this.executeScalarQuery(maxsql, data)) + 1);
					m.put(lastpk.getId(), Long.valueOf(key));
				} else {
					throw new Exception("Unsupported primary key type.");
				}
				this.executeInsert(table, m);
			} else {
				if (this.findRecord(table, m) == null) {
					this.executeInsert(table, m);
				} else {
					this.executeUpdate(table, m);
				}
			}
		}
	}

	/**
	 * 指定されたレコードと同じPKを持つレコードがリスト中に存在するかどうかをチェックします。
	 *
	 * @param table テーブル。
	 * @param rec レコード。
	 * @param list レコードリスト。
	 * @return レコードがリスト中に存在する場合true。
	 */
	protected boolean existRecord(final Table table, final Map<String, Object> rec, final List<Map<String, Object>> list) {
		boolean ret = false;
		FieldList pklist = table.getPkFieldList();
		for (Map<String, Object> m: list) {
			boolean eq = true;
			for (Field<?> f: pklist) {
				Object k0 = rec.get(f.getId());
				Object k1 = m.get(f.getId());
				if (k1 == null) {
					eq = false;
					continue;
				}
				if (!k1.equals(k0)) {
					eq = false;
					break;
				}
			}
			if (eq) {
				ret = true;
				break;
			}
		}
		return ret;
	}

	/**
	 * oldlistに存在し、listに存在しないレコードを削除します。
	 * @param table テーブル。
	 * @param list 新リスト。
	 * @param oldlist 旧リスト。
	 * @throws Exception 例外。
	 */
	protected void deleteNotExistRecord(final Table table, final List<Map<String, Object>> list, final List<Map<String, Object>> oldlist) throws Exception {
		for (Map<String, Object> om: oldlist) {
			if (!this.existRecord(table, om, list)) {
				this.executeDelete(table, om);
			}
		}
	}

	/**
	 * 明細テーブルの保存を行います。
	 * <pre>
	 * PKが明細IDのみでヘッダのIDを別途持つことを前提条件として明細を保存します。
	 *
	 * ヘッダテーブルに対応した明細テーブルの保存は、通常対応レコードの
	 * 全削除、挿入で実装するのが簡単です。
	 * しかしBLOB項目等を含む場合の更新は毎回ファイルをやり取りするわけでは
	 * ないので、複雑な更新処理が必要になります。
	 * このメソッドは以下のロジックでBLOBを含む明細テーブルの更新に対応します。
	 *
	 * 1.listに含まなれない既存レコードを削除します。
	 * 2.レコードのIDがnullの場合、レコードのIDを作成し挿入します。
	 * 3.レコードのIDがnullでない場合、対応レコードを更新します。
	 *
	 * flistにはヘッダのIDフィールドを指定し、condはヘッダのIDを含むマップである必要があります。
	 *
	 * </pre>
	 * @param table テーブル。
	 * @param list 保存するレコードの全リスト。
	 * @param cond 条件データ。
	 * @param flist 条件フィールドリスト。
	 * @throws Exception 例外。
	 */
	public void saveTable(final Table table, final List<Map<String, Object>> list, final Map<String, Object> cond, final FieldList flist) throws Exception {
		Query query = new Query();
		query.setFieldList(table.getFieldList());
		query.setMainTable(table);
		if (flist != null) {
			query.setConditionFieldList(flist);
		}
		if (cond != null) {
			query.setConditionData(cond);
		}
		List<Map<String, Object>> oldlist = this.executeQuery(query);
		this.deleteNotExistRecord(table, list, oldlist);
		for (Map<String, Object> m: list) {
			if (this.existRecord(table, m, list)) {
				boolean ret = this.isUpdatable(table, m);
				if (!ret) {
					throw new ApplicationException(this.getPage(), "error.notupdatable");
				}
				this.executeUpdate(table, m);
			} else {
				this.executeInsert(table, m);
			}
		}
	}

	/**
	 * テーブルの全レコードを保存します。
	 * <pre>
	 * このメソッドは以下のロジックでBLOBを含む明細テーブルの更新に対応します。
	 *
	 * 1.listに含まなれない既存レコードを削除します。
	 * 2.レコードのIDがnullの場合、レコードのIDを作成し挿入します。
	 * 3.レコードのIDがnullでない場合、対応レコードを更新します。
	 * </pre>
	 *
	 * @param table テーブル。
	 * @param list 保存するレコードの全リスト。
	 * @throws Exception 例外。
	 */
	public void saveTable(final Table table, final List<Map<String, Object>> list) throws Exception {
		this.saveTable(table, list, null, null);
	}

	/**
	 * レコードを取得します。
	 *
	 * @param table テーブル。
	 * @param rec レコード(PK項目値を含むMap)
	 * @return レコード。
	 * @throws Exception 例外。
	 */
	public Map<String, Object> findRecord(final Table table, final Map<String, Object> rec) throws Exception {
		Query query = new Query();
		query.setFieldList(table.getFieldList());
		query.setMainTable(table);
		query.setConditionFieldList(table.getPkFieldList());
		query.setConditionData(rec);
		return this.executeRecordQuery(query);
	}


	/**
	 * 指定されたフィールドの最大値+1を取得し、次のコード地の候補とします。
	 *
	 * @param field 文字列フィールド(テーブル中のフィールドである必要があります)
	 * @param condition 条件。
	 * @return 次のコードの候補。
	 * @throws Exception 例外。
	 */
	public String queryNextCode(final Field<?> field, final String condition) throws Exception {
		Table table = field.getTable();
		SingleTableQuery query = new SingleTableQuery(table);
		FieldList flist = new FieldList();
		flist.addField(new MaxField(field.getId(), field));
		query.setFieldList(flist);
		if (condition != null) {
			query.setCondition(condition);
		}
		String ret = null;
		String maxcode = (String) this.executeScalarQuery(query);
		if (maxcode != null) {
			Long no = Long.parseLong(maxcode);
			ret = String.format("%0" + field.getLength() + "d", no.longValue() + 1);
		} else {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < field.getLength(); i++) {
				sb.append("0");
			}
			ret = sb.toString();
		}
		return ret;
	}

	/**
	 * 指定されたフィールドの最大値+1を取得し、次のコード地の候補とします。
	 *
	 * @param field 文字列フィールド(テーブル中のフィールドである必要があります)
	 * @return 次のコードの候補。
	 * @throws Exception 例外。
	 */
	public String queryNextCode(final Field<?> field) throws Exception {
		return this.queryNextCode(field, null);
	}
}
