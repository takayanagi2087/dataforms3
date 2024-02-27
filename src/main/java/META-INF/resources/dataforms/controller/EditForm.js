/**
 * @fileOverview  {@link EditForm}クラスを記述したファイルです。
 */

'use strict';

/**
 * @class EditForm
 *
 * データ編集フォーム。
 * <pre>
 * データ編集を行うフォームです。
 * </pre>
 * @extends TableUpdateForm
 *
 * @prop {String} mode "edit"(フォームが編集可能な状態)または"confirm"(フォーム全体が編集不可の状態)の値を取ります。
 * @prop {String} saveMode "new"(新規データの入力中)または"update"(既存データの編集中)の値を取ります。
 * @prop {Boolean} multiRecord 複数レコード編集モードの場合はtrue。このクラスではfalseに設定。
 *
 *
 */
class EditForm extends Form {
	/**
	 * コンストラクタ。
	 */
	constructor() {
		super();
		this.mode = "edit";
		this.saveMode =  "new";
		this.multiRecord = false;
	}

	/**
	 * HTMLエレメントとの対応付けを行います。
	 * <pre>
	 * 以下のボタンが存在した場合、イベント処理を登録します。
	 * #confirmButton ... 「確認」ボタンの処理.
	 * #saveButton ... 「保存」ボタンの処理.
	 * #resetButton ... 「リセット」ボタンの処理.
	 * #deleteButton ... 「削除」ボタンの処理.
	 * #backButton ... 「戻る」ボタンの処理.
	 * </pre>
	 */
	attach() {
		super.attach();
		this.get("confirmButton").click(() => {
			this.confirm();
			return false;
		});
		this.get("saveButton").click(() => {
			this.save();
			return false;
		});
		this.get("resetButton").click(() => {
			this.reset();
			return false;
		});
		this.get("backButton").click(() => {
			if (this.parent.isBrowserBackEnabled()) {
				history.back();
			} else {
				this.back();
			}
			return false;
		});
		this.get('deleteButton').click(() => {
			this.del();
			return false;
		});
		this.toEditMode();
	}

	/**
	 * queryForm, queryResultFormが存在しない(EditFormのみ)ページ場合の初期化処理。
	 * <pre>
	 * 1レコード編集モードの場合
	 * 		デフォルトは新規登録
	 * 		QueryStringでPKを指定した場合は対象データを編集
	 * 複数レコードの場合は全レコードの編集
	 * </pre>
	 */
	initWithoutQuery() {
		logger.log("initWithoutQuery");
		if (this.singleRecord) {
			let id = this.getQueryString()[this.pkFieldId];
			if (id != null) {
				this.get(this.pkFieldId).val(id);
				this.updateData();
			} else {
				this.newData();
			}
		} else {
			this.updateData();
		}
	}

	/**
	 * 戻るボタンのイベント処理を行います。
	 */
	back() {
		if (this.mode == "edit") {
			this.clearData();
			if (!this.parent.toQueryMode()) {
				currentPage.toTopPage();
			}
		} else if (this.mode == "confirm") {
			this.toEditMode();
		}
	}

	/**
	 * 更新モードの時にPKをロックします。
	 *
	 */
	lockPkFields() {
		let lk = false;
		if (this.saveMode == "new") {
			lk = false;
		} else {
			lk = true;
		}
		if (this.pkFieldIdList != null) {
			for (let i = 0; i < this.pkFieldIdList.length; i++) {
				let f = this.getComponent(this.pkFieldIdList[i]);
				if (f != null) {
					f.lock(lk);
				}
			}
		}
	}

	/**
	 * 編集モードにします。
	 * <pre>
	 * 各フィールドを編集可能状態にします。
	 * </pre>
	 */
	toEditMode() {
		this.mode = "edit";
		this.lockFields(false);
		let cb = this.get("confirmButton");
		if (cb.length > 0) {
			// 確認画面があるパターン.
			cb.show();
			this.get("resetButton").show();
			this.get("saveButton").hide();
		} else {
			// いきなり保存するパターン.
			this.get("saveButton").show();
		}
		this.lockPkFields();
	}

