package jp.dataforms.fw.field.sqlfunc;

import java.util.List;
import java.util.Map;

import jp.dataforms.fw.dao.sqlgen.SqlGenerator;
import jp.dataforms.fw.field.base.Field;
import jp.dataforms.fw.validator.FieldValidator;

/**
 * SQLの各種関数フィールド。
 */
public interface FunctionField {
	/**
	 * 対象フィールドを取得します。
	 * @return 対象フィールド。
	 */
	Field<?> getTargetField();
	
	/**
	 * IDを取得します。
	 * @return ID.
	 */
	String getId();

	/**
	 * ページ中でユニークな実際のIDを取得します。
	 * @return ページ中でユニークなID。
	 */
	String getRealId();

	/**
	 * バリデータリストを取得します。
	 * @return バリテータリスト。
	 */
	List<FieldValidator> getValidatorList();
	
	
	/**
	 * バリデータリストを設定する。
	 * @param validatorList バリデータリスト。
	 */
	void setValidatorList(final List<FieldValidator> validatorList);

	
	/**
	 * フォーム用フィールドを取得します。
	 * @return フォーム用フィールド。
	 */
	default Field<?> getFormField() {
		Field<?> field = this.getTargetField();
		field.setId(this.getId());
		field.setRealId(this.getRealId());
		field.setValidatorList(this.getValidatorList());
		return field;
	}
	
	/**
	 * 関数SQLを作成します。
	 * @param gen SQL Generator。
	 * @return 関数SQL.
	 */
	String getFunctionSql(final SqlGenerator gen);
	

	/**
	 * フィールドオプションを取得します。
	 * @return フィールドオプション。
	 */
	Map<String, Object> getOption();
	
	
	/**
	 * フィールドオプションテキストを取得します。
	 * @return フィールドオプションテキスト。
	 */
	default String getOptionText() {
		String ret = null;
		Map<String, Object> map = this.getOption();
		if (map != null) {
			String sqlFieldType = (String) map.get("sqlFieldType");
			if (sqlFieldType != null) {
				String fieldClassName = (String) map.get("fieldClassName");
				ret = fieldClassName + " -> " + sqlFieldType;
			} else {
				ret = (String) map.get("dateFieldType");
				Integer addValue = (Integer) map.get("addValue");
				if (addValue != null) {
					ret = ret + " + " + addValue;
				}

			}
		}
		return ret;
	}
}
