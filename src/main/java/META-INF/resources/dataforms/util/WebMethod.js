/**
 * @fileOverview  {@link WebMethod}クラスを記述したファイルです。
 */

'use strict';

/**
 * @class WebMethod
 *
 * asycn, await対応のサーバメソッドクラス。
 *
 * @prop {String} serverUrl location.pathnameの値。
 * @prop {String} method メソッド名。
 * @prop {String} errorMessagesArea エラーメッセージ領域のID。
 * @prop {Object} init fetchのinitパラメータ。
 * <pre>
 * 初期設定は以下の様になっています。
 * {
 * 	method: "POST"
 * 	, mode: "cors"
 * 	, cache: "no-cache"
 * 	, credentials: "same-origin"
 * 	, redirect: "follow"
 * 	, headers: {}
 * }
 * </pre>
 * @prop {String} contentType 応答のcontent-type。
 */
class WebMethod {

	/**
	 * パラメータタイプurlencoded。
	 */
	static get PARAM_TYPE_URLENCODED() {
		return "application/x-www-form-urlencoded";
	}

	/**
	 * パラメータタイプJSON。
	 */
	static get PARAM_TYPE_JSON() {
		return "application/json; charset=utf-8";
	}

	/**
	 * パラメータタイプFormDataオブジェクト。
	 */
	static get PARAM_TYPE_FORM_DATA() {
		return "formData"; // content-typeは指定しない。
	}

	/**
	 * コンストラクタ。
	 *
	 * @param {String} m メソッド名。
	 */
	constructor(m) {
		this.serverUrl = location.pathname;
		this.method = m;
		this.errorMessagesArea = "errorMessages";
		this.contentType = null;
		this.init = {
			method: "POST"
			, mode: "cors"
			, cache: "no-cache"
			, credentials: "same-origin"
			, redirect: "follow"
			, headers: {}
		};
	}

	/**
	 * アプリケーション例外発生時の処理です。
	 * <pre>
	 * dataは以下の形式のオブジェクトです。
	 * {
	 *   status:サーバの処理結果(JsonResponse.APPLICATION_EXCEPTION).
	 *   result:例外に対応したメッセージ。
	 * }
	 * </pre>
	 * @param {Object} data 返却されたオブジェクト.
	 */
	async onCatchApplicationException(data) {
		// logger.log("onCatchApplicationException data=" + JSON.stringify(data));
		if (data.result.key == "error.auth") {
			window.location.href = currentPage.contextPath + currentPage.errorPage + "?msg=" + data.result.message;
		} else {
			// await currentPage.alert(null, data.result.message);
			throw new Error(data.result.message);
		}
	}

	/**
	* システム例外発生時の処理を行います。
	* <pre>
	* jQuery.ajaxのerror オプションに指定するメソッドです。
	* </pre>
	*/
	onCatchError() {
		alert(MessagesUtil.getMessage("error.ajax"));
	}

	/**
	 * 送信するパラメータを設定します。
	 * @param {String} method メソッド。
	 * @param {Object} param パラメータ。
	 * @returns パラメータタイプ。
	 */
	setInitBody(method, param) {
		let ret = WebMethod.PARAM_TYPE_URLENCODED;
		if (param instanceof FormData) {
			logger.log("param is FormData");
			param.append("dfMethod", method);
			if (currentPage.csrfToken != null) {
				param.append("csrfToken", currentPage.csrfToken);
			}
			ret = WebMethod.PARAM_TYPE_FORM_DATA;
		} else if (param instanceof Object) {
			param.dfMethod = method;
			if (currentPage.csrfToken != null) {
				param.csrfToken = currentPage.csrfToken;
			}
			param = JSON.stringify(param);
			logger.log("param is Object json=" + param);
			ret = WebMethod.PARAM_TYPE_JSON;
		} else {
			logger.log("param is String param=[" + param + "]");
			param = "dfMethod=" + method + "&" + param;
			if (currentPage.csrfToken != null) {
				param = "csrfToken=" + currentPage.csrfToken + "&" + param;
			}
			ret = WebMethod.PARAM_TYPE_URLENCODED;
		}
		this.init.body = param;
		return ret;
	}

