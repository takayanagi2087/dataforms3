package jp.dataforms.fw.servlet;

import java.io.File;
import java.util.Enumeration;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import jp.dataforms.fw.field.common.FileField;
import jp.dataforms.fw.field.upload.UploadField;
import jp.dataforms.fw.field.upload.UploadFile;

/**
 * セッションタイムアウト時に残っているストリーミングファイルの残骸を削除するための
 * セッションリスナークラスです。
 *
 */
@WebListener
public class StreamingFileCleaner implements HttpSessionListener {

	/**
	 * Logger.
	 */
	private Logger logger = LogManager.getLogger(StreamingFileCleaner.class);

	/**
	 * コンストラクタ。
	 */
	public StreamingFileCleaner() {
		
	}
	
	@Override
	public void sessionCreated(final HttpSessionEvent arg0) {
	}

	@Override
	public void sessionDestroyed(final HttpSessionEvent se) {
		logger.debug("sessionDestroyed");
		HttpSession session = se.getSession();
		Enumeration<String> e = session.getAttributeNames();
		while (e.hasMoreElements()) {
			String name = e.nextElement();
			// TODO:FileField対応のそょりなので、非推奨だが互換性のために当分残す。
			if (Pattern.matches("^" + FileField.DOWNLOADING_FILE + ".+", name)) {
				logger.debug(() -> "attribute name=" + name);
				String filename = (String) session.getAttribute(name);
				logger.debug(() -> "filename=" + filename);
				File f = new File(filename);
				f.delete();
			}
			// UploadFieldストリーミング対応のセッション削除
			if (Pattern.matches("^" + UploadField.DOWNLOADING_UPLOAD_FILE + ".+", name)) {
				logger.debug(() -> "attribute name=" + name);
				UploadFile uploadFile = (UploadFile) session.getAttribute(name);
				uploadFile.deleteServerFile();
			}
		}
	}

}
