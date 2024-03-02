
'use strict';

/**
 * メニューフォーム.
 */
class MenuForm extends Form {
	/**
	 *
	 */
	attach() {
		super.attach(this);
	}

	/**
	 * メニュー項目を更新する.
	 */
	async update() {
		let thisForm = this;
		let method = this.getWebMethod("getMenu");
		let ret = await method.execute("");
		if (ret.status == JsonResponse.SUCCESS) {
			thisForm.menu.menuGroupList = ret.result.menuGroupList;
			thisForm.menu.update();
		}
	}
}


