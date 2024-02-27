package jp.dataforms.fw.devtool.field;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jp.dataforms.fw.field.common.SelectField;
import jp.dataforms.fw.field.common.VarcharSingleSelectField;

/**
 * テーブル結合種別選択フィールド。
 *
 */
public class JoinTypeField extends VarcharSingleSelectField {
	/**
	 * INNER_JJOIN。
	 */
	public static final String INNER_JOIN = "0";
	/**
	 * LEFT JOIN。
	 */
	public static final String LEFT_JOIN = "1";
	/**
	 *  RIGHT JOIN。
	 */
	public static final String RIGHT_JOIN = "2";

	/**
	 * コンストラクタ。
	 */
	public JoinTypeField() {
		this(null);
	}

	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public JoinTypeField(final String id) {
		super(id, 32);
		this.setComment("テーブル結合種別");
	}

	/**
	 * 選択肢。
	 */
	private static final String[][] OPTIONS = {
		{INNER_JOIN, "inner join"}
		, {LEFT_JOIN, "left join"}
		, {RIGHT_JOIN, "right join"}
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
