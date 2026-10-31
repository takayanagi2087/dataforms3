package jp.dataforms.fw.devtool.field;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jp.dataforms.fw.controller.WebComponent;
import jp.dataforms.fw.field.common.SelectField;
import jp.dataforms.fw.field.common.VarcharSingleSelectField;

/**
 * フィールドクラス選択フィールドクラス。
 *
 */
public class FieldClassSelectField extends VarcharSingleSelectField {
	/**
	 * 項目長。
	 */
	private static final int LENGTH = 1024;

	/**
	 * コンストラクタ。
	 */
	public FieldClassSelectField() {
		this(null);
	}

	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public FieldClassSelectField(final String id) {
		super(id, LENGTH);
	}

	/**
	 * 選択肢。
	 */
	private static final String[][] OPTIONS = {
		{"1", " - ", null} // 単純に出力する。
		// 集計フィールドクラス
		, {WebComponent.BASE_PACKAGE + ".field.sqlfunc.ExtractField", "日時要素抽出(ExtractField)", null}
		, {WebComponent.BASE_PACKAGE + ".field.sqlfunc.DateAddField", "日付加算(DateAddField)", null}
		, {WebComponent.BASE_PACKAGE + ".field.sqlfunc.TimestampAddField", "日時加算(TimestampAddField)", null}
		, {WebComponent.BASE_PACKAGE + ".field.sqlfunc.CaseField", "条件分岐(CaseField)", null}
		, {WebComponent.BASE_PACKAGE + ".field.sqlfunc.SqlField", "SQL式(SqlField)", null}
		// 集計フィールドクラス
		, {WebComponent.BASE_PACKAGE + ".field.sqlfunc.CountField", "カウント(CountField)", "summary"}
		, {WebComponent.BASE_PACKAGE + ".field.sqlfunc.AvgField", "平均値(AvgField)", "summary"}
		, {WebComponent.BASE_PACKAGE + ".field.sqlfunc.MaxField", "最大値(MaxField)", "summary"}
		, {WebComponent.BASE_PACKAGE + ".field.sqlfunc.MinField", "最小値(MinField)", "summary"}
		, {WebComponent.BASE_PACKAGE + ".field.sqlfunc.SumField", "合計値(SumField)", "summary"}
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
			if (opt[2] != null) {
				e.getMap().put("summary", true);
			} else {
				e.getMap().put("summary", false);
			}
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
