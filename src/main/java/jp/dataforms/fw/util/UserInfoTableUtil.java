package jp.dataforms.fw.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jp.dataforms.fw.app.user.dao.UserInfoTable;
import jp.dataforms.fw.controller.Page;
import jp.dataforms.fw.servlet.DataFormsServlet;

/**
 * ユーザ情報テーブルユーティリティ。
 * @author takay
 *
 */
public final class UserInfoTableUtil {
	/**
	 * Logger.
	 */
	private static Logger logger = LogManager.getLogger(UserInfoTableUtil.class);

	/**
	 * テーブルクラス。
	 */
	private static Class<? extends UserInfoTable> tableClass = null;

	// 初期化処理。
	static {
		UserInfoTableUtil.tableClass = UserInfoTableUtil.getUserInfoTableClass();
	}

	/**
	 * コンストラクタ。
	 */
	private UserInfoTableUtil() {

	}

	/**
	 * web.xmlのuser-info-table-classに指定されたテーブルのクラスを取得します。
	 * @return テーブルクラス。
	 */
	@SuppressWarnings("unchecked")
	public static Class<? extends UserInfoTable> getUserInfoTableClass() {
		Class<? extends UserInfoTable> table = null;
		try {
			DataFormsServlet servlet = Page.getServlet();
			String className = servlet.getServletContext().getInitParameter("user-info-table-class");
			if (className != null) {
				className = className.trim();
				if (className.length() != 0) {
					table = (Class<? extends UserInfoTable>) Class.forName(className);
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return table;
	}

	/**
	 * ユーザ情報テーブルのインスタンスを取得します。
	 * @return ユーザ情報テーブルのインスタンス。
	 */
	public static UserInfoTable newUserInfoTable() {
		UserInfoTable ret = new UserInfoTable();
		try {
			if (UserInfoTableUtil.tableClass != null) {
				ret = UserInfoTableUtil.tableClass.getConstructor().newInstance();
			}
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
		}
		return ret;
	}

}
