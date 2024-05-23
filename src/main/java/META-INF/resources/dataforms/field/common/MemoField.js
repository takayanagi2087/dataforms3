/**
 * @fileOverview {@link MemoField}クラスを記述したファイルです。
 */

'use strict';

import { VarcharField } from '../sqltype/VarcharField.js';

/**
 * @class MemoField
 *
 * @extends VarcharField
 */
export class MemoField extends VarcharField {
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

