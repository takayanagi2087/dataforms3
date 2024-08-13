package jp.dataforms.fw.field.common;

import java.util.List;
import java.util.Map;

/**
 * 選択オプションを持つフィールド。
 * @param <TYPE> フィールドのデータ型。
 */
public interface OptionField<TYPE> {

	/**
	 * 選択肢のリストを取得します。
	 * @return 選択肢のリスト。
	 */
	List<Map<String, Object>> getOptionList();

	
	/**
	 * オプションの値から名称を取得します。
	 * @param value 値。
	 * @return 名称。
	 */
	default String getOptionName(final TYPE value) {
		String ret = null;
		if (this.getOptionList() != null) {
			if (value != null) {
				for (Map<String, Object> m: this.getOptionList()) {
					SelectField.OptionEntity e = new SelectField.OptionEntity(m);
					if (value.equals(e.getValue())) {
						ret = e.getName();
					}
				}
			}
		}
		return ret;
	}

	/**
	 * オプションの名前から値を取得します。
	 * @param name 名前。
	 * @return 名称。
	 */
	@SuppressWarnings("unchecked")
	default TYPE getOptionValue(final String name) {
		TYPE ret = null;
		if (this.getOptionList() != null) {
			if (name != null) {
				for (Map<String, Object> m: this.getOptionList()) {
					SelectField.OptionEntity e = new SelectField.OptionEntity(m);
					if (name.equals(e.getName())) {
						ret = (TYPE) e.getValue();
					}
				}
			}
		}
		return ret;
	}
}
