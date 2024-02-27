/**
 * @fileOverview  {@link StringUtil}クラスを記述したファイルです。
 */

'use strict';

/**
 * @class StringUtil
 * 文字列ユーティリティ。
 * <pre>
 * 文字列操作ユーティリティクラス。
 * </pre>
 */
class StringUtil {
	/**
	 * 空白文字列判定を行います。
	 * @param {String} s 判定する文字列。
	 * @returns {Boolean} 空白文字の場合true。
	 */
	static isBlank(s) {
		if (s == null) {
			return true;
		}
		if (s.length == 0) {
			return true;
		}
		return false;
	}

	/**
	 * 半角カナリスト。
	 * @returns 半角カナ文字リスト。
	 */
	static get HALF_KANA_LIST() {
				// 12345678901234567890123456789012345678901234567890123456789012
		let ret = "ｱｲｳｴｵｶｷｸｹｺｻｼｽｾｿﾀﾁﾂﾃﾄﾅﾆﾇﾈﾉﾊﾋﾌﾍﾎﾏﾐﾑﾒﾓﾔﾕﾖﾗﾘﾙﾚﾛﾜｦﾝｧｨｩｪｫｬｭｮｯ､｡ｰ｢｣ﾞﾟ";
		return ret;
	}

	/**
	 * 全角カナリスト。
	 * @returns 全角カナ文字リスト。
	 */
	static get FULL_KANA_LIST() {
				// １２３４５６７８９０１２３４５６７８９０１２３４５６７８９０１２３４５６７８９０１２３４５６７８９０１２３４５６７８９０
		let ret = "アイウエオカキクケコサシスセソタチツテトナニヌネノハヒフヘホマミムメモヤユヨラリルレロワヲンァィゥェォャュョッ、。ー「」"
				+ "　　　　　ガギグゲゴザジズゼゾダヂヅデド　　　　　バビブベボ　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　"
				+ "　　　　　　　　　　　　　　　　　　　　　　　　　パピプペポ　　　　　　　　　　　　　　　　　　　　　　　　　　　　　";
		return ret;
	}

	/**
	 * 半角カナ→全角カナ変換を行います。
	 *
	 * @param {String} orgtext 変換対象文字列。
	 * @returns {String} 変換結果。
	 */
	static halfToFullKana(orgtext) {
		let str = "";
		for (let i=0; i < orgtext.length; i++){
			let c = orgtext.charAt(i);
			let cnext = orgtext.charAt(i + 1);
			let n = StringUtil.HALF_KANA_LIST.indexOf(c, 0);
			let nnext = StringUtil.HALF_KANA_LIST.indexOf(cnext, 0);
			if (n >= 0){
				if (nnext == 60){
					c = StringUtil.FULL_KANA_LIST.charAt(n + 60);
					i++;
				}else if (nnext == 61){
					c = StringUtil.FULL_KANA_LIST.charAt(n + 120);
					i++;
				}else{
					c = StringUtil.FULL_KANA_LIST.charAt(n);
				}
			}
			if ((n != 60) && (n != 61)){
				str += c;
			}
		}
		return str;
	}


	/**
	 * 半角→全角変換を行います。
	 *
	 * @param {String} str 変換対象文字列。
	 * @returns {String} 変換結果。
	 */
	static halfToFull(str) {
		let v = str.replace(/[A-Za-z0-9-!"#$%&'()=<>,.?_\[\]{}@^~\\]/g, function(s) {
			return String.fromCharCode(s.charCodeAt(0) + 65248);
		});
		return StringUtil.halfToFullKana(v);
	}

	/**
	 * 全角カナ→半角カナ変換を行います。
	 *
	 * @param {String} orgtext 変換対象文字列。
	 * @returns {String} 変換結果。
	 */
	static fullToHalfKana(orgtext) {
		let str = "";
		for (let i=0; i < orgtext.length; i++){
			let c = orgtext.charAt(i);
			let n = StringUtil.FULL_KANA_LIST.indexOf(c, 0);
			if (n < 0) {
				str += c;
			} else if (n < 60) {
				str += StringUtil.HALF_KANA_LIST.charAt(n);
			} else if (n < 120) {
				str += StringUtil.HALF_KANA_LIST.charAt(n - 60);
				str += "ﾞ";
			} else {
				str += StringUtil.HALF_KANA_LIST.charAt(n - 120);
				str += "ﾟ";
			}
		}
		return str;
	}

	/**
	 * 全角→半角変換。
	 *
	 * @param {String} str 変換対象文字列。
	 * @returns {String} 変換結果。
	 */
	static fullToHalf(str) {
		let v = str.replace( /[Ａ-Ｚａ-ｚ０-９－！”＃＄％＆’（）＝＜＞，．？＿［］｛｝＠＾～￥]/g, (s) => {
			return String.fromCharCode(s.charCodeAt(0) - 65248);
		});
		return StringUtil.fullToHalfKana(v);
	}
}
