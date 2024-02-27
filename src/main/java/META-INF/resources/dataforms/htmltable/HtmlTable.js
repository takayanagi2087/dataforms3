/**
 * @fileOverview {@link HtmlTable}クラスを記述したファイルです。
 */

'use strict';

/**
 * @class HtmlTable
 * HTMLテーブルクラス。
 * <pre>
 * このクラスに対応するHTMLの記述は以下のようになります。
 * 以下の例はid="queryResult"の場合です。
 * <code>
 *	&lt;table id=&quot;queryResult&quot;&gt;
 *		&lt;thead&gt;
 *			&lt;tr&gt;
 *				&lt;th&gt;No.&lt;/th&gt;
 *				&lt;th&gt;&#12525;&#12464;&#12452;&#12531;ID&lt;/th&gt;
 *				&lt;th&gt;&#27663;&#21517;&lt;/th&gt;
 *				&lt;th&gt;&#12518;&#12540;&#12470;&#12524;&#12505;&#12523;&lt;/th&gt;
 *				&lt;th class=&quot;deleteColumn&quot;&gt;&#21066;&#38500;&lt;/th&gt;
 *			&lt;/tr&gt;
 *		&lt;/thead&gt;
 *		&lt;tbody&gt;
 *			&lt;tr&gt;
 *				&lt;td style=&quot;text-align:right;&quot;&gt;
 *					&lt;span id=&quot;queryResult[0].rowNo&quot; &gt;&lt;/span&gt;
 *					&lt;input type=&quot;hidden&quot; id=&quot;queryResult[0].userId&quot;&gt;
 *				&lt;/td&gt;
 *				&lt;td&gt;
 *					&lt;a id=&quot;queryResult[0].updateButton&quot; href=&quot;javascript:void(0);&quot;&gt;&lt;span id=&quot;queryResult[0].loginId&quot;&gt;&lt;/span&gt;&lt;/a&gt;
 *				&lt;/td&gt;
 *				&lt;td&gt;&lt;span id=&quot;queryResult[0].userName&quot;&gt;&lt;/span&gt;&lt;/td&gt;
 *				&lt;td&gt;&lt;span id=&quot;queryResult[0].userLevelName&quot;&gt;&lt;/span&gt;&lt;/td&gt;
 *				&lt;td class=&quot;deleteColumn&quot;&gt;
 *					&lt;input type=&quot;button&quot; id=&quot;queryResult[0].deleteButton&quot; value=&quot;&#21066;&#38500;&quot;&gt;
 *				&lt;/td&gt;
 *			&lt;/tr&gt;
 *		&lt;/tbody&gt;
 *	&lt;/table&gt;
 * </code>
 * </pre>
 * @extends WebComponent
 * @prop fields {Array} fieldListのクラス情報を元に作成した、Fieldクラスのインスタンスです。
 *
 */
class HtmlTable extends WebComponent {
	/**
	 * コンストラクタ。
	 */
	constructor() {
		super();
		this.fields = [];
	}

	/**
	 * HTMLテーブルを初期化します。
	 *
	 */
	init() {
		super.init();
		this.initField(this.fieldList);
	}

