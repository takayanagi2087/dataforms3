package jp.dataforms.fw.field.common;

import jp.dataforms.fw.field.sqltype.BigintField;

/**
 * レコード作成者フィールドクラス。
 * <pre>
 * レコード作成したユーザIDを記録するフィールド。
 * </pre>
 */
public class CreateUserIdField extends BigintField implements DoNotUpdateField {
	/**
	 * フィールドコメント。
	 */
	private static final String COMMENT = "作成者ID";

	/**
	 * コンストラクタ。
	 */
	public CreateUserIdField() {
		super(null);
		this.setComment(COMMENT);
		this.setHidden(true);
	}

	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public CreateUserIdField(final String id) {
		super(id);
		this.setComment(COMMENT);
		this.setHidden(true);
	}

	/**
	 * 検索条件には使用しない。
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
