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
	 * OkCancelFormのインスタンスを取得します。
	 * @return {OkCancelForm} OkCancelFormのインスタンス。
	 */
	getOkCancelForm() {
		let formId = Object.keys(this.formMap)[0];
		logger.log("form = " + formId);
		let frm = this.getComponent(formId);
		return frm;	
	}

	/**
	 * フォームの初期化処理を行います。
	 * @param {Object} p フォームデータ。
	 * <pre>
	 * p.formDataの内容をフォームに設定します。
	 * </pre>
	 */
	setFormData(p) {
		let frm = this.getOkCancelForm();
		logger.log("initForm data = ", p);
		frm.setFormData(p.formData);
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
		let frm = this.getOkCancelForm();
		if (frm.ok) {
			return frm.getFormData();
		} else {
			return null;
		}
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