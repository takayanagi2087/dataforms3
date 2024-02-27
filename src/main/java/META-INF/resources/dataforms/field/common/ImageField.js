/**
 * @fileOverview {@link ImageField}クラスを記述したファイルです。
 */

'use strict';

/**
 * @class ImageField
 * 画像ファイルアップロードフィールドクラス。
 * @extends FileField
 */
class ImageField extends FileField {
	/**
	 * HTMLエレメントとの対応付けを行います。
	 * <pre>
	 * 削除チェックボックス、ダウンロードリンクなどの設定を行います。
	 * </pre>
	 */
	attach() {
		super.attach();
		let thisField = this;
		let linkid = this.id + "_link";
		let link = this.parent.get(linkid);
		let thumbid = this.id + "_thm"; // サムネイルID.
		let thumb = this.parent.get(thumbid);
		thumb.click(() => {
			let fval = thisField.get().val();
			let val = {};
			if (fval.length == 0) {
				val.fileName = link.attr("data-value");
				val.size = link.attr("data-size");
				val.downloadParameter = link.attr("data-dlparam");
			} else {
				let fl = thisField.get().get()[0].files[0];
				let url = URL.createObjectURL(fl);
				val.fileName = fl.name;
				val.size = fl.size;
				val.url = url;
				logger.log("url=" + url);
			}
			if (val.fileName.length > 0) {
				this.showImage(val);
			}
		});
	}

	/**
	 * ファイルの選択処理。
	 * @param {jQuery} fld ファイルフィールド。
	 */
	selectFile(fld) {
		super.selectFile(fld);
		let thumbid = this.id + "_thm"; // サムネイルID.
		let thumb = this.parent.get(thumbid);
		this.previewImage(fld, thumb);
	}

	/**
	 * 削除チェックボックスの処理を行います。
	 */
	delFile() {
		super.delFile();
		// サムネイルの表示制御を追加。
		let thumbid = this.id + "_thm"; // サムネイルID.
		let thumb = this.parent.get(thumbid);
		thumb.attr("src", null);
	}

	/**
	 * 画像ファイル指定時のprevie表示。
	 * @param {jQuery} inputFile ファイルフィールド。
	 * @param {jQuery} thumb サムネイル。
	 */
	previewImage(inputFile, thumb) {
		let fileList = inputFile.get()[0].files;
		if (fileList.length > 0) {
			let fileReader = new FileReader() ;
			// 読み込み後の処理を決めておく
			fileReader.onload = function() {
				// データURIを取得
				let dataUri = this.result ;
				// HTMLに書き出し (src属性にデータURIを指定)
				//document.getElementById( "output" ).innerHTML += '<img src="' + dataUri + '">' ;
				thumb.attr("src", dataUri);
			}
			fileReader.readAsDataURL(fileList[0]);
		}
	}

	/**
	 * 指定されたURLの画像を表示します。
	 * @param img イメージ。
	 */
	showImage(img) {
		logger.log("img=" + JSON.stringify(img));
		if (this.parent.id == "imageForm") {
			return;
		}
		let dlg = currentPage.getComponent("imageDialog");
		if (dlg == null) {
			if (img != null) {
				if (img.url != null) {
					window.open(img.url, "_image");
				} else {
					let url = location.pathname + "?dfMethod=" + encodeURIComponent(this.getUniqId()) + ".download"  + "&" + img.downloadParameter;
					if (currentPage.csrfToken != null) {
						url += "&csrfToken=" + currentPage.csrfToken;
					}
					window.open(url, "_image");
				}
			}
		} else {
			let imgfrm = dlg.getComponent("imageForm");
			let imgfld = imgfrm.getComponent("image");
			logger.dir(imgfld);
			imgfld.setValue(img);
			dlg.showModal();
		}
	}
	/**
	 * ファイルフィールドに付随する各種コンポーネントを配置します。
	 * @param comp ファイルフィールド。
	 */
	addElements(comp) {
		let htmlstr = this.additionalHtmlText;
		let html = htmlstr.replace(/\$\{fieldId\}/g, this.id);
		html = html.replace(/\$\{width\}/g, this.thumbnailWidth);
		html = html.replace(/\$\{height\}/g, this.thumbnailHeight);
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
	 * 値を設定します。
	 *
	 * @param {String} value 値。
	 */
	setValue(value) {
		super.setValue(value);
		let thumbid = this.id + "_thm"; // サムネイルID.
		let thumb = this.parent.get(thumbid);
		this.downloadUrl = null;
		if (value != null) {
			let linkid = this.id + "_link";
			let fnlink = this.parent.get(linkid);
			if (value.url == null) {
				let func = ".downloadThumbnail";
				if (!this.reducedThumbnail) {
					func = ".download";
				}
				let url = location.pathname + "?dfMethod=" + encodeURIComponent(this.getUniqId()) + func  + "&" + value.downloadParameter;
				if (currentPage.csrfToken != null) {
					url += "&csrfToken=" + currentPage.csrfToken;
				}
				thumb.attr("src", url);
				this.downloadUrl = url;
			} else {
				thumb.attr("src", value.url);
				fnlink.attr("href", "javascript:void(0);");
				this.downloadUrl = value.url;
			}
		} else {
			thumb.removeAttr("src");
			thumb.attr("alt", "");
			this.downloadUrl = null;
		}
	}
}

