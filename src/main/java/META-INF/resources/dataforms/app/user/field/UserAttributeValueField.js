/**
 * @fileOverview {@link UserAttributeValueField}クラスを記述したファイルです。
 */

'use strict';

/**
 * @class UserAttributeValueField
 * ユーザ属性値フィールドクラス。
 * <pre>
 * ユーザ属性を指定すると、その選択肢を取得します。
 * </pre>
 * @extends EnumOptionSingleSelectField
 */
class UserAttributeValueField extends EnumOptionSingleSelectField {
	/**
	 * 値の設定を行ないます。
	 * <pre>
	 * 同時にユーザ属性値の選択肢も更新します。
	 * </pre>
	 * @param {String} v 値。
	 *
	 */
	setValue(v) {
		// この設定処理は選択肢がそろっていないので空振りします。
		super.setValue(v);
		// 一旦値を保持し、選択肢を取得してから設定します。
		let tid = this.id.replace("userAttributeValue", "userAttributeType");
		let type = this.getParentForm().getFieldValue(tid);
		this.setUserAttributeType(type, v);
	}

	/**
	 * ユーザ属性を設定します。
	 * <pre>
	 * 指定されたユーザ属性に対応した選択肢を取得し設定します。
	 * </pre>
	 * @param {String} type ユーザ属性。
	 */
	async setUserAttributeType(type, v) {
		try {
			let m = this.getWebMethod("getTypeOption");
			let opt = await m.execute("type=" + type);
			if (opt.status == JsonResponse.SUCCESS) {
				this.setOptionList(opt.result);
				if (v != null) {
					super.setValue(v);
				}
			}
		} catch (e) {
			currentPage.reportError(e);
		}
	}
}

