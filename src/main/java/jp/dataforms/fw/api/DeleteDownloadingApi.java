package jp.dataforms.fw.api;

import java.io.File;
import java.util.Enumeration;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.servlet.http.HttpSession;
import jp.dataforms.fw.annotation.WebMethod;
import jp.dataforms.fw.controller.WebApi;
import jp.dataforms.fw.field.common.FileField;
import jp.dataforms.fw.field.upload.UploadField;
import jp.dataforms.fw.field.upload.UploadFile;
import jp.dataforms.fw.response.JsonResponse;
import jp.dataforms.fw.response.Response;

/**
 * ダウンロード中のファイルを削除するAPIクラスです。
 */
public class DeleteDownloadingApi extends WebApi {
	
	/**
	 * Logger.
	 */
	private static Logger logger = LogManager.getLogger(DeleteDownloadingApi.class);

	/**
	 * コンストラクタ。
	 */
	public DeleteDownloadingApi() {
	}

	@Override
	public boolean isAuthenticated(Map<String, Object> params) throws Exception {
		return true;
	}

	@WebMethod(useDB = false)
	@Override
	public Response exec(Map<String, Object> p) throws Exception {
		HttpSession session = this.getRequest().getSession();
		Enumeration<String> e = session.getAttributeNames();
		while (e.hasMoreElements()) {
			String name = e.nextElement();
			// TODO:FileField対応の処理、非推奨だが互換性のために当分残す。
			if (Pattern.matches("^" + FileField.DOWNLOADING_FILE + ".+", name)) {
				logger.debug(() -> "attribute name=" + name);
				String filename = (String) session.getAttribute(name);
				logger.debug(() -> "filename=" + filename);
				File f = new File(filename);
				if (f.delete()) {
					session.removeAttribute(name);
				}
			}
			// UploadFieldストリーミング対応のセッション削除
			if (Pattern.matches("^" + UploadField.DOWNLOADING_UPLOAD_FILE + ".+", name)) {
				logger.debug(() -> "attribute name=" + name);
				UploadFile uploadFile = (UploadFile) session.getAttribute(name);
				if (uploadFile.deleteServerFile()) {
					// ファイルの削除に成功したらセッションも削除。
					session.removeAttribute(name);
				}
				// これで削除できなかった場合は、セッション廃棄時にStreamingFileCleanerで削除する。
			}
		}

		return new JsonResponse(JsonResponse.SUCCESS, "");
	}

}