	/**
	 * 確認モードにします。
	 * <pre>
	 * 各フィールドを編集不可状態にします。
	 * </pre>
	 */
	toConfirmMode() {
		this.mode = "confirm";
		this.lockFields(true);
		let cb = this.get("confirmButton");
		if (cb.length > 0) {
			// 確認画面があるパターン.
			cb.hide();
			this.get("resetButton").hide();
			this.get("saveButton").show();
		} else {
			// いきなり保存するパターン.
			this.get("saveButton").show();
		}
	}

	/**
	 * 確認ボタンのイベント処理を行います。
	 * <pre>
	 * 対応するFormのconfirmメソッドを呼び出し、問題なければ確認モードに遷移します。
	 * ファイルアップロードフィールドはサーバーに送信されません。
	 * </pre>
	 */
	async confirm() {
		try {
			if (this.validate()) {
				this.get("saveMode").val(this.saveMode);
				let result = await this.submitWithoutFile("confirm");
				this.parent.resetErrorStatus();
				if (result.status == JsonResponse.SUCCESS) {
					this.toConfirmMode();
					this.parent.pushConfirmModeStatus();
				} else {
					this.parent.setErrorInfo(this.getValidationResult(result), this);
				}
			}
		} catch (e) {
			currentPage.reportError(e);
		}
	}

	/**
	 * 新規登録モードにします。
	 * <pre>
	 * 対応するEditFormのgetNewDataを呼び出し、初期データを取得します。
	 * 各フィールドに取得データを設定し、編集モードにします。
	 * </pre>
	 */
	async newData() {
		try {
			let title = MessagesUtil.getMessage("message.editformtitle.new");
			this.get("editFormTitle").text(title);
			let result = await this.submitWithoutFile("getNewData");
			this.parent.resetErrorStatus();
			if (result.status == JsonResponse.SUCCESS) {
				this.saveMode = "new";
				this.setFormData(result.result);
				this.toEditMode();
				this.parent.pushEditModeStatus();
			} else {
				this.parent.setErrorInfo(this.getValidationResult(result), this);
			}
		} catch (e) {
			currentPage.reportError(e);
		}
	}

	/**
	 * 更新登録モードにします。
	 * <pre>
	 * 対応するEditFormのgetDataを呼び出し、編集対象データを取得します。
	 * 各フィールドに取得データを設定し、編集モードにします。
	 * </pre>
	 */
	async updateData() {
		try {
			let title = MessagesUtil.getMessage("message.editformtitle.update");
			this.get("editFormTitle").text(title);
			let result = await this.submitWithoutFile("getData");
			this.parent.resetErrorStatus();
			if (result.status == JsonResponse.SUCCESS) {
				this.parent.toEditMode();
				this.saveMode = "update";
				this.setFormData(result.result);
				this.toEditMode();
				this.parent.pushEditModeStatus();
			} else {
				this.parent.setErrorInfo(this.getValidationResult(result), this);
			}
		} catch (e) {
			currentPage.reportError(e);
		}
	}

	/**
	 * データを参照登録します。
	 * <pre>
	 * 対応するEditFormのgetReferDataを呼び出し、参照対象データを取得します。
	 * 各フィールドに取得データを設定し、編集モードにします。
	 * </pre>
	 *
	 */
	async referData() {
		try {
			let title = MessagesUtil.getMessage("message.editformtitle.refer");
			this.get("editFormTitle").text(title);
			let result = await this.submitWithoutFile("getReferData");
			this.parent.resetErrorStatus();
			if (result.status == JsonResponse.SUCCESS) {
				this.parent.toEditMode();
				this.saveMode = "new";
				this.setFormData(result.result);
				this.toEditMode();
				this.parent.pushEditModeStatus();
			} else {
				this.parent.setErrorInfo(this.getValidationResult(result), this);
			}
		} catch (e) {
			currentPage.reportError(e);
		}
	}

