package jp.dataforms.fw.devtool.field;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jp.dataforms.fw.field.common.SelectField;
import jp.dataforms.fw.field.common.VarcharSingleSelectField;

/**
 * 集計フィールド選択フィールドクラス。
 *
 */
public class SummerySelectField extends VarcharSingleSelectField {
	/**
	 * 項目長。
	 */
	private static final int LENGTH = 1024;

	/**
	 * コンストラクタ。
	 */
	public SummerySelectField() {
		this(null);
	}

	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public SummerySelectField(final String id) {
		super(id, LENGTH);
	}

	/**
	 * 選択肢。
	 */
	private static final String[][] OPTIONS = {
		{"0", ""}
		, {"1", "&#10004;"}
		, {"dataforms.field.sqlfunc.CountField", "CountField"}
		, {"dataforms.field.sqlfunc.AvgField", "AvgField"}
		, {"dataforms.field.sqlfunc.MaxField", "MaxField"}
		, {"dataforms.field.sqlfunc.MinField", "MinField"}
		, {"dataforms.field.sqlfunc.SumField", "SumField"}
	};

	/**
	 * 選択肢リストを取得ます。
	 * @return 選択肢リスト。
	 */
	private List<Map<String, Object>> queryOptionList() {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for (String[] opt: OPTIONS) {
			SelectField.OptionEntity e = new SelectField.OptionEntity();
			e.setValue(opt[0]);
			e.setName(opt[1]);
			list.add(e.getMap());
		}
		return list;
	}

	@Override
	public void init() throws Exception {
		super.init();
		this.setOptionList(this.queryOptionList());
	}

}
