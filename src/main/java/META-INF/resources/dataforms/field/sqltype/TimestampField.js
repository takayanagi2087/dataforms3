
/**
 * @fileOverview {@link TimestampField}クラスを記述したファイルです。
 */

'use strict';

/**
 * @class TimestampField
 * Timestamp型フィールドクラス。
 * <pre>
 * </pre>
 * @extends DateTimeField
 */
class TimestampField extends DateTimeField {
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
	attach() {
		super.attach();
		let comp = this.get();
		if (!comp.prop("readonly")) {
			this.setFormat(this.displayFormat, this.editFormat);
		}
		this.backupStyle();
	}
}

