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
	 * 年月日。
	 */
	#dateValue = null;

	/**
	 * 日付フォーマット。
	 */
	#dateFormat = null;
	
	/**
	 * カレンダー情報。
	 */
	#calendarInfo = null;
	
	get calendarInfo() {
		return this.#calendarInfo;
	}
	
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
		this.get().html(this.calendarHtml);
		this.get().append("<input type='hidden' name='" + this.id + "' />");
		for (let i = 0; i < this.weekList.length; i++) {
			this.find(".week" + i).text(this.weekList[i]);
		}
		this.setupTd();
		this.find(".prevMonthButton").click(() => {
			this.onPrevMonth();
		});
		this.find(".nextMonthButton").click(() => {
			this.onNextMonth();
		});
	}

	/**
	 * 各TDのイベント処理を登録します。
	 */
	setupTd() {
		let idx = 0;
		this.find("table.calendarTable td").each((_, td) => {
			$(td).attr("data-index", idx);
			$(td).attr("class", "dateCell");
			$(td).click((ev) => {
				this.onClickCell(ev);
			});
			idx++;
		});
	}

	/**
	 * 日付を選択します。
	 * @param {String} d 日付。
	 */
	selectDate(d) {
		logger.log("this.#dateValue=" + this.#dateValue + ", " + d);
		this.#dateValue = d;
		this.find("table.calendarTable td").each((_, td) => {
			let date = $(td).attr("data-date");
			if (this.#dateValue == date) {
				$(td).addClass("selected");
			} else {
				$(td).removeClass("selected");
			}			
		});
		this.find("[name='" + this.id + "']").val(this.#dateValue);
	}
		
	/**
	 * カレンダーのセルのクリックイベント処理。
	 * @param {Event} ev イベント処理。
	 */
	onClickCell(ev) {
		let date = $(ev.currentTarget).attr("data-date");
		this.selectDate(date);
	}
	
		
	/**
	 * 前の月を表示。
	 */
	async onPrevMonth() {
		let fmt = new SimpleDateFormat(this.#dateFormat);
		let d = fmt.parse(this.#dateValue);
		d.setMonth(d.getMonth() - 1);
		this.setValue(fmt.format(d));
	}
	
	/**
	 * 次の月を表示。
	 */
	async onNextMonth() {
		let fmt = new SimpleDateFormat(this.#dateFormat);
		let d = fmt.parse(this.#dateValue);
		d.setMonth(d.getMonth() + 1);
		this.setValue(fmt.format(d));
	}

	/**
	 * 日付毎の表示内容を取得します。
	 * <pre>
	 * このメソッドをオーバーライドしてアプリケーション毎の表示内容を作成してください。
	 * </pre>
	 * @param {Object} dateInfo 日付毎の情報。
	 */
	getDateContents(dateInfo) {
//		logger.log("dateInfo=", dateInfo);
		return "<div class='contents'></div>";
	}
	
	
	/**
	 * 日付セルに設定するHTMLテキストを取得します。
	 * @param {Number} idx セルインデックス。
	 * @param {Object} dateInfo 日付情報。
	 */	
	getDateCellHtml(idx, dateInfo) {
		let fmt = new SimpleDateFormat(this.#dateFormat);
		let selectDate = fmt.parse(this.#dateValue);
		let d = fmt.parse(dateInfo.date);
		let tag = "";
		let contents = this.getDateContents(dateInfo);
		if (selectDate.getFullYear() == d.getFullYear() && selectDate.getMonth() == d.getMonth()) {
			tag += '<div data-id="date' + idx + '" class="date">' + d.getDate() + '</div>';
			tag += contents;
		} else {
			tag += '<div data-id="date' + idx + '" class="outdate">' + (d.getMonth() + 1) + "/" + d.getDate() + '</div>';
			tag += contents;
		}
		return tag;
	}
	
	/**
	 * カレンダーに表示する情報を取得します。
	 * <pre>
	 * 指定日付以外のパラメータが必要な場合、
	 * このメソッドをオーバーライドしてください。
	 * </pre>
	 * @param {String} dateValue 日付情報。
	 * @returns カレンダーに表示する情報。
	 */
	async getCalenderInfo(dateValue) {
		let m = this.getWebMethod("getCalenderInfo");
		let r = await m.execute(this.id + "=" + dateValue);
		return r;
	}
	
	async update() {
		this.setValue(this.#dateValue);
	}
	
	/**
	 * 日付を設定します。
	 * @param {String} v 日付。
	 */
	async setValue(v) {
		this.#dateValue = v;
		let fmt = new SimpleDateFormat(this.#dateFormat);
		let d = fmt.parse(this.#dateValue);
		let r = await this.getCalenderInfo(this.#dateValue);
		if (r.status == JsonResponse.SUCCESS) {
			logger.log("r=", r);
			this.find(".monthYear").text(r.result.monthYear);
			this.#calendarInfo = r.result;
			let dateList = this.#calendarInfo.dateList;
			let idx = 0;
			this.find("table.calendarTable tbody tr").each((_, tr) => {
				$(tr).find("td").each((_, td) => {
					let dateInfo = dateList[idx];
					let tag = this.getDateCellHtml(idx, dateInfo);
					$(td).html(tag);
					$(td).attr("data-date", dateInfo.date);
					idx++;
				});
			});
			this.selectDate(this.#dateValue);
		}
	}
	
	/**
	 * 値を取得します。
	 * @return {Date} 値の取得。 
	 */
	getValue() {
		if (this.#dateValue != null) {
			let fmt = new SimpleDateFormat(this.#dateFormat);
			return fmt.parse(this.#dateValue);			
		} else {
			return null;
		}
	}
}

