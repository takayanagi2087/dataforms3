package jp.dataforms.fw.field.upload;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.servlet.http.Part;
import jp.dataforms.fw.dao.sqldatatype.SqlBlob;
import jp.dataforms.fw.dao.sqlgen.mysql.MysqlSqlGenerator;
import jp.dataforms.fw.dao.sqlgen.pgsql.PgsqlSqlGenerator;
import jp.dataforms.fw.exception.ApplicationError;
import jp.dataforms.fw.field.base.Field;

/**
 * アップロードフィールド。
 * <pre>
 * FielFieldクラス階層が複雑になったため作り直し。
 * 最終的にFielField階層は非推奨(Deprecated)にしたい。
 * </pre>
 */
public class UploadField extends Field<UploadFile> implements SqlBlob {
	/**
	 * Logger.
	 */
	private static Logger logger = LogManager.getLogger(UploadField.class);
	
	/**
	 * アップロードファイル情報カラム。
	 */
//	public static final String UFINFO = "ufinfo";
	
	/**
	 * 保存先。
	 */
//	public enum Store {
//		/** サーバー上のファイル。 */
//		FILE
//		/** DB上のBLOBフィールド。 */
//		, BLOB
//	}

	
	/**
	 * 保存先。
	 */
//	@Getter
//	@Setter
//	private Store store = Store.BLOB;
	
	
	/**
	 * コンストラクタ。
	 * @param fieldId フィールドID。
	 */
	public UploadField(final String fieldId) {
		super(fieldId);
		this.setDbDependentType(PgsqlSqlGenerator.DATABASE_PRODUCT_NAME, "bytea");
		this.setDbDependentType(MysqlSqlGenerator.DATABASE_PRODUCT_NAME, "longblob");
	}
	
	
	@Override
	public void init() throws Exception {
		super.init();
		this.setAdditionalHtml(this.getPage().getPageFramePath() + "/UploadField.html");
	}

	/**
	 * クライアントからPostされたファイルを設定します。
	 * @param v Postされたファイルに対応するPartクラスのインスタンス。
	 */
	@Override
	public void setClientValue(Object v) {
		try {
			if (v instanceof Part) {
				UploadFile uf = new UploadFile();
				uf.setPart((Part) v);
				this.setValueObject(uf);
			} else {
				this.setValueObject(null);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ApplicationError(e);
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Object getClientValue() {
		Map<String, Object> ret = null;
		Object obj = this.getValue();
		if (obj != null) {
			if (obj instanceof UploadFile) {
				UploadFile v = (UploadFile) obj;
				ret = new HashMap<String, Object>();
				ret.put("fileName", v.getFileName());
				ret.put("size", v.getSize());
				// FIXME: ダウンロードパラメータの作成を実装。
//				ret.put("downloadParameter", v.getDownloadParameter());
			}
		}
		return ret;
	}
	
	/**
	 * DBから読み込んだファイル情報を設定します。
	 * @param v DBから読み込んだUploadFileクラスのインスタンス。
	 */
	@Override
	public void setDBValue(Object v) {
		if (v instanceof UploadFile) {
			super.setDBValue(v);
		} else {
			super.setDBValue(null);
		}
	}
	
	/**
	 * 原作条件に使用しない。
	 */
	@Override
	public jp.dataforms.fw.field.base.Field.MatchType getDefaultMatchType() {
		return MatchType.NONE;
	}

	
	@Override
	public String getBlobDownloadParameter(Map<String, Object> m) {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	
	/**
	 * 対応するblobフィールドにはファイルの内容のみを記録します。
	 * さらに情報カラムを別途作成し、ファイル名とそのサイズを記録します。
	 */
	@Override
	public boolean hasFileInfoColumn() {
		return true;
	}
	/**
	 * 情報カラムの接尾語を指定します。
	 * <pre>
	 * UploadFieldはそのファイルの内容を記録するBLOBフィールドに対応しますが、
	 * そのファイル名とファイルサイズを記録するための情報フィールドを生成します。
	 * </pre>
	 * @return 情報カラムの接尾語。
     */
/*	@Override
	public String infoColumnSuffix() {
		return UFINFO;
	}
*/	
	/**
	 * アップロードファイル情報カラムかどうかを判定します。
	 * @param colname カラム名。
	 * @return アップロードファイル情報カラムの場合true。
	 */
/*	public static boolean isUfInfo(final String colname) {
		return colname.matches(".+_" + UploadField.UFINFO + "$");
	}*/
}
