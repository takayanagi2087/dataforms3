/**
 * @fileOverview {@link CalendarField}クラスを記述したファイルです。
 */

'use strict';

import { Field } from '../field/base/Field.js';
import { JsonResponse } from '../response/JsonResponse.js';
import { SimpleDateFormat } from '../util/SimpleDateFormat.js';
import { MessagesUtil } from '../util/MessagesUtil.js';

/**
 * @class CalendarField
 *
 * @extends Field
 */
export class CalendarField extends Field {
	/**
	 * 日付の値。
	 */
	#dateValue = null;
	
	/**
	 * 日付フォーマット。
	 */
	#dateFormat = null;
	
	/**
	 * コンストラクタ。
	 */
	constructor() {
		super();
		this.#dateFormat = MessagesUtil.getMessage("format.datefield");
	}

	/**
	 * HTMLエレメントとの対応付けを行います。
	 */
	attach() {
		super.attach();
		logger.log("id=" + this.id);
		logger.log("calendarHtml=" + this.calendarHtml);
		logger.log("calendar=" + this.realId);
		this.get().html(this.calendarHtml);
		for (let i = 0; i < this.weekList.length; i++) {
			this.find(".week" + i).text(this.weekList[i]);
		}
		this.find(".prevMonthButton").click(() => {
			this.onPrevMonth();
		});
		this.find(".nextMonthButton").click(() => {
			this.onNextMonth();
		});
//		$("#" + this.selectorEscape(this.get().attr("id"))).html(this.calendarHtml);
//
// フィールドのイベント処理
//
// this.get()でフィールドのjQueryオブジェクトの取得が可能です。
//
// 変更時のイベント処理
/*
		this.get().change(() => {
			// TODO:必要な処理を実装してください。
		});
*/

// クリック時のイベント処理
/*
		this.get().click(() => {
			// TODO:必要な処理を実装してください。
		});
*/
	}
	
	/**
	 * 前の月を表示。
	 */
	async onPrevMonth() {
		let fmt = new SimpleDateFormat(this.#dateFormat);
		let d = fmt.parse(this.#dateValue);
		logger.log("d=", d);
		d.setMonth(d.getMonth() - 1);
		this.setValue(fmt.format(d));
	}
	
	/**
	 * 次の月を表示。
	 */
	async onNextMonth() {
		let fmt = new SimpleDateFormat(this.#dateFormat);
		let d = fmt.parse(this.#dateValue);
		logger.log("d=", d);
		d.setMonth(d.getMonth() + 1);
		this.setValue(fmt.format(d));
	}

	/**
	 * 日付セルに設定するHTMLテキストを取得します。
	 * @param {Number} idx セルインデックス。
	 * @param {Object} dateInfo 日付情報。
	 */	
	getDateCellHtml(idx, dateInfo) {
		let tag = '<span data-id="date' + idx + '" class="date">' + dateInfo.day + '</span><br/>';
		return tag;
	}
	
	
	/**
	 * 日付を設定します。
	 * @param {String} v 日付。
	 */
	async setValue(v) {
		this.#dateValue = v;
		logger.log("date=" + this.#dateValue);
		let m = this.getWebMethod("getCalenderInfo");
		let r = await m.execute("date=" + this.#dateValue);
		logger.log("r=", r);
		if (r.status == JsonResponse.SUCCESS) {
			this.find(".monthYear").text(r.result.monthYear);
			let idx = 0;
			this.find("table.calendarTable tbody tr").each((_, tr) => {
				$(tr).find("td").each((_, td) => {
					let dateInfo = r.result["dateInfo" + idx];
					if (dateInfo != null) {
						let tag = this.getDateCellHtml(idx, dateInfo);
						$(td).html(tag);
					} else {
						$(td).html("");
					}
					idx++;
				});
			});

		}
	}
	
	

	// 独自のWebメソッドを呼び出す場合は、以下のコメントを参考にしてください。
	/**
	 * Webメソッドの呼び出しサンプル。
	 *
	 */
/*
	async callWebMethod() {
		try {
			let method = this.getWebMethod("webMethod");
			let r = await method.execute(this.id + "=" + this.getValue());
			// TODO:応答情報を適切に処理してください。
			logger.dir(r);
		} catch (e) {
			currentPage.reportError(e);
		}
	}
*/

	// フィールドの各種動作をカスタマイズするには以下のメソッドをオーバーライドしてください。
	/**
	 * autocompleteの選択時の処理を記述します。
	 */
/*
	onAutocompleteSelected() {
		super.onAutocompleteSelected();
	}
*/

	/**
	 * 関連データの更新後に呼び出されるメソッドです。
	 */
/*
	onUpdateRelationField() {
		super.onUpdateRelationField();
		// このフィールドが配置されたフォームのメソッドを呼び出す場合は以下の様にします。
		let form = this.getParentForm();
		if (typeof(form.hogeFunc) == "function") {
			form.hogeFunc();
		}
	}
*/

}

