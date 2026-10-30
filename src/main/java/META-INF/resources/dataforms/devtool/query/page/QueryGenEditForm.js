/**
 * @fileOverview {@link QueryGenEditForm}クラスを記述したファイルです。
 */

'use strict';

import { JsonResponse } from '../../../response/JsonResponse.js';
import { MessagesUtil } from '../../../util/MessagesUtil.js';
import { EditForm } from '../../../controller/EditForm.js';

/**
 * @class QueryGenEditForm
 *
 * @extends EditForm
 */
export class QueryGenEditForm extends EditForm {
	/**
	 * HTMLエレメントとの対応付けを行います。
	 */
	attach() {
		super.attach();
		this.get("selectAll").click((ev) => {
			logger.log("selectAll");
			if (this.mode == "confirm") {
				return false;
			}
			let tableFieldList = this.getComponent("tableFieldList");
			let ck = $(ev.currentTarget).prop("checked");
			tableFieldList.checkAll(ck);
			this.#setSelectButtonStatus();
		});
		this.get("notUpdateConstractor").click((ev) => {
			this.lockStructer(ev);
		});
		this.#setSelectButtonStatus();
	}

	/**
	 * フィールド選択ボタンの状態の設定。
	 */
	#setSelectButtonStatus() {
		let cb = this.find("input.fieldSelect:checked");
		logger.log("cb=", cb);
		if (cb.length > 0) {
			this.get("referButton").prop("disabled", false);
		} else {
			this.get("referButton").prop("disabled", true);
		}
	}
	
	/**
	 * テーブル構造を元に戻す。
	 * @param ev イベント情報。
	 */
	async lockStructer(ev) {
		let ck = $(ev.currentTarget).prop("checked");
		if (ck) {
			if (await currentPage.confirm(null, MessagesUtil.getMessage("message.confirmfieldreset"))) {
				this.formData.notUpdateConstractor = "1";
				let notGenerateEntity = this.get("notGenerateEntity").prop("checked");
				if (notGenerateEntity) {
					this.formData.notGenerateEntity = "1";
				} else {
					this.formData.notGenerateEntity = "0";
				}
				this.setFormData(this.formData);
			}
		}
	}


	/**
	 * パッケージ名から機能を設定します。
	 * @param {String} sel 機能選択肢のid。
	 * @param {String} pkg パッケージ名。
	 */
	setFunctionSelect(sel, pkg) {
		let fsel = this.getComponent(sel);
		let v = fsel.getValue();
		if (v == null || v.length == 0) {
			fsel.selectPackage(pkg);
		}
	}

	/**
	 * 結合条件を取得する。
	 */
	async getJoinCondition() {
		let r = await this.submit("getJoinCondition");
		currentPage.resetErrorStatus();
		if (r.status == JsonResponse.SUCCESS) {
			logger.log("field list=" + JSON.stringify(r.result));
			this.setJoinCondition(r.result);
		}
		return r;
	}

	/**
	 * フォームに対してデータを設定します。
	 * @param {Object} data 設定するデータ。
	 */
	async setFormData(data) {
		try {
			logger.log("this.saveMode=" + this.saveMode);
			if (this.saveMode == "new") {
				this.get("notUpdateConstractor").prop("disabled", true);
			} else {
				this.get("notUpdateConstractor").prop("disabled", false);
			}
			super.setFormData(data);
			this.setFunctionSelect("functionSelect", data.packageName);
			let joinTableList = this.getComponent("joinTableList");
			for (let i = 0; i < joinTableList.getRowCount(); i++) {
				let pkg = joinTableList.getRowField(i, "packageName").getValue();
				let id = "joinTableList[" + i + "].functionSelect";
				this.setFunctionSelect(id, pkg);
			}
			this.setFunctionSelect("mainTableFunctionSelect", data.mainTablePackageName);
			if (data.joinTableList != null) {
				logger.log("data=", data);
				await this.getJoinCondition();
			}
		} catch (e) {
			currentPage.reportError(e);
		}
	}

	/**
	 * 選択フラグの取得。
	 */
	getSelFlag() {
		let ret = {};
		let tableFieldList = this.getComponent("tableFieldList");
		for (let i = 0; i < tableFieldList.getRowCount(); i++) {
			let sel = tableFieldList.getRowField(i, "sel").getValue();
			let fieldId = tableFieldList.getRowField(i, "fieldId").getValue();
			let fieldClassName = tableFieldList.getRowField(i, "fieldClassName").getValue();
			let tableClassName = tableFieldList.getRowField(i, "tableClassName").getValue();
			let key = tableClassName + "," + fieldClassName + "," + fieldId;
			ret[key] = sel;
		}
		return ret;
	}

	/**
	 * 選択フラグの再設定。
	 */
	setSelFlag(selmap) {
		let tableFieldList = this.getComponent("tableFieldList");
		for (let i = 0; i < tableFieldList.getRowCount(); i++) {
			let fieldId = tableFieldList.getRowField(i, "fieldId").getValue();
			let fieldClassName = tableFieldList.getRowField(i, "fieldClassName").getValue();
			let tableClassName = tableFieldList.getRowField(i, "tableClassName").getValue();
			let key = tableClassName + "," + fieldClassName + "," + fieldId;
			let v = selmap[key];
			if (v != null) {
				tableFieldList.getRowField(i, "sel").setValue(v);
			}
		}
	}

	/**
	 * フィールドリストを取得します。
	 */
	async getFieldList() {
		try {
			let selmap = this.getSelFlag();
			logger.log("selmap=", selmap);
			let r = await this.submit("getFieldList");
			currentPage.resetErrorStatus();
			if (r.status == JsonResponse.SUCCESS) {
				this.get("selectAll").prop("checked", false);
				logger.log("field list=" + JSON.stringify(r.result));
				let ftbl = this.getComponent("tableFieldList");
				ftbl.setTableData(r.result);
				this.setSelFlag(selmap);
				let cr = await this.submit("getJoinCondition");
				currentPage.resetErrorStatus();
				if (cr.status == JsonResponse.SUCCESS) {
					logger.log("field list=" + JSON.stringify(cr.result));
					this.setJoinCondition(cr.result);
				}
				this.find("input.fieldSelect").click(() => {
					this.#setSelectButtonStatus();
				});
			}
		} catch (e) {
			currentPage.reportError(e);
		}
	}


	/**
	 * テーブルクラス入力時のフィールドリスト取得。
	 * @param {jQuery} f 更新されたフィールド。
	 */
	onCalc(f) {
		super.onCalc(f);
		if (f != null) {
			logger.log("onCalc=" + f.attr("id"));
			this.getFieldList();
		}
	}

	/**
	 * 指定されたJOINテーブルの結合時要件を表示します。
	 * @param {Table} table HTMLテーブルのインスタンス。
	 * @param {Array} list 結合条件リスト。
	 */
	setJoinConditionToTable(table, list) {
		if (list != null) {
			let cf = table.getColumnField("joinCondition");
			for (let i = 0; i < list.length; i++) {
				let f = table.getRowField(i, cf);
				f.setValue(list[i].joinCondition);
			}
		}
	}

	/**
	 * 結合条件を表示します。
	 * @param {Object} data データ。
	 */
	setJoinCondition(data) {
		this.setJoinConditionToTable(this.getComponent("joinTableList"), data.joinTableList);
		this.setJoinConditionToTable(this.getComponent("leftJoinTableList"), data.leftJoinTableList);
		this.setJoinConditionToTable(this.getComponent("rightJoinTableList"), data.rightJoinTableList);
	}
}


