package jp.dataforms.fw.field.common;

import jp.dataforms.fw.field.sqltype.TimestampField;

/**
 * レコード作成日時フィールドクラス。
 * <pre>
 * DBテーブル中のレコードの作成日時のフィールドです。
 * </pre>
 */
public class CreateTimestampField extends TimestampField implements DoNotUpdateField {
	/**
	 * フィールドコメント。
	 */
	private static final String COMMENT = "作成日時";
	/**
	 * コンストラクタ。
	 */
	public CreateTimestampField() {
		super(null);
		this.getValidatorList().clear();
		this.setDateFormat("format.updatetimestampfield");
		this.setComment(COMMENT);
		this.setHidden(true);
	}
	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public CreateTimestampField(final String id) {
		super(id);
		this.getValidatorList().clear();
		this.setDateFormat("format.updatetimestampfield");
		this.setComment(COMMENT);
		this.setHidden(true);
	}

	/**
	 * 検索条件にしない。
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
