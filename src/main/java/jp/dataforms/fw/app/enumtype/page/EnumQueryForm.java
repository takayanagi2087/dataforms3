package jp.dataforms.fw.app.enumtype.page;

import java.util.HashMap;
import java.util.Map;

import jp.dataforms.fw.annotation.WebMethod;
import jp.dataforms.fw.app.enumtype.dao.EnumNameTable;
import jp.dataforms.fw.app.enumtype.dao.EnumTable;
import jp.dataforms.fw.controller.Page;
import jp.dataforms.fw.controller.QueryForm;
import jp.dataforms.fw.controller.WebComponent;
import jp.dataforms.fw.devtool.base.page.DeveloperPage;
import jp.dataforms.fw.devtool.db.dao.TableManagerDao;
import jp.dataforms.fw.field.base.Field.MatchType;
import jp.dataforms.fw.response.JsonResponse;
import jp.dataforms.fw.util.MessagesUtil;



/**
 * 問い合わせフォームクラス。
 */
public class EnumQueryForm extends QueryForm {
	/**
	 * Logger.
	 */
	// private static Logger logger = LogManager.getLogger(EnumQueryForm.class);

	/**
	 * コンストラクタ。
	 */
	public EnumQueryForm() {
		EnumTable table = new EnumTable();
		this.addField(table.getEnumCodeField()).setMatchType(MatchType.PART);
		this.addField(table.getEnumGroupCodeField()).setMatchType(MatchType.PART);
		this.addField(table.getMemoField()).setMatchType(MatchType.PART);
	}

	/**
	 * 列挙型関連テーブルのエクスポートを行います。
	 * @param p パラメータ。
	 * @return Json形式のエクスポート。
	 * @throws Exception 例外。
	 */
	@WebMethod
	public JsonResponse export(final Map<String, Object> p) throws Exception {
		JsonResponse ret = null;
		if (this.getPage().checkUserAttribute("userLevel", "developer")) {
			TableManagerDao dao = new TableManagerDao(this);
			String initialDataPath =  DeveloperPage.getExportInitalDataPath(this.getPage()); //DeveloperPage.getWebSourcePath() + "/WEB-INF/initialdata";
			dao.exportData(WebComponent.BASE_PACKAGE + ".app.enumtype.dao.EnumTable", initialDataPath);
			dao.exportData(WebComponent.BASE_PACKAGE + ".app.enumtype.dao.EnumNameTable", initialDataPath);
			ret = new JsonResponse(JsonResponse.SUCCESS, MessagesUtil.getMessage(this.getPage(), "message.initializationdatacreated"));
		} else {
			ret = new JsonResponse(JsonResponse.INVALID, MessagesUtil.getMessage(this.getPage(), "error.permission"));
		}
		return ret;
	}

	/**
	 * ユーザ関連テーブルの初期化データのインポートを行います。
	 * @param p パラメータ。
	 * @return エクスポート結果。
	 * @throws Exception 例外。
	 */
	@WebMethod
	public JsonResponse importData(final Map<String, Object> p) throws Exception {
		JsonResponse ret = null;
		if (this.getPage().checkUserAttribute("userLevel", "developer")) {
			TableManagerDao dao = new TableManagerDao(this);
			String initialDataPath = Page.getServlet().getServletContext().getRealPath("/WEB-INF/initialdata");
			dao.executeUpdate("delete from " + new EnumNameTable().getTableName(), new HashMap<String, Object>());
			dao.executeUpdate("delete from " + new EnumTable().getTableName(), new HashMap<String, Object>());
			dao.importData(WebComponent.BASE_PACKAGE + ".app.enumtype.dao.EnumTable", initialDataPath);
			dao.importData(WebComponent.BASE_PACKAGE + ".app.enumtype.dao.EnumNameTable", initialDataPath);
			ret = new JsonResponse(JsonResponse.SUCCESS, MessagesUtil.getMessage(this.getPage(), "message.initialDataImported"));
		} else {
			ret = new JsonResponse(JsonResponse.INVALID, MessagesUtil.getMessage(this.getPage(), "error.permission"));
		}
		return ret;
	}
}
