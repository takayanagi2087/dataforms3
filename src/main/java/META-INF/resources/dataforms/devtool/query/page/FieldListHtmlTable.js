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
	
	setTableData(data) {
		super.setTableData(data);
		for (let i = 0; i < this.getRowCount(); i++) {
			let fld = this.getRowField(i, "fieldClass");
			let rfld = this.getRowField(i, "refFieldId");
			let sidx = fld.get().prop("selectedIndex");
			if (fld.optionList[sidx].summary) {
				rfld.lock(false);
			} else {
				rfld.lock(true);
			}
		}
	}
}

