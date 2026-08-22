package jp.dataforms.fw.field.upload;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;
import jp.dataforms.fw.annotation.WebMethod;
import jp.dataforms.fw.dao.Dao;
import jp.dataforms.fw.dao.Table;
import jp.dataforms.fw.dao.sqldatatype.SqlBlob;
import jp.dataforms.fw.dao.sqlgen.mysql.MysqlSqlGenerator;
import jp.dataforms.fw.dao.sqlgen.pgsql.PgsqlSqlGenerator;
import jp.dataforms.fw.exception.ApplicationError;
import jp.dataforms.fw.field.base.Field;
import jp.dataforms.fw.response.BinaryResponse;
import jp.dataforms.fw.servlet.DataFormsServlet;
import jp.dataforms.fw.util.CryptUtil;
import jp.dataforms.fw.util.JsonUtil;
import jp.dataforms.fw.util.StringUtil;

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
				ret.put("downloadParameter", v.getDownloadParameter());
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

	/**
	 * ダウンロード情報マップを作成します。
	 * @param d データマップ。
	 * @return ダウンロードパラメータマップ。
	 */
	public Map<String, Object> getDownloadInfoMap(final Map<String, Object> d) {
		Map<String, Object> m = new HashMap<String, Object>();
		Table table = this.getTable();
		if (table != null) {
			m.put("table", table.getClass().getName());
			m.put("fieldId", this.getId());
			// キャッシュされるのを防止するために時刻を追加
			SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmss");
			java.util.Date now = new java.util.Date();
			m.put("ts", fmt.format(now));
			for (Field<?> f : table.getPkFieldList()) {
				m.put(f.getId(), d.get(f.getId()).toString());
			}
		} else {
			logger.warn(() -> "Table not found. field ID=" + this.getId());
		}
		return m;
	}

	/**
	 * 暗号化されたダウンロードパラメータを取得します。
	 * @param p ダウンロードパラメータマップ。
	 * @return 暗号化されたダウンロードパラメータ。
	 */
	public String encryptDownloadParameter(final Map<String, Object> p) {
		String json = JsonUtil.encode(p, false);
		logger.debug(() -> "download paramater=" + json);
		String ret = "";
		try {
			ret = java.net.URLEncoder.encode(CryptUtil.encrypt(json, DataFormsServlet.getQueryStringCryptPassword()), DataFormsServlet.getEncoding());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	
	/**
	 * ダウンロードパラメータの解読を行います。
	 * @param key キー。
	 * @return ダウンロードパラメータ。
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> decryptDownloadParameter(final String key) {
		Map<String, Object> ret = null;
		try {
			String json = CryptUtil.decrypt(key, DataFormsServlet.getQueryStringCryptPassword());
			logger.debug("json=" + json);
			ret = (Map<String, Object>) JsonUtil.decode(json, HashMap.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	@Override
	public String getBlobDownloadParameter(Map<String, Object> m) {
		Map<String, Object> p = this.getDownloadInfoMap(m);
		return "key=" + this.encryptDownloadParameter(p);
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
	 * UploadFileをDBから読み込みます。
	 * @param param パラメータ。
	 * @return UploadFileのオブジェクト。
	 * @throws Exception 例外。
	 */
	public UploadFile readUploadFile(final Map<String, Object> param) throws Exception {
		String downloadingFile = (String) param.get("downloadingFile");
		logger.debug(() -> "downloadingFile=" + downloadingFile);
		Dao dao = new Dao(this);
		String tblclass = (String) param.get("table");
		@SuppressWarnings("unchecked")
		Class<? extends Table> cls = (Class<? extends Table>) Class.forName(tblclass);
		Table table = cls.getDeclaredConstructor().newInstance();
		Map<String, Object> data = table.getPkFieldList().convertClientToServer(param);
		UploadFile fobj = null;
		if (!StringUtil.isBlank(downloadingFile)) {
//			fobj = dao.queryBlobFileInfo(table, (String) param.get("fieldId"), data);
//			fobj.setTempFile(new File(downloadingFile));
		} else {
			fobj = dao.queryBlobUploadFile(table, (String) param.get("fieldId"), data);
		}
		return fobj;
	}


	/**
	 * ファイルをダウンロードします。
	 * @param p パラメータ。
	 * @return 画像応答。
	 * @throws Exception 例外。
	 */
	@WebMethod(useDB = true)
	public BinaryResponse download(final Map<String, Object> p) throws Exception {
		HttpServletRequest req = this.getPage().getRequest();
		Map<String, Object> param = p;
		String key = (String) p.get("key");
		logger.debug(() -> "key=" + key);
		if (key != null) {
			param = this.decryptDownloadParameter(key);
			// FIXME:ストリーミング対応。
			
			// Rangeヘッダが指定されていた場合、送信中ファイルがあればそれをセットする。
/*			if (!StringUtil.isBlank(req.getHeader("Range"))) {
				String sessionKey = DOWNLOADING_FILE + key;
				logger.debug(() -> "*sessionKey=" + sessionKey);
				String downloadingFile = (String) req.getSession().getAttribute(sessionKey);
				if (downloadingFile != null) {
					File tf = new File(downloadingFile);
					if (tf.exists()) {
						param.put("downloadingFile", downloadingFile);
					}
				}
			}
*/
			logger.debug("param={}", param);
		}

		UploadFile fobj = this.readUploadFile(param);
		BinaryResponse resp = new BinaryResponse(fobj);
		resp.setRequest(req);
//		resp.setTempFile(store.getTempFile(fobj));
//		resp.setContentDisposition(this.contentDisposition);
/*		if (key != null) {
			if (!store.isSeekingSupported()) {
				// BLOBでRangeヘッダが指定されていた場合、一時ファイルのパスをセッションに記録する。
				if (!StringUtil.isBlank(req.getHeader("Range"))) {
					req.getSession().setAttribute(DOWNLOADING_FILE + key, fobj.getTempFile().getAbsolutePath());
					resp.setTempFile(null); // 転送終了時にファイルを削除しないようにする。
				}
			}
		}
*/		return resp;
	}

	
}
