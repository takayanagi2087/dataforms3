/**
 * @fileOverview {@link DbTableListHtmlTable}クラスを記述したファイルです。
 */

'use strict';

/**
 * @class DbTableListHtmlTable
 *
 * @extends HtmlTable
 */
class DbTableListHtmlTable extends HtmlTable {
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
	 * ソートしたリストを取得します。
	 * @return {Array} ソート結果リスト。
	 */
	getSortedList() {
		let list = super.getSortedList();
		for (let i = 0; i < list.length; i++) {
			list[i].rowNo = (i + 1);
		}
		return list;
	}

	/**
	 * ソートを行います。
	 * @param co {jQuery} ラベルのエレメント.
	 * @return {Array} ソート結果リスト。
	 */
	sortTable(col) {
		let list = super.sortTable(col);
		this.parent.setTableEventHandler(list);
		return list;
	}
}

