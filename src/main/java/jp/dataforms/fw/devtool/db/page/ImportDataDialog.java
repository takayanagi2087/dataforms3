package jp.dataforms.fw.devtool.db.page;

import jp.dataforms.fw.controller.Dialog;

/**
 * インポートダイアログクラス。
 * <pre>
  * インポートデータが存在するフォルダを指定するフォームです。
* </pre>
 *
 */
public class ImportDataDialog extends Dialog {
	/**
	 * コンストラクタ。
	 */
	public ImportDataDialog() {
		super("importDataDialog");
		this.addForm(new ImportDataForm());
	}
}