	/**
	* サーバー上のメソッドをfetch APIを使用して呼び出します。
	* @param {String} method メソッド名。
	* @param {Object} param パラメータ。
	* <pre>
	*  FormData型のオブジェクトはそのまま送信。
	*  Stringの場合はURLENCODED(p1=v1&p2=v2)形式。
	*  上記以外のObjectの場合はJSON形式に変換して送信。
	* </pre>
	* @returns {Promise} Promiseオブジェクト。
	*/
	async callMethod(method, param) {
		if (window.currentPage != null) {
			window.currentPage.lock();
		}
		if (param == null) {
			param = "";
		}
		let errorfunc = this.onCatchError;
		try {
			let ptype = this.setInitBody(method, param);
			if (ptype != WebMethod.PARAM_TYPE_FORM_DATA) {
				// FormData形式以外のパラメータの場合はConetnt-Typeを設定する。
				this.init.headers["Content-Type"] = ptype;
			}
			if (window.location.search != null && window.location.search.length > 1) {
				// QueryStringが指定された場合、それを送信する。
				this.init.headers["queryString"] = window.location.search.substring(1)
			}
			let r = await fetch(this.serverUrl, this.init);
			this.contentType = r.headers.get("Content-Type");
			this.headers = r.headers;
			if (window.currentPage != null) {
				window.currentPage.unlock();
			}
			return r;
		} catch(err) {
			if (err.stack) {
				logger.error(err.stack);
			}
			logger.error(err.message, err);
			if (window.currentPage != null) {
				window.currentPage.unlock();
			}
			errorfunc.call(this);
		}
	}

	/**
	 * Responseに応じた適切なオブジェクトを取得します。
	 * @param {Response} r レスポンス。
	 * @returns {Promise} 応答情報。
	 */
	async getResponseObject(r) {
		if (r.redirected) {
			window.location.href = r.url;
			return null;
		}
		if (this.contentType.indexOf("application/json") >= 0) {
			logger.log("received json");
			let data = await r.json();
			if (data.status != null) {
				// JSONが帰ってきた場合はstatusを判定する。
				if (data.status == JsonResponse.SUCCESS || data.status == JsonResponse.INVALID) {
					return data;
				} else {
					await this.onCatchApplicationException(data);
					return data;
				}
			} else {
				return data;
			}
		} else if (this.contentType.indexOf("text/") >= 0) {
			logger.log("received text");
			return await r.text();
		} else {
			logger.log("received blob");
			// 上記以外の場合はBlobを返す。
			let blob = await r.blob();
			return blob;
		}
	}

	/**
	 * 非同期メソッドを呼び出します。
	 * <pre>
	 * 指定されたメソッドを非同期で呼び出し、コールバックメソッドで結果を処理ます。
	 * </pre>
	 *
	 * @param {String} param パラメータ(QueryString形式)。
	 * <pre>
	 *  FormData型のオブジェクトはそのまま送信。
	 *  Stringの場合はURLENCODED(p1=v1&p2=v2)形式。
	 *  上記以外のObjectの場合はJSON形式に変換して送信。
	 * </pre>
	 * @returns {Promise} Promiseオブジェクト。
	 * <pre>
	 *  callMethodで取得したリスポンスのContent-Typeを判定し、JSONの場合はそれを変換したオブジェクト、
	 *  テキストの場合文字列、それ以外はBlobを保持したPromiseを返します。
	 * </pre>
	 *
	 */
	async execute(param) {
		let r = await this.callMethod(this.method, param);
		if (!r.ok) {
			let msg = "HTTP_" + r.status + " " + r.statusText;
			throw new Error(msg);
		}
		return await this.getResponseObject(r);
	}

	/**
	 * UPLOADフォームかどうかを判定します。
	 * @param {jQuery} form フォーム。
	 * @returns ファイルアップロードフィールドが存在するフォームの場合trueを返します。
	 *
	 */
	isUploadForm(form) {
		var fileFields = form.find(':file');
		return fileFields.length > 0;
	}


	/**
	 * 指定したformをメソッドに対してPostします。
	 * <pre>
	 * 指定されたformの内容をサーバーメソッドに対してpostします。
	 * </pre>
	 *
	 * @param {jQuery} form FROM。
	 * @returns {Promise} Promiseオブジェクト。
	 */
	async submit(form) {
		return await this.submitWithFile(form);
	}

	/**
	 * 指定したformを指定したメソッドに対してPostします、ファイルは送信されません。
	 * <pre>
	 * 指定されたformの内容をサーバーメソッドに対してx-www-form-urlencoded形式でpostします。
	 * </pre>
	 * @param {jQuery} form FROM。
	 * @returns {Promise} Promiseオブジェクト。
	 *
	 */
	async submitWithoutFile(form) {
		let data = form.serialize();
		// ファイルは名前だけ送信する。
		form.find(':file').each((_, el) => {
			if (data.length > 0) {
				data += "&";
			}
			data += $(el).attr("name") + "=" + encodeURIComponent($(el).val());
		});
		logger.log("data=" + data);
		return await this.execute(data);
	}

	/**
	 * 指定したformを指定したメソッドに対してPostします、ファイルは送信されません。
	 * <pre>
	 * 指定されたformの内容をサーバーメソッドに対してFormData形式でpostします。
	 * </pre>
	 * @param {jQuery} form FROM。
	 * @returns {Promise} Promiseオブジェクト。
	 */
	async submitWithFile(form) {
		this.paramType = WebMethod.PARAM_TYPE_FORM_DATA;
		let formData = new FormData(form.get(0));
		return await this.execute(formData);
	}
}
