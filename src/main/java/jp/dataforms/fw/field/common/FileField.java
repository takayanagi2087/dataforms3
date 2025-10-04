package jp.dataforms.fw.field.common;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;
import jp.dataforms.fw.annotation.WebMethod;
import jp.dataforms.fw.dao.file.BlobFileStore;
import jp.dataforms.fw.dao.file.FileObject;
import jp.dataforms.fw.dao.file.FileStore;
import jp.dataforms.fw.dao.file.StaticFolderFileStore;
import jp.dataforms.fw.dao.file.TableFolderFileStore;
import jp.dataforms.fw.dao.file.WebResource;
import jp.dataforms.fw.dao.file.WebResourceFileStore;
import jp.dataforms.fw.dao.sqldatatype.SqlBlob;
import jp.dataforms.fw.dao.sqldatatype.SqlVarchar;
import jp.dataforms.fw.exception.ApplicationError;
import jp.dataforms.fw.field.base.Field;
import jp.dataforms.fw.response.BinaryResponse;
import jp.dataforms.fw.response.BinaryResponse.Disposition;
import jp.dataforms.fw.response.JsonResponse;
import jp.dataforms.fw.util.StringUtil;


/**
 * ファイルフィールドベースクラス。
 * <pre>
 * 対応するHTMLのタグは&lt;input type=&quot;file&quot; ...&gt;になります。
 * </pre>
 *
 * @param <TYPE> サーバで処理するJavaのデータ型。
 */
public abstract class FileField<TYPE extends FileObject> extends Field<TYPE> {

	/**
	 * ダウンロードファイル一時ファイルを記録するセッションキー。
	 */
	public static final String DOWNLOADING_FILE = "downloadingFile_";


	/**
	 * ファイルのDrag&Drop領域の有効フラグ。
	 */
	private Boolean enableFileReceiver = false;

	/**
	 * 基底フォルダ。
	 */
	private String baseFolder = null;

	/**
	 * ファイルダウンロード時のContent-Dispositio指定。
	 */
	private Disposition contentDisposition = Disposition.ATTACHMENT;
	
	/**
	 * ファイルダウンロード時のContent-Dispositionを指定します。
	 * @param contentDisposition Content-Dispositionを指定。
	 */
	public void setContentDisposition(Disposition contentDisposition) {
		this.contentDisposition = contentDisposition;
	}

	/**
	 * Logger.
	 */
	private static Logger logger = LogManager.getLogger(FileField.class);

	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public FileField(final String id) {
		super(id);
	}


	@Override
	protected void onBind() {
		super.onBind();
	}

	@Override
	public void init() throws Exception {
		super.init();
		this.setAdditionalHtml(this.getPage().getPageFramePath() + "/FileField.html");
	}

	/**
	 * 基底フォルダを取得します。
	 * @return 基底フォルダ。
	 */
	public String getBaseFolder() {
		return this.baseFolder;
	}

	/**
	 * 基底フォルダを設定します。
	 * <pre>
	 * このプロパティはBlobStoreXXXXXFieldでは無効で、FolderStoreXXXXXFieldクラスの場合のみ有効です。
	 * FolderStoreXXXXXFieldはファイルの実態は指定されたフォルダに記録し、テーブル中にはそのパスを記録するフィールドです。
	 * このプロパティがnullの場合テーブル,カラム,ユーザIDに応じたフォルダにファイルを保存します。
	 * baseFolderプロパティに基底フォルダを指定するとそのフォルダ以下にファイル保存します。
	 * </pre>
	 * @param baseFolder 基底フォルダ。
	 * @return FileFieldのオブジェクト。
	 */
	public FileField<? extends FileObject> setBaseFolder(final String baseFolder) {
		this.baseFolder = baseFolder;
		return this;
	}


	/**
	 * ファイルのDrag&amp;Drop領域の有効フラグを取得します。
	 * @return ファイルのDrag&amp;Drop領域の有効フラグ。
	 */
	public Boolean getEnableFileReceiver() {
		return enableFileReceiver;
	}

	/**
	 * ファイルのDrag&amp;Drop領域の有効フラグを設定します。
	 * @param enableFileReceiver ファイルのDrag&amp;Drop領域の有効フラグ。
	 */
	public void setEnableFileReceiver(final Boolean enableFileReceiver) {
		this.enableFileReceiver = enableFileReceiver;
	}

	/**
	 * ファイルストアを作成します。
	 * @return ファイルストア。
	 */
	public FileStore newFileStore() {
		if (this instanceof SqlBlob) {
			return new BlobFileStore(this);
		} else if (this instanceof SqlVarchar) {
			if (this.getBaseFolder() != null) {
				return new StaticFolderFileStore(this);
			} else {
				return new TableFolderFileStore(this);
			}
		} else if (this instanceof WebResource){
			return new WebResourceFileStore(this);
		} else {
			return null;
		}
	}

	/**
	 * ファイルオブジェクトを作成します。
	 * @return ファイルオブジェクト。
	 */
	protected abstract FileObject newFileObject();


