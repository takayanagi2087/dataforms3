package jp.dataforms.fw.field.common;

import jp.dataforms.fw.dao.sqldatatype.SqlBlob;
import jp.dataforms.fw.dao.sqlgen.mysql.MysqlSqlGenerator;
import jp.dataforms.fw.dao.sqlgen.pgsql.PgsqlSqlGenerator;


/**
 * BLOB保存ファイルフィールドクラス。
 *
 */
public class BlobStoreFileField extends FileObjectField implements SqlBlob {
	/**
	 * Log.
	 */
//	private Logger log = Logger.getLogger(BlobStoreFileField.class);

	/**
	 * コンストラクタ。
	 */
	public BlobStoreFileField() {
		this(null);
	}
	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public BlobStoreFileField(final String id) {
		super(id);
		this.setDbDependentType(PgsqlSqlGenerator.DATABASE_PRODUCT_NAME, "bytea");
		this.setDbDependentType(MysqlSqlGenerator.DATABASE_PRODUCT_NAME, "longblob");
	}

}
