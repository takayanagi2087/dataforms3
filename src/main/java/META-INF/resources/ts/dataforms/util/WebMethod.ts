/**
 * @fileOverview {@link WebMethod}クラスを記述したファイルです。
 */

'use strict';

import { MessagesUtil } from './MessagesUtil.js';
import { JsonResponse } from '../response/JsonResponse.js';


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


type WebMethodParam =
	| string
	| FormData
	| Record<string, unknown>
	| null
	| undefined;

interface JsonResult {
	key: string;
	message: string;
}

interface JsonResponseData {
	status?: unknown;
	result?: JsonResult;
	[key: string]: unknown;
}

interface WebMethodInit extends RequestInit {
	headers: Record<string, string>;
}

/**
 * @class WebMethod
 *
 * async, await対応のサーバメソッドクラス。
 */
export class WebMethod {
	private static get PARAM_TYPE_URLENCODED(): string {
		return 'application/x-www-form-urlencoded';
	}

	private static get PARAM_TYPE_JSON(): string {
		return 'application/json; charset=utf-8';
	}

	private static get PARAM_TYPE_FORM_DATA(): string {
		return 'formData';
	}

	private serverUrl: string;
	private method: string;
//	private errorMessagesArea: string;
	private contentType: string | null;
	private init: WebMethodInit;
	private headers?: Headers; // FIXME:Form.jsをForm.tsに変換したときエラーするはず。
	private paramType?: string;

	constructor(m: string) {
		this.serverUrl = location.pathname;
		this.method = m;
//		this.errorMessagesArea = 'errorMessages';
		this.contentType = null;
		this.init = {
			method: 'POST',
			mode: 'cors',
			cache: 'no-cache',
			credentials: 'same-origin',
			redirect: 'follow',
			headers: {}
		};
	}

	/**
	 * アプリケーション例外発生時の処理です。
	 */
	protected async onCatchApplicationException(data: JsonResponseData): Promise<never | void> {
		if (data.result?.key === 'error.auth') {
			window.location.href =
				currentPage.contextPath
				+ currentPage.errorPage
				+ '?msg='
				+ encodeURIComponent(data.result.message);
		} else if (data.result) {
			throw new Error(data.result.message);
		}
	}

	/**
	 * システム例外発生時の処理を行います。
	 */
	protected onCatchError(): void {
		alert(MessagesUtil.getMessage('error.ajax') ?? '');
	}

	/**
	 * 送信するパラメータを設定します。
	 */
	protected setInitBody(method: string, param: Exclude<WebMethodParam, null | undefined>): string {
		let ret = WebMethod.PARAM_TYPE_URLENCODED;

		if (param instanceof FormData) {
			logger.log('param is FormData');
			param.append('dfMethod', method);

			if (currentPage.csrfToken != null) {
				param.append('csrfToken', currentPage.csrfToken);
			}

			ret = WebMethod.PARAM_TYPE_FORM_DATA;
		} else if (param instanceof Object) {
			const jsonParam = param as Record<string, unknown>;
			jsonParam.dfMethod = method;

			if (currentPage.csrfToken != null) {
				jsonParam.csrfToken = currentPage.csrfToken;
			}

			const json = JSON.stringify(jsonParam);
			logger.log('param is Object json=' + json);
			param = jsonParam;
			this.init.body = json;
			ret = WebMethod.PARAM_TYPE_JSON;
			return ret;
		} else {
			logger.log('param is String param=[' + param + ']');
			let encodedParam = 'dfMethod=' + method + '&' + param;

			if (currentPage.csrfToken != null) {
				encodedParam = 'csrfToken=' + currentPage.csrfToken + '&' + encodedParam;
			}

			this.init.body = encodedParam;
			return ret;
		}

		this.init.body = param;
		return ret;
	}

