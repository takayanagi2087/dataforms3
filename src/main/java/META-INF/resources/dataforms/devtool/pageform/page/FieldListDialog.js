/**
 * @fileOverview {@link FieldListDialog}クラスを記述したファイルです。
 */

'use strict';

/**
 * @class FieldListDialog
 *
 * @extends Dialog
 */
class FieldListDialog extends Dialog {
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
	 * ダイアログを表示します。
	 * @param {Boolean} modal モーダル表示の場合true。
	 * @param {Object} p 追加プロパティ。
	 *
	 */
	show(modal, p) {
		super.show(modal, p);
		let fieldListForm = this.getComponent("fieldListForm");
		logger.log("fieldListForm=", fieldListForm);
		logger.log("p=", p);
		fieldListForm.getFieldList(p);
	}
}

