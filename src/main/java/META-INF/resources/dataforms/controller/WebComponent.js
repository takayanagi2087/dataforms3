/**
 * @fileOverview  {@link WebComponent}クラスを記述したファイルです。
 */

'use strict';

/**
 * @class WebComponent
 *
 * ウエブコンポーネントクラス。
 * <pre>
 * HTML中の各要素の情報を保持するオブジェクトの基本クラスです。
 * ID、親の要素へのポインタと子要素のマップを持ちます。
 * ver 1.xxでは複数のFormクラスにを1ページにまとめると
 * ページ内でIDが重複してしまうという問題がありました。
 * WebComponent.js以下のクラスではコンポーネントの階層を判定して
 * IDの重複があっても動作に問題発生しないようにしてあります。
 * しかしこれはHTMLの規格上違反になってしまいます。
 * そこでVer2.xxではこれを回避する機能を用意しました。
 * web.xmlのuse-unique-idをtrueに設定すると、htmlに記述したidを
 * data-idに設定し、idにはサーバから与えられたrealIdプロパティの
 * 値を設定します。realIdコンポーネントの階層情報を持ちページ内で
 * ユニークな値になります。
 *
 * </pre>
 *
 * @prop id {String} コンポーネントのID。
 * @prop realId {String} "mainDiv.queryForm"のようにコンポーネントの階層情報を持ったID。
 * @prop componentMap {Object} 所有するコンポーネントのマップです。this.componentMap[id]でIDを指定し、対応するコンポーネントを取得することができます。
 * @prop parent {WebComponent} 親となるコンポーネントです。
 *
 */
class WebComponent {
	/**
	 * コンストラクタ。
	 */
	constructor() {
		this.id = null;
		this.componentMap = {};
		this.parent = null;
		this.idPrepared = false;
	}


	/**
	 * 親フォームを取得します。
	 * @returns {Form} 親フォーム。
	 */
	getParentForm() {
		let f = this;
		while (!(f instanceof Form)) {
			f = f.parent;
		}
		return f;
	}


	/**
	 * 親となるページまたはダイアログを取得します。
	 * @returns {DataForms} 親となるページまたはダイアログ。
	 */
	getParentDataForms() {
		let f = this;
		while (!(f instanceof DataForms)) {
			f = f.parent;
		}
		return f;
	}

	/**
	 * QueryStringを取得します。
	 * @returns {Object} QueryStringを展開したオブジェクト。
	 */
	getQueryString() {
		return QueryStringUtil.parse(window.location.search);
	}

	/**
	 * ServerMethodのインスタンスを取得します。
	 * @param {String} method メソッド名。
	 * @returns {ServerMethod} ServerMethodのインスタンス。
	 * @deprecated async/awaitに対応した、getWebMethodを使用してください。
	 */
	getServerMethod(method) {
		return new ServerMethod(this.getUniqId() + "." + method);
	}

	/**
	 * WebMethodを取得します。
	 * <pre>
	 * WebMethodはServerMethodのasync,await対応版です。
	 * </pre>
	 * @param {String} method メソッド名。
	 * @returns {WebMethod} WebMethodのインスタンス。
	 */
	getWebMethod(method) {
		return new WebMethod(this.getUniqId() + "." + method);
	}

	/**
	 * idアトリビュートを返します。
	 * @return {String} idアトリビュート。
	 */
	getIdAttribute() {
		if (currentPage.useUniqueId) {
			return "data-id";
		} else {
			return "id";
		}
	}