	/**
	 * HTMLエレメントとの対応付けを行います。
	 */
	attach() {
		// table配下のフィールドは行の追加時にattachを呼び出すので、ここではsuper.attach()は呼び出さない。
		super.setRealId();
		logger.log("fixedColumns=" + this.fixedColumns);
		logger.log("fixedWidth=" + this.fixedWidth);
		if (this.fixedColumns >= 0 || this.fixedWidth != null) {
			if (this.fixedWidth != null) {
				// Windowサイズ変更に伴う固定カラムの設定変更。
				$(window).resize(() => {
					this.calcFixedColumns(this.columnWidthList, this.tableWidth);
					if (this.fixedColumns > 0) {
						this.setFixedColumn(this.fixedColumns, this.columnWidthList);
					}
				});
			}
			this.setFixedColumnStyle();
			let bt = currentPage.getBrowserType();
			logger.log("browserType=" + bt);
			let isEdge = (bt == Page.BROWSER_EDGE);
			let isIe = (bt == Page.BROWSER_IE);
			if (isIe || isEdge) {
				// Edgeはtheadのstickyの動作がおかしいので、theadの固定をスクリプトで行う。
				this.get().find("thead").css("position", "relative");
				this.timeoutId = null;
				this.get().scroll(() => {
					if (this.timeoutId != null) {
						clearTimeout(this.timeoutId);
						this.timeoutId = null;
					}
					this.timeoutId = setTimeout(() => {
						let top = this.get().scrollTop();
						this.get().find("thead").css("top", top);
						if (isIe) {
							this.moveColumnForIe();
						}
					}, 100 ) ;
				});
			}
		}
		this.setSortMark();
		this.setColumnSortEvent();
		let tbl = this.get();
		this.trLine = tbl.find("tbody>tr:first").html();
		this.clear();
	}

	/**
	 * 所有コンポーネントのインスタンスを取得します。
	 * <pre>
	 * テーブル中のフィールドのインスタンスは行情報を持たないものをthis.fieldsに保持しています。
	 * 指定されたidが"tableid[0].fieldid"の形式で行情報が指定されていた場合、this.fields中の対応フィールドのコピーを作成し、
	 * そのインスタンスのidを"tableid[0].fieldid"に設定したものを返します。
	 * 指定されたidが"fieldid"の形式で行情報が指定されてない場合、this.fields中の対応フィールドを返します。
	 * </pre>
	 * @param {String} id 所有オブジェクトのID。
	 * @returns {WebComponent} 所有オブジェクトのインスタンス。
	 */
	getComponent(id) {
		let tblid = this.getHtmlTableId(id);
		if (tblid != null) {
			let colid = this.getHtmlTableColumnId(id);
			let tbl = this;
			for (let i = 0; i <  tbl.fields.length; i++) {
				if (tbl.fields[i].id == colid) {
					let tblfield = new tbl.fields[i].constructor();
					Object.assign(tblfield, tbl.fields[i]);
					tblfield.id = id;
					return tblfield;
				}
			}
		} else {
			return this.componentMap[id];
		}
	}


	/**
	 * テーブル中のフィールドに対応したラベルを取得します。
	 * @param {Field} field フィールド。
	 * @returns {jQuery} ラベルのエレメント.
	 */
	getLabelElement(field) {
		let label = null;
		let tblid = this.id;
//		let tag = this.parent.find('#' + this.selectorEscape(field.labelId));
		let fid = field.id;
		if (this.isHtmlTableElementId(fid)) {
			fid = this.getHtmlTableColumnId(fid);
		}
		let tag = this.parent.find("label[for='" + fid + "']");

		if (tag.length > 0) {
			// ラベルのIDが指定されていた場合の処理.
			label = tag;
		} else {
			let theadTr = this.parent.find("#" + tblid).find("thead tr:last");
			let tbodyTr = this.parent.find("#" + tblid).find("tbody tr:first");
			if (tbodyTr.length == 0) {
				tbodyTr = $("<tr>" + this.trLine + "</tr>");
			}
			let tdlist = tbodyTr.children();
			let idx = -1;
			for (let i = 0; i < tdlist.length; i++) {
				let comp = $(tdlist[i]).find(this.convertSelector("[id$='" + field.id + "']"));
				if (comp.length > 0) {
					idx = i;
					break;
				}
			}
			if (idx >= 0) {
				let thlist = theadTr.find("th")
				let hidx = idx;
				for (let i = 0; i < idx && i < thlist.length; i++) {
					let colspan = thlist.eq(i).attr("colspan");
					if (colspan != null) {
						let cs = parseInt(colspan);
						hidx -= (cs - 1);
					}
				}
				label = $(theadTr.children()[hidx]);
			}
		}
		return label;
	}


