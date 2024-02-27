/**
 * @fileOverview {@link Menu}クラスを記述したファイルです。
 */

'use strict';


/**
 * @class Menu
 * メニュークラス。
 * <pre>
 * このクラスに対応するHTMLの記述は以下のようなものになります。
 * 以下の例はid="menu"の場合です。
 * <code>
 * 	&lt;div id=&quot;menu&quot; style=&quot;width:100%;&quot;&gt;
 *		&lt;table id=&quot;menu[0]&quot; style=&quot;width:100%;&quot;&gt;
 *			&lt;thead&gt;
 *				&lt;tr&gt;
 *					&lt;th id=&quot;menu[0].name&quot; colspan=&quot;2&quot;&gt;&lt;/th&gt;
 *				&lt;/tr&gt;
 *			&lt;/thead&gt;
 *			&lt;tbody id=&quot;menu[0].pageList&quot;&gt;
 *				&lt;tr&gt;
 *					&lt;td style=&quot;width:150px;&quot;&gt;
 *						&lt;a id=&quot;menu[0].pageList[0].url&quot;&gt;
 *							&lt;span id=&quot;menu[0].pageList[0].name&quot;&gt;Menu item&lt;/span&gt;
 *						&lt;/a&gt;
 *					&lt;/td&gt;
 *					&lt;td&gt;
 *						&lt;span id=&quot;menu[0].pageList[0].description&quot;&gt;Description&lt;/span&gt;
 *					&lt;/td&gt;
 *				&lt;/tr&gt;
 *			&lt;/tbody&gt;
 *		&lt;/table&gt;
 *	&lt;/div&gt;
 * </code>
 *
 * </pre>
 * @extends WebComponent
 */
class Menu extends WebComponent {
	/**
	 * HTMLエレメントとの対応付けを行います。
	 */
	attach() {
		super.attach();
		this.getMenuLayout();
		this.update();
	}

	/**
	 * メニューのレイアウト情報を取得します。
	 */
	getMenuLayout() {
		this.menuLayout = this.get().html();
	}

	/**
	 * メニューの表示内容を更新します。
	 */
	update() {
		let mglist = this.menuGroupList;
		let menuHtml = this.getMenuHtml(mglist);
		let menu = this.get();
		menu.html(menuHtml);
	}

	/**
	 * メニューの内容を展開したHTML作成します。
	 * @param {Array} mglist メニューグループリスト.
	 * @returns {String} HTML.
	 */
	getMenuHtml(mglist) {
	    let ret = "";
		let pat = new RegExp(this.id + "\\[0\\]", "g");
		let patl = new RegExp("pageList\\[0\\]", "g");
		for (let i = 0; i < mglist.length; i++) {
			let mg = this.menuLayout.replace(pat, this.id + "[" + i + "]")
			let q = $("<div>" + mg + "</div>");
			let menu = $(q.find(this.convertSelector("[id$='\\.pageList']")).get()[0]).html();
			let plist = q.find(this.convertSelector("[id$='\\.pageList']"));
			plist.empty();
			for (let j = 0; j < mglist[i].pageList.length; j++) {
				plist.append(menu.replace(patl, "pageList[" + j +"]"));
			}
			q.find(this.convertSelector("#" + this.id + "\\[" + i + "\\]\\.name")).html(mglist[i].name);
			q.find(this.convertSelector("#" + this.id + "\\[" + i + "\\]\\.name")).attr("data-menu-group-id", mglist[i].id);
			for (let j = 0; j < mglist[i].pageList.length; j++) {
				plist.find(this.convertSelector("[id$='\\.pageList\\[" + j + "\\]\\.url']")).attr("href", mglist[i].pageList[j].url);
				if (mglist[i].pageList[j].menuTarget != null) {
					plist.find(this.convertSelector("[id$='\\.pageList\\[" + j + "\\]\\.url']")).attr("target", mglist[i].pageList[j].menuTarget);
				}
				plist.find(this.convertSelector("[id$='\\.pageList\\[" + j + "\\]\\.name']")).html(mglist[i].pageList[j].menuName);
				plist.find(this.convertSelector("[id$='\\.pageList\\[" + j + "\\]\\.description']")).html(mglist[i].pageList[j].description);
			}
			ret += q.html();
		}
		return ret;
	}

}


