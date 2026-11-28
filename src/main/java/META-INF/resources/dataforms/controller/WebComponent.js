/**
 * @fileOverview {@link WebComponent}クラスを記述したファイルです。
 */
'use strict';
import { WebMethod } from '../util/WebMethod.js';
import { QueryStringUtil } from '../util/QueryStringUtil.js';
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
    /**
     * IDの一意化済フラグ。
     */
    #idPrepared = false;
    /**
     * 子のコンポーネントのマップ。
     */
    #componentMap = {};
    get componentMap() {
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
    isDataForms() {
        return false;
    }
    isForm() {
        return false;
    }
    isHtmlTable() {
        return false;
    }
    isDialog() {
        return false;
    }
    isPage() {
        return false;
    }
    getParentForm() {
        let component = this;
        while (component != null && !component.isForm()) {
            component = component.parent;
        }
        if (component == null) {
            throw new Error('Parent form was not found.');
        }
        return component;
    }
    getParentDataForms() {
        let component = this;
        while (component != null && !component.isDataForms()) {
            component = component.parent;
        }
        if (component == null) {
            throw new Error('Parent DataForms component was not found.');
        }
        return component;
    }
    getQueryString() {
        return QueryStringUtil.parse(window.location.search);
    }
    getWebMethod(method) {
        return new WebMethod(this.getUniqId() + '.' + method);
    }
    getIdAttribute() {
        if (currentPage.useUniqueId) {
            return 'data-id';
        }
        return 'id';
    }
    convertSelector(q) {
        if (currentPage.useUniqueId) {
            let ret = q.replace(/#([0-9A-Za-z\-_:.\[\]\\]+)/g, "[data-id='$1']");
            ret = ret.replace(/\[id([\$\~\!\*\^]?)=['"](.*)['"]\]/g, "[data-id$1='$2']");
            return ret;
        }
        return q;
    }
    find(q) {
        return this.get().find(this.convertSelector(q));
    }
    get(id) {
        let ret = null;
        if (this.parent == null) {
            const escapedId = this.selectorEscape(this.id ?? '');
            let sel = '#' + escapedId;
            if (id != null) {
                sel += ' #' + id;
            }
            sel = this.convertSelector(sel);
            ret = $(sel);
        }
        else {
            if (currentPage.useUniqueId && this.#idPrepared) {
                let sel = '#' + this.selectorEscape(this.realId);
                if (id != null) {
                    sel += ' ' + this.convertSelector('#' + id);
                }
                ret = $(sel);
            }
            else {
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
    #getChildImportJs(map) {
        const list = [];
        for (const key in map) {
            const obj = map[key];
            list.push(...this.#getImportJs(obj));
            list.push(obj);
        }
        return list;
    }
    #getImportJs(obj) {
        let list = [];
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
    uniq(list) {
        const ret = [];
        for (let i = 0; i < list.length; i++) {
            if (ret.indexOf(list[i]) < 0) {
                ret.push(list[i]);
            }
        }
        return ret;
    }
    static #moduleMap = {};
    async dynamicImport(pageInfo) {
        logger.log('pageInfo=', pageInfo);
        let list = this.#getImportJs(pageInfo);
        list = this.uniq(list);
        logger.log('jsImportList:', list);
        for (let i = 0; i < list.length; i++) {
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
    newInstance(clazz) {
        const classname = clazz.jsClass;
        const module = WebComponent.#moduleMap[classname];
        if (module == null) {
            throw new Error(`Module was not found: ${classname}`);
        }
        const constructor = module[classname];
        if (typeof constructor !== 'function') {
            throw new Error(`Class was not found: ${classname}`);
        }
        const obj = new constructor();
        Object.assign(obj, clazz);
        obj.parent = this;
        this.componentMap[obj.id ?? ''] = obj;
        return obj;
    }
    isHtmlTableElementId(id) {
        return /^[A-Za-z0-9]+\[[0-9]+\]\.[A-Za-z0-9]+$/.test(id);
    }
    getHtmlTableId(id) {
        if (this.isHtmlTableElementId(id)) {
            const sp = id.split(/[\[\]\.]/);
            return sp[0];
        }
        return null;
    }
    getHtmlTableColumnId(id) {
        if (this.isHtmlTableElementId(id)) {
            const sp = id.split(/[\[\]\.]/);
            const lidx = sp.length - 1;
            return lidx >= 0 ? sp[lidx] : null;
        }
        return null;
    }
    getComponent(id) {
        const tblid = this.getHtmlTableId(id);
        if (tblid != null) {
            const colid = this.getHtmlTableColumnId(id);
            const tbl = this.componentMap[tblid];
            if (tbl == null || colid == null) {
                return null;
            }
            const fields = tbl.fields;
            for (let i = 0; i < fields.length; i++) {
                if (fields[i].id === colid) {
                    const field = new fields[i].constructor();
                    Object.assign(field, fields[i]);
                    field.id = id;
                    return field;
                }
            }
            return null;
        }
        return this.componentMap[id] ?? null;
    }
    selectorEscape(val) {
        return val.replace(/[ !"#$%&'()*+,.\/:;<=>?@\[\\\]^`{|}~]/g, '\\$&');
    }
    getIndent() {
        let t = '';
        let p = this;
        while (p != null) {
            t += '\t';
            p = p.parent;
        }
        return t;
    }
    init() {
    }
    setRealId() {
        if (currentPage.useUniqueId) {
            if (this.realId.indexOf('[0]') >= 0) {
                const arg = this.id?.match(/\[\d\]/)?.[0];
                this.realId = arg == null
                    ? this.realId
                    : this.realId.replace(/\[0\]/, arg);
            }
            let jq;
            if (this.parent == null) {
                jq = $(this.convertSelector('#' + this.selectorEscape(this.id ?? '')));
            }
            else {
                const sel = this.getUniqSelector();
                jq = $(this.convertSelector(sel));
            }
            jq.attr('id', this.realId);
        }
        this.#idPrepared = true;
    }
    attach() {
        this.setRealId();
        for (const id in this.componentMap) {
            this.componentMap[id].attach();
        }
    }
    getCookie(name) {
        let result = null;
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
    setCookie(name, val) {
        const now = new Date();
        const expires = new Date();
        expires.setTime(now.getTime() + 365 * 24 * 60 * 60 * 1000);
        const x = name
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
    getUniqSelector() {
        let t = this;
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
    getUniqId() {
        let t = this;
        let sel = '';
        if (t.isHtmlTable()) {
            while (t != null && !t.isDataForms()) {
                if (sel.length > 0) {
                    sel = '.' + sel;
                }
                sel = (t.id ?? '') + sel;
                t = t.parent;
            }
        }
        else {
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
    getLanguage() {
        return window.navigator.userLanguage
            ?? window.navigator.language
            ?? window.navigator.browserLanguage;
    }
    setEventId(ev) {
        const target = ev.currentTarget;
        if (target != null) {
            this.id = $(target).attr(this.getIdAttribute()) ?? null;
            this.realId = $(target).attr('id') ?? '';
        }
    }
}
