/**
 * @fileOverview {@link ConfirmDialog}クラスを記述したファイルです。
 */

'use strict';

/**
 * @class ConfirmDialog
 *
 * @extends Dialog
 */
class ConfirmDialog extends Dialog {
	/**
	 * コンストラクタ。
	 */
	constructor() {
		super();
		this.title = null;
		this.message = null;
		this.okFunc =  null;
		this.cancelFunc = null;
	}

	/**
	 * HTMLエレメントとの対応付けを行います。
	 */
	attach() {
		super.attach();
		this.get("confirmOkButton").click(() => {
			this.close();
			if (this.okFunc != null) {
				this.okFunc.call(this);
			}
			return false;
		});
		this.get("confirmCancelButton").click(() => {
			this.close();
			if (this.cancelFunc != null) {
				this.cancelFunc.call(this);
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
		this.get("confirmMessage").html(this.message);
		super.show(modal, p);
	}
}