	/**
	 * フィールドに対応したラベル文字列を取得します。
	 * @param {Field} field フィールド。
	 * @returns {String} ラベル文字列。
	 */
	getLabel(field) {
		let el = this.getLabelElement(field);
		if (el != null) {
			return el.html();
		} else {
			return null;
		}
	}

	/**
	 * フィールドの初期化を行います。
	 *
	 * @param {Array} fieldList フィールドリスト。
	 *
	 */
	initField(fieldList) {
		for (let i = 0; i < fieldList.length; i++) {
			let f = fieldList[i];
			let field = this.newInstance(f);
			this.fields[i] = field;
		}
	}

	/**
	 * カラムソートイベントを設定します。
	 */
	setColumnSortEvent() {
		for (let i = 0; i < this.fields.length; i++) {
			let field = this.fields[i];
			field.label = this.getLabel(field);
			if (field.sortable) {
				let el = this.getLabelElement(field);
				if (el != null) {
					el.click((ev) => {
						this.sortTable($(ev.currentTarget));
					});
				}
			}
		}
	}

	/**
	 * テーブルの各カラム幅を設定します。
	 * @param {String} tag "thead"または"tfoot"。
	 * @param {Array} warray カラム幅の配列。
	 * @param {Array} warrayAll カラム幅の配列(padding, borderを含めた幅)。
	 */
	setColumnWidth(tag, warray, warrayAll) {
		this.find(tag + " tr").each((_, el) => {
			let i = 0;
			$(el).children().each((_, col) => {
				let colspan = $(col).attr("colspan");
				if (colspan === undefined) {
					$(col).width(warray[i++]);
				} else {
					let w = 0;
					let cnt = parseInt(colspan);
					for (let c = 0; c < cnt; c++) {
						if (c < (cnt - 1)) {
							w += warrayAll[i++];
						} else {
							w += warray[i++];
						}
					}
					$(col).width(w);
				}
			});
		});
	}

	/**
	 * ヘッダのカラム幅を設定します。
	 * @param {Array} warray カラム幅の配列。
	 * @param {Array} warrayAll カラム幅の配列(padding, borderを含めた幅)。
	 */
	setHeaderColumnWidth(warray, warrayAll) {
		this.setColumnWidth("thead", warray, warrayAll);
	}

	/**
	 * フッタのカラム幅を設定します。
	 * @param {Array} warray カラム幅の配列。
	 * @param {Array} warrayAll カラム幅の配列(padding, borderを含めた幅)。
	 */
	setFooterColumnWidth(warray, warrayAll) {
		this.setColumnWidth("tfoot", warray, warrayAll);
	}

	/**
	 * thead,tfoot用の固定カラム設定。
	 * @param {jQuery} tr 設定するtrのjQueryオブジェクト。
	 * @param {Number} cols 固定カラム数。
	 * @param {Array} warray カラム幅の配列。
	 */
	lockColumn(tr, cols, warray) {
		let idx = 0;
		let pos = 0;
		tr.children().each((_, el) => {
			if (idx < cols) {
				$(el).addClass("fixedColumn");
				$(el).css("left", pos + "px");
				let colspan = $(el).prop("colspan");
				logger.log("colspan=" + colspan);
				if (colspan == null) {
					colspan = 1;
				}
				for (let i = 0; i < colspan; i++) {
					pos += warray[idx++];
				}
				$(el).css("z-index", "3");
			}
		});
	}

	/**
	 * thead用の固定カラム設定。
	 * @param {jQuery} tr 設定するtrのjQueryオブジェクト。
	 * @param {Number} cols 固定カラム数。
	 * @param {Array} warray カラム幅の配列。
	 */
	setTheadFixedColumn(tr, cols, warray) {
		this.lockColumn(tr, cols, warray);
	}


