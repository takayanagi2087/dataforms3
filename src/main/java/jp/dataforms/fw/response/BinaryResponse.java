package jp.dataforms.fw.response;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URLEncoder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jp.dataforms.fw.dao.file.FileObject;
import jp.dataforms.fw.servlet.DataFormsServlet;
import jp.dataforms.fw.util.HttpRangeInfo;

/**
 * バイナリデータの応答情報クラス。
 *
 */
public class BinaryResponse extends FileResponse {

	/**
	 * Logger.
	 */
	private static Logger logger = LogManager.getLogger(BinaryResponse.class);

	/**
	 * 入力ストリーム。
	 */
	private InputStream inputStream = null;

	/**
	 * 削除すべき一時ファイル。
	 */
	private File tempFile = null;

	/**
	 * HTTP要求情報。
	 */
	private HttpServletRequest request = null;


	/**
	 * HTTP要求情報を取得します。
	 * @return HTTPの要求情報。
	 */
	public HttpServletRequest getRequest() {
		return request;
	}

	/**
	 * HTTP応答情報を設定します。
	 * @param request HTTP応答情報。
	 */
	public void setRequest(final HttpServletRequest request) {
		this.request = request;
	}

	/**
	 * Content-Dispositionの種類。
	 *
	 */
	public enum Disposition {
		/** Content-Disposition: inlineを指定し、ブラウザのインライン表示を促します。 */
		INLINE,
		/** Content-Disposition: ATTACHMENTを指定し、ブラウザのファイルダウンロードを促します。 */
		ATTACHMENT
	}

	/**
	 * Content-Displitionのinline, attachの区分。
	 */
	private Disposition contentDisposition = Disposition.ATTACHMENT;

	/**
	 * Content-Displitionのinline, attachの区分を取得します。
	 * @return Content-Displitionのinline, attachの区分。
	 */
	public Disposition getContentDisposition() {
		return contentDisposition;
	}

	/**
	 * Content-Displitionのinline, attachの区分を設定します。
	 * @param contentDisposition Content-Displitionのinline, attachの区分。
	 */
	public void setContentDisposition(Disposition contentDisposition) {
		this.contentDisposition = contentDisposition;
	}

	/**
	 * コンストラクタ。
	 * @param result 実行結果。
	 */
	public BinaryResponse(final byte[] result) {
		this.setResult(result);
		this.setContentType("application/octet-stream");
		this.inputStream = new ByteArrayInputStream(result);
	}

	/**
	 * コンストラクタ。
	 * @param result 実行結果。
	 * @param contentType コンテントタイプ。
	 */
	public BinaryResponse(final byte[] result, final String contentType) {
		this.setResult(result);
		this.setContentType(contentType);
		this.inputStream = new ByteArrayInputStream(result);
	}

	/**
	 * コンストラクタ。
	 * @param result 実行結果。
	 * @param contentType コンテントタイプ。
	 * @param fileName ファイル名。
	 */
	public BinaryResponse(final byte[] result, final String contentType, final String fileName) {
		this.setResult(result);
		this.setContentType(contentType);
		this.setFileName(fileName);
		this.inputStream = new ByteArrayInputStream(result);
	}

	/**
	 * コンストラクタ。
	 * @param is 結果の入力ストリーム。
	 */
	public BinaryResponse(final InputStream is) {
		this.setContentType("application/octet-stream");
		this.inputStream = is;
	}

	/**
	 * コンストラクタ。
	 * @param is 結果の入力ストリーム。
	 * @param contentType コンテントタイプ。
	 */
	public BinaryResponse(final InputStream is, final String contentType) {
		this.setContentType(contentType);
		this.inputStream = is;
	}

	/**
	 * コンストラクタ。
	 * @param is 結果の入力ストリーム。
	 * @param contentType コンテントタイプ。
	 * @param fileName ファイル名。
	 */
	public BinaryResponse(final InputStream is, final String contentType, final String fileName) {
		this.setContentType(contentType);
		this.setFileName(fileName);
		this.inputStream = is;
	}

