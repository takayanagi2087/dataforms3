/**
 * @fileOverview {@link OkCancelFrom}クラスを記述したファイルです。
 */

import { Form } from '../../controller/Form.js';

/**
 * @class OkCancelForm
 *
 * データ編集フォーム。
 * <pre>
 * データ編集を行うフォームです。
 * </pre>
 * @extends Form
 *
 * @prop {Boolean} ok OKボタンが押下された場合true。
 *
 *
 */
export class OkCancelForm extends Form {
	/**
	 * okプロパティ。
	 */
	#ok = false;
	get ok() {
		return this.#ok;
	}
	set ok(ok) {
		this.#ok = ok;
	}
	
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
		console.log("OkCancelForm.attach()");
		this.get("okButton").click(() => {
			logger.log("okButton");
			this.#onOkButton();
			return false;
		});
		this.get("cancelButton").click(() => {
			logger.log("cancelButton");
			this.#onCancelButton();
			return false;
		});
	}
	
	/**
	 * OKボタン押下時の処理。
	 */
	onOkButton() {
		
	}
	
	#onOkButton() {
		this.#ok = true;
		this.onOkButton();
		this.parent.close();
	}
	
	/**
	 * Cancelボタン押下時の処理。
	 */
	onCancelButton() {
		
	}
	
	#onCancelButton() {
		this.#ok = false;
		this.onCancelButton();
		this.parent.close();
	}
	
}