package jp.dataforms.fw.dialog.okcancel;

import jp.dataforms.fw.controller.Dialog;

/**
 * Ok Cancelボタンを持ったダイアログクラス。
 */
public abstract class OkCancelDialog extends Dialog {
	/**
	 * コンストラクタ。
	 * @param id　ダイアログID。
	 */
	public OkCancelDialog(String id) {
		super(id);
	}
}
