/**
 * @fileOverview {@link FunctionSelectField}クラスを記述したファイルです。
 */

'use strict';

import { SingleSelectField } from '../../field/common/SingleSelectField.js';

/**
 * @class FunctionSelectField
 * 機能フィールドクラス。
 * <pre>
 * </pre>
 * @extends SingleSelectField
 */
export class FunctionSelectField extends SingleSelectField {
	
	/**
	 * コンストラクタ。
	 */
	constructor() {
		super();
	}

	
	/**
	 * HTMLエレメントとの対応付けを行います。
	 * <pre>
	 * 機能を変更したタイミングで、パッケージ名を設定します。
	 * </pre>
	 */
	attach() {
		super.attach();
		this.get().change((ev) => {
			this.setPackageName($(ev.currentTarget))
		});
	}

	/**
	 * パッケージ名称を設定します。
	 * @param {String} jq パッケージ名を設定するフィールド。
	 */
	setPackageName(jq) {
		let form = this.getParentForm();
		let funcname = jq.val();
		if (funcname != null && funcname.length > 0) {
			for (let i = 0; i < this.packageOption.length; i++) {
//				let packageName = funcname.replace(/\//g, ".").substr(1);
				let packageName = this.pathPackageMap[funcname];
				if (this.packageOption[i].length > 0) {
					packageName +=  "." + this.packageOption[i];
				}
				let id = jq.attr(this.getIdAttribute());
				logger.log("functionSelectField id=" + id)
				if (this.isHtmlTableElementId(id)) {
					let a = id.split(".");
					form.find("#" + this.selectorEscape(a[0] + "." + this.packageFieldId[i])).val(packageName);
				} else {
					form.find("#" + this.selectorEscape(this.packageFieldId[i])).val(packageName);
				}
			}
			if (typeof this.parent.setPackageName == "function") {
				this.parent.setPackageName(jq);
			}
		}
	}

	/**
	 * パッケージ名から機能を設定します。
	 * @param {String} packageName パッケージ名。
	 */
	selectPackage(packageName) {
		for (let i = 0; i < this.optionList.length; i++) {
			let path = this.optionList[i].value;
			let pkg = path.substring(1).replace("/", ".");
			if (packageName != null && packageName.length > 0) {
				if (packageName.indexOf(pkg) == 0) {
					this.get().val(path);
				}
			}
		}
	}
}


