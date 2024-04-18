package jp.dataforms.fw.devtool.menu.page;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jp.dataforms.fw.controller.EditForm;
import jp.dataforms.fw.controller.WebComponent;
import jp.dataforms.fw.devtool.menu.gen.AppFunctionMapGenerator;
import jp.dataforms.fw.field.base.TextField;
import jp.dataforms.fw.menu.FunctionMap;
import jp.dataforms.fw.util.JsonUtil;
import jp.dataforms.fw.validator.RequiredValidator;

/**
 * メニュー編集フォーム。
 */
public class MenuEditForm extends EditForm {
	/**
	 * Logger.
	 */
	private Logger logger = LogManager.getLogger(MenuEditForm.class);
	
	/**
	 * アプリケーションパッケージ名のフィールドID。
	 */
	public static final String ID_APP_BASE_PACKAGE = "appBasePackage";

	/**
	 * コンストラクタ。
	 */
	public MenuEditForm() {
		this.addField(new TextField(ID_APP_BASE_PACKAGE)).addValidator(new RequiredValidator());
		this.addHtmlTable(new MenuTable());
	}
	
	@Override
	public void init() throws Exception {
		super.init();
		this.setFormDataMap(this.queryData(new HashMap<String, Object>()));
	}
	
	@Override
	protected Map<String, Object> queryData(Map<String, Object> data) throws Exception {
		FunctionMap fmap = WebComponent.getFunctionMap();
		Map<String, Object> ret = fmap.getMenuMap();
		String json = JsonUtil.encode(ret);
		logger.debug("*** json=" + json);
		return ret;
	}

	@Override
	protected boolean isUpdate(Map<String, Object> data) throws Exception {
		// 常に更新モード。
		return true;
	}

	@Override
	protected void insertData(Map<String, Object> data) throws Exception {
		// 使用しない
	}

	@Override
	protected void updateData(Map<String, Object> data) throws Exception {
		AppFunctionMapGenerator gen = new AppFunctionMapGenerator();
		gen.generage(this, data);
	}

	@Override
	public void deleteData(Map<String, Object> data) throws Exception {
		// 使用しない
	}

}
