type ContextMenuCallback = (ev: JQuery.Event) => void;
/**
 * ContextMenuイベント設定クラス。
 * iOS、iPadOSはcontextmenuイベントをサポートしていないため、
 * 長押しを検出して動作します。
 */
export declare class ContextMenuEventListener {
    /**
     * コンストラクタ。
     * FIXME: http://ipaddress/app/ でアクセスすると動作しない問題がある。原因不明。
     * @param jq jqueryオブジェクト。
     * @param contextmenu contextmenuイベント処理メソッド。
     */
    constructor(jq: JQuery, contextmenu: ContextMenuCallback);
}
export {};
//# sourceMappingURL=ContextMenuEventListener.d.ts.map