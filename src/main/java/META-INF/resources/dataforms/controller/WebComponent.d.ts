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
    id: string;
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
    /**
     * DataFormsから派生したクラスかどうかを判定します。
     * @return DataFormsから派生したクラスの場合true。
     */
    isDataForms(): boolean;
    /**
     * Formから派生したクラスかどうかを判定します。
     * @return Formから派生したクラスの場合true。
     */
    isForm(): boolean;
    /**
     * HtmlTableから派生したクラスかどうかを判定します。
     * @return HtmlTableから派生したクラスの場合true。
     */
    isHtmlTable(): boolean;
    /**
     * Dialogから派生したクラスかどうかを判定します。
     * @return Dialogから派生したクラスの場合true。
     */
    isDialog(): boolean;
    /**
     * Pagegから派生したクラスかどうかを判定します。
     * @return Pageから派生したクラスの場合true。
     */
    isPage(): boolean;
    /**
     * 親フォームを取得します。
     * @returns  親フォーム。
     * FIXME: 最終的に戻り値をFormに限定する。
     */
    getParentForm(): WebComponent;
    /**
     * 親となるページまたはダイアログを取得します。
     * @returns 親となるページまたはダイアログ。
     */
    getParentDataForms(): WebComponent;
    /**
     * QueryStringを取得します。
     * @returns QueryStringを展開したオブジェクト。
     */
    getQueryString(): Record<string, string | null>;
    /**
     * WebMethodを取得します。
     * @param method メソッド名。
     * @returns WebMethodのインスタンス。
     */
    getWebMethod(method: string): WebMethod;
    /**
     * idアトリビュートを返します。
     * @return idアトリビュート。
     */
    getIdAttribute(): string;
    /**
     * jQueryのselectorを適切に変換します。
     * <pre>
     * idに関するセレクタをdata-idに変換する。
     * </pre>
     * @param {String} q セレクター。
     * @returns 変換されたセレクター。
     */
    convertSelector(q: string): string;
    /**
     * jQueryオブジェクトの検索を行います。
     * jQueryセレクタを指定して、セレクタに合致する子を検索します。
     * @param q jQueryのセレクタ。
     * @returns jQueryオブジェクト。
     */
    find(q: string): JQuery;
    /**
     * jQueryオブジェクトを取得します。
     * コンポーネントに対応したjQueryオブジェクトを取得します。
     * @param id コンポーネントのID。
     * 	省略した場合このインスタンスのIDで検索します。
     *  idを指定した場合、このコンポーネントが内包する要素を検索します。
     * @returns jQueryオブジェクト。
     */
    get(id?: string): JQuery | null;
    /**
     * 配列内の重複する情報を削除した配列を作成します。
     * @param list 入力配列。
     * @return 出力配列。
     */
    uniq<T>(list: T[]): T[];
    /**
     * ページ情報に含まれるJavascriptクラスの内インポートされていないものをダイナミックインポートする。
     * @param pageInfo ページ情報。
     */
    dynamicImport(pageInfo: PageInfoComponent): Promise<void>;
    /**
     * サーバから送信されたクラス情報から、そのクラスのインスタンスを作成します。
     * @param clszz クラス情報。
     * @returns 作成されたインスタンス。
     */
    newInstance(clazz: ComponentClassInfo): WebComponent;
    /**
     * HtmlTable中の要素のIDかどうかをチェックします。
     * @param id 判定するID。
     * @returns HtmlTable中の要素の場合true.
     */
    isHtmlTableElementId(id: string): boolean;
    /**
     * テーブルのID部分を取得します。
     * @param id HtmlTable中の各要素のID。
     * @returns テーブルのID.
     */
    getHtmlTableId(id: string): string | null;
    /**
     * テーブルのカラムID部分を取得します。
     * @param id HtmlTable中の各要素のID。
     * @returns テーブルのカラムID。
     */
    getHtmlTableColumnId(id: string): string | null;
    /**
     * 所有コンポーネントのインスタンスを取得します。
     * @param id コンポーネントのID。
     * @returns コンポーネントのインスタンス。
     */
    getComponent(id: string): WebComponent | null;
    /**
     * JQueryのセレクタをエスケープします。
     * @param val エスケープする文字列。
     * @returns エスケープされた文字列。
     */
    selectorEscape(val: string): string;
    /**
     * コンポーネントの親子関係から、インデント文字列を作成します。
     * ログ出力用の機能です。
     * @returns インデント文字列。
     */
    private getIndent;
    /**
     * オブジェクトの初期化を行います。
     */
    init(): void;
    /**
     * 一意なidを設定します。
     */
    private setRealId;
    /**
     * エレメントとの対応付けを行います。
     * 各オブジェクトとHTMLの各エレメントへの対応付けを行い、イベント登録等の設定を行います。
     */
    attach(): void;
    /**
     * Cookieを取得します。
     * @param name Cookie名称。
     * @returns Cookie値。
     */
    getCookie(name: string): string | null;
    /**
     * Cookieを設定します。
     * @param name Cookie名称。
     * @param val Cookie値。
     */
    setCookie(name: string, val: string): void;
    /**
     * 一意に対応するセレクタを作成します。
     * <pre>
     * コンポーネントの階層を辿り、一意になるjQueryセレクタを作成します。
     * </pre>
     * @returns jQueryセレクタ。
     */
    private getUniqSelector;
    /**
     * 一意なIDを取得します。
     * このIDはWebMethodを呼び出す際に各コンポーネントを区別するために使用します。
     * コンポーネントの階層を辿り、各コンポーネントのidを"."で繋げた文字列を返します。
     * @returns 一意なID。
     */
    getUniqId(): string;
    /**
     * ブラウザの言語コードを取得します。
     * @returns ブラウザの言語コード。
     */
    getLanguage(): string | undefined;
    /**
     * イベントが発生した要素のIDをWebComponentに設定します。
     * 編集中のテーブルのWebComponentはWebComponentのIDがずれることがあるので、
     * イベントの発生時にIDを合わせるために使用します。
     * @param ev イベント情報。
     */
    setEventId(ev: JQuery.TriggeredEvent): void;
}
export {};
//# sourceMappingURL=WebComponent.d.ts.map