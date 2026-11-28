/**
 * @fileoverview {@link DisplayedRequiredValidator}クラスを記述したファイルです。
 */

import { WebComponent } from "../controller/WebComponent.js";
import { RequiredValidator } from "./RequiredValidator.js";

/**
 * 表示中の項目だけを必須とするバリデータクラス。
 *
 * @extends RequiredValidator
 */
export class DisplayedRequiredValidator extends RequiredValidator {
	/**
	 * 親コンポーネント。
	 */
	declare parent: WebComponent; // フレームワークが実行時に設定

	/**
	 * バリデーションを行ないます。
	 * @param v 値。
	 * @returns バリデーション結果。
	 */
	validate(v: string): boolean {
		const form = this.getParentForm();
		if (form != null) {
			const parent = form.get(this.parent.id);
			if (parent != null) {
				const visible = parent.is(":visible");
				if (visible) {
					return !this.isBlank(v);
				}
			}
		}
		return true;
	}
}