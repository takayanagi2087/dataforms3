package jp.dataforms.fw.field.common;

import jp.dataforms.fw.field.sqltype.BigintField;

/**
 * 更新者IDフィールドクラス。
 */
public class UpdateUserIdField extends BigintField {
	/**
	 * フィールドコメント。
	 */
	private static final String COMMENT = "更新者ID";

	/**
	 * コンストラクタ。
	 */
	public UpdateUserIdField() {
		super(null);
		this.setComment(COMMENT);
		this.setHidden(true);
	}

	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public UpdateUserIdField(final String id) {
		super(id);
		this.setComment(COMMENT);
		this.setHidden(true);
	}

	/**
	 * 検索条件に使用しない。
	 */
	@Override
	public MatchType getDefaultMatchType() {
		return MatchType.NONE;
	}

	/**
	 * 検索結果フォームはhidden出力。
	 */
	@Override
	public Display getQueryResultFormDefaultDisplay() {
		return Display.INPUT_HIDDEN;
	}


	/**
	 * 編集フォームはhidden出力。
	 */
	@Override
	public Display getEditFormDefaultDisplay() {
		return Display.INPUT_HIDDEN;
	}
}
