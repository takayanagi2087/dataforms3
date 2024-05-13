
/**
 * @fileOverview {@link TimestampField}クラスを記述したファイルです。
 */

'use strict';

import { DateTimeField } from '../base/DateTimeField.js';

/**
 * @class TimestampField
 * Timestamp型フィールドクラス。
 * <pre>
 * </pre>
 * @extends DateTimeField
 */
export class TimestampField extends DateTimeField {
	/**
	 * コンストラクタ。
	 */
	constructor() {
		super();
		this.displayFormat = MessagesUtil.getMessage("format.timestampfield");
		this.editFormat = MessagesUtil.getMessage("editformat.timestampfield");
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
	}
}

