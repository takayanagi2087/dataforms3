package jp.dataforms.fw.field.sqlfunc;

import jp.dataforms.fw.field.base.Field;
import jp.dataforms.fw.field.sqltype.BigintField;

/**
 * カウントフィールドクラス。
 * <pre>
 * QueryのfieldListにnew CountField("yyy", xxxField)を追加すると、以下のようなsqlを作成します。
 * select count(m.xxx) as yyy ...  from main_table m  ...　group by ...
 * </pre>
 */
public class CountField extends GroupSummaryField<Long> {

	/**
	 * DISTINCTフラグ。
	 */
	private boolean distinct = false;

	/**
	 * コンストラクタ.
	 * @param id フィールドID.
	 * @param field 集計対象フィールド.
	 */
	public CountField(final String id, final Field<?> field) {
		this(id, field, false);
	}

	/**
	 * CountFieldの結果はBigintFieldをターゲットにする。
	 * @param field 元のフィールドクラス。
	 * @return BigintFieldのインスタンス。
	 */
	private static BigintField getBigintField(final Field<?> field) {
		BigintField f = new BigintField(field.getId());
		f.setTable(field.getTable());
		f.setComment(field.getComment());
		return f;
	}

	/**
	 * コンストラクタ.
	 * @param id フィールドID.
	 * @param distinct DISTINCTフラグ.
	 * @param field 集計対象フィールド.
	 */
	public CountField(final String id, final Field<?> field, final boolean distinct) {
		super(id, CountField.getBigintField(field));
		this.distinct = distinct;
	}

	/**
	 * DISTINCTフラグを取得します。
	 * @return DISTINCTフラグ。
	 */
	public boolean isDistinct() {
		return distinct;
	}

	/**
	 * DISTINCTフラグを設定します。
	 * @param distinct DISTINCTフラグ。
	 */
	public void setDistinct(final boolean distinct) {
		this.distinct = distinct;
	}

	@Override
	public String getFunctionName() {
		return "count";
	}

	@Override
	public Field<?> cloneForSubQuery() {
		Field<?> ret = new BigintField(this.getId());
		ret.setTable(this.getTargetField().getTable());
		ret.setComment(this.getTargetField().getComment());
		return ret;
	}
}
