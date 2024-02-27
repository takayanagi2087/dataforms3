/**
 * @fileOverview  {@link ServerMethod}クラスを記述したファイルです。
 */

'use strict';

/**
 * @class ServerMethod
 *
 * サーバメソッドクラス。
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
 * @deprecated async/awaitに対応したWebMethodを使用してください。
 */
class ServerMethod {

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
	 * Ajax呼び出しの成功を示します。
	 * @constant ServerMethod.SUCCESS
	 */
	static get SUCCESS() {
		return 0;
	}


	/**
	 * Ajax呼び出しのバリデーションエラーを示します。
	 * @constant ServerMethod.INVALID
	 */
	static get INVALID() {
		return 1;
	}


	/**
	 * アプリケーション例外を示します。
	 * @constant ServerMethod.APPLICATION_EXCEPTION
	 */
	static get APPLICATION_EXCEPTION() {
		return 2;
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
	 *   status:サーバの処理結果(ServerMethod.APPLICATION_EXCEPTION).
	 *   result:例外に対応したメッセージ。
	 * }
	 * </pre>
	 * @param {Object} data 返却されたオブジェクト.
	 * @param {Object} type 返却されたオブジェクトタイプ.
	 */
	onCatchApplicationException(data) {
		logger.log("onCatchApplicationException data=" + JSON.stringify(data));
		if (data.result.key == "error.auth") {
			window.location.href = currentPage.contextPath + currentPage.errorPage + "?msg=" + data.result.message;
		} else {
			alert(data.result.message);
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
		let ret = ServerMethod.PARAM_TYPE_URLENCODED;
		if (param instanceof FormData) {
			logger.log("param is FormData");
			param.append("dfMethod", method);
			if (currentPage.csrfToken != null) {
				param.append("csrfToken", currentPage.csrfToken);
			}
			ret = ServerMethod.PARAM_TYPE_FORM_DATA;
		} else if (param instanceof Object) {
			param.dfMethod = method;
			if (currentPage.csrfToken != null) {
				param.csrfToken = currentPage.csrfToken;
			}
			param = JSON.stringify(param);
			logger.log("param is Object json=" + param);
			ret = ServerMethod.PARAM_TYPE_JSON;
		} else {
			logger.log("param is String param=[" + param + "]");
			param = "dfMethod=" + method + "&" + param;
			if (currentPage.csrfToken != null) {
				param = "csrfToken=" + currentPage.csrfToken + "&" + param;
			}
			ret = ServerMethod.PARAM_TYPE_URLENCODED;
		}
		this.init.body = param;
		return ret;
	}


	/**
	* サーバー上のメソッドを呼び出します。
	* @param {String} method メソッド名。
	* @param {Object} param パラメータ。
	* <pre>
	*  FormData型のオブジェクトはそのまま送信。
	*  Stringの場合はURLENCODED(p1=v1&p2=v2)形式。
	*  上記以外のObjectの場合はJSON形式に変換して送信。
	* </pre>
	* @param {Function} success 成功時の応答処理。
	*/
	callMethod(method, param, success) {
		let as = true;
		if (window.currentPage != null) {
			window.currentPage.lock();
		}
		if (param == null) {
			param = "";
		}
		let ptype = this.setInitBody(method, param);
		var errorfunc = this.onCatchError;
		if (ptype != ServerMethod.PARAM_TYPE_FORM_DATA) {
			// FormData形式以外のパラメータの場合はConetnt-Typeを設定する。
			this.init.headers["Content-Type"] = ptype;
		}
		if (window.location.search != null && window.location.search.length > 1) {
			// QueryStringが指定された場合、それを送信する。
			this.init.headers["queryString"] = window.location.search.substring(1)
		}
		fetch(this.serverUrl, this.init).then((r) => {
			logger.log("r.contentType=" + r.headers.get("Content-Type"));
			this.contentType = r.headers.get("Content-Type");
			this.headers = r.headers;
			if (r.redirected) {
				window.location.href = r.url;
				return null;
			}
			if (r.ok) {
				if (this.contentType != null) {
					if (this.contentType.indexOf("application/json") >= 0) {
						logger.log("received json");
						return r.json();
					} else if (this.contentType.indexOf("text") >= 0) {
						logger.log("received text");
						return r.text();
					} else {
						logger.log("received blob");
						return r.blob();
					}
				}
			} else {
				let msg = "HTTP_" + r.status + " " + r.statusText;
				return Promise.reject(new Error(msg));
			}
		//}).then((text) => {
		//	return eval("(" + text + ")");
		}).then((data) => {
			if (window.currentPage != null) {
				window.currentPage.unlock();
			}
			if (this.contentType.indexOf("application/json") >= 0) {
				// JSONが帰ってきた場合はstatusを判定する。
				if (data.status == ServerMethod.SUCCESS || data.status == ServerMethod.INVALID) {
					success.call(this, data);
				} else {
					this.onCatchApplicationException(data);
				}
			} else {
				// json以外の場合はそのままメソットに渡す。
				success.call(this, data);
			}
		}).catch((err) => {
			if (err.stack) {
				logger.error(err.stack);
			}
			logger.error(err.message, err);
			if (window.currentPage != null) {
				window.currentPage.unlock();
			}
			errorfunc.call(this);
		});
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
	 * @param {Function} success 成功時の応答処理 function(data)。
	 * 応答処理メソッドには以下の形式のObjectを渡します。
	 * <pre>
	 * {
	 *   status:サーバの処理結果(ServerMethod.SUCCESS or ServerMethod.INVALID).
	 *   result:サーバが応答したオブジェクト。
	 * }
	 * </pre>
	 *
	 */
	execute(param, success) {
	    this.callMethod(this.method, param, success);
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
	 * サーバーメソッドの応答内容は、通常json形式です。
	 * 通常はフォームの内容をform.serialize()メソッドで$.ajax()メソッドに渡します。
	 * フォーム中に&lt;input type=&quot;file&quot; ...&gt;が存在する場合、
	 * formのenctype,action,methodを適切に設定し、targetを非表示の&lt;iframe&gt;にして
	 * submitを行うことにより、ファイル送信を実現しています。
	 * </pre>
	 *
	 * @param {jQuery} form FROM。
	 * @param {Function} func 応答処理 function(data)。
	 * 応答処理メソッドには以下の形式のObjectを渡します。
	 * <pre>
	 * {
	 *   status:サーバの処理結果(ServerMethod.SUCCESS or ServerMethod.INVALID).
	 *   result:サーバが応答したオブジェクト。
	 * }
	 * </pre>
	 */
	submit(form, func) {
		if (!this.isUploadForm(form)) {
			this.submitWithoutFile(form, func);
		} else {
			this.submitWithFile(form, func);
		}
	}

	/**
	 * 指定したformを指定したメソッドに対してPostします、ファイルは送信されません。
	 * <pre>
	 * 指定されたformの内容をサーバーメソッドに対してpostします。
	 * サーバーメソッドの応答内容は、通常json形式です。
	 * フォームの内容をform.serialize()メソッドで取得しfetch APIに渡します。
	 * </pre>
	 * @param {jQuery} form FROM。
	 * @param {Function} func 応答処理 function(data)。
	 * 応答処理メソッドには以下の形式のObjectを渡します。
	 * <pre>
	 * {
	 *   status:サーバの処理結果(ServerMethod.SUCCESS or ServerMethod.INVALID).
	 *   result:サーバが応答したオブジェクト。
	 * }
	 * </pre>
	 *
	 */
	submitWithoutFile(form, func) {
		var data = form.serialize();
		form.find(':file').each(function() {
			if (data.length > 0) {
				data += "&";
			}
			data += $(this).attr("name") + "=" + encodeURIComponent($(this).val());
		});
		logger.log("data=" + data);
		this.callMethod(this.method, data, func);
	}

	/**
	 * 指定したformを指定したメソッドに対してPostします、ファイルは送信されません。
	 * <pre>
	 * 指定されたformの内容をサーバーメソッドに対してpostします。
	 * サーバーメソッドの応答内容は、通常json形式です。
	 * formのenctype,action,methodを適切に設定し、targetを非表示の&lt;iframe&gt;にして
	 * submitを行うことにより、ファイル送信を実現しています。
	 * </pre>
	 * @param {jQuery} form FROM。
	 * @param {Function} func 応答処理 function(data)。
	 * 応答処理メソッドには以下の形式のObjectを渡します。
	 * <pre>
	 * {
	 *   status:サーバの処理結果(ServerMethod.SUCCESS or ServerMethod.INVALID).
	 *   result:サーバが応答したオブジェクト。
	 * }
	 * </pre>
	 */
	submitWithFile(form, func) {
		this.paramType = ServerMethod.PARAM_TYPE_FORM_DATA;
		let formData = new FormData(form.get(0));
		this.callMethod(this.method, formData, func);
	}
}





