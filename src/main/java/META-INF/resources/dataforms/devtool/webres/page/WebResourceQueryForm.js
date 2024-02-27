/**
 * @fileOverview {@link WebResourceQueryForm}クラスを記述したファイルです。
 */

'use strict';

/**
 * @class WebResourceQueryForm
 * Webリソース検索フォームクラス。
 * <pre>
 * 指定されたページ中のWebコンポーネントを検索するためのフォームです。
 * </pre>
 * @extends QueryForm
 */
class WebResourceQueryForm extends QueryForm {
	/**
	 * HTMLエレメントフォームとの対応付けを行います。
	 *
	 */
	attach() {
		super.attach();
		this.get("checkAllTypeButton").click(() => {
			this.find("[name='webComponentTypeList']").each((_, el) => {
				$(el).prop("checked", true);
			});
		});
		this.get("uncheckAllTypeButton").click(() => {
			this.find("[name='webComponentTypeList']").each((_, el) => {
				$(el).prop("checked", false);
			});
		});
	}
}

