package jp.dataforms.fw.field.sqlfunc;

/**
 * 日時加算フィールド。
 */
public interface DateTimeAddFunctionField extends DateFunctionField {
	/**
	 * 加算する値を取得します。
	 * @return 加算する値。
	 */
	Integer getAddValue();
}