	/**
	 * サーバー上のメソッドをfetch APIを使用して呼び出します。
	 */
	protected async callMethod(method: string, param: WebMethodParam): Promise<Response | undefined> {
		if (window.currentPage != null) {
			window.currentPage.lock();
		}

		if (param == null) {
			param = '';
		}

		const errorfunc = this.onCatchError;

		try {
			const ptype = this.setInitBody(method, param);

			if (ptype !== WebMethod.PARAM_TYPE_FORM_DATA) {
				this.init.headers['Content-Type'] = ptype;
			}

			if (window.location.search != null && window.location.search.length > 1) {
				this.init.headers.queryString = window.location.search.substring(1);
			}

			const response = await fetch(this.serverUrl, this.init);
			this.contentType = response.headers.get('Content-Type');
			this.headers = response.headers;

			if (window.currentPage != null) {
				window.currentPage.unlock();
		 }

			return response;
		} catch (err: unknown) {
			if (err instanceof Error) {
				if (err.stack) {
					logger.error(err.stack);
				}
				logger.error(err.message, err);
		 } else {
				logger.error(String(err), err);
			}

			if (window.currentPage != null) {
				window.currentPage.unlock();
			}

			errorfunc.call(this);
			return undefined;
		}
	}

	/**
	 * Responseに応じた適切なオブジェクトを取得します。
	 */
	protected async getResponseObject(
		r: Response
	): Promise<JsonResponseData | Record<string, unknown> | string | Blob | null> {
		if (r.redirected) {
			window.location.href = r.url;
			return null;
		}

		if (this.contentType?.includes('application/json')) {
			logger.log('received json');
			const data = await r.json() as JsonResponseData;

			if (data.status != null) {
				if (data.status === JsonResponse.SUCCESS || data.status === JsonResponse.INVALID) {
					return data;
			 }

				await this.onCatchApplicationException(data);
				return data;
			}

			return data;
		}

		if (this.contentType?.includes('text/')) {
			logger.log('received text');
			return await r.text();
		}

		logger.log('received blob');
		return await r.blob();
	}

	/**
	 * 非同期メソッドを呼び出します。
	 */
	public async execute(param: WebMethodParam): Promise<
		JsonResponseData | Record<string, unknown> | string | Blob | null
	> {
		const r = await this.callMethod(this.method, param);

		if (r == null) {
			throw new Error('No response received');
	 }

		if (!r.ok) {
			const msg = 'HTTP_' + r.status + ' ' + r.statusText;
			throw new Error(msg);
	 }

		return await this.getResponseObject(r);
	}

	/**
	 * UPLOADフォームかどうかを判定します。
	 */
/*	private isUploadForm(form: JQueryForm): boolean {
		const fileFields = form.find(':file');
		return fileFields.length > 0;
	}
*/
	/**
	 * 指定したformをメソッドに対してPostします。
	 */
	public async submit(form: JQueryForm): Promise<
		JsonResponseData | Record<string, unknown> | string | Blob | null 
		> {
		const ff = form.find("input[type='file']");

		if (ff.length > 0) {
			return await this.submitWithFile(form);
		}

		return await this.submitWithoutFile(form);
	}

	/**
	 * ファイルアップロードなしでPostします。
	 */
	public async submitWithoutFile(form: JQueryForm): Promise<
		JsonResponseData | Record<string, unknown> | string | Blob | null
	> {
		let data = form.serialize();

		form.find(':file').each((_, el: Element) => {
			if (data.length > 0) {
				data += '&';
		 }

			const name = $(el).attr('name') ?? '';
			const value = $(el).val();
			data += name + '=' + encodeURIComponent(String(value));
		});

		logger.log('data=' + data);
		return await this.execute(data);
	}

	/**
	 * FormData形式でPostします。
	 */
	public async submitWithFile(form: JQueryForm): Promise<
		JsonResponseData | Record<string, unknown> | string | Blob | null
	> {
		this.paramType = WebMethod.PARAM_TYPE_FORM_DATA;
		const formData = new FormData(form.get(0) as HTMLFormElement);
		return await this.execute(formData);
	}
}
