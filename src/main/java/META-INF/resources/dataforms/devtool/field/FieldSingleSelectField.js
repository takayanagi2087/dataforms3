/**
 * @fileOverview {@link FieldSingleSelectField}クラスを記述したファイルです。
 */

'use strict';

// import { JsonResponse } from '../../response/JsonResponse.js';
// import { MessagesUtil } from '../../util/MessagesUtil.js';
// import { ValidationError } from '../../validator/ValidationError.js';
import { VarcharSingleSelectField } from '../../field/common/VarcharSingleSelectField.js';

/**
 * @class FieldSingleSelectField
 *
 * @extends VarcharSingleSelectField
 */
export class FieldSingleSelectField extends VarcharSingleSelectField {
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
		this.get().focus(() => {
			this.setOption();
		});
	}

	/**
	 * テーブルフィールドの選択肢を設定します。
	 */
	setOption() {
		let tbl = this.getParentForm().getComponent("tableFieldList");
		logger.log("tbl=", tbl);
		let list = tbl.getTableData();
		let optlist = [];
		logger.log("tableFieldList=", list);
		for (let i = 0; i < list.length; i++) {
			let value = list[i].fieldId;
			let opt = {};
			opt.value = value;
			opt.name = list[i].tableAlias + "." + value + "(" + list[i].comment + ")";
			optlist.push(opt);
		}
		this.setOptionList(optlist);
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

