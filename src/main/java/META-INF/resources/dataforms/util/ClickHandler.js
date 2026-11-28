/**
 * @fileOverview {@link ClickHandler}クラスを記述したファイルです。
 */
'use strict';
/**
 * 1つのコンポーネントでクリックとダブルクリックを分けて処理する為のクラスです。
 *
 */
export class ClickHandler {
    /**
     * クリック数。
     */
    #clickCount = 0;
    /**
     * クリックイベント。
     */
    #ev = null;
    /**
     * クリック時のメソッド。
     */
    #click;
    /**
     * ダブルクリック時のメソッド。
     */
    #dblclick;
    /**
     * コンストラクタ。
     * @param jq jQueryオブジェクト。
     * @param click クリック処理メソッド。
     * @param dblclick ダブルクリック処理メソッド。
     */
    constructor(jq, click, dblclick) {
        this.#click = click;
        this.#dblclick = dblclick;
        jq.on('click', (ev) => {
            this.#handleClick(ev, this.#click, this.#dblclick);
        });
    }
    /**
     * クリックイベント処理
     * @param ev イベント情報。
     * @param click クリック処理メソッド。
     * @param dblclick ダブルクリック処理メソッド。
     */
    #handleClick(ev, click, dblclick) {
        this.#ev = ev;
        // 300ms後にクリック数を判定。
        if (this.#clickCount === 0) {
            setTimeout(() => {
                if (!this.#ev)
                    return;
                if (this.#clickCount === 1) {
                    // クリック
                    click(this.#ev);
                }
                else {
                    // ダブルクリック
                    dblclick(this.#ev);
                }
                this.#clickCount = 0;
            }, 300);
        }
        this.#clickCount++;
    }
}
