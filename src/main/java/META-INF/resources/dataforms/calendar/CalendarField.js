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
	 * 年。
	 */
	#year = null;
	
	/**
	 * 月。
	 */
	#month = null;
	
	/**
	 * 日。
	 */
	#day = null;
		
	/**
	 * 日付フォーマット。
	 */
	#dateFormat = null;
	
	/**
	 * カレンダー情報。
	 */
	#calendarInfo = null;
	
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
	 * @param {Date} d 日付。
	 */
	selectDate(d) {
		this.#year = d.getFullYear();
		this.#month = d.getMonth();
		this.#day = d.getDate();
		this.find("table.calendarTable td").each((_, td) => {
			let day = parseInt($(td).data("day"));
			if (this.#day == day) {
				$(td).addClass("selected");
			} else {
				$(td).removeClass("selected");
			}			
		});
	}
		
	/**
	 * カレンダーのセルのクリックイベント処理。
	 * @param {Event} ev イベント処理。
	 */
	onClickCell(ev) {
		logger.log("ev=", ev);
		let idx = $(ev.currentTarget).data("index");
		logger.log("idx=" + idx);
		let dateInfo = this.#calendarInfo["dateInfo" + idx];
		if (dateInfo != null) {
			let day = $(ev.currentTarget).data("day");
			logger.log("date=" + day);
			let date = new Date(this.#year, this.#month, parseInt(day));
			let fmt = new SimpleDateFormat(this.#dateFormat);
			this.#dateValue	= fmt.format(date);
			logger.log("#dateValue=" + this.#dateValue);
			this.selectDate(date);
		}
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
	 * 日付毎の表示内容を取得します。
	 * <pre>
	 * このメソッドをオーバーライドしてアプリケーション毎の表示内容を作成してください。
	 * </pre>
	 * @param {Object} dateInfo 日付毎の情報。
	 */
	getDateContents(dateInfo) {
		logger.log("dateInfo=", dateInfo);
		return "";
	}
	
	
	/**
	 * 日付セルに設定するHTMLテキストを取得します。
	 * @param {Number} idx セルインデックス。
	 * @param {Object} dateInfo 日付情報。
	 */	
	getDateCellHtml(idx, dateInfo) {
		let tag = '<div data-id="date' + idx + '" class="date">' + dateInfo.day + '</div>';
		let contents = this.getDateContents(dateInfo);
		tag += '<div class="contents">' + contents + '</div>';
		return tag;
	}
	
	/**
	 * 日付を設定します。
	 * @param {String} v 日付。
	 */
	async setValue(v) {
		this.#dateValue = v;
		let fmt = new SimpleDateFormat(this.#dateFormat);
		let d = fmt.parse(this.#dateValue);
		this.#year = d.getFullYear();
		this.#month = d.getMonth();
		this.#day = d.getDate();
		let m = this.getWebMethod("getCalenderInfo");
		let r = await m.execute("date=" + this.#dateValue);
		if (r.status == JsonResponse.SUCCESS) {
			this.find(".monthYear").text(r.result.monthYear);
			this.#calendarInfo = r.result;
			let idx = 0;
			this.find("table.calendarTable tbody tr").each((_, tr) => {
				$(tr).find("td").each((_, td) => {
					let dateInfo = this.#calendarInfo["dateInfo" + idx];
					logger.log("dateInfo=", dateInfo);
					if (dateInfo != null) {
						let tag = this.getDateCellHtml(idx, dateInfo);
						$(td).html(tag);
						$(td).attr("data-day", dateInfo.day);
					} else {
						$(td).html("");
						$(td).attr("data-day", null);
					}
					idx++;
				});
			});
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

