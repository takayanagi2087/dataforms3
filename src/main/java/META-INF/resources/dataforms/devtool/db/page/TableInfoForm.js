/**
 * @fileOverview {@link TableInfoForm}クラスを記述したファイルです。
 */

'use strict';

/**
 * @class TableInfoForm
 * テーブル情報フォームクラス。
 * <pre>
 * テーブル情報を表示するためのフォームです。
 * </pre>
 * @extends Form
 */
class TableInfoForm extends Form {
	/**
	 * HTMLエレメントフォームとの対応付けを行います。
	 *
	 */
	attach() {
		super.attach();
		this.get("initTableButton").click(() => {
			this.initTable();
		});
		this.get("updateTableButton").click(() => {
			this.updateTable();
		});
		this.get("dropTableButton").click(() => {
			this.dropTable();
		});
		this.get("closeButton").click(() => {
			this.parent.close();
		});
	}

	/**
	 * フォームデータの設定を行います。
	 * @param {Object} formData フォームデータ。
	 */
	setFormData(formData) {
		super.setFormData(formData);
		if (formData.tableExists) {
			this.get("dropTableButton").prop("disabled", false);
		} else {
			this.get("dropTableButton").prop("disabled", true);
		}
	}

	/**
	 * クエリ結果リストのクラス情報を更新します。
	 * @param {Object} result クラス情報。
	 */
	updateTableInfo(result) {
		let page = this.parent.parent;
		let resultForm = page.getComponent("queryResultForm");
		resultForm.updateTableInfo(result);
	}

	/**
	 * DBテーブルの初期化を行います。
	 */
	async initTable() {
		try {
			let systemName = MessagesUtil.getMessage("message.systemname");
			if (await currentPage.confirm(systemName, MessagesUtil.getMessage("message.initTableConfirm"))) {
				let clsname = this.get("className").html();
				let p = "className=" + clsname;
				let method = this.getWebMethod("initTable");
				let result = await method.execute(p);
				if (result.status == JsonResponse.SUCCESS) {
					this.setFormData(result.result);
					this.updateTableInfo(result.result);
				}
			}
		} catch (e) {
			currentPage.reportError(e);
		}
	}


	/**
	 * DBテーブルの削除を行います。
	 */
	async dropTable() {
		try {
			let systemName = MessagesUtil.getMessage("message.systemname");
			if (await currentPage.confirm(systemName, MessagesUtil.getMessage("message.dropTableConfirm"))) {
				let clsname = this.get("className").html();
				let p = "className=" + clsname;
				let method = this.getWebMethod("dropTable");
				let result = await method.execute(p);
				if (result.status == JsonResponse.SUCCESS) {
					this.setFormData(result.result);
					this.updateTableInfo(result.result);
				}
			}
		} catch (e) {
			currentPage.reportError(e);
		}
	}

	/**
	 * DBテーブルの再構築を行います。
	 */
	async updateTable() {
		try {
			let systemName = MessagesUtil.getMessage("message.systemname");
			if (await currentPage.confirm(systemName, MessagesUtil.getMessage("message.updateTableConfirm"))) {
				let clsname = this.get("className").html();
				let p = "className=" + clsname;
				let method = this.getWebMethod("updateTable");
				let result = await method.execute(p);
				if (result.status == JsonResponse.SUCCESS) {
					this.setFormData(result.result);
					this.updateTableInfo(result.result);
				}
			}
		} catch (e) {
			currentPage.reportError(e);
		}
	}

}


