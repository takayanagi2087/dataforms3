/**
 * @fileOverview {@link AudioField}クラスを記述したファイルです。
 */

'use strict';

import { StreamingField } from './StreamingField.js';


/**
 * @class AudioField
 * 音声ファイルアップロードフィールドクラス。
 * @extends StreamingField
 */
export class AudioField extends StreamingField {
	/**
	 * HTMLエレメントとの対応付けを行います。
	 * <pre>
	 * 削除チェックボックス、ダウンロードリンクなどの設定を行います。
	 * </pre>
	 */
	attach() {
		super.attach();
	}
}

