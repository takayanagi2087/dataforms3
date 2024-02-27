/**
 * @fileOverview  {@link MessagesUtil}クラスを記述したファイルです。
 */

'use strict';

/**
 * @class MessagesUtil
 * メッセージユーティリティ。
 * <pre>
 * 各種メッセージを取得します。
 * ClientMessages.properties, &lt;Page&gt;.properties中のメッセージは初期化時にサーバから取得します。
 * 以下の仕様は同期通信を使用するため削除。
 * <span style="text-decoration: line-through;">Messages.propertiesのメッセージは、要求された時にサーバから取得します。</span>
 * </pre>
 */
class MessagesUtil {
	/**
	 * メッセージマップのgetterです。
	 * @returns メッセージマップ。
	 */
	static get messageMap() {
		return window._df_messageMap;
	}

	/**
	 * メッセージマップのsetterです。
	 * @param {Object} map メッセージマップ。
	 */
	static set messageMap(map) {
		window._df_messageMap = map;
	}

	/**
	 * ClientMessages.properties, &lt;Page&gt;.propertiesに指定されたメッセージを読み込みます。
	 * @param {Object} map メッセージマップ。
	 */
	static init(map) {
		MessagesUtil.messageMap = map;
	}

	/**
	 * メッセージを取得します。
	 * <pre>
	 * 以下の仕様は同期通信を使用するため削除。
	 * <span style="text-decoration: line-through;">
	 * initメソッドで読み込んでいないメッセージは、ajaxで取得します。
	 * </span>
	 * </pre>
	 * @param {String} key メッセージキー.
	 * @param {String} [arg] メッセージ引数(複数指定可).
	 * @returns {String} メッセージ.
	 */
	static getMessage() {
		let msg = MessagesUtil.messageMap[arguments[0]];
		if (msg != null) {
			for (let i = 1; i < arguments.length; i++) {
				let rex = RegExp('\\{' + (i - 1) +  '\\}');
				if (msg.match(rex)) {
					let mid = RegExp.lastMatch;
					msg = msg.replace(mid, arguments[i]);
				}
			}
		}
		return msg;
	}
}
