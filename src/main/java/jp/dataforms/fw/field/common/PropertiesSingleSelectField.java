package jp.dataforms.fw.field.common;

import java.util.List;
import java.util.Map;

import jp.dataforms.fw.dao.sqldatatype.SqlVarchar;
import jp.dataforms.fw.util.MessagesUtil;

/**
 * *.propertiesの内容を選択するフィールド。
 *
 */
public class PropertiesSingleSelectField extends SingleSelectField<String> implements SqlVarchar {

	/**
	 * propertiesのキー。
	 */
	private String key = null;

	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 * @param length 項目長。
	 * @param key リソースのkey。
	 */
	public PropertiesSingleSelectField(final String id, final int length, final String key) {
		super(id, length);
		this.key = key;
	}


	@Override
	public void init() throws Exception {
		super.init();
		List<Map<String, Object>> options = MessagesUtil.getSelectFieldOption(this.getPage(), this.key);
		this.setOptionList(options);
	}

	@Override
	public String getLengthParameterPattern() throws Exception {
		return "^[0-9]+$";
	}


	@Override
	public String getLengthParameter() throws Exception {
		return Integer.toString(this.getLength());
	}
}
