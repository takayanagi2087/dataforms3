
/**
 * @fileOverview {@link TimeField}クラスを記述したファイルです。
 */

'use strict';

/**
 * @class TimeField
 * Time型フィールドクラス。
 * <pre>
 * </pre>
 * @extends DateTimeField
 */
class TimeField extends DateTimeField {
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
	attach() {
		super.attach();
		let comp = this.get();
		if (!comp.prop("readonly")) {
			this.setFormat(this.displayFormat, this.editFormat);
		}
		this.backupStyle();
	};
}

