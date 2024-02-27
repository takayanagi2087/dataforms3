package jp.dataforms.fw.devtool.field;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.dataforms.fw.field.base.Field.MatchType;
import jp.dataforms.fw.field.common.SelectField;
import jp.dataforms.fw.field.common.SingleSelectField;
import jp.dataforms.fw.util.MessagesUtil;
import jp.dataforms.fw.util.StringUtil;

/**
 * マッチタイプ選択フィールドクラス。
 */
public class MatchTypeSelectField extends SingleSelectField<MatchType> {
	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public MatchTypeSelectField(final String id) {
		super(id);
	}

	/**
	 * MatchTypeのリストを取得する。
	 * @return MatchTypeのリスト。
	 */
	private List<Map<String, Object>> getMatchTypeList() {
		List<Map<String, Object>> ret = new ArrayList<Map<String, Object>>();
		for (MatchType mt: MatchType.values()) {
			String key = "matchtype." + mt.toString();
			String name = MessagesUtil.getMessage(getWebEntryPoint(), key);
			if (!StringUtil.isBlank(name)) {
				Map<String, Object> opt = new HashMap<String, Object>();
				opt.put(SelectField.OptionEntity.ID_VALUE, mt.toString());
				opt.put(SelectField.OptionEntity.ID_NAME, name);
				ret.add(opt);
			}
		}
		return ret;
	}

	@Override
	public void init() throws Exception {
		super.init();
		List<Map<String, Object>> options = this.getMatchTypeList();
		this.setOptionList(options);
	}

}
