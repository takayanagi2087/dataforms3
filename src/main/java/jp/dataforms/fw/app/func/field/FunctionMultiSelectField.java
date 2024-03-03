package jp.dataforms.fw.app.func.field;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jp.dataforms.fw.controller.WebComponent;
import jp.dataforms.fw.field.common.MultiSelectField;
import jp.dataforms.fw.field.common.SelectField;
import jp.dataforms.fw.menu.FunctionMap.Menu;

/**
 * 機能複数選択フィールド。
 *
 */
public class FunctionMultiSelectField extends MultiSelectField<String> {
	/**
	 * フィールドコメント。
	 */
	private static final String COMMENT = "機能複数選択";

	/**
	 * コンストラクタ。
	 */
	public FunctionMultiSelectField() {
		this(null);
	}

	/**
	 * コンストラクタフィールドID。
	 * @param id フィールドID。
	 */
	public FunctionMultiSelectField(final String id) {
		super(id);
		this.setComment(COMMENT);
		this.setHtmlFieldType(HtmlFieldType.CHECKBOX);
	}


	@Override
	public void init() throws Exception {
		super.init();
		String lang = WebComponent.getFunctionMap().getLang(this.getPage());
		List<Map<String, Object>> options = new ArrayList<Map<String, Object>>();
		for (Menu m: WebComponent.getFunctionMap().getMenuList()) {
			SelectField.OptionEntity e = new SelectField.OptionEntity();
			e.setValue(m.getPath());
			e.setName(m.getName(lang));
			options.add(e.getMap());
		}
		this.setOptionList(options);
	}
}
