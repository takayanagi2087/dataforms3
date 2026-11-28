/**
 * @fileOverview {@link LongTapHandler}クラスを記述したファイルです。
 */
'use strict';

declare const logger: {
	log(message: string, ...values: unknown[]): void;
};

/**
 * ロングタップを検出するためのクラスです。
 */
export class LongTapHandler {
	/**
	 * マウスダウン時のイベント情報。
	 */
	#ev: JQuery.Event | null = null;

	/**
	 * ロングタップ処理メソッド。
	 */
	#longtap: (ev: JQuery.Event) => void;

	#touch: boolean = false;

	/**
	 * コンストラクタ。
	 * @param jq jqueryオブジェクト。
	 * @param longtap ロングタップ処理メソッド。
	 */
	constructor(jq: JQuery, longtap: (ev: JQuery.Event) => void) {
		this.#longtap = longtap;

		jq.on('touchstart', (ev: JQuery.Event) => {
			logger.log('touchstart', ev);
			this.#touch = true;
			this.#onDown(ev);
		});

		jq.on('touchend', (ev: JQuery.Event) => {
			logger.log('touchend', ev);
			if (this.#touch) {
				this.#onUp(ev);
			}
		});

		jq.on('touchmove', (ev: JQuery.Event) => {
			if (this.#touch) {
				this.#onUp(ev);
			}
		});
	}

	/**
	 * タップまたはクリック開始時の処理。
	 * @param ev イベント情報。
	 */
	#onDown(ev: JQuery.Event): void {
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
	#onUp(_ev: JQuery.Event): void {
		this.#ev = null;
	}
}
