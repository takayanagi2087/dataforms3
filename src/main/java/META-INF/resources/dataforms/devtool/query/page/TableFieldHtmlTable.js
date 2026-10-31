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
	
	/**
	 * フィールドの選択選択肢リストを作成します。
	 * @param {Function} func 追加判定関数。
	 * @return {Object} 選択肢リスト。
	 */
	getFieldSelectOption(func) {
		let list = this.getTableData();
		let optlist = [];
		logger.log("tableFieldList=", list);
		for (let i = 0; i < list.length; i++) {
			if (func != null) {
				if (func(list[i]) == false) {
					continue;
				}
			}
			let value = list[i].fieldId;
			let opt = {};
			opt.value = list[i].tableAlias + "." + value;
			opt.name = list[i].tableAlias + "." + value + "(" + list[i].comment + ")";
			optlist.push(opt);
		}
		return optlist;
	}
}

