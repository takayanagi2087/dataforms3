/**
 * @fileOverview {@link CheckListBoxField}クラスを記述したファイルです。
 */

'use strict';

import { SingleSelectField } from './SingleSelectField.js';


/**
 * @class CheckListBoxField
 * 複数選択リストフィールドクラス。
 * <pre>
 * </pre>
 * @extends SingleSelectField
 */
export class RadioListBoxField extends SingleSelectField {
	
	/**
	 * スクロールリスト用のDIV設定済フラグ。
	 */
	#noListDiv = true;

	/**
	 * 各項目の間に改行を追加します。
	 */
	addBr() {
		let f = this.getParentForm();
		let first = true;
		f.find("[name='" + this.id + "']").each((_, el) => {
			if (first) {
				first = false;
			} else {
				$(el).before("<br>");
			}
		});
	}
	
	
	/**
	 * オプションを設定します。
	 * @param {Array} opt オプション情報。
	 */
	setOptionList(opt) {
		if (opt != null) {
			this.optionList = opt;
		}
		let hide = false;
		if (this.optionList == null || this.optionList.length == 0) {
			this.optionList = [];
			this.optionList.push({value:"", name: ""});
			hide = true;
		}
		let f = this.getParentForm();
		if (this.#noListDiv) {
			let span = f.find("#" + this.selectorEscape(this.id + "[0]")).parents("span:first");
			let div = span.parents(":first");
			span.wrap("<div class='radioList' style='height: " + (div.innerHeight() - 5) + "px !important;'></div>")
			this.#noListDiv = false;
		}
		this.setRadioOptionList();
		if (hide) {
			f.find("[name='" + this.id + "']").hide();
		} else {
			this.addBr();
		}
	}
}

