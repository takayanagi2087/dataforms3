/**
 * @fileOverview  {@link NumberUtil}クラスを記述したファイルです。
 */
'use strict';
/**
 * @class NumberUtil
 *
 * 数値整形ユーティリティ。
 * <pre>
 * </pre>
 */
class NumberUtil {

	/**
	 * 3桁ごとに','を付けます。
	 * @param {String} v 数値文字列。
	 * @returns {String} 整形後の文字列。
	 */
	static addComma(v) {
		// カンマとスペースを除去（入力ミスを考慮）
		let value = this.delComma(v);
		// カンマ区切り
		if (isNaN(Number(value))) {
			// 数値以外の場合はそのまま
			value = v;
		} else {
			while (value != (value = value.replace(/^([+-]?\d+)(\d{3})/, "$1,$2")));
		}
		return value;
	}


	/**
	 * ','を削除します。
	 * @param {String} v 数値文字列。
	 * @return {String} 整形後の文字列。
	 */
	static delComma(v) {
		// 正規表現で扱うために文字列に変換
		let value = "" + v;
		// スペースとカンマを削除
		return value.replace(/^\s+|\s+$|,/g, "");
	}


	/**
	 * 数値文字列を数値に変換します.
	 * <pre>
	 * 数値に','が含まれていても変換します。
	 * </pre>
	 * @param v 数値文字列.
	 * @returns 数値.
	 */
	static parse(v) {
		return Number(NumberUtil.delComma(v));
	}

}


