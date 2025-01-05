/**
 * @fileOverview  {@link ContextMenu}クラスを記述したファイルです。
 */
'use strict';

import { LongTapHandler } from './LongTapHandler.js';

/**
 * ContextMenuイベント設定クラス。
 * <pre>
 * iOS,iPadosはcontextmenuイベントをサポートしていないので、
 * 長押しを無理やり検出して動作します。
 * </pre>
 */
export class ContextMenuEventListener {
	/**
	 * コンストラクタ。
	 * @param {jQuery} jqueryオブジェクト。
	 * @param {Function} contextmenu contextmenuイベント処理メソッド。
	 */
	constructor(jq, contextmenu) {
		let ph = currentPage.getPlatform();
		if ("ipados" == ph || "ios" == ph) {
			// iOSの場合は長押しを検出。
			new LongTapHandler(jq, contextmenu);	
		} else {
			// iOS以外はcontextmenuイベント処理を登録する。
			jq.on("contextmenu", contextmenu);
		}
		// モバイル端末での長押しコピーの禁止
		jq.attr("oncontextmenu", "return false;");
		jq.css("-webkit-touch-callout", "none");
		jq.css("-webkit-user-select", "none");
		jq.css("touch-action", "manipulation");
	}
	
	
}
