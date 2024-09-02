/**
 * @fileOverview {@link PasswordCheckForm}クラスを記述したファイルです。
 */

'use strict';

import { Form } from '../../../controller/Form.js';
import { JsonResponse } from '../../../response/JsonResponse.js';

/**
 * @class PasswordCheckForm
 *
 * @extends Form
 */
export class PasswordCheckForm extends Form {
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
		this.get("checkPasswordButton").click(() => {
			this.checkPassword();
			return false;
		});
	}
	
	/**
	 * パスワードの確認。
	 */
	async checkPassword() {
		try {
			if (this.validate()) {
				let r = await this.submit("checkPassword");
				this.parent.resetErrorStatus();
				logger.log("r=", r);
				if (r.status == JsonResponse.SUCCESS) {
					eval(this.onOkScript);
				} else {
					this.parent.setErrorInfo(this.getValidationResult(r), this);
				}
			}
		} catch (e) {
			currentPage.reportError(e);
		}
	}
}

