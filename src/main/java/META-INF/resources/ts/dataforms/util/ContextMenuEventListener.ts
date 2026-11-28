/**
 * @fileOverview {@link ContextMenuEventListener}クラスを記述したファイルです。
 */
'use strict';

import { LongTapHandler } from './LongTapHandler.js';

declare const currentPage: {
	getPlatform(): string;
};

type ContextMenuCallback = (ev: JQuery.Event) => void;

/**
 * ContextMenuイベント設定クラス。
 * iOS、iPadOSはcontextmenuイベントをサポートしていないため、
 * 長押しを検出して動作します。
 */
export class ContextMenuEventListener {
	/**
	 * コンストラクタ。
	 * FIXME: http://ipaddress/app/ でアクセスすると動作しない問題がある。原因不明。
	 * @param jq jqueryオブジェクト。
	 * @param contextmenu contextmenuイベント処理メソッド。
	 */
	public constructor(jq: JQuery, contextmenu: ContextMenuCallback) {
		const platform = currentPage.getPlatform();

		if (platform === 'ipados' || platform === 'ios') {
			// iOSの場合は長押しを検出。
			new LongTapHandler(jq, contextmenu);
		} else {
			// iOS以外はcontextmenuイベント処理を登録する。
			jq.on('contextmenu', contextmenu);
		}

		// モバイル端末での長押しコピーの禁止
		jq.attr('oncontextmenu', 'return false;');
		jq.css('-webkit-touch-callout', 'none');
		jq.css('-webkit-user-select', 'none');
		jq.css('touch-action', 'manipulation');
	}
}
