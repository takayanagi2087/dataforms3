package jp.dataforms.fw.devtool.field;

import java.util.Map;

import jp.dataforms.fw.field.common.VarcharSingleSelectField;

/**
 * フィールド選択フィールド。
 *
 */
public class FieldSingleSelectField extends VarcharSingleSelectField {
	/**
	 * 自動オプション取得。
	 */
	private Boolean autoOption = true;
	
	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public FieldSingleSelectField(final String id) {
		this(id, true);
	}

	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 * @param autoOption 自動オプション取得。
	 */
	public FieldSingleSelectField(final String id, final Boolean autoOption) {
		super(id, 128);
		this.setComment("フィールドID");
		this.autoOption = autoOption;
	}
	
	
	@Override
	public Map<String, Object> getProperties() throws Exception {
		Map<String, Object> ret = super.getProperties();
		ret.put("autoOption", this.autoOption);
		return ret;
	}
}
