package jp.dataforms.fw.field.sqlfunc;

import java.util.List;

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
	

}
