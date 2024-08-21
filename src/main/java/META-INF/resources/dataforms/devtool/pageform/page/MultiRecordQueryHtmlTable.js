/**
 * @fileOverview {@link MultiRecordQueryHtmlTable}クラスを記述したファイルです。
 */

'use strict';

import { EditableHtmlTable } from '../../../htmltable/EditableHtmlTable.js';

/**
 * @class MultiRecordQueryHtmlTable
 *
 * @extends EditableHtmlTable
 */
export class MultiRecordQueryHtmlTable extends EditableHtmlTable {
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
	 * 行追加時に呼び出されるメソッドです。
	 * <pre>
	 * 行中のボタンなどにイベント処理を登録します。
	 * </pre>
	 * @param {String} rowid 設定する行のID('tableid[idx]'形式)。
	 */
	onAddTr(rowid) {
		super.onAddTr(rowid);
		this.get(rowid + ".fieldButton").click((ev) => {
			this.parent.onFieldButton(ev);
		});
	}
}

