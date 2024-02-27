package jp.dataforms.fw.dao.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.fileupload2.core.DiskFileItem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jp.dataforms.fw.field.common.FileField;
import jp.dataforms.fw.util.FileUtil;
import jp.dataforms.fw.util.StringUtil;

/**
 * 固定フォルダに保存するファイルストアクラス。
 * <pre>
 * アップロードされたファイルを指定されたフォルダに保存し、テーブルにはそのフォルダ以下のパスを記録するファイルストアです。
 * </pre>
 */
public class StaticFolderFileStore extends FileStore {
	/**
	 * Logger。
	 */
	private Logger logger = LogManager.getLogger(StaticFolderFileStore.class);

	/**
	 * ユーザID。
	 */
	private String baseFolder = null;

	/**
	 * ファイルのパス。
	 */
	private String filePath = null;

	/**
	 * コンストラクタ。
	 * @param field フィールド。
	 */
	public StaticFolderFileStore(final FileField<? extends FileObject> field) {
		this.baseFolder = field.getBaseFolder();
	}

	@Override
	public void initDownloadParameter(Map<String, Object> p) {
		super.initDownloadParameter(p);
		this.baseFolder = (String) p.get("b");
	}

	/**
	 * ファイルの保存フォルダを取得します。
	 * @return ファイルの保存フォルダ。
	 */
	public String getSaveFolder() {
		File f = new File(this.baseFolder);
		if (!f.exists()) {
			f.mkdirs();
		}
		return this.baseFolder;
	}

	/**
	 * ファイルパスを取得します。
	 * @return ファイルパス。
	 */
	public String getFilePath() {
		return filePath;
	}

	/**
	 * ファイルパスを設定します。
	 * @param filePath ファイルパス。
	 */
	public void setFilePath(final String filePath) {
		this.filePath = filePath;
	}

	/**
	 * {@inheritDoc}
	 * <pre>
	 * 一時ファイルの保存処理ですが、性能を考慮し本来のフォルダに保存します。
	 * </pre>
	 */
	@Override
	public File makeTempFromFileItem(final DiskFileItem fileItem) throws Exception {
		File file = new File(this.baseFolder + "/" + fileItem.getName());
		FileOutputStream os = new FileOutputStream(file);
		try {
			InputStream is = fileItem.getInputStream();
			try {
				FileUtil.copyStream(is, os);
			} finally {
				is.close();
				fileItem.delete();
			}
		} finally {
			os.close();
		}
		return file;
	}

	@Override
	public File makeTemp(final String filename, final File orgfile) throws Exception {
		File file = new File(this.baseFolder + "/" + orgfile.getName());
		FileOutputStream os = new FileOutputStream(file);
		try {
			InputStream is = new FileInputStream(orgfile);
			try {
				FileUtil.copyStream(is, os);
			} finally {
				is.close();
			}
		} finally {
			os.close();
		}
		return file;
	}

	/**
	 * {@inheritDoc}
	 * <pre>
	 * 保存したファイルのパスを返します。
	 * 対応するDBのカラムはVARCHAR型で、この値をそのまま記録します。
	 * </pre>
	 */
	@Override
	public Object convertToDBValue(final Object obj) throws Exception {
		if (obj != null) {
			if (obj instanceof FileObject) {
				FileObject fobj = (FileObject) obj;
				if (fobj.getTempFile() != null) {
					String ret = fobj.getTempFile().getAbsolutePath().substring(this.baseFolder.length());
					ret = ret.replaceAll("\\\\", "/");
					logger.debug("FileFolderStore:path={}", ret);
					return ret;
				} else if (fobj.getFileName() != null){
					String ret = fobj.getFileName();
					ret = ret.replaceAll("\\\\", "/");
					logger.debug("FileFolderStore:path={}", ret);
					return ret;
				} else {
					return null;
				}
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	/**
	 * {@inheritDoc}
	 * <pre>
	 * colValueは保存されたファイルパスが渡ってくるので、その内容を解析し、fobjに設定します。
	 * </pre>
	 *
	 */
	@Override
	public FileObject convertFromDBValue(final Object colValue) throws Exception {
		FileObject fobj = new FileObject();
		String path = (String) colValue;
		if (!StringUtil.isBlank(path)) {
			File f = new File(this.baseFolder + "/" + path);
			this.filePath = path;
			fobj.setFileName(f.getName());
			fobj.setLength(f.length());
			String dlparam = this.getDownloadParameter(null, null); //"store=" + this.getClass().getName() + "&u=" + this.userId + "&t=" + this.tableName + "&f=" + this.fieldId + "&n=" + this.fileName + "&ts=" + ts;
			fobj.setDownloadParameter(dlparam);
			fobj.setTempFile(f);
		} else {
			fobj.setFileName(null);
			fobj.setLength(0);
		}
		return fobj;
	}


	@Override
	public FileObject readFileObject(final Map<String, Object> param) throws Exception {
		String path = (String) param.get("p");
		File file = new File(this.baseFolder + "/" + path);
		FileObject fobj = new FileObject();
		fobj.setFileName(file.getName());
		fobj.setLength(file.length());
		fobj.setTempFile(file);
		return fobj;
	}

	/**
	 * {@inheritDoc}
	 * <pre>
	 * 削除すべき一時ファイルは無いので、nullを返します。
	 * </pre>
	 */
	@Override
	public File getTempFile(final FileObject fobj) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 * <pre>
	 * DBに記録されたパスからダウンロードパラメータを取得します。
	 * FolderFileStoreの場合、引数のfield,dは使用しないで作成可能です。
	 * </pre>
	 */
	@Override
	public String getDownloadParameter(final FileField<?> field, final Map<String, Object> d) {
		Map<String, Object> m = getDownloadInfoMap(field, d);
		return "key=" + this.encryptDownloadParameter(m);
	}

	@Override
	public Map<String, Object> getDownloadInfoMap(final FileField<?> field, final Map<String, Object> d) {
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("store", this.getClass().getName());
		m.put("b", this.baseFolder);
		m.put("p", this.filePath);
		return m;
	}

}
