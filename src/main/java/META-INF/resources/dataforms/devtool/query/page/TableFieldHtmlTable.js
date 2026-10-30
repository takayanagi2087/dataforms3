/**
 * @fileOverview {@link TableFieldHtmlTable}クラスを記述したファイルです。
 */

'use strict';

// import { JsonResponse } from '../../../response/JsonResponse.js';
// import { MessagesUtil } from '../../../util/MessagesUtil.js';
// import { ValidationError } from '../../../validator/ValidationError.js';
import { HtmlTable } from '../../../htmltable/HtmlTable.js';

/**
 * @class TableFieldHtmlTable
 *
 * @extends HtmlTable
 */
export class TableFieldHtmlTable extends HtmlTable {
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
	}
	
	/**
	 * フィールド選択チェックボックスのイベント処理を行います。
	 * @param {Boolean} ck チェック。
	 */
	checkAll(ck) {
		logger.log("checkAll ck=" + ck);
		if (this.tableData != null) {
			let map = {};
			for (let i = 0; i < this.tableData.length; i++) {
				logger.log("checkAll i=" + i);
				let selid = "tableFieldList[" + i + "].sel";
				let sel = this.find("#" + this.selectorEscape(selid));
				let fid = "tableFieldList[" + i + "].fieldId";
				let f = this.find("#" + this.selectorEscape(fid));
				let afid = "tableFieldList[" + i + "].alias";
				let af = this.find("#" + this.selectorEscape(afid));
				let id = f.text();
				if (af.text().length > 0) {
					id = af.text();
				}
				if (map[id] == null) {
					if (ck) {
						sel.prop("checked", true);
						map[id] = true;
					} else {
						sel.prop("checked", false);
					}
				}
			}
		}
	}
}

