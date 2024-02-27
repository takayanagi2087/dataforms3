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

	@Override
	public void sessionCreated(final HttpSessionEvent arg0) {
	}

	@Override
	public void sessionDestroyed(final HttpSessionEvent se) {
		HttpSession session = se.getSession();
		Enumeration<String> e = session.getAttributeNames();
		while (e.hasMoreElements()) {
			String name = e.nextElement();
			if (Pattern.matches("^" + FileField.DOWNLOADING_FILE + ".+", name)) {
				logger.debug(() -> "attribute name=" + name);
				String filename = (String) session.getAttribute(name);
				logger.debug(() -> "filename=" + filename);
				File f = new File(filename);
				f.delete();
			}
		}
	}

}
