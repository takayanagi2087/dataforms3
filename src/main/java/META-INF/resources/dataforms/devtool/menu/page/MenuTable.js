/**
 * @fileOverview {@link MenuTable}クラスを記述したファイルです。
 */

'use strict';

/**
 * @class MenuTable
 *
 * @extends EditableHtmlTable
 */
class MenuTable extends EditableHtmlTable {
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
		logger.log("MenuTable=", this);
		let h = this.find("thead tr");
		let r = this.find("tbody tr");
		let cnt = 0;
		for (let i = 3; i < this.fields.length; i++) {
			let id = this.fields[i].id;
			let lang = id.replace("Name", "");
			logger.log("lang=" + lang);
			h.append("<th>" + MessagesUtil.getMessage("message.menuname", lang) + "</th>");
			let tag = "<td><input type='text' data-id='menuList[0]." + id + "' id='mainDiv.editForm.menuList[0]." + id + "' name='menuList[0]." + id + "'></td>";
			r.append(tag);
			cnt++;
		}
		this.find("th.footer").attr("colspan", 5 + cnt);
		super.attach();
	}
	
	/**
	 * Dataformsのメニューをロックします。
	 */
	lockDataformsMenu() {
		let table = this;
		for (let i = 0; i < table.getRowCount(); i++) {
			let path = table.getRowField(i, "path").getValue();
			if (path.indexOf("/dataforms") == 0) {
				for (let j = 0; j < this.fields.length; j++) {
					let fld = table.getRowField(i, this.fields[j].id);
					fld.lock(true);
				}		
			}
		}
	}

	/**
	 * テーブルのデータを設定します。
	 * @param {Array} data テーブルデータ。
	 */
/*	setTableData(data) {
		super.setTableData(data);
	}
*/
}

