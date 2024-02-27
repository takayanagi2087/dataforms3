/**
 * @fileOverview {@link FlagField}クラスを記述したファイルです。
 */

'use strict';

/**
 * @class FlagField
 * フラグフィールドクラス。
 * <pre>
 * 通常checkboxに割り当て、'0' or '1'を指定します。
 * </pre>
 * @extends CharField
 */
class FlagField extends CharField {
	/**
	 * HTMLの要素との対応付けを行います。
	 */
	attach() {
		super.attach();
		if (currentPage.useUniqueId) {
			let lbl = this.getParentForm().find("label[for='" + this.id + "']");
			if (lbl.length > 0) {
				lbl.attr("for", this.realId);
			}
		}
	}

	/**
	 * checkboxへの値設定を行います。
	 * @param {jQuery} comp 値を設定するコンポーネント。
	 * @param {String} value 値。
	 */
	setInputValue(comp, value) {
		let tag = comp.prop("tagName");
		let type = comp.prop("type");
		if (tag == "INPUT" && type.toLowerCase() == "checkbox") {
			if (value == "1") {
				comp.prop("checked", true);
			} else {
				comp.prop("checked", false);
			}
		} else {
			comp.val(value);
		}
	}

	/**
	 * フィールドの値を取得します。
	 * @return {String} 0:チェックされていない場合、1:チェックされた場合。
	 */
	getValue() {
		if (this.get().prop("checked")) {
			return "1";
		} else {
			return "0";
		}
	}

	/**
	 * フィールドのロック/ロック解除を行ないます。
	 * @param {Boolean} lk ロックする場合true。
	 */
	lock(lk) {
		let comp = this.get();
		let tag = comp.prop("tagName");
		let type = comp.prop("type");
		if ("INPUT" == tag && type.toLowerCase() == "checkbox") {
			let span = this.addSpan(comp);
			span.html("<input type='checkbox' " + this.getIdAttribute() + "='" + this.id + "_ck' onclick='return false;'>");
			this.parent.get(this.id + "_ck").prop("checked", comp.prop("checked"));
			if (lk) {
				span.show()
				comp.hide();
			} else {
				span.hide();
				comp.show();
			}
		}
	}

	/**
	 * 値を設定します。
	 *
	 * @param {String} flg フラグ。
	 */
	setValue(flg) {
		super.setValue(flg);
		this.parent.get(this.id + "_ck").prop("checked", flg == "1" ? true :false);
	}
}

