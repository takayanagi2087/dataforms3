/**
 * @fileOverview {@link FlagMultiSelectField}クラスを記述したファイルです。
 */

'use strict';

import { MultiSelectField } from './MultiSelectField.js';

/**
 * @class FlagMultiSelectField
 *
 * @extends MultiSelectField
 */
export class FlagMultiSelectField extends MultiSelectField {
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

