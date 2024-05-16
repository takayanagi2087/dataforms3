/**
 * @fileOverview {@link WebSourcePathField}クラスを記述したファイルです。
 */

'use strict';

import { MessagesUtil } from "../../util/MessagesUtil.js";
import { VarcharField } from "../../field/sqltype/VarcharField.js";

/**
 * @class WebSourcePathField
 *
 * @extends VarcharField
 */
export class WebSourcePathField extends VarcharField {
	/**
	 * HTMLエレメントとの対応付けを行います。
	 */
	attach() {
		super.attach();
		this.get().attr("placeholder", MessagesUtil.getMessage("message.setwebsourcepath"))
	}
}



