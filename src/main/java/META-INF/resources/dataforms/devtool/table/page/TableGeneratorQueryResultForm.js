/**
 * @fileOverview {@link TableGeneratorQueryResultForm}クラスを記述したファイルです。
 */

'use strict';

/**
 * @class TableGeneratorQueryResultForm
 *
 * @extends QueryResultForm
 */
class TableGeneratorQueryResultForm extends QueryResultForm {
	/**
	 * HTMLエレメントとの対応付けを行います。
	 */
	attach() {
		super.attach();
		this.get("printButton").click(() => {
			this.print();
		});
		let tbl = this.getComponent("queryResult");
		// ソート結果の行番号を修正。
		tbl.getSortedList = () => {
			let list = HtmlTable.prototype.getSortedList.call(tbl);
			for (let i = 0; i < list.length; i++) {
				list[i].rowNo = (i + 1);
			}
			return list;
		};
		// ソート時のイベントハンドラ設定。
		tbl.sortTable = (col) => {
			logger.log("sort");
			this.queryResult.queryResult = HtmlTable.prototype.sortTable.call(tbl, col);
			this.setQueryResultEventHandler();
		};
	}

	/**
	 * 問い合わせ結果を表示します。
	 * @param {Object} result 問い合わせ結果。
	 */
	setFormData(result) {
		super.setFormData(result);
		let queryResult = result.queryResult;
		if (queryResult != null) {
			for (let i = 0; i < queryResult.length; i++) {
				logger.log("statusVal=" + queryResult[i].statusVal + ",differenceVal=" + queryResult[0].differenceVal);
				if (queryResult[i].differenceVal == "1") {
					this.find("#queryResult tbody tr:eq(" + i + ")").addClass("warnTr");
				}
				if (queryResult[i].statusVal == "0") {
					this.find("#queryResult tbody tr:eq(" + i + ")").addClass("errorTr");
				}
			}
		}
	}

	/**
	 * テーブル定義書を印刷します。
	 */
	print() {
		this.parent.resetErrorStatus();
		this.submit("print");
	}

}


