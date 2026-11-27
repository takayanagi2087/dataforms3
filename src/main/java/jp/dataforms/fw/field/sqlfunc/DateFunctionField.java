package jp.dataforms.fw.field.sqlfunc;

/**
 * 日付関数フィールドインターフェース。。
 */
public interface DateFunctionField extends FunctionField {
	/**
	 * 抽出するフィールドの種類。
	 */
	enum FieldType {
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
	
	/**
	 * フィールドタイプを取得します。
	 * @return フィールドタイプ。
	 */
	FieldType getType();
	
	/**
	 * フィールドタイプの名称を取得します。
	 * @return フィールドタイプの名称。
	 */
	default String getTypeName() {
		return this.getType().name().toLowerCase();
	}
}
