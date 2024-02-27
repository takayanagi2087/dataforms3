package jp.dataforms.fw.dao.condition;

import jp.dataforms.fw.field.base.Field;

/**
 * フィールド条件のクラス。
 *
 */
public class FieldConditionExpression implements ConditionExpression {
	/**
	 * 条件フィールド。
	 */
	private Field<?> field = null;

	/**
	 * コンストラクタ。
	 * @param field フィールド。
	 */
	public FieldConditionExpression(final Field<?> field) {
		this.field = field;
	}

	/**
	 * フィールドを取得します。
	 * @return フィールド。
	 */
	public Field<?> getField() {
		return field;
	}


}
