package jp.dataforms.fw.devtool.db.page;

import java.sql.Connection;

import jp.dataforms.fw.controller.Form;
import jp.dataforms.fw.dao.sqlgen.SqlGenerator;
import jp.dataforms.fw.field.base.TextField;

/**
 * データベース情報フォームクラス。
 * <pre>
 * 接続しているデータベースの製品名、バージョン情報を表示するフォームです。
 * </pre>
 */
public class DatabaseInfoForm extends Form {
	/**
	 * Logger.
	 */
	// private Logger logger = LogManager.getLogger(DatabaseInfoForm.class);

	/**
	 * コンストラクタ。
	 */
	public DatabaseInfoForm() {
		super(null);
		this.addField(new TextField("dbServerName")).setComment("DBサーバ名");
		this.addField(new TextField("dbServerVersion")).setComment("DBサーババージョン");
		this.addField(new TextField("dbServerURL").setComment("DB接続URL"));
//		this.addField(new PackageNameField()).addValidator(new RequiredValidator());
	}

	/**
	 * {@inheritDoc}
	 * <pre>
	 * データベースの製品名とバージョンを取得します。
	 * </pre>
	 */
	@Override
	public void init() throws Exception {
		super.init();
		Connection conn = this.getConnection();
		String dbServerName =  conn.getMetaData().getDatabaseProductName();
		String dbServerVersion = conn.getMetaData().getDatabaseProductVersion();
		SqlGenerator gen = SqlGenerator.getInstance(conn);
		String dbServerURL = gen.getConnectionUrl(conn);
		this.setFormData("dbServerName", dbServerName);
		this.setFormData("dbServerVersion", dbServerVersion);
		this.setFormData("dbServerURL", dbServerURL);
	}
}
