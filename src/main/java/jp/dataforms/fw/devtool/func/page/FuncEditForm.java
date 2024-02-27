package jp.dataforms.fw.devtool.func.page;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jp.dataforms.fw.annotation.WebMethod;
import jp.dataforms.fw.app.func.dao.FuncInfoDao;
import jp.dataforms.fw.app.func.dao.FuncInfoTable;
import jp.dataforms.fw.app.func.field.FuncNameField;
import jp.dataforms.fw.controller.EditForm;
import jp.dataforms.fw.controller.Page;
import jp.dataforms.fw.devtool.base.page.DeveloperPage;
import jp.dataforms.fw.devtool.db.dao.TableManagerDao;
import jp.dataforms.fw.devtool.field.WebSourcePathField;
import jp.dataforms.fw.field.base.FieldList;
import jp.dataforms.fw.htmltable.EditableHtmlTable;
import jp.dataforms.fw.response.JsonResponse;
import jp.dataforms.fw.servlet.DataFormsServlet;
import jp.dataforms.fw.util.FileUtil;
import jp.dataforms.fw.util.MessagesUtil;
import jp.dataforms.fw.util.SequentialProperties;

/**
 *
 * 機能編集フォームクラス。
 * <pre>
 * func_infoを編集するためのフォームです。
 * </pre>
 */
public class FuncEditForm extends EditForm {

    /**
     * Logger.
     */
    private static Logger logger = LogManager.getLogger(FuncEditForm.class.getName());

	/**
	 * コンストラクタ。
	 */
	public FuncEditForm() {
		this.addField(new WebSourcePathField());
		FuncInfoTable tbl = new FuncInfoTable();
		FieldList flist = new FieldList();
		flist.addAll(tbl.getFieldList());
		flist.add(new FuncNameField());
		EditableHtmlTable funcTable = new EditableHtmlTable("funcTable", flist);
		this.addHtmlTable(funcTable);
	}


	@Override
	public void init() throws Exception {
		super.init();
		this.setFormDataMap(this.queryData(null));
	}

	@Override
	protected Map<String, Object> queryData(final Map<String, Object> data) throws Exception {
		Map<String, Object> ret = new HashMap<String, Object>();
		ret.put("webSourcePath", DeveloperPage.getWebSourcePath());
		FuncInfoDao dao = new FuncInfoDao(this);
		List<Map<String, Object>> list = dao.queryFuncList(true);
		for (Map<String, Object> m: list) {
			FuncInfoTable.Entity e = new FuncInfoTable.Entity(m);
			String funcPath = e.getFuncPath(); // (String) m.get("funcPath");
			String propFile = funcPath + "/Function";
			String key = funcPath.substring(1).replaceAll("/", ".");
			logger.debug("key=" + key);
			SequentialProperties prop = MessagesUtil.getProperties(this.getPage(), propFile);
			String name = prop.getProperty(key);
			m.put("funcName", name);
		}
		ret.put("funcTable", list);
		return ret;
	}

	/**
	 * {@inheritDoc}
	 * <pre>
	 * 常に更新モードで動作させる。
	 * </pre>
	 */
	@Override
	protected boolean isUpdate(final Map<String, Object> data) throws Exception {
		return true;
	}

	@Override
	protected void insertData(final Map<String, Object> data) throws Exception {
		// 何もしない
	}

	/**
	 * Function.propertiesのパスを新規作成します。
	 * @param path 機能のパス。
	 * @return 言語に応じたFunction.propertiesのバス。
	 * @throws Exception 例外。
	 */
	private String getFunctionPropertiesPath(final String path) throws Exception {
		String ret = path + "/Function.properties";
/*		String lang = this.getPage().getRequest().getLocale().getLanguage();
		String langlist = DataFormsServlet.getSupportLanguage();
		log.debug("langlist=" + langlist + "," + lang);
		if (langlist.indexOf(lang) >= 0) {
			ret = ret.replaceAll("\\.properties$", "_" + lang + ".properties");
		}*/
		return ret;
	}

	@Override
	protected void updateData(final Map<String, Object> data) throws Exception {
		this.setUserInfo(data);
		Page page = this.getPage();
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> list = (List<Map<String, Object>>) data.get("funcTable");
		FuncInfoDao dao = new FuncInfoDao(this);
		dao.saveFuncList(list);
		String webResourcePath = (String) data.get("webSourcePath");
		for (Map<String, Object> m: list) {
			FuncInfoTable.Entity e = new FuncInfoTable.Entity(m);
			String path = e.getFuncPath(); //(String) m.get("funcPath");
			String name = (String) m.get("funcName");
			if (path.indexOf("/dataforms") != 0) {
				String funcprop = page.getAppropriatePath(path + "/Function.properties", page.getRequest());
				if (funcprop == null) {
					funcprop = this.getFunctionPropertiesPath(path);
				}
				funcprop = webResourcePath + funcprop;
				String text = "";
				File propfile = new File(funcprop);
				if (propfile.exists()) {
					text = FileUtil.readTextFile(funcprop, DataFormsServlet.getEncoding());
				}
				logger.debug("funcprop=" + funcprop);
				SequentialProperties prop = new SequentialProperties();
				prop.loadText(text);
				prop.put(path.substring(1).replaceAll("/", "."), name);
				String str = prop.getSaveText();
				logger.debug("str=" + str);
				FileUtil.writeTextFileWithBackup(funcprop, str, DataFormsServlet.getEncoding());
			}
		}
	}

	@Override
	public void deleteData(final Map<String, Object> data) throws Exception {
		// 何もしない
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
			dao.exportData("dataforms.app.func.dao.FuncInfoTable", initialDataPath);
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
			logger.debug("initialDataPath=" + initialDataPath);
			dao.executeUpdate("delete from " + new FuncInfoTable().getTableName(), new HashMap<String, Object>());
			dao.importData("dataforms.app.func.dao.FuncInfoTable", initialDataPath);
			ret = new JsonResponse(JsonResponse.SUCCESS, MessagesUtil.getMessage(this.getPage(), "message.initialDataImported"));
		} else {
			ret = new JsonResponse(JsonResponse.INVALID, MessagesUtil.getMessage(this.getPage(), "error.permission"));
		}
		return ret;
	}

	/**
	 * ユーザ関連テーブルのV1.x形式の初期化データのインポートを行います。
	 * @param p パラメータ。
	 * @return エクスポート結果。
	 * @throws Exception 例外。
	 */
	@WebMethod
	public JsonResponse importV1Data(final Map<String, Object> p) throws Exception {
		JsonResponse ret = null;
		if (this.getPage().checkUserAttribute("userLevel", "developer")) {
			TableManagerDao dao = new TableManagerDao(this);
			String initialDataPath = Page.getServlet().getServletContext().getRealPath("/WEB-INF/initialdata_v1");
			logger.debug("initialDataPath=" + initialDataPath);
			dao.executeUpdate("delete from " + new FuncInfoTable().getTableName(), new HashMap<String, Object>());
			dao.importV1Data("dataforms.app.func.dao.FuncInfoTable", "/dataforms/app/dao/func/FuncInfoTable.data.json", initialDataPath);
			ret = new JsonResponse(JsonResponse.SUCCESS, MessagesUtil.getMessage(this.getPage(), "message.initialDataImported"));
		} else {
			ret = new JsonResponse(JsonResponse.INVALID, MessagesUtil.getMessage(this.getPage(), "error.permission"));
		}
		return ret;
	}


}
