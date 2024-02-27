/**
 * @fileOverview {@link ZipCodeField}クラスを記述したファイルです。
 */

'use strict';

/**
 * @class ZipCodeField
 * フラグフィールドクラス。
 * @extends CharField
 */
class ZipCodeField extends CharField {
	/**
	 * HTMLの要素との対応付けを行います。
	 */
	attach() {
		super.attach();
		this.get().change((ev) => {
			let comp = $(ev.currentTarget);
			this.addHyphen(comp);
			this.queryAddress(comp);
		});
	}

	/**
	 * 7桁の数字が入力されている場合"-"を付加します。
	 * @param {jQuery} comp 郵便番号入力フィールド。
	 */
	addHyphen(comp) {
		let val = comp.val();
		if (val.match(/[0-9]{7}/)) {
			let v = val.substr(0, 3) + "-" + val.substr(3);
			comp.val(v);
		}
	}

	/**
	 * 住所を検索する。
	 * @param comp {jQuery} 郵便番号フィールドに対応したjQueryオブジェクト。
	 */
	queryAddress(comp) {
		let thisField = this;
		let address = thisField.addressFieldId;
		if (address != null) {
			let address2 = thisField.addressFieldId2;
			let address3 = thisField.addressFieldId3;
			let sp = comp.attr(this.getIdAttribute()).split(".");
			if (sp.length == 2) {
				address = sp[0] + "." + thisField.addressFieldId;
				if (address2 != null) {
					address2 = sp[0] + "." + thisField.addressFieldId2;
				}
				if (address3 != null) {
					address3 = sp[0] + "." + thisField.addressFieldId3;
				}
			}
			if ("AjaxZip3" in window) {
				if (address2 == null && address3 == null) {
					AjaxZip3.zip2addr(comp.get()[0], "", address, address);
				} else if (address2 != null && address3 == null) {
					AjaxZip3.zip2addr(comp.get()[0], "", address, address2);
				} else if (address2 != null && address3 != null) {
					AjaxZip3.zip2addr(comp.get()[0], "", address, address2, address3);
				}
			}
		}
	}

}





