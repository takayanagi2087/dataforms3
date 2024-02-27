/**
 * @fileOverview {@link ImageDialog}クラスを記述したファイルです。
 */

'use strict';

/**
 * @class ImageDialog 画像表示ダイアログクラス。
 *
 * @extends Dialog
 */
class ImageDialog extends Dialog {
	/**
	 * HTMLエレメントとの対応付けを行います。
	 */
	attach() {
		super.attach();
	}

	/**
	 * 閉じる際に画像をクリアします。
	 */
	close() {
		let imgfld = this.getComponent("imageForm").getComponent("image");
		imgfld.setValue(null);
		super.close();
	}
}



