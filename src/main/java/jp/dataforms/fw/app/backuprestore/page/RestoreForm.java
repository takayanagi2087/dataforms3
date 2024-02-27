package jp.dataforms.fw.app.backuprestore.page;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jp.dataforms.fw.annotation.WebMethod;
import jp.dataforms.fw.controller.Form;
import jp.dataforms.fw.dao.file.FileObject;
import jp.dataforms.fw.devtool.db.dao.TableManagerDao;
import jp.dataforms.fw.field.common.FlagField;
import jp.dataforms.fw.field.common.FolderStoreFileField;
import jp.dataforms.fw.response.JsonResponse;
import jp.dataforms.fw.response.Response;
import jp.dataforms.fw.servlet.DataFormsServlet;
import jp.dataforms.fw.util.FileUtil;
import jp.dataforms.fw.util.MessagesUtil;
import jp.dataforms.fw.validator.RequiredValidator;
import jp.dataforms.fw.validator.ValidationError;

/**
 * リストアーフォーム。
 *
 */
public class RestoreForm extends Form {

	/**
	 * Logger.
	 */
	private static Logger logger = LogManager.getLogger(RestoreForm.class);
	/**
	 * コンストラクタ。
	 */
	public RestoreForm() {
		super(null);
		this.addField(new FolderStoreFileField("backupFile")).addValidator(new RequiredValidator());
		this.addField(new FlagField("deleteDataFlag"));
	}

	@Override
	public void init() throws Exception {
		super.init();
		this.setFormData("deleteDataFlag", "1");
	}

	/**
	 * バックアップファイルを解凍します。
	 * @param fileObject バックアップファイル。
	 * @return 展開されたディレクトリのパス。
	 * @throws Exception 例外。
	 */
	public String unpackRestoreFile(final FileObject fileObject) throws Exception {
		InputStream is = new FileInputStream(fileObject.getTempFile());
		String ret = null;
		try {
			File bkdir = new File(DataFormsServlet.getTempDir() + "/restore");
			if (!bkdir.exists()) {
				bkdir.mkdirs();
			}
			Path backup = FileUtil.createTempDirectory(bkdir.getAbsolutePath(), "restore");
			FileUtil.unpackZipFile(is, backup.toString());
			ret = backup.toString();
		} finally {
			is.close();
			fileObject.getTempFile().delete();
		}
		return ret;
	}

	/**
	 * リストアを行います。
	 * @param p パラメータ。
	 * @return 処理結果。
	 * @throws Exception 例外。
	 */
	@WebMethod
	public Response restore(final Map<String, Object> p) throws Exception {
		JsonResponse resp = null;
		List<ValidationError> list = this.validate(p);
		if (list.size() == 0) {
			Map<String, Object> data = this.convertToServerData(p);
			String deleteDataFlag = (String) data.get("deleteDataFlag");
			FileObject fo = (FileObject) data.get("backupFile");
			logger.debug("fo.class=" + fo.getClass().getName());
			logger.debug("fo.fileName=" + fo.getFileName());
			logger.debug("fo.length=" + fo.getLength());
			String path = this.unpackRestoreFile(fo);
			try {
				TableManagerDao dao = new TableManagerDao(this);
				dao.dropAllForeignKeys(); // 全外部キーの削除
				List<String> flist = FileUtil.getFileList(path);
				for (String fn: flist) {
					if (Pattern.matches(".*\\.data\\.json$", fn)) {
						logger.debug(() -> "fn=" + fn);
						String classname = fn.substring(path.length() + 1).replaceAll("[\\\\/]", ".").replaceAll("\\.data\\.json$", "");
						logger.debug(() -> "classname=" + classname);
						if ("1".equals(deleteDataFlag)) {
							dao.deleteTableData(classname);
						}
						dao.importData(classname, path);
					}
				}
				dao.createAllForeignKeys(); // 全外部キーの作成
			} finally {
				logger.debug(() -> "delete:" + path.toString());
				if (path.indexOf("restore") >= 0) {
					FileUtil.deleteDirectory(path);
				}
			}
			resp = new JsonResponse(JsonResponse.SUCCESS, MessagesUtil.getMessage(this.getPage(), "message.restored"));
		} else {
			resp = new JsonResponse(JsonResponse.INVALID, list);
		}
		return resp;
	}

}
