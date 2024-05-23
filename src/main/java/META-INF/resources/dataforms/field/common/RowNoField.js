/**
 * @fileOverview {@link RowNoField}クラスを記述したファイルです。
 */

'use strict';

import { IntegerField } from '../sqltype/IntegerField.js';

/**
 * @class RowNoField
 *
 * @extends IntegerField
 */
export class RowNoField extends IntegerField {
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

