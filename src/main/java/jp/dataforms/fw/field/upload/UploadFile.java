package jp.dataforms.fw.field.upload;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

import jakarta.servlet.http.Part;
import jp.dataforms.fw.servlet.DataFormsServlet;
import jp.dataforms.fw.util.ConfUtil.Conf;
import jp.dataforms.fw.util.ConfUtil.ContentType;
import jp.dataforms.fw.util.FileUtil;
import lombok.Getter;
import lombok.Setter;

/**
 * アップロードファイル。
 * <pre>
 * FileObjectが複雑になったため作り直し。
 * </pre>
 */
public class UploadFile {
	/**
	 * Logger.
	 */
//	private static Logger logger = LogManager.getLogger(UploadFile.class);
	
	/**
	 *　このサイズを以下の場合ファイルを一時ファイルに保存する。
	 */
	private static final int FILE_SIZE_THRESHOLD = 8 * 1024 * 1024;
	
	/**
	 * ファイルタイプ。
	 */
	public enum Type {
		/** 任意のファイル。 */
		FILE
		/** 画像のファイル。 */
		, IMAGE
		/** 音声。 */
		, AUDIO
		/** 動画。 */
		, VIDEO
	}

	/**
	 * ファイルタイプ。
	 */
	@Getter
	@Setter
	private Type type = Type.FILE;
	
	/**
	 * POSTしたフィールドID。
	 */
	@Getter
	@Setter
	private String fieldId = null;
	
	/**
	 * ファイル名。
	 */
	@Getter
	@Setter
	private String fileName = null;
	
	/**
	 * ファイルの内容。
	 */
	private byte[] contents = null;
	
	/**
	 * サーバ中のファイル。
	 * <pre>
	 * DBから取得した場合に使用する。
	 * BLOBにファイルを記録した場合、BLOBの内容を一時ファイルに展開し差のファイルを設定する。
	 * DBにファイルパスを記録する場合はそのパスを設定する。
	 * </pre>
	 */
	private File serverFile = null;
	
	
	/**
	 * ファイルサイズ。 
	 */
	@Getter
	@Setter
	private Long size = null;

	/**
	 * ダウンロードパラメータ。
	 */
	@Getter
	@Setter
	private String downloadParameter = null;
	
	/**
	 * コンストラクタ。
	 */
	public UploadFile() {
		this.type = Type.FILE;
		this.fileName = null;
	}

	/**
	 * ファイル名に対応したcontentTypeを取得します。
	 * @param filename ファイル名。
	 * @return contentType。
	 */
	public String getContentType(final String filename) {
		String ret = "application/octet-stream";
		Conf conf = DataFormsServlet.getConf();
		List<ContentType> list = conf.getApplication().getContentTypeList();
		for (ContentType ct : list) {
			if (ct.match(filename)) {
				ret = ct.getContentType();
				break;
			}
		}
		return ret;
	}
	
	/**
	 * contentTypeを取得します。
	 * @return contentType。
	 */
	public String getContentType() {
		return this.getContentType(this.fileName);
	}
	
	/**
	 * ファイル名からファイルタイプを取得します。
	 * @param filename ファイル名。
	 * @return ファイルタイプ。
	 */
	public Type getType(final String filename) {
		Type ret = Type.FILE;
		String contentType = this.getContentType(filename);
		if (contentType.indexOf("image/") == 0) {
			ret = Type.IMAGE;
		} else if (contentType.indexOf("video/") == 0) {
			ret = Type.VIDEO;
		} else if (contentType.indexOf("audio/") == 0) {
			ret = Type.AUDIO;
		}
		return ret;
	}
	
