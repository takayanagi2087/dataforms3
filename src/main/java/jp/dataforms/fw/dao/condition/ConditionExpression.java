package jp.dataforms.fw.dao.condition;

import jp.dataforms.fw.field.base.FieldList;

/**
 * 条件のインターフェース。
 */
public interface ConditionExpression {

	/**
	 * 条件式に含まれるフィールドのリストを取り出します。
	 * @param flist 出力するフィールドリスト。
	 * @param ce 条件式。
	 */
	default void extractFieldList(final FieldList flist, final ConditionExpression ce) {
		if (ce instanceof ConditionExpressionList) {
			ConditionExpressionList list = (ConditionExpressionList) ce;
			for (ConditionExpression e: list) {
				this.extractFieldList(flist, e);
			}

		} else {
			FieldConditionExpression fce = (FieldConditionExpression) ce;
			flist.add(fce.getField());
		}
	}

	/**
	 * 条件式に含まれるフィールドのリストを取得します。
	 * @return 条件式に含まれるフィールドのリスト。
	 */
	default FieldList getFieldList() {
		FieldList flist = new FieldList();
		this.extractFieldList(flist, this);
		return flist;
	}

}