	/**
	 * コンストラクタ。
	 * @param fobj ファイルオブジェクト。
	 * @throws Exception 例外。
	 */
	public BinaryResponse(final FileObject fobj) throws Exception {
		this.setContentType(fobj.getContentType());
		this.setFileName(fobj.getFileName());
		this.inputStream = fobj.openInputStream();
	}


	/**
	 * コンストラクタ。
	 * @param path 送信するファイル。
	 * @param contentType コンテントタイプ。
	 * @param fileName ファイル名。
	 * @throws Exception 例外。
	 */
	public BinaryResponse(final String path, final String contentType, final String fileName) throws Exception {
		this.setContentType(contentType);
		this.setFileName(fileName);
		this.inputStream = new FileInputStream(path);
	}



	/**
	 * 一時ファイルを取得します。
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
	 * {@inheritDoc}
	 * バイナリデータを送信する。
	 */
	@Override
	public void send(final HttpServletResponse resp) throws Exception {
		resp.setContentType(this.getContentType());
		logger.debug(() -> "content-type:" + resp.getContentType());
		if (this.getFileName() != null) {
			// このヘッダはjavascripで解析するので、filename*等のパラメータは追加しないでください。
			final String CONTENT_DISPOSITION_FORMAT = "%s; filename=%s";
			String utf8fn =  URLEncoder.encode(this.getFileName(), DataFormsServlet.getEncoding());
			String h = String.format(CONTENT_DISPOSITION_FORMAT, this.getContentDisposition().name().toLowerCase(),  utf8fn);
			logger.debug(() -> "Content-Disposition:" + h);
			resp.setHeader("Content-Disposition", h);
		}
		HttpRangeInfo p = new HttpRangeInfo(this.getRequest());
		p.parse(this.inputStream);
		logger.debug(() -> "filename=" + this.getFileName());
		logger.debug(() -> "encoding=" + DataFormsServlet.getEncoding());
		logger.debug(() -> "status=" + p.getStatus());
		logger.debug(() -> "contentLength=" + p.getContentLength());
		logger.debug(() -> "contentRange=" + p.getContentRange());
		logger.debug(() -> "Accept-Ranges=" + "bytes");
		resp.setHeader("Accept-Ranges", "bytes");
		resp.setHeader("Content-Length", "" +  p.getContentLength());
		resp.setStatus(p.getStatus());
		resp.addCookie(new Cookie("downloaded", "1"));
		if (p.getContentRange() != null) {
			resp.setHeader("Content-Range", p.getContentRange());
		}
		long sendSize = 0;
		try {
			BufferedOutputStream bos = new BufferedOutputStream(resp.getOutputStream());
			try {
				BufferedInputStream bis = new BufferedInputStream(this.inputStream);
				try {
					bis.skip(p.getStart());
					for (long idx = p.getStart(); idx <= p.getFinish(); idx++) {
						int c = bis.read();
						if (c < 0) {
							break;
						}
						bos.write(c);
						sendSize++;
					}
				} finally {
					bis.close();
				}
			} finally {
				bos.close();
				if (this.getTempFile() != null) {
					this.getTempFile().delete();
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			logger.debug("sendSize={}", sendSize);
		}
	}

	/**
	 * ファイルへの保存。
	 * @param path 保存先。
	 * @throws Exception 例外。
	 */
	public void saveFile(final String path) throws Exception {
		FileOutputStream os = new FileOutputStream(path);
		try {
			InputStream is = this.inputStream;
			try {
				byte[] buf = new byte[16 * 1024];
				while (true) {
					int len = is.read(buf);
					if (len <= 0) {
						break;
					}
					os.write(buf, 0, len);
				}
			} finally {
				is.close();
			}
		} finally {
			os.flush();
			os.close();
		}
		if (this.getTempFile() != null) {
			this.getTempFile().delete();
		}
	}

	@Override
	public String toString() {
		return super.toString() + ":" + this.getFileName();
	}

}
