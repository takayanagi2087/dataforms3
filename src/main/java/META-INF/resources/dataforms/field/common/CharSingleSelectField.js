/**
 * @fileOverview {@link CharSingleSelectField}クラスを記述したファイルです。
 */

'use strict';

import { SingleSelectField } from './SingleSelectField.js';

/**
 * @class CharSingleSelectField
 *
 * @extends SingleSelectField
 */
export class CharSingleSelectField extends SingleSelectField {
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


