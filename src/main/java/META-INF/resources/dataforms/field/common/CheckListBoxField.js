/**
 * @fileOverview {@link CheckListBoxField}クラスを記述したファイルです。
 */

'use strict';

import { MultiSelectField } from './MultiSelectField.js';


/**
 * @class CheckListBoxField
 * 複数選択リストフィールドクラス。
 * <pre>
 * </pre>
 * @extends MultiSelectField
 */
export class CheckListBoxField extends MultiSelectField {

	/**
	 * オプションを設定します。
	 * @param {Array} opt オプション情報。
	 */
	setOptionList(opt) {
		let f = this.getParentForm();
		let span = f.find("#" + this.selectorEscape(this.id + "[0]")).parents("span:first");
		let div = span.parents(":first");
		span.wrap("<div class='checkboxList' style='width: 100%; height: " + (div.innerHeight() - 5) + "px !important; overflow-y: scroll; border-style: solid; border-width: 1px;'></div>")
		super.setOptionList(opt);
		let first = true;
		f.find("[name='" + this.id + "']").each((_, el) => {
			if (first) {
				first = false;
			} else {
				$(el).before("<br>");
			}
		});

	}
}

