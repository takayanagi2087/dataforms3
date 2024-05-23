/**
 * @fileOverview {@link WebResourceImageField}クラスを記述したファイルです。
 */

'use strict';

import { ImageField } from './ImageField.js';

/**
 * @class WebResourceImageField
 *
 * @extends ImageField
 */
export class WebResourceImageField extends ImageField {
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

