/**
 * @fileOverview {@link FileField}クラスを記述したファイルです。
 */

'use strict';

 /**
 * @class FileReceiver
 * ファイルDrag&Drop受付領域。
 * @extends Field
 */
 class FileReceiver {

	/**
	 * コンストラクタ。
	 * @param {FileField} fileField ファイルフィールド。
	 */
	constructor(fileField) {
		this.fileField = fileField;
	}

	/**
	 * HTMLエレメントとの対応付けを行います。
	 * <pre>
	 * ファイルのドロップイベントの設定を行います。
	 * </pre>
	 */
	attach() {
		let msg = MessagesUtil.getMessage("message.filereceiver")
		logger.log("realId=" + this.fileField.realId);
		let rdiv = this.fileField.parent.find("[data-id='" + this.fileField.id + "_rcv']");
		rdiv.text(msg);
		rdiv.show();

		rdiv.on("dragenter", (ev) => {
			ev.stopPropagation();
			ev.preventDefault();
			$(ev.target).addClass("fileReceiverActive");
		});

		rdiv.on("dragover", (ev) => {
			ev.stopPropagation();
			ev.preventDefault();
		});

		rdiv.on("drop", (ev) => {
			ev.stopPropagation();
			ev.preventDefault();
			$(ev.target).removeClass("fileReceiverActive");
			this.setFile(ev.originalEvent.dataTransfer.files);
		});

		rdiv.on("dragleave", (ev) => {
			ev.stopPropagation();
			ev.preventDefault();
			$(ev.target).removeClass("fileReceiverActive");
		});

	}

	/**
	 * ファイルを設定します。
	 * @param {Array} ファイルリスト。
	 */
	setFile(files) {
		let el = this.fileField.get().get()[0];
		el.files = files;
		this.fileField.selectFile(this.fileField.get());
	}
}

/**
 * @class FileField
 * ファイルフィールドクラス。
 * <pre>
 * 各種ファイルフィールドの基底クラスです。
 * </pre>
 * @extends Field
 */
class FileField extends Field {
	/**
	 * HTMLエレメントとの対応付けを行います。
	 * <pre>
	 * 削除チェックボックス、ダウンロードリンクなどの設定を行います。
	 * </pre>
	 */
	attach() {
		let comp = this.get();
		this.addElements(comp);
		super.attach();
		let selid = this.id + "_sel"; // 選択ボタンID.
		let delid = this.id + "_del"; // ファイル削除のチェックボックス.
		this.parent.get(selid).click((ev) => {
			let inpid = $(ev.currentTarget).attr(this.getIdAttribute()).replace("_sel", "");
			this.id = inpid;
			this.parent.get(inpid).click();
		});
		this.parent.get(delid).click((ev) => {
			this.id = $(ev.currentTarget).attr(this.getIdAttribute()).replace("_del", "");
			$(ev.currentTarget).hide();
			this.delFile();
		});
		comp.change((ev) => {
			this.adjustIdIndex($(ev.currentTarget));
			this.selectFile($(ev.currentTarget));
		});
		if (this.readonly) {
			this.lock(true);
		} else {
			this.lock(false);
		}
		let tag = comp.prop("tagName");
		let type = comp.prop("type");
		if (tag == "INPUT" && type.toLowerCase() == "file") {
			comp.hide();
		} else {
			this.parent.get(selid).hide();
		}
		if (this.enableFileReceiver) {
			let r = new FileReceiver(this);
			r.attach();
		}
	}

	/**
	 * ファイルの選択処理。
	 * @param {jQuery} fld ファイルフィールド。
	 */
	selectFile(fld) {
		let selfileid = this.id + "_selfile"; // 選択ボタンID.
		let selfile = this.parent.get(selfileid);
		selfile.text(fld.val());

		let linkid = this.id + "_link"; // ダウンロードリンク.
		let fnlink = this.parent.get(linkid);

		fnlink.attr("data-value", "");
		fnlink.attr("data-size", "");
		fnlink.attr("data-dlparam", "");

		fnlink.html(fnlink.attr("data-value"));

		let fnid = this.id + "_fn"; // ファイル名のリンク.
		let fnhidden = this.parent.find("[name='" + this.selectorEscape(fnid) + "']");
		fnhidden.val(fnlink.attr("data-value"));
		this.id = fld.attr(this.getIdAttribute());
		this.showDelCheckbox();
	}

