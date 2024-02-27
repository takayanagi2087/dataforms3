package jp.dataforms.fw.devtool.update.page;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jp.dataforms.fw.annotation.WebMethod;
import jp.dataforms.fw.dao.Dao;
import jp.dataforms.fw.dao.Table;
import jp.dataforms.fw.devtool.base.page.DeveloperPage;
import jp.dataforms.fw.response.Response;


/**
 * ページクラス。
 */
public class UpdateSqlPage extends DeveloperPage {

	/**
	 * Logger.
	 */
	private static Logger logger = LogManager.getLogger(UpdateSqlPage.class);

	/**
	 * テーブル名のセッションID。
	 */
	public static final String ID_TABLE_NAME = "tableName";

	/**
	 * コンストラクタ。
	 */
	public UpdateSqlPage() {
		this.addForm(new UpdateSqlForm());

	}

	/**
	 * Pageが配置されるパスを返します。
	 *
	 * @return Pageが配置されるパス。
	 */
	public String getFunctionPath() {
		return "/dataforms/devtool";
	}

	/**
	 * 操作対象テーブルクラスを取得します。
	 * <pre>
	 * ページjavaクラス作成で用のメソッドです。
	 * </pre>
	 * @return 操作対象テーブル。
	 */
	public Class<? extends  Table> getTableClass() {
		return Table.class;
	}

	/**
	 * テーブルを操作するためのDaoクラスを取得します。
	 * <pre>
	 * ページjavaクラス作成で用のメソッドです。
	 * </pre>
	 * @return Daoクラス。
	 */
	public Class<? extends Dao> getDaoClass() {
		return Dao.class;
	}

	@WebMethod
	@Override
	public Response getHtml(final Map<String, Object> params) throws Exception {
		String t = (String) params.get("t");
		logger.debug("tableName=" + t);
		this.getRequest().getSession().setAttribute(ID_TABLE_NAME, t);
		return super.getHtml(params);
	}
}
