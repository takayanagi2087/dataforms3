/**
 * @fileOverview {@link UserEditForm}クラスを記述したファイルです。
 */

'use strict';

import { EditForm } from '../../../controller/EditForm.js';

/**
 * @class UserEditForm
 *
 * @extends EditForm
 */
export class UserEditForm extends EditForm {
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

	toEditMode() {
		super.toEditMode();
		if (this.saveMode == "new") {
			this.find(".passwordTr").show();
		} else {
			if (this.userPasswordType == "IRREVERSIBLE_PASSWORD") {
				this.find(".passwordTr").hide();
			} else {
				this.find(".passwordTr").show();
			}
		}
	}

	/**
	 * データの設定。
	 * @param {Object} data 表示データ。
	 */
	setFormData(data) {
		logger.log("data=", data);
		super.setFormData(data);
	}
}

