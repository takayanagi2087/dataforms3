package jp.dataforms.fw.field.common;

import jp.dataforms.fw.field.sqltype.TimestampField;

/**
 * レコード更新日時フィールドクラス。
 * <pre>
 * DBテーブル中のレコードの更新日時のフィールドです。
 * 楽観ロックチェックの時参照します。
 * </pre>
 */
public class UpdateTimestampField extends TimestampField {
	/**
	 * フィールドコメント。
	 */
	private static final String COMMENT = "更新日時";

	/**
	 * コンストラクタ.
	 */
	public UpdateTimestampField() {
		super(null);
		this.getValidatorList().clear();
		this.setDateFormat("format.updatetimestampfield");
		this.setComment(COMMENT);
		this.setHidden(true);
	}

	/**
	 * コンストラクタ.
	 * @param id フィールドID.
	 */
	public UpdateTimestampField(final String id) {
		super(id);
		this.getValidatorList().clear();
		this.setDateFormat("format.updatetimestampfield");
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
