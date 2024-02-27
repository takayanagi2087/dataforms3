/**
 * @fileOverview {@link DeveloperEditForm}クラスを記述したファイルです。
 */

'use strict';

/**
 * @class DeveloperEditForm
 *
 * @extends EditForm
 */
class DeveloperEditForm extends EditForm {
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
				this.get("userInfoTable").hide();
			} else {
				this.get("userInfoTable").show();
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
				this.get("userInfoTable").hide();
			} else {
				this.get("userInfoTable").show();
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


