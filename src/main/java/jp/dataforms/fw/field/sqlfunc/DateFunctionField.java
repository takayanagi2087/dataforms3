package jp.dataforms.fw.field.sqlfunc;

/**
 * 日付関数フィールドインターフェース。。
 */
public interface DateFunctionField extends FunctionField {
	/**
	 * 抽出するフィールドの種類。
	 */
	public enum FieldType {
		/**
		 * 年。
		 */
		YEAR
		/**
		 * 月。
		 */
		, MONTH
		/**
		 * 日。
		 */
		, DAY
		/**
		 * 時。
		 */
		, HOUR
		/**
		 * 分。
		 */
		, MINUTE
		/**
		 * 秒。
		 */
		, SECOND
	}
}
