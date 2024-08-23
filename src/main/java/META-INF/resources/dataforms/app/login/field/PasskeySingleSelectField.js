/**
 * @fileOverview {@link PasskeySingleSelectField}クラスを記述したファイルです。
 */

'use strict';

import { SingleSelectField } from '../../../field/common/SingleSelectField.js';
import { JsonResponse } from '../../../response/JsonResponse.js';
import { MessagesUtil } from '../../../util/MessagesUtil.js';

/**
 * @class PasskeySingleSelectField
 *
 * @extends SingleSelectField
 */
export class PasskeySingleSelectField extends SingleSelectField {
	/**
	 * コンストラクタ。
	 */
	constructor() {
		super();
	}

	/**
	 * HTMLエレメントとの対応付けを行います。
	 */
	attach() {
		super.attach();
	}

	/**
	 * サーバーからパスキーのリストを取得します。
	 * @param {String} loginId ログインID。
	 */
	async getPasskeyList(loginId) {
		try {
			let method = this.getWebMethod("getPasskeyList");
			let r = await method.execute("loginId=" + loginId);
			if (r.status == JsonResponse.SUCCESS) {
				this.setOptionList(r.result);
				if (r.result.length > 0) {
					this.get().val(r.result[0].value);
				}
			}
			logger.dir(r);
		} catch (e) {
			currentPage.reportError(e);
		}
	}

}