	/**
	 * 削除チェックボックスの処理を行います。
	 */
	delFile() {
		let comp = this.parent.get(this.id);
		let linkid = this.id + "_link"; // ダウンロードリンク.
		let selfileid = this.id + "_selfile"; // 選択ボタンID.
		let fnid = this.id + "_fn"; // ファイル名のリンク.
		let selfile = this.parent.get(selfileid);
		let fnlink = this.parent.get(linkid);
		let fnhidden = this.parent.find("[name='" + this.selectorEscape(fnid) + "']");
		selfile.html("");
		fnhidden.val("");
		fnlink.html("");
		fnlink.attr("data-value", "");
		fnlink.attr("data-size", "");
		fnlink.attr("data-dlparam", "");
		comp.val("");
	}


	/**
	 * 削除チェックボックスを表示します。
	 */
	showDelCheckbox() {
		let delid = this.id + "_del";
		this.parent.get(delid).show();
	}

	/**
	 * 削除チェックボックスを隠します。
	 */
	hideDelCheckbox() {
		let delid = this.id + "_del";
		this.parent.get(delid).hide();
	}

	/**
	 * ファイルフィールドに付随する各種コンポーネントを配置します。
	 * @param comp ファイルフィールド。
	 */
	addElements(comp) {
		let htmlstr = this.additionalHtmlText;
		let html = htmlstr.replace(/\$\{fieldId\}/g, this.id);
		let tag = comp.prop("tagName");
		let type = comp.prop("type");
		if ("INPUT" == tag && type == "file") {
			comp.after(html);
		} else if (tag == "DIV" || tag == "SPAN") {
			comp.html(html);
		}
	}

	/**
	 * 値を設定します。
	 *
	 * @param {Object} value 値。
	 */
	setValue(value) {
		let comp = this.get();
		let tag = comp.prop("tagName");
		let linkid = this.id + "_link";
		let selfileid = this.id + "_selfile";
		let fnid = this.id + "_fn";

		// 選択ファイル名をリセット
		let selfile = this.parent.get(selfileid);
		selfile.html("");
		// 削除フラグのリセット
		if (value != null) {
			let url = location.pathname + "?dfMethod=" + encodeURIComponent(this.getUniqId()) + ".download"  + "&" + value.downloadParameter;
			if (currentPage.csrfToken != null) {
				url += "&csrfToken=" + currentPage.csrfToken;
			}
			let fnlink = this.parent.get(linkid);
			fnlink.attr("href", url);
			let fnhidden = this.parent.find("[name='" + this.selectorEscape(fnid) + "']");
			fnlink.html(value.fileName);
			fnlink.attr("data-value", value.fileName);
			fnlink.attr("data-size", value.size);
			fnlink.attr("data-dlparam", value.downloadParameter);
			fnhidden.val(value.fileName);
			if (this.readonly) {
				this.hideDelCheckbox();
			} else {
				let tag = comp.prop("tagName");
				let type = comp.prop("type");
				if ("INPUT" == tag && type == "file") {
					this.showDelCheckbox();
				}
			}
			this.hideDelCheckbox();
		} else {
			let fnlink = this.parent.get(linkid);
			fnlink.attr("href", "");
			let fnhidden = this.parent.find("[name='" + this.selectorEscape(fnid) + "']");
			fnlink.html("");
			fnlink.attr("data-value", "");
			fnlink.attr("data-size", "");
			fnlink.attr("data-dlparam", "");
			fnhidden.val("");
			this.hideDelCheckbox();
		}
		if ("INPUT" == tag) {
			comp.val("");
		}
	}

	/**
	 * 値を取得します。
	 * @return {String} 値。
	 */
	getValue() {
		let ret = this.get().val();
		if (ret.length == 0) {
			let fnid = this.id + "_link";
			ret = this.parent.get(fnid).text();
		}
		return ret;
	}

	/**
	 * フィールドの検証を行ないます。
	 * <pre>
	 * 各フィールドのバリデータを呼び出します。
	 * 追加のチェックが必要な場合、このメソッドをオーバーライドします。
	 * </pre>
	 * @returns {ValidationError} 検証結果。問題が発生しなければnullを返します。
	 */
	validate() {
		let val = this.getValue();
		this.value = val;
		if (this.validators != null) {
			for (let i = 0; i < this.validators.length; i++) {
				let v = this.validators[i];
				if (v.validate(val) == false) {
					let msg = v.getMessage(this.label);
					return new ValidationError(this.id, msg);
				}
			}
		}
		return null;
	}
}

