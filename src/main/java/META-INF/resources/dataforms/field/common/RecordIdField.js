/**
 * @fileOverview {@link RecordIdField}クラスを記述したファイルです。
 */

'use strict';

import { BigintField } from '../sqltype/BigintField.js';

/**
 * @class RecordIdField
 *
 * @extends BigintField
 */
export class RecordIdField extends BigintField {
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

