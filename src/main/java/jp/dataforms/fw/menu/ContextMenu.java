package jp.dataforms.fw.menu;

import java.util.List;
import java.util.Map;

import jp.dataforms.fw.controller.WebComponent;
import jp.dataforms.fw.util.MessagesUtil;

/**
 * コンテキストメニュー。
 */
public class ContextMenu extends WebComponent {
	
	/**
	 * メニュー項目を定義するプロパティキー。
	 */
	private String propertyKey = null;
	
	/**
	 * メニュー項目リスト。
	 */
	private List<Map<String, Object>> itemList = null;
	
	/**
	 * コンストラクタ。
	 * @param propKey メニュー項目を定義するプロパティキー。
	 */
	public ContextMenu(final String propKey) {
		this.setId(this.getDefaultId());
		this.propertyKey = propKey;
	}
	
	/**
	 * コンストラクタ。
	 * @param id メニューID。
	 * @param propKey メニュー項目を定義するプロパティキー。
	 */
	public ContextMenu(final String id, final String propKey) {
		this.setId(id);
		this.propertyKey = propKey;
	}

	@Override
	public void init() throws Exception {
		super.init();
		this.itemList = MessagesUtil.getSelectFieldOption(this.getPage(), this.propertyKey);
	}

	@Override
	public Map<String, Object> getProperties() throws Exception {
		Map<String, Object> prop = super.getProperties();
		prop.put("itemList", this.itemList);
		return prop;
	}
}
