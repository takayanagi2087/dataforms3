/**
 * @fileOverview {@link FolderField}クラスを記述したファイルです。
 */

'use strict';

import { VarcharField } from '../sqltype/VarcharField.js';

/**
 * @class FolderField
 *
 * @extends VarcharField
 */
export class FolderField extends VarcharField {
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
}

