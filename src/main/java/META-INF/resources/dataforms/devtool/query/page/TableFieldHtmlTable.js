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
	 * 選択肢として追加するか銅貨を判定します。
	 * @param {Obejct} data テーブルフィールドリストの行。
	 * @param {String} fclass 関数フィールドクラス。
	 * @return 追加対象の場合true。
	 */
	#addCheck(data, fclass) {
		if ("jp.dataforms.fw.field.sqlfunc.ExtractField" == fclass) {
			if (data.fieldType == "date" 
				|| data.fieldType == "time"
				|| data.fieldType == "timestamp") {
				return true;
			} else {
				return false;
			}
		} else if ("jp.dataforms.fw.field.sqlfunc.DateAddField" == fclass) {
			if (data.fieldType == "date") {
				return true;
			} else {
				return false;
			}
		} else if ("jp.dataforms.fw.field.sqlfunc.TimestampAddField" == fclass) {
			if (data.fieldType == "timestamp") {
				return true;
			} else {
				return false;
			}
		}
		return true;
	}
	
	
	/**
	 * フィールドの選択選択肢リストを作成します。
	 * @param {Function} fclass 関数フィールドクラス。
	 * @return {Object} 選択肢リスト。
	 */
	getFieldSelectOption(fclass) {
		let list = this.getTableData();
		let optlist = [];
		logger.log("tableFieldList=", list);
		for (let i = 0; i < list.length; i++) {
			if (fclass != null) {
				if (this.#addCheck(list[i], fclass) == false) {
					continue;
				}
			}
			let value = list[i].fieldId;
			let opt = {};
			opt.value = list[i].tableAlias + "." + value;
			opt.name = list[i].tableAlias + "." + value + "(" + list[i].comment + ")";
			opt.fieldType = list[i].fieldType;
			opt.fieldClassName = list[i].fieldFullClassName;
			opt.tableClassName = list[i].tableClassName;
			optlist.push(opt);
		}
		return optlist;
	}
	
	/**
	 * テーブル参照IDからテーブルフィールド情報を取得します。
	 * @param {String} refId 参照フィールドID。
	 * @return {Object} テーブルフィールド情報。
	 */
	getTableFieldInfo(refId) {
		let ret = null;
		let list = this.getTableData();
		for (let i = 0; i < list.length; i++) {
			let id = list[i].tableAlias + "." + list[i].fieldId;
			if (id == refId) {
				ret = list[i];
				break;
			}
		}
		return ret;
	}
}

