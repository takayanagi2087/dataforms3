/**
 * @fileOverview {@link FieldListForm}クラスを記述したファイルです。
 */

'use strict';

/**
 * @class FieldListForm
 *
 * @extends Form
 */
class FieldListForm extends Form {
	/**
	 * コンストラクタ。
	 */
	constructor() {
		super();
		this.listQuery = true;
	}

	/**
	 * HTMLエレメントとの対応付けを行います。
	 */
	attach() {
		super.attach();
		this.get("okButton").click(() => {
			this.onOk();
		});
		this.get("cancelButton").click(() => {
			this.onCancel();
		});

	}

	onCalc(jq) {
		logger.log("onCalc");
/*		let table = this.getComponent("fieldList");
		if (table != null) {
			for (let i = 0; i < table.getRowCount(); i++) {
				let v = table.getRowField(i, "listFieldDisplay").getValue();
				logger.log("v=" + v);
				if (v == "NONE" || v == "SPAN") {
					table.getRowField(i, "editKey").get().prop("disabled", true);
					table.getRowField(i, "editKey").get().prop("checked", false);
				} else {
					table.getRowField(i, "editKey").get().prop("disabled", false);
				}
			}
		}*/
	}

	/**
	 * 指定したクラスからフィールドリストを取得します。
	 * @param {Object} p テーブルまたは問合せクラス。
	 */
	async getFieldList(p) {
		logger.log("class=", p);
		this.listQuery = p.listQuery;
		this.setFunc = p.setFunc;
		let fieldList = this.getComponent("fieldList");
		fieldList.setTableData(p.conf);
		if (p.listQuery) {
			this.find(".listQuery").show();
			this.find(".editQuery").hide();
		} else {
			this.find(".listQuery").hide();
			this.find(".editQuery").show();
		}

		this.onCalc(null);
	}

	/**
	 * OKボタンの処理。
	 */
	onOk() {
		let list = this.getComponent("fieldList").getTableData();
		let json = JSON.stringify(list);
		logger.log("setFunc=", this.setFunc);
		if (this.setFunc != null) {
			this.setFunc(json);
		}
		this.parent.close();
	}

	/**
	 * Cancelボタンの処理。
	 */
	onCancel() {
		this.parent.close();
	}
}

