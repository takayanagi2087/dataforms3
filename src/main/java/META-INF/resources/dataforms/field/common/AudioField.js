/**
 * @fileOverview {@link AudioField}クラスを記述したファイルです。
 */

'use strict';

/**
 * @class AudioField
 * 音声ファイルアップロードフィールドクラス。
 * @extends StreamingField
 */
class AudioField extends StreamingField {
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

