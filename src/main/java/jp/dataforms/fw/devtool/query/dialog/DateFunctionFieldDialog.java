package jp.dataforms.fw.devtool.query.dialog;

import jp.dataforms.fw.dialog.okcancel.OkCancelDialog;

/**
 * DateFunctionFieldDialogフォーム。
 */
public class DateFunctionFieldDialog extends OkCancelDialog {
	/**
	 * コンストラクタ。
	 */
	public DateFunctionFieldDialog() {
		super(null);
		this.addForm(new DateFunctionFieldForm());
	}
}
