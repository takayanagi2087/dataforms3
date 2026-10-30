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
		{"1", "参照"} // 単純に出力する。
		// 集計フィールドクラス
		, {WebComponent.BASE_PACKAGE + ".field.sqlfunc.ExtractField", "日時要素抽出(ExtractField)"}
		, {WebComponent.BASE_PACKAGE + ".field.sqlfunc.DateAddField", "日付加算(DateAddField)"}
		, {WebComponent.BASE_PACKAGE + ".field.sqlfunc.TimestampAddField", "日時加算(TimestampAddField)"}
		, {WebComponent.BASE_PACKAGE + ".field.sqlfunc.CaseField", "条件分岐(CaseField)"}
		, {WebComponent.BASE_PACKAGE + ".field.sqlfunc.SqlField", "SQL式(SqlField)"}
		// 集計フィールドクラス
		, {WebComponent.BASE_PACKAGE + ".field.sqlfunc.CountField", "カウント(CountField)"}
		, {WebComponent.BASE_PACKAGE + ".field.sqlfunc.AvgField", "平均値(AvgField)"}
		, {WebComponent.BASE_PACKAGE + ".field.sqlfunc.MaxField", "最大値(MaxField)"}
		, {WebComponent.BASE_PACKAGE + ".field.sqlfunc.MinField", "最小値(MinField)"}
		, {WebComponent.BASE_PACKAGE + ".field.sqlfunc.SumField", "合計値(SumField)"}
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
