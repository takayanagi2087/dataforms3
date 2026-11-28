/**
 * @fileOverview {@link WebComponent}クラスを記述したファイルです。
 */

'use strict';

import { WebMethod } from '../util/WebMethod.js';
import { QueryStringUtil } from '../util/QueryStringUtil.js';

interface PageInfoComponent {
	id?: string;
	jsPath?: string;
	jsClass?: string;
	formMap?: Record<string, PageInfoComponent>;
	dialogMap?: Record<string, PageInfoComponent>;
	fieldList?: Record<string, PageInfoComponent>;
	htmlTableList?: Record<string, PageInfoComponent>;
	contextMenuList?: Record<string, PageInfoComponent>;
	validatorList?: Record<string, PageInfoComponent>;
}

interface ComponentClassInfo {
	id: string;
	jsClass: string;
	[key: string]: unknown;
}

interface PageRuntime {
	contextPath: string;
}


declare global {

	interface Navigator {
		userLanguage?: string;
		browserLanguage?: string;
	}

}


/**
 * @class WebComponent
 *
 * ウエブコンポーネントクラス。
 * 
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
 * 値を設定します。realIdはコンポーネントの階層情報を持ちページ内で
 * ユニークな値になります。
 */
export class WebComponent {
	declare id: string | null;
	declare realId: string;
	declare parent: WebComponent | null;
	declare parentDivId: string | null;
	declare menuJs: string | null;
	declare sideMenuJs: string | null;

	/**
	 * IDの一意化済フラグ。
	 */
	#idPrepared = false;

	/**
	 * 子のコンポーネントのマップ。
	 */
	#componentMap: Record<string, WebComponent> = {};

	get componentMap(): Record<string, WebComponent> {
		return this.#componentMap;
	}

	/**
	 * コンストラクタ。
	 */
	constructor() {
		this.id = null;
		this.realId = '';
		this.parent = null;
		this.parentDivId = null;
		this.menuJs = null;
		this.sideMenuJs = null;
		this.#componentMap = {};
		this.#idPrepared = false;
	}

	/**
	 * DataFormsから派生したクラスかどうかを判定します。
	 * @return DataFormsから派生したクラスの場合true。
	 */
	public isDataForms(): boolean {
		return false;
	}

	/**
	 * Formから派生したクラスかどうかを判定します。
	 * @return Formから派生したクラスの場合true。
	 */
	public isForm(): boolean {
		return false;
	}

	/**
	 * HtmlTableから派生したクラスかどうかを判定します。
	 * @return HtmlTableから派生したクラスの場合true。
	 */
	public isHtmlTable(): boolean {
		return false;
	}

	/**
	 * Dialogから派生したクラスかどうかを判定します。
	 * @return Dialogから派生したクラスの場合true。
	 */
	public isDialog(): boolean {
		return false;
	}

	/**
	 * Pagegから派生したクラスかどうかを判定します。
	 * @return Pageから派生したクラスの場合true。
	 */
	public isPage(): boolean {
		return false;
	}

	/**
	 * 親フォームを取得します。
	 * @returns  親フォーム。
	 * FIXME: 最終的に戻り値をFormに限定する。
	 */
	public getParentForm(): WebComponent {
		let component: WebComponent | null = this;

		while (component != null && !component.isForm()) {
			component = component.parent;
		}

		if (component == null) {
			throw new Error('Parent form was not found.');
		}

		return component;
	}

	/**
	 * 親となるページまたはダイアログを取得します。
	 * @returns 親となるページまたはダイアログ。
	 */
	public getParentDataForms(): WebComponent {
		let component: WebComponent | null = this;

		while (component != null && !component.isDataForms()) {
			component = component.parent;
		}

		if (component == null) {
			throw new Error('Parent DataForms component was not found.');
		}

		return component;
	}

	/**
	 * QueryStringを取得します。
	 * @returns QueryStringを展開したオブジェクト。
	 */
	public getQueryString(): Record<string, string | null> {
		return QueryStringUtil.parse(window.location.search);
	}

	/**
	 * WebMethodを取得します。
	 * @param method メソッド名。
	 * @returns WebMethodのインスタンス。
	 */
	public getWebMethod(method: string): WebMethod {
		return new WebMethod(this.getUniqId() + '.' + method);
	}

