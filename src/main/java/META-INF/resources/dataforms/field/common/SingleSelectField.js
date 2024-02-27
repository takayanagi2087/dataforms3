/**
 * @fileOverview {@link SingleSelectField}クラスを記述したファイルです。
 */

'use strict';

/**
 * @class SingleSelectField
 * 単一選択リストクラス。
 * <pre>
 * </pre>
 * @extends SelectField
 */
class SingleSelectField extends SelectField {
	/**
	 * コンストラクタ。
	 */
	constructor() {
		super();
	}

	/**
	 * INPUTまたはSELECTタグへ値を設定します。
	 * @param {jQuery} comp 値を設定するコンポーネント。
	 * @param {String} value 値。
	 */
	setInputValue(comp, value) {
		let tag = comp.prop("tagName");
		let type = comp.prop("type");
		if ("INPUT" == tag && "radio" == type) {
			comp.each((_, el) => {
				if ($(el).val() == value) {
					$(el).prop("checked", true);
				}
			});
		} else {
			comp.val(value);
		}
	}

	/**
	 * 値を取得します。
	 * @return {String} 値。
	 */
	getValue() {
		let comp = this.get();
		let tag = comp.prop("tagName");
		let type = comp.prop("type");
		let ret = "";
		if ("INPUT" == tag && "radio" == type) {
			comp.each((_, el) => {
				if ($(el).prop("checked")) {
					ret = $(el).val();
				}
			});
		} else {
			ret = comp.val();
		}
		return ret;
	}

}


