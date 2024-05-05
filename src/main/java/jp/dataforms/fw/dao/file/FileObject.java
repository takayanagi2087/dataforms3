package jp.dataforms.fw.dao.file;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.internal.LinkedTreeMap;

import jp.dataforms.fw.exception.ApplicationError;
import jp.dataforms.fw.util.FileUtil;

/**
 * 任意のファイルクラス。
 * <pre>
 * &lt;input type=&quot;file&quot; ...&gt;タグで入力された情報を保存するためのクラスです。
 * </pre>
 *
 */
public class FileObject implements Serializable {
	/**
	 * UID.
	 */
	private static final long serialVersionUID = 7003862083171262693L;

	/**
	 * Logger.
	 */
	private static Logger logger = LogManager.getLogger(FileObject.class);

	/**
	 * ファイル名。
	 */
	private String fileName = null;

	/**
	 * ファイル長。
	 */
	private long length = 0L;

	/**
	 * ダウンロードパラメータ。
	 */
	private String downloadParameter = null;


	/**
	 * 一時ファイル。
	 * <pre>
	 * 大きめのファイルの場合使用し、contentsとは排他利用となります。
	 * </pre>
	 */
	private File tempFile = null;


	/**
	 * ファイルの内容。
	 * <pre>
	 * 小さめのファイルの場合使用し、tempFileとは排他利用となります。
	 * </pre>
	 */
	private byte[] contents = null;

	/**
	 * Webリソースの場合のURLを記録します。
	 */
	private String webResourceUrl = null;

	/**
	 * Conetnt-type。
	 */
	private String contentType = null;


	/**
	 * PNGのコンテントタイプ。
	 */
	public static final String CONTENT_TYPE_PNG = "image/png";
	/**
	 * JPEGのコンテントタイプ。
	 */
	public static final String CONTENT_TYPE_JPEG = "image/jpeg";
	/**
	 * GIFのコンテントタイプ。
	 */
	public static final String CONTENT_TYPE_GIF = "image/gif";
	/**
	 * SVGのコンテントタイプ。
	 */
	public static final String CONTENT_TYPE_SVG = "image/svg+xml";

	/**
	 * XLSXのコンテントタイプ。
	 */
	public static final String CONTENT_TYPE_XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";


	/**
	 * PDFのコンテントタイプ。
	 */
	public static final String CONTENT_TYPE_PDF = "application/pdf";

	/**
	 * mp4のコンテントタイプ。
	 */
	public static final String CONTENT_TYPE_MP4 = "video/mp4";


	/**
	 * 拡張子とContent-typeの対応表。
	 */
	private static final String [][] CONTENT_TYPE_TABLE = {
		{"(?i).*\\.png$", CONTENT_TYPE_PNG}
		, {"(?i).*\\.jpg$", CONTENT_TYPE_JPEG}
		, {"(?i).*\\.gif$", CONTENT_TYPE_GIF}
		, {"(?i).*\\.svg$", CONTENT_TYPE_SVG}
		, {"(?i).*\\.xlsx$", CONTENT_TYPE_XLSX}
		, {"(?i).*\\.pdf$", CONTENT_TYPE_PDF}
		, {"(?i).*\\.mp4$", CONTENT_TYPE_MP4}
	};



	/**
	 * content-typeリスト。
	 */
	private static List<LinkedTreeMap<String, String>> contentTypeList = null;


	/**
	 * content-typeリストを取得します。
	 * @return content-typeリスト。
	 */
	public static List<LinkedTreeMap<String, String>> getContentTypeList() {
		return contentTypeList;
	}

	/**
	 * content-typeリストを設定します。
	 * @param contentTypeList content-typeリスト。
	 */
	public static void setContentTypeList(final List<LinkedTreeMap<String, String>> contentTypeList) {
		FileObject.contentTypeList = contentTypeList;
	}

	/**
	 * コンストラクタ。
	 */
	public FileObject() {
		this.fileName = null;
		this.length = 0L;
		this.tempFile = null;
		this.contents = null;
		this.downloadParameter = null;
		this.contentType = null;
		this.webResourceUrl = null;
	}

	/**
	 * コンストラクタ。
	 * @param fileName ファイル名。
	 */
	public FileObject(final String fileName) {
		this();
		this.setFileName(fileName);
	}

	/**
	 * コンストラクタ。
	 * @param fileName ファイル名。
	 * @param contents ファイルの内容。
	 */
	public FileObject(final String fileName, final byte[] contents) {
		this();
		this.setFileName(fileName);
		this.setContents(contents);
	}


	/**
	 * Content-typeを取得します。
	 * @return Content-type。
	 */
	public String getContentType() {
		return contentType;
	}

	/**
	 * Content-typeを設定します。
	 * @param contentType Content-type。
	 */
	public void setContentType(final String contentType) {
		this.contentType = contentType;
	}


