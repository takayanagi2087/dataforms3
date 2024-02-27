package jp.dataforms.fw.app.base.page;

import java.util.Map;

import jp.dataforms.fw.controller.Page;

/**
 * 表示不可ページ。
 * <pre>
 * メニューにも表示されないページで、全てのユーザが表示できないページです。
 * dataformsが標準で持っているページをオーバーライドして隠してしまう目的で使用します。
 * </pre>
 */
public class HiddenPage extends Page {
	/**
	 * コンストラクタ。
	 */
	public HiddenPage() {
		this.setMenuItem(false);
	}

	@Override
	public boolean isAuthenticated(final Map<String, Object> params) throws Exception {
		return false;
	}
}
