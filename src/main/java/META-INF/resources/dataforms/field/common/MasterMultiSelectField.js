/**
 * @fileOverview {@link MasterMultiSelectField}クラスを記述したファイルです。
 */

'use strict';

import { MultiSelectField } from './MultiSelectField.js';

/**
 * @class MasterMultiSelectField
 *
 * @extends MultiSelectField
 */
export class MasterMultiSelectField extends MultiSelectField {
	/**
	 * HTMLエレメントとの対応付けを行います。
	 */
	async attach() {
		await super.attach();
	}
}

