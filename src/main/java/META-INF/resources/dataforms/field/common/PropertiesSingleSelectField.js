/**
 * @fileOverview {@link PropertiesSingleSelectField}クラスを記述したファイルです。
 */

'use strict';

import { SingleSelectField } from './SingleSelectField.js';

/**
 * @class PropertiesSingleSelectField
 *
 * @extends SingleSelectField
 */
export class PropertiesSingleSelectField extends SingleSelectField {
	/**
	 * コンストラクタ。
	 */
	constructor() {
		super();
	}

	/**
	 * HTMLエレメントとの対応付けを行います。
	 */
	async attach() {
		await super.attach();
	}
}

