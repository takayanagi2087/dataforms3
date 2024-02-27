/**
 * @fileOverview {@link QueryExecutorQueryResultForm}クラスを記述したファイルです。
 */

'use strict';

/**
 * @class QueryExecutorQueryResultForm
 *
 * @extends QueryResultForm
 */
class QueryExecutorQueryResultForm extends QueryResultForm {
	/**
	 * HTMLエレメントとの対応付けを行います。
	 */
	attach() {
		this.headerHtml = null;
		super.attach();
	}

	/**
	 * 問い合わせ結果を設定します。
	 * @param {Object} queryResult 問い合わせ結果。
	 */
	setQueryResult(queryResult) {
		logger.log("headerHtml=" + queryResult.headerHtml);
		logger.log("dataHtml=" + queryResult.dataHtml);
		logger.log("htmlTable=" + queryResult.htmlTable);
		let table = this.getComponent("queryResult");
		// mainDiv.queryResultForm.queryResult[0].
		table.fields = [];
		table.initField(queryResult.htmlTable.fieldList);
		for (let i = 0; i < table.fields.length; i++) {
			table.fields[i].realId = "mainDiv.queryResultForm.queryResult[0]." + table.fields[i].id;
			for (let j = 0; j < table.fields[i].validatorList.length; j++) {
				table.fields[i].validatorList[j].realId = "mainDiv.queryResultForm.queryResult[0]." + table.fields[i].validatorList[j].id;
			}
		}
		table.trLine = queryResult.dataHtml;
		if (this.headerHtml != queryResult.headerHtml) {
			logger.log("updateHeader");
			this.find("#queryResult thead tr").html(queryResult.headerHtml);
			table.setColumnSortEvent();
			this.headerHtml = queryResult.headerHtml;
		}
		super.setQueryResult(queryResult);
	}
}



