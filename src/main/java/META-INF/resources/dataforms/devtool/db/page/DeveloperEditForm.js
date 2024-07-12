/**
 * @fileOverview {@link DeveloperEditForm}クラスを記述したファイルです。
 */

'use strict';

import { EditForm } from '../../../controller/EditForm.js';

/**
 * @class DeveloperEditForm
 *
 * @extends EditForm
 */
export class DeveloperEditForm extends EditForm {
	/**
	 * HTMLエレメントとの対応付けを行います。
	 */
	attach() {
		super.attach();
		if (this.userInfoDataExists) {
			this.get("flagDiv").show();
		} else {
			this.get("flagDiv").hide();
		}
		this.get("userImportFlag").click((ev) => {
			if ($(ev.currentTarget).prop("checked")) {
				this.find(".userInfo").hide();
			} else {
				this.find(".userInfo").show();
			}
		});
	}

	/**
	 * フォームデータを設定します。
	 */
	setFormData(data) {
		super.setFormData(data);
		if (this.userInfoDataExists) {
			if (data.userImportFlag == "1") {
				this.find(".userInfo").hide();
			} else {
				this.find(".userInfo").show();
			}
		}
	}

	/**
	 * フォームのチェックを行います。
	 *
	 */
	validate() {
		if (this.get("userImportFlag").prop("checked")) {
			return true;
		} else {
			return super.validate();
		}
	}
}


