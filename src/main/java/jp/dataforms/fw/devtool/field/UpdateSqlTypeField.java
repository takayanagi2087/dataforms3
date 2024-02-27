package jp.dataforms.fw.devtool.field;

import jp.dataforms.fw.field.common.PropertiesSingleSelectField;

/**
 * 更新SQLタイプフィールドクラス。
 *
 */
public class UpdateSqlTypeField extends PropertiesSingleSelectField {
	/**
	 * 更新SQLタイプ。
	 */
	public enum UpdateSqlType {
		/**
		 * insert文。
		 */
		INSERT("0")
		/**
		 * update文。
		 */
		, UPDATE("1")
		/**
		 * delete文。
		 */
		, DELETE("2");

		/**
		 * テキスト値。
		 */
		private String text = null;

		/**
		 * コンストラクタ。
		 * @param text テキスト値。
		 */
		private UpdateSqlType(final String text) {
			this.text = text;
		}

		/**
		 * 文字列値を取得します。
		 * @return 文字列値。
		 */
		public String getString() {
			return this.text;
		}

	}

	/**
	 * コンストラクタ。
	 */
	public UpdateSqlTypeField() {
		this(null);
	}

	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public UpdateSqlTypeField(final String id) {
		super(id, 16, "updatesqltype");
		this.setHtmlFieldType(HtmlFieldType.RADIO);
	}
}
