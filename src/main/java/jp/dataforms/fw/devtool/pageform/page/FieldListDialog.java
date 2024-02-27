package jp.dataforms.fw.devtool.pageform.page;

import jp.dataforms.fw.controller.Dialog;

/**
 * フィールドリスト設定ダイアログ。
 */
public class FieldListDialog extends Dialog {
	/**
	 * コンストラクタ。
	 */
	public FieldListDialog() {
		this(null);
	}

	/**
	 * コンストラクタ。
	 * @param id ダイアログID。
	 */
	public FieldListDialog(final String id) {
		super(id);
		this.addForm(new FieldListForm());
	}
}
