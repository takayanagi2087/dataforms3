/**
 * @fileOverview {@link LongTapHandler}クラスを記述したファイルです。
 */
'use strict';
/**
 * ロングタップを検出するためのクラスです。
 */
export class LongTapHandler {
    /**
     * マウスダウン時のイベント情報。
     */
    #ev = null;
    /**
     * ロングタップ処理メソッド。
     */
    #longtap;
    #touch = false;
    /**
     * コンストラクタ。
     * @param jq jqueryオブジェクト。
     * @param longtap ロングタップ処理メソッド。
     */
    constructor(jq, longtap) {
        this.#longtap = longtap;
        jq.on('touchstart', (ev) => {
            logger.log('touchstart', ev);
            this.#touch = true;
            this.#onDown(ev);
        });
        jq.on('touchend', (ev) => {
            logger.log('touchend', ev);
            if (this.#touch) {
                this.#onUp(ev);
            }
        });
        jq.on('touchmove', (ev) => {
            if (this.#touch) {
                this.#onUp(ev);
            }
        });
    }
    /**
     * タップまたはクリック開始時の処理。
     * @param ev イベント情報。
     */
    #onDown(ev) {
        this.#ev = ev;
        setTimeout(() => {
            if (this.#ev !== null) {
                this.#longtap(this.#ev);
            }
            this.#ev = null;
        }, 1000);
    }
    /**
     * タップまたはクリック終了時の処理。
     * @param ev イベント情報。
     */
    #onUp(_ev) {
        this.#ev = null;
    }
}
