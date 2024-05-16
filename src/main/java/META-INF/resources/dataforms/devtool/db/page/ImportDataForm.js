/**
 * @fileOverview {@link ImportDataForm}クラスを記述したファイルです。
 */

'use strict';

import { Form } from '../../../controller/Form.js';

/**
 * @class ImportDataForm
 * テーブル情報フォームクラス。
 * <pre>
 * テーブル情報を表示するためのフォームです。
 * </pre>
 * @extends Form
 */
export class ImportDataForm extends Form {
	/**
	 * HTMLエレメントフォームとの対応付けを行います。
	 *
	 */
	attach() {
		super.attach();
		this.get("importButton").click(() => {
			let rform = currentPage.getComponent("queryResultForm");
			let path = this.getFieldValue("pathName");
			rform.importTableData(path);
			this.parent.close();
		});
	}
}