	/**
	 * idアトリビュートを返します。
	 * @return idアトリビュート。
	 */
	public getIdAttribute(): string {
		return 'data-id';
	}

	
	/**
	 * jQueryのselectorを適切に変換します。
	 * <pre>
	 * idに関するセレクタをdata-idに変換する。
	 * </pre>
	 * @param {String} q セレクター。
	 * @returns 変換されたセレクター。
	 */
	convertSelector(q: string): string {
		let ret = q.replace(/#([0-9A-Za-z\-_:.\[\]\\]+)/g, "[data-id='$1']");
		ret = ret.replace(/\[id([\$\~\!\*\^]?)=['"](.*)['"]\]/g, "[data-id$1='$2']");
		return ret;
	}

	/**
	 * jQueryオブジェクトの検索を行います。
	 * jQueryセレクタを指定して、セレクタに合致する子を検索します。
	 * @param q jQueryのセレクタ。
	 * @returns jQueryオブジェクト。
	 */
	find(q: string): JQuery {
		return this.get()!.find(this.convertSelector(q));
	}

	/**
	 * jQueryオブジェクトを取得します。
	 * コンポーネントに対応したjQueryオブジェクトを取得します。
	 * @param id コンポーネントのID。
	 * 	省略した場合このインスタンスのIDで検索します。
	 *  idを指定した場合、このコンポーネントが内包する要素を検索します。
	 * @returns jQueryオブジェクト。
	 */
	get(id?: string): JQuery | null {
		let ret: JQuery | null = null;

		if (this.parent == null) {
			const escapedId = this.selectorEscape(this.id ?? '');
			let sel = '#' + escapedId;

			if (id != null) {
				sel += ' #' + id;
		 }

			sel = this.convertSelector(sel);
			ret = $(sel);
		} else {
			if (this.#idPrepared) {
				let sel = '#' + this.selectorEscape(this.realId);

				if (id != null) {
					sel += ' ' + this.convertSelector('#' + id);
				}

				ret = $(sel);
			} else {
				let sel = this.getUniqSelector();

				if (id != null) {
					sel += ' #' + id;
				}

				sel = this.convertSelector(sel);
				ret = $(sel);
			}
		}

		return ret;
	}

	/**
	 * ダイナミックインポートが必要な子モジュールのリストを取得する。
	 * @param  map ページ情報。
	 * @returns jsファイル名のリスト。
	 */
	#getChildImportJs(map: Record<string, PageInfoComponent>): PageInfoComponent[] {
		const list: PageInfoComponent[] = [];

        for (const key in map) {
            const obj = map[key];
            list.push(...this.#getImportJs(obj));
            list.push(obj);
        }

        return list;
    }

	/**
	 * ダイナミックインポートが必要なモジュールのリストを取得する。
	 * @param obj ページ情報。
	 * @returns jsファイル名のリスト。
	 */
	#getImportJs(obj: PageInfoComponent): PageInfoComponent[] {
		let list: PageInfoComponent[] = [];
		
		if (obj.formMap != null) {
			list = list.concat(this.#getChildImportJs(obj.formMap));
		}
		
		if (obj.dialogMap != null) {
			list = list.concat(this.#getChildImportJs(obj.dialogMap));
		}
		
		if (obj.fieldList != null) {
			list = list.concat(this.#getChildImportJs(obj.fieldList));
		}
		
		if (obj.htmlTableList != null) {
			list = list.concat(this.#getChildImportJs(obj.htmlTableList));
		}
		
		if (obj.contextMenuList != null) {
			list = list.concat(this.#getChildImportJs(obj.contextMenuList));
		}
		
		if (obj.validatorList != null) {
			list = list.concat(this.#getChildImportJs(obj.validatorList));
		}
		
		return list;
	}

	/**
	 * 配列内の重複する情報を削除した配列を作成します。
	 * @param list 入力配列。
	 * @return 出力配列。
	 */
	uniq<T>(list: T[]): T[] {
		const ret: T[] = [];
		
		for (let i = 0;i < list.length;i++) {
			if (ret.indexOf(list[i]) < 0) {
				ret.push(list[i]);
			}
		}
		
		return ret;
	}

	/**
	 * ダイナミックインポートしたモジュールマップ。
	 */
	static #moduleMap: Record<string, unknown> = {};

	/**
	 * ページ情報に含まれるJavascriptクラスの内インポートされていないものをダイナミックインポートする。
	 * @param pageInfo ページ情報。
	 */
	public async dynamicImport(pageInfo: PageInfoComponent): Promise<void> {
		logger.log('pageInfo=', pageInfo);
		
		let list = this.#getImportJs(pageInfo);
		list = this.uniq(list);
		
		logger.log('jsImportList:', list);
		
		for (let i = 0;i < list.length;i++) {
			const obj = list[i];
			
			if (obj.jsPath == null || obj.jsClass == null) {
			    continue;
			}
			
			const module = await import(currentPage.contextPath + obj.jsPath);
			WebComponent.#moduleMap[obj.jsClass] = module;
		}
		
		if (this.menuJs != null) {
			logger.log('menuJs=' + this.menuJs);
			const module = await import(currentPage.contextPath + this.menuJs);
			WebComponent.#moduleMap.Menu = module;
		}
		
		if (this.sideMenuJs != null) {
			logger.log('sideMenuJs=' + this.sideMenuJs);
			const module = await import(currentPage.contextPath + this.sideMenuJs);
			WebComponent.#moduleMap.SideMenu = module;
		}
		
		logger.log('#moduleMap=', WebComponent.#moduleMap);
	}

	/**
	 * サーバから送信されたクラス情報から、そのクラスのインスタンスを作成します。
	 * @param clszz クラス情報。
	 * @returns 作成されたインスタンス。
	 */
	public newInstance(clazz: ComponentClassInfo): WebComponent {
		const classname = clazz.jsClass;
		const module = WebComponent.#moduleMap[classname];
		
		if (module == null) {
			throw new Error(`Module was not found: ${classname}`);
		}
		
		const constructor = (module as Record<string, unknown>)[classname];
		
		if (typeof constructor !== 'function') {
			throw new Error(`Class was not found: ${classname}`);
		}
		
		const obj = new (constructor as new () => WebComponent)();
		Object.assign(obj, clazz);
		obj.parent = this;
		this.componentMap[obj.id ?? ''] = obj;
		
		return obj;
	}

	/**
	 * HtmlTable中の要素のIDかどうかをチェックします。
	 * @param id 判定するID。
	 * @returns HtmlTable中の要素の場合true.
	 */
	public isHtmlTableElementId(id: string): boolean {
		return /^[A-Za-z0-9]+\[[0-9]+\]\.[A-Za-z0-9]+$/.test(id);
	}

	/**
	 * テーブルのID部分を取得します。
	 * @param id HtmlTable中の各要素のID。
	 * @returns テーブルのID.
	 */
	public getHtmlTableId(id: string): string | null {
		if (this.isHtmlTableElementId(id)) {
			const sp = id.split(/[\[\]\.]/);
			return sp[0];
		}
		return null;
	}

	/**
	 * テーブルのカラムID部分を取得します。
	 * @param id HtmlTable中の各要素のID。
	 * @returns テーブルのカラムID。
	 */
	public getHtmlTableColumnId(id: string): string | null {
		if (this.isHtmlTableElementId(id)) {
			const sp = id.split(/[\[\]\.]/);
			const lidx = sp.length - 1;
			return lidx >= 0 ? sp[lidx] : null;
		}
		return null;
	}

	/**
	 * 所有コンポーネントのインスタンスを取得します。
	 * @param id コンポーネントのID。
	 * @returns コンポーネントのインスタンス。
	 */
	public getComponent(id: string): WebComponent | null {
		const tblid = this.getHtmlTableId(id);
		
		if (tblid != null) {
			const colid = this.getHtmlTableColumnId(id);
			const tbl = this.componentMap[tblid];
			
			if (tbl == null || colid == null) {
				return null;
			}
			
			const fields = (tbl as unknown as {
				fields: Array<WebComponent & { id: string }>;
			}).fields;
			
			for (let i = 0;i < fields.length;i++) {
				if (fields[i].id === colid) {
					const field = new (fields[i].constructor as new () => WebComponent)();
					Object.assign(field, fields[i]);
					field.id = id;
					return field;
				}
			}
			
			return null;
		}
		
		return this.componentMap[id] ?? null;
	}

	/**
	 * JQueryのセレクタをエスケープします。
	 * @param val エスケープする文字列。
	 * @returns エスケープされた文字列。
	 */
	public selectorEscape(val: string): string {
		return val.replace(/[ !"#$%&'()*+,.\/:;<=>?@\[\\\]^`{|}~]/g, '\\$&');
	}

	/**
	 * コンポーネントの親子関係から、インデント文字列を作成します。
	 * ログ出力用の機能です。
	 * @returns インデント文字列。
	 */
	private getIndent(): string {
		let t = '';
		let p: WebComponent | null = this;

		while (p != null) {
			t += '\t';
			p = p.parent;
		}
		return t;
	}

	/**
	 * オブジェクトの初期化を行います。
	 */
	public init(): void {
	}

	/**
	 * 一意なidを設定します。
	 */
	private setRealId(): void {
		if (this.realId.indexOf('[0]') >= 0) {
			const arg = this.id?.match(/\[\d\]/)?.[0];
			this.realId = arg == null
				? this.realId
				: this.realId.replace(/\[0\]/, arg);
		}
		
		let jq: JQuery;
		
		if (this.parent == null) {
			jq = $(this.convertSelector('#' + this.selectorEscape(this.id ?? '')));
		} else {
			const sel = this.getUniqSelector();
			jq = $(this.convertSelector(sel));
		}
		
		jq.attr('id', this.realId);
		
		this.#idPrepared = true;
	}

	/**
	 * エレメントとの対応付けを行います。
	 * 各オブジェクトとHTMLの各エレメントへの対応付けを行い、イベント登録等の設定を行います。
	 */
	public attach(): void {
		this.setRealId();

		for (const id in this.componentMap) {
			this.componentMap[id].attach();
		}
	}

	/**
	 * Cookieを取得します。
	 * @param name Cookie名称。
	 * @returns Cookie値。
	 */
	public getCookie(name: string): string | null {
		let result: string | null = null;
		const cookieName = name + '=';
		const allcookies = document.cookie;
		logger.log('getCookie():allcookies = ' + allcookies);

		const sp = allcookies.split(';');

		for (let i = 0; i < sp.length; i++) {
			const c = sp[i].trim();

			if (c.indexOf(cookieName) === 0) {
				result = c.substring(cookieName.length);
				break;
			}
		}
		return result == null ? null : decodeURIComponent(result);
	}

	/**
	 * Cookieを設定します。
	 * @param name Cookie名称。
	 * @param val Cookie値。
	 */
	public setCookie(name: string, val: string): void {
		const now = new Date();
		const expires = new Date();
		expires.setTime(now.getTime() + 365 * 24 * 60 * 60 * 1000);

		const x =
			name
			+ '='
			+ encodeURIComponent(val)
			+ '; expires='
			+ expires.toUTCString()
			+ '; path='
			+ currentPage.contextPath
			+ ';';

		logger.log('setCookie():cookie x = ' + x);
		document.cookie = x;
		logger.log('document.cookie=' + document.cookie);
	}

	/**
	 * 一意に対応するセレクタを作成します。
	 * <pre>
	 * コンポーネントの階層を辿り、一意になるjQueryセレクタを作成します。
	 * </pre>
	 * @returns jQueryセレクタ。
	 */
	private getUniqSelector(): string {
		let t: WebComponent | null = this;
		let sel = '';
		
		while (t != null && !t.isDataForms()) {
			sel = '#' + this.selectorEscape(t.id ?? '') + ' ' + sel;
			
			if (t.isForm()) {
				if (t.parentDivId != null) {
					sel = '#' + this.selectorEscape(t.parentDivId) + ' ' + sel;
					return sel;
				}
			}
			
			t = t.parent;
		}
		
		if (t == null) {
			return sel.trim();
		}
		
		sel = '#' + this.selectorEscape(t.id ?? '') + ' ' + sel;
		return sel.trim();
	}

	/**
	 * 一意なIDを取得します。
	 * このIDはWebMethodを呼び出す際に各コンポーネントを区別するために使用します。
	 * コンポーネントの階層を辿り、各コンポーネントのidを"."で繋げた文字列を返します。
	 * @returns 一意なID。
	 */
	public getUniqId(): string {
		let t: WebComponent | null = this;
		let sel = '';
		
		if (t.isHtmlTable()) {
			while (t != null && !t.isDataForms()) {
				if (sel.length > 0) {
					sel = '.' + sel;
				}
				sel = (t.id ?? '') + sel;
				t = t.parent;
			}
		} else {
			while (t != null && !t.isDataForms()) {
				if (!t.isHtmlTable()) {
					if (sel.length > 0) {
						sel = '.' + sel;
					}
					sel = (t.id ?? '') + sel;
				}
				t = t.parent;
			}
		}
		
		if (t != null && t.isDialog()) {
			if (sel.length > 0) {
				sel = '.' + sel;
			}
			sel = (t.id ?? '') + sel;
		}
		
		return sel;
	}

	/**
	 * ブラウザの言語コードを取得します。
	 * @returns ブラウザの言語コード。
	 */
	public getLanguage(): string | undefined {
		return window.navigator.userLanguage
			|| window.navigator.language
			|| window.navigator.browserLanguage;
	}

	/**
	 * イベントが発生した要素のIDをWebComponentに設定します。
	 * 編集中のテーブルのWebComponentはWebComponentのIDがずれることがあるので、
	 * イベントの発生時にIDを合わせるために使用します。
	 * @param ev イベント情報。
	 */
	public setEventId(ev: JQuery.TriggeredEvent): void {
		const target = ev.currentTarget as Element | null;
		if (target != null) {
			this.id = $(target).attr(this.getIdAttribute()) ?? null;
			this.realId = $(target).attr('id') ?? '';
		}
	}
}