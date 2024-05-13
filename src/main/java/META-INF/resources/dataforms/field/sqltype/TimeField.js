
/**
 * @fileOverview {@link TimeField}クラスを記述したファイルです。
 */

'use strict';

import { DateTimeField } from '../base/DateTimeField.js';

/**
 * @class TimeField
 * Time型フィールドクラス。
 * <pre>
 * </pre>
 * @extends DateTimeField
 */
export class TimeField extends DateTimeField {
	/**
	 * コンストラクタ。
	 */
	constructor() {
		super();
		this.displayFormat = MessagesUtil.getMessage("format.timefield");
		this.editFormat = MessagesUtil.getMessage("editformat.timefield");
	}

	/**
	 * HTMLエレメントとの対応付けを行います。
	 */
	async attach() {
		await super.attach();
		let comp = this.get();
		if (!comp.prop("readonly")) {
			this.setFormat(this.displayFormat, this.editFormat);
		}
		this.backupStyle();
	};
}

