/**
 * @fileOverview {@link LangCodeField}クラスを記述したファイルです。
 */

'use strict';

import { VarcharSingleSelectField } from './VarcharSingleSelectField.js';

/**
 * @class LangCodeField
 *
 * @extends VarcharSingleSelectField
 */
export class LangCodeField extends VarcharSingleSelectField {
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

