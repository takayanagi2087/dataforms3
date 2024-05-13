/**
 * @fileOverview {@link SmallintSingleSelectField}クラスを記述したファイルです。
 */

'use strict';

import { SingleSelectField } from './SingleSelectField.js';


/**
 * @class SmallintSingleSelectField
 *
 * @extends SingleSelectField
 */
export class SmallintSingleSelectField extends SingleSelectField {
	/**
	 * HTMLエレメントとの対応付けを行います。
	 */
	async attach() {
		await super.attach();
	}
}


