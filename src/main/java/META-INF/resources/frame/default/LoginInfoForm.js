/**
 * @fileOverview {@link LoginInfoForm}クラスを記述したファイルです。
 */

'use strict';

/**
 * @class LoginInfoForm
 *
 * ユーザ情報フォーム。
 * <pre>
 * ログインしているユーザの情報を表示するフォームです。
 * </pre>
 * @extends Form
 */
class LoginInfoForm extends Form {
	/**
	 * ログイン状態の更新.
	 */
	update() {
		var thisForm = this;
		var method = this.getServerMethod("getUserInfo");
		method.execute("", function (ret) {
			if (ret.status == ServerMethod.SUCCESS) {
				if (ret.result.loginId != null) {
					thisForm.setFormData(ret.result);
					thisForm.get("underLoginDiv").show();
					thisForm.get("dontLoginDiv").hide();
				} else {
					thisForm.get("underLoginDiv").hide();
					thisForm.get("dontLoginDiv").show();
				}
			}
		});
	}

	/**
	 * ログアウト処理.
	 */
	logout() {
		var thisForm = this;
		var method = this.getServerMethod("logout");
		method.execute("", function(ret) {
			currentPage.toTopPage();
		});
	}

	/**
	 * ページの各エレメントとの対応付け.
	 */
	attach() {
		super.attach(this);
		var form = this;
		var thisPage = this.parent;
		form.get('loginButton').click(function () {
			thisPage.showLoginDialog();
		});
		if (form.userRegistPage != null) {
			form.get('regUserButton').click(function() {
				window.location.href = thisPage.contextPath + form.userRegistPage + "." + currentPage.pageExt;
			});
		} else {
			form.get('regUserButton').remove();
		}
		form.get('logoutButton').click(function() {
			form.logout();
		});
		this.update();
	}
}


