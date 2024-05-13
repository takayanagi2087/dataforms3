
'use strict';

import { Form } from '../../../controller/Form.js';

/**
 * メニューフォーム.
 */
export class MenuForm extends Form {
	/**
	 *
	 */
	async attach() {
		await super.attach(this);
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


