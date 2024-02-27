package jp.dataforms.fw.dao.file;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.text.DecimalFormat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jp.dataforms.fw.util.ObjectUtil;

/**
 * BLOB項目の先頭に保存する管理情報クラス。
 *
 */
public class BlobFileHeader implements Serializable {

	/**
	 * UID。
	 */
	private static final long serialVersionUID = -7222388177179295487L;

	/**
	 * Log.
	 */
	private static Logger logger = LogManager.getLogger(BlobFileHeader.class);

	/**
	 * ファイル名。
	 */
	private String fileName = null;
	/**
	 * ファイル長。
	 */
	private long length = 0L;

	/**
	 * コンストラクタ。
	 * @param name ファイル名。
	 * @param len ファイル長。
	 */
	public BlobFileHeader(final String name, final long len) {
		this.fileName = name;
		this.length = len;
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
	 * ヘッダ情報に対応したFileObjectのインスタンスを作成します。
	 * @return ヘッダ情報に対応したFileObjectのインスタンス。
	 */
	public FileObject newFileObject() {
		FileObject fobj = new FileObject();
		fobj.setFileName(this.getFileName());
		fobj.setLength(this.getLength());
		return fobj;
	}

	/**
	 * BLOBファイルヘッダを出力します。
	 * @param os 出力す先。
	 * @throws Exception 例外。
	 */
	public void writeBlobFileHeader(final OutputStream os) throws Exception {
		byte[] data = ObjectUtil.getBytes(this);
		int hlen = data.length;
		DecimalFormat fmt = new DecimalFormat("00000000");
		String headerLength = fmt.format(hlen);
		os.write(headerLength.getBytes());
		os.write(data);
	}

	/**
	 * BLOBファイルヘッダを読み込みます。
	 * @param is 入力ストリーム。
	 * @return ファイルヘッダ。
	 * @throws Exception 例外。
	 */
	public static BlobFileHeader readBlobFileHeader(final InputStream is) throws Exception {
		byte[] lenbuf = new byte[8];
		is.read(lenbuf);
		int len = Integer.parseInt(new String(lenbuf));
		logger.debug(() -> "header length=" + len);
		byte[] fileHeaderBuffer = new byte[len];
		is.read(fileHeaderBuffer);
		BlobFileHeader header = (BlobFileHeader) ObjectUtil.getObject(fileHeaderBuffer);
		return header;
	}
}
