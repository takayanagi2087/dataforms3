package jp.dataforms.fw.field.upload;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.servlet.http.Part;
import jp.dataforms.fw.exception.ApplicationError;
import jp.dataforms.fw.field.base.Field;
import lombok.Getter;
import lombok.Setter;

/**
 * アップロードフィールド。
 * <pre>
 * FielFieldクラス階層が複雑になったため作り直し。
 * 最終的にFielField階層は非推奨(Deprecated)にしたい。
 * </pre>
 */
public class UploadField extends Field<UploadFile> {
	/**
	 * Logger.
	 */
	private static Logger logger = LogManager.getLogger(UploadField.class);
	
	/**
	 * 保存先。
	 */
	public enum Store {
		/** サーバー上のファイル。 */
		FILE
		/** DB上のBLOBフィールド。 */
		, BLOB
	}

	
	/**
	 * 保存先。
	 */
	@Getter
	@Setter
	private Store store = null;
	
	
	/**
	 * コンストラクタ。
	 * @param fieldId フィールドID。
	 */
	public UploadField(final String fieldId) {
		super(fieldId);
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
	
	
}
