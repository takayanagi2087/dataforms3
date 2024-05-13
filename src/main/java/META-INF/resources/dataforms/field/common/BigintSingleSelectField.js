/**
 * @fileOverview {@link BigintSingleSelectField}クラスを記述したファイルです。
 */

'use strict';

import { SingleSelectField } from './SingleSelectField.js';

/**
 * @class BigintSingleSelectField
 *
 * @extends SingleSelectField
 */
export class BigintSingleSelectField extends SingleSelectField {
	/**
	 * HTMLエレメントとの対応付けを行います。
	 */
	async attach() {
		await super.attach();
	}
}


