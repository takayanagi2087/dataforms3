/**
 * @fileOverview {@link ClassNameValidator}クラスを記述したファイルです。
 */

'use strict';

import { MessagesUtil } from '../../util/MessagesUtil.js';
import { RegexpValidator } from '../../validator/RegexpValidator.js';

/**
 * @class ClassNameValidator
 *
 * @extends RegexpValidator
 */

export class ClassNameValidator extends RegexpValidator {
	/**
	 * HTMLエレメントとの対応付けを行います。
	 */
	attach() {
		super.attach();
	}

	/**
	 * エラーメッセージを取得します。
	 * @returns {String} エラーメッセージ。
	 */
	getMessage(dspname) {
		return MessagesUtil.getMessage(this.messageKey, dspname, this.classType);
	}
}




