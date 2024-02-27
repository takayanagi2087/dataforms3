/**
 * @fileOverview {@link MultiSelectField}クラスを記述したファイルです。
 */

'use strict';

/**
 * @class MultiSelectField
 * 複数選択リストフィールドクラス。
 * <pre>
 * </pre>
 * @extends SelectField
 */
class MultiSelectField extends SelectField {
	/**
	 * 対応するコンポーネントに値を設定します。
	 * <pre>
	 * 対応するのは、マルチ選択可能なselectまたは同一nameを持つチェックボックスです。
	 * </pre>
	 * @param {Array} value 値。
	 *
	 */
	setValue(value) {
		let comp = this.get();
		if (comp.length > 0) {
			let tag = comp.prop("tagName");
			let type = comp.prop("type");
			if ("INPUT" == tag && type == "checkbox") {
				// checkboxの対応.
				comp.each((_, el) => {
					let v = $(el).val();
					$(el).prop("checked", false);
					if (value != null) {
						for (let i = 0; i < value.length; i++) {
							if (v == value[i]) {
								$(el).prop("checked", true);
							}
						}
					}
				});
			} else if ("SELECT" == tag) {
				// マルチ選択リストボックスの設定.
				let opt = comp.find("option");
				opt.each((_, el) => {
					let v = $(el).val();
					$(el).prop("selected", false);
					if (value != null) {
						for (let i = 0; i < value.length; i++) {
							if (v == value[i]) {
								$(el).prop("selected", true);
							}
						}
					}
				});
			} else {
				this.setTextValue(comp, value);
			}
		}
	}

	/**
	 * SPAN等の表示用タグへ値を設定します。
	 * @param {jQuery} comp コンポーネント。
	 * @param {Array} value 値。
	 */
	setTextValue(comp, value) {
		let v = "";
		if (value != null) {
			for (let i = 0; i < value.length; i++) {
				if (v.length > 0) {
					v += ",";
				}
				let iv = value[i];
				for (let j = 0; j < this.optionList.length; j++) {
					if (value[i] == this.optionList[j].value) {
						iv = this.optionList[j].name;
						break;
					}
				}
				v += iv;
			}
		}
		comp.text(v);
	}

	/**
	 * 値を取得します。
	 * @return {String} 値。
	 */
	getValue() {
		let comp = this.get();
		let tag = comp.prop("tagName");
		let type = comp.prop("type");
		let ret = [];
		if ("INPUT" == tag && "checkbox" == type) {
			comp.each((_, el) => {
				if ($(el).prop("checked")) {
					ret.push($(el).val());
				}
			});
		} else {
			ret = comp.val();
		}
		return ret;
	}
}




