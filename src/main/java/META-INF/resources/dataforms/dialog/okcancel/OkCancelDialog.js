/**
 * @fileOverview {@link OkCancelDialog} クラスを記述したファイルです。
 */
'use strict';

import { Dialog } from '../../controller/Dialog.js';

/**
 * @class AlertDialog
 *
 * @extends Dialog
 */
export class OkCancelDialog extends Dialog {
	
	/**
	 * 結果通知メソッド。
	 */
	#resolv = null;

	/**
	 * コンストラクタ。
	 */
	constructor() {
		super();
	}

	/**
	 * HTMLエレメントとの対応付けを行います。
	 */
	attach() {
		super.attach();
		logger.info("OkCancelDialog.attach()");
	}

	/**
	 * フォームの初期化処理を行います。
	 * @param {Object} data フォームデータ。
	 */
	setFormData(data) {
		logger.log("initForm data = ", data);
		
	}
	
	/**
	 * モーダル表示処理。
	 * @param {Object} p パラメータ。
	 */
	async doModal(p) {
		// フォームの処理結果を返すためのpromiseを作成。
		let ret = new Promise((resolv) => {
			this.#resolv = resolv; 
		});
		this.setFormData(p)
		this.showModal(p);
		return ret;
	}

	/**
	 * フォームの結果を取得します。
	 * @return {Obejct} フォームの処理結果。
	 */	
	getFormData() {
		return {};
	}
	
	/**
	 * ダイアログクローズ前の処理。
	 * @param {Event} ev イベント情報。
	 * @param {Object} ui オブジェクト。 
	 */
	beforeClose(ev, ui) {
		super.beforeClose(ev, ui);
		// フォームの処理結果を返します。
		let ret = this.getFormData();
		this.#resolv(ret);
	}
}