/**
 * @fileOverview {@link TableOrSubQueryClassNameField}クラスを記述したファイルです。
 */

'use strict';

/**
 * @class TableOrSubQueryClassNameField
 *
 * @extends SimpleClassNameField
 */
class TableOrSubQueryClassNameField extends SimpleClassNameField {
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

