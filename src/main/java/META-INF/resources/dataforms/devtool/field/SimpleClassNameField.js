/**
 * @fileOverview {@link SimpleClassNameField}クラスを記述したファイルです。
 */

'use strict';

import { VarcharField } from "../../field/sqltype/VarcharField.js";

/**
 * @class SimpleClassNameField
 *
 * @extends VarcharField
 */
export class SimpleClassNameField extends VarcharField {
	/**
	 * HTMLエレメントとの対応付けを行います。
	 */
	attach() {
		super.attach();
	}
}


