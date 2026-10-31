/**
 * @fileOverview {@link ExtractDialog}クラスを記述したファイルです。
 */

'use strict';

// import { JsonResponse } from '../../../response/JsonResponse.js';
// import { MessagesUtil } from '../../../util/MessagesUtil.js';
// import { ValidationError } from '../../../validator/ValidationError.js';
import { OkCancelDialog } from '../../../dialog/okcancel/OkCancelDialog.js';

/**
 * @class ExtractDialog
 *
 * @extends OkCancelDialog
 */
export class ExtractDialog extends OkCancelDialog {
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
	
	async doModal(p) {
		let frm = this.getOkCancelForm();
		let fldsel = frm.getComponent("refFieldId");
		fldsel.setOptionList(p.optlist);
		return await super.doModal(p);
	}
}

