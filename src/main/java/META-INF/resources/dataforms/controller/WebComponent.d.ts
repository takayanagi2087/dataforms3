/**
 * @fileOverview {@link WebComponent}クラスを記述したファイルです。
 */
import { WebMethod } from '../util/WebMethod.js';
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
export declare class WebComponent {
    #private;
    id: string | null;
    realId: string;
    parent: WebComponent | null;
    parentDivId: string | null;
    menuJs: string | null;
    sideMenuJs: string | null;
    get componentMap(): Record<string, WebComponent>;
    /**
     * コンストラクタ。
     */
    constructor();
    isDataForms(): boolean;
    isForm(): boolean;
    isHtmlTable(): boolean;
    isDialog(): boolean;
    isPage(): boolean;
    getParentForm(): WebComponent;
    getParentDataForms(): WebComponent;
    getQueryString(): Record<string, string | null>;
    getWebMethod(method: string): WebMethod;
    getIdAttribute(): string;
    convertSelector(q: string): string;
    find(q: string): JQuery;
    get(id?: string): JQuery | null;
    uniq<T>(list: T[]): T[];
    dynamicImport(pageInfo: PageInfoComponent): Promise<void>;
    newInstance(clazz: ComponentClassInfo): WebComponent;
    isHtmlTableElementId(id: string): boolean;
    getHtmlTableId(id: string): string | null;
    getHtmlTableColumnId(id: string): string | null;
    getComponent(id: string): WebComponent | null;
    selectorEscape(val: string): string;
    getIndent(): string;
    init(): void;
    setRealId(): void;
    attach(): void;
    getCookie(name: string): string | null;
    setCookie(name: string, val: string): void;
    getUniqSelector(): string;
    getUniqId(): string;
    getLanguage(): string | undefined;
    setEventId(ev: JQuery.TriggeredEvent): void;
}
export {};
//# sourceMappingURL=WebComponent.d.ts.map