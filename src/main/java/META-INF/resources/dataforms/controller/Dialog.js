/**
 * @fileOverview {@link Dialog}クラスを記述したファイルです。
 */

'use strict';

import { DataForms } from './DataForms.js';


/**
 * @class Dialog
 *
 * ダイアログクラス。
 * <pre>
 * DataFormsのサブクラスのDialogクラスです。
 * ダイアログはjquery-uiのdialogで実装しています。
 * </pre>
 * @extends DataForms
 */
export class Dialog extends DataForms {
	/**
	 * コンストラクタ。
	 */
	constructor() {
		super();
		this.width = "auto";
		this.height = "auto";
		this.resizable = true;

	}

	/**
	 * Dialogから派生したクラスかどうかを判定します。
	 * @return Dialogから派生したクラスの場合true。
	 */
	isDialog() {
		return true;
	}

	/**
	 * 初期化処理を行います。
	 * <pre>
	 * </pre>
	 */
	init() {
		super.init();
		let dlgdiv = $('body').find(this.convertSelector('#' + this.selectorEscape(this.id)));
		if (dlgdiv.length == 0) {
			let htmlstr = this.additionalHtmlText;
			dlgdiv = $('body').append("<div " + this.getIdAttribute() + "='" + this.id + "' class='" + this.id + "' style='display:none;'>" + htmlstr + "</div>");
		}
		// ダイアログ中のFormの初期化.
		this.initForm(this.formMap);
	}

	/**
	 * HTMLエレメントとの対応付けを行います。
	 * <pre>
	 * #closeButtonのイベント処理を登録します。
	 * </pre>
	 */
	 attach() {
		super.attach();
		this.get("closeButton").click(() => {
			this.close();
			return false;
		});
	}
	
	/**
	 * ダイアログクローズ前の処理。
	 * @param {Event} ev イベント情報。
	 * @param {Object} ui オブジェクト。 
	 */
	beforeClose(ev, ui) {
		logger.log("ev=", ev);
		logger.log("ui=", ui);
	}
	
	/**
	 * ダイアログを表示します。
	 * @param {Boolean} modal モーダル表示の場合true。
	 * @param {Object} p 追加プロパティ。
	 *
	 */
	show(modal, p) {
		this.toQueryMode();
		let dlgdiv = $('body').find(this.convertSelector('#' + this.selectorEscape(this.id)));
		let m = {
			modal: modal
			, title: this.title
			, width: this.width
			, height: this.height
			, resizable: this.resizable
			, beforeClose: (ev, ui) => {
				this.beforeClose(ev, ui);	
			}
		};
		if (p != null) {
			for (let k in p) {
				m[k] = p[k];
			}
		}
		dlgdiv.dialog(m);
	}


	/**
	 * モーダルダイアログ表示を行います。
	 * @param {Object} p 追加プロパティ。
	 */
	showModal(p) {
		this.show(true, p);
	}

	/**
	 * モードレスダイアログ表示を行います。
	 * @param {Object} p 追加プロパティ。
	 */
	showModeless(p) {
		this.show(false, p);
	}

	/**
	 * ダイアログを閉じます。
	 */
	close() {
		this.resetErrorStatus();
		let dlgdiv = $('body').find(this.convertSelector('#' + this.selectorEscape(this.id)));
		dlgdiv.dialog('close');
	}

}


