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
export declare class MessagesUtil {
    /**
     * メッセージマップのgetterです。
     */
    private static get messageMap();
    /**
     * メッセージマップのsetterです。
     */
    private static set messageMap(value);
    /**
     * ClientMessages.properties, Page.propertiesに指定されたメッセージを読み込みます。
     */
    static init(map: MessageMap): void;
    /**
     * メッセージを取得します。
     * @param key メッセージキー
     * @param args メッセージ引数（複数指定可）
     * @returns メッセージ
     */
    static getMessage(key: string, ...args: string[]): string | undefined;
}
export {};
//# sourceMappingURL=MessagesUtil.d.ts.map