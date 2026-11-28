/**
 * @fileOverview {@link NumberUtil}クラスを記述したファイルです。
 */

type NumericString = string | number;

/**
 * @class NumberUtil
 *
 * 数値整形ユーティリティ。
 */
export class NumberUtil {
	/**
	 * 3桁ごとに','を付けます。
	 * @param v 数値文字列
	 * @returns 整形後の文字列
	 */
	public static addComma(v: NumericString): string {
		if (v == null) {
			return "";
		} else {
			// カンマとスペースを除去（入力ミスを考慮）
			let value = NumberUtil.delComma(v);

			// カンマ区切り
			if (isNaN(Number(value))) {
				// 数値以外の場合は元の値を文字列として返す
				value = String(v);
			} else {
				while (value !== (value = value.replace(/^([+-]?\d+)(\d{3})/, '$1,$2')));
			}

			return value;
		}
	}

	/**
	 * ','を削除します。
	 * @param v 数値文字列
	 * @returns 整形後の文字列
	 */
	public static delComma(v: NumericString): string {
		// 正規表現で扱うために文字列に変換
		const value = String(v);

		// スペースとカンマを削除
		return value.replace(/^\s+|\s+$|,/g, '');
	}

	/**
	 * 数値文字列を数値に変換します。
	 * 数値に','が含まれていても変換します。
	 * @param v 数値文字列
	 * @returns 数値
	 */
	public static parse(v: NumericString): number {
		return Number(NumberUtil.delComma(v));
	}
}
