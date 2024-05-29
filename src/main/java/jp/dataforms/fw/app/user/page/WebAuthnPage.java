package jp.dataforms.fw.app.user.page;

import jp.dataforms.fw.app.base.page.GuestPage;
import jp.dataforms.fw.dao.Dao;


/**
 * 生体認証ページ ページクラス。
 */
public class WebAuthnPage extends GuestPage {
	/**
	 * コンストラクタ。
	 */
	public WebAuthnPage() {
		this.addForm(new WebAuthnForm());
	}

	/**
	 * Pageのタイトルを返します。
	 *
	 * @return Pageのタイトル。
	 */
    @Override
	public String getPageName() {
		return "生体情報登録";
	}

	/**
	 * Pageの説明を返します。
	 *
	 * @return Pageの説明。
	 */
    @Override
	public String getPageDescription() {
		return "ユーザアカウントに生体情報を登録します。(何らかの生体認証をサポートした機器が必要です)";
	}

	/**
	 * テーブルを操作するためのDaoクラスを取得します。
	 * <pre>
	 * ページjavaクラス作成用のメソッドです。
	 * </pre>
	 * @return Daoクラス。
	 */
	public Class<? extends Dao> getDaoClass() {
		return null;
	}
}
