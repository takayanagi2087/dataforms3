/**
 * @fileOverview {@link FolderStoreVideoField}クラスを記述したファイルです。
 */

'use strict';

import { VideoField } from './VideoField.js';

/**
 * @class FolderStoreVideoField
 *
 * @extends VideoField
 */
export class FolderStoreVideoField extends VideoField {
	/**
	 * コンストラクタ。
	 */
	constructor() {
		super();
	}

	/**
	 * HTMLエレメントとの対応付けを行います。
	 */
	attach() {
		super.attach();
	}
}

