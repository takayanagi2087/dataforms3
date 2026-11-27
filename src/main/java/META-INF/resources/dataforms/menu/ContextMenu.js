/**
 * @fileOverview {@link ContextMenu}クラスを記述したファイルです。
 */

'use strict';

import { WebComponent } from '../controller/WebComponent.js';

/**
 * @class ContextMenu
 * コンテキストメニュークラス。
 * @extends WebComponent
 */
export class ContextMenu extends WebComponent {

	/**
	 * resolvメソッド。
	 */
	#resolv = null;
	/**
	 * メニューオープンフラグ
	 */
	#open = false;
	
	/**
	 * HTMLエレメントとの対応付けを行います。
	 */
	attach() {
		super.attach();
		logger.log("ContextMenu:", this);
		this.getParentForm().get().append(this.getHtml());
		this.find("li").click((ev) => {
			if (this.#open) {
				let value = $(ev.target).data("value");
				logger.log("value=" + value);
				this.#resolv(value);
				this.get().offset({
					top: 0,
					left: 0
				});
				this.get().hide();
				this.#open = false;
			}
		});
		$("body").click(() => {
			this.get().offset({
				top: 0,
				left: 0
			});
			this.get().hide();
			if (this.#resolv != null) {
				this.#resolv(null);
			}
			this.#open = false;
		});
	}
	
	/** 
	 * コンテキストメニューのHTMLを取得します。
	 * @return HTML。
	 */
	getHtml() {
		let html = "<ul id='" + this.realId + "' data-id='" + this.id + "' class='contextMenu'>\n";
		for (let i = 0; i < this.itemList.length; i++) {
			html += "<li data-value='" + this.itemList[i].value + "'>" + this.itemList[i].name + "</li>\n";
		}
		html += "</ul>\n";
		return html;
	}
	
	/**
	 * メニューを選択します。
	 * @return メニューの選択値。
	 */
	async select(ev) {
		this.#open = true;
		let x = ev.clientX;
		let y = ev.clientY;
		if (ev.touches != null) {
			if (ev.touches.length > 0) {
				x = ev.touches[0].pageX;
				y = ev.touches[0].pageY;
			}
		}
		let height = $(document).height();

		this.get().offset({
			top: y,
			left: x
		});
		this.get().show();
		logger.log("height=" + height);
		logger.log("y=" + y);
		if (height < y + this.get().height()) {
			logger.log("over");
			y = height - this.get().height() - 30;
			this.get().offset({
				top: y,
			});
		}
		
		let ret = new Promise((resolv) => {
			this.#resolv = resolv;
		});
		return ret;
	}
	
	/**
	 * コンテキストメニューを閉じます。
	 */
	close() {
		this.get().offset({
			top: 0,
			left: 0
		});
		this.get().hide();
		if (this.#open) {
			this.#resolv(null);
		}
		this.#open = false;
	}
}