	/**
	 * データを参照します。
	 * <pre>
	 * 対応するEditFormのgetDataを呼び出し、参照対象データを取得します。
	 * 各フィールドに取得データを設定し、参照モードにします。
	 * </pre>
	 */
	async viewData() {
		try {
			let title = MessagesUtil.getMessage("message.editformtitle.view");
			this.get("editFormTitle").text(title);
			let result = await this.submitWithoutFile("getData");
			this.parent.resetErrorStatus();
			if (result.status == JsonResponse.SUCCESS) {
				this.parent.toEditMode();
				this.saveMode = "update";
				this.setFormData(result.result);
				this.lockFields(true);
				this.get("confirmButton").hide();
				this.get("saveButton").hide();
				this.get("resetButton").hide();
				this.parent.pushConfirmModeStatus();
			} else {
				this.parent.setErrorInfo(this.getValidationResult(result), this);
			}
		} catch (e) {
			currentPage.reportError(e);
		}
	}

	/**
	 * 各フィールドにデータを設定します。
	 * <pre>
	 * 新規モードの場合、削除ボタンを隠します。
	 * </pre>
	 * @param {Object} data フォームデータ.
	 *
	 */
	setFormData(data) {
		super.setFormData(data);
		if (this.saveMode == "new") {
			this.get('deleteButton').hide();
		} else {
			this.get('deleteButton').show();
		}
	}

	/**
	 * 保存や削除後の画面状態遷移を行います。
	 */
	changeStateForAfterUpdate() {
		var form = this;
		var queryForm = form.parent.getComponent("queryForm");
		var resultForm = form.parent.getComponent("queryResultForm");
		if (queryForm == null && resultForm == null) {
			form.clearData();
			currentPage.toTopPage();
		} else {
			form.clearData();
			form.toEditMode();
			form.parent.toQueryMode();
			var queryResultForm = form.parent.getComponent("queryResultForm");
			if (queryResultForm != null) {
				queryResultForm.changePage();
			}
		}
	}

	/**
	 * 保存ボタンのイベント処理を行います。
	 * <pre>
	 * 対応するFormのsaveメソッドを呼び出し、保存処理を行います。
	 * ファイルアップロードフィールドもサーバーに送信されます。
	 * </pre>
	 */
	async save() {
		try {
			if (this.validate()) {
				this.get("saveMode").val(this.saveMode);
				let result = await this.submit("save");
				this.parent.resetErrorStatus();
				if (result.status == JsonResponse.SUCCESS) {
					if (result.result != null && result.result.length > 0) {
						await currentPage.alert(null, result.result);
						this.changeStateForAfterUpdate();
					} else {
						this.changeStateForAfterUpdate();
					}
				} else {
					this.parent.setErrorInfo(this.getValidationResult(result), this);
				}
			}
		} catch (e) {
			currentPage.reportError(e);
		}
	}


	/**
	 * 保存ボタンのイベント処理を行います。
	 * <pre>
	 * 対応するEditFormのdeleteメソッドを呼び出し、保存処理を行います。
	 * </pre>
	 */
	async del() {
		try {
			let systemName = MessagesUtil.getMessage("message.systemname");
			let msg = MessagesUtil.getMessage("message.deleteconfirm");
			if (await currentPage.confirm(systemName, msg)) {
				let result = await this.submit("delete");
				this.parent.resetErrorStatus();
				if (result.status == JsonResponse.SUCCESS) {
					if (result.result != null && result.result.length > 0) {
						await currentPage.alert(null, result.result);
						this.changeStateForAfterUpdate();
					} else {
						this.changeStateForAfterUpdate();
					}
				}
			}
		} catch (e) {
			currentPage.reportError(e);
		}
	}
}





