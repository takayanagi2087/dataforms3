/**
 * @fileOverview  {@link ClickHandler}クラスを記述したファイルです。
 * 1つのコンポーネントでクリックとダブルクリックを分けて処理する為のクラスです。
 */
'use strict';

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
	#click = null;

	/**
	 * ダブルクリック時のメソッド。
	 */	
	#dblclick = null;
	
	/**
	 * コンストラクタ。
	 * @param {jQuery} jq jQueryオブジェクト。 
	 * @param {Function} click クリック処理メソッド。
	 * @param {Function} dblclick ダブルクリック処理メソッド。
	 */
	constructor(jq, click, dblclick) {
		this.#click = click;
		this.#dblclick = dblclick;
		jq.click((ev) => {
			this.#handleClick(ev, this.#click, this.#dblclick);
		});
	}

	/**
	 * クリックイベント処理
	 * @param {Event} ev イベント情報。
	 * @param {Function} click クリック処理メソッド。
	 * @param {Function} dblclick ダブルクリック処理メソッド。
	 */
	#handleClick(ev, click, dblclick) {
		this.#ev = ev;
		// 300ms後にクリック数を判定。
		if (this.#clickCount == 0) {
			setTimeout(() => {
			    if (this.#clickCount == 1) {
					// クリック
					click(this.#ev);
			    } else {
					// ダブルクリック
					dblclick(this.#ev);
				}
			    this.#clickCount = 0;
			}, 300);
		}
		this.#clickCount++;
	}

}