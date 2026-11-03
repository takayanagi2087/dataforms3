package jp.dataforms.fw.devtool.field;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jp.dataforms.fw.field.common.SingleSelectField;

/**
 * SqlFieldTypeフィールド。
 */
public class SqlFieldTypeField extends SingleSelectField<String> {
	/**
	 * クラスリスト。
	 */
	private static final String[] LIST = {
		"SqlField"
		, "CaseField"
	};
	
	
	/**
	 * コンストラクタ。
	 */
	public SqlFieldTypeField() {
		this(null);
	}

	/**
	 * コンストラクタ。
	 * @param id　フィールドID。
	 */
	public SqlFieldTypeField(String id) {
		super(id);
	}

	
	@Override
	public void init() throws Exception {
		super.init();
		List<Map<String, Object>> optlist = new ArrayList<Map<String, Object>>();
		for (String opt: LIST) {
			OptionEntity oe = new OptionEntity();
			oe.setValue(opt);
			oe.setName(opt);
			optlist.add(oe.getMap());
		}
		this.setOptionList(optlist);
	}
}
