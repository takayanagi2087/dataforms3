/**
 * @fileOverview {@link MenuEditForm}クラスを記述したファイルです。
 */

'use strict';

import { EditForm } from "../../../controller/EditForm.js";

/**
 * @class MenuEditForm
 *
 * @extends EditForm
 */
export class MenuEditForm extends EditForm {
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


	/**
	 * 編集モードにします。
	 * <pre>
	 * 各フィールドを編集可能状態にします。
	 * </pre>
	 */
	toEditMode() {
		super.toEditMode();
		let table = this.getComponent("menuList");
		table.lockDataformsMenu();
	}
}

