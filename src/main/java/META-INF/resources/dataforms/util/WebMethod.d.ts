/**
 * @fileOverview {@link WebMethod}クラスを記述したファイルです。
 */
interface JQueryElementCollection {
    length: number;
    attr(name: string): string | undefined;
    val(): unknown;
    each(callback: (index: number, element: Element) => void): void;
}
interface JQueryForm {
    find(selector: string): JQueryElementCollection;
    serialize(): string;
    get(index: number): Element;
}
type WebMethodParam = string | FormData | Record<string, unknown> | null | undefined;
interface JsonResult {
    key: string;
    message: string;
}
interface JsonResponseData {
    status?: unknown;
    result?: JsonResult;
    [key: string]: unknown;
}
/**
 * @class WebMethod
 *
 * async, await対応のサーバメソッドクラス。
 */
export declare class WebMethod {
    private static get PARAM_TYPE_URLENCODED();
    private static get PARAM_TYPE_JSON();
    private static get PARAM_TYPE_FORM_DATA();
    private serverUrl;
    private method;
    private contentType;
    private init;
    private headers?;
    private paramType?;
    constructor(m: string);
    /**
     * アプリケーション例外発生時の処理です。
     */
    protected onCatchApplicationException(data: JsonResponseData): Promise<never | void>;
    /**
     * システム例外発生時の処理を行います。
     */
    protected onCatchError(): void;
    /**
     * 送信するパラメータを設定します。
     */
    protected setInitBody(method: string, param: Exclude<WebMethodParam, null | undefined>): string;
    /**
     * サーバー上のメソッドをfetch APIを使用して呼び出します。
     */
    protected callMethod(method: string, param: WebMethodParam): Promise<Response | undefined>;
    /**
     * Responseに応じた適切なオブジェクトを取得します。
     */
    protected getResponseObject(r: Response): Promise<JsonResponseData | Record<string, unknown> | string | Blob | null>;
    /**
     * 非同期メソッドを呼び出します。
     */
    execute(param: WebMethodParam): Promise<JsonResponseData | Record<string, unknown> | string | Blob | null>;
    /**
     * UPLOADフォームかどうかを判定します。
     */
    /**
     * 指定したformをメソッドに対してPostします。
     */
    submit(form: JQueryForm): Promise<JsonResponseData | Record<string, unknown> | string | Blob | null>;
    /**
     * ファイルアップロードなしでPostします。
     */
    submitWithoutFile(form: JQueryForm): Promise<JsonResponseData | Record<string, unknown> | string | Blob | null>;
    /**
     * FormData形式でPostします。
     */
    submitWithFile(form: JQueryForm): Promise<JsonResponseData | Record<string, unknown> | string | Blob | null>;
}
export {};
//# sourceMappingURL=WebMethod.d.ts.map