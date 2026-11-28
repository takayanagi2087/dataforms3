/**
 * @fileOverview {@link MessagesUtil}クラスを記述したファイルです。
 */

type MessageMap = Record<string, string>;

declare global {
	interface Window {
		_df_messageMap: MessageMap;
	}
}

/**
 * @class MessagesUtil
 * メッセージユーティリティ。
 * 各種メッセージを取得します。
　* ClientMessages.properties, Page.properties中のメッセージは初期化時にサーバから取得します。
　* 以下の仕様は同期通信を使用するため削除。
 */
export class MessagesUtil {
	/**
	 * メッセージマップのgetterです。
	 */
	private static get messageMap(): MessageMap {
		return window._df_messageMap;
	}

	/**
	 * メッセージマップのsetterです。
	 */
	private static set messageMap(map: MessageMap) {
		window._df_messageMap = map;
	}

	/**
	 * ClientMessages.properties, Page.propertiesに指定されたメッセージを読み込みます。
	 */
	public static init(map: MessageMap): void {
		MessagesUtil.messageMap = map;
	}

	/**
	 * メッセージを取得します。
	 * @param key メッセージキー
	 * @param args メッセージ引数（複数指定可）
	 * @returns メッセージ
	 */
	public static getMessage(key: string, ...args: string[]): string | undefined {
		let msg = MessagesUtil.messageMap[key];

		if (msg != null) {
			for (let i = 0; i < args.length; i++) {
				const rex = new RegExp('\\{' + i + '\\}');
				if (rex.test(msg)) {
					msg = msg.replace(rex, args[i]);
				}
			}
		}

		return msg;
	}
}
