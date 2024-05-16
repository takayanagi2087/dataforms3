/**
 * @fileOverview {@link TableClassNameField}クラスを記述したファイルです。
 */

'use strict';

import { SimpleClassNameField } from '../../devtool/field/SimpleClassNameField.js';

/**
 * @class TableClassNameField
 *
 * @extends SimpleClassNameField
 */
export class TableClassNameField extends SimpleClassNameField {
	/**
	 * HTMLエレメントとの対応付けを行います。
	 */
	attach() {
		super.attach();
	}

	/**
	 * テーブルクラス名の更新時の処理。
	 */
	onUpdateRelationField() {
		super.onUpdateRelationField();
		if (this.get().val().length != 0) {
			let form = this.getParentForm();
			if (typeof form.getFieldList == "function") {
				form.getFieldList();
			}
		}
	}

}



