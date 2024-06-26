/**
 * @fileOverview {@link VideoField}クラスを記述したファイルです。
 */

'use strict';


import { StreamingField } from './StreamingField.js';

/**
 * @class VideoField
 * 動画ファイルアップロードフィールドクラス。
 * @extends FileField
 */
export class VideoField extends StreamingField {
	/**
	 * HTMLエレメントとの対応付けを行います。
	 * <pre>
	 * 削除チェックボックス、ダウンロードリンクなどの設定を行います。
	 * </pre>
	 */
	attach() {
		super.attach();
		let player = this.getPlayer();
		player.attr("width", this.playerWidth);
		player.attr("height", this.playerlHeight);
	};
}

