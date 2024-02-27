/**
 * @fileOverview {@link JavaSourcePathField}クラスを記述したファイルです。
 */

'use strict';

/**
 * @class JavaSourcePathField
 *
 * @extends VarcharField
 */
class JavaSourcePathField extends VarcharField {
	/**
	 * HTMLエレメントとの対応付けを行います。
	 */
	attach() {
		super.attach();
		this.get().attr("placeholder", MessagesUtil.getMessage("message.setjavasourcepath"))
	}
}


