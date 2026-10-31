package jp.dataforms.fw.devtool.field;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jp.dataforms.fw.field.common.SingleSelectField;
import jp.dataforms.fw.field.sqlfunc.DateFunctionField;

/**
 * 日時フィール選択フィールド。
 */
public class DataFieldTypeSelectField extends SingleSelectField<String> {
	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public DataFieldTypeSelectField(final String id) {
		super(id);
	}

	/**
	 * 日時のフィールド。
	 */
	private static final String [] OPTLIST = {
		DateFunctionField.FieldType.YEAR.name()
		, DateFunctionField.FieldType.MONTH.name()
		, DateFunctionField.FieldType.DAY.name()
		, DateFunctionField.FieldType.HOUR.name()
		, DateFunctionField.FieldType.MINUTE.name()
		, DateFunctionField.FieldType.SECOND.name()
	};
	
	
	@Override
	public void init() throws Exception {
		super.init();
		List<Map<String, Object>> optlist = new ArrayList<Map<String, Object>>();
		for (String fld: DataFieldTypeSelectField.OPTLIST) {
			OptionEntity e = new OptionEntity();
			e.setValue(fld);
			e.setName(fld);
			optlist.add(e.getMap());
		}
		this.setOptionList(optlist);
	}
}
