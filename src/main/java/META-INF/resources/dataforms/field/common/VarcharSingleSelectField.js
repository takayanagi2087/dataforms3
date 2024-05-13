/**
 * @fileOverview {@link VarcharSingleSelectField}クラスを記述したファイルです。
 */

'use strict';

import { SingleSelectField } from './SingleSelectField.js';

/**
 * @class VarcharSingleSelectField
 *
 * @extends SingleSelectField
 */
export class VarcharSingleSelectField extends SingleSelectField {
	/**
	 * HTMLエレメントとの対応付けを行います。
	 */
	async attach() {
		await super.attach();
	}
}

