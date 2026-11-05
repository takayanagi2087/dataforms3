/**
 * @fileOverview {@link FieldClassSelectField}クラスを記述したファイルです。
 */

'use strict';

// import { JsonResponse } from '../../response/JsonResponse.js';
// import { MessagesUtil } from '../../util/MessagesUtil.js';
// import { ValidationError } from '../../validator/ValidationError.js';
import { VarcharSingleSelectField } from '../../field/common/VarcharSingleSelectField.js';

/**
 * @class FieldClassSelectField
 *
 * @extends VarcharSingleSelectField
 */
export class FieldClassSelectField extends VarcharSingleSelectField {
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
//
// フィールドのイベント処理
//
// this.get()でフィールドのjQueryオブジェクトの取得が可能です。
//
// 変更時のイベント処理
/*
		this.get().change(() => {
			// TODO:必要な処理を実装してください。
		});
*/

// クリック時のイベント処理
/*
		this.get().click(() => {
			// TODO:必要な処理を実装してください。
		});
*/
	}

	/**
	 * 参照フィールドを選択可能にするかを判定します。
	 * @return {Boolean} 選択可能な場合true。
	 */
	isRefFieldSelectable() {
		let sidx = this.get().prop("selectedIndex");
		logger.log("sidx = " + sidx);
		logger.log("fieldType = " + this.optionList[sidx].type);
		if (this.optionList[sidx].type == null || 
			this.optionList[sidx].type == "summary" ||
			this.optionList[sidx].type == "datefunc") {
			return true;
		} else {
			return false;
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
	 * @param {Object} data 関連データ。
	 */
/*
	onUpdateRelationField(data) {
		super.onUpdateRelationField(data);
		// このフィールドが配置されたフォームのメソッドを呼び出す場合は以下の様にします。
		let form = this.getParentForm();
		if (typeof(form.hogeFunc) == "function") {
			form.hogeFunc();
		}
	}
*/

}

