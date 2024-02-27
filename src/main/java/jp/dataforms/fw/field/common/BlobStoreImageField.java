package jp.dataforms.fw.field.common;

import jp.dataforms.fw.dao.sqldatatype.SqlBlob;
import jp.dataforms.fw.dao.sqlgen.mysql.MysqlSqlGenerator;
import jp.dataforms.fw.dao.sqlgen.pgsql.PgsqlSqlGenerator;

/**
 * BLOB保存画像ファイルフィールドクラス。
 *
 */
public class BlobStoreImageField extends ImageField implements SqlBlob {
	/**
	 * Log.
	 */
//	private Logger log = Logger.getLogger(BlobStoreImageField.class);

	/**
	 * コンストラクタ。
	 */
	public BlobStoreImageField() {
		this(null);
	}

	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public BlobStoreImageField(final String id) {
		super(id);
		this.setDbDependentType(PgsqlSqlGenerator.DATABASE_PRODUCT_NAME, "bytea");
		this.setDbDependentType(MysqlSqlGenerator.DATABASE_PRODUCT_NAME, "longblob");
	}
}
