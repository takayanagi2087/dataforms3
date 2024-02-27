/**
 * @fileOverview {@link UserEditForm}クラスを記述したファイルです。
 */

'use strict';

/**
 * @class UserEditForm
 *
 * @extends EditForm
 */
class UserEditForm extends EditForm {
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
			this.find("tr.passwordTr").show();
		} else {
			if (this.userPasswordType == "IRREVERSIBLE_PASSWORD") {
				this.find("tr.passwordTr").hide();
			} else {
				this.find("tr.passwordTr").show();
			}
		}
	}

	/**
	 * データの設定。
	 * @param {Object} data 表示データ。
	 */
	setFormData(data) {
		super.setFormData(data);
	}
}

