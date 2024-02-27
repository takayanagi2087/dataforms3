/**
 * @fileOverview {@link SelectFieldHtmlTable}クラスを記述したファイルです。
 */

'use strict';

/**
 * @class SelectFieldHtmlTable
 *
 * @extends HtmlTable
 */
class SelectFieldHtmlTable extends EditableHtmlTable {
	/**
	 * HTMLエレメントとの対応付けを行います。
	 */
	attach() {
		super.attach();
	}

	/**
	 * テーブルにデータを設定します。
	 * @param {Array} list テーブルデータ。
	 *
	 * <pre>
	 * テーブルデータを設定し、レコードフィールドのチェックボックスにイベント処理を登録します。
	 * </pre>
	 */
	setTableData(list) {
		super.setTableData(list);
	}

	/**
	 * フィールド選択チェックボックスのイベント処理を行います。
	 * @param {Boolean} ck チェック。
	 */
	checkAll(ck) {
		logger.log("checkAll ck=" + ck);
		if (this.tableData != null) {
			let map = {};
			for (let i = 0; i < this.tableData.length; i++) {
				logger.log("checkAll i=" + i);
				let selid = "selectFieldList[" + i + "].sel";
				let sel = this.find("#" + this.selectorEscape(selid));
				let fid = "selectFieldList[" + i + "].fieldId";
				let f = this.find("#" + this.selectorEscape(fid));

				let afid = "selectFieldList[" + i + "].alias";
				let af = this.find("#" + this.selectorEscape(afid));
				let id = f.val();
				if (af.val().length > 0) {
					id = af.val();
				}
				if (map[id] == null) {
					if (ck) {
						sel.val("1");
					} else {
						sel.val("0");
					}
				}
				if (sel.val() == "1") {
					map[id] = true;
				}
			}
		}
	}

}


