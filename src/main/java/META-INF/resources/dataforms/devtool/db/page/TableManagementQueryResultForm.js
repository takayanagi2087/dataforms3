/**
 *
 * @fileOverview {@link TableManagementQueryResultForm}クラスを記述したファイルです。
 */

'use strict';

/**
 * @class TableManagementQueryResultForm
 * テーブル問い合わせ結果フォーム。
 * <pre>
 * テーブル管理の問い合わせ結果を表示するフォームです。
 * </pre>
 * @extends QueryResultForm
 */
class TableManagementQueryResultForm extends QueryResultForm {
	/**
	 * HTMLエレメントとの対応付けを行います。
	 */
	attach() {
		super.attach();
		let systemName = MessagesUtil.getMessage("message.systemname");
		this.get("selectAllButton").click(() => {
			$("[name='checkedClass']").each((_, ck) => {
				$(ck).prop("checked", true);
			});
			this.controlButton();
		});
		this.get("selectNothingButton").click(() => {
			for (let i = 0;;i++) {
				let id = "queryResult[" + i + "].statusVal";
				let st = this.get(id);
				if (st.length == 0) {
					break;
				}
				if (st.val() == "0") {
					let cbid = "queryResult[" + i + "].checkedClass";
					this.get(cbid).prop("checked", true);
				}
			}
			this.controlButton();
		});
		this.get("selectDiffButton").click(() => {
			for (let i = 0;;i++) {
				let id = "queryResult[" + i + "].differenceVal";
				let st = this.get(id);
				if (st.length == 0) {
					break;
				}
				if (st.val() == "1") {
					let cbid = "queryResult[" + i + "].checkedClass";
					this.get(cbid).prop("checked", true);
				}
			}
			this.controlButton();
		});
		this.get("unselectAllButton").click(() => {
			$("[name='checkedClass']").each((_, ck) => {
				$(ck).prop("checked", false);
			});
			this.controlButton();
		});
		this.get("initTableButton").click(async () => {
			if (await currentPage.confirm(systemName, MessagesUtil.getMessage("message.initTableConfirm"))) {
				try {
					let result = await this.submit("initTable");
					this.updateTableInfoList(result);
				} catch (e) {
					currentPage.reportError(e);
				}
			}
		});

		this.get("updateTableButton").click(async () => {
			if (await currentPage.confirm(systemName, MessagesUtil.getMessage("message.updateTableConfirm"))) {
				try {
					let result = await this.submit("updateTable");
					this.updateTableInfoList(result);
				} catch (e) {
					currentPage.reportError(e);
				}
			}
		});

		this.get("dropTableButton").click(async () => {
			if (await currentPage.confirm(systemName, MessagesUtil.getMessage("message.dropTableConfirm"))) {
				try {
					let result = await this.submit("dropTable");
					this.updateTableInfoList(result);
				} catch (e) {
					currentPage.reportError(e);
				}
			}
		});

		this.get("exportAsInitialDataButton").click(async () => {
			if (await currentPage.confirm(systemName, MessagesUtil.getMessage("message.exportAsInitialDataConfirm"))) {
				try {
					let result = await this.submit("exportTableAsInitialData");
					if (result.status == JsonResponse.SUCCESS) {
						let path = result.result;
						currentPage.alert(systemName, MessagesUtil.getMessage("message.exportInitialDataResult", path));
					}
				} catch (e) {
					currentPage.reportError(e);
				}
			}
		});

		this.get("exportTableButton").click(async () => {
			if (await currentPage.confirm(systemName, MessagesUtil.getMessage("message.dexportTableConfirm"))) {
				try {
					let result = await this.submit("exportTable");
					if (result.status == JsonResponse.SUCCESS) {
						let path = result.result;
						currentPage.alert(systemName, MessagesUtil.getMessage("message.exportInitialDataResult", path));
					}
				} catch (e) {
					currentPage.reportError(e);
				}
			}
		});

		this.get("importTableButton").click(() => {
			let dlg = this.parent.getComponent("importDataDialog");
			dlg.showModal();
		});
		this.controlButton();
	}

	/**
	 * ボタンのenable/disable制御を行います。
	 */
	controlButton() {
		let tr = this.find("#queryResult>tbody>tr");
		if (tr.length > 0) {
			this.get("selectAllButton").prop("disabled", false);
			this.get("selectNothingButton").prop("disabled", false);
			this.get("selectDiffButton").prop("disabled", false);
			this.get("unselectAllButton").prop("disabled", false);
		} else {
			this.get("selectAllButton").prop("disabled", true);
			this.get("selectNothingButton").prop("disabled", true);
			this.get("selectDiffButton").prop("disabled", true);
			this.get("unselectAllButton").prop("disabled", true);
		}
		let ckcb = this.find("[name='checkedClass']:checked");
		if (ckcb.length > 0) {
			this.get("updateTableButton").prop("disabled", false);
			this.get("initTableButton").prop("disabled", false);
			this.get("dropTableButton").prop("disabled", false);
			this.get("exportAsInitialDataButton").prop("disabled", false);
			this.get("exportTableButton").prop("disabled", false);
			this.get("importTableButton").prop("disabled", false);
		} else {
			this.get("updateTableButton").prop("disabled", true);
			this.get("initTableButton").prop("disabled", true);
			this.get("dropTableButton").prop("disabled", true);
			this.get("exportAsInitialDataButton").prop("disabled", true);
			this.get("exportTableButton").prop("disabled", true);
			this.get("importTableButton").prop("disabled", true);
		}
	}