	/**
	 * コピーを行います。
	 * @param fobj コピー元のオブジェクト。
	 * @throws Exception 例外。
	 */
	public void copy(final FileObject fobj) throws Exception {
		this.fileName = fobj.fileName;
		this.length = fobj.length;
		this.tempFile = fobj.tempFile;
		this.contents = fobj.contents;
		this.downloadParameter = fobj.downloadParameter;
		this.contentType = fobj.contentType;
		this.webResourceUrl = fobj.webResourceUrl;
	}

	/**
	 * ファイル名を取得します。
	 * @return ファイル名。
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * ファイル名を設定します。
	 * @param fileName ファイル名。
	 */
	public void setFileName(final String fileName) {
		this.fileName = fileName;
		if (fileName != null) {
			if (FileObject.contentTypeList == null) {
				for (String[] t: CONTENT_TYPE_TABLE) {
					if (Pattern.matches(t[0], fileName)) {
						this.setContentType(t[1]);
						break;
					}
				}
			} else {
				String ct = FileObject.getContentType(fileName);
				this.setContentType(ct);
			}
		}
	}

	/**
	 * ファイル名からcontent-typeを取得します。
	 * <pre>
	 * web.xmlのcontent-type-listを参照しています。
	 * </pre>
	 * @param fileName ファイル名。
	 * @return content-type。
	 */
	public static String getContentType(final String fileName) {
		String ret = null;
		for (LinkedTreeMap<String, String> m: FileObject.contentTypeList) {
			String fnPattern = m.get("fnPattern");
			if (Pattern.matches(fnPattern, fileName)) {
				ret = m.get("contentType");
				break;
			}
		}
		return ret;
	}


	/**
	 * ファイル長を取得します。
	 * @return ファイル長。
	 */
	public long getLength() {
		return length;
	}

	/**
	 * ファイル長を設定します。
	 * @param length ファイル長。
	 */
	public void setLength(final long length) {
		this.length = length;
	}

	/**
	 * 一時ファイルを取得します。
	 * <pre>
	 * 大きめのファイルの場合使用し、contentsとは排他利用となります。
	 * </pre>
	 * @return 一時ファイル。
	 */
	public File getTempFile() {
		return tempFile;
	}

	/**
	 * 一時ファイルを設定します。
	 * @param tempFile 一時ファイル。
	 */
	public void setTempFile(final File tempFile) {
		this.tempFile = tempFile;
	}

	/**
	 * ファイルの内容を取得します。
	 * <pre>
	 * 小さめのファイルの場合使用し、tempFileとは排他利用となります。
	 * </pre>
	 * @return ファイルの内容。
	 */
	public byte[] getContents() {
		return contents;
	}

	/**
	 * ファイルの内容を設定します。
	 * @param contents ファイルの内容。
	 */
	public void setContents(final byte[] contents) {
		this.contents = contents;
	}

	/**
	 * ダウンロードパラメータを取得します。
	 * @return ダウンロードパラメータ。
	 */
	public String getDownloadParameter() {
		return downloadParameter;
	}

	/**
	 * ダウンロードパラメータを設定します。
	 * @param downloadParameter ダウンロードパラメータ。
	 */
	public void setDownloadParameter(final String downloadParameter) {
		this.downloadParameter = downloadParameter;
	}

	/**
	 * 内容の入力ストリームを取得します。
	 * @return 内容の入力ストリーム。
	 * @throws Exception 例外。
	 */
	public InputStream openInputStream() throws Exception {
		if (this.contents != null) {
			return new ByteArrayInputStream(this.contents);
		} else if (this.tempFile != null) {
			return new FileInputStream(this.tempFile);
		} else {
			return null;
		}
	}

	/**
	 * 指定ファイルを読み込んで、メモリに展開する。
	 * @param file ファイル。
	 * @throws Exception 例外。
	 */
	public void readContents(final File file) throws Exception {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			FileInputStream is = new FileInputStream(file);
			try {
				FileUtil.copyStream(is, os);
			} finally {
				is.close();
			}
		} finally {
			os.close();
		}
		this.setTempFile(null);
		this.setContents(os.toByteArray());
	}


	/**
	 * ファイルを読み込みバイト列に展開します。
	 * @return ファイルのバイト列。
	 * @throws Exception 例外。
	 */
	public byte[] readContents() throws Exception {
		if (this.contents != null) {
			return this.contents;
		} else {
			this.readContents(this.tempFile);
			return this.contents;
		}
	}

	/**
	 * WebリソースのURLを取得します。
	 * @return WebリソースのURL。
	 */
	public String getWebResourceUrl() {
		return webResourceUrl;
	}

	/**
	 * WebリソースのURLを設定します。
	 * @param webResourceUrl WebリソースのURL。
	 */
	public void setWebResourceUrl(final String webResourceUrl) {
		try {
			this.webResourceUrl = webResourceUrl;
			this.setDownloadParameter("store=" + WebResourceFileStore.class.getName() + "&url=" + URLEncoder.encode(this.webResourceUrl, "utf-8"));
			String[] sp = webResourceUrl.split("/");
			this.setFileName(sp[sp.length - 1]);
		} catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage(), e);
			throw new ApplicationError(e);
		}
	}
}
