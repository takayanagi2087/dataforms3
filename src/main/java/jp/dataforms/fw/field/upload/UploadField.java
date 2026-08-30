package jp.dataforms.fw.field.upload;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;
import jp.dataforms.fw.annotation.WebMethod;
import jp.dataforms.fw.dao.Dao;
import jp.dataforms.fw.dao.Table;
import jp.dataforms.fw.dao.file.FileStore;
import jp.dataforms.fw.dao.file.ImageData;
import jp.dataforms.fw.dao.sqldatatype.SqlBlob;
import jp.dataforms.fw.dao.sqlgen.mysql.MysqlSqlGenerator;
import jp.dataforms.fw.dao.sqlgen.pgsql.PgsqlSqlGenerator;
import jp.dataforms.fw.exception.ApplicationError;
import jp.dataforms.fw.field.base.Field;
import jp.dataforms.fw.response.BinaryResponse;
import jp.dataforms.fw.response.BinaryResponse.Disposition;
import jp.dataforms.fw.response.ImageResponse;
import jp.dataforms.fw.servlet.DataFormsServlet;
import jp.dataforms.fw.util.CryptUtil;
import jp.dataforms.fw.util.JsonUtil;
import jp.dataforms.fw.util.StringUtil;
import lombok.Getter;
import lombok.Setter;

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
	 * Previewを行うかどうかを設定します。
	 */
	@Setter
	@Getter
	private Boolean preview = true;
	
	/**
	 * プレビュー枠の幅。
	 */
	@Setter
	@Getter
	private Integer thumbnailWidth = 128;

	/**
	 * プレビュー枠の高さ。
	 * <pre>
	 * nullを指定すると画像の比率に合わせて高さを調整します。
	 * </pre>
	 */
	@Setter
	@Getter
	private Integer thumbnailHeight = null;
	
	
	/**
	 * ビデオプレーヤー幅。
	 */
	@Setter
	@Getter
	private Integer videoPlayerWidth = 420;
	/**
	 * ビデオプレーヤー高さ。
	 */
	@Setter
	@Getter
	private Integer videoPlayerHeight = null;
	

	
	
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
		String mode = (String) p.get("mode");
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
		if ("inline".equals(mode)) {
			resp.setContentDisposition(Disposition.INLINE);
		}
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
	
	/**
	 * 画像データを読み込みます。
	 * @param p 読み込みのパラメータ。
	 * @return 読み込み結果。
	 * @throws Exception 例外。
	 */
	protected UploadFile readImageData(final Map<String, Object> p) throws Exception {
		Map<String, Object> param = p;
		String key = (String) p.get("key");
		if (key != null) {
			 param = FileStore.decryptDownloadParameter(key);
		}
		UploadFile ret = this.readUploadFile(param);
		return ret;
	}
	
	/**
	 * 画像データを読み込みます。
	 * @param buf 画像データ。
	 * @return イメージ。
	 * @throws Exception 例外。
	 */
	public BufferedImage readImage(final byte[] buf) throws Exception {
		BufferedImage image = null;
		ByteArrayInputStream is = new ByteArrayInputStream(buf);
		try {
			image = ImageIO.read(is);
		} finally {
			is.close();
		}
		return image;
	}

	/**
	 * PNGのコンテントタイプ。
	 */
	public static final String CONTENT_TYPE_PNG = "image/png";

	/**
	 * 画像データを書き出します。
	 * @param img イメージ。
	 * @return 出力されたバイト列。
	 * @throws Exception 例外。
	 */
	public byte[] writeImage(final BufferedImage img) throws Exception {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			ImageWriter writer = ImageIO.getImageWritersByMIMEType(CONTENT_TYPE_PNG).next();
			try {
				writer.setOutput(ImageIO.createImageOutputStream(os));
				writer.write(img);
			} finally {
				writer.dispose();
			}
		} finally {
			os.close();
		}
		byte[] ret = os.toByteArray();
//		this.setContents(ret);
		return ret;
	}

	/**
	 * 画像を縮小します。
	 * @param uf 画像データ。
	 * @param w 幅。
	 * @param h 高さ。
	 * @return 画像データ。
	 * @throws Exception 例外。
	 */
	public ImageData getReducedImage(final UploadFile uf, final int w, final int h) throws Exception {
		ImageData ret = new ImageData();
		BufferedImage img = this.readImage(uf.getContents());
		int width = w;
		int height = h;
		double iw = img.getWidth();
		double ih = img.getHeight();
		if (iw > ih) {
			width = w;
			height = (int) (ih * (w / iw));
		} else {
			width = (int) (iw * (h / ih));
			height = h;
		}
		logger.debug("width,height={},{}", width, height);
		// なぜかPNGのタイプが0で返される。(JDKのBUGと思われる)
		int type = img.getType();
		if (type == 0) {
			type = 5;
		}
		BufferedImage thumb = new BufferedImage(width, height, type);
		thumb.getGraphics().drawImage(img.getScaledInstance(width, height, java.awt.Image.SCALE_AREA_AVERAGING), 0, 0, width, height, null);
		ret.setContents(this.writeImage(thumb));
		ret.setFileName("thumb.png");
		return ret;
	}

	/**
	 * サムネイル画像をダウンロードします。
	 * @param param パラメータ。
	 * @return 画像応答。
	 * @throws Exception 例外。
	 */
	@WebMethod(useDB = true)
	public ImageResponse downloadThumbnail(final Map<String, Object> param) throws Exception {
		UploadFile image = this.readImageData(param);
		Integer h = this.thumbnailHeight;
		if (h == null) {
			// 高さがnullの場合widthと同じに設定で縮小する。
			h = this.thumbnailWidth;
		}
		ImageResponse resp = new ImageResponse(this.getReducedImage(image, this.thumbnailWidth, h));
		return resp;
	}

	@Override
	public Map<String, Object> getProperties() throws Exception {
		Map<String, Object> prop = super.getProperties();
		prop.put("contentTypeList", DataFormsServlet.getConf().getApplication().getContentTypeList());
		prop.put("preview", this.preview);
		prop.put("thumbnailWidth", this.thumbnailWidth);
		prop.put("thumbnailHeight", this.thumbnailHeight);
		prop.put("videoPlayerWidth", this.videoPlayerWidth);
		prop.put("videoPlayerHeight", this.videoPlayerHeight);
		return prop;
	}
	
}
