/**
 * @fileOverview  {@link QueryStringUtil}クラスを記述したファイルです。
 */

'use strict';

/**
 * @class QueryStringUtil
 * 問合せ文字列ユーティリティ。
 * <pre>
 * 問合せ文字列操作ユーティリティクラス。
 * </pre>
 */
class QueryStringUtil {

	/**
	 * 問合せ文字列を解析し、Objectに変換します。
	 * @param {String} s 問合せ文字列。
	 * @returns {Object} 変換されたObject。
	 */
	static parse(qs) {
		let vars = {};
		if (qs != null && qs.length > 0) {
			let sidx = 0;
			if (qs.charAt(0) == "?") {
				sidx = 1;
			}
			let temp = qs.substring(sidx).split('&');
			for(let i = 0; i <temp.length; i++) {
				let params = temp[i].split('=');
				if (params[1] != null) {
					let v = params[1].replace(/\+/g, "%20");
					vars[params[0]] = decodeURIComponent(v);
				} else {
					let v = params[1];
					vars[params[0]] = decodeURIComponent(v);
				}
			}
		}
		return vars;
	}
}


