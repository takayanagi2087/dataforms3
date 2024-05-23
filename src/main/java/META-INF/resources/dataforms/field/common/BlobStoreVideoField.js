/**
 * @fileOverview {@link BlobStoreVideoField}クラスを記述したファイルです。
 */

'use strict';

import { VideoField } from './VideoField.js';

/**
 * @class BlobStoreVideoField
 *
 * @extends VideoField
 */
export class BlobStoreVideoField extends VideoField {
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

