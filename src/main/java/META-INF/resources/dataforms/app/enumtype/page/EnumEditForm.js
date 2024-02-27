/**
 * @fileOverview {@link EnumEditForm}クラスを記述したファイルです。
 */

'use strict';

/**
 * @class EnumEditForm
 *
 * @extends EditForm
 */
class EnumEditForm extends EditForm {
	/**
	 * コンストラクタ。
	 */
	constructor() {
		super();
	}

	/**
	 * 言語に応じた名称フィールドを追加します。
	 */
	remodelHtml() {
		let typeName = this.find("tr.typeName");
		let hhtml = typeName.prop("outerHTML");
		logger.log("hhtml=" + hhtml);
		let ohName = this.find("#optionTable thead th.optionName");
		let ohhtml = ohName.prop("outerHTML");
		let tdName = this.find("#optionTable tbody td.optionName");
		let tdhtml = tdName.prop("outerHTML");
		for (let i = 0; i < this.langList.length; i++) {
			// 各言語の列挙型名称フィールドを展開
			let nm = hhtml.replace("default", this.langList[i]);
			let fid = this.langList[i] + "EnumName";
			nm = nm.replace(/enumName/g, fid);
			typeName.after(nm);
			// 各言語の列挙型選択肢名称ヘッダを展開
			let oh = ohhtml.replace(/default/g, this.langList[i]);
			ohName.after(oh);
			// 各言語の列挙型選択肢名称フィールドを展開
			let td = tdhtml.replace(/enumName/g, fid);
			tdName.after(td);
		}
		this.find("#optionTable th.footer").attr("colspan", "" + (5 + this.langList.length));
	}

	/**
	 * HTMLエレメントとの対応付けを行います。
	 */
	attach() {
		super.attach();
	}
}

