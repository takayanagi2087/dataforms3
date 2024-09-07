package jp.dataforms.fw.field.sqlfunc;

import jp.dataforms.fw.field.base.Field;

/**
 * 集計関数フィールド基本クラス。
 * <pre>
 * Sum,Max,Min.Avg,Count等の集計関数フィールドの基本クラスです。
 * </pre>
 * @param <TYPE> データ型。
 */
public abstract class GroupSummaryField<TYPE> extends Field<TYPE>  implements FunctionField {
    /**
     * Logger.
     */
//    private static Logger log = Logger.getLogger(GroupSummaryField.class.getName());

	/**
	 * 対象フィールド。
	 */
	private Field<?> targetField = null;

	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 * @param field フィールド。
	 */
	public GroupSummaryField(final String id, final Field<?> field) {
		super(id);
		this.targetField = field;
//		log.debug("GroupSummaryField.table=" + field.getTable());
		this.setTable(field.getTable());
	}

	/**
	 * 対象フィールドを取得します。
	 * @return 対象フィールド。
	 */
	public Field<?> getTargetField() {
		return targetField;
	}

	@Override
	public Field<?> getFormField() {
		Field<?> field = this.getTargetField();
		field.setId(this.getId());
		field.setRealId(this.getRealId());
		return field;
	}
	
	
	/**
	 * 対象フィールドを設定します。
	 * @param targetField 対象フィールド。
	 * @return 設定したフィールド。
	 */
	public GroupSummaryField<TYPE> setTargetField(final Field<?> targetField) {
		this.targetField = targetField;
		return this;
	}

	@Override
	public void setClientValue(final Object v) {
		// 何もしない.
	}

	/**
	 * 集計関数名を取得します。
	 * @return 集計関数名。
	 */
	public abstract String getFunctionName();


	@Override
	public Field<?> cloneForSubQuery() {
		Field<?> sf = this.getTargetField().clone();
		sf.setId(this.getId());
		return sf;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @return MatchType.NONEを返します。
	 */
	@Override
	public MatchType getDefaultMatchType() {
		return MatchType.NONE;
	}

	/**
	 * 検索結果フォームは表示のみ。
	 */
	@Override
	public Display getQueryResultFormDefaultDisplay() {
		return Display.SPAN;
	}


	/**
	 * 編集フォームはは表示のみ。
	 */
	@Override
	public Display getEditFormDefaultDisplay() {
		return Display.SPAN;
	}

	@Override
	public Field<?> setComment(final String comment) {
		Field<?> field = this.getTargetField();
		field.setComment(comment);
		return super.setComment(comment);
	}
}
