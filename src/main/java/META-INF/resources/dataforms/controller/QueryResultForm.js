/**
 * @fileOverview {@link QueryResultForm}クラスを記述したファイルです。
 */

'use strict';

/**
 * @class QueryResultForm
 *
 * 問い合わせ結果フォームクラス。
 *  * <pre>
 * 問い合わせ結果を表示するフォームです。
 * </pre>
 * @extends Form
 */
class QueryResultForm extends Form {
	/**
	 * HTMLエレメントへの対応付けを行います。
	 * <pre>
	 * 以下のコンポーネントが存在した場合、イベント処理を登録します。
	 * #linesPerPage ... 1ページの行数指定。
	 * #pageNo ... ページ番号指定。
	 * #topPageButton ... 先頭ページボタン。
	 * #bottomPageButton ... 末尾ページボタン。
	 * #prevPageButton ... 前ページボタン。
	 * #nextPageButton ... 次ページボタン。
	 * </pre>
	 */
	attach() {
		super.attach();
		this.queryResult = null;
		this.get("linesPerPage").change(() => {
			this.get("pageNo").val(0);
			this.changePage();
		});
		this.get("pageNo").change(() => {
			this.changePage();
		});

		this.get("topPageButton").click(() => {
			this.topPage();
			return false;
		});

		this.get("bottomPageButton").click(() => {
			this.bottomPage();
			return false;
		});

		this.get("prevPageButton").click(() => {
			this.prevPage();
			return false;
		});

		this.get("nextPageButton").click(() => {
			this.nextPage();
			return false;
		});
		this.controlPager();
	}


	/**
	 * 先頭ページに遷移します。
	 */
	topPage() {
		this.get("pageNo").val(0);
		this.changePage();
	}


	/**
	 * 末尾ページに遷移します。
	 */
	bottomPage() {
		let v = this.find("#pageNo>option:last").val();
		this.get("pageNo").val(v);
		this.changePage();
	}

	/**
	 * 前ページに遷移します。
	 */
	prevPage() {
		let v = parseInt(this.get("pageNo").val(), 10);
		let idx = v - 1;
		if (idx < 0){
			idx = 0;
		}
		this.get("pageNo").val(idx);
		this.changePage();
	}

	/**
	 * 次ページに遷移します。
	 */
	nextPage() {
		let max = parseInt(this.find("#pageNo>option:last").val(), 10);
		let v = parseInt(this.get("pageNo").val(), 10);
		let idx = v + 1;
		if (idx > max){
			idx = max;
		}
		this.get("pageNo").val(idx);
		this.changePage();
	}

	/**
	 * ページの更新を行います。
	 */
	async changePage() {
		try {
			let lpp = this.get("linesPerPage");
			let lines = "";
			if (lpp.prop("disabled")) {
				// 1ページの行数がdisabledの場合無理やり取得する。
				this.get("linesPerPage").find("option").each((_, el) => {
					if ($(el).attr("selected") == "selected") {
						lines = "&linesPerPage=" + $(el).val();
					}
				});
			}
			let rt = this.getComponent("queryResult");
			logger.log("sortOrder=" + rt.sortOrder);
			let param = this.condition + lines +  "&" + this.get().serialize() + "&sortOrder=" + rt.sortOrder;
			logger.log("param=" + param);
			let method = this.getWebMethod("changePage");
			let result = await method.execute(param);
			this.parent.resetErrorStatus();
			if (result.status == JsonResponse.SUCCESS) {
				this.setQueryResult(result.result);
			} else {
				this.parent.setErrorInfo(this.getValidationResult(result), this);
			}
		} catch (e) {
			currentPage.reportError(e);
		}
	}

	/**
	 * 選択データを更新します。
	 */
	updateData() {
		let queryResultForm = this;
		let editForm = this.parent.getComponent("editForm");
		if (editForm != null) {
			editForm.updateData();
		} else {
			if (queryResultForm.parent instanceof Dialog) {
				queryResultForm.parent.close();
			}
		}
	}

	/**
	 * 選択データをコピーした新規データを登録します。
	 */
	referData() {
		let editForm = this.parent.getComponent("editForm");
		if (editForm != null) {
			editForm.referData();
		}
	}

	/**
	 * 選択データの表示します。
	 */
	viewData() {
		let editForm = this.parent.getComponent("editForm");
		if (editForm != null) {
			editForm.viewData();
		}
	}

	/**
	 * 選択データの削除を行います。
	 */
	async deleteData() {
		try {
			let systemName = MessagesUtil.getMessage("message.systemname");
			let msg = MessagesUtil.getMessage("message.deleteconfirm");
			if (await currentPage.confirm(systemName, msg)) {
				logger.log("selectedQueryString=" + this.selectedQueryString);
				let method = this.getWebMethod("delete");
				let result = await method.execute(this.selectedQueryString);
				this.parent.resetErrorStatus();
				if (result.status == JsonResponse.SUCCESS) {
					this.changePage();
				} else {
					this.parent.setErrorInfo(this.getValidationResult(result), this);
				}
			}
		} catch (e) {
			currentPage.reportError(e);
		}
	}

