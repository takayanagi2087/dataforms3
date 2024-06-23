package jp.dataforms.fw.devtool.base.page;

import java.util.Map;

import jp.dataforms.fw.servlet.DataFormsServlet;

/**
 * ソースコード生成ページ。
 */
public class SrcGenPage extends DeveloperPage {
	/**
	 * コンストラクタ。
	 */
	public SrcGenPage() {
		
	}
	
	
	@Override
	public boolean isAuthenticated(Map<String, Object> params) throws Exception {
		if (DataFormsServlet.getConf().getDevelopmentTool().getDisableCodeGenerationTool()) {
			return false;
		}
		return super.isAuthenticated(params);
	}
}
