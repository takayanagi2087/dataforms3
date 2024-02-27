package jp.dataforms.fw.field.common;

import jp.dataforms.fw.field.sqltype.BigintField;

/**
 * レコードIDフィールドクラス。
 * <pre>
 * テーブルのレコードIDとなるフィールドです。
 * このクラスから派生したフィールドをテーブルの先頭の主キーとし
 * autoIncrementIdがtrueの場合、Dao.executeInsertでレコードIDを
 * 自動生成します。
 * </pre>
 */
public class RecordIdField extends BigintField {
	/**
	 * フィールドコメント。
	 */
	private static final String COMMENT = "レコードID";

	/**
	 * コンストラクタ。
	 */
	public RecordIdField() {
		super(null);
		this.setHidden(true);
		this.setComment(COMMENT);
	}

	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public RecordIdField(final String id) {
		super(id);
		this.setHidden(true);
		this.setComment(COMMENT);
	}

	/**
	 * 検索条件に使用しない。
	 */
	@Override
	public jp.dataforms.fw.field.base.Field.MatchType getDefaultMatchType() {
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
