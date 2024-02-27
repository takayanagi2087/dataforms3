package jp.dataforms.fw.devtool.webres.page;

import jp.dataforms.fw.controller.WebComponent;
import jp.dataforms.fw.field.base.Field;
import jp.dataforms.fw.field.sqltype.DateField;

/**
 * 範囲条件のフィールドクラス。
 */
public class RangeFieldPair extends WebComponent {
	/**
	 * 範囲開始フィールド。
	 */
	private Field<?> from = null;

	/**
	 * 範囲終了フィールド。
	 */
	private Field<?> to = null;

	/**
	 * コンストラクタ。
	 */
	public RangeFieldPair() {
	}

	/**
	 * 開始フィールドを取得します。
	 * @return 開始フィールド。
	 */
	public Field<?> getFrom() {
		return this.from;
	}

	/**
	 * 開始フィールドを取得します。
	 * @param from 開始フィールドを設定します。
	 */
	public void setFrom(final Field<?> from) {
		this.from = from;
	}

	/**
	 * 終了フィールドを取得します。
	 * @return 終了フィールド。
	 */
	public Field<?> getTo() {
		return to;
	}

	/**
	 * 終了フィールドを取得します。
	 * @param to 終了フィールドを設定します。
	 */
	public void setTo(final Field<?> to) {
		this.to = to;
	}

	/**
	 * フィールドコメントを取得します。
	 * @return フィールドコメント。
	 */
	public String getComment() {
		String ret = this.from.getComment();
		return ret.replaceAll("\\(from\\)$", "");
	}

	/**
	 * 範囲フィールドのcssクラスを取得します。
	 * @return 範囲フィールドのcssクラス。
	 *
	 */
	public String getRangeFieldClass() {
		if (this.from instanceof DateField) {
			return "dateRangeField";
		} else {
			return "rangeField";
		}
	}
}
