/**
 * @fileOverview {@link FieldListHtmlTable}クラスを記述したファイルです。
 */

'use strict';

// import { JsonResponse } from '../../../response/JsonResponse.js';
// import { MessagesUtil } from '../../../util/MessagesUtil.js';
// import { ValidationError } from '../../../validator/ValidationError.js';
import { EditableHtmlTable } from '../../../htmltable/EditableHtmlTable.js';

/**
 * @class FieldListHtmlTable
 *
 * @extends EditableHtmlTable
 */
export class FieldListHtmlTable extends EditableHtmlTable {
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
	 * データの設定状況に応じたフィールド制御を行います。
	 * @param {Array} data テーブルデータ。
	 */
	setTableData(data) {
		super.setTableData(data);
		for (let i = 0; i < this.getRowCount(); i++) {
			let fld = this.getRowField(i, "funcFieldClassName");
			let rfld = this.getRowField(i, "refFieldId");
			if (fld.isRefFieldSelectable()) {
				rfld.lock(false);
			} else {
				rfld.setValue(null);
				rfld.lock(true);
			}
		}
	}
	
	onAddTr(rowid) {
		super.onAddTr(rowid);
		logger.log("onAddTr(" + rowid + ")");
	}
}

