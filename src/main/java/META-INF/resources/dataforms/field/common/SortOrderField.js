/**
 * @fileOverview {@link SortOrderField}クラスを記述したファイルです。
 */

'use strict';

import { SmallintField } from '../sqltype/SmallintField.js';

/**
 * @class SortOrderField
 *
 * @extends SmallintField
 */
export class SortOrderField extends SmallintField {
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

