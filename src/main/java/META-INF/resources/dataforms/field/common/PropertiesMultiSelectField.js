/**
 * @fileOverview {@link PropertiesMultiSelectField}クラスを記述したファイルです。
 */

'use strict';

import { MultiSelectField } from './MultiSelectField.js';

/**
 * @class PropertiesMultiSelectField
 *
 * @extends MultiSelectField
 */
export class PropertiesMultiSelectField extends MultiSelectField {
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

