/**
 * @fileOverview {@link MenuTable}クラスを記述したファイルです。
 */

'use strict';

/**
 * @class MenuTable
 *
 * @extends EditableHtmlTable
 */
class MenuTable extends EditableHtmlTable {
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
		logger.log("MenuTable=", this);
	}
}

