/**
 * @fileOverview {@link LoginInfoForm}クラスを記述したファイルです。
 */

'use strict';
import { Form } from '../../../controller/Form.js';
import { JsonResponse } from '../../../response/JsonResponse.js';
/**
 * @class LoginInfoForm
 *
 * ユーザ情報フォーム。
 * <pre>
 * ログインしているユーザの情報を表示するフォームです。
 * </pre>
 * @extends Form
 */
export class LoginInfoForm extends Form {
	/**
	 * ログイン状態の更新.
	 */
	async update() {
		let thisForm = this;
		let method = this.getWebMethod("getUserInfo");
		let ret = await method.execute("");
		if (ret.status == JsonResponse.SUCCESS) {
			if (ret.result.loginId != null) {
				thisForm.setFormData(ret.result);
				thisForm.get("underLoginDiv").show();
				thisForm.get("dontLoginDiv").hide();
			} else {
				thisForm.get("underLoginDiv").hide();
				thisForm.get("dontLoginDiv").show();
			}
		}
	}

	/**
	 * ログアウト処理.
	 */
	async logout() {
		try {
			let method = this.getWebMethod("logout");
			await method.execute("");
			currentPage.toTopPage();
		} catch (e) {
			currentPage.reportError(e);
		}
	}

	/**
	 * ページの各エレメントとの対応付け.
	 */
	attach() {
		super.attach();
		if (this.userRegistPage != null) {
			this.get('regUserButton').click(() => {
				window.location.href = currentPage.contextPath + this.userRegistPage + "." + currentPage.pageExt;
			});
		} else {
			this.get('regUserButton').remove();
		}
		this.get('logoutButton').click(() => {
			this.logout();
		});
		this.update();
	}
}


