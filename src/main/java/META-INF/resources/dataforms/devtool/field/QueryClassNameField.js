/**
 * @fileOverview {@link QueryClassNameField}クラスを記述したファイルです。
 */

'use strict';

import { SimpleClassNameField } from "./SimpleClassNameField.js";

/**
 * @class QueryClassNameField
 *
 * @extends SimpleClassNameField
 */
export class QueryClassNameField extends SimpleClassNameField {
	/**
	 * HTMLエレメントとの対応付けを行います。
	 */
	attach() {
		super.attach();
	}

	/**
	 * 関連フィールドの更新。
	 */
	onUpdateRelationField() {
		super.onUpdateRelationField();
		if (this.get().val().length != 0) {
			let form = this.getParentForm();
			if (typeof form.getSql == "function") {
				form.getSql();
			}
		}
	}
}


