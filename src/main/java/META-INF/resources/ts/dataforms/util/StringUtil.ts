/**
 * @fileOverview {@link StringUtil}クラスを記述したファイルです。
 */

'use strict';

/**
 * @class StringUtil
 * 文字列ユーティリティ。
 */
export class StringUtil {
	/**
	 * 空白文字列判定を行います。
	 * @param s 判定する文字列
	 * @returns 空白文字の場合true
	 */
	public static isBlank(s: string | null | undefined): boolean {
		if (s == null) {
			return true;
		}
		if (s.length === 0) {
			return true;
		}
		return false;
	}

	/**
	 * 半角カナリスト。
	 * @returns 半角カナ文字リスト
	 */
	private static get HALF_KANA_LIST(): string {
		// 12345678901234567890123456789012345678901234567890123456789012
		return 'ｱｲｳｴｵｶｷｸｹｺｻｼｽｾｿﾀﾁﾂﾃﾄﾅﾆﾇﾈﾉﾊﾋﾌﾍﾎﾏﾐﾑﾒﾓﾔﾕﾖﾗﾘﾙﾚﾛﾜｦﾝｧｨｩｪｫｬｭｮｯ､｡ｰ｢｣ﾞﾟ';
	}

	/**
	 * 全角カナリスト。
	 * @returns 全角カナ文字リスト
	 */
	private static get FULL_KANA_LIST(): string {
		// １２３４５６７８９０１２３４５６７８９０１２３４５６７８９０１２３４５６７８９０１２３４５６７８９０１２３４５６７８９０
		return 'アイウエオカキクケコサシスセソタチツテトナニヌネノハヒフヘホマミムメモヤユヨラリルレロワヲンァィゥェォャュョッ、。ー「」'
			+ '　　　　　ガギグゲゴザジズゼゾダヂヅデド　　　　　バビブベボ　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　'
			+ '　　　　　　　　　　　　　　　　　　　　　　　　　パピプペポ　　　　　　　　　　　　　　　　　　　　　　　　　　　　　';
	}

	/**
	 * 半角カナ→全角カナ変換を行います。
	 * @param orgtext 変換対象文字列
	 * @returns 変換結果
	 */
	public static halfToFullKana(orgtext: string): string {
		let str = '';

		for (let i = 0; i < orgtext.length; i++) {
			let c = orgtext.charAt(i);
			const cnext = orgtext.charAt(i + 1);
			const n = StringUtil.HALF_KANA_LIST.indexOf(c, 0);
			const nnext = StringUtil.HALF_KANA_LIST.indexOf(cnext, 0);

			if (n >= 0) {
				if (nnext === 60) {
					c = StringUtil.FULL_KANA_LIST.charAt(n + 60);
					i++;
				} else if (nnext === 61) {
					c = StringUtil.FULL_KANA_LIST.charAt(n + 120);
					i++;
				} else {
					c = StringUtil.FULL_KANA_LIST.charAt(n);
				}
			}

			if (n !== 60 && n !== 61) {
				str += c;
			}
		}

		return str;
	}

	/**
	 * 半角→全角変換を行います。
	 * @param str 変換対象文字列
	 * @returns 変換結果
	 */
	public static halfToFull(str: string): string {
		const v = str.replace(/[A-Za-z0-9-!"#$%&'()=<>,.?_\[\]{}@^~\\]/g, (s: string): string => {
			return String.fromCharCode(s.charCodeAt(0) + 65248);
		});

		return StringUtil.halfToFullKana(v);
	}

	/**
	 * 全角カナ→半角カナ変換を行います。
	 * @param orgtext 変換対象文字列
	 * @returns 変換結果
	 */
	public static fullToHalfKana(orgtext: string): string {
		let str = '';

		for (let i = 0; i < orgtext.length; i++) {
			const c = orgtext.charAt(i);
			const n = StringUtil.FULL_KANA_LIST.indexOf(c, 0);

			if (n < 0) {
				str += c;
			} else if (n < 60) {
				str += StringUtil.HALF_KANA_LIST.charAt(n);
			} else if (n < 120) {
				str += StringUtil.HALF_KANA_LIST.charAt(n - 60);
				str += 'ﾞ';
			} else {
				str += StringUtil.HALF_KANA_LIST.charAt(n - 120);
				str += 'ﾟ';
			}
		}

		return str;
	}

	/**
	 * 全角→半角変換。
	 * @param str 変換対象文字列
	 * @returns 変換結果
	 */
	public static fullToHalf(str: string): string {
		let v = str.replace(/　/g, ' ');
		v = v.replace(/[Ａ-Ｚａ-ｚ０-９－！”＃＄％＆’（）＝＜＞，．？＿［］｛｝＠＾～￥]/g, (s: string): string => {
			return String.fromCharCode(s.charCodeAt(0) - 65248);
		});

		return StringUtil.fullToHalfKana(v);
	}
}
