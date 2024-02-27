/**
 * @fileOverview {@link EditableHtmlTable}クラスを記述したファイルです。
 */

'use strict';

/**
 * @class EditableHtmlTable
 * 編集可能なHTMLテーブルクラス。
 * <pre>
 * 行の追加、削除、ドラックによる順序変更をサポートします。
 * </pre>
 * @extends HtmlTable
 */
class EditableHtmlTable extends HtmlTable {
	/**
	 * 各エレメントとの対応付け.
	 */
	attach() {
		super.attach();
		if (!this.readonly) {
			this.find("tfoot").find(this.convertSelector("[id$='\\.addButton']")).click(() => {
				this.addRow(null);
				return false;
			});
			if (this.sortable) {
				this.makeSortable();
			}
		}
	}

	/**
	 * ソート切替モードかどうがの判定を行います。
	 *
	 * @reutrns {Boolean} ソート切替モードの場合true。
	 */
	isSortableSwitching() {
		if (!this.sortableSwitching) {
			if ("ontouchstart" in window) {
				return true;
			} else {
				return false;
			}
		} else {
			return true;
		}
	}


	/**
	 * 行入替機能を有効にします。
	 *
	 */
	enableSortable() {
		if (this.isSortable != true) {
			this.get().find("tbody").sortable({
				start:(_, __) => {
				},
				update:(_, __) => {
					this.resetIdIndex();
				},
				axis: "y"
			});
			this.isSortable = true;
		}
	}

	/**
	 * 行入替機能を無効にします。
	 *
	 */
	disableSortable() {
		if (this.isSortable == true) {
			this.get().find("tbody").sortable("destroy");
			this.isSortable = false;
		}
	}


	/**
	 * ロック処理時のソート設定の制御。
	 */
	lockSortable(lk) {
		if (this.sortable) {
			if (!this.isSortableSwitching()) {
				// sorableの切り替えをしない。
				if (lk) {
					this.disableSortable();
				} else {
					this.enableSortable();
				}
			} else {
				let ckid = this.id + ".sortable";
				let ck = this.parent.get(ckid);
				let sort = ck.prop("checked");
				if (sort) {
					// sortのチェックがOnの時のみ切り替える。
					if (lk) {
						this.disableSortable();

					} else {
						this.enableSortable();
					}
				}
				if (lk) {
					ck.prop("disabled", true);
				} else {
					ck.prop("disabled", false);
				}
			}
		}
	}


	/**
	 * 行入替可能なテーブルに設定します。
	 */
	makeSortable() {
		let ckid = this.id + ".sortable";
		this.enableSortable();
		if (this.isSortableSwitching()) {
			// タッチパネルの場合は行入替のOn/Off切り替え機能が必要。
			let label = MessagesUtil.getMessage("message.table.sortablelabel");
			let cb = '<input type="checkbox" id="' + ckid + '"><label for="' + ckid + '">' + label + '</label>';
			if (currentPage.useUniqueId) {
				cb = '<input type="checkbox" ' + this.getIdAttribute() + '="' + ckid + '" id="' + this.realId + '.sortable" "><label for="' + this.realId + '.sortable">' + label + '</label>';
			}
//			this.get().before(cb);
			this.find("thead th:first").append(cb);
			this.parent.get(ckid).click((ev) => {
				if ($(ev.currentTarget).prop("checked")) {
					this.find("[id$='\\.addButton']").prop("disabled", true);
					this.find("[id$='\\.deleteButton']").prop("disabled", true);
					this.enableSortable();
				} else {
					this.find("[id$='\\.addButton']").prop("disabled", false);
					this.find("[id$='\\.deleteButton']").prop("disabled", false);
					this.disableSortable();
				}
				return true;
			});
			this.disableSortable();
		}
	}

	/**
	 * ソートはサポートしない。
	 * @param col {jQuery} ソート対象カラム。
	 * @returns 常にnull。
	 */
	sortTable(col) {
		return null;
	}

	/**
	 * 「+」「-」ボタンの表示/非表示.
	 * @param lk 非表示にする場合true.
	 */
	lockEditButton(lk) {
		let ckid = this.id + ".sortable";
		let addbtn = this.find("[id$='\\.addButton']");
		let delbtn = this.find("[id$='\\.deleteButton']");
		if (lk) {
			addbtn.hide();
			delbtn.hide();
			this.get(ckid).hide();
			this.get(ckid).next("label").hide();
		} else {
			addbtn.show();
			delbtn.show();
			this.get(ckid).show();
			this.get(ckid).next("label").show();
		}
		this.lockSortable(lk);
	}


	/**
	 * テーブル中のフィールドのロック/アンロックを行う.
	 * @param lk ロックする場合true.
	 */
	lockFields(lk) {
		if (this.readonly) {
			super.lockFields(true);
			this.lockEditButton(true);
		} else {
			super.lockFields(lk);
			this.lockEditButton(lk);
		}
	}

