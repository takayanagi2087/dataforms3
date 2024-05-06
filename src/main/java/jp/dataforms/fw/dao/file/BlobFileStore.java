package jp.dataforms.fw.dao.file;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.servlet.http.Part;
import jp.dataforms.fw.dao.Dao;
import jp.dataforms.fw.dao.JDBCConnectableObject;
import jp.dataforms.fw.dao.Table;
import jp.dataforms.fw.field.base.Field;
import jp.dataforms.fw.field.common.FileField;
import jp.dataforms.fw.servlet.DataFormsServlet;
import jp.dataforms.fw.util.FileUtil;
import jp.dataforms.fw.util.StringUtil;

/**
 * BLOBファイルストアクラス。
 * <pre>
 * アップロードされたファイルを、テーブル中のBLOBに記録するファイルストアです。
 * BLOB中にはファイル名、ファイルサイズを記録したヘッダ情報を付加して記録します。
 * </pre>
 */
public class BlobFileStore extends FileStore {

	/**
	 * Log.
	 */
	private static Logger logger = LogManager.getLogger(BlobFileStore.class);

	/**
	 * フィールドへのポインタ。
	 */
	private JDBCConnectableObject jdbcConnectableObject = null;


	/**
	 * コンストラクタ。
	 * @param field ファイルフィールド。
	 */
	public BlobFileStore(final JDBCConnectableObject field) {
		this.jdbcConnectableObject = field;
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
		File tempdir = new File(DataFormsServlet.getTempDir() + "/blobStore");
		if (!tempdir.exists()) {
			tempdir.mkdirs();
		}
		File ret = File.createTempFile("upload", ".tmp", tempdir);
		return ret;
	}

	/**
	 * 一時ファイルの残骸を削除する。
	 */
	public static void cleanup() {
		logger.debug("cleanup");
		File tempdir = new File(DataFormsServlet.getTempDir() + "/blobStore");
		if (!tempdir.exists()) {
			tempdir.mkdirs();
		}
		File[] list = tempdir.listFiles(new FileFilter() {
			@Override
			public boolean accept(final File pathname) {
				return Pattern.matches("^upload.+\\.tmp", pathname.getName());
			}
		});
		for (File f: list) {
			logger.debug(() -> "delete temp file=" + f.getAbsolutePath());
			f.delete();
		}
	}

	/**
	 * アップロードファイル情報をBLOB用一時ファイルに記録します。
	 * @param filename ファイル名。
	 * @param length 長さ。
	 * @param is 入力ストリーム。
	 * @return 作成されたファイル。
	 * @throws Exception 例外。
	 */
	private File makeBlobTempFile(final String filename, final long length, final InputStream is) throws Exception {
		BlobFileHeader info = new BlobFileHeader(filename, length);
		File blobFile = this.makeTempFile();
		FileOutputStream os = new FileOutputStream(blobFile);
		try {
			info.writeBlobFileHeader(os);
			FileUtil.copyStream(is, os);
		} finally {
			os.close();
		}
		logger.debug(() -> "blobfile=" + blobFile.getAbsolutePath());
		return blobFile;
	}


	/**
	 * {@inheritDoc}
	 * <pre>
	 * BLOBに保存するためヘッダ情報 + ファイル本体の形式の一時ファイルを作成します。
	 * </pre>
	 */
	@Override
	protected File makeTempFromFileItem(final Part part) throws Exception {
//		log.error("makeTempFromFileItem", new Exception());
		String fileName = FileUtil.getFileName(part.getSubmittedFileName());
		logger.debug("makeTempFromFileItem fileName=" + fileName);
		long length = part.getSize();
		File file = null;
		InputStream is = part.getInputStream();
		try {
			file = this.makeBlobTempFile(fileName, length, is);
		} finally {
			is.close();
		}
//		this.tempFile = file;
		return file;
	}

	@Override
	public File makeTemp(final String filename, final File orgfile) throws Exception {
		FileInputStream is = new FileInputStream(orgfile);
		File file = null;
		try {
			file = this.makeBlobTempFile(filename, orgfile.length(), is);
		} finally {
			is.close();
		}
		return file;
	}

	/**
	 * {@inheritDoc}
	 * <pre>
	 * FileObjectをそのまま返します。
	 * </pre>
	 */
	@Override
	public Object convertToDBValue(final Object fobj) throws Exception {
		return fobj;
	}

	/**
	 * {@inheritDoc}
	 * <pre>
	 * DAOがBLOBを読みFileObjectのインスタンスに変換するため、
	 * colValueはFileObjectのインスタンスが指定される。
	 * </pre>
	 */
	@Override
	public FileObject convertFromDBValue(final Object colValue) throws Exception {
		FileObject v = (FileObject) colValue;
		return v;
	}

	/**
	 * BLOBファイルヘッダを読み込みます。
	 * @param is 入力ストリーム。
	 * @return ファイルヘッダ。
	 * @throws Exception 例外。
	 */
	private BlobFileHeader readBlobFileHeader(final InputStream is) throws Exception {
		return BlobFileHeader.readBlobFileHeader(is);
	}


	/**
	 * ファイルストアからファイルの情報を取得します。
	 * <pre>
	 * BLOBからファイル名、ファイル長を取得します。
	 * </pre>
	 * @param colValue DBのカラム値。
	 * @return FileObjectのインスタンス。
	 * @throws Exception 例外。
	 */
	public FileObject readFileInfo(final Object colValue) throws Exception {
		FileObject fobj = null;
		InputStream is = (InputStream) colValue;
		if (is != null) {
			try {
				BlobFileHeader header = this.readBlobFileHeader(is);
				fobj = header.newFileObject();
			} finally {
				is.close();
			}
		}
		return fobj;
	}

