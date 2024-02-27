/**
 * @fileOverview {@link QueryForm}クラスを記述したファイルです。
 */

'use strict';

/**
 * @class QueryForm
 *
 * 問い合わせフォームクラス。
 * <pre>
 * 問い合わせの条件を入力するためのフォームです。
 * </pre>
 * @extends Form
 */
class QueryForm extends Form {
	/**
	 * HTMLエレメントとの対応付けを行います。
	 * <pre>
	 * 以下のボタンが存在した場合、イベント処理を登録します。
	 * #queryButton ... 「検索」ボタンの処理.
	 * #resetButton ... 「リセット」ボタンの処理.
	 * </pre>
	 */
	attach() {
		super.attach();
		this.get("queryButton").click(() => {
			this.query();
			return false;
		});
		this.get("resetButton").click(() => {
			this.reset();
			return false;
		});
		this.get("exportButton").click(() => {
			this.exportData();
			return false;
		});
	}

	/**
	 * 問合せを行います。
	 */
	async query() {
		try {
			if (this.validate()) {
				let result = await this.submit("query");
				this.parent.resetErrorStatus();
				if (result.status == JsonResponse.SUCCESS) {
					let resultForm = this.parent.componentMap["queryResultForm"];
					if (resultForm != null) {
						this.showQueryResultForm();
					} else {
						this.showEditForm();
					}
				} else {
					this.parent.setErrorInfo(this.getValidationResult(result), this);
				}
			}
		} catch (e) {
			currentPage.reportError(e);
		}
	}
	/**
	 * 問い合わせ結果フォームに入力された検索条件を設定し、検索を行い結果を表示します。
	 */
	showQueryResultForm() {
		let resultForm = this.parent.componentMap["queryResultForm"];
		if (resultForm != null) {
			this.parent.get("queryResultForm").show();
			let condition = this.get().serialize();
			resultForm.condition = condition;
			resultForm.changePage();
		}
	}

	/**
	 * 編集フォームに入力された検索条件を設定し、対象データを編集します。
	 */
	showEditForm() {
		let editForm = this.parent.componentMap["editForm"];
		if (editForm != null) {
			for (let i = 0; i < this.fields.length; i++) {
				let f = editForm.getComponent(this.fields[i].id);
				f.setValue(this.getFieldValue(this.fields[i].id));
			}
			editForm.updateData();
		}
	}

	/**
	 * 問合せ結果をエクスポートします。
	 */
	async exportData() {
		try {
			if (this.validate()) {
				let sortOrder = this.getSortOrder();
				logger.log("sortOrder=" + sortOrder);
				this.setHiddenField("sortOrder", sortOrder);
				let result = await this.submit("exportData");
				this.parent.resetErrorStatus();
				if (result != null) {
					if (result.status == JsonResponse.INVALID) {
						this.parent.setErrorInfo(this.getValidationResult(result), this);
					}
				}
				logger.log("remove sortOrder");
				this.get("sortOrder").remove();
			}
		} catch (e) {
			currentPage.reportError(e);
		}
	}


	/**
	 * QueryResultFormのソート順を取得します。
	 */
	getSortOrder() {
		let ret = null;
		let qrf = currentPage.getComponent("queryResultForm");
		if (qrf != null) {
			let list = qrf.getComponent("queryResult");
			if (list != null) {
				ret = list.sortOrder;
			}
		}
		logger.log("sort order=" + ret);
		return ret;
	}

}

