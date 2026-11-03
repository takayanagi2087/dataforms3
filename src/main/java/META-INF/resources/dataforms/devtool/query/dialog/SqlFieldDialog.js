/**
 * @fileOverview {@link SqlFieldDialog}クラスを記述したファイルです。
 */

'use strict';

// import { JsonResponse } from '../../../response/JsonResponse.js';
// import { MessagesUtil } from '../../../util/MessagesUtil.js';
// import { ValidationError } from '../../../validator/ValidationError.js';
import { OkCancelDialog } from '../../../dialog/okcancel/OkCancelDialog.js';

/**
 * @class SqlFieldDialog
 *
 * @extends OkCancelDialog
 */
export class SqlFieldDialog extends OkCancelDialog {
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

