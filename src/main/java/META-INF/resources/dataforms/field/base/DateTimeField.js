/**
 * @fileOverview {@link DateTimeField}クラスを記述したファイルです。
 */

'use strict';

/**
 * @class DateTimeField
 *
 * 日付/時刻フィールドクラス。
 * <pre>
 * 日付/時刻フィールドの基底クラスです。
 * </pre>
 * @extends Field
 */
class DateTimeField extends Field {
	/**
	 * コンストラクタ。
	 */
	constructor() {
		super();
		this.displayFormat = null;
		this.editFormat = null;
	}

	/**
	 * 日付の表示フォーマットを指定します。
	 * @param {String} displayFormat 表示時のフォーマット。
	 * @param {String} editFormat 編集時のフォーマット。
	 */
	setFormat(displayFormat, editFormat) {
		this.displayFormat = displayFormat;
		this.editFormat = editFormat;
		this.get().focus((ev) => {
			this.toEditFormat($(ev.currentTarget));
		});
		this.get().blur((ev) => {
			this.toDisplayFormat($(ev.currentTarget));
		});
	}

	/**
	 * 日付を編集フォーマットに変更します。
	 * @param {jQuery} f テキストフィールド。
	 */
	toEditFormat(f) {
		let v = f.val();
		let fmt = new SimpleDateFormat(this.displayFormat);
		let efmt = new SimpleDateFormat(this.editFormat);
		let ev = fmt.parse(v);
		if (ev != null) {
			f.val(efmt.format(ev));
		}
	}


	/**
	 * 日付を表示フォーマットに変更します。
	 * @param {jQuery} f テキストフィールド。
	 */
	toDisplayFormat(f) {
		let v = f.val();
		let fmt = new SimpleDateFormat(this.displayFormat);
		let efmt = new SimpleDateFormat(this.editFormat);
		let ev = efmt.parse(v);
		if (ev != null) {
			f.val(fmt.format(ev));
		}
	}

	/**
	 * 親フォームのonCalcメソットを呼び出します。
	 * @param {jQuery} f フィールド。
	 */
	callOnCalc(f) {
		this.toDisplayFormat(f);
		super.callOnCalc(f);
	}


	/**
	 * 値を設定します。
	 * @param {String} date 値。
	 */
	setValue(date) {
		if (date instanceof Date) {
			let fmt = new SimpleDateFormat(this.displayFormat);
			super.setValue(fmt.format(date));
		} else {
			super.setValue(date);
		}
	}

	/**
	 * 値を取得します。
	 * @returns {Date} 値(日付形式)。
	 */
	getValue() {
		let v = super.getValue();
		if (v != null && v.length > 0) {
			logger.log("getValue()=" + v);
			let dfmt = new SimpleDateFormat(this.displayFormat);
			let ret = dfmt.parse(v);
			if (ret == null) {
				let efmt = new SimpleDateFormat(this.editFormat);
				ret = efmt.parse(v);
			}
			return ret;
		} else {
			return null;
		}
	}
}

