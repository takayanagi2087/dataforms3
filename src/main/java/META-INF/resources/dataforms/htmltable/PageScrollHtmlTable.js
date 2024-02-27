/**
 * @fileOverview {@link PageScrollHtmlTable}クラスを記述したファイルです。
 */

'use strict';

/**
 * @class PageScrollHtmlTable
 * ページスクロールHTMLテーブルクラス。
 * <pre>
 * 行の追加、削除、ドラックによる順序変更をサポートします。
 * </pre>
 * @extends HtmlTable
 */
class PageScrollHtmlTable extends HtmlTable {
	/**
	 * エレメントとの対応付け.
	 */
	attach() {
		this.sortOrder = "";
		super.attach();
		this.get().before(this.additionalHtmlText);
		this.parent.find("div.pageController :input").each((_, el) => {
			let id = $(el).attr(this.getIdAttribute());
			if ($(el).attr("name") == null) {
				$(el).attr("name", id);
			}
		});
		if (currentPage.useUniqueId) {
			// TODO: ページャーの構造は見直した方がよいかも。
			// ページャー関連のユニークIDを設定します。
			this.setPageControllerRealId("hitCount");
			this.setPageControllerRealId("pageNo");
			this.setPageControllerRealId("linesPerPage");
		}
		this.sortOrder = this.getSortOrder();
	}

	/**
	 * ページャー関連のコンポートネントにユニークなIDを設定します。
	 * @param {String} fid フィールドID。
	 */
	setPageControllerRealId(fid) {
		let comp = this.parent.getComponent(fid);
		let jq = this.parent.find("[" + this.getIdAttribute() + "='" + fid + "']");
		jq.attr("id", comp.realId);
	}

	/**
	 * ソート順の情報を取得します。
	 * @return {String} ソート順情報。
	 */
	getSortOrder() {
		let flist = this.getSortFieldList();
		let sortOrder = "";
		for (let i = 0; i < flist.length; i++) {
			if (sortOrder.length > 0) {
				sortOrder += ",";
			}
			let f = flist[i];
			sortOrder += (f.id + ":" + f.currentSortOrder);
		}
		return sortOrder;
	}

	/**
	 * ソートを行います。
	 * @param co {jQuery} ラベルのエレメント.
	 * @return {Array} ソート結果リスト。
	 *
	 */
	sortTable(col) {
		this.changeSortMark(col);
		this.sortOrder = this.getSortOrder();
		for (let i = 0; i < this.fields.length; i++) {
			let f = this.fields[i];
			f.sortOrder = f.currentSortOrder;
		}
		this.parent.get("pageNo").val("0");
		this.parent.changePage();
	}
}


