/**
 * @fileOverview {@link JavaSourcePathField}クラスを記述したファイルです。
 */

'use strict';

import { VarcharField } from "../../field/sqltype/VarcharField.js";
import { MessagesUtil } from "../../util/MessagesUtil.js";
/**
 * @class JavaSourcePathField
 *
 * @extends VarcharField
 */
export class JavaSourcePathField extends VarcharField {
	/**
	 * HTMLエレメントとの対応付けを行います。
	 */
	attach() {
		super.attach();
		this.get().attr("placeholder", MessagesUtil.getMessage("message.setjavasourcepath"))
	}
}