	/**
	 * tbody用の固定カラム設定。
	 * @param {jQuery} tr 設定するtrのjQueryオブジェクト。
	 * @param {Number} cols 固定カラム数。
	 * @param {Array} warray カラム幅の配列。
	 */
	setTbodyFixedColumn(tr, cols, warray) {
		let idx = 0;
		let pos = 0;
		tr.children().each((_, el) => {
			if (idx < cols) {
				$(el).addClass("fixedColumn");
				$(el).css("left", pos + "px");
				pos += warray[idx++];
				$(el).css("z-index", "1");
			}
		});
	}


	/**
	 * tfoot用の固定カラム設定。
	 * @param {jQuery} tr 設定するtrのjQueryオブジェクト。
	 * @param {Number} cols 固定カラム数。
	 * @param {Array} warray カラム幅の配列。
	 */
	setTfootFixedColumn(tr, cols, warray) {
		this.lockColumn(tr, cols, warray);
	}


	/**
	 * 固定カラムを設定する。
	 * @param {Number} col 固定カラム数。
	 * @param {Array} warray カラム幅の配列。
	 */
	setFixedColumn(cols, warray) {
		// 固定カラム系の設定を削除。
		this.find("th").removeClass("fixedColumn");
		this.find("th").css("left", "");
		this.find("th").css("z-index", "");
		this.find("td").removeClass("fixedColumn");
		this.find("td").css("left", "");
		this.find("td").css("z-index", "");
		let thisTable = this;
		this.find("thead tr").each((_, el) => {
			thisTable.setTheadFixedColumn($(el), cols, warray);
		});
		this.find("tbody tr").each((_, el) => {
			thisTable.setTbodyFixedColumn($(el), cols, warray);
		});
		this.find("tfoot tr").each((_, el) => {
			thisTable.setTfootFixedColumn($(el), cols, warray);
		});
	}


	/**
	 * 指定されたカラムの幅を取得します。
	 * @param {jQuery} col カラムのjQueryオブジェクト。
	 */
	getColumnWidth(col) {
		return col.outerWidth(true);
	}

	/**
	 * 固定カラム幅に対応したカラム数を計算します。
	 */
	calcFixedColumns(warrayAll, tbodyWidth) {
		let form = this.getParentForm();
		let tw = form.get().width();
		logger.log("tw=" + tw);
		let fw = tw * this.fixedWidth / 100.0;
		let cols = 0;
		let w = 0;
		for (let i = 0; i < warrayAll.length; i++) {
			w += warrayAll[i];
			if (w > fw) {
				break;
			}
			cols++;
		}
		if (cols > warrayAll.length) {
			cols = warrayAll.length;
		}
		logger.log("cols=" + cols);
		this.fixedColumns = cols;
	}


	/**
	 * 固定カラム用のスタイル設定を行います。
	 */
	setFixedColumnStyle() {
		this.get().closest("div.hScrollDiv").css("overflow-x", "hidden");
		this.get().addClass("columnFixedTable");
		let wbody = 0;
		let warray = [];
		let warrayAll = [];
		this.find("tbody tr:first").find("td").each((_, el) => {
			warray.push($(el).width());
			let fw = this.getColumnWidth($(el));
			warrayAll.push(fw);
			wbody += fw;
		});

		this.find("thead").width(wbody);
		this.find("tbody").width(wbody);
		this.find("tfoot").width(wbody);
		if (this.fixedWidth != null) {
			this.calcFixedColumns(warrayAll, wbody);
		}

		this.setHeaderColumnWidth(warray, warrayAll);
		this.setFooterColumnWidth(warray, warrayAll);
		if (this.fixedColumns > 0) {
			this.setFixedColumn(this.fixedColumns, warrayAll);
		}
		this.tableWidth = wbody;
		this.columnWidthList = warrayAll;

	}


	/**
	 * IE用のカラム位置設定。
	 */
	setColumnLeftForIe(tr) {
		let sx = this.get().scrollLeft();
		tr.children(".fixedColumn").each((_, el) => {
			$(el).css("left", sx + "px");
		});
	}


