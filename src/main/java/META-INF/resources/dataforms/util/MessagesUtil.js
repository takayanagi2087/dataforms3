/**
 * @fileOverview {@link MessagesUtil}クラスを記述したファイルです。
 */
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
    static get messageMap() {
        return window._df_messageMap;
    }
    /**
     * メッセージマップのsetterです。
     */
    static set messageMap(map) {
        window._df_messageMap = map;
    }
    /**
     * ClientMessages.properties, Page.propertiesに指定されたメッセージを読み込みます。
     */
    static init(map) {
        MessagesUtil.messageMap = map;
    }
    /**
     * メッセージを取得します。
     * @param key メッセージキー
     * @param args メッセージ引数（複数指定可）
     * @returns メッセージ
     */
    static getMessage(key, ...args) {
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
