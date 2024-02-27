package jp.dataforms.fw.dialog.image;

import jp.dataforms.fw.controller.Dialog;

/**
 * 画像表示ダイアログ。
 *
 */
public class ImageDialog extends Dialog {
	/**
	 * ダイアログ。
	 */
	public ImageDialog() {
		super("imageDialog");
		this.addForm(new ImageForm());
	}
}