	/**
	 * IE用のカラム移動。
	 */
	moveColumnForIe() {
		this.get().find("th.fixedColumn").css("position", "relative");
		this.get().find("td.fixedColumn").css("position", "relative");
		this.find("thead tr").each((_, el) => {
			this.setColumnLeftForIe($(el));
		});
		this.find("tbody tr").each((_, el) => {
			this.setColumnLeftForIe($(el));
		});
		this.find("tfoot tr").each((_, el) => {
			this.setColumnLeftForIe($(el));
		});

	}


	/**
	 * カラムにソートマークを設定する。
	 */
	setSortMark() {
		logger.log("setSortMark");
		let thisTable = this;
		for (let i = 0; i < this.fields.length; i++) {
			let field = this.fields[i];
			if (field.sortable) {
				let el = this.getLabelElement(field);
				if (el == null) {
					continue;
				}
				logger.log("el.tag=" + el.prop("tagName"));
				let labelspan = MessagesUtil.getMessage("htmltable.sortablelabel");
				let mark = MessagesUtil.getMessage("htmltable.sortable");
				if (field.sortOrder == "ASC") {
					mark = MessagesUtil.getMessage("htmltable.sortedasc");
					el.data("order", "ASC");
				} else if (field.sortOrder == "DESC") {
					mark = MessagesUtil.getMessage("htmltable.sorteddesc");
					el.data("order", "DESC");
				} else {
					mark = MessagesUtil.getMessage("htmltable.sortable");
					el.data("order", "NONE");
				}
				if (el.find(".sortableMark").length == 0) {
					el.contents().wrap(labelspan).before(mark);
					el.data("id", field.id);
				} else {
					el.find(".sortableMark").remove();
					el.find(".sortableLabel").prepend(mark);
				}

			}
		}
	}

	/**
	 * ソート状態を変更する。
	 * @param co {jQuery} ラベルのエレメント.l
	 */
	changeSortMark(col) {
		let colid = col.data("id");
		let order = col.data("order");
		logger.log("column click=" + col.data("id") + "," + col.data("order"));
		let mark = MessagesUtil.getMessage("htmltable.sortable");
		if (order == "ASC") {
			mark = MessagesUtil.getMessage("htmltable.sorteddesc");
			col.data("order", "DESC");
		} else if (order == "DESC") {
			mark = MessagesUtil.getMessage("htmltable.sortable");
			col.data("order", "NONE");
		} else {
			mark = MessagesUtil.getMessage("htmltable.sortedasc");
			col.data("order", "ASC");
		}
		col.find(".sortableMark").remove();
		col.find(".sortableLabel").prepend(mark);
	}

	/**
	 * ソート対象フィールドのリストを取得します。
	 * @returns {Array} ソート対象フィールドのリスト。
	 */
	getSortFieldList() {
		let flist = [];
		for (let i = 0; i < this.fields.length; i++) {
			let field = this.fields[i];
			if (field.sortable) {
				let col = this.getLabelElement(field);
				if (col != null) {
					field.currentSortOrder = col.data("order");
					logger.log(field.id + ":" + field.currentSortOrder);
					if (field.currentSortOrder == "ASC" || field.currentSortOrder == "DESC") {
						flist.push(field);
					}
				}
			}
		}
		return flist;
	}

	/**
	 * カラムのソート設定に応じて、リストをソートします。
	 *
	 * @param list ソートするリスト。
	 * @returns ソート結果。
	 */
	sort(list) {
		let sflg = false;
		for (let i = 0; i < this.fields.length; i++) {
			let field = this.fields[i];
			if (field.sortable) {
				let col = this.getLabelElement(field);
				if (col != null) {
					field.currentSortOrder = col.data("order");
					logger.log(field.id + ":" + field.currentSortOrder);
					if (field.currentSortOrder == "ASC" || field.currentSortOrder == "DESC") {
						sflg = true;
					}
				}
			}
		}
		let slist = list;
		if (sflg) {
//			logger.log("sflg=" + sflg);
			slist = list.sort((a, b) => {
				let cmp = 0;
				for (let i = 0; i < this.fields.length; i++) {
					let field = this.fields[i];
					if (field.sortable) {
						if (field.currentSortOrder == "ASC") {
							cmp = field.comp(a, b);
						} else if (field.currentSortOrder == "DESC") {
							cmp = field.comp(b, a);
						}
					}
					if (cmp != 0) {
						return cmp;
					}
				}
				return cmp;
			});
		}
		return slist;
	}

