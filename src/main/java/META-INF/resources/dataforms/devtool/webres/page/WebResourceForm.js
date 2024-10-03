/**
 * @fileOverview {@link WebResourceForm}クラスを記述したファイルです。
 */

'use strict';

import { MessagesUtil } from '../../../util/MessagesUtil.js';
import { JsonResponse } from '../../../response/JsonResponse.js';
import { Form } from '../../../controller/Form.js';

/**
 * @class WebResourceForm
 * Webリソース作成フォームクラス。
 * @extends QueryForm
 */
export class WebResourceForm extends Form {
	/**
	 * HTMLエレメントフォームとの対応付けを行います。
	 *
	 */
	attach() {
		super.attach();
		this.get("closeButton").click(() => {
			this.parent.close();
		});
		this.get("generateHtml").click(() => {
			this.generateHtml();
		});
		this.get("generateJavascript").click(() => {
			this.generateJavascript();
		});
	}

	/**
	 * フォームに対してデータを設定します。
	 * @param {Object} data 表示データ。
	 */
	setFormData(data) {
		this.get("generateHtml").prop("checked", false);
		this.get("generateJavascript").prop("checked", false);
		super.setFormData(data);
		if (data.htmlStatus == "0" || data.htmlStatus == "1") {
			this.find(".htmlTr").show();
			this.get("generateHtmlButton").show();
			this.get("noFormContent").prop("checked", false);
			if (data.className.match(/Form$/) == null) {
				this.get("outputFormHtml").show();
				this.find("label.outputFormHtml").show();
			} else {
				this.get("outputFormHtml").hide();
				this.find("label.outputFormHtml").hide();
			}
		} else {
			this.find(".htmlTr").hide();
		}
		if (data.javascriptStatus == "0" || data.javascriptStatus == "1") {
			this.get("generateJavascriptButton").show();
		} else {
			this.get("generateJavascriptButton").hide();
		}
	}

	/**
	 * HTML作成を行います。
	 */
	async generateHtml() {
		try {
			let ret = await this.submit("generateHtml");
			this.parent.resetErrorStatus();
			if (ret.status == JsonResponse.SUCCESS) {
				let systemName = MessagesUtil.getMessage("message.systemname");
				currentPage.alert(systemName, ret.result);
			} else if (ret.status == JsonResponse.INVALID) {
				this.parent.setErrorInfo(this.getValidationResult(ret), this);
			}
		} catch (e) {
			currentPage.reportError(e);
		}
	}

	/**
	 * javascriptの作成を行います。
	 */
	async generateJavascript() {
		try {
			let ret = await this.submit("generateJavascript");
			this.parent.resetErrorStatus();
			if (ret.status == JsonResponse.SUCCESS) {
				let systemName = MessagesUtil.getMessage("message.systemname");
				await currentPage.alert(systemName, ret.result);
			} else if (ret.status == JsonResponse.INVALID) {
				this.parent.setErrorInfo(this.getValidationResult(ret), this);
			}
		} catch (e) {
			currentPage.reportError(e);
		}
	}
}


