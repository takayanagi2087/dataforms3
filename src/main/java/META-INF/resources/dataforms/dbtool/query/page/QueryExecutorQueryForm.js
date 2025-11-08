/**
 * @fileOverview {@link QueryExecutorQueryForm}クラスを記述したファイルです。
 */

'use strict';

import { JsonResponse } from '../../../response/JsonResponse.js';
import { QueryForm } from '../../../controller/QueryForm.js';

/**
 * @class QueryExecutorQueryForm
 *
 * @extends QueryForm
 */
export class QueryExecutorQueryForm extends QueryForm {
	/**
	 * HTMLエレメントとの対応付けを行います。
	 */
	attach() {
		super.attach();
	}

	/**
	 * Queryクラスに対応するSQLを取得します。
	 */
	async getSql() {
		try {
			let r = await this.submit("getSql");
			if (r.status == JsonResponse.SUCCESS) {
				this.get("sql").val(r.result);
			}
		} catch (e) {
			currentPage.reportError(e);
		}
	}
}