	/**
	 * 結果テーブルへイベントハンドラを設定します。
	 * @param queryResult {Array} 検索結果。
	 */
	setTableEventHandler(queryResult) {
		if (queryResult != null) {
			for (let i = 0; i < queryResult.length; i++) {
				let id = "queryResult[" + i + "].className";
				this.get(id).click(async (ev) => {
					try {
						let clsname = $(ev.currentTarget).html();
						let qs="className=" + clsname;
						let method = this.getWebMethod("getTableInfo");
						let sqllist = await method.execute(qs);
						if (sqllist.status == JsonResponse.SUCCESS) {
							this.showTableInfo(sqllist.result);
						}
					} catch (e) {
						currentPage.reportError(e);
					}
				});
			}
			this.find("[name='checkedClass']").each((_, ck) => {
				$(ck).click(() => {
					this.controlButton();
				});
			});
			for (let i = 0; i < queryResult.length; i++) {
				logger.log("statusVal=" + queryResult[i].statusVal + ",differenceVal=" + queryResult[0].differenceVal);
				if (queryResult[i].differenceVal == "1") {
					this.find("#queryResult tbody tr:eq(" + i + ")").addClass("warnTr");
				} else {
					this.find("#queryResult tbody tr:eq(" + i + ")").removeClass("warnTr");
				}
				if (queryResult[i].statusVal == "0") {
					this.find("#queryResult tbody tr:eq(" + i + ")").addClass("errorTr");
				} else {
					this.find("#queryResult tbody tr:eq(" + i + ")").removeClass("errorTr");

				}
			}
			this.find("[id$='\.tableName']").click((ev) => {
				this.showQueryForm($(ev.currentTarget));
			});

			let tbl = this.getComponent("queryResult");
			for (let i = 0; i < queryResult.length; i++) {
				let bkfld = tbl.getRowField(i, "backupTable");
				let flg = queryResult[i].backupTable;
				if (flg == "1") {
					bkfld.get().next().show();
				} else {
					bkfld.get().next().hide();
				}
				let dropButton = bkfld.get().next().find(".dropButton");
				dropButton.data("table", queryResult[i].className)
				dropButton.click((ev) => {
					this.dropBackupTable(ev);
				});
			}
		}
		this.controlButton();
	}


	/**
	 * 問い合わせフォームを表示します。
	 */
	showQueryForm(lnk) {
		let table = lnk.text();
		logger.log("tableName=" + table);
		let url = currentPage.contextPath + "/dataforms/devtool/query/page/QueryExecutorPage.df?t=" + table;
		window.open(url, "_blank")
	}

	/**
	 * 問い合わせの結果を設定します。
	 * <pre>
	 * 問い合わせ結果をHTMLテーブルに設定されたソート順に従ってソートしてから設定します。
	 * </pre>
	 * @param result 問い合わせ結果。
	 */
	setQueryResult(result) {
		let tbl = this.getComponent("queryResult");
		result.queryResult = tbl.sort(result.queryResult);
		super.setQueryResult(result);
	}

	/**
	 * 問い合わせ結果を表示します。
	 * @param {Object} result 問い合わせ結果。
	 */
	setFormData(result) {
		super.setFormData(result);
		let queryResult = result.queryResult;
		this.setTableEventHandler(queryResult);
	}

	/**
	 * バックアップテーブルを削除します。
	 * @param {Event} ev イベント情報。
	 */
	async dropBackupTable(ev) {
		try {
			let msg = MessagesUtil.getMessage("message.dropBackupConfirm");
			let ck = await currentPage.confirm(null, msg);
			if (ck) {
				let table = $(ev.target).data("table");
				let m = this.getWebMethod("dropBackupTable");
				let r = await m.execute("table=" + table);
				if (r.status == JsonResponse.SUCCESS) {
					currentPage.alert(null, r.result);
					this.changePage();
				}
			}
		} catch (e) {
			currentPage.reportError(e);
		}
	}

	/**
	 * テーブル情報ダイアログを表示します。
	 * @param {Object} result テーブル情報。
	 *
	 */
	showTableInfo(result) {
		let dlg = this.parent.getComponent("tableInfoDialog");
		dlg.getComponent("tableInfoForm").setFormData(result);
		dlg.showModal();
	}

	/**
	 * 検索結果リストのテーブル情報を更新します。
	 * @param {Object} result テーブル情報。
	 */
	updateTableInfo(result) {
		for (let i = 0;;i++) {
			let id = "queryResult[" + i + "].className";
			let clsname = this.get(id);
			if (clsname.length > 0) {
				if (clsname.html() == result.className) {
					result.rowNo = (i + 1);
					let rt = this.getComponent("queryResult");
					rt.updateRowData(i, result);
					if (result.differenceVal == "1") {
						this.find("#queryResult tbody tr:eq(" + i + ")").addClass("warnTr");
					} else {
						this.find("#queryResult tbody tr:eq(" + i + ")").removeClass("warnTr");
					}
					if (result.statusVal == "0") {
						this.find("#queryResult tbody tr:eq(" + i + ")").addClass("errorTr");
					} else {
						this.find("#queryResult tbody tr:eq(" + i + ")").removeClass("errorTr");
					}

				}
			} else {
				break;
			}
		}
	}

	/**
	 * 検索結果リストのテーブル情報を更新します。
	 * @param {Array} result テーブル情報の配列。
	 */
	updateTableInfoList(result) {
		if (result.status == JsonResponse.SUCCESS) {
			let tlist = result.result;
			for (let i = 0; i < tlist.length; i++) {
				this.updateTableInfo(tlist[i]);
			}
		}
	}

	/**
	 * Importを実行します。
	 * @param {String} path インポートデータのパス。
	 */
	async importTableData(path) {
		logger.log("import=" + path);
		try {
			$(this.convertSelector("#datapath")).val(path);
			let result = await this.submit("importTable");
			this.updateTableInfoList(result);
		} catch (e) {
			currentPage.reportError(e);
		}
	}
}


