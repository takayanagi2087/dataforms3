package jp.dataforms.fw.app.enumtype.page;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jp.dataforms.fw.annotation.WebMethod;
import jp.dataforms.fw.app.enumtype.dao.EnumNameTable;
import jp.dataforms.fw.app.enumtype.dao.EnumTable;
import jp.dataforms.fw.controller.Page;
import jp.dataforms.fw.controller.QueryForm;
import jp.dataforms.fw.devtool.base.page.DeveloperPage;
import jp.dataforms.fw.devtool.db.dao.TableManagerDao;
import jp.dataforms.fw.field.base.Field.MatchType;
import jp.dataforms.fw.response.JsonResponse;
import jp.dataforms.fw.util.MessagesUtil;
import net.arnx.jsonic.JSON;



/**
 * 問い合わせフォームクラス。
 */
public class EnumQueryForm extends QueryForm {
	/**
	 * Logger.
	 */
	private static Logger logger = LogManager.getLogger(EnumQueryForm.class);

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
			dao.exportData("dataforms.app.enumtype.dao.EnumTable", initialDataPath);
			dao.exportData("dataforms.app.enumtype.dao.EnumNameTable", initialDataPath);
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
			dao.importData("dataforms.app.enumtype.dao.EnumTable", initialDataPath);
			dao.importData("dataforms.app.enumtype.dao.EnumNameTable", initialDataPath);
			ret = new JsonResponse(JsonResponse.SUCCESS, MessagesUtil.getMessage(this.getPage(), "message.initialDataImported"));
		} else {
			ret = new JsonResponse(JsonResponse.INVALID, MessagesUtil.getMessage(this.getPage(), "error.permission"));
		}
		return ret;
	}

	@SuppressWarnings("unchecked")
	private List<Map<String, Object>> readJson(final String json) throws Exception {
		List<Map<String, Object>> ret = null;
		try (InputStream is = new FileInputStream(json)) {
			ret = (List<Map<String, Object>>) JSON.decode(is, ArrayList.class);
		}
		return ret;
	}

	/**
	 * リストをソートします。
	 * @param list ソート対象のリスト。
	 * @return ソート結果。
	 */
	private List<Map<String, Object>> sort(List<Map<String, Object>> list) {
		list.sort((Map<String, Object> a, Map<String, Object> b) -> {
			String typeA = (String) a.get("enumTypeCode");
			String typeB = (String) b.get("enumTypeCode");
//			logger.debug("a:" + typeA + "," + a.get("sortOrder"));
//			logger.debug("b:" + typeB + "," + b.get("sortOrder"));
			short orderA = Short.parseShort((String) a.get("sortOrder"));
			short orderB = Short.parseShort((String) b.get("sortOrder"));
			if (typeA.compareTo(typeB) < 0) {
				return -1;
			} else if (typeA.compareTo(typeB) > 0) {
				return 1;
			} else {
				return orderA - orderB;
			}
		});
		return list;
	}

	/**
	 * グループ対応表を取得します。
	 * @param list グループリスト。
	 * @return グループ対応表。
	 */
	private Map<String, String> getGroupMap(final List<Map<String, Object>> list) {
		Map<String, String> ret = new HashMap<String, String>();
		for (Map<String, Object> m: list) {
			String tcode = (String) m.get("enumTypeCode");
			String gcode = (String) m.get("enumGroupCode");
			int sortOrder = Integer.parseInt((String) m.get("sortOrder"));
			String no = String.format("%03d", sortOrder);
			ret.put(tcode, gcode + "_" + no);
		}
		return ret;
	}

	/**
	 * EnumTableのデータを取得します。
	 * @param enumGroupList ver1のEnumGroupデータ。
	 * @param enumOptionList ver1のEnumOptionデータ。
	 * @return EnumTableのリスト。
	 */
	private List<Map<String, Object>> getEnumTable(
		final List<Map<String, Object>> enumGroupList
		, final List<Map<String, Object>> enumOptionList) {
		List<Map<String, Object>> ret = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> list = this.sort(enumOptionList);
		logger.info("list json=" + JSON.encode(list));
		Map<String, String> groupMap = this.getGroupMap(enumGroupList);
		long pid = 1;
		long id = 1;
		String enumTypeCode0 = "";
		Long userId = this.getPage().getUserId();
		for (Map<String, Object> m: list) {
			String enumTypeCode = (String) m.get("enumTypeCode");
			String enumOptionCode = (String) m.get("enumOptionCode");
			String sortOrder = (String) m.get("sortOrder");
//			logger.debug("data:" + enumTypeCode + "," + enumOptionCode + "," + sortOrder);
			if (!enumTypeCode0.equals(enumTypeCode)) {
				enumTypeCode0 = enumTypeCode;
				EnumTable.Entity e = new EnumTable.Entity();
				e.setEnumId(id);
				e.setEnumCode(enumTypeCode0);
				e.setParentId(null);
				e.setSortOrder(null);
				e.setEnumGroupCode(groupMap.get(enumTypeCode0));
				e.setCreateUserId(userId);
				e.setUpdateUserId(userId);
				ret.add(e.getMap());
				pid = id;
			} else {
				EnumTable.Entity e = new EnumTable.Entity();
				e.setEnumId(id);
				e.setParentId(pid);
				e.setEnumCode(enumOptionCode);
				e.setSortOrder(Short.parseShort(sortOrder));
				e.setEnumGroupCode(groupMap.get(enumTypeCode0));
				e.setCreateUserId(userId);
				e.setUpdateUserId(userId);
				ret.add(e.getMap());
			}
			id++;
		}
		// logger.debug("json=" + JSON.encode(ret));

		return ret;
	}

	private List<Map<String, Object>> getEnumNameTable(final List<Map<String, Object>> enumList, final List<Map<String, Object>> enumTypeNameList, final List<Map<String, Object>> enumOptionNameList) {
		List<Map<String, Object>> ret = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> m: enumList) {
			EnumTable.Entity e = new EnumTable.Entity(m);
			Long userId = this.getPage().getUserId();
			Long eid = e.getEnumId();
			String ecode = e.getEnumCode();
			if (e.getParentId() == null) {
				for (Map<String, Object> nm: enumTypeNameList) {
					String enumTypeCode = (String) nm.get("enumTypeCode");
					if (enumTypeCode.equals(ecode)) {
						String langCode = (String) nm.get("langCode");
						String enumTypeName = (String) nm.get("enumTypeName");
						EnumNameTable.Entity ne = new EnumNameTable.Entity();
						ne.setEnumId(eid);
						ne.setLangCode(langCode);
						ne.setEnumName(enumTypeName);
						ne.setCreateUserId(userId);
						ne.setUpdateUserId(userId);
						ret.add(ne.getMap());
					}
				}
			} else {
				for (Map<String, Object> nm: enumOptionNameList) {
					String enumOptionCode = (String) nm.get("enumOptionCode");
					if (enumOptionCode.equals(ecode)) {
						String langCode = (String) nm.get("langCode");
						String enumOptionName = (String) nm.get("enumOptionName");
						EnumNameTable.Entity ne = new EnumNameTable.Entity();
						ne.setEnumId(eid);
						ne.setLangCode(langCode);
						ne.setEnumName(enumOptionName);
						ne.setCreateUserId(userId);
						ne.setUpdateUserId(userId);
						ret.add(ne.getMap());
					}
				}
			}
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
			String enumPath = initialDataPath + "/dataforms/app/dao/enumeration/";
			List<Map<String, Object>> enumGroupList = this.readJson(enumPath + "EnumGroupTable.data.json");
			List<Map<String, Object>> enumOptionList = this.readJson(enumPath + "EnumOptionTable.data.json");
			List<Map<String, Object>> enumTypeNameList = this.readJson(enumPath + "EnumTypeNameTable.data.json");
			List<Map<String, Object>> enumOptionNameList = this.readJson(enumPath + "EnumOptionNameTable.data.json");

			List<Map<String, Object>> enumList = this.getEnumTable(enumGroupList, enumOptionList);
			List<Map<String, Object>> enumNameList = this.getEnumNameTable(enumList, enumTypeNameList, enumOptionNameList);
			logger.debug("enumNameList json=" + JSON.encode(enumNameList));
			dao.executeUpdate("delete from " + new EnumNameTable().getTableName(), new HashMap<String, Object>());
			dao.executeUpdate("delete from " + new EnumTable().getTableName(), new HashMap<String, Object>());
			dao.executeInsert(new EnumTable(), enumList);
			dao.executeInsert(new EnumNameTable(), enumNameList);
			ret = new JsonResponse(JsonResponse.SUCCESS, MessagesUtil.getMessage(this.getPage(), "message.initialDataImported"));
		} else {
			ret = new JsonResponse(JsonResponse.INVALID, MessagesUtil.getMessage(this.getPage(), "error.permission"));
		}
		return ret;
	}
}
