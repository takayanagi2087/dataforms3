/**
 * @fileOverview  {@link DataForms}クラスを記述したファイルです。
 */

'use strict';

/**
 * @class DataForms
 * フォームコンテナクラス。
 * <pre>
 * 複数のフォームをまとめ、制御するクラスです。
 * DataFormsのサブクラスはPageとDialogです。
 * Pageは複数のFormと複数のDialogを持つことができます。
 * Dialogはjquery-uiのdialogで実装し、複数のFormオブジェクトを持つことができます。
 * QueryForm,QueryResultForm,EditFormを部品として持つ場合、
 * それぞれのFormを適切に制御します。
 * </pre>
 * @extends WebComponent
 */
class DataForms extends WebComponent {
	/**
	 * コンストラクタ。
	 */
	constructor() {
		super();
	}

	/**
	 * フォーム情報の初期化を行います。
	 * @param {Object} formMap フォームマップ.
	 */
	initForm(formMap) {
		for (let key in formMap) {
			let f = formMap[key];
			let form = this.newInstance(f);
			form.init();
		}
	}

	/**
	 * 初期化処理を行います。
	 *
	 */
	init() {
		super.init();
	}

	/**
	 * HTMLエレメントとの対応付けを行います。
	 * <pre>
	 * QueryForm,QueryResultForm,EditFormの存在をチェックし、適切適切な状態を設定します。
	 * </pre>
	 */
	attach() {
		let editMode = true;
		let qf = this.get("queryForm");
		if (qf.length > 0) {
			qf.show();
			editMode = false;
		}
		let rf = this.get("queryResultForm");
		if (rf.length > 0) {
			rf.hide();
			editMode = false;
		}
		let ef = this.get("editForm");
		if (editMode) {
			this.replaceState("editMode", "editMode", location.href);
			ef.show();
		} else {
			this.replaceState("queryMode", "queryMode", location.href);
			ef.hide();
		}
		super.attach();
		if (qf.length == 0 && rf.length > 0) {
			// QueryFormが無くQueryResultFormが存在する場合、先頭ページを表示する。
			let f = this.getComponent("queryResultForm");
			if (f != null) {
				f.topPage();
				rf.show();
			}
		}
		if (editMode) {
			// EditFormしか存在しない場合、更新対象データを読み込む。
			let f = this.getComponent("editForm");
			if (f != null) {
				f.initWithoutQuery();
			}
		}
		// エラーメッセージ領域が無い場合自動的に追加する.
		if (this.get("errorMessages").length == 0) {
			let f = this.find("form:first");
			f.before('<div class="errorMessages" ' + this.getIdAttribute() + '="errorMessages"><!--エラーメッセージ領域--></div>');
		}
		if (ef.length == 0) {
			// 編集フォームがない場合
			this.get("newButton").hide();
		}
	}

	/**
	 * エラー状態をリセットする.
	 */
	resetErrorStatus() {
		let area = this.get("errorMessages");
		area.html("");
		let ef = this.find('.errorField');
		ef.each((_, el) => {
			$(el).removeClass('errorField');
		});
	}

	/**
	 * エラー情報を表示します。
	 * @param {Array} errors エラー情報の配列。
	 * <pre>
	 * 配列の各要素は以下のような形式のオブジェクトです。
	 * {
	 * 		message: エラーメッセージ.
	 * 		fieldId: エラーの発生したフィールドID.
	 * }
	 * </pre>
	 * @param {Form} form エラーの発生したフォーム。
	 */
	setErrorInfo(errors, form) {
		let area = this.get("errorMessages");
		this.resetErrorStatus();
		for (let i = 0; i < errors.length; i++) {
			area.append(errors[i].message + "<br/>");
			let comp = form.get(this.selectorEscape(errors[i].fieldId));
			comp.addClass("errorField");
			let tag = comp.prop("tagName");
			let type = comp.prop("type");
			if ("INPUT" == tag && type == "file") {
				form.get(this.selectorEscape(errors[i].fieldId + "_sel")).addClass("errorField");
			}
		}
	}


	/**
	 * ページのモードをクエリモードにします。
	 * <pre>
	 * EditFormを隠し、QueryForm,QueryResultFormを表示します。
	 * </pre>
	 * @reutrns {Boolean} QueryFormが存在しない場合falseを返します。
	 */
	toQueryMode() {
		let qf = this.get("queryForm");
		let qrf = this.get("queryResultForm");
		if (qf.length > 0 || qrf.length > 0) {
			let queryForm = this.getComponent("queryForm");
			if (queryForm != null) {
				qf.show();
			}
			let queryResultForm = this.getComponent("queryResultForm");
			if (queryResultForm != null) {
				let rf = this.get("queryResultForm");
				if (queryResultForm.queryResult != null) {
					rf.show();
				}
			}
			let editForm = this.getComponent("editForm");
			if (editForm != null) {
				let ef = this.get("editForm");
				ef.hide();
			}
			return true;
		} else {
			return false;
		}
	}

	/**
	 * ページのモードを編集モードにします。
	 * <pre>
	 * EditFormを表示し、QueryForm,QueryResultFormを隠します。
	 * </pre>
	 * @reutrns {Boolean} EditFormが存在しない場合falseを返します。
	 */
	toEditMode() {
		let queryForm = this.getComponent("queryForm");
		if (queryForm != null) {
			let qf = this.get("queryForm");
			qf.hide();
		}
		let rf = this.get("queryResultForm");
		rf.hide();
		let ef = this.get("editForm");
		ef.show();
		return (ef.length > 0);
	}



	/**
	 * history.pushStateを呼び出すためのメソッドです。
	 * <pre>
	 * このメソッドはDataFormsクラスでは何もしませんが、Pageクラスではhistory.pushStateを呼び出します。
	 * </pre>
	 * @param {Object} state 状態。
	 * @param {String} title タイトル。
	 * @param {String} url タイトル。
	 *
	 */
	pushState(state, title, url) {
	}


	/**
	 * history.replaceStateを呼び出すためのメソッドです。
	 * <pre>
	 * このメソッドはDataFormsクラスでは何もしませんが、Pageクラスではhistory.replaceStateを呼び出します。
	 * </pre>
	 * @param {Object} state 状態。
	 * @param {String} title タイトル。
	 * @param {String} url タイトル。
	 *
	 */
	replaceState(state, title, url) {
	}

	/**
	 * 編集モードへの画面遷移履歴を登録します。
	 */
	pushEditModeStatus() {
		this.pushState("editMode", "editMode", location.href);
	}

	/**
	 * 確認モードへの画面遷移履歴を登録します。
	 */
	pushConfirmModeStatus() {
		this.pushState("confirmMode", "confirmMode", location.href);
	}

	/**
	 * ブラウザの戻るボタンでフォームの制御を行うかどうかを返します。
	 * @returns {Boolean} DataFormsクラスでは常にfalseを返します。
	 */
	isBrowserBackEnabled() {
		return false;
	}

}