	/**
	 * カラムの設定に従った、ソートされたリストを取得します。
	 * @returns {Array} ソートされたリスト。
	 */
	getSortedList() {
		let list = this.tableData.concat();
		return this.sort(list);
	}


	/**
	 * ソートを行います。
	 * @param co {jQuery} ラベルのエレメント。
	 * @return {Array} ソート結果リスト。
	 */
	sortTable(col) {
		this.changeSortMark(col);
		let slist = this.getSortedList();
		this.setTableData(slist);
		return slist;
	}


	/**
	 * テーブルをクリアします。
	 */
	clear() {
		// フィールドの解放を行う
		let n = this.find("tbody>tr").length;
		for (let lidx = 0; lidx < n; lidx++) {
			for (let i = 0; i < this.fields.length; i++) {
				let f = this.getRowField(lidx, this.fields[i]);
				f.onDestroy();
			}
		}
		let tbl = this.parent.get(this.id);
		tbl.find("tbody").empty();
	}


	/**
	 * テーブルのカラムフィールドを所得します。
	 * <pre>
	 * テーブルの各カラムに割り当てられたフィールドクラスのインスタンスを取得します。
	 * このフィールドをgetRowFieldに渡すことにより、行を限定したフィールドを取得することができます。
	 * </pre>
	 * @param {String} id フィールドID。
	 * @returns テーブルのカラムフィールド。
	 */
	getColumnField(id) {
		for (let i = 0; i < this.fields.length; i++) {
			if (this.fields[i].id == id) {
				return this.fields[i];
			}
		}
		return null;
	}

	/**
	 * 指定行のフィールドを取得します。
	 * @param {Integer} idx 指定行。
	 * @param {Field} field フィールド。
	 * @returns {Field} フィールド。
	 * @deprecated getRowFieldを使用してください。
	 */
	getLineField(idx, field) {
		return this.getRowField(idx, field);
	}

	/**
	 * 指定行のフィールドを取得します。
	 * @param {Integer} idx 指定行。
	 * @param {Object} fobj フィールドIDまたはカラムフィールド。
	 * @returns {Field} フィールド。
	 */
	getRowField(idx, fobj) {
		let field = fobj;
		if (!(fobj instanceof Field)) {
			field = this.getColumnField(fobj);
		}
		let f = new field.constructor();
		Object.assign(f, field);
		f.id = this.id + "[" + idx + "]." + field.id;
		if (f.realId != null) {
			f.realId = this.realId + "[" + idx + "]." + field.id;
		}
		f.initValidator(f.validatorList);
		return f;
	}

	/**
	 * 行を追加します。
	 *
	 */
	addTr(l) {
		let tb = this.find("tbody");
		let lidx = this.find("tbody>tr").length;
		let line = this.trLine.replace(/\[0\]/g, "[" + lidx + "]");
		if (l == null) {
			tb.append("<tr>" + line + "</tr>");
		} else {
			$(this.find("tbody>tr").get(l)).before("<tr>" + line + "</tr>");
		}
		for (let i = 0; i < this.fields.length; i++) {
			let f = this.getRowField(lidx, this.fields[i]);
			f.attach();
		}
		return lidx;
	}

	/**
	 * 指定行のデータを設定します。
	 * @param {Integer} idx 行。
	 * @param {Object} line フォームデータ。
	 * @deprecated setRowDataを使用してください。
	 */
	setLineData(idx, line) {
		this.setRowData(idx, line)
	}

	/**
	 * 指定行のデータを設定します。
	 * @param {Integer} idx 行。
	 * @param {Object} line フォームデータ。
	 */
	setRowData(idx, line) {
		for (let i = 0; i < this.fields.length; i++) {
			let orgf = this.fields[i];
			let f = this.getRowField(idx, orgf);
			f.setValue(line[orgf.id]);
		}
	}

