/**
 * @fileOverview {@link ZipCodeField}クラスを記述したファイルです。
 */

'use strict';

import { CharField } from '../sqltype/CharField.js';
import { StringUtil } from '../../util/StringUtil.js';


/**
 * @class ZipCodeField
 * 郵便番号フィールドクラス。
 * <pre>
 * zipcloud様のAPIを利用して住所検索をサポートしています。
 * 利用する際は以下のサイトのAPI利用規約にしたがってください。
 * https://zipcloud.ibsnet.co.jp/rule/api
 * </pre>
 * @extends CharField
 */
export class ZipCodeField extends CharField {
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
	 * zipcloud様のAPIを使用して住所を検索します。
	 * @param comp {jQuery} 郵便番号フィールドに対応したjQueryオブジェクト。
	 */
	async queryAddress(comp) {
		let address = this.addressFieldId;
		if (address != null) {
			let address2 = this.addressFieldId2;
			let address3 = this.addressFieldId3;
			let kanaAddress = this.kanaAddressFieldId;
			let kanaAddress2 = this.kanaAddressFieldId2;
			let kanaAddress3 = this.kanaAddressFieldId3;
			let sp = comp.attr(this.getIdAttribute()).split(".");
			if (sp.length == 2) {
				address = sp[0] + "." + this.addressFieldId;
				if (address2 != null) {
					address2 = sp[0] + "." + this.addressFieldId2;
				}
				if (address3 != null) {
					address3 = sp[0] + "." + this.addressFieldId3;
				}
				if (kanaAddress != null) {
					kanaAddress = sp[0] + "." + this.kanaAddressFieldId;
				}
				if (kanaAddress2 != null) {
					address2 = sp[0] + "." + this.kanaAddressFieldId2;
				}
				if (kanaAddress3 != null) {
					kanaAddress3 = sp[0] + "." + this.kanaAddressFieldId3;
				}
			}
			let r = await fetch("https://zipcloud.ibsnet.co.jp/api/search?zipcode=" + this.get().val());
			let adr = await r.json();
			logger.log("zipcloud=", adr);
			if (adr.status == 200) {
				if (address2 == null && address3 == null) {
					this.parent.get(address).val(adr.results[0].address1 + adr.results[0].address2 + adr.results[0].address3);
				} else if (address2 != null && address3 == null) {
					this.parent.get(address).val(adr.results[0].address1)
					this.parent.get(address2).val(adr.results[0].address2 + adr.results[0].address3);
				} else if (address2 != null && address3 != null) {
					this.parent.get(address).val(adr.results[0].address1)
					this.parent.get(address2).val(adr.results[0].address2);
					this.parent.get(address3).val(adr.results[0].address3);
				}
				if (kanaAddress != null) {
					adr.results[0].kana1 = StringUtil.halfToFull(adr.results[0].kana1);
					adr.results[0].kana2 = StringUtil.halfToFull(adr.results[0].kana2);
					adr.results[0].kana3 = StringUtil.halfToFull(adr.results[0].kana3);
					if (kanaAddress2 == null && kanaAddress3 == null) {
						this.parent.get(kanaAddress).val(adr.results[0].kana1 + adr.results[0].kana2 + adr.results[0].kana3);
					} else if (kanaAddress2 != null && kanaAddress3 == null) {
						this.parent.get(kanaAddress).val(adr.results[0].kana1)
						this.parent.get(kanaAddress2).val(adr.results[0].kana2 + adr.results[0].kana3);
					} else if (address2 != null && address3 != null) {
						this.parent.get(kanaAddress).val(adr.results[0].kana1)
						this.parent.get(kanaAddress2).val(adr.results[0].kana2);
						this.parent.get(kanaAddress3).val(adr.results[0].kana3);
					}
				}
			}
		}
	}

}





