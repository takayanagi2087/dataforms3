package jp.dataforms.fw.dao.file;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.servlet.http.Part;
import jp.dataforms.fw.field.common.FileField;
import jp.dataforms.fw.servlet.DataFormsServlet;
import jp.dataforms.fw.util.CryptUtil;
import jp.dataforms.fw.util.FileUtil;
import jp.dataforms.fw.util.JsonUtil;

/**
 * ファイル保存領域クラス。
 * <pre>
 * アップロードされたファイルを管理するためのクラスです。
 *
 * </pre>
 *
 */
public abstract class FileStore {

	/**
	 * Logger.
	 */
	private static Logger logger = LogManager.getLogger(FileStore.class);

	/**
	 * コンストラクタ。
	 */
	public FileStore() {
	}

	/**
	 * アップロード直後にファイルを一時ファイルに保存します。
	 * @param part アップロードファイル情報。
	 * @return 作成された一時ファイル。
	 * @throws Exception 例外。
	 */
	protected abstract File makeTempFromFileItem(final Part part) throws Exception;

	/**
	 * テーブル保存用一時ファイルを作成します。
	 * @param filename ファイル名。
	 * @param orgfile 元ファイル。
	 * @return 作成された一時ファイル。
	 * @throws Exception 例外。
	 */
	public abstract File makeTemp(final String filename, final File orgfile) throws Exception;

	/**
	 * ダウンロードパラメータからファイルストアを作成した場合の初期化処理です。
	 * @param param デコードされたダウンロードパラメータ。
	 */
	public void initDownloadParameter(final Map<String, Object> param) {

	}

	/**
	 * アップロードファイル情報をFileObjectに変換します。
	 * @param part アップロードファイル情報。
	 * @return FileObject。
	 * @throws Exception 例外。
	 */
	public FileObject convertToFileObject(final Part part) throws Exception {
		FileObject ret = new FileObject();
		File tempFile = this.makeTempFromFileItem(part);
		ret.setFileName(FileUtil.getFileName(part.getSubmittedFileName()));
		ret.setLength(part.getSize());
		ret.setTempFile(tempFile);
		return ret;
	}



	/**
	 * DBに保存する値に変換します。
	 * @param obj FileObject。
	 * @return DBに保存する値。
	 * @throws Exception 例外。
	 */
	public abstract Object convertToDBValue(final Object obj) throws Exception;


	/**
	 * DBから読み込んだ値をFileObjectに変換します。
	 * @param colValue DBから読み込んだ値。
	 * @return FileObject。
	 * @throws Exception 例外。
	 */
	public abstract FileObject convertFromDBValue(final Object colValue) throws Exception;


	/**
	 * ファイルダウンロード用にファイルオブジェクトを読み込みます。
	 * @param param ダウンロードパラメータ。
	 * @return ファイルオブジェクト。
	 * @throws Exception 例外。
	 */
	public abstract FileObject readFileObject(final Map<String, Object> param) throws Exception;

	/**
	 * 削除すべき一時ファイルを取得します。
	 * @param fobj ファイルオブジェクト。
	 * @return 削除すべき一時ファイル。
	 */
	public abstract File getTempFile(final FileObject fobj);


	/**
	 * ファイルのダウンロードに必要な情報のマップを作成します。
	 * @param field フィールドクラス。
	 * @param d 同一レコードのデータマップ。
	 * @return ファイルのダウンロードに必要な情報のマップ。
	 */
	public abstract Map<String, Object> getDownloadInfoMap(final FileField<?> field, final Map<String, Object> d);


	/**
	 * ダウンロードパラメータを取得します。
	 * @param field フィールド。
	 * @param d フィールドが存在するレコードのマップ。
	 * @return ダウンロードパラメータ。
	 */
	public abstract String getDownloadParameter(final FileField<?> field, final Map<String, Object> d);

	/**
	 * 暗号化されたダウンロードパラメータを取得します。
	 * @param p ダウンロードパラメータマップ。
	 * @return 暗号化されたダウンロードパラメータ。
	 */
	public String encryptDownloadParameter(final Map<String, Object> p) {
		String json = JsonUtil.encode(p, false);
		String ret = "";
		try {
			ret = java.net.URLEncoder.encode(CryptUtil.encrypt(json, DataFormsServlet.getQueryStringCryptPassword()), DataFormsServlet.getEncoding());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	/**
	 * 暗号化されたダウンロードパラメータを複合します。
	 * @param p 暗号化されたダウンロードパラメータ。
	 * @return ダウンロードパラメータマップ。
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> decryptDownloadParameter(final String p) {
		Map<String, Object> ret = null;
		try {
			String json = CryptUtil.decrypt(p, DataFormsServlet.getQueryStringCryptPassword());
			logger.debug(() -> "json=" + json);
			ret =  (Map<String, Object>) JsonUtil.decode(json, HashMap.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	/**
	 * シークサポートの有無を返します。
	 * @return 常にtrueを返します。
	 */
	public boolean isSeekingSupported() {
		return true;
	}
}

