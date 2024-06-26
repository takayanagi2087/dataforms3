/**
 * @fileOverview {@link PathNameField}クラスを記述したファイルです。
 */

'use strict';

import { VarcharField } from '../../field/sqltype/VarcharField.js';

/**
 * @class PathNameField
 *
 * @extends VarcharField
 */
export class PathNameField extends VarcharField {
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
		this.get().change((ev) => {
			this.onChange(ev);
		});

// クリック時のイベント処理
/*
		this.get().click(() => {
			// TODO:必要な処理を実装してください。
		});
*/
	}

	/**
	 * 変更イベント処理を行います。
	 * @param {Event} ev イベント情報。
	 */
	onChange(ev) {
		if (typeof(this.parent.setPackageName) == "function") {
			this.parent.setPackageName(ev);
		}
	}

	// 独自のWebメソッドを呼び出す場合は、以下のコメントを参考にしてください。
	/**
	 * Webメソッドの呼び出しサンプル。
	 *
	 */
/*
	async callWebMethod() {
		try {
			let method = this.getWebMethod("webMethod");
			let r = await method.execute(this.id + "=" + this.getValue());
			// TODO:応答情報を適切に処理してください。
			logger.dir(r);
		} catch (e) {
			currentPage.reportError(e);
		}
	}
*/

	// フィールドの各種動作をカスタマイズするには以下のメソッドをオーバーライドしてください。
	/**
	 * autocompleteの選択時の処理を記述します。
	 */
/*
	onAutocompleteSelected() {
		super.onAutocompleteSelected();
	}
*/

	/**
	 * 関連データの更新後に呼び出されるメソッドです。
	 */
/*
	onUpdateRelationField() {
		super.onUpdateRelationField();
		// このフィールドが配置されたフォームのメソッドを呼び出す場合は以下の様にします。
		let form = this.getParentForm();
		if (typeof(form.hogeFunc) == "function") {
			form.hogeFunc();
		}
	}
*/

}

