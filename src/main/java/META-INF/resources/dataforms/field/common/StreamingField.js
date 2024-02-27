/**
 * @fileOverview {@link StreamingField}クラスを記述したファイルです。
 */

'use strict';

/**
 * @class StreamingField
 * ストリーミングファイルアップロードフィールドクラス。
 * @extends FileField
 */
class StreamingField extends FileField {
	/**
	 * HTMLエレメントとの対応付けを行います。
	 * <pre>
	 * 削除チェックボックス、ダウンロードリンクなどの設定を行います。
	 * </pre>
	 */
	attach() {
		super.attach();
		let player = this.getPlayer();
		player.on("abort", () => {
			logger.log("abort");
			setTimeout(() => {
				this.deleteTempFile();
			}, 3000);
		});
		player.on("ended", () => {
			logger.log("ended");
			setTimeout(() => {
				this.deleteTempFile();
			}, 3000);
		});
	}

	/**
	 * ファイルの選択処理。
	 * @param {jQuery} fld ファイルフィールド。
	 */
	selectFile(fld) {
		super.selectFile(fld);
		let player = this.getPlayer();
		let f = fld.get()[0];
		for(let j=0; j < f.files.length; j++){
			let url = URL.createObjectURL(f.files[j])
			logger.log("url=" + url);
			player.attr("src", url);
		}
	}


	/**
	 * 削除チェックボックスの処理を行います。
	 */
	delFile() {
		super.delFile();
		let player = this.getPlayer();
		player.attr("src", null);
	}
	/**
	 * ファイルフィールドに付随する各種コンポーネントを配置します。
	 * @param comp ファイルフィールド。
	 */
	addElements(comp) {
		let htmlstr = this.additionalHtmlText;
		let html = htmlstr.replace(/\$\{fieldId\}/g, this.id);
		logger.log("htmlstr=" + html);
		let tag = comp.prop("tagName");
		let type = comp.prop("type");
		if ("INPUT" == tag && type == "file") {
			comp.after(html);
		} else if (tag == "DIV") {
			comp.html(html);
			this.hideDelCheckbox();
		}
	}

	/**
	 * ストリーミングデータのプレーヤーを取得します。
	 * @returns {jQuery} ストリーミングデータのプレーヤー。
	 */
	getPlayer() {
		let playerid = this.id + "_player"; // プレーヤー.
		let player = this.parent.get(playerid);
		return player;
	}

	/**
	 * サーバ中のストリーミングデータの一時ファイルを削除します。
	 */
	async deleteTempFile() {
		try {
			let playerid = this.id + "_player"; // プレーヤーID.
			let player = this.parent.get(playerid);
			let key = player.attr("data-key");
			logger.log("key=" + key);
			let m = this.getWebMethod("deleteTempFile");
			await m.execute(key);
		} catch (e) {
			currentPage.reportError(e);
		}
	}

	/**
	 * 値を設定します。
	 *
	 * @param {Object} value 値。
	 */
	setValue(value) {
		super.setValue(value);
		let videoid = this.id + "_player"; // プレーヤーID.
		let video = this.parent.get(videoid);
		this.downloadUrl = null;
		if (value != null) {
			this.downloadParameter = value.downloadParameter;
			let url = location.pathname + "?dfMethod=" + encodeURIComponent(this.getUniqId()) + ".download"  + "&" + value.downloadParameter;
			if (currentPage.csrfToken != null) {
				url += "&csrfToken=" + currentPage.csrfToken;
			}
			video.attr("src", url);
			video.attr("data-key", value.downloadParameter);
			this.downloadUrl = url;
		} else {
			video.attr("src", "");
			video.removeAttr("data-key");
			this.downloadUrl = null;
		}
	}

}






