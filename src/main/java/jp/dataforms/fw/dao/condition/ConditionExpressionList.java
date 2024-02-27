package jp.dataforms.fw.dao.condition;

import java.util.ArrayList;

import jp.dataforms.fw.field.base.Field;
import jp.dataforms.fw.field.base.FieldList;

/**
 * 条件リストクラス。
 */
public class ConditionExpressionList extends ArrayList<ConditionExpression> implements ConditionExpression {
	/**
	 * UID.
	 */
	private static final long serialVersionUID = 6889716379641016441L;

	/**
	 * 結合演算子。
	 *
	 */
	public enum Operator {
		/**
		 * AND.
		 */
		AND,
		/**
		 * OR.
		 */
		OR
	}

	/**
	 * 結合演算子。
	 */
	private Operator operator = null;

	/**
	 * コンストラクタ。
	 * @param ope 結合演算子。
	 */
	public ConditionExpressionList(final Operator ope) {
		this.operator = ope;
	}

	/**
	 * コンストラクタ。
	 * @param ope 結合演算子。
	 * @param flist フィールドリスト。
	 */
	public ConditionExpressionList(final Operator ope, final FieldList flist) {
		this.operator = ope;
		this.addFieldList(flist);
	}

	/**
	 * フィールドを追加します。
	 * @param field フィールド。
	 * @return 追加したフィールド。
	 */
	public Field<?> addField(final Field<?> field) {
		this.add(new FieldConditionExpression(field));
		return field;
	}

	/**
	 * フィールドリストを追加します。
	 * @param flist フィールドリスト。
	 */
	public void addFieldList(final FieldList flist) {
		for (Field<?> f: flist) {
			this.add(new FieldConditionExpression(f));
		}
	}

	/**
	 * 結合演算子を取得します。
	 * @return 結合演算子。
	 */
	public Operator getOperator() {
		return operator;
	}



}
