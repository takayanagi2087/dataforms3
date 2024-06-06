package jp.dataforms.fw.devtool.base.page;

import java.util.Map;

import jp.dataforms.fw.servlet.DataFormsServlet;

/**
 * DB管理ページ。
 */
public class DbToolPage extends DeveloperPage {
	@Override
	public boolean isAuthenticated(Map<String, Object> params) throws Exception {
		if (DataFormsServlet.getConf().getDevelopmentTool().getDisableDatabaseTool()) {
			return false;
		}
		return super.isAuthenticated(params);
	}
}
