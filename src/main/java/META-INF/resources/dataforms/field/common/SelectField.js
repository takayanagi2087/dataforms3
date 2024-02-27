/**
 * @fileOverview {@link SelectField}クラスを記述したファイルです。
 */

'use strict';

/**
 * @class SelectField
 * 選択肢フィールドクラス。
 * <pre>
 * 各種選択肢フィールドの基底クラスです。
 * </pre>
 * @extends Field
 * @prop {Array} optionList 選択肢リスト。
 *
 */
class SelectField extends Field {
	/**
	 * コンストラクタ。
	 */
	constructor() {
		super();
	}

	/**
	 * HTMLの要素との対応付けを行ないます。
	 * <pre>
	 * setOptionListを呼び出し、選択肢を設定します。
	 * </pre>
	 */
	attach() {
		super.attach();
		this.setOptionList();
		if (this.calcEventField) {
			let comp = this.get();
			let tag = comp.prop("tagName");
			let type = comp.prop("type");
			if ("INPUT" == tag && (type.toLowerCase() == "radio" || type.toLowerCase() == "checkbox")) {
				comp.click(() => {
					let form = this.getParentForm();
					form.onCalc($(ev.currentTarget));
				});
			}
		}
	}

	/**
	 * 対応するHTMLの要素を取得する.
	 * @returns {jQuery} 対応するHTMLエレメント。
	 */
	get() {
		let el = super.get();
		if (el.length == 0) {
			el = this.parent.find("[id^='" + this.selectorEscape(this.id + "[") + "']");
		}
		if (el.length == 0) {
			el = this.parent.find("[name='" + this.selectorEscape(this.id) + "']");
		}
		return el;
	}

	/**
	 * 値に対応する選択肢の名称を取得します。
	 * @param {String} value 値。
	 * @return 名称。
	 */
	getOptionName(value) {
		let ret = "";
		if (value != null && this.optionList != null) {
			for (let i = 0; i < this.optionList.length; i++) {
				let opt = this.optionList[i];
				let ov = (opt.value == null ? "" : opt.value.toString());
				let v = value.toString();
				if (ov == v) {
					ret = opt.name;
					break;
				}
			}
		}
		return ret;
	}

	/**
	 * SPAN等の表示用タグへ値を設定します。
	 * <pre>
	 * 選択肢の場合は値ではなく名称を設定します。
	 * </pre>
	 * @param {jQuery} comp コンポーネント。
	 * @param {String} value 値。
	 */
	setTextValue(comp, value) {
		if (this.optionList != null) {
			if (value == null) {
				comp.text("");
				return;
			}
			for (let i = 0; i < this.optionList.length; i++) {
				let opt = this.optionList[i];
				let ov = (opt.value == null ? "" : opt.value.toString());
				let v = value.toString();
				if (ov == v) {
					comp.text(opt.name);
					break;
				}
			}
		} else {
			if (value != null) {
				comp.text(value);
			} else {
				comp.text("");
			}
		}
	}

	/**
	 * 選択肢を対応する要素に設定します。
	 * @param {Array} opt 選択肢のリスト。
	 */
	setOptionList(opt) {
		if (opt != null) {
			this.optionList = opt;
		}
		if (this.optionList == null) {
			return;
		}
		let el = this.get();
		if (el.length > 0) {
			if (el.prop("tagName") == "SELECT") {
				let opthtml = "";
				if (this.blankOption) {
					opthtml += "<option value=''></option>";
				}
				for (let i = 0; i < this.optionList.length; i++) {
					let opt = this.optionList[i];
					opthtml += "<option value='" + opt.value + "'>" + opt.name + "</option>";
				}
				el.html(opthtml);
			} else if (el.prop("tagName") == "INPUT") {
				if (el.attr("type").toLowerCase() == "radio" ) {
					let pl = el.parent();
					pl.html("");
					let opthtml = "";
					for (let i = 0; i < this.optionList.length; i++) {
						let opt = this.optionList[i];
						if (currentPage.useUniqueId) {
							opthtml +=
								"<input type='radio' id='" + this.realId + "[" + i + "]' data-id='" + this.id + "[" + i + "]' name='" + this.id + "' value='" + opt.value + "'/>"
									+ "<label for='" + this.realId + "[" + i + "]'>" + opt.name + "</label>&nbsp;";
						} else {
							opthtml +=
								"<input type='radio' id='" + this.id + "[" + i + "]' name='" + this.id + "' value='" + opt.value + "'/>"
									+ "<label for='" + this.id + "[" + i + "]'>" + opt.name + "</label>&nbsp;";
						}
					}
					pl.html(opthtml);
				} else if (el.attr("type").toLowerCase() == "checkbox" ) {
					let pl = el.parent();
					pl.html("");
					let opthtml = "";
					for (let i = 0; i < this.optionList.length; i++) {
						let opt = this.optionList[i];
						if (currentPage.useUniqueId) {
							opthtml +=
								"<input type='checkbox' id='" + this.realId  + "[" + i + "]' data-id='" + this.id + "[" + i + "]' name='" + this.id + "' value='" + opt.value + "'/>"
									+ "<label for='" + this.realId + "[" + i + "]'>" + opt.name + "</label>&nbsp;";
						} else {
							opthtml +=
								"<input type='checkbox' id='" + this.id + "[" + i + "]' name='" + this.id + "' value='" + opt.value + "'/>"
									+ "<label for='" + this.id + "[" + i + "]'>" + opt.name + "</label>&nbsp;";
						}
					}
					pl.html(opthtml);
				}
			}
		}
	}
}

