/**
 * @fileOverview {@link QueryOrTableClassNameField}クラスを記述したファイルです。
 */

'use strict';

import { QueryClassNameField } from './QueryClassNameField.js';

/**
 * @class QueryOrTableClassNameField
 *
 * @extends QueryClassNameField
 */
export class QueryOrTableClassNameField extends QueryClassNameField {
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
		logger.debug("QueryOrTableClassNameField");
		logger.dir(this);
	}

	/**
	 * 関連フィールドの更新。
	 */
	onUpdateRelationField() {
		super.onUpdateRelationField();
		let form = this.getParentForm();
		logger.log("form=", form);
		if (typeof form.getFieldConfig == "function") {
			form.getFieldConfig(this.id);
		}
	}
}

