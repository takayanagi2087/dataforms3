/**
 * @fileOverview {@link DocFramePage}クラスを記述したファイルです。
 */

'use strict';

/**
 * @class DocFramePage
 *
 * @extends BasePage
 */
class DocFramePage extends BasePage {
	/**
	 * HTMLエレメントとの対応付けを行います。
	 */
	attach() {
//		let thisPage = this;
		super.attach();
		let qs = this.getQueryString();
		logger.log("doc=" + qs["doc"])
		if (qs["doc"] != null) {
			let src = "../../../../doc/" + qs["doc"];
			this.get("docFrame").attr("src", src);
		} else {
			let path = this.get("introduction").data("path");
			let src = "../../../../doc/" + path;
			logger.log("src=" + src);
			this.get("docFrame").attr("src", src);
		}
		this.find("a").click((ev) => {
			let path = $(ev.currentTarget).data("path");
			let src = "../../../../doc/" + path;
			this.get("docFrame").attr("src", src);
			$("ul.dropdwn_menu").slideUp();
		});
		this.get("docFrame").on('load', (ev) => {
			this.onLoadDocFrame($(ev.currentTarget));
		});

		$(window).resize(() => {
			this.onResize();
		});
		$(".sideMenuGroup").click(() => {
			setTimeout(() => {
				this.onResize();
			}, 500);
		});
		this.onResize();
		$('.dropdwn li').hover((ev) =>{
			$("ul:not(:animated)", ev.currentTarget).slideDown();
		}, (ev) => {
			$("ul.dropdwn_menu", ev.currentTarget).slideUp();
		});
	}

	/**
	 * Windowサイズ変更の際のiframeのサイズを調整します。
	 */
	onResize() {
		this.adjustDocFrameHeight();
	}

	/**
	 * ドキュメント表示用のiframeの高さを調整します。
	 */
	adjustDocFrameHeight() {
		let docFrame = this.get("docFrame");
		let footer = $("div.footerDiv");
		let top = docFrame.position().top;
		let bodyHeight = $("body").height();
		let clientHeight = $(window).height();
		logger.log("top=" + top + ",bodyHeight=" + bodyHeight + ",clientHeight=" + clientHeight);
		let menuHeight = $("div.menuDiv").height();
		let menuTop = $("div.menuDiv").position().top;
		let mainHeight = $("div.mainDiv").height();
		logger.log("main, menu, client = " + mainHeight + "," + menuHeight + "," + clientHeight);
//		if (bodyHeight > clientHeight) {
		if ((menuTop + menuHeight) > clientHeight) {
			let h = bodyHeight - top - 20;
			logger.log("b h=" + h);
			docFrame.height(h);
		} else {
			let h = clientHeight - top - footer.outerHeight() - 20;
			logger.log("a h=" + h);
			docFrame.height(h);
		}
	}

	/**
	 * docFrame内に各機ドキュメントが読み込まれた際の処理を行います。
	 * @param {jQuery} docFrame ドキュメントが読み込まれたiframe。
	 */
	onLoadDocFrame(docFrame) {
		let src = docFrame.attr("src");
		if (src.indexOf("javadoc") < 0 && src.indexOf("jsdoc") < 0) {
			this.setH2No(docFrame);
			this.setH3No(docFrame);
			this.setFigNo(docFrame);
			this.setFigEvent(docFrame);
			this.setTableNo(docFrame);
			this.setFileNo(docFrame);
		}
		this.adjustDocFrameHeight();
	}

	/**
	 * 各H2タグに番号を設定します。
	 * @param {jQuery} docFrame ドキュメントが読み込まれたiframe。
	 */
	setH2No(docFrame) {
		let n = docFrame.contents().find("h1 > span").text();
		let no = 1;
		docFrame.contents().find("h2").each((_, el) => {
			let text = $(el).html();
			let caption = "<span>" + n + (no++) + ".</span>" + text;
			$(el).html(caption);
		});
	}

	/**
	 * 各H3タグに番号を設定します。
	 * @param {jQuery} docFrame ドキュメントが読み込まれたiframe。
	 */
	setH3No(docFrame) {
		let no = 1;
		let baseno = "";
		docFrame.contents().find("h3").each((_, el) => {
			let n = $(el).prevAll("h2:first").find("span").text();
			if (baseno != n) {
				no = 1;
				baseno = n;
			}
			let text = $(el).html();
			let caption = "<span>" + n + (no++) + ".</span>" + text;
			$(el).html(caption);
		});
	}

	/**
	 * 図番を設定します。
	 * @param {jQuery} docFrame ドキュメントが読み込まれたiframe。
	 */
	setFigNo(docFrame) {
		let no = 1;
		docFrame.contents().find("figure > figcaption").each((_, el) => {
			let text = $(el).html();
			let caption = "図 " + (no++) + "." + text;
			$(el).html(caption);
		});
	}

	/**
	 * 表番を設定します。
	 * @param {jQuery} docFrame ドキュメントが読み込まれたiframe。
	 */
	setTableNo(docFrame) {
		let no = 1;
		docFrame.contents().find("table caption").each((_, el) => {
			let text = $(el).html();
			let caption = "表 " + (no++) + "." + text;
			$(el).html(caption);
		});
	}

	/**
	 * ファイル番号を設定します。
	 * @param {jQuery} docFrame ドキュメントが読み込まれたiframe。
	 */
	setFileNo(docFrame) {
		let no = 1;
		docFrame.contents().find("div.filecaption").each((_, el) => {
			let text = $(el).html();
			let caption = "ファイル " + (no++) + "." + text;
			$(el).html(caption);
		});
	}

	/**
	 * 図に対するイベント処理を設定します。
	 * @param {jQuery} docFrame ドキュメントが読み込まれたiframe。
	 */
	setFigEvent(docFrame) {
		docFrame.contents().find("img").click((ev) => {
			let url = docFrame.attr("src");
			let idx = url.lastIndexOf('/');
			if (idx > 0) {
				url = url.substring(0, idx);
			}
			let img = $(ev.currentTarget).attr("src");
			let title = $(ev.currentTarget).prev("figcaption").text();
			$("[" + this.getIdAttribute() + "='imageViewer']").attr("src", url + "/" + img);
			let image = new Image();
			image.onload = () => {
				$("[" + this.getIdAttribute() + "='imageDialog']").dialog({
					modal: true
					,width: image.width + 34
					,height: image.height + 64
					,title: title
					,resizable: true
				});
			};
			image.src = url + "/" + img;
			logger.log("image.src=" + image.src);
		});
	}
}