	/**
	 * jQueryのselectorを適切に変換します。
	 * <pre>
	 * useDataIdAttribueがtrueの場合、idに関するセレクタをdata-idに変換する。
	 * </pre>
	 * @param {String} q セレクター。
	 * @returns 変換されたセレクター。
	 */
	convertSelector(q) {
		if (currentPage.useUniqueId) {
			let r = q.replace(/#([0-9A-Za-z\-_:.\\[\]]+)/g, "[data-id='$1']");
			r = r.replace(/\[id([\$\~\!\*\^]?)=['"](.*)['"]\]/g, "[data-id$1='$2']");
			return r;
		} else {
			return q;
		}
	}

	/**
	 * jQueryオブジェクトの検索を行います。
	 * <pre>
	 * jQueryセレクタを指定して、セレクタに合致する子を検索します。
	 * </pre>
	 * @param {String} q jQueryのセレクタ。
	 * @returns {jQuery} jQueryオブジェクト。
	 */
	find(q) {
		return this.get().find(this.convertSelector(q));
	}

	/**
	 * jQueryオブジェクトを取得します。
	 * <pre>
	 * コンポーネントに対応したjQueryオブジェクトを取得します。
	 * </pre>
	 * @param {String} id コンポーネントのID。
	 * <pre>
	 * 	省略した場合このインスタンスのIDで検索します。
	 *  idを指定した場合、このコンポーネントが内包する要素を検索します。
	 * </pre>
	 * @returns {jQuery} jQueryオブジェクト。
	 */
	get(id) {
		let ret = null;
		if (this.parent == null) {
			// ページトップの場合の検索
			let sel = "#" + this.selectorEscape(this.id);
			if (id != null) {
				sel = sel + " #" + id;
			}
			sel = this.convertSelector(sel);
			ret = $(sel);
//			logger.log("A:" + this.id + ":get(" + id + ") sel=" + sel + ",sel.length=" + ret.length);
		} else {
			if (currentPage.useUniqueId && this.idPrepared) {
				// ユニークIDが有効でidアトリビュート設定済
				let sel = "#" + this.selectorEscape(this.realId);
				if (id != null) {
					sel += " " + this.convertSelector("#" + id);
				}
				ret = $(sel);
//				logger.log("B:" + this.id + ":get(" + id + ") sel=" + sel + ",sel.length=" + ret.length);
			} else {
				// ユニークIDが無効またはdアトリビュート設定前
				let sel = this.getUniqSelector();
				if (id != null) {
					sel += " #" + id;
				}
				sel = this.convertSelector(sel);
				ret = $(sel);
//				logger.log("C:" + this.id + ":get(" + id + ") sel=" + sel + ",sel.length=" + ret.length);
			}
		}
		return ret;
	}

	/**
	 * サーバから送信されたクラス情報から、そのクラスのインスタンスを作成します。
	 * @param {Object} clszz クラス情報。
	 * @returns {WebComponent} 作成されたインスタンス。
	 */
	newInstance(clazz) {
		let classname = clazz.jsClass;
		let obj = eval("new " + classname + "()");
		Object.assign(obj, clazz);
		obj.parent = this;
		this.componentMap[obj.id] = obj;
		return obj;
	}

	/**
	 * HtmlTable中の要素のIDかどうかをチェックします。
	 * @param {String} id 判定するID。
	 * @returns {Boolean} HtmlTable中の要素の場合true.
	 */
	isHtmlTableElementId(id) {
		let ret = false;
		if (id.match(/^[A-Za-z0-9]+\[[0-9]+\]\.[A-Za-z0-9]+$/)) {
			ret = true;
		}
		return ret;
	}

	/**
	 * テーブルのID部分を取得します。
	 * @param {String} id HtmlTable中の各要素のID。
	 * @returns {String} テーブルのID.
	 */
	getHtmlTableId(id) {
		if (this.isHtmlTableElementId(id)) {
			let sp = id.split(/[\[\]\.]/);
			return sp[0];
		} else {
			return null;
		}
	}

	/**
	 * テーブルのカラムID部分を取得します。
	 * @param {String} id HtmlTable中の各要素のID。
	 * @returns {String} テーブルのカラムID。
	 */
	getHtmlTableColumnId(id) {
		if (this.isHtmlTableElementId(id)) {
			let sp = id.split(/[\[\]\.]/);
			let lidx = sp.length - 1;
			if (lidx >= 0) {
				return sp[lidx];
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	/**
	 * 所有オブジェクトのインスタンスを取得します。
	 * @param {String} id 所有オブジェクトのID。
	 * @returns {WebComponent} 所有オブジェクトのインスタンス。
	 */
	getComponent(id) {
		let tblid = this.getHtmlTableId(id);
		if (tblid != null) {
			let colid = this.getHtmlTableColumnId(id);
			let tbl = this.componentMap[tblid];
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
	 * jqueryのセレクタをエスケープします。
	 * @param {String} val エスケープする文字列。
	 * @returns {String} エスケープされた文字列。
	 */
	selectorEscape(val){
	    return val.replace(/[ !"#$%&'()*+,.\/:;<=>?@\[\\\]^`{|}~]/g, '\\$&');
	}

	/**
	 * コンポーネントの親子関係から、インデント文字列を作成します。
	 * <pre>
	 * ログ出力用の機能です。
	 * </pre>
	 * @returns {String} インデント文字列。
	 */
	getIndent() {
		let t = "";
		let p = this;
		while (p != null) {
			t += "\t";
			p = p.parent;
		}
		return t;
	}

	/**
	 * オブジェクトの初期化を行います。
	 * <pre>
	 * </pre>
	 */
	init() {
	}

	/**
	 * 一意なidを設定します。
	 */
	setRealId() {
		if (currentPage.useUniqueId) {
			if (this.realId.indexOf("[0]") >= 0) {
				let arg = this.id.match(/\[\d\]/);
				this.realId = this.realId.replace(/\[0\]/, arg);
			}
			let jq = null;
			if (this.parent == null) {
				jq = $(this.convertSelector('#' + this.selectorEscape(this.id)));
			} else {
				let sel = this.getUniqSelector();
				jq = $(this.convertSelector(sel));
			}
			jq.attr("id", this.realId);
		}
		this.idPrepared = true;
	}

	/**
	 * エレメントとの対応付けを行います。
	 * <pre>
	 * 各オブジェクトとHTMLの各エレメントへの対応付けを行い、イベント登録等の設定を行います。
	 * </pre>
	 */
	attach() {
		this.setRealId();
		for (let id in this.componentMap) {
			this.componentMap[id].attach();
		}
	}

	/**
	 * Cookieを取得します。
	 * @param {String} name Cookie名称。
	 * @returns {String} Cookie値。
	 */
	getCookie(name) {
	    let result = null;
	    let cookieName = name + '=';
	    let allcookies = document.cookie;
		logger.log("getCookie():allcookies = " + allcookies);
	    let sp = allcookies.split(";");
	    for (let i = 0; i < sp.length; i++) {
	        let c = sp[i].trim();
	    	if (c.indexOf(cookieName) == 0) {
	    		result = c.substring(cookieName.length);
	    		break;
	    	}
	    }
	    return result == null ? result : decodeURIComponent(result);
	}

	/**
	 * Cookieを設定します。
	 * @param {String} name Cookie名称。
	 * @param {String} val Cookie値。
	 */
	setCookie(name, val) {
		let now = new Date();
		let expires = new Date();
		expires.setTime(now.getTime() + 365*24*60*60*1000);
		let x = name + "=" + encodeURIComponent(val) + "; expires=" + expires.toGMTString() + "; path=" + currentPage.contextPath + ";";
		logger.log("setCookie():cookie x = " + x);
		document.cookie = x;
		logger.log("setCookie():document.cookie=" + document.cookie);
	}

	/**
	 * 一意に対応するセレクタを作成します。
	 * <pre>
	 * コンポーネントの階層を辿り、一意になるjQueryセレクタを作成します。
	 * </pre>
	 * @returns {String} jQueryセレクタ。
	 */
	getUniqSelector() {
		let t = this;
		let sel = "";
		while (!(t instanceof DataForms)) {
			sel = "#" + this.selectorEscape(t.id) + " " + sel;
			if (t instanceof Form) {
				if (t.parentDivId != null) {
					sel = "#" + this.selectorEscape(t.parentDivId) + " " + sel;
					return sel;
				}
			}
			t = t.parent;
		}
		sel = "#" + this.selectorEscape(t.id) + " " + sel;
//		let ret = sel.trim();
		let ret = $.trim(sel);
		return ret;
	}

	/**
	 * 一意なIDを取得します。
	 * <pre>
	 * このIDはWebMethodを呼び出す際に各コンポーネントを区別するために使用します。
	 * コンポーネントの階層を辿り、各コンポーネントのidを"."で繋げた文字列を返します。
	 * </pre>
	 *  * @returns {String} 一意なID。
	 */
	getUniqId() {
		let t = this;
		let sel = "";
		if (t instanceof HtmlTable) {
			while (!(t instanceof DataForms)) {
				if (sel.length > 0) {
					sel = "." + sel;
				}
				sel = t.id + sel;
				t = t.parent;
			}
		} else {
			while (!(t instanceof DataForms)) {
				if (!(t instanceof HtmlTable)) {
					if (sel.length > 0) {
						sel = "." + sel;
					}
					sel = t.id + sel;
				}
				t = t.parent;
			}
		}
		if (t instanceof Dialog) {
			if (sel.length > 0) {
				sel = "." + sel;
			}
			sel = t.id + sel;
		}
		return sel;
	}

	/**
	 * ブラウザの言語コードを取得します。
	 * @returns {String} ブラウザの言語コード。
	 */
	getLanguage() {
		return window.navigator.userLanguage || window.navigator.language || window.navigator.browserLanguage;
	}
}