	/**
	 * 指定行のデータを更新する.
	 * <pre>
	 * 行編集後の更新用.
	 * </pre>
	 * @param line 行.
	 * @param rowData 行のみのデータ.
	 */
	updateRowData(line, rowData) {
		for (let i = 0; i < this.fields.length; i++) {
			let orgf = this.fields[i];
			let f = this.getRowField(line, orgf);
			f.setValue(rowData[orgf.id]);
		}
	}

	/**
	 * テーブルに対するテータ設定を行います。
	 * @param {Object} formData フォームのデータ。
	 */
	setFormData(formData) {
		let list = formData[this.id];
		this.setSortMark();
		this.setTableData(list);
		this.tableData = list;
	}

	/**
	 * 各行の背景色を設定します。
	 */
	resetBackgroundColor() {
		let fsel = 'input[type="text"],input[type="password"],textarea,select';
		//
		this.find('tbody tr:even').removeClass("oddTr");
		this.find('tbody tr:even').find(fsel).removeClass("oddTr");
		this.find('tbody tr:even td').removeClass("oddTr");
		//
		this.find('tbody tr:even').addClass("evenTr");
		this.find('tbody tr:even').find(fsel).addClass("evenTr");
		this.find('tbody tr:even td').addClass("evenTr");
		//
		this.find('tbody tr:odd').removeClass("evenTr");
		this.find('tbody tr:odd').find(fsel).removeClass("evenTr");
		this.find('tbody tr:odd td').removeClass("evenTr");
		//
		this.find('tbody tr:odd').addClass("oddTr");
		this.find('tbody tr:odd').find(fsel).addClass("oddTr");
		this.find('tbody tr:odd td').addClass("oddTr");
	}

	/**
	 * 行追加時に呼び出されるメソッドです。
	 *
	 * @param {String} rowid 設定する行のID('tableid[idx]'形式)。
	 */
	onAddTr(rowid) {
	}

	/**
	 * テーブルに対するテータ設定を行います。
	 * @param {Array} list テーブルデータ。
	 */
	setTableData(list) {
		this.tableData = list;
		if (list != null) {
			this.find("tbody").empty();
			// 表の行を追加.
			for (let i = 0; i < list.length; i++) {
				this.addTr();
			}
			// 表のデータを追加.
			for (let i = 0; i < list.length; i++) {
				this.setRowData(i, list[i]);
				this.onAddTr(this.id + "[" + i + "]");
			}
			this.resetBackgroundColor();
		}
	}

	/**
	 * テーブルに保存されているデータを取得します。
	 * @return {Array} テーブルデータ。
	 */
	getTableData() {
		let list = [];
		for (let r = 0; r < this.getRowCount(); r++) {
			let m = {};
			for (let c = 0; c < this.fieldList.length; c++) {
				let f = this.getRowField(r, this.fieldList[c].id);
				m[this.fieldList[c].id] = f.getValue();
			}
			list.push(m);
		}
		return list;
	}

	/**
	 * テーブルのバリデーションを行います。
	 * @returns {Array} バリデーション結果。
	 */
	validate() {
		let result = [];
		for (let i = 0;; i++) {
			let flg = false;
			for (let f = 0; f < this.fields.length; f++) {
				let fld = this.getRowField(i, this.fields[f]);
				if (fld.get().length > 0) {
					flg = true;
					let e = fld.validate();
					if (e != null) {
						result.push(e);
					}
				}
			}
			if (!flg) {
				break;
			}
		}
		return result;
	}

	/**
	 * テーブルの指定行をロック/アンロックします。
	 * @param {Number} line 指定行インデックス。
	 * @param {Boolean} lk ロック指定。
	 * @returns {Boolean} 指定行が存在する場合true。
	 * @deprecated lockRowを使用してください。
	 */
	lockLine(line, lk) {
		return this.lockRow(line, lk);
	}

