/**
 * @fileOverview {@link QueryGeneratorQueryResultForm}クラスを記述したファイルです。
 */

'use strict';

/**
 * @class QueryGeneratorQueryResultForm
 *
 * @extends QueryResultForm
 */
class QueryGeneratorQueryResultForm extends QueryResultForm {
	/**
	 * コンストラクタ。
	 */
	constructor() {
		super();
	}

	/**
	 * HTMLエレメントとの対応付けを行います。
	 */
	attach() {
		super.attach();
	}

	/**
	 * SubQueryの生成を行います。
	 * @param {jQuery} btn ボタン。
	 */
	async generateSubQuery(btn) {
		try {
			let queryResult = this.getComponent("queryResult");
			let queryClassName = queryResult.getSameRowField(btn, "fullClassName").text();
			logger.log("queryClassName=" + queryClassName);
			let m = this.getWebMethod("generateSubQuery");
			let r = await m.execute("queryClass=" + queryClassName);
			if (r.status == JsonResponse.SUCCESS) {
				await currentPage.alert(null, r.result);
			}
			this.changePage();
		} catch (e) {
			currentPage.reportError(e);
		}
	}

	/**
	 * 問合せ結果にデフォルトイベント処理を設定します。
	 */
	setQueryResultEventHandler() {
		try {
			super.setQueryResultEventHandler();
			let queryResult = this.getComponent("queryResult");
			// リスト中のボタンに対してイベント処理を追加。
			this.find("[id$='\.generateSubQueryButton']").click(async (ev) => {
				let sq = queryResult.getSameRowField($(ev.currentTarget), "subQuery").text();
				if (sq.length > 0) {
					let msg = MessagesUtil.getMessage("message.confirmsubquery");
					if (await currentPage.confirm(null, msg)) {
						await this.generateSubQuery($(ev.currentTarget));
					};
				} else {
					await this.generateSubQuery($(ev.currentTarget));
				}
			});
		} catch (e) {
			currentPage.reportError(e);
		}
	}
}

