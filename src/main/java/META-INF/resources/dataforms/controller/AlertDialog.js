/**
 * @fileOverview {@link AlertDialog}クラスを記述したファイルです。
 */

'use strict';

/**
 * @class AlertDialog
 *
 * @extends Dialog
 */
class AlertDialog extends Dialog {
	/**
	 * コンストラクタ。
	 */
	constructor() {
		super();
		this.title = null;
		this.message = null;
		this.okFunc = null;
	}
	/**
	 * HTMLエレメントとの対応付けを行います。
	 */
	attach() {
		super.attach();
		this.get("alertOkButton").click(() => {
			this.close();
			if (this.okFunc != null) {
				this.okFunc.call(this);
			}
			return false;
		});
	}

	/**
	 * ダイアログを表示します。
	 * @param {Boolean} modal モーダル表示の場合true。
	 * @param {Object} p 追加プロパティ。
	 *
	 */
	show(modal, p) {
		this.get("alertMessage").html(this.message);
		super.show(modal, p);
	}
}


