
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
	update() {
		var thisForm = this;
		var method = this.getServerMethod("getMenu");
		method.execute("", function(ret) {
			if (ret.status == ServerMethod.SUCCESS) {
				thisForm.menu.menuGroupList = ret.result.menuGroupList;
				thisForm.menu.update();
			}
		});
	}
}


