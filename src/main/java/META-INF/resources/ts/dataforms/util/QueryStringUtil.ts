/**
 * @fileOverview QueryStringUtilクラスを記述したファイルです。
 */

/**
 * QueryStringユーティリティ。
　* QueryString操作ユーティリティクラス。
 */
export class QueryStringUtil {
	/**
	 * 問合せ文字列を解析し、Objectに変換します。
	 * @param qs 問合せ文字列
	 * @returns 変換されたObject
	 */
	public static parse(qs: string | null | undefined): Record<string, string> {
		const vars: Record<string, string> = {};

		if (qs != null && qs.length > 0) {
			const sidx = qs.charAt(0) === "?" ? 1 : 0;
			const temp = qs.substring(sidx).split("&");

			for (let i = 0; i < temp.length; i++) {
				const params = temp[i].split("=");
				const key = params[0];

				if (!key) continue;

				if (params[1] != null) {
					const v = params[1].replace(/\+/g, "%20");
					vars[key] = decodeURIComponent(v);
				} else {
					// 値なしキー（例: ?a）を空文字として扱う
					vars[key] = "";
				}
			}
		}

		return vars;
	}
}
