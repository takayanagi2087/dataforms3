/**
 * @fileOverview {@link JsonResponse}クラスを記述したファイルです。
 */

'use strict';

/**
 * @class JsonResponse
 * JsonResponseの定数を定義するクラスです。
 */
export class JsonResponse {
	/**
	 * 成功を示します。
	 * @constant JsonResponse.SUCCESS
	 */
	public static get SUCCESS(): number {
		return 0;
	}

	/**
	 * バリデーションエラーを示します。
	 * @constant JsonResponse.INVALID
	 */
	public static get INVALID(): number {
		return 1;
	}

	/**
	 * アプリケーション例外を示します。
	 * @constant JsonResponse.APPLICATION_EXCEPTION
	 */
	public static get APPLICATION_EXCEPTION(): number {
		return 2;
	}
}