	/**
	 * 一時ファイルを作成します。
	 * <pre>
	 * アップロードされたファイルを一時的に記録さるための一時ファイルを作成します。
	 * </pre>
	 * @return 一時ファイル。
	 * @throws Exception 例外。
	 */
	private File makeTempFile() throws Exception {
		File tempdir = new File(DataFormsServlet.getTempDir());
		if (!tempdir.exists()) {
			tempdir.mkdirs();
		}
		File ret = File.createTempFile("upload", ".tmp", tempdir);
		return ret;
	}


	
	/**
	 * ファイルを含むPartクラスのインスタンス。を設定します。
	 * <pre>
	 * Webからアップロードされたファイルの情報を設定します。
	 * </pre>
	 * @param part　ファイルPart。
	 */
	public void setPart(final Part part) throws Exception {
		this.serverFile = null;
		this.type = Type.FILE;
		if (part != null) {
			this.fieldId = part.getName();
			this.fileName = part.getSubmittedFileName();
			this.size = part.getSize();
			if (this.fileName != null) {
				this.type = this.getType(this.fileName);
				if (part.getSize() < UploadFile.FILE_SIZE_THRESHOLD) {
					try (InputStream is = part.getInputStream()) {
						this.contents = FileUtil.readInputStream(is);
					}
				} else {
					this.serverFile = this.makeTempFile();
					try (InputStream is = part.getInputStream()) {
						try (FileOutputStream os = new FileOutputStream(this.serverFile)) {
							FileUtil.copyStream(is, os);
						}
					}
				}
			}
		}
	}
	
	/**
	 * サーバー中のファイルを設定します。
	 * @param path サーバー中のファイルのパス。
	 */
	public void setServerFile(final String path) {
		this.serverFile = new File(path);
		this.size = this.serverFile.length();
		this.contents = null;
	}
	
	/**
	 * ファイル名と入力ストリームを設定します。
	 * <pre>
	 * データベースからファイルの情報を取得する際に使用します。
	 * ファイルの内容まで保持する場合はファイルの内容を読み込むための入力ストリームを指定します。
	 * </pre>
	 * @param fileName ファイル名。
	 * @param size ファイルサイズ。
	 * @param is 入力ストリーム。
	 * @throws Exception 例外。
	 */
	public void setContents(final String fileName, final long size, final InputStream is) throws Exception {
		this.fileName = fileName;
		this.size = size;
		if (is != null) {
			this.setContents(is);
		}
	}
	
	/**
	 * 入力ストリームからファイルの内容を設定します。
	 * @param is 入力ストリーム。
	 * @throws Exception 例外。
	 */
	public void setContents(final InputStream is) throws Exception {
		if (size < FILE_SIZE_THRESHOLD) {
			this.contents = FileUtil.readInputStream(is);
			this.serverFile = null;
		} else {
			this.serverFile = this.makeTempFile();
			try (FileOutputStream os = new FileOutputStream(this.serverFile)) {
				FileUtil.copyStream(is, os);
			}
			this.contents = null;
		}
	}
	
	/**
	 * ファイルのバイト列を取得します。
	 * @return ファイルのバイト列。
	 * @throws Exception 例外。
	 */
	public byte[] getContents() throws Exception {
		byte[] ret = null;
		if (this.contents != null) {
			return this.contents;
		}
		if (this.serverFile != null) {
			try (InputStream is = new FileInputStream(this.serverFile)) {
				ret = FileUtil.readInputStream(is);
			}
		}
		return ret;
	}
	
	/**
	 * ファイルの入力ストリームを取得します。
	 * @return ファイルの入力ストリーム。
	 * @throws Exception 例外。
	 */
	public InputStream getInputStream() throws Exception {
		InputStream ret = null;
		if (this.contents != null) {
			ret = new ByteArrayInputStream(this.contents);
		}
		if (this.serverFile != null) {
			ret = new FileInputStream(this.serverFile);
		}
		return ret;
	}
	
	/**
	 * アップロードファイルの情報を取得します。
	 * @return アップロードファイルの情報。
	 */
	public String getInfoColumnData() {
		String ufinfo = this.getFileName() + "\t" + this.getSize();
		return ufinfo;
	}

	/**
	 * 情報カラムの情報を設定します。
	 * @param info 情報カラムのjsonテキスト。
	 */
	public void setInfoColumnData(String info) {
		String[] sp = info.split("\t");
		this.setFileName(sp[0]);
		this.setSize(Long.parseLong(sp[1]));
	}
	
}
