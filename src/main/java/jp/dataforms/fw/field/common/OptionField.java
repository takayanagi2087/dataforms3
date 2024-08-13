package jp.dataforms.fw.field.common;

import java.util.List;
import java.util.Map;

/**
 * 選択オプションを持つフィールド。
 * @param <T> フィールドのデータ型。
 */
public interface OptionField<T> {

	/**
	 * 選択肢のリストを取得します。
	 * @return 選択肢のリスト。
	 */
	List<Map<String, Object>> getOptionList();

	/**
	 * 初期化処理を行います。
	 * @throws Exception 例外。
	 */
	void init() throws Exception;
	
	/**
	 * オプションの値から名称を取得します。
	 * @param value 値。
	 * @return 名称。
	 */
	default String getOptionText(final Object value) {
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
	 * オプションの値から名称を取得します。
	 * @param value 値。
	 * @return 名称。
	 */
	default String getOptionName(final T value) {
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
	default T getOptionValue(final String name) {
		T ret = null;
		if (this.getOptionList() != null) {
			if (name != null) {
				for (Map<String, Object> m: this.getOptionList()) {
					SelectField.OptionEntity e = new SelectField.OptionEntity(m);
					if (name.equals(e.getName())) {
						ret = (T) e.getValue();
					}
				}
			}
		}
		return ret;
	}
}