	/**
	 * ファイルストアからファイルの情報と内容を取得します。
	 * @param colValue DBのカラム値。
	 * @return FileObjectのインスタンス。
	 * @throws Exception 例外。
	 * @deprecated readForDownloadを使用してください。
	 */
	@Deprecated
	public FileObject readFileInfoAndBody(final Object colValue) throws Exception {
		FileObject fobj = null;
		InputStream is = (InputStream) colValue;
		if (is != null) {
			try {
				BlobFileHeader header = this.readBlobFileHeader(is);
				fobj = header.newFileObject();
				File tempFile = this.makeTempFile();
				fobj.setTempFile(tempFile);
				FileOutputStream os = new FileOutputStream(tempFile);
				try {
					FileUtil.copyStream(is, os);
				} finally {
					os.close();
				}
			} finally {
				is.close();
			}
		}
		return fobj;
	}

	/**
	 * ファイルストアからファイルの情報と内容を取得します(ダウンロード用)。
	 * <pre>
	 * BLOBからファイル名、ファイル長を取得した後、ファイル本体を一時ファイルに展開します。
	 * </pre>
	 * @param colValue DBのカラム値。
	 * @return FileObjectのインスタンス。
	 * @throws Exception 例外。
	 */
	public FileObject readForDownload(final Object colValue) throws Exception {
		FileObject fobj = null;
		InputStream is = (InputStream) colValue;
		if (is != null) {
			try {
				BlobFileHeader header = this.readBlobFileHeader(is);
				fobj = header.newFileObject();
				File tempFile = this.makeTempFile();
				fobj.setTempFile(tempFile);
				FileOutputStream os = new FileOutputStream(tempFile);
				try {
					FileUtil.copyStream(is, os);
				} finally {
					os.close();
				}
			} finally {
				is.close();
			}
		}
		return fobj;
	}

	/**
	 * ファイルストアからファイルの情報と内容を取得します(DB書き込み用)。
	 * <pre>
	 * BLOBの内容をヘッダ情報も含めて一時ファイルに出力します。
	 * </pre>
	 * @param colValue DBのカラム値。
	 * @return FileObjectのインスタンス。
	 * @throws Exception 例外。
	 */
	public FileObject readForDbWriting(final Object colValue) throws Exception {
		FileObject fobj = null;
		File tempFile = null;
		InputStream is = (InputStream) colValue;
		if (is != null) {
			// BLOBの内容を全て一時ファイルに出力する。
			try {
				tempFile = this.makeTempFile();
				FileOutputStream os = new FileOutputStream(tempFile);
				try {
					FileUtil.copyStream(is, os);
				} finally {
					os.close();
				}
			} finally {
				is.close();
			}
			// 一時ファイルからヘッダ情報を取得する。
			FileInputStream fis = new FileInputStream(tempFile);
			try {
				BlobFileHeader header = this.readBlobFileHeader(fis);
				fobj = header.newFileObject();
				fobj.setTempFile(tempFile);
			} finally {
				fis.close();
			}
		}
		return fobj;
	}


	@Override
	public FileObject readFileObject(final Map<String, Object> param) throws Exception {
		String downloadingFile = (String) param.get("downloadingFile");
		logger.debug(() -> "downloadingFile=" + downloadingFile);
		Dao dao = new Dao(this.jdbcConnectableObject);
		String tblclass = (String) param.get("table");
		@SuppressWarnings("unchecked")
		Class<? extends Table> cls = (Class<? extends Table>) Class.forName(tblclass);
		Table table = cls.getDeclaredConstructor().newInstance();
		Map<String, Object> data = table.getPkFieldList().convertClientToServer(param);
		FileObject fobj = null;
		if (!StringUtil.isBlank(downloadingFile)) {
			fobj = dao.queryBlobFileInfo(table, (String) param.get("fieldId"), data);
			fobj.setTempFile(new File(downloadingFile));
		} else {
			fobj = dao.queryBlobFileObject(table, (String) param.get("fieldId"), data);
		}
		return fobj;
	}

	/**
	 * {@inheritDoc}
	 * <pre>
	 * fobj中の一時ファイルを取得します。
	 * </pre>
	 */
	@Override
	public File getTempFile(final FileObject fobj) {
		return fobj.getTempFile();
	}

	@Override
	public String getDownloadParameter(final FileField<?> field, final Map<String, Object> d) {
		Map<String, Object> m = getDownloadInfoMap(field, d);
		String ret = "key=" + this.encryptDownloadParameter(m);
		logger.debug(() -> "downloadParameter=" + ret);
		return ret;
	}


	@Override
	public Map<String, Object> getDownloadInfoMap(final FileField<?> field, final Map<String, Object> d) {
		Map<String, Object> m = new HashMap<String, Object>();
		Table table = field.getTable();
		if (table != null) {
			m.put("store", this.getClass().getName());
			m.put("table", table.getClass().getName());
			m.put("fieldId", field.getId());
			// キャッシュされるのを防止するために時刻を追加
			SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmss");
			java.util.Date now = new java.util.Date();
			m.put("ts", fmt.format(now));
			for (Field<?> f : table.getPkFieldList()) {
				m.put(f.getId(), d.get(f.getId()).toString());
			}
		} else {
			logger.warn(() -> "Table not found. field ID=" + field.getId());
		}
		return m;
	}

	/**
	 * シークサポートの有無を返します。
	 * @return 常にfalseを返します。
	 */
	public boolean isSeekingSupported() {
		return false;
	}

}