	/**
	 * テーブルの指定行をロック/アンロックします。
	 * @param {Number} line 指定行インデックス。
	 * @param {Boolean} lk ロック指定。
	 * @returns {Boolean} 指定行が存在する場合true。
	 */
	lockRow(line, lk) {
		let flg = false;
		for (let f = 0; f < this.fields.length; f++) {
			let fld = this.getRowField(line, this.fields[f]);
			if (fld.get().length > 0) {
				flg = true;
				fld.lock(lk);
			}
		}
		return flg;
	}

	/**
	 * テーブル内のフィールドをロック/アンロックします。
	 * @param {Boolean} lk ロックする場合true.
	 */
	lockFields(lk) {
		for (let i = 0;; i++) {
			let flg = this.lockRow(i, lk);
			if (!flg) {
				break;
			}
		}
	}

	/**
	 * 同じ行の指定フィールドを取得します。
	 * @param {jQuery} f 指定フィールドに対応したjQueryオブジェクト。
	 * @param {String} tid 取得するフィールドID.
	 * @return {jQuery} 見つけた要素のjQueryオブジェクト。
	 * <pre>
	 * fで指定されたjQueryオブジェクトと同じ行にある、tidをもつ要素を取得します。
	 * </pre>
	 * @deprecated getSameRowFieldを使用してください。
	 */
	getSameLineField(f, tid) {
		return this.getSameRowField(f, tid);
	}

	/**
	 * 指定フィールドと同じ行のフィールドを取得します。
	 * @param {Object} f 指定フィールドに対応したjQueryオブジェクトまたはFieldオブジェクト。
	 * @param {String} tid 取得するフィールドID.
	 * @return {Object} 見つけた要素のjQueryオブジェクトまたはFieldオブジェクト。
	 * <pre>
	 * fで指定されたフィールドと同じ行にある、tidをもつ要素を取得します。
	 * </pre>
	 */
	getSameRowField(f, tid) {
		if (f instanceof jQuery) {
			let id = f.attr(this.getIdAttribute());
			let rid = id.replace(/\]\..+$/, "]." + tid);
			return this.get(rid);
		} else {
			let rid = f.id.replace(/\]\..+$/, "]." + tid);
			return this.getComponent(rid);
		}
	}

	/**
	 * 指定された要素の行インデックスを取得します。
	 * @param {jQuery} el 要素。
	 * @returns {Integer} 行インデックス。
	 */
	getRowIndex(el) {
		let id = el.attr(this.getIdAttribute());
		let sp = id.split(/[\[\]]/);
		return parseInt(sp[1]);
	}

	/**
	 * 必須マークを設定します。
	 */
	setRequiredMark() {
	}


	/**
	 * 指定行の指定フィールドをもつtd要素を取得します。
	 * @param {Number} row 行。
	 * @param {String} id フィールドID。
	 * @returns {jQuery} td要素。
	 */
	getTd(row, id) {
		let fid = this.id + "[" + row + "]." + id;
		return this.get(fid).parents("td:first");
	}

	/**
	 * 指定されたフィールドの同じ値のものをまとめます。
	 * @param {String} id フィールドID。
	 */
	setRowSpan(id) {
		if (this.tableData != null) {
			let v0 = null;
			let rowspan = 1;
			let startrow = 0;
			for (let i = 0; i < this.tableData.length; i++) {
				let v = this.tableData[i][id];
				logger.log("v=" + v);
				if (v != v0) {
					this.getTd(startrow, id).prop("rowspan", rowspan);
					v0 = v;
					rowspan = 1;
					startrow = i;
				} else {
					rowspan++;
					this.getTd(i, id).remove();
				}
			}
			this.getTd(startrow, id).prop("rowspan", rowspan);
		}
	}

	/**
	 * テーブルの行数を取得します。
	 * @returns {Number} テーブルの行数。
	 */
	getRowCount() {
		let n = this.find("tbody>tr").length;
		return n;
	}

}
