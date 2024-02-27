/**
 * @fileOverview {@link JsonResponse}クラスを記述したファイルです。
 *
 */

'use strict';

/**
 * @class JsonResponse
 * JsonResponseの定数を定義するクラスです。
 */
class JsonResponse {
	/**
	 * 成功を示します。
	 * @constant JsonResponse.SUCCESS
	 */
	static get SUCCESS() {
		return 0;
	}


	/**
	 * バリデーションエラーを示します。
	 * @constant JsonResponse.INVALID
	 */
	static get INVALID() {
		return 1;
	}


	/**
	 * アプリケーション例外を示します。
	 * @constant JsonResponse.APPLICATION_EXCEPTION
	 */
	static get APPLICATION_EXCEPTION() {
		return 2;
	}
}
