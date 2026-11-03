package jp.dataforms.fw.devtool.query.dialog;

import jp.dataforms.fw.dialog.okcancel.OkCancelDialog;

/**
 * SqlFieldダイアログ。
 */
public class SqlFieldDialog extends OkCancelDialog {
	/**
	 * コンストラクタ。
	 */
	public SqlFieldDialog() {
		super(null);
		this.addForm(new SqlFieldForm());
	}
}