	/**
	 * 各行のid中のインデックスを整列する.
	 */
	resetIdIndex() {
		let trlist = this.find("tbody>tr,tfoot>tr");
		for (let i = 0; i < trlist.length; i++) {
			{
				let c = $(trlist.get(i)).find(this.convertSelector("[id^='" + this.id + "\\[']"));
				c.each((_, el) => {
					{
						let id = $(el).attr(this.getIdAttribute());
						let newid = id.replace(new RegExp(this.id + "\\[.+?\\]"), this.id + "[" + i + "]");
						$(el).attr(this.getIdAttribute(), newid);
					}
					{
						if ("id" != this.getIdAttribute()) {
							let id = $(el).attr("id");
							if (id != null) {
								let newid = id.replace(new RegExp(this.id + "\\[.+?\\]"), this.id + "[" + i + "]");
								$(el).attr("id", newid);
							}
						}
					}
					{
						let name = $(el).attr("name");
						if (name != null) {
							let newname = name.replace(new RegExp(this.id + "\\[.+?\\]"), this.id + "[" + i + "]");
							$(el).attr("name", newname);
						}
					}
				});
			}
			{
				let c = $(trlist.get(i)).find(this.convertSelector("[for^='" + this.id + "\\[']"));
				c.each((_, el) => {
					let id = $(el).attr("for");
					let newid = id.replace(new RegExp(this.id + "\\[.+?\\]"), this.id + "[" + i + "]")
					$(el).attr("for", newid);
				});
			}
		}
		this.resetRowNo();
		// IDを作り直したのでdatepickerを設定し直す.
		let n = this.find("tbody>tr").length;
		for (let lidx = 0; lidx < n; lidx++) {
			for (let i = 0; i < this.fields.length; i++) {
				let f = this.getRowField(lidx, this.fields[i]);
				f.onIdChange();
			}
		}
	}

	/**
	 * 行追加時に呼び出されるメソッドです。
	 * <pre>
	 * 行中のボタンなどにイベント処理を登録します。
	 * </pre>
	 * @param {String} rowid 設定する行のID('tableid[idx]'形式)。
	 */
	onAddTr(rowid) {
		this.get(rowid + ".addButton").click((ev) => {
			this.addRow(ev.currentTarget);
			return false;
		});
		this.get(rowid + ".deleteButton").click((ev) => {
			this.deleteRow(ev.currentTarget);
			return false;
		});
	}


	/**
	 * 行を追加します。
	 * @param {Object} rowinfo 追加ボタンまたは追加する行。
	 * <pre>
	 * 	rowinfoには挿入する行のElementまたはjQueryオブジェクトまたは行のインデックス値を指定します。
	 * 	rowinfoにnullを設定すると最終行に追加されます。
	 * </pre>
	 */
	addRow(rowinfo) {
		let thisTable = this;
		let rowIndex = null;
		if (rowinfo != null) {
			if (rowinfo instanceof Element) {
				rowIndex = thisTable.getRowIndex($(rowinfo));
			} else if (typeof rowinfo.attr == "function") {
				rowIndex = thisTable.getRowIndex(rowinfo);
			} else {
				rowIndex = rowinfo;
			}
		}
		if (this.getRowCount() == 0) {
			rowIndex = null;
		}
		let lidx = thisTable.addTr(rowIndex);
		this.onAddTr(thisTable.id + "[" + lidx + "]");
		this.resetIdIndex();
	}

	/**
	 * 行を削除します。
	 * @param {Element} btn 削除ボタン。
	 */
	deleteRow(btn) {
		let thisTable = this;
		let rowIndex = thisTable.getRowIndex($(btn));
		for (let i = 0; i < this.fields.length; i++) {
			let f = this.getRowField(rowIndex, this.fields[i]);
			f.onDestroy();
		}
		thisTable.find("tbody>tr:eq(" + rowIndex + ")").remove();
		this.resetIdIndex();
		let form = this.getParentForm();
		form.onCalc(this.get());
	}


	/**
	 * 行番号等を設定し直します。
	 * <pre>
	 * 行番号(no),ソート順(sortOrder)の振り直しを行います。
	 * </pre>
	 */
	resetRowNo() {
		let trlist = this.find("[" + this.getIdAttribute() + "$=\\.no]");
		for (let i = 0; i < trlist.length; i++) {
			let lineNoId = this.id + "[" + i + "].no";
			this.get(lineNoId).text(i + 1);
			let sortOrderId = this.id + "[" + i + "].sortOrder";
			this.get(sortOrderId).val(i);

		}
		this.resetBackgroundColor();
	}


	/**
	 * テーブルに対するテータ設定を行います。
	 * @param {Array} list テーブルデータ。
	 */
	setTableData(list) {
		let ckid = this.id + ".sortable";
		let ck = this.parent.get(ckid);
		ck.prop("checked", false);
		super.setTableData(list);
		let lastAddButton = this.find("tfoot").find(this.convertSelector("[id$='\\.addButton']"));
		lastAddButton.prop("disabled", false);
		this.resetRowNo();
	}


	/**
	 * 必須マークを設定します。
	 */
	setRequiredMark() {
		for (let i = 0; i < this.fields.length; i++) {
			let el = $(this.trLine).find(this.convertSelector("#" + this.selectorEscape(this.id + "[0]." + this.fields[i].id)));
			if (this.fields[i].isRequired(el)) {
				let lel = this.getLabelElement(this.fields[i]);
				if (lel != null) {
					lel.addClass("requiredFieldLabel");
				}
			}

		}
	}
}

