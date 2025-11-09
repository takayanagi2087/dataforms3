package jp.dataforms.fw.dbtool.db.page;

import jp.dataforms.fw.controller.Dialog;

/**
 * テーブル情報ダイアログクラス。
 */
public class TableInfoDialog extends Dialog {

	/**
	 * コンストラクタ。
	 */
	public TableInfoDialog() {
		super(null);
		this.addForm(new TableInfoForm());
	}

}
