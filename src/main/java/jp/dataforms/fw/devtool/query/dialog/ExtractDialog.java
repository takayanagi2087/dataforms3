package jp.dataforms.fw.devtool.query.dialog;

import jp.dataforms.fw.dialog.okcancel.OkCancelDialog;

/**
 * ExtractFieldフォーム。
 */
public class ExtractDialog extends OkCancelDialog {

	/**
	 * コンストラクタ。
	 */
	public ExtractDialog() {
		super(null);
		this.addForm(new ExtractForm());
	}
}