	/**
	 * ページ関連情報を設定します。
	 * @param {Object} queryResult 問い合わせ結果。
	 */
	setPagerInfo(queryResult) {
		let hitCount = queryResult.hitCount;
		let linesPerPage = queryResult.linesPerPage;
		let pageSelector = this.find("select[id='pageNo']");
		if (pageSelector.length) {
			let max = Math.floor(hitCount / linesPerPage);
			if (hitCount % linesPerPage != 0) {
				max ++;
			}
			pageSelector.empty();
			for (let i = 0; i < max; i++) {
				pageSelector.append('<option value="' + i + '">' + (i + 1) + '</option>');
			}
		}
	}

	/**
	 * ページ関連情報を制御します。
	 */
	controlPager() {
		if (this.find("#queryResult>tbody>tr").length == 0) {
			this.get("linesPerPage").prop("disabled", true);
			this.get("topPageButton").prop("disabled", true);
			this.get("prevPageButton").prop("disabled", true);
			this.get("pageNo").prop("disabled", true);
			this.get("nextPageButton").prop("disabled", true);
			this.get("bottomPageButton").prop("disabled", true);
		} else {
			this.get("linesPerPage").prop("disabled", false);
			this.get("topPageButton").prop("disabled", false);
			this.get("prevPageButton").prop("disabled", false);
			this.get("pageNo").prop("disabled", false);
			this.get("nextPageButton").prop("disabled", false);
			this.get("bottomPageButton").prop("disabled", false);
			let minPage = 0;
			let maxPage = parseInt(this.find("#pageNo>option:last").val(), 10);
			let pageNo = parseInt(this.get("pageNo").val(), 10);
			if (pageNo == minPage) {
				this.get("topPageButton").prop("disabled", true);
				this.get("prevPageButton").prop("disabled", true);
			}
			if (pageNo == maxPage) {
				this.get("nextPageButton").prop("disabled", true);
				this.get("bottomPageButton").prop("disabled", true);
			}
		}
	}

	/**
	 * 各種操作をするためのキーを設定します。
	 * <pre>
	 * 更新等のイベント処理時に更新対象のキー情報を適切に設定する処理です。
	 * </pre>
	 * @param {jQuery} comp イベントの発生したコンポーネント。
	 * @return {Boolean} 基本的にtrueを返す。
	 */
	setSelectedKey(comp) {
		// クリックされたボタンと同一行にあるキー項目の値を取得する.
		this.selectedQueryString = "";
		let tbl = this.getComponent("queryResult");
		let ridx = tbl.getRowIndex(comp);
		for (let i = 0; i < this.pkFieldList.length; i++) {
			let id = this.pkFieldList[i];
			let v = this.queryResult.queryResult[ridx][id];
			// 処理対象を指定するキーフィールドに値を設定する.
			if (this.selectedQueryString.length > 0) {
				this.selectedQueryString += "&"
			}
			this.selectedQueryString += (id + "=" + v);
			let editForm = this.parent.getComponent("editForm");
			if (editForm != null) {
				editForm.setFieldValue(id, v);
			}
		}
		return true;
	}

	/**
	 * 選択データを設定します。
	 * @param {jQuery} comp イベントの発生したコンポーネント。
	 */
	setSelectedData(comp) {
		let table = this.getComponent("queryResult");
		let idx = table.getRowIndex(comp);
		let seldata = this.queryResult.queryResult[idx];
		let dlg = this.getParentDataForms();
		dlg.data = seldata;
	}

	/**
	 * 問合せ結果にデフォルトイベント処理を設定します。
	 */
	setQueryResultEventHandler() {
		this.find("[id$='\.viewButton']").click((ev) => {
			if (this.setSelectedKey($(ev.currentTarget))) {
				this.viewData();
			}
		});
		//
		this.find("[id$='\.updateButton']").click((ev) => {
			if (this.setSelectedKey($(ev.currentTarget))) {
				// データ検索ダイアログ用に選択されたデータを設定する。
				this.setSelectedData($(ev.currentTarget));
				this.updateData();
			}
		});
		this.find("[id$='\.referButton']").click((ev) => {
			if (this.setSelectedKey($(ev.currentTarget))) {
				this.referData();
			}
		});
		this.find("[id$='\.deleteButton']").click((ev) => {
			if (this.setSelectedKey($(ev.currentTarget))) {
				this.deleteData();
			}
		});

		let editForm = this.parent.getComponent("editForm");
		if (editForm == null) {
			this.find(".deleteColumn").hide();
		}
	}

	/**
	 * 問い合わせ結果を表示します。
	 * <pre>
	 * 各結果行に以下のボタンが存在した場合、それぞれのイベント処理を登録します。
	 * [id$='\.viewButton'] ... 表示ボタン。
	 * [id$='\.updateButton'] ... 更新ボタン。
	 * [id$='\.referButton'] ... 参照登録ボタン。
	 * [id$='\.deleteButton'] ... 削除ボタン。
	 *
	 * </pre>
	 * @param {Object} queryResult 問い合わせ結果。
	 */
	setQueryResult(queryResult) {
		// データの設定に時間がかかる場合があるのでlockする。
		currentPage.lock();
		setTimeout(() => {
			this.queryResult = queryResult;
			this.setPagerInfo(queryResult);
			this.setFormData(queryResult);
			// 各リンクのイベント処理を登録.
			this.controlPager();
			// テーブルのイベント処理を追加する。
			this.setQueryResultEventHandler();
			currentPage.unlock();
		}, 10);
	}

}