	/**
	 * {@inheritDoc}
	 * <pre>
	 * BLOB保存の場合、一時ファイルを返し、フォルダ保存の場合、そのパスを返します。
	 * </pre>
	 */
	@Override
	public Object getDBValue() {
		Object ret = null;
		try {
			if (this.getValue() != null) {
				FileStore store = this.newFileStore();
				ret = store.convertToDBValue(this.getValue());
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ApplicationError(e);
		}
		return ret;
	}

	/**
	 * {@inheritDoc}
	 * <pre>
	 * BLOB保存の場合、FileObjectのインスタンスが渡されるため、それをそのままセットします。
	 * フォルダ保存の場合、保存されたパスが渡ってくるので、それからFileObjectのインスタンスを
	 * 作成し、設定します。
	 * </pre>
	 */
	@Override
	public void setDBValue(final Object value) {
		FileObject fobj = newFileObject();
		try {
			FileStore store = this.newFileStore();
			FileObject o = store.convertFromDBValue(value);
			if (o != null) {
				fobj.copy(o);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ApplicationError(e);
		}
		this.setValueObject(fobj);
	}

	@Override
	public void setClientValue(final Object v) {
		try {
			FileObject value = this.newFileObject();
			if (v instanceof Part) {
				value.copy(this.newFileStore().convertToFileObject((Part) v));
			}
			this.setValueObject(value);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ApplicationError(e);
		}
	}


	/**
	 * {@inheritDoc}
	 * <pre>
	 * クライアントの処理で必要な項目のみを抜き出したマップを作成し、ブラウザに送信します。
	 * </pre>
	 */
	@Override
	public Object getClientValue() {
		Object obj = this.getValue();
		Map<String, Object> ret = null;
		if (obj != null) {
			if (obj instanceof FileObject) {
				FileObject v = (FileObject) obj;
				if (!StringUtil.isBlank(v.getFileName())) {
					ret = new HashMap<String, Object>();
					// ブラウザに渡す必要最小限の情報をマップに記録します。
					ret.put("fileName", v.getFileName());
					ret.put("size", v.getLength());
					ret.put("downloadParameter", v.getDownloadParameter());
				}
			}
		}
		return ret;
	}

	/**
	 * BLOB用ダウンロードパラメータを取得します。
	 * @param m データマップ。
	 * @return ダウンロードパラメータ。
	 *
	 *
	 */
	public String getBlobDownloadParameter(final Map<String, Object> m) {
		FileStore store = this.newFileStore();
		String ret = store.getDownloadParameter(this, m);
		return ret;
	}

	/**
	 * ダウンロードパラメータからファイルストアを作成します。
	 * @param param ダウンロードパラメータ。
	 * @return ファイルストア。
	 * @throws Exception 例外。
	 */
	protected FileStore newFileStore(final Map<String, Object> param) throws Exception {
		String clsname = (String) param.get("store");
		Class<?> scls = Class.forName(clsname);
		FileStore store = (FileStore) scls.getDeclaredConstructors()[0].newInstance(this);
		store.initDownloadParameter(param);
		return store;
	}

	/**
	 * 一時ファイルを削除します。
	 * @param p パラメータ。
	 * @return 処理結果。
	 * @throws Exception 例外。
	 */
	@WebMethod
	public JsonResponse deleteTempFile(final Map<String, Object> p) throws Exception {
		String key = (String) p.get("key");
		logger.debug(() -> "key=" + key);
		if (key != null) {
			String sessionKey = DOWNLOADING_FILE + key;
			String downloadingFile = (String) this.getPage().getRequest().getSession().getAttribute(sessionKey);
			logger.debug(() -> "downloadingFile=" + downloadingFile);
			if (downloadingFile != null) {
				File tf = new File(downloadingFile);
				tf.delete();
			}
		}
		JsonResponse resp = new JsonResponse(JsonResponse.SUCCESS, "");
		return resp;
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
			param = FileStore.decryptDownloadParameter(key);
			// Rangeヘッダが指定されていた場合、送信中ファイルがあればそれをセットする。
			if (!StringUtil.isBlank(req.getHeader("Range"))) {
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
			logger.debug("param={}", param);
		}

		FileStore store = this.newFileStore(param);
		FileObject fobj = store.readFileObject(param);
		BinaryResponse resp = new BinaryResponse(fobj);
		resp.setRequest(req);
		resp.setTempFile(store.getTempFile(fobj));
		resp.setContentDisposition(this.contentDisposition);
		if (key != null) {
			if (!store.isSeekingSupported()) {
				// BLOBでRangeヘッダが指定されていた場合、一時ファイルのパスをセッションに記録する。
				if (!StringUtil.isBlank(req.getHeader("Range"))) {
					req.getSession().setAttribute(DOWNLOADING_FILE + key, fobj.getTempFile().getAbsolutePath());
					resp.setTempFile(null); // 転送終了時にファイルを削除しないようにする。
				}
			}
		}
		return resp;
	}


	@Override
	public Object getValue(final Map<String, Object> param) {
		Object ret = super.getValue(param);
		if (StringUtil.isBlank(ret)) {
			ret = param.get(this.getId() + "_fn");
		}
		return ret;
	}

	/**
	 * 原作条件に使用しない。
	 */
	@Override
	public jp.dataforms.fw.field.base.Field.MatchType getDefaultMatchType() {
		return MatchType.NONE;
	}

	/**
	 * インポートデータからファイルオブジェクトを取得します。
	 * @param map インポートフィールドマップ。
	 * @param filePath ファイルの保存パス。
	 * @return FileObject形式のデータ。
	 * @throws Exception 例外。
	 */
	public FileObject getFileObjectFromImportMap(final Map<String, Object> map, final String filePath) throws Exception {
		FileObject ret = this.newFileObject();
		File f = new File(filePath + "/" + (String) map.get("saveFile"));
		ret.setFileName((String) map.get("filename"));
		ret.setLength(f.length());
		ret.setDownloadParameter((String) map.get("downloadParameter"));
		FileStore fs = this.newFileStore();
		File tf = fs.makeTemp(ret.getFileName(), f);
		ret.setTempFile(tf);
		return ret;
	}

	@Override
	public int calcDefaultColumnWidth() {
		return 300;
	}

	@Override
	public Map<String, Object> getProperties() throws Exception {
		Map<String, Object> prop = super.getProperties();
		prop.put("enableFileReceiver", this.enableFileReceiver);
		return prop;
	}
}
