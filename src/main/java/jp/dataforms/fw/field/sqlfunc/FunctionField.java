package jp.dataforms.fw.field.sqlfunc;

import jp.dataforms.fw.field.base.Field;

/**
 * SQLの各種関数フィールド。
 */
public interface FunctionField {
	/**
	 * フォーム用フィールドを取得します。
	 * @return フォーム用フィールド。
	 */
	Field<?> getFormField();
}
