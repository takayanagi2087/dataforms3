type ClickCallback = (ev: JQuery.Event) => void;
/**
 * 1つのコンポーネントでクリックとダブルクリックを分けて処理する為のクラスです。
 *
 */
export declare class ClickHandler {
    #private;
    /**
     * コンストラクタ。
     * @param jq jQueryオブジェクト。
     * @param click クリック処理メソッド。
     * @param dblclick ダブルクリック処理メソッド。
     */
    constructor(jq: JQuery, click: ClickCallback, dblclick: ClickCallback);
}
export {};
//# sourceMappingURL=ClickHandler.d.ts.map