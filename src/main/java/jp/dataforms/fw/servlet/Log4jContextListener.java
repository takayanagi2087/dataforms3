package jp.dataforms.fw.servlet;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configurator;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import jp.dataforms.fw.util.ConfUtil.Conf;

/**
 * Log4j開放リスナー。
 */
@WebListener
public class Log4jContextListener implements ServletContextListener {

	/**
	 * Logger.
	 */
	private static Logger logger = LogManager.getLogger(Log4jContextListener.class);

	/**
	 * サーバーのlog4j2.xmlのパス。
	 */
	public static String serverLog4j2Xml = null;
	

	/**
	 * コンストラクタ。
	 * 
	 */
	public Log4jContextListener() {
	}

	/**
	 * 初期化処理。
	 */
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		ServletContextListener.super.contextInitialized(sce);
		try {
			String confPath = sce.getServletContext().getRealPath("/WEB-INF/dataforms.conf.jsonc");
			File cf = new File(confPath);
			if (cf.exists()) {
				logger.debug("confPath=" + confPath);
				Conf appConf  =  Conf.read(confPath);
				logger.debug("server log4j2.xml=" + appConf.getApplication().getServerLog4j2Xml());
				Log4jContextListener.serverLog4j2Xml = appConf.getApplication().getServerLog4j2Xml();
			}
			
			if (Log4jContextListener.serverLog4j2Xml != null) {
				File log4j2xml = new File(Log4jContextListener.serverLog4j2Xml);
				if (log4j2xml.exists()) {
					logger.info("log4j2 config = " + Log4jContextListener.serverLog4j2Xml);
					Configurator.initialize(null, Log4jContextListener.serverLog4j2Xml);
				}
			}
			logger.info("Log4jContextListener initialized.");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		// Log4jのLoggerContextを確実にシャットダウン
		logger.info("Shutting down Log4j...");
		LoggerContext context = (LoggerContext) LogManager.getContext(false);
		context.stop();
		logger.info("context.stop");
		LogManager.shutdown();
	}

}
